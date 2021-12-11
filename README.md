ČSLD
====

Československá larpová databáze je portál pro larpy české a slovenské larpové komunity.

## Jak rozběhat vývojové prostředí

### Přístup do cloudového IDE plně připraveného pro práci

1. Založ si účet na AWS (https://aws.amazon.com/) 
2. Ozvi se na jakub@balhar.net a tam se dohodneme jak ti nasdílím cloudové vývojové prostředí

### Rozběhnutí dockeru pro lokální vývoj
1. Nainstalujte si docker z docker.com.
_Pokud máte docker pro Windows, musíte udělat:_
   
   1.1. Open Hyper-V Manager (Windows search : “Hyper-V …”)

   1.2. Go to Virtual Switch Manager on the right side.

   1.3. Go to DockerNAT then choose Connection type -> to External network -> #which interface you deside.

   1.4. Hope that help you, too.
2. Spusťe docker
3. Pullněte si nejnovější obraz z veřejně přístupného docker hubu:
`docker pull jbalhar/csld:latest`
4. Spusťe kontejner:
`docker run -it --net host --name csld jbalhar/csld:latest bash`

_Vysvětlení jednotlivých parametrů:
* `-it`: Říká dockeru, že má pustit kontejner v interaktivním módu a umožnit se do něj připojit přes shell
* `--net host`: Přesměruje všechny porty dockeru do operačního systému
* `--name`: Alias pro právě spouštěný kontejner
* `jbalhar/csld:latest`: Definuje, který obraz kontejneru se má spustit
* `bash`: Spouští proces po nastartování kontejneru. V tomto případě se díky procesu bash objeví konzole.

### Rozběhnutí serveru
1. Spusťe skript pro inicializaci serveru `_csld init`

_Co přesně skript dělá se můžete podívat na https://github.com/CSLD/CSLD/blob/master/docker/_csld_

#### Poznámky
_Co je docker a proč ho používat: https://www.zdrojak.cz/clanky/proc-pouzivat-docker/_

_Dokumentace dockeru: https://docs.docker.com/engine/reference/commandline/docker/_

### Budoucnost

Možná přepsat pod Micronaut pro snížení zátěže paměti: https://micronaut.io/
