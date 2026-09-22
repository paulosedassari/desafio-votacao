# Teste de performance

O arquivo `votacao.js` cria uma pauta, abre sua sessão e executa dois cenários em paralelo:

- votos de associados distintos durante um período configurável;
- uma requisição simultânea por VU para o mesmo associado.

O segundo cenário possui uma verificação de consistência: exatamente um voto deve ser criado e os demais devem receber conflito. O resumo do k6 apresenta throughput, taxa de erros e tempos `med` (p50), p90, p95 e p99. Não foram definidos limites arbitrários de latência ou throughput.

Com a aplicação disponível em `http://localhost:8080`, execute:

```bash
k6 run performance/votacao.js
```

As configurações podem ser alteradas por variáveis de ambiente:

```bash
k6 run \
  -e BASE_URL=http://localhost:8080 \
  -e VUS=50 \
  -e DURACAO=1m \
  -e VUS_DUPLICIDADE=20 \
  -e DURACAO_SESSAO_MINUTOS=10 \
  performance/votacao.js
```

| Variável | Padrão | Finalidade |
|---|---:|---|
| `BASE_URL` | `http://localhost:8080` | Endereço da API |
| `VUS` | `20` | Usuários virtuais com associados distintos |
| `DURACAO` | `30s` | Duração da carga principal |
| `VUS_DUPLICIDADE` | `10` | Requisições concorrentes para o mesmo associado |
| `DURACAO_SESSAO_MINUTOS` | `10` | Tempo de abertura da sessão criada pelo teste |

Para volumes maiores, aumente `VUS` e `DURACAO` progressivamente e registre os resultados junto das características da máquina utilizada.
