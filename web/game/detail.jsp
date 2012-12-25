<%@ page import="org.pilirion.models.game.Ratings" %>
<%@ page import="org.pilirion.models.game.Rating" %>
<%@ page import="java.util.List" %>
<%@ page import="org.pilirion.models.game.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/templates/header.jsp"%>
<%
    Games games = new Games(conn);
    int gameId = Integer.parseInt(request.getParameter("id"));
    Game game = games.getById(gameId);
    if(game == null){
        RequestDispatcher request_Dispatcher=request.getRequestDispatcher(resource.getDefault());
        request_Dispatcher.forward(request, response);
        return;
    }
%>
<%@include file="/layout/header.jsp"%>
<%@include file="/layout/menu.jsp"%>
<div id="content">
    <%
        if(loggedUser != null) {
    %>
    <script type="text/javascript">
        document.observe('dom:loaded', function wasHereLoaded(){
            var wasHereDiv = $('wasHere');

            wasHereDiv.observe('click', function saveUserAsPresent(){
                var requestParams = {
                    onSuccess: function onSuccessWasHere(response){
                        var statusObj = response.responseText.strip().evalJSON();
                        var status = statusObj.status;
                        if(status == "ok"){
                            if(wasHereDiv.hasClassName("wasNotThere")){
                                wasHereDiv.removeClassName("wasNotThere");
                                wasHereDiv.update("Byl jsem na akci")
                            } else {
                                wasHereDiv.addClassName("wasNotThere");
                                wasHereDiv.update("Nebyl jsem na akci");
                            }
                        }
                    },

                    parameters: {
                        gameId: "<%=gameId%>",
                        userId: "<%=loggedUser.getId()%>"
                    }
                };

                new Ajax.Request('/game/playedByUserAjax.jsp', requestParams);
            });
        });
    </script>
    <%
        }
    %>
<div id="game">
    <div class="fb-like" data-href="http://www.csldb.cz/game/detail/<%=gameId%>" data-send="true" data-width="450"
         data-show-faces="true"></div>
    <%
        if(loggedUser != null){
            int userId = loggedUser.getId();
            if(games.isUserPlayerOfGame(userId, gameId)){
        %>
            <div id="wasHere" name="wasHere" class="button wasNotThere">Nebyl jsem na akci</div>
        <%
            } else {
        %>
            <div id="wasHere" name="wasHere" class="button">Byl jsem na akci</div>
        <%
            }
        }
    %>
    <div name="name" class="name"><%=game.getName()%></div>
    <div name="year" class="year"><%=game.getYear()%></div>
    <div name="description" class="description"><%=game.getDescription()%></div>
    <div name="web" class="web"><%=game.getWeb()%></div>
    <div name="authors" class="authors">
        <% List<Author> authors = game.getAuthors();
            Person person;
            for(Author author: authors){
                person = author.getPerson();
        %>
        <div name="author" class="author"><a href="/author/detail/<%=author.getId()%>"><%=person.getName()%></a></div>
        <%
            }
        %>
    </div>
    <div name="comments" class="comments">
        <%
        if(roleHasResource.hasRoleResource(role, "/game/commentHandler")){
        %>
        <form name="commentForm" action="/game/commentHandler.jsp" method="post">
            <div name="formField">
                <span class="label">Komentář: </span>
                <textarea id="comment_text" rows="5" cols="30" name="comment_text"></textarea>
                <span id="commentError" class="error"></span>
            </div>
            <input type="hidden" name="gameId" value="<%=game.getId()%>"/>
            <input type="submit" value="Komentovat" />
        </form>
        <%
        }
        %>
        <% List<Comment> comments = game.getComments();
            Users objUsers = new Users(conn);
            for(Comment comment : comments) {
        %>
        <div name="comment" class="comment">
            <div name="author" class="author"><%=objUsers.getById(comment.getUserId()).getPerson().getNickName()%></div>
            <div name="date" class="date"><%=comment.getDate()%></div>
            <div name="text" class="text"><%=comment.getText()%></div>
        </div>
        <%
            }
        %>
    </div>
    <div name="ratings" class="ratings">
        <%
            if(roleHasResource.hasRoleResource(role, "/game/ratingHandler")){
                Ratings rRatings = new Ratings(conn);
                Rating rating = rRatings.getRatingGameUser(game.getId(), loggedUser.getId());
                if(rating == null){
        %>
        <form name="ratingForm" action="/game/ratingHandler.jsp" method="post">
            <div name="formField">
                <span class="label">Hodnocení: </span>
                <select id="rating" name="rating">
                    <option value="1">1</option>
                    <option value="2">2</option>
                    <option value="3">3</option>
                    <option value="4">4</option>
                    <option value="5">5</option>
                    <option value="6">6</option>
                    <option value="7">7</option>
                    <option value="8">8</option>
                    <option value="9">9</option>
                    <option value="10">10</option>
                </select>
                <span id="ratingError" class="error"></span>
            </div>
            <input type="hidden" name="gameId" value="<%=game.getId()%>"/>
            <input type="submit" value="Hodnotit" />
        </form>
        <%
                }
            }
        %>
        <% List<Rating> ratings = game.getRatings();
            int iRatings = 0;
            for(Rating rating: ratings){
                  iRatings += rating.getRating();
        %>
        <div name="rating" class="rating">
            <div name="author" class="author"><%=objUsers.getById(rating.getAuthorId()).getPerson().getNickName()%></div>
            <div name="value" class="value"><%=rating.getRating()%></div>
        </div>
        <%
            }
        %>
        <div name="totalRating" class="totalRating">
            <%
                int ratingAmount = ratings.size();
                if(ratingAmount > 0) {
                    double finalRating = iRatings / ratingAmount;
                    out.println(String.valueOf(finalRating));
                } else {
                    out.println("0");
                }
            %>
        </div>
    </div>
</div>
</div>
<%@include file="/layout/footer.jsp"%>