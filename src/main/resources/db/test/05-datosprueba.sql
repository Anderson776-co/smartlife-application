-- Ejecutar sobre la base smartlife (después de 03-vistas-materializadas.sql)
SET ROLE smartlife_app;

-- Urbanización
INSERT INTO urbanizations.urbanization (id, name) VALUES
    ('aaaaaaaa-0000-0000-0000-000000000001', 'Urbanización Montaña');

-- Usuarios
INSERT INTO users.document_type (id, name) VALUES
    ('dddddddd-0000-0000-0000-000000000001', 'Cédula de ciudadanía'),
    ('dddddddd-0000-0000-0000-000000000002', 'Pasaporte');

INSERT INTO users.role (id, name) VALUES
    ('eeeeeeee-0000-0000-0000-000000000001', 'ADMIN'),
    ('eeeeeeee-0000-0000-0000-000000000002', 'OWNER'),
    ('eeeeeeee-0000-0000-0000-000000000003', 'RESIDENT');

INSERT INTO users."user"
    (id, document_number, first_name, middle_name, last_name, second_last_name,
     email, is_email_confirmed, is_email_confirmed_default_value, is_active,
     external_id, id_type, id_urbanization)
VALUES
    ('bbbbbbbb-0000-0000-0000-000000000001', '1001', 'Carlos', 'Andrés', 'Pérez', 'Gómez',
     'carlos@example.com', TRUE, FALSE, TRUE, "W4k9Xz2mLp7vQ1bN8sR5tJ3cY6h",
     'dddddddd-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000001'),
    ('bbbbbbbb-0000-0000-0000-000000000002', '1002', 'María', NULL, 'López', 'Ruiz',
     'maria@example.com', TRUE, FALSE, TRUE,
     'dddddddd-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000001');

INSERT INTO users.user_role (id_user, id_role) VALUES
    ('bbbbbbbb-0000-0000-0000-000000000001', 'eeeeeeee-0000-0000-0000-000000000002'),
    ('bbbbbbbb-0000-0000-0000-000000000002', 'eeeeeeee-0000-0000-0000-000000000003');

-- Viviendas (1 casa y 1 apartamento, ambas de Carlos)
INSERT INTO dwellings.dwelling (id, dwelling_number, block, id_owner, id_urbanization) VALUES
    ('cccccccc-0000-0000-0000-000000000001', '101', 'A',
     'bbbbbbbb-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000001'),
    ('cccccccc-0000-0000-0000-000000000002', '202', 'B',
     'bbbbbbbb-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000001');

INSERT INTO dwellings.house (street_number, id_dwelling) VALUES
    ('10 # 5-20', 'cccccccc-0000-0000-0000-000000000001');

INSERT INTO dwellings.apartment (floor, tower, id_dwelling) VALUES
    (2, '1', 'cccccccc-0000-0000-0000-000000000002');

-- Residentes (el id del residente es el id del usuario María)
INSERT INTO residents.resident (id) VALUES
    ('bbbbbbbb-0000-0000-0000-000000000002');

INSERT INTO residents.resident_dwelling (id_residence, id_resident, is_active) VALUES
    ('cccccccc-0000-0000-0000-000000000002', 'bbbbbbbb-0000-0000-0000-000000000002', TRUE);

-- Visitas
INSERT INTO visits.document_type (id, name) VALUES
    ('ffffffff-0000-0000-0000-000000000001', 'Cédula de ciudadanía');

INSERT INTO visits.visit_reason (id, name, requires_document) VALUES
    ('ffffffff-0000-0000-0000-000000000002', 'Visita familiar',  FALSE),
    ('ffffffff-0000-0000-0000-000000000005', 'Servicio técnico', TRUE);

INSERT INTO visits.visitor
    (id, document_number, first_name, last_name, email, vehicle_plate, document_type)
VALUES
    ('ffffffff-0000-0000-0000-000000000003', '2001', 'Luis', 'Torres',
     'luis@example.com', 'ABC123', 'ffffffff-0000-0000-0000-000000000001');

INSERT INTO visits.visit_authorization
    (id, start_date, end_date, qr_code, id_property, id_authorizator, id_visitor, id_visit_reason)
VALUES
    ('ffffffff-0000-0000-0000-000000000004', now(), now() + interval '4 hours',
     'http://localhost:5173/visitas/autorizaciones/ffffffff-0000-0000-0000-000000000004',
     'cccccccc-0000-0000-0000-000000000002',
     'bbbbbbbb-0000-0000-0000-000000000002',
     'ffffffff-0000-0000-0000-000000000003',
     'ffffffff-0000-0000-0000-000000000002');

INSERT INTO visits.visit (entry_date, id_visit_authorization) VALUES
    (now(), 'ffffffff-0000-0000-0000-000000000004');

-- Refrescar las 8 vistas materializadas
REFRESH MATERIALIZED VIEW users.urbanization;
REFRESH MATERIALIZED VIEW dwellings.urbanization;
REFRESH MATERIALIZED VIEW dwellings.user_owner;
REFRESH MATERIALIZED VIEW residents.user_resident;
REFRESH MATERIALIZED VIEW residents.residence;
REFRESH MATERIALIZED VIEW visits.authorizator;
REFRESH MATERIALIZED VIEW visits.property_visit;
REFRESH MATERIALIZED VIEW visits.resident;

RESET ROLE;