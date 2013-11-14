#!/bin/bash
# Nastavit login a heslo pro prihlaseni administratora (ktery ma pravo editovat DDL databaze csld) k databazi
# Pridat do adresare:
#    hostname:port:database:username:password
# Na Linuxu:
#    ~\.pgpass
# Na Windows:
#    %APPDATA%\postgresql\pgpass.conf

user=$1
host=localhost
port=5432

echo "createDb.sql"
psql -U $user -h $host -p $port -d postgres  -f createDb.sql
echo "csldFinal.sql"
psql -U $user -h $host -p $port -d csld -f csldFinal.sql

for FILE in $(find ./function/ -name '*.sql' -type f); do
    echo "$FILE"
    psql -U $user -h $host -p $port -d csld -f $FILE
done

echo "testValues.sql"
psql -U $user -h $host -p $port -d csld -f testValues.sql
