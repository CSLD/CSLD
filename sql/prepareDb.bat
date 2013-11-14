REM Skript zahodi databazi csld na localhostu a vytvori ji znovu s testovacimi daty. Soucasne zalozi
REM i testovaci ucet csld s heslem csld pokud jiz neexistuje.
REM
REM Na vstupu skriptu je predavano uzivatelske jmeno administratora,
REM ktery ma pravo dropovat a vytvaret databaze.
REM
REM Skript predpoklada, ze v systemove promene PATH je nastavenea cesta do bin adresare instalace PostgresDB.
REM Pokud nechcete nastavovat tuto cestu (což je možné v ovládacích panelech -> systém -> rozšířená nastavení -> proměnné systému)
REM Pak je možné upravit promenou psql níže.
REM
REM Heslo pro prihlaseni administratora (ktery ma pravo editovat DDL databaze csld) k databazi
REM Pridat do souboru %APPDATA%\postgresql\pgpass.conf řádek:
REM    hostname:port:database:username:password
REM Example:
REM    localhost:5432:*:pgadmin:HesloPGadmin

set user=%1
set host=localhost
set port=5432
set db=csld
set psql=psql

%psql% -U %user% -h %host% -p %port% -d postgres -f createDb.sql
%psql% -U %user% -h %host% -p %port% -d postgres -f createUser.sql
%psql% -U %user% -h %host% -p %port% -d %db% -f csldFinal.sql

cd function
FOR %%i IN (*) DO %psql% -U %user% -h %host% -p %port% -d %db% -f %%i
cd ..

%psql% -U %user% -h %host% -p %port% -d %db% -f testValues.sql
