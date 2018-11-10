Priprava
--------

V prvni rade je potreba mit pridelena prava pro pristup do repozitare na docker hubu. Aktualne neni zadny gisat ucet, takze to jde pres ucet "mbabic84". Musite mit samozrejme vlastni ucet na docker hubu.
    
Prihlasime se do docker hubu:
    
    docker login
        
Stahneme si kontejner z repozitare:
    
    docker pull mbabic84/panther:new

Vytvoreni datovych kontejneru pro uchovavani dat pri zmene systemoveho kontejneru:

    docker volume create --name panther_tmp && \
    docker volume create --name panther_data && \
    docker volume create --name panther_postgresql && \
    docker volume create --name panther_geoserver && \
    docker volume create --name panther_tomcat7temp && \
    docker volume create --name panther_gnode_uploaded && \
    docker volume create --name panther_geonetwork
    
Pokud byste nekdo prisel na nejaky dalsi adresar ktery je potreba uchovavat, obecne to plati pro vsechny adresare ktere maji tendence zvetsovat svuj obsah v radech stovek mb az nekolika GB, dejte prosim vedet.
Protoze systemovy kontejner ma nastavenou maximalni velikost 10 GB, je potreba smerovat vsechny takove adresare do datoveho kontejneru, predejde se mozne nestabilite celeho systemu z duvodu nedostatku mista.
    
Nastartovani kontejneru s pantherem
-----------------------------------

Jako prvni doporucuji nastarovat pouze kontejner, tak aby zustal bezici na pozadi:
   
    docker run -dt --name panther --net host -v panther_tmp:/tmp -v panther_data:/data -v panther_postgresql:/var/lib/postgresql -v panther_geoserver:/var/lib/tomcat7/webapps/geoserver/data -v panther_tomcat7temp:/var/lib/tomcat7/temp -v panther_gnode_uploaded:/home/geonode/geonode/geonode/uploaded -v panther_geonetwork:/var/lib/tomcat7/webapps/geonetwork/WEB-INF/data mbabic84/panther:new cli
    
    docker run -dt --name panther -p 80:80 -v panther_tmp:/tmp -v panther_data:/data -v panther_postgresql:/var/lib/postgresql -v panther_geoserver:/var/lib/tomcat7/webapps/geoserver/data -v panther_tomcat7temp:/var/lib/tomcat7/temp -v panther_gnode_uploaded:/home/geonode/geonode/geonode/uploaded -v panther_geonetwork:/var/lib/tomcat7/webapps/geonetwork/WEB-INF/data mbabic84/panther:new cli
    
Parametr "--net host" a "-p 80:80" urcuje jake porty budou do kontejneru otevrene. Pro vyvoj je idealni pouzivat "--net host" protoze otevre vsechny porty ktere jsou v kontejneru dostupne.
Pro produkci je dobre otevrit pouze nezbytne nutne porty, takze napriklad "-p 80:80" pro otevreni portu 80. Parametr -p je mozne pouzit opakovane.
                    
Nasledne se pripojime do beziciho kontejneru:
    
    docker exec -it panther bash
        
Spusteni aplikaci
-----------------

Pred prvnim spustenim aplikaci je potreba nechat system natahout konfiguraci, ktera musi byt pripravena na githubu v repozitari "docker" ve vlastnim adresari.
System si na zakladne konfigurace nataha aplikace z githubu, takze pokud je potreba udelat update systemu (ne docker kontejneru jako celku), staci vse zastavit a znovu spustit.

Pro spusteni a update komponent pouzijte:

    _control start -f nazev_adresare_s_konfiguraci
    _gsconfig

Pro spusteni bez natazeni a updatu (lze pouzit az pote co se minimalne jednou natahne konfigurace):

    _control start

Pro zastaveni vsech komponent:

    _control stop
            
Spusteny kontejner obsahuje jednoduchou webovou aplikaci ktera umoznuje cist logy a ovladat jednotlive komponenty v dockeru.

Aplikace bezi na portu "5589" a je dostupna na "host/logreader"
    
Pristupove udaje do aplikace jsou:
    
    user: logreader
    pass: &dm1n1str&t0r