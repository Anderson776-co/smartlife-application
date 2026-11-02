-- =====================================================================
-- Quita el refresco automatico de las vistas materializadas (marcha atras).
-- Despues de esto las vistas vuelven a refrescarse solo a mano:
--   REFRESH MATERIALIZED VIEW CONCURRENTLY visits.authorizator;  (etc.)
-- =====================================================================
SET ROLE smartlife_app;

DROP TRIGGER IF EXISTS tg_refrescar_vistas ON urbanizations.urbanization;
DROP TRIGGER IF EXISTS tg_refrescar_vistas ON users."user";
DROP TRIGGER IF EXISTS tg_refrescar_vistas ON dwellings.dwelling;
DROP TRIGGER IF EXISTS tg_refrescar_vistas ON dwellings.house;
DROP TRIGGER IF EXISTS tg_refrescar_vistas ON dwellings.apartment;
DROP TRIGGER IF EXISTS tg_refrescar_vistas ON residents.resident_dwelling;
DROP FUNCTION IF EXISTS public.refrescar_vistas_materializadas();

RESET ROLE;
