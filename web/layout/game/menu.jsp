<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script type="text/javascript">
    document.observe('dom:loaded', function updateArrows(){
        var amountOfAllGames = <%=request.getParameter("amountOfAllGames")%>;
        var actualPage = <%=request.getParameter("actualPage")%>;
        var gamesPerPage = <%=request.getParameter("gamesPerPage")%>;
        var baseUrl = "<%=request.getParameter("baseUrl")%>";
        var maxPage = Math.ceil(amountOfAllGames / gamesPerPage);
        function navigate(actualPage){
            window.location = baseUrl + "/" + actualPage;
        }

        $('previousArrow').observe('click', function previousArrowWasClicked(){
            if(actualPage > 1){
                actualPage--;
                navigate(actualPage);
            }
        });
        $('nextArrow').observe('click', function nextArrowWasClicked(){
            if(actualPage < maxPage){
                actualPage++;
                navigate(actualPage);
            }
        });
    });
</script>
<div id="gameMenu">
    <div id="menuItems">
        <a href="/game/list">Od nejnovější</a>
        <a href="/game/ratings">Od nejlepší</a>
        <a href="/game/comments">Od nejkomentovanější</a>
        <a href="/user/list">Nejlepší uživatelé</a>
        <a href="/search">Vyhledávání</a>
    </div>
    <div id="moveArrows">
        <div id="previousArrow">&lt;</div>
        <div id="nextArrow">&gt;</div>
    </div>
</div>
