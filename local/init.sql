-- Role: postgres01
-- DROP ROLE postgres01;

CREATE ROLE postgres01 WITH
  PASSWORD 'password123'
  LOGIN
  SUPERUSER
  INHERIT
  CREATEDB
  CREATEROLE
  REPLICATION;

-- DROP DATABASE empdb;

CREATE DATABASE empdb
    WITH
    OWNER = postgres01
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.utf8'
    LC_CTYPE = 'en_US.utf8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

-- DROP DATABASE eventdb;

CREATE DATABASE eventdb
    WITH
    OWNER = postgres01
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.utf8'
    LC_CTYPE = 'en_US.utf8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;