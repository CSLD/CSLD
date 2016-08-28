$(function () {
    jQuery.getJSON('http://files.korh.cz/larp.cz-kalendar-api/kalendar.php', function (data) { //vyžádáme si data z api, zpracujeme je jako JSON a uložíme do proměnné "data" s kterou budeme dál pracovat
        var maxAkci = 3; //ruční limit počtu vypsaných akcí - databázový je 30 (více se ani tak nevypíše)
        if (data.chyba)  //pokud nastala chyba php, měla by nám být předána v parametru chyba, vypsat
        {
            alert(data.chyba.popis);
        }
        else if (!data.larpy) //pokud neexistuje parametr larpy (ale nebyla chyba), něco je kardinálně špatně
        {
            alert('Špatný formát přijatých dat');
        }
        else //zřejmě je vše v pořádku
        {
            var larpy = data.larpy; //do proměnné larpy si uložíme pole larpů, které se nám načetlo z JSONu
            var text = ''; //výstupní buffer, do kterého si budeme ukládat html připravované tabulky
            var temp = 0;
            var randomIndex = 0;
            var random = 0;
            var pool = new Array(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
            var k = new Array();
            for (var i = 0; i < larpy.length && i < maxAkci; i++) {
                randomIndex = Math.floor(Math.random() * pool.length);
                random = pool[randomIndex];
                pool.splice(randomIndex, 1); //odstraní prvek z pole
                k[i] = random;
            }
            k.sort();
            for (var i = 0; i < larpy.length && i < maxAkci; i++) //projdeme polem larpů
            {
                temp = k[i];
                var larp = larpy[temp]; //aktuální larp si uložíme do proměnné larp
                //začátek (konec) larpu jsou uvedeny v javascriptem zpracovatelném formátu, vytvoříme si z nich tedy objekty typu Date (datum)
                text += '<div class="polickoSekce">'; //postupně si vytváříme řádek budoucí tabulky a ukládáme do proměnné text
                text += '<div class="datumKalendar">' + larp.datum + '</div><a href="' + larp.link + '" target="_blank"><div class="nadpisKalendar"><b> ' + larp.nazev + '</b></div></a>';
                text += '<div class="popisek">' + larp.obec + ', ' + larp.kraj + '</div><div class="popisekNormal">pořádá ' + larp.poradatel + '</div>';
                text += '</div>';
            }
            //nakonec přepíšeme celý obsah elementu tbody proměnnou text
            jQuery('#kalendarObsah').html(text);
        }
    });

    /*$('#nivoSlider').slidesjs({
        width: 642,
        height: 420,
        navigation: true
    });*/
});