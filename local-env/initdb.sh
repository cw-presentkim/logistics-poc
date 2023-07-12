#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
	CREATE USER cw_america WITH ENCRYPTED PASSWORD 'cw_america';
	CREATE DATABASE cw_america;
	GRANT ALL PRIVILEGES ON DATABASE cw_america TO cw_america;

  \connect cw_america;
  CREATE SCHEMA liquibase;
  GRANT ALL PRIVILEGES ON SCHEMA liquibase TO cw_america;
  GRANT ALL PRIVILEGES ON SCHEMA public TO cw_america;
EOSQL
