CREATE TABLE IF NOT EXISTS usuarios (
    id               BIGSERIAL PRIMARY KEY,
    nome_completo    VARCHAR(255) NOT NULL,
    email            VARCHAR(255) NOT NULL UNIQUE,
    senha            VARCHAR(255) NOT NULL,
    cpf              VARCHAR(20)  NOT NULL UNIQUE,
    tipo             VARCHAR(20)  NOT NULL,
    saldo            NUMERIC(19, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS transferencias (
    id                  BIGSERIAL PRIMARY KEY,
    pagador_id          BIGINT        NOT NULL REFERENCES usuarios(id),
    recebedor_id        BIGINT        NOT NULL REFERENCES usuarios(id),
    valor               NUMERIC(19, 2) NOT NULL,
    data_transferencia  TIMESTAMP     NOT NULL
);

