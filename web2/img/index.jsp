<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.pilirion.models.user.Users" %>
<%@ page import="org.pilirion.models.user.User" %>
<%@ page import="org.pilirion.models.game.*" %>
<%@ page import="org.pilirion.models.game.Ratings" %>
<%@ page import="org.pilirion.models.game.Rating" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/layout/header.jsp" %>
<body>
<div class="page">

<!-- HEADER -->

<%@include file="/layout/head.jsp" %>

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
                  pauseTime:5000,
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
            <div class="dalsiPridaneHryOdkaz"><a href="/" onclick="$('#dalsiPridaneHry').slideToggle('slow'); return false;">Další přidané hry</a></div>
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
                List<Comment> lastComments = comments.getLastComments(2);
                for (Comment comment : lastComments) {
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
        <div class="obsah">
            <a href="">
                <div class="polickoSekce">
                    <div class="nadpisKalendar">14.12.2012 <b>Setkání larp.cz</b></div>
            </a>

            <div class="popisek">
                Brno, Jihomoravský kraj<br/>
                pořádá Moravian larp
            </div>
                </div>
        </div>
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