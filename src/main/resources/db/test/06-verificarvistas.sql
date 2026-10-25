SELECT 'users.urbanization'      AS vista, count(*) AS filas FROM users.urbanization
UNION ALL SELECT 'dwellings.urbanization',  count(*) FROM dwellings.urbanization
UNION ALL SELECT 'dwellings.user_owner',    count(*) FROM dwellings.user_owner
UNION ALL SELECT 'residents.user_resident', count(*) FROM residents.user_resident
UNION ALL SELECT 'residents.residence',     count(*) FROM residents.residence
UNION ALL SELECT 'visits.authorizator',     count(*) FROM visits.authorizator
UNION ALL SELECT 'visits.property_visit',   count(*) FROM visits.property_visit
UNION ALL SELECT 'visits.resident',         count(*) FROM visits.resident;