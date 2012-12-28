<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.pilirion.models.user.Users" %>
<%@ page import="org.pilirion.models.user.User" %>
<%@ page import="org.pilirion.models.game.*" %>
<%@ page import="org.pilirion.models.game.Ratings" %>
<%@ page import="org.pilirion.models.game.Rating" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="layout/header.jsp" %>
<body>
<div class="page">

<!-- HEADER -->

<%@include file="layout/head.jsp" %>

<!-- HOMEPAGE -->

<script type="text/javascript">

$(document).ready(function() {
          var total = $('#nivoSlider img').length;
          var rand = Math.floor(Math.random()*total);
          jQuery(window).load(function(){
              jQuery("#nivoSlider").nivoSlider({
                  effect:'boxRainGrow',
                  slices:15,
                  boxCols:8,
                  boxRows:4,
                  animSpeed:500,
                  pauseTime:6000,
                  startSlide: rand,
                  directionNav:true,
                  controlNav:false,
                  controlNavThumbs:false,
                  pauseOnHover:false,
                  manualAdvance:false
              });
          });
        });
        
</script>

<script type="text/javascript">
jQuery(document).ready(function() {
jQuery.getJSON('http://devel.larp.cz/misc/api/kalendar-beta.php', function(data){ //vyžádáme si data z api, zpracujeme je jako JSON a uložíme do proměnné "data" s kterou budeme dál pracovat
  var maxAkci=3; //ruční limit počtu vypsaných akcí - databázový je 30 (více se ani tak nevypíše)
  if(data.chyba)  //pokud nastala chyba php, měla by nám být předána v parametru chyba, vypsat
  {
    alert(data.chyba.popis);
  }
  else if(!data.larpy) //pokud neexistuje parametr larpy (ale nebyla chyba), něco je kardinálně špatně
  {
    alert('Špatný formát přijatých dat');
  }
  else //zřejmě je vše v pořádku
  {
    var larpy=data.larpy; //do proměnné larpy si uložíme pole larpů, které se nám načetlo z JSONu
    var text=''; //výstupní buffer, do kterého si budeme ukládat html připravované tabulky
    var temp=0;
    var randomIndex=0;
    var random=0;
    var pool= new Array(0,1,2,3,4,5,6,7,8,9);
    var k=new Array();
    for( var i=0; i<larpy.length && i<maxAkci; i++ ) {
      randomIndex=Math.floor( Math.random()*pool.length );
      random=pool[randomIndex];
      pool.splice(randomIndex,1); //odstraní prvek z pole
      k[i]=random;
    }
    k.sort();
    for(var i=0; i<larpy.length && i<maxAkci; i++) //projdeme polem larpů
    {
      temp=k[i];
      var larp=larpy[temp]; //aktuální larp si uložíme do proměnné larp
      //začátek (konec) larpu jsou uvedeny v javascriptem zpracovatelném formátu, vytvoříme si z nich tedy objekty typu Date (datum)
      var z=new Date(larp.zacatek);
      text+='<div class="polickoSekce">'; //postupně si vytváříme řádek budoucí tabulky a ukládáme do proměnné text
      text+='<a href="'+larp.link+'" target="_blank"><div class="nadpisKalendar">'+z.getDate()+'.'+(z.getMonth()+1)+'.'+z.getFullYear()+'<b> '+larp.nazev+'</b></div></a>';
      text+='<div class="popisek">'+larp.obec+', '+larp.kraj+'</div><div class="popisekNormal">pořádá '+larp.poradatel+'</div>';
      text+='</div>';
    }
    //nakonec přepíšeme celý obsah elementu tbody proměnnou text
    jQuery('#kalendarObsah').html(text);
  }
});
});
</script>

<div class="levaCast">
    <div id="nivoSlider" class="nivoSlider">
        <img src="/img/slider/panprstenu650.jpg" alt=""
             title="Dřevárny jsou hry v přírodě a znamenají hlavně adrenalin a souboje armád bezpečnými zbraněmi."/>
        <img src="/img/slider/santiago650.jpg" alt=""
             title="Komorní larpy kladou důraz na hlubší ztvárnění role a hrají se jen pár hodin."/>
        <img src="/img/slider/pass650.jpg" alt=""
             title="Postapokalyptické larpy simulují přežití po světové katastrofě."/>
        <img src="/img/slider/borograv650.jpg" alt=""
             title="Dramatické larpy disponují silným, obvykle řízeným příběhem a jsou pro větší počet hráčů."/>
        <img src="/img/slider/thief650.jpg" alt=""
             title="Městské larpy se hrají ve městech a kombinují roli s akčními prvky."/>
        <img src="/img/slider/bezkrali650.jpg" alt="" title="Larpy typu svět se hrají v přírodě a usilují o reálnou simulaci obchodních a politických vztahů."/>
        <img src="/img/slider/gangy650.jpg" alt=""
             title="Městské larpy se hrají ve městech a kombinují roli s akčními prvky."/>
    </div>

    <div id="vypisy">
        <div class="sloupecLevy">
            <div class="nadpis">Naposledy přidáno</div>
            <%
                System.out.println("test");
                Games games = new Games(conn);
                List<Game> last = games.getLastAdded(20);
                List<Game> firstTwo = new ArrayList<Game>();
                List<Game> anotherEighteen = new ArrayList<Game>();
                for (int i=0; i<2; i++) {
                    firstTwo.add(last.get(i));
                }
                for (int i=2; i<20; i++) {
                    anotherEighteen.add(last.get(i));
                }

                for (Game game : firstTwo) {
                    Label required = game.getRequiredLabel();
                    String requiredName = (required != null) ? required.getName() : "";
            %>
            <div class="pridanaHra">
                <table>
                    <tr>
                        <td>
                            <a href="/hra.jsp?id=<%=game.getId()%>"><img style='width: 75px; height: 75px;' src="<%=game.getImage()%>" class="obrazekPopis"
                                                                   alt=""></a><br/>

                        </td>
                        <td class="obsah">
                            <img alt="" src="/img/ctverecek.png" class="ctverecek"
                                 style="background-color: <%=game.getRatingColor()%>"/></span>
                                <span class="nadpisHra"><a href="/hra.jsp?id=<%=game.getId()%>"><%=game.getName()%>
                                </a></span>

                            <div class="popisek"><%=requiredName%>, <%=game.getPlayersAmount()%> hráčů<br/>
                                <%=game.getShortenedDescription()%><a
                                        href="/hra.jsp?id=<%=game.getId()%>"> více</a></div>
                        </td>
                    </tr>
                </table>
            </div>
            <%
                }
            %>
            <div class="viceOdkaz"><a href="/" onclick="$('#dalsiPridaneHry').slideToggle('slow'); return false;">více ...</a></div>
                        <div id="dalsiPridaneHry">

            <%
            for (Game game : anotherEighteen) {
                    Label required = game.getRequiredLabel();
                    String requiredName = (required != null) ? required.getName() : "";
            %>

                <div class="pridanaHra">
                    <table>
                        <tr>
                            <td>
                                <a href="/hra.jsp?id=<%=game.getId()%>"><img style='width: 75px; height: 75px;' src="<%=game.getImage()%>" class="obrazekPopis"
                                                                       alt=""></a><br/>

                            </td>
                            <td class="obsah">
                                <img alt="" src="/img/ctverecek.png" class="ctverecek"
                                     style="background-color: <%=game.getRatingColor()%>"/></span>
                                    <span class="nadpisHra"><a href="/hra.jsp?id=<%=game.getId()%>"><%=game.getName()%>
                                    </a></span>

                                <div class="popisek"><%=requiredName%>, <%=game.getPlayersAmount()%> hráčů<br/>
                                    <%=game.getShortenedDescription()%><a
                                            href="/hra.jsp?id=<%=game.getId()%>"> více</a></div>
                            </td>
                        </tr>
                    </table>
                </div>
            <%
                }
            %>
            </div>
        </div>

        <div class="sloupecPravy">
            <div class="nadpis">Poslední komentáře</div>
            <%
                Comments comments = new Comments(conn);
                Users users = new Users(conn);
                List<Comment> lastComments = comments.getLastComments(20);
                List<Comment> firstTwoComments = new ArrayList<Comment>();
                List<Comment> anotherEighteenComments = new ArrayList<Comment>();
                   
                for (int i=0; i<2; i++) {
                    firstTwoComments.add(lastComments.get(i));
                }
                for (int i=2; i<20; i++) {
                    anotherEighteenComments.add(lastComments.get(i));
                }

                for (Comment comment : firstTwoComments) {
                    User user = users.getById(comment.getUserId());
                    Game game = games.getById(comment.getGameId());
            %>         
            <div class="pridanaHra">
                <table>
                    <tr>
                        <td>
                            <a href="/uzivatel.jsp?id=<%=comment.getUserId()%>"><img style='width: 75px; height: 75px;' src="<%=user.getPerson().getImage()%>"
                                                                          class="obrazekPopis" alt=""></a><br/>

                            <div class="datum"><%=comment.getDate()%>
                            </div>
                        </td>
                        <td class="obsah">
                                <span class="nadpisHra"><a
                                        href="/uzivatel.jsp?id=<%=user.getId()%>"><%=user.getPerson().getNickName()%>
                                </a></span> o hře <span
                                class="nadpisHra"><img src="img/ctverecek.png" class="ctverecek"
                                                       style="background-color: <%=game.getRatingColor()%>" alt="">
                                <a href="/hra.jsp?id=<%=game.getId()%>"><%=game.getName()%>
                                </a></span>

                            <div class="popisek"><%=comment.getShortenedText()%> <a
                                    href="/hra.jsp?id=<%=game.getId()%>">více</a></div>
                        </td>
                    </tr>
                </table>
            </div>
            <%
                }
            %>
            <div class="viceOdkaz"><a href="/" onclick="$('#dalsiKomentare').slideToggle('slow'); return false;">více ...</a></div>
            <div id="dalsiKomentare">

            <%
            for (Comment comment : anotherEighteenComments ) {
                    User user = users.getById(comment.getUserId());
                    Game game = games.getById(comment.getGameId());
            %>
            <div class="pridanaHra">
                <table>
                    <tr>
                        <td>
                            <a href="/uzivatel.jsp?id=<%=comment.getUserId()%>"><img style='width: 75px; height: 75px;' src="<%=user.getPerson().getImage()%>"
                                                                          class="obrazekPopis" alt=""></a><br/>

                            <div class="datum"><%=comment.getDate()%>
                            </div>
                        </td>
                        <td class="obsah">
                                <span class="nadpisHra"><a
                                        href="/uzivatel.jsp?id=<%=user.getId()%>"><%=user.getPerson().getNickName()%>
                                </a></span> o hře <span
                                class="nadpisHra"><img src="img/ctverecek.png" class="ctverecek"
                                                       style="background-color: <%=game.getRatingColor()%>" alt="">
                                <a href="/hra.jsp?id=<%=game.getId()%>"><%=game.getName()%>
                                </a></span>

                            <div class="popisek"><%=comment.getShortenedText()%> <a
                                    href="/hra.jsp?id=<%=game.getId()%>">více</a></div>
                        </td>
                    </tr>
                </table>
            </div>
            <%
                }
            %>
            </div>

        </div>

    </div>

</div>

                                    <!-- PRAVE MENU -->

<div class="pravaCast">
    <div class="praveMenu">
        <div class="nadpisMenu">rychlá navigace</div>
    </div>
    <%
        if(loggedUser != null){
    %>
    <div class="sekce">
        <table>
            <tr>
                <td>
                    <a href="/pridatHru.jsp"><img src="/img/icon/plus_icon.png" class="pridatIkona"></a>
                </td>
                <td class="pridatPopis">
                    <a href="/pridatHru.jsp">Přidat hru</a>
                </td>
            </tr>
        </table>
    </div>
    <%
        } else {
    %>
    <div class="sekce">
        Pro přidání hry je potřeba se nejprve zaregistrovat.
    </div>
    <%
        }
    %>
    <div class="sekce">
        <div class="nadpisSekce">Kalendář akcí</div>
        <div id="kalendarObsah">
        <!-- obsah z javascriptu v "hlavicce" -->
        </div>
        <div class="viceOdkazKalendar"><a href="http://kalendar.larp.cz" target="_blank"> více ...</a></div>
    </div>

    <div class="sekce">
        <div class="nadpisSekce">Náhodný larp</div>
        <%
            Game random = games.getRandomGame();
            if (random != null) {
        %>
        <table>
            <tr>
                <td>
                    <a href="/hra.jsp?id=<%=random.getId()%>"><img alt="" src="<%=random.getImage()%>" class="obrazekHra"></a>
                </td>
                <td class="obsah">
                    <a href="/hra.jsp?id=<%=random.getId()%>"><img src="/img/ctverecek.png" class="ctverecek"
                                                             style="background-color: <%=random.getRatingColor()%>"
                                                             alt=""/></span><span
                            class="nadpisHra"><%=random.getName()%></span></a>

                    <div class="popisek"><%=random.getLabelsText()%>, <%=random.getPlayersAmount()%> hráčů<br/>
                        <%=random.getYear()%><br/>
                        Hodnocení: <%=random.getRatingPercents()%>%
                    </div>
                </td>
            </tr>
        </table>
        <%
            }
        %>
    </div>

    <div class="sekce">
        <%
            int amountOfUsers = users.getAllUsers().size();
            Authors authors = new Authors(conn);
            int amountOfAuthors = authors.getAllAuthors().size();
            int amountOfComments = comments.getComments().size();
            int amountOfGames = games.getGames().size();
            Ratings ratings = new Ratings(conn);
            int amountOfRatings = ratings.getRatings().size();
            int averageRating = ratings.getAverage();
        %>
        <div class="nadpisSekce">Statistiky</div>
        <table>
            <tr>
                <td>
                    <div style="width: 60px" class="pruhStatistika"><%=amountOfUsers%>
                    </div>
                </td>
                <td class="obsah">
                    <div class="popisek">uživatelů</div>
                </td>
            </tr>
            <tr>
                <td>
                    <div style="width: 18px" class="pruhStatistika"><%=amountOfAuthors%>
                    </div>
                </td>
                <td class="obsah">
                    <div class="popisek">autorů</div>
                </td>
            </tr>
            <tr>
                <td>
                    <div style="width: 81px" class="pruhStatistika"><%=amountOfComments%>
                    </div>
                </td>
                <td class="obsah">
                    <div class="popisek">komentářů</div>
                </td>
            </tr>
            <tr>
                <td>
                    <div style="width: 35px" class="pruhStatistika"><%=amountOfGames%>
                    </div>
                </td>
                <td class="obsah">
                    <div class="popisek">larpů</div>
                </td>
            </tr>
            <tr>
                <td>
                    <div style="width: 100px" class="pruhStatistika"><%=amountOfRatings%>
                    </div>
                </td>
                <td class="obsah">
                    <div class="popisek">hodnocení</div>
                </td>
            </tr>
            <tr>
                <td>
                    <div style="width: 81px" class="pruhStatistika"><%=averageRating%>%</div>
                </td>
                <td class="obsah">
                    <div class="popisek">průměrné hodnocení</div>
                </td>
            </tr>
        </table>
    </div>
</div>
<div style="clear: both"></div>
<%@include file="layout/footer.jsp" %>