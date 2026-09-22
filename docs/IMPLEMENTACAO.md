# Implementação da API de votação

## Visão geral

API REST para cadastrar pautas, abrir sessões temporais, registrar um voto por associado e consultar a contabilização final. Os dados são persistidos no MySQL e o esquema é gerenciado pelo Flyway.

O projeto utiliza Java 21, Spring Boot, Spring Web MVC, Spring Data JPA, Bean Validation, MySQL, Flyway, springdoc-openapi, JUnit 5, Mockito, Testcontainers e k6.

## Arquitetura

A aplicação segue uma arquitetura hexagonal pragmática e organizada por domínio:

```text
br.com.paulosedassari.votacao
├── domain
│   ├── pauta
│   ├── sessao
│   ├── voto
│   └── elegibilidade
├── infra
│   ├── config
│   ├── external
│   └── persistence
└── interfaces
    └── web
```

As portas de entrada representam os casos de uso acessados pelos Controllers. As portas de saída isolam persistência e integrações. O domínio não depende de JPA, HTTP ou Spring. As configurações em `infra/config` conectam os casos de uso aos adaptadores.

Essa separação mantém as regras testáveis sem infraestrutura e evita abstrações que não participem de uma fronteira real.

## Execução local

Pré-requisitos:

- Java 21;
- Docker com Docker Compose.

Suba o MySQL:

```bash
docker compose up -d
```

Inicie a aplicação no Linux ou macOS:

```bash
./mvnw spring-boot:run
```

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Por padrão, a aplicação usa `jdbc:mysql://localhost:3306/votacao`, usuário `votacao` e senha `votacao`. Esses valores são exclusivos do ambiente local e podem ser substituídos pelas variáveis:

| Variável | Padrão |
|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/votacao?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC` |
| `DB_USERNAME` | `votacao` |
| `DB_PASSWORD` | `votacao` |
| `DB_POOL_MAX_SIZE` | `20` |
| `DB_POOL_MIN_IDLE` | `5` |

O Flyway aplica automaticamente as migrations na inicialização. O Hibernate opera com `ddl-auto: validate`, portanto valida o mapeamento sem criar ou alterar tabelas.

Para encerrar o banco local:

```bash
docker compose down
```

O volume `votacao_mysql_data` preserva os dados. Use `docker compose down -v` somente quando desejar apagá-los.

## OpenAPI

Com a aplicação em execução:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- especificação OpenAPI: `http://localhost:8080/v3/api-docs`

As anotações HTTP e OpenAPI estão concentradas nas interfaces `*OpenApi`, mantendo os Controllers dedicados à adaptação entre HTTP e os casos de uso.

## Endpoints

Todos os contratos são versionados pela URI `/api/v1`. Essa estratégia torna a versão explícita para os clientes e permite manter uma futura `/api/v2` durante migrações incompatíveis.

| Método | URI | Resultado esperado |
|---|---|---|
| `POST` | `/api/v1/pautas` | Cadastra uma pauta |
| `GET` | `/api/v1/pautas` | Lista as pautas cadastradas |
| `POST` | `/api/v1/pautas/{pautaId}/sessoes` | Abre a sessão única da pauta |
| `POST` | `/api/v1/pautas/{pautaId}/votos` | Registra um voto |
| `GET` | `/api/v1/pautas/{pautaId}/resultado` | Retorna a contabilização final |
| `GET` | `/api/v1/elegibilidade?cpf={cpf}` | Executa o fake de elegibilidade |

### Cadastrar pauta

```http
POST /api/v1/pautas
Content-Type: application/json

{
  "titulo": "Aprovação do orçamento 2027",
  "descricao": "Votação referente ao orçamento anual"
}
```

Resposta `201 Created`:

```json
{
  "id": 1,
  "titulo": "Aprovação do orçamento 2027",
  "descricao": "Votação referente ao orçamento anual",
  "criadoEm": "2026-09-21T18:00:00Z"
}
```

Título e descrição são obrigatórios e não aceitam somente espaços. Títulos repetidos são permitidos.

### Listar pautas

```http
GET /api/v1/pautas
```

Retorna `200 OK` com uma lista, que pode estar vazia.

### Abrir sessão

```http
POST /api/v1/pautas/1/sessoes
Content-Type: application/json

{
  "duracaoEmMinutos": 5
}
```

Resposta `201 Created`:

```json
{
  "id": 1,
  "pautaId": 1,
  "abertaEm": "2026-09-21T18:00:00Z",
  "encerraEm": "2026-09-21T18:05:00Z"
}
```

O corpo `{}` aplica a duração padrão de um minuto. A duração informada deve ser positiva. Uma segunda sessão para a mesma pauta retorna `409 Conflict`.

### Registrar voto

```http
POST /api/v1/pautas/1/votos
Content-Type: application/json

{
  "associadoId": "12345",
  "voto": "SIM"
}
```

Resposta `201 Created`:

```json
{
  "id": 1,
  "pautaId": 1,
  "associadoId": "12345",
  "voto": "SIM",
  "criadoEm": "2026-09-21T18:01:00Z"
}
```

O voto aceita somente `SIM` ou `NAO`. A pauta e sua sessão devem existir, a sessão precisa estar aberta e o associado pode votar somente uma vez na pauta.

### Consultar resultado

```http
GET /api/v1/pautas/1/resultado
```

Resposta `200 OK`, disponível depois do encerramento da sessão:

```json
{
  "pautaId": 1,
  "sim": 150,
  "nao": 83,
  "total": 233
}
```

Uma consulta durante a sessão retorna `409 Conflict`. A resposta apresenta somente os totais porque não foi definida uma regra de aprovação ou desempate.

### Consultar elegibilidade

```http
GET /api/v1/elegibilidade?cpf=52998224725
```

Para um CPF válido, a resposta `200 OK` contém aleatoriamente:

```json
{
  "status": "ABLE_TO_VOTE"
}
```

ou:

```json
{
  "status": "UNABLE_TO_VOTE"
}
```

O endpoint aceita CPF com ou sem pontuação. CPF inválido retorna `404 Not Found`.

## Tratamento de erros

O `GlobalExceptionHandler` centraliza a conversão das exceções conhecidas para HTTP. A API não expõe stack trace, SQL ou detalhes da infraestrutura.

Exemplo de conflito:

```json
{
  "dataHora": "2026-09-21T18:02:00Z",
  "status": 409,
  "codigo": "VOTO_JA_REGISTRADO",
  "mensagem": "O associado já votou nesta pauta",
  "caminho": "/api/v1/pautas/1/votos",
  "violacoes": []
}
```

Validações de campos retornam `400 Bad Request` e detalham as violações. Recursos inexistentes retornam `404 Not Found`; violações do estado atual, como voto duplicado ou sessão aberta durante a consulta do resultado, retornam `409 Conflict`.

## Persistência e concorrência

A migration cria `pauta`, `sessao_votacao` e `voto`, incluindo chaves estrangeiras e os seguintes controles:

- `UNIQUE (pauta_id)` em `sessao_votacao`;
- `UNIQUE (pauta_id, associado_id)` em `voto`;
- `INDEX (pauta_id)` em `voto`;
- `CHECK (valor IN ('SIM', 'NAO'))`.

Os casos de uso fazem verificações prévias para produzir mensagens claras. As constraints continuam sendo a garantia final diante de requisições concorrentes. Violações de unicidade são traduzidas pelos adaptadores para exceções de domínio.

A contabilização utiliza `COUNT(*)` e `GROUP BY valor` em uma query nativa. Os votos não são carregados em memória e a pauta não armazena uma quantidade que poderia divergir dos registros reais.

## Testes automatizados

Execute toda a suíte no Linux ou macOS:

```bash
./mvnw test
```

No Windows:

```powershell
.\mvnw.cmd test
```

Os testes unitários cobrem regras dos casos de uso, tempo controlado, adaptadores e Controllers. Os testes de integração utilizam Testcontainers com MySQL 8.4 para verificar migrations, mapeamentos JPA, constraints, query de agregação e concorrência real. O Docker precisa estar em execução para a suíte completa.

O cenário concorrente dispara oito tentativas para a mesma pauta e associado e confirma que somente um voto é persistido.

## Performance

O teste em `performance/votacao.js` cria sua própria pauta e sessão. Ele executa carga com associados distintos e um cenário concorrente com o mesmo associado.

Com a aplicação em execução:

```bash
k6 run performance/votacao.js
```

Exemplo configurado:

```bash
k6 run -e VUS=50 -e DURACAO=1m -e VUS_DUPLICIDADE=20 performance/votacao.js
```

O relatório apresenta throughput, taxa de erros, p50, p95 e p99. O teste de duplicidade exige exatamente um `201 Created` e conflitos para as demais requisições. Não existem metas arbitrárias de latência ou throughput; os resultados devem ser comparados no mesmo ambiente e registrados com as características da máquina.

As demais variáveis e instruções estão em `performance/README.md`.

## Bônus implementados

- integração fake para CPF e elegibilidade por meio de porta e adaptador;
- teste de performance reproduzível com k6;
- versionamento explícito da API por URI.
