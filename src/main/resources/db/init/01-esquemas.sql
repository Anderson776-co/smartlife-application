CREATE SCHEMA IF NOT EXISTS users;
CREATE SCHEMA IF NOT EXISTS urbanizations;
CREATE SCHEMA IF NOT EXISTS dwellings;
CREATE SCHEMA IF NOT EXISTS residents;
CREATE SCHEMA IF NOT EXISTS visits;

GRANT ALL ON SCHEMA users          TO smartlife_app;
GRANT ALL ON SCHEMA urbanizations  TO smartlife_app;
GRANT ALL ON SCHEMA dwellings      TO smartlife_app;
GRANT ALL ON SCHEMA residents      TO smartlife_app;
GRANT ALL ON SCHEMA visits         TO smartlife_app;