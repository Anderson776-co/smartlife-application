SET ROLE smartlife_app;

-- 1. Cambiar el nombre en la tabla de origen
UPDATE users."user" SET first_name = 'Carlitos'
WHERE id = 'bbbbbbbb-0000-0000-0000-000000000001';

-- 2. La vista todavía muestra el nombre viejo
SELECT id, full_name FROM dwellings.user_owner
WHERE id = 'bbbbbbbb-0000-0000-0000-000000000001';

-- 3. Refrescar sin bloquear lecturas (usa el índice único de la vista)
REFRESH MATERIALIZED VIEW CONCURRENTLY dwellings.user_owner;

-- 4. Ahora muestra el nombre nuevo
SELECT id, full_name FROM dwellings.user_owner
WHERE id = 'bbbbbbbb-0000-0000-0000-000000000001';

RESET ROLE;