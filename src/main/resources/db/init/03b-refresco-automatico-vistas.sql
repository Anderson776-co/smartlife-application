-- =====================================================================
-- Refresco automatico de las vistas materializadas
-- Ejecutar sobre la base smartlife DESPUES de 03-vistasmaterializadas.sql
--
-- Problema: una vista materializada es una COPIA. Si cambia el dato de
-- origen (por ejemplo se desactiva un usuario) la copia queda vieja hasta
-- que alguien la refresque.
--
-- Solucion: un trigger por tabla de origen. Despues de cada INSERT, UPDATE,
-- DELETE o TRUNCATE refresca, en la MISMA transaccion, las vistas que
-- dependen de esa tabla:
--   * Si la transaccion hace ROLLBACK, el refresco tambien se deshace.
--   * Las lecturas no se bloquean (REFRESH ... CONCURRENTLY; por eso cada
--     vista tiene su indice unico).
--   * Cubre cualquier forma de escribir: la aplicacion, otro modulo o un
--     script SQL ejecutado a mano.
--   * Los refrescos se hacen de uno en uno (bloqueo asesor), asi dos
--     transacciones simultaneas no se pisan el trabajo.
-- =====================================================================
SET ROLE smartlife_app;

-- ---------------------------------------------------------------------
-- Funcion generica: refresca las vistas que recibe como argumento.
-- ---------------------------------------------------------------------
CREATE OR REPLACE FUNCTION public.refrescar_vistas_materializadas()
RETURNS trigger
LANGUAGE plpgsql
SECURITY DEFINER                       -- se ejecuta con los permisos del dueño de las vistas
SET search_path = pg_catalog, pg_temp  -- evita que otro esquema suplante nombres
AS $$
DECLARE
    nombre text;
    vista  regclass;
BEGIN
    -- Un solo refresco a la vez. Este bloqueo se pide ANTES de refrescar: si dos
    -- transacciones escriben al mismo tiempo, la segunda espera aqui y, al despertar,
    -- toma una "foto" de los datos que ya incluye el cambio de la primera. Sin esto,
    -- la segunda podia refrescar con datos viejos y pisar el cambio de la primera.
    -- Tambien evita bloqueos mutuos (deadlocks) entre refrescos de distintas vistas.
    -- Se libera solo al terminar la transaccion (COMMIT o ROLLBACK).
    PERFORM pg_advisory_xact_lock(hashtextextended('smartlife.refrescar_vistas_materializadas', 0));

    FOREACH nombre IN ARRAY TG_ARGV LOOP
        vista := to_regclass(nombre);
        IF vista IS NULL THEN
            RAISE EXCEPTION 'SmartLife: la vista materializada % no existe (trigger sobre %.%)',
                nombre, TG_TABLE_SCHEMA, TG_TABLE_NAME;
        END IF;
        EXECUTE format('REFRESH MATERIALIZED VIEW CONCURRENTLY %s', vista);
        RAISE DEBUG 'SmartLife: % refrescada por % en %.%', nombre, TG_OP, TG_TABLE_SCHEMA, TG_TABLE_NAME;
    END LOOP;
    RETURN NULL;   -- trigger AFTER de sentencia: el valor devuelto se ignora
END;
$$;

-- ---------------------------------------------------------------------
-- urbanizations.urbanization  ->  users.urbanization, dwellings.urbanization
-- ---------------------------------------------------------------------
DROP TRIGGER IF EXISTS tg_refrescar_vistas ON urbanizations.urbanization;
CREATE TRIGGER tg_refrescar_vistas
    AFTER INSERT OR UPDATE OR DELETE OR TRUNCATE ON urbanizations.urbanization
    FOR EACH STATEMENT
    EXECUTE FUNCTION public.refrescar_vistas_materializadas('users.urbanization', 'dwellings.urbanization');

-- ---------------------------------------------------------------------
-- users."user"  ->  dwellings.user_owner, residents.user_resident, visits.authorizator
-- (cubre cambios de nombre, documento, estado activo y urbanizacion)
-- ---------------------------------------------------------------------
DROP TRIGGER IF EXISTS tg_refrescar_vistas ON users."user";
CREATE TRIGGER tg_refrescar_vistas
    AFTER INSERT OR UPDATE OR DELETE OR TRUNCATE ON users."user"
    FOR EACH STATEMENT
    EXECUTE FUNCTION public.refrescar_vistas_materializadas(
        'dwellings.user_owner', 'residents.user_resident', 'visits.authorizator');

-- ---------------------------------------------------------------------
-- dwellings.dwelling / house / apartment  ->  residents.residence, visits.property_visit
-- ---------------------------------------------------------------------
DROP TRIGGER IF EXISTS tg_refrescar_vistas ON dwellings.dwelling;
CREATE TRIGGER tg_refrescar_vistas
    AFTER INSERT OR UPDATE OR DELETE OR TRUNCATE ON dwellings.dwelling
    FOR EACH STATEMENT
    EXECUTE FUNCTION public.refrescar_vistas_materializadas('residents.residence', 'visits.property_visit');

DROP TRIGGER IF EXISTS tg_refrescar_vistas ON dwellings.house;
CREATE TRIGGER tg_refrescar_vistas
    AFTER INSERT OR UPDATE OR DELETE OR TRUNCATE ON dwellings.house
    FOR EACH STATEMENT
    EXECUTE FUNCTION public.refrescar_vistas_materializadas('residents.residence', 'visits.property_visit');

DROP TRIGGER IF EXISTS tg_refrescar_vistas ON dwellings.apartment;
CREATE TRIGGER tg_refrescar_vistas
    AFTER INSERT OR UPDATE OR DELETE OR TRUNCATE ON dwellings.apartment
    FOR EACH STATEMENT
    EXECUTE FUNCTION public.refrescar_vistas_materializadas('residents.residence', 'visits.property_visit');

-- ---------------------------------------------------------------------
-- residents.resident_dwelling  ->  visits.resident
-- ---------------------------------------------------------------------
DROP TRIGGER IF EXISTS tg_refrescar_vistas ON residents.resident_dwelling;
CREATE TRIGGER tg_refrescar_vistas
    AFTER INSERT OR UPDATE OR DELETE OR TRUNCATE ON residents.resident_dwelling
    FOR EACH STATEMENT
    EXECUTE FUNCTION public.refrescar_vistas_materializadas('visits.resident');

-- ---------------------------------------------------------------------
-- Refresco inicial: deja las 8 vistas al dia con los datos que ya existen.
-- ---------------------------------------------------------------------
REFRESH MATERIALIZED VIEW CONCURRENTLY users.urbanization;
REFRESH MATERIALIZED VIEW CONCURRENTLY dwellings.urbanization;
REFRESH MATERIALIZED VIEW CONCURRENTLY dwellings.user_owner;
REFRESH MATERIALIZED VIEW CONCURRENTLY residents.user_resident;
REFRESH MATERIALIZED VIEW CONCURRENTLY residents.residence;
REFRESH MATERIALIZED VIEW CONCURRENTLY visits.authorizator;
REFRESH MATERIALIZED VIEW CONCURRENTLY visits.property_visit;
REFRESH MATERIALIZED VIEW CONCURRENTLY visits.resident;

RESET ROLE;
