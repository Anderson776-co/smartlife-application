-- Ejecutar sobre la base smartlife (después de 02-tablas.sql)
SET ROLE smartlife_app;

-- users.urbanization (origen: urbanizations)
CREATE MATERIALIZED VIEW users.urbanization AS
SELECT u.id, u.name
FROM urbanizations.urbanization u
WITH DATA;
CREATE UNIQUE INDEX ux_mv_users_urbanization ON users.urbanization (id);

-- dwellings.urbanization (origen: urbanizations)
CREATE MATERIALIZED VIEW dwellings.urbanization AS
SELECT u.id, u.name
FROM urbanizations.urbanization u
WITH DATA;
CREATE UNIQUE INDEX ux_mv_dwellings_urbanization ON dwellings.urbanization (id);

-- dwellings.user_owner (origen: users)
CREATE MATERIALIZED VIEW dwellings.user_owner AS
SELECT u.id,
       u.document_number,
       concat_ws(' ', u.first_name, u.middle_name, u.last_name, u.second_last_name) AS full_name,
       u.is_active,
       u.id_urbanization AS urbanization_id
FROM users."user" u
WITH DATA;
CREATE UNIQUE INDEX ux_mv_dwellings_user_owner ON dwellings.user_owner (id);

-- residents.user_resident (origen: users)
CREATE MATERIALIZED VIEW residents.user_resident AS
SELECT u.id,
       u.document_number,
       concat_ws(' ', u.first_name, u.middle_name, u.last_name, u.second_last_name) AS full_name,
       u.is_active,
       u.id_urbanization AS urbanization_id
FROM users."user" u
WITH DATA;
CREATE UNIQUE INDEX ux_mv_residents_user_resident ON residents.user_resident (id);

-- residents.residence (origen: dwellings)
CREATE MATERIALIZED VIEW residents.residence AS
SELECT d.id,
       concat_ws(', ',
                 'Bloque ' || d.block,
                 'Vivienda ' || d.dwelling_number,
                 CASE
                     WHEN h.id IS NOT NULL THEN 'Calle ' || h.street_number
                     WHEN a.id IS NOT NULL THEN 'Torre ' || a.tower || ' piso ' || a.floor::text
                 END) AS location,
       d.id_owner,
       d.id_urbanization
FROM dwellings.dwelling d
LEFT JOIN dwellings.house     h ON h.id_dwelling = d.id
LEFT JOIN dwellings.apartment a ON a.id_dwelling = d.id
WITH DATA;
CREATE UNIQUE INDEX ux_mv_residents_residence ON residents.residence (id);

-- visits.authorizator (origen: users)
CREATE MATERIALIZED VIEW visits.authorizator AS
SELECT u.id,
       concat_ws(' ', u.first_name, u.middle_name, u.last_name, u.second_last_name) AS full_name,
       u.is_active,
       u.id_urbanization AS urbanization_id
FROM users."user" u
WITH DATA;
CREATE UNIQUE INDEX ux_mv_visits_authorizator ON visits.authorizator (id);

-- visits.property_visit (origen: dwellings)
CREATE MATERIALIZED VIEW visits.property_visit AS
SELECT d.id,
       concat_ws(', ',
                 'Bloque ' || d.block,
                 'Vivienda ' || d.dwelling_number,
                 CASE
                     WHEN h.id IS NOT NULL THEN 'Calle ' || h.street_number
                     WHEN a.id IS NOT NULL THEN 'Torre ' || a.tower || ' piso ' || a.floor::text
                 END) AS location,
       d.id_owner AS owner_id
FROM dwellings.dwelling d
LEFT JOIN dwellings.house     h ON h.id_dwelling = d.id
LEFT JOIN dwellings.apartment a ON a.id_dwelling = d.id
WITH DATA;
CREATE UNIQUE INDEX ux_mv_visits_property_visit ON visits.property_visit (id);

-- visits.resident (origen: residents)
-- Un solo id por residente (como la entidad): se toma una de sus viviendas activas
CREATE MATERIALIZED VIEW visits.resident AS
SELECT DISTINCT ON (rd.id_resident)
       rd.id_resident  AS id,
       rd.id_residence AS id_property
FROM residents.resident_dwelling rd
WHERE rd.is_active
ORDER BY rd.id_resident, rd.id_residence
WITH DATA;
CREATE UNIQUE INDEX ux_mv_visits_resident ON visits.resident (id);