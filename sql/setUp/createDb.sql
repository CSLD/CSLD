DROP DATABASE csld;

CREATE DATABASE csld
  WITH OWNER = padmin
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       LC_COLLATE = 'cs_CZ.UTF-8'
       LC_CTYPE = 'cs_CZ.UTF-8'
       CONNECTION LIMIT = -1;