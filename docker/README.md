Priprava
--------

V prvni rade je potreba mit pridelena prava pro pristup do repozitare na docker hubu. Aktualne neni zadny gisat ucet, takze to jde pres ucet "mbabic84". Musite mit samozrejme vlastni ucet na docker hubu.
    
Prihlasime se do docker hubu:
    
    docker login
        
Stahneme si kontejner z repozitare:
    
    docker pull jbalhar/csld:latest

Nastartovani kontejneru s pantherem
-----------------------------------

Jako prvni doporucuji nastarovat pouze kontejner, tak aby zustal bezici na pozadi:
   
    docker run -it --name csld -p 80:80 jbalhar/csld:latest
    
Parametr "--net host" a "-p 80:80" urcuje jake porty budou do kontejneru otevrene. Pro vyvoj je idealni pouzivat "--net host" protoze otevre vsechny porty ktere jsou v kontejneru dostupne.
Pro produkci je dobre otevrit pouze nezbytne nutne porty, takze napriklad "-p 80:80" pro otevreni portu 80. Parametr -p je mozne pouzit opakovane.
                    
Nasledne se pripojime do beziciho kontejneru:
    
    docker exec -it csld bash
        
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