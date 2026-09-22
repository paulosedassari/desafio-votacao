import http from 'k6/http';
import { check, fail } from 'k6';
import { Counter, Rate } from 'k6/metrics';
import exec from 'k6/execution';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const DURACAO_SESSAO_MINUTOS = Number(__ENV.DURACAO_SESSAO_MINUTOS || 10);
const VUS = Number(__ENV.VUS || 20);
const DURACAO = __ENV.DURACAO || '30s';
const VUS_DUPLICIDADE = Number(__ENV.VUS_DUPLICIDADE || 10);

const requisicoesVotoAceitas = new Rate('requisicoes_voto_aceitas');
const votosDuplicadosCriados = new Counter('votos_duplicados_criados');
const votosDuplicadosRejeitados = new Counter('votos_duplicados_rejeitados');

export const options = {
    scenarios: {
        votos_distintos: {
            executor: 'constant-vus',
            exec: 'registrarVotoDistinto',
            vus: VUS,
            duration: DURACAO,
            tags: { tipo: 'votos_distintos' },
        },
        mesmo_associado: {
            executor: 'per-vu-iterations',
            exec: 'registrarVotoMesmoAssociado',
            vus: VUS_DUPLICIDADE,
            iterations: 1,
            maxDuration: '30s',
            tags: { tipo: 'mesmo_associado' },
        },
    },
    thresholds: {
        votos_duplicados_criados: ['count==1'],
        votos_duplicados_rejeitados: [`count==${VUS_DUPLICIDADE - 1}`],
    },
    summaryTrendStats: ['avg', 'min', 'med', 'p(90)', 'p(95)', 'p(99)', 'max'],
};

const parametrosJson = {
    headers: { 'Content-Type': 'application/json' },
};

export function setup() {
    const pautaResponse = http.post(
        `${BASE_URL}/api/v1/pautas`,
        JSON.stringify({
            titulo: `Teste de carga k6 ${Date.now()}`,
            descricao: 'Pauta criada automaticamente pelo teste de carga',
        }),
        parametrosJson,
    );

    if (pautaResponse.status !== 201) {
        fail(`Falha ao criar pauta: HTTP ${pautaResponse.status} - ${pautaResponse.body}`);
    }

    const pautaId = pautaResponse.json('id');
    const sessaoResponse = http.post(
        `${BASE_URL}/api/v1/pautas/${pautaId}/sessoes`,
        JSON.stringify({ duracaoEmMinutos: DURACAO_SESSAO_MINUTOS }),
        parametrosJson,
    );

    if (sessaoResponse.status !== 201) {
        fail(`Falha ao abrir sessão: HTTP ${sessaoResponse.status} - ${sessaoResponse.body}`);
    }

    return { pautaId };
}

export function registrarVotoDistinto(data) {
    const associadoId = [
        'k6',
        exec.vu.idInTest,
        exec.scenario.iterationInTest,
        Date.now(),
    ].join('-');

    const response = votar(data.pautaId, associadoId);
    const criado = check(response, {
        'voto distinto criado': resposta => resposta.status === 201,
    });

    requisicoesVotoAceitas.add(criado);
}

export function registrarVotoMesmoAssociado(data) {
    const response = votar(data.pautaId, 'associado-concorrente-k6');

    check(response, {
        'mesmo associado recebe 201 ou 409': resposta =>
            resposta.status === 201 || resposta.status === 409,
    });

    if (response.status === 201) {
        votosDuplicadosCriados.add(1);
    }

    if (response.status === 409) {
        votosDuplicadosRejeitados.add(1);
    }
}

function votar(pautaId, associadoId) {
    const voto = Math.random() < 0.5 ? 'SIM' : 'NAO';

    return http.post(
        `${BASE_URL}/api/v1/pautas/${pautaId}/votos`,
        JSON.stringify({ associadoId, voto }),
        parametrosJson,
    );
}
