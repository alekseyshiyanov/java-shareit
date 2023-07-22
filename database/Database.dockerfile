FROM postgres:13.7-alpine
COPY 01-schema.sql /docker-entrypoint-initdb.d/
