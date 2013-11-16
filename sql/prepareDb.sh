#!/bin/bash
## Skript zahodi databazi csld na localhostu a vytvori ji znovu s testovacimi daty. Soucasne zalozi
## i testovaci ucet csld s heslem csld pokud jiz neexistuje.
##
## Na vstupu skriptu je predavano uzivatelske jmeno administratora,
## ktery ma pravo dropovat a vytvaret databaze.
##
## Heslo pro prihlaseni administratora (ktery ma pravo editovat DDL databaze csld) k databazi
## Pridat do souboru ~\.pgpass (na Linuxu) resp. %APPDATA%\postgresql\pgpass.conf (na Widndows) řádek:
##    hostname:port:database:username:password
## Example:
##    localhost:5432:*:pgadmin:HesloPGadmin
##

user=$1
host=localhost
port=5432
db=csld

echo "createDb.sql"
psql -U $user -h $host -p $port -d postgres  -f createDb.sql
echo "crateUser.sql"
psql -U $user -h $host -p $port -d $db -f createUser.sql
echo "csldFinal.sql"
psql -U $user -h $host -p $port -d $db -f csldFinal.sql

for FILE in $(find ./function/ -name '*.sql' -type f); do
    echo "$FILE"
    psql -U $user -h $host -p $port -d $db -f $FILE
done

echo "testValues.sql"
psql -U $user -h $host -p $port -d $db -f testValues.sql
