-- =====================================================================
-- Comprueba si las 8 vistas materializadas coinciden con sus tablas de origen.
-- Ejecutar sobre la base smartlife (despues de 03-vistasmaterializadas.sql).
--
--   SELECT * FROM public.vistas_desactualizadas();
--
-- Devuelve una fila por vista. Todas deben tener 0 en ambas columnas:
--   filas_que_faltan : filas que existen en la tabla pero no en la vista
--   filas_sobrantes  : filas que estan en la vista pero ya no existen en la tabla
-- =====================================================================
SET ROLE smartlife_app;

CREATE OR REPLACE FUNCTION public.vistas_desactualizadas()
RETURNS TABLE (vista text, filas_que_faltan bigint, filas_sobrantes bigint, al_dia boolean)
LANGUAGE plpgsql
STABLE
AS $$
DECLARE
    v record;
    consulta text;
    faltan bigint;
    sobran bigint;
BEGIN
    FOR v IN
        SELECT c.oid::regclass AS rel, n.nspname || '.' || c.relname AS nombre
        FROM pg_class c
        JOIN pg_namespace n ON n.oid = c.relnamespace
        WHERE c.relkind = 'm'
          AND n.nspname IN ('users', 'urbanizations', 'dwellings', 'residents', 'visits')
        ORDER BY 2
    LOOP
        consulta := rtrim(pg_get_viewdef(v.rel), ';');
        EXECUTE format('SELECT count(*) FROM ((%s) EXCEPT ALL SELECT * FROM %s) t', consulta, v.rel) INTO faltan;
        EXECUTE format('SELECT count(*) FROM (SELECT * FROM %s EXCEPT ALL (%s)) t', v.rel, consulta) INTO sobran;
        vista := v.nombre; filas_que_faltan := faltan; filas_sobrantes := sobran; al_dia := (faltan = 0 AND sobran = 0);
        RETURN NEXT;
    END LOOP;
END;
$$;

RESET ROLE;
