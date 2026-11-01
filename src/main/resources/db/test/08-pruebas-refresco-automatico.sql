-- =====================================================================
-- Pruebas del refresco automatico de las vistas materializadas.
-- Ejecutar sobre la base smartlife DESPUES de 01, 02, 03, 03b, 03c y 05-datosprueba.
-- Cada prueba imprime "OK ..." y deja los datos como estaban. Si algo falla,
-- el script se detiene con el error.
-- =====================================================================
\set ON_ERROR_STOP on
\pset format unaligned
\pset tuples_only on
SET ROLE smartlife_app;

\echo '=== 0. Comprobacion negativa: SIN trigger la vista se queda vieja (el problema del profesor) ==='
ALTER TABLE users."user" DISABLE TRIGGER tg_refrescar_vistas;
UPDATE users."user" SET is_active = FALSE WHERE email = 'maria@example.com';
SELECT 'tabla is_active=' || (SELECT is_active FROM users."user" WHERE email='maria@example.com')
    || ' | vista authorizator is_active=' || (SELECT is_active FROM visits.authorizator WHERE id='bbbbbbbb-0000-0000-0000-000000000002')
    || ' | desactualizadas: ' || (SELECT string_agg(vista, ',') FROM public.vistas_desactualizadas() WHERE NOT al_dia);
ALTER TABLE users."user" ENABLE TRIGGER tg_refrescar_vistas;
UPDATE users."user" SET is_active = TRUE WHERE email = 'maria@example.com';   -- el trigger vuelve a dejar todo al dia
SELECT 'despues de reactivar el trigger: desactualizadas=' || count(*) FROM public.vistas_desactualizadas() WHERE NOT al_dia;

\echo '=== 1. Desactivar usuario: la vista cambia al instante (misma transaccion) ==='
BEGIN;
UPDATE users."user" SET is_active = FALSE WHERE email = 'maria@example.com';
DO $$ BEGIN
  ASSERT (SELECT is_active FROM visits.authorizator WHERE id='bbbbbbbb-0000-0000-0000-000000000002') = FALSE, 'authorizator no cambio';
  ASSERT (SELECT is_active FROM residents.user_resident WHERE id='bbbbbbbb-0000-0000-0000-000000000002') = FALSE, 'user_resident no cambio';
  ASSERT (SELECT is_active FROM dwellings.user_owner WHERE id='bbbbbbbb-0000-0000-0000-000000000002') = FALSE, 'user_owner no cambio';
END $$;
COMMIT;
SELECT 'OK 1: authorizator.is_active (tras commit) = ' || is_active FROM visits.authorizator WHERE id='bbbbbbbb-0000-0000-0000-000000000002';
UPDATE users."user" SET is_active = TRUE WHERE email = 'maria@example.com';

\echo '=== 2. Cambiar el nombre + ROLLBACK: el refresco tambien se deshace ==='
BEGIN;
UPDATE users."user" SET first_name = 'Mariana' WHERE email = 'maria@example.com';
SELECT 'dentro de la transaccion: ' || full_name FROM visits.authorizator WHERE id='bbbbbbbb-0000-0000-0000-000000000002';
ROLLBACK;
SELECT 'OK 2 tras ROLLBACK: ' || full_name FROM visits.authorizator WHERE id='bbbbbbbb-0000-0000-0000-000000000002';

\echo '=== 3. Insertar y borrar usuarios ==='
INSERT INTO users."user" (id, document_number, first_name, last_name, email, is_email_confirmed, is_email_confirmed_default_value, is_active, external_id, id_type, id_urbanization)
VALUES ('bbbbbbbb-0000-0000-0000-000000000099','9999','Nuevo','Usuario','nuevo@example.com',TRUE,FALSE,TRUE,'UID-NUEVO','dddddddd-0000-0000-0000-000000000001','aaaaaaaa-0000-0000-0000-000000000001');
SELECT 'OK 3a: tras INSERT -> authorizator=' || (SELECT count(*) FROM visits.authorizator WHERE id='bbbbbbbb-0000-0000-0000-000000000099')
    || ' user_owner=' || (SELECT count(*) FROM dwellings.user_owner WHERE id='bbbbbbbb-0000-0000-0000-000000000099')
    || ' user_resident=' || (SELECT count(*) FROM residents.user_resident WHERE id='bbbbbbbb-0000-0000-0000-000000000099');
DELETE FROM users."user" WHERE id='bbbbbbbb-0000-0000-0000-000000000099';
SELECT 'OK 3b: tras DELETE -> authorizator=' || (SELECT count(*) FROM visits.authorizator WHERE id='bbbbbbbb-0000-0000-0000-000000000099');

\echo '=== 4. Viviendas: dwelling / house / apartment ==='
UPDATE dwellings.dwelling SET block = 'Z' WHERE id='cccccccc-0000-0000-0000-000000000001';
SELECT 'OK 4a: property_visit = ' || location FROM visits.property_visit WHERE id='cccccccc-0000-0000-0000-000000000001';
UPDATE dwellings.house SET street_number = '99 # 1-1' WHERE id_dwelling='cccccccc-0000-0000-0000-000000000001';
SELECT 'OK 4b: residence    = ' || location FROM residents.residence WHERE id='cccccccc-0000-0000-0000-000000000001';
UPDATE dwellings.apartment SET floor = 7 WHERE id_dwelling='cccccccc-0000-0000-0000-000000000002';
SELECT 'OK 4c: property_visit = ' || location FROM visits.property_visit WHERE id='cccccccc-0000-0000-0000-000000000002';
UPDATE dwellings.dwelling SET block='A' WHERE id='cccccccc-0000-0000-0000-000000000001';
UPDATE dwellings.house SET street_number='10 # 5-20' WHERE id_dwelling='cccccccc-0000-0000-0000-000000000001';
UPDATE dwellings.apartment SET floor=2 WHERE id_dwelling='cccccccc-0000-0000-0000-000000000002';

\echo '=== 5. Residente se muda: visits.resident ==='
UPDATE residents.resident_dwelling SET is_active = FALSE WHERE id_resident='bbbbbbbb-0000-0000-0000-000000000002';
SELECT 'OK 5a: filas de visits.resident para Maria tras desactivar = ' || count(*) FROM visits.resident WHERE id='bbbbbbbb-0000-0000-0000-000000000002';
UPDATE residents.resident_dwelling SET is_active = TRUE WHERE id_resident='bbbbbbbb-0000-0000-0000-000000000002';
SELECT 'OK 5b: tras reactivar = ' || count(*) FROM visits.resident WHERE id='bbbbbbbb-0000-0000-0000-000000000002';

\echo '=== 6. Urbanizacion ==='
UPDATE urbanizations.urbanization SET name = 'Montana II' WHERE id='aaaaaaaa-0000-0000-0000-000000000001';
SELECT 'OK 6: users.urbanization = ' || name FROM users.urbanization;
UPDATE urbanizations.urbanization SET name = 'Urbanización Montaña' WHERE id='aaaaaaaa-0000-0000-0000-000000000001';

\echo '=== 7. Triggers TRUNCATE presentes ==='
SELECT 'tablas con trigger TRUNCATE: ' || count(*) FROM pg_trigger WHERE tgname='tg_refrescar_vistas' AND (tgtype & 32) <> 0;

\echo '=== 8. Estado final de las 8 vistas ==='
SELECT vista || ' al_dia=' || al_dia FROM public.vistas_desactualizadas();
