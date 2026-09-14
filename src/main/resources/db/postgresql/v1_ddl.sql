CREATE TYPE CustomerStatus AS ENUM ('ACTIVE','INACTIVE');
CREATE SEQUENCE cstmr_seq
    INCREMENT BY 1
    MINVALUE 1
    START 1
	NO CYCLE;
create table cstmr(
    id bigint not null default nextval('cstmr_seq'),
    nme VARCHAR(50) not null,
    cpf varchar(11) not null,
    eml varchar(254) not null,
    stts CustomerStatus not null,
    crtdt timestamptz not null default CURRENT_TIMESTAMP,
    updtdt timestamptz not null default CURRENT_TIMESTAMP
);
ALTER TABLE cstmr ADD PRIMARY KEY (id);

ALTER TABLE cstmr ADD CONSTRAINT unqe_cstmr_eml UNIQUE (eml);
ALTER TABLE cstmr ADD CONSTRAINT unqe_cstmr_dcmnt UNIQUE (cpf);



CREATE TABLE prcssd_mssgs (
        evnt_id UUID PRIMARY KEY,
        evnt_tpe VARCHAR(100) NOT NULL,
        prcssd_at TIMESTAMPTZ NOT NULL default CURRENT_TIMESTAMP
);
