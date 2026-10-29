-- Ejecutar sobre la base smartlife (después de 01-esquemas.sql)
SET ROLE smartlife_app;


-- Contexto urbanizations

CREATE TABLE urbanizations.urbanization (
    id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(150) NOT NULL
);


-- Contexto users 
CREATE TABLE users.document_type (
    id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE users.role (
    id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE
);

-- "user" va entre comillas porque es palabra reservada en PostgreSQL
CREATE TABLE users."user" (
    id                                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    document_number                   VARCHAR(30)  NOT NULL,
    first_name                        VARCHAR(100) NOT NULL,
    middle_name                       VARCHAR(100),
    last_name                         VARCHAR(100) NOT NULL,
    second_last_name                  VARCHAR(100),
    email                             VARCHAR(255) NOT NULL,
    is_email_confirmed                BOOLEAN DEFAULT FALSE,
    is_email_confirmed_default_value  BOOLEAN DEFAULT FALSE,
    is_active                         BOOLEAN NOT NULL DEFAULT TRUE,
    external_id                       VARCHAR(128) UNIQUE,
    id_type                           UUID NOT NULL REFERENCES users.document_type (id),
    id_urbanization                   UUID NOT NULL  -- vista users.urbanization (sin FK)
);

CREATE TABLE users.user_role (
    id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_user  UUID NOT NULL REFERENCES users."user" (id),
    id_role  UUID NOT NULL REFERENCES users.role (id),
    CONSTRAINT uq_user_role UNIQUE (id_user, id_role)
);


-- Contexto dwellings 
CREATE TABLE dwellings.dwelling (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    dwelling_number  VARCHAR(30) NOT NULL,
    block            VARCHAR(30),
    id_owner         UUID NOT NULL,  -- vista dwellings.user_owner (sin FK)
    id_urbanization  UUID NOT NULL   -- vista dwellings.urbanization (sin FK)
);

CREATE TABLE dwellings.house (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    street_number  VARCHAR(30) NOT NULL,
    id_dwelling    UUID NOT NULL UNIQUE REFERENCES dwellings.dwelling (id)
);

CREATE TABLE dwellings.apartment (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    floor        INTEGER     NOT NULL,
    tower        VARCHAR(30) NOT NULL,
    id_dwelling  UUID NOT NULL UNIQUE REFERENCES dwellings.dwelling (id)
);

-- Contexto residents 
-- id = id de la vista residents.user_resident (sin FK)
CREATE TABLE residents.resident (
    id UUID PRIMARY KEY
);

CREATE TABLE residents.resident_dwelling (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_residence  UUID    NOT NULL,  -- vista residents.residence (sin FK)
    id_resident   UUID    NOT NULL REFERENCES residents.resident (id),
    is_active     BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_resident_dwelling UNIQUE (id_resident, id_residence)
);


-- Contexto visits 
CREATE TABLE visits.document_type (
    id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE visits.visit_reason (
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name               VARCHAR(100) NOT NULL UNIQUE,
    requires_document  BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE visits.visitor (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    document_number   VARCHAR(30)  NOT NULL,
    first_name        VARCHAR(100) NOT NULL,
    middle_name       VARCHAR(100),
    last_name         VARCHAR(100) NOT NULL,
    second_last_name  VARCHAR(100),
    email             VARCHAR(255) NOT NULL,
    vehicle_plate     VARCHAR(20),
    document_type     UUID REFERENCES visits.document_type (id),
    CONSTRAINT uq_visitor_document_number UNIQUE (document_number),
    CONSTRAINT uq_visitor_email UNIQUE (email)
);

CREATE TABLE visits.visit_authorization (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    start_date        TIMESTAMPTZ  NOT NULL,
    end_date          TIMESTAMPTZ  NOT NULL,
    register_date     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    observation       VARCHAR(500),
    qr_code           VARCHAR(255) NOT NULL,
    file_reference    VARCHAR(255),
    is_active         BOOLEAN NOT NULL DEFAULT TRUE,
    id_property       UUID NOT NULL,  -- vista visits.property_visit (sin FK)
    id_authorizator   UUID NOT NULL,  -- vista visits.authorizator (sin FK)
    id_visitor        UUID NOT NULL REFERENCES visits.visitor (id),
    id_visit_reason   UUID NOT NULL REFERENCES visits.visit_reason (id)
);

CREATE INDEX ix_visit_authorization_overlap
    ON visits.visit_authorization (id_visitor, id_property, start_date, end_date);

CREATE TABLE visits.visit (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    entry_date              TIMESTAMPTZ NOT NULL,
    departure_date          TIMESTAMPTZ,
    observation             VARCHAR(500),
    id_visit_authorization  UUID NOT NULL UNIQUE REFERENCES visits.visit_authorization (id)
);