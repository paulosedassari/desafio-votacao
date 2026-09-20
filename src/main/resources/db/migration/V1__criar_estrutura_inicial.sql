CREATE TABLE pauta (
    id BIGINT NOT NULL AUTO_INCREMENT,
    titulo VARCHAR(200) NOT NULL,
    descricao VARCHAR(1000) NOT NULL,
    criado_em TIMESTAMP(6) NOT NULL,
    CONSTRAINT pk_pauta PRIMARY KEY (id)
);

CREATE TABLE sessao_votacao (
    id BIGINT NOT NULL AUTO_INCREMENT,
    pauta_id BIGINT NOT NULL,
    aberta_em TIMESTAMP(6) NOT NULL,
    encerra_em TIMESTAMP(6) NOT NULL,
    CONSTRAINT pk_sessao_votacao PRIMARY KEY (id),
    CONSTRAINT uk_sessao_votacao_pauta UNIQUE (pauta_id),
    CONSTRAINT fk_sessao_votacao_pauta
        FOREIGN KEY (pauta_id) REFERENCES pauta (id)
);

CREATE TABLE voto (
    id BIGINT NOT NULL AUTO_INCREMENT,
    pauta_id BIGINT NOT NULL,
    associado_id VARCHAR(100) NOT NULL,
    valor VARCHAR(3) NOT NULL,
    criado_em TIMESTAMP(6) NOT NULL,
    CONSTRAINT pk_voto PRIMARY KEY (id),
    CONSTRAINT uk_voto_pauta_associado UNIQUE (pauta_id, associado_id),
    CONSTRAINT fk_voto_pauta
        FOREIGN KEY (pauta_id) REFERENCES pauta (id),
    CONSTRAINT ck_voto_valor CHECK (valor IN ('SIM', 'NAO'))
);

CREATE INDEX idx_voto_pauta ON voto (pauta_id);
