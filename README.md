ČSLD
====

Československá larpová databáze je portál pro larpy české a slovenské larpové komunity.

## Jak rozběhat vývojové prostředí

### Rozběhnutí dockeru
1. Nainstalujte si docker z docker.com.
_Co je docker a proč ho používat: https://www.zdrojak.cz/clanky/proc-pouzivat-docker/_
_Dokumentace dockeru: https://docs.docker.com/engine/reference/commandline/docker/_
1. Spusťe docker
1. Pullněte si nejnovější obraz z veřejně přístupného docker hubu:
`docker pull jbalhar/csld:latest`
1. Spusťe kontejner:
`docker run -it --net host --name csld jbalhar/csld:latest bash`
_Vysvětlení jednotlivých parametrů:
`-it`: TO-DO
`--net`: Přesměruje všechny porty dockeru do operačního systému(?)
`host`: TO-DO
`--name`: Alias pro právě spouštěný kontejner
`jbalhar/csld:latest`: Definuje, který obraz konterjneru se má spustit
`bash`: Spouští proces po nastartování kontejneru. V tomto případě se díky procesu bash objeví konzole.

### Rozběhnutí serveru
1. Spusťe skript pro inicializaci serveru:
`_csld init`
_Co přesně skript dělá se můžete podívat na https://github.com/CSLD/CSLD/blob/master/docker/_csld_

## Running the development environemnt without docker (obsolete)

### Set up for local Development
1. Set up Java at least 1.7
1. Set up PostgreSql database csld with user csld and password csld.
1. Run mvn flyway:migrate -Dflyway.locations=filesystem:sql/migration -Dflyway.url=jdbc:postgresql://localhost:5432/csld -Dflyway.user=csld -Dflyway.password=csld
1. Run the Project in Wicket development mode. 
1. Initialize the database by going to the page /testDatabase
_Don't forget inside of the tomcat, which the application is run on insert jars for mail._
