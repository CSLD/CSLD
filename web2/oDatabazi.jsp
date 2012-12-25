<%@ page import="org.pilirion.models.user.User" %>
<%@ page import="org.pilirion.models.user.Users" %>
<%@ page import="java.util.List" %>
<%@ page import="org.pilirion.models.user.Person" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="layout/header.jsp" %>
<body>
<div class="page">

    <!-- HEADER -->
    <%@include file="layout/head.jsp" %>

    <!-- O DATABAZI -->

    <div id="levaCastOdatabazi">
        <div class="nadpisStranky"><img src="img/icon/question_icon.png" class="ikonaStranky" alt="">O databázi</div>
        <p>Česko-slovenská larpová databáze (ČSLD) je projekt, mající za cíl evidovat staré i nové počiny, které se objeví na larpové scéně, a poskytnout možnost jejich účastníkům hodnotit jejich průběh a celkovou zdařilost.</p>

        <p>Slovo larp je složeninou anglického sousloví "live action role-playing". Jedná se o aktivitu určenou všem věkovým generacím, blízce podobnou tu akční dobrodružné hře, tu divadlu. Avšak divadlu, které má jen volný nebo vůbec žádný scénář a jeho příběh vytvářejí sami účastníci svým jednáním. Divadlu, hranému bez publika, jen pro vlastní potěchu. Více o fenoménu larpu se dozvíte na <a href="http://larp.cz/cs/info" target="_blank">http://larp.cz</a>.</p>

        <p>Pro účely databáze larpem míníme komorní, venkovní (outdoorový), jakož i městský larp a přes jen částečnou spřízněnost také bitvu. Larpem tedy nemusí být nutně larpová akce (jedná-li se např. o festival larpů). Pod larp se dají zařadit také některé malostrukturované hry zážitkové pedagogiky, mají-li mechanismy blízké larpům. ČSLD by měla shromažďovat primárně české a slovenské hry, larpy zahraniční provenience tu mají své místo pouze v případě, že byly na území Česka či Slovenska někdy uvedeny.</p>

        <p>Smyslem databáze je vytvořit prostor pro diskuzi a výměnu názorů nad jednotlivými hrami. Tím, že se zaregistrujete a stanete se hodnotitelem, můžete napomoci ostatním hráčům v rozhodování, zda se daného larpu či akce v budoucnu zúčastnit či ne. Poskytnete také důležitou zpětnou vazbu organizátorům, aby mohli své hry vylepšovat. Uživatelé ČSLD svými hodnoceními a komentáři tedy přispívají k snazší orientaci na současné larpové scéně a zároveň napomáhají zvyšovat její kvalitu. Proto zde vítáme každého nového uživatele!</p>


        <div class="nadpisStranky">Časté otázky</div>
          <ol>
          <li><a href="\" onclick="$('#hodnoceniher').slideToggle('slow'); return false;">Jak funguje hodnocení her?</a></li>
          <div id="hodnoceniher" class="skryty" style="display: block;">Hodnocení 10 je nejvyšší, hodnocení 0 pak nejnižší. Používáme stejné barevné rozlišení jako ČSFD, které je generováno na základě průměrového hodnocení uživatelů. Larp, který nemá ještě čtyři hodnocení, je zabarven šedě. Nad čtyři ohodnocení je barevné spektrum následující: červená (nad 70 %), modrá (30-70 %), černá (1-30 %).</div>
          <li><a href="\" onclick="$('#komentare').slideToggle('slow'); return false;">Co patří do komentářů a proč je psát?</a></li>
          <div id="komentare" class="skryty" style="display: none;">Komentáře jsou nejlepším modelem zpětné vazby od hráčů tvůrci. Procentuální hodnocení není zdaleka tak výstižné a nedává autorům hry potřebný feedback. Proto jsme rádi, když doprovodíte své hodocení i komentářem. Komentář by měl být jakousi minirecenzí. Patří sem samozřejmě váš bezprostřední subjektivní dojem, ale ještě raději jsme za pokus objektivně zhodnotit, v jakých ohledech hra funguje či nefunguje, co je na ní zajímavého a činí ji specifickou. Naopak sem nepatří výkřiky do tmy ve stylu: "Tenhle larp stojí za starou bačkoru." V případě urážlivého či vulgárního komentáře si vyhrazujeme možnost váš komentář smazat. Komentář by taky neměl obsahovat výrazné spoilery, které kazí hráči překvapení z herního vývoje.</div>
          <li><a href="\" onclick="$('#hraljsem').slideToggle('slow'); return false;">K čemu slouží u larpu tlačítku "hrál jsem"?</a></li>
          <div id="hraljsem" class="skryty" style="display: none;">Po zakliknutí se přemístí hra do vaší osobní "larpotéky", generované na osobním profilu. Je užitečné si je zaklikávat před hodnocením jakékoli hry, protože tak získáváte přehled o tom, které hry jste už hodnotili či komentovali.</div>
          <li><a href="\" onclick="$('#pridavaniher').slideToggle('slow'); return false;">Mohu přidávat i larpy, které jsem sám nehrál či nepořádal?</a></li>
          <div id="pridavaniher" class="skryty" style="display: none;">Ano, máte-li dost informací k vyplnění formuláře. Pokud nemáte, je užitečnější doplníte-li si napřed u tvůrce všechny potřebné údaje a vyzískáte-li si logo hry.</div>
          <li><a href="\" onclick="$('#novyautor').slideToggle('slow'); return false;">Jak přidám nového autora larpu?</a></li>
          <div id="novyautor" class="skryty" style="display: none;">Pod položkou "přidat hru". Je vhodné nejprve přidat všechny autory hry, než se pustíte do přidávání nového larpu.</div>
          <li><a href="\" onclick="$('#vicerocniku').slideToggle('slow'); return false;">Jak je to s přidáváním larpu, které mají ročníky?</a></li>
          <div id="vicerocniku" class="skryty" style="display: none;">Jedná-li se o sérii larpů, které se pod stejným názvem opakuju v (např. ročních) intervalech, ale jejich děj je s každým ročníkem jiný, měl by mít každý ročník svoji vlastní stránku ideálně odlišenou podnázvem (např. Asterion 3: Dračí gambit) nebo alespoň vročením (Bitva pěti armád 2011).</div>
          <li><a href="\" onclick="$('#podnety').slideToggle('slow'); return false;">Kde můžu vznést podněty k dalšímu rozvoji databáze?</a></li>
          <div id="podnety" class="skryty" style="display: none;">Ideálně na naší facebookové skupině, která je přesně tomuto účelu zasvěcena (<a href="https://www.facebook.com/groups/larpovadatabaze/" target="_blank_">https://www.facebook.com/groups/larpovadatabaze/</a>) nebo na našem kontaktním emailu.</div>
          
          
          
          <li><a href="\" onclick="$('#prvni').slideToggle('slow'); return false;">Je hodnocení her objektivní?</a></li>
          <div id="prvni" class="skryty" style="display: none;">Není. A z principu ani nemůže být. Larp netvoří organizátor, ale z (různě velké části) hráči. Ti vytváři tu méně, tu více zajímavých herních situací a jsou to hlavně oni, kteří tvoří larp. I více běhů stejného larpu může být hodně rozdílných. Známe to na vlastní kůži. Cílem hodnocení není vytořit absolutně objektivní srovnání, ale poskytnout přibližnou orientaci hráčům, tvůrcům či organizátorům festivalů v nepřehledném larpovém poli.</div>
          <li><a href="\" onclick="$('#druhy').slideToggle('slow'); return false;">Jak správně používat štítky?</a></li>
          <div id="druhy" class="skryty">Štítky mají pomoci v rozlišení jednotlivých druhů larpu. Každý larp musí mít alespoň jeden štítek označující druh larpu. Existuje spousta larpů, které spadají do více kategorií a na první pohled můžou být štítky protichůdné. Štítky mají definovat ty vlastnosti, které jsou jim společné, proto larp odehrávající se v jedné místnosti, avšak s dobře připravenými postavami a řízeným dějem dostane štítky dva - komorní a dramatický. Maximální počet štítků je deset, ale užívejte je prosím s rozumem.</div>
          <li><a href="\" onclick="$('#treti').slideToggle('slow'); return false;">K čemu slouží moderátoři?</a></li>
          <div id="treti" class="skryty"">Moderátoři dohlíží na chod databáze. Po pečlivém posouzení schvalují přidávání uživateli navržených (nových) štítků a kontrolují, že štítky ke hrám jsou přiřazeny správně; připadné chyby opraví. Pokud rozporný štítek přiřadil samotný tvůrce larpu, vždy se ptají tvůrce, proč přiřadil právě tento štítek. Nikdy nemažou štítky přiřazené autorem bez předchozí diskuse.</div>
          <li><a href="\" onclick="$('#ctvrty').slideToggle('slow'); return false;">Proč je hodnocení uživatelů skryté?</a></li>
          <div id="ctvrty" class="skryty">Zatímco na filmové databázi neexistuje osobní vztah mezi autorem filmu a divákem, u larpu tomu tak není. Hráči velmi často nadhodnocují larpy, protože si tvůrce váží, ačkoliv hra nebyla objektivně tak dobrá. Skrytím hodnocení uživatelů chceme dosáhnout reálného hodnocení namísto hodnocení ze zdvořilosti.</div>
          <li><a href="\" onclick="$('#paty').slideToggle('slow'); return false;">Od kolika hodnocení se hra zařadí do žebříčku?</a></li>
          <div id="paty" class="skryty">Od čtyřech.</div>
          <li><a href="\" onclick="$('#sesty').slideToggle('slow'); return false;">Jak se vypočítává žebříček?</a></li>
          <div id="sesty" class="skryty">Pro výpočet žebříčku používáme váhované hodnocení. Algoritmus jsme převzali z IMDb, jeho přesný vzorec najdete na <a href="http://en.wikipedia.org/wiki/Internet_Movie_Database#Ranking_.28IMDb_Top_250.29" target="_blank">wikipedii</a>. Proměnnou m jsme prozatím stanovili na hodnotu 10.</div>
          <li><a href="\" onclick="$('#sedmy').slideToggle('slow'); return false;">Jaké je technické řešení databáze?</a></li>
          <div id="sedmy" class="skryty">Databáze běží na databázovém systému postgreSQL, programována je v jazyce java. Webdesign je řešený pomoci xhtml, css, javascriptu a knihovny jquery včetně jejích pluginů. Pokud Vám tyto pojmy nejsou cizí a chtěli byste nám pomoci s vylepšením databáze, napiště nám prosím na kontaktní e-mail.</div>
          <li><a href="\" onclick="$('#osmy').slideToggle('slow'); return false;">Jak je databáze financována?</a></li>
          <div id="osmy" class="skryty">Hosting pro databázi běží na virtuálním serveru, který má v pronájmu hlavní administrátor Balda a pro účely databáze ho poskytuje zcela zdarma. Na něm je pak pro databázi vyhrazeno 10GB diskového prostoru. Pokud bude nutnost hosting rozšířit či změnit, do budoucna zvažujeme různé modely financování, včetně dobrovolných příspěvků.</div>
          <li><a href="\" onclick="$('#devaty').slideToggle('slow'); return false;">Larpová databáze mi připadá jako smysluplný projekt. Jak Vás můžu odměnit?</a></li>
          <div id="devaty" class="skryty">Larpová databáze v současnosti není výdělečným projektem a negeneruje žádný zisk. Pokud nás chcete nějak odměnit, pozvěte nás na některé akci na pivo ;-).</div>
          </ol>
    </div>

<!-- PRAVA CAST -->

<div id="pravaCastOdatabazi">
    <div class="praveMenu">
        <div class="nadpisMenu">kontakty</div>
    </div>
    <div class="sekce">
        <table>
            <tr>
                <td>
                    <a href="/pridatHru.jsp"><img src="img/icon/plus_icon.png" class="pridatIkona"></a>
                </td>
                <td class="pridatPopis">
                    <a href="/pridatHru.jsp">Přidat hru</a>
                </td>
            </tr>
        </table>
    </div>
    <div class="sekce">
        <div class="nadpisSekce">Napište nám</div>
        <div class="napisteNam"><a href="mailto: larpovadatabaze@gmail.com">larpovadatabaze@gmail.com</a></div>
    </div>
    <div class="sekce">
        <div class="nadpisSekce">Administrátoři</div>
        <table>
            <%
                Users users = new Users(conn);
                List<User> admins = users.getAdministrators();
                List<User> moderators = users.getModerators();
                for(User admin: admins){
                    Person person = admin.getPerson();
            %>
            <tr>
                <td>
                    <a href="uzivatel.jsp?id=<%=admin.getId()%>"><img src="<%=person.getImage()%>" class="obrazekModerator"></a>
                </td>
                <td class="obsah">
                    <a href="uzivatel.jsp?id=<%=admin.getId()%>"><span class="nick"><%=person.getNickName()%></span> <span class="jmeno"><%=person.getFullName()%></span></a>
                    <div class="popisek"><a href="mailto:"><%=person.getEmail()%></a><br /></div>
                </td>
            </tr>
            <%
                }
            %>
        </table>
    </div>
    <div class="sekce">

        <div class="nadpisSekce">Moderátoři</div>
        <table>
            <%
                for(User moderator: moderators){
                    Person person = moderator.getPerson();
            %>
            <tr>
                <td>
                    <a href="uzivatel.jsp?id=<%=moderator.getId()%>"><img src="<%=person.getImage()%>" class="obrazekModerator"></a>
                </td>
                <td class="obsah">
                    <a href="uzivatel.jsp?id=<%=moderator.getId()%>"><span class="nick"><%=person.getNickName()%></span> <span class="jmeno"><%=person.getFullName()%></span></a>
                </td>
            </tr>
            <%
                }
            %>
        </table>
    </div>
</div>




<!-- FOOTER -->
<%@include file="layout/footer.jsp" %>