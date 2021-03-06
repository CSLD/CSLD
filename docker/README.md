Priprava
--------
    
Prihlasime se do docker hubu:
    
    docker login
        
Stahneme si kontejner z repozitare:
    
    docker pull jbalhar/csld:latest

Nastartovani kontejneru s csld
-----------------------------------

Jako prvni doporucuji nastarovat pouze kontejner, tak aby zustal bezici na pozadi:
   
```
docker run -it --name csld -p 80:80 jbalhar/csld:latest bash
```   
    
Parametr "--net host" a "-p 80:80" urcuje jake porty budou do kontejneru otevrene. Pro vyvoj je idealni pouzivat "--net host" protoze otevre vsechny porty ktere jsou v kontejneru dostupne.
Pro produkci je dobre otevrit pouze nezbytne nutne porty, takze napriklad "-p 80:80" pro otevreni portu 80. Parametr -p je mozne pouzit opakovane.
                    
        
Spusteni aplikaci
-----------------

Pro prvni spusteni je potreba nejprve inicializovat aplikace a jejich zavislosti. 
    
    _csld init

Pro spusteni bez natazeni a updatu (lze pouzit az pote co se minimalne jednou natahne konfigurace):

    _csld start

Pro zastaveni vsech komponent:

    _csld stop
            
Pro update aplikace na nove verze z repozitaru je pak potreba pustit

    _csld update
    
Vytvoření nové verze kontejneru
-------------------------------

```
docker build -t jbalhar/csld .
docker login
docker push jbalhar/csld
```
