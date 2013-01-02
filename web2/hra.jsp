<%@ page import="java.util.List" %>
<%@ page import="java.util.Collections" %>
<%@ page import="org.pilirion.models.game.*" %>
<%@ page import="org.pilirion.models.game.Game" %>
<%@ page import="org.pilirion.models.game.Ratings" %>
<%@ page import="org.pilirion.models.game.Rating" %>
<%@ page import="org.pilirion.models.user.Users" %>
<%@ page import="org.pilirion.models.user.User" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/layout/header.jsp" %>
<body>
<link rel="stylesheet" type="text/css" href="/templates/css/js.css"/>
<div class="page">

<!-- HEADER -->
<%@include file="layout/head.jsp" %>

<script type="text/javascript">
    var loggedUser = <% if(loggedUser == null) {out.print("false");} else {out.print("true");}%>;
    var role = <% if(loggedUser == null) {out.print("1");} else {out.print(loggedUser.getRole().getId());}%>;
</script>
<script type="text/javascript" src="/templates/js/content/hra.js"></script>
<!-- GAME -->
<!-- LEVA CAST -->

    <%
        int gameId = Integer.parseInt(request.getParameter("id"));
        Games games = new Games(conn);
        Game game = games.getById(gameId);
        List<Comment> comments = game.getComments();
        Collections.reverse(comments);
    %>
<div class="levaCast" id="game">
    <%
        if (loggedUser != null && loggedUser.getRole().getId() > 2) {
    %>
    <div class="button" id="editovatHru" style="cursor: pointer;"><b>Editovat</b></div>
    <%
        }
    %>
    <%
        if (loggedUser != null) {
            int userId = loggedUser.getId();
            String checked = "";
            if (games.isUserPlayerOfGame(userId, gameId)) {
                checked = "checked=\"checked\"";
            }
    %>
    <form id="ucastFormular" method="post" action="/handlers/ucast.jsp">
        <ul class="checklist" style="position: absolute; top: 180px; left: 530px;">
            <li>
                <input type="hidden" name="gameId" value="<%=gameId%>"/>
                <input name="hral_jsem" value="hral_jsem" type="checkbox" <%=checked%> id="hral_jsem"/>
                <a class="checkbox-select stitekNapoveda played" href="#">Hrál/a jsem</a>
                <a class="checkbox-deselect stitekNapoveda played" href="#">Hrál/a jsem</a>
            </li>
        </ul>
    </form>
    <%
        }
    %>

    <table>
        <tr>
            <td class="obrazekHraBunka">
                <img src="<%=game.getImage()%>" class="obrazekHra" alt="">
            </td>
            <td class="bunkaHra">
                <div class="nadpisHra"><span id="nazevHry"><%=game.getName()%></span>
                </div>
                <div class="popisek">
                        <div id="labels">
                        <%
                            out.println(game.getLabelsText());
                        %>
                        </div>
                </div>
                <div class="popisek">
                    <span id="playersAmount"><%=game.getPlayersAmount()%></span> hráčů, <span
                        id="menRole"><%=game.getMenRoles()%></span> mužů, <span
                        id="womenRole"><%=game.getWomenRoles()%></span> žen, <span
                        id="bothRole"><%=game.getBothRole()%></span> obojetných
                </div>
                <div class="popisek">
                    <span id="hours"><%=game.getHours()%></span> hodin, <span id="days"><%=game.getDays()%></span> dnů,
                    <span id="year"><%=game.getYear()%></span>
                </div>
                <div class="popisek">
                    Autoři:
                        <div id="authors">
                        <%
                            out.println(game.getAuthorsText());
                        %>
                        </div>
                </div>
            </td>
        </tr>
    </table>
    <div class="obsah"><div id="description"><%=game.getDescription()%></div>
    </div>

    <div class="menuHra">
        <div class="vybranaPolozka">komentáře</div>
        <div class="polozka">fotky</div>
        <div class="polozka">videa</div>
    </div>

    <% if (loggedUser != null) {%>
    <table class="pridatKomentarTabulka">
        <tr>
            <td><a href="\" onclick="$('#pridatKomentar').slideToggle('slow'); return false;"><img
                    src="img/icon/comment_icon.png"></a>
            </td>
            <td class="pridatKomentarNadpis"><a href="\"
                                                onclick="$('#pridatKomentar').slideToggle('slow'); return false;">Přidat
                komentář</a>
            </td>
        </tr>
    </table>

    <div id="pridatKomentar">
        <form id="komentarFormular" method="post" action="/handlers/komentar.jsp">
            <input type="hidden" name="gameId" id="gameIdComment" value="<%=gameId%>"/>

            <textarea class="textarea" name="comment_text" id="komentar" title="komentar" value=""
                      style="width: 90%;height: 100%;" rows="4"></textarea>
            <button class="pridatKomentarButton" type="submit" name="pridatKomentarButton" title="pridatKomentarButton">
                Odeslat komentář
            </button>
        </form>
    </div>
    <% } %>


    <div class="komentare">
        <%
            Users users = new Users(conn);
            for (Comment comment : comments) {
                User user = users.getById(comment.getUserId());
        %>
        <div class="komentar">
            <table>
                <tr>
                    <td class="bunkaObrazek">
                        <img src="<%=user.getPerson().getImage()%>" class="obrazekUziv" alt=""><br/>

                        <div class="datum"><%=comment.getDate()%>
                        </div>
                    </td>
                    <td class="bunkaKomentar">
                        <a href="/uzivatel.jsp?id=<%=user.getPerson().getId()%>"><span class="nick"><%=user.getPerson().getNickName()%></span>
                        <span class="jmeno"><%=user.getPerson().getFirstName() + " " + user.getPerson().getLastName()%></span></a>

                        <div class="obsahKomentar"><%=comment.getText()%>
                        </div>
                    </td>
                </tr>
            </table>
        </div>
        <%
            }
        %>
    </div>

</div>

<!-- PRAVE MENU -->

<div class="pravaCast" id="game">
    <div class="hodnoceni" style="background-color: <%=game.getRatingColor()%>">
        <div class="hodnoceniVysledne"><%
            if (game.getRatings().size() > 3) {
                out.print(game.getRatingPercents() + "&thinsp;%");
            } else {
                out.print("?&thinsp;%");
            }
        %></div>
        <div class="hodnotilo">Hodnotilo <%=game.getRatings().size()%> uživatelů</div>
    </div>
    <div class="sekce">
        <%
            Rating yourRating = new Rating(-1, 5, gameId, -1);
            Rating rating;
            if (loggedUser != null) {
                Ratings ratings = new Ratings(conn);
                rating = ratings.getRatingGameUser(gameId, loggedUser.getId());
                yourRating = (rating != null) ? rating : yourRating;
                String ratingToWrite = (rating != null) ? String.valueOf(rating.getRating()) : "Dosud jste nehodnotili";
        %>
        <div class=hodnoceniUziv>Vaše hodnocení je: <%=ratingToWrite%>
        </div>
        <%
        } else {
        %>
        <div class=hodnoceniUziv>Pro hodnocení se musíte přihlásit.</div>
        <%
            }
        %>
        <%
            if (loggedUser != null) {
        %>
        <script type="text/javascript">
            $(document).ready(function () {
                $("#sliderHodnoceni").slider({ max:10, min:1, step:1, value: <%=yourRating.getRating()%> });
            });
            $(document).ready(function () {
                $("#sliderHodnoceni").slider({
                    slide:function (event, ui) {
                        $("#vypisHodnoceni").html(ui.value);
                        $("#rating").val(ui.value);
                    }
                });
            });
        </script>
        <div id="sliderHodnoceni"></div>
        <form id="hodnoceniFormular" method="post" action="/handlers/hodnoceni.jsp">
            <input type="hidden" name="gameId" value="<%=gameId%>"/>
            <input type="hidden" id="rating" name="rating" value="5"/>
            <input type="submit" value="Hodnotit" id="tlacitkoHodnotit">
        </form>
        <div id="vypisHodnoceni"><%=yourRating.getRating()%>
        </div>
        <% } %>
    </div>

    <%
        if (game.getPremier() != null) {
    %>
    <div class="sekce">

        <div class="nadpisSekce">Premiéra</div>
        <div class="obsah">
            <%=game.getPremier()%>
        </div>
    </div>
    <%
        }
    %>

    <div class="sekce">
        <div class="nadpisSekce">Podobné larpy</div>
        <div class="obsah">
            <%
                List<Game> similarGames = games.getSimilarLarps(game, 6);
                for (Game similar : similarGames) {
            %>
            <div class="obsahPolozka"><img src="img/ctverecek.png" class="ctverecek"
                                           style="background-color: <%=similar.getRatingColor()%>" alt="">
                <a href="/hra.jsp?id=<%=similar.getId()%>"><%=similar.getName()%> (<%=similar.getYear()%>)</a></div>
            <%
                }
            %>
        </div>
    </div>

    <div class="sekce">
        <div class="nadpisSekce">Larpy autorů</div>
        <div class="obsah">
            <%
                List<Game> gamesOfAuthors = games.getGamesOfAuthors(game.getAuthors());
                for (Game authorGame : gamesOfAuthors) {

            %>
            <div class="obsahPolozka"><img src="img/ctverecek.png" class="ctverecek"
                                           style="background-color: <%=authorGame.getRatingColor()%>" alt="">
                <a href="/hra.jsp?id=<%=authorGame.getId()%>"><%=authorGame.getName()%> (<%=authorGame.getYear()%>)</a></div>
            <%
                }
            %>
        </div>
    </div>

</div>

<div style="clear: both"></div>


<%@include file="layout/footer.jsp" %>
