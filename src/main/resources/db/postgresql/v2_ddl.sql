CREATE TABLE priority_addresses (
    cep VARCHAR(8) PRIMARY KEY CHECK (cep ~ '^[0-9]{8}$'),
    logradouro VARCHAR(200) NOT NULL,
    complemento VARCHAR(200),
    unidade VARCHAR(100),
    bairro VARCHAR(100) NOT NULL,
    localidade VARCHAR(100) NOT NULL,
    uf VARCHAR(2) NOT NULL,
    estado VARCHAR(100),
    regiao VARCHAR(100),
    ibge VARCHAR(20),
    gia VARCHAR(20),
    ddd VARCHAR(5),
    siafi VARCHAR(20)
);

CREATE TABLE address_lookup_logs (
    event_id UUID PRIMARY KEY,
    consulted_at TIMESTAMPTZ NOT NULL,
    cep VARCHAR(8) NOT NULL CHECK (cep ~ '^[0-9]{8}$'),
    source VARCHAR(16) NOT NULL,
    logradouro VARCHAR(200),
    complemento VARCHAR(200),
    unidade VARCHAR(100),
    bairro VARCHAR(100),
    localidade VARCHAR(100),
    uf VARCHAR(2),
    estado VARCHAR(100),
    regiao VARCHAR(100),
    ibge VARCHAR(20),
    gia VARCHAR(20),
    ddd VARCHAR(5),
    siafi VARCHAR(20),
    erro BOOLEAN
);

CREATE INDEX address_lookup_logs_cep_consulted_at_idx
    ON address_lookup_logs (cep, consulted_at DESC);
