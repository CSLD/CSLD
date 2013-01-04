<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="/templates/header.jsp" %>
<html>
<head>
    <title>Změna obrázku hry</title>
    <script src='/templates/js/jquery-1.8.3.min.js'></script>
</head>
<body>
<h2>Změnit obrázek</h2>
<form id='ulozitObrazek' method='post' enctype='multipart/form-data' action='/handlers/editaceObrazkuHry.jsp'>
    <input type='hidden' name='gameId' value='<%=request.getParameter("gameId")%>'/>
    <input type='file' name='obrazekHry'/>
    <input type='submit' value='Uložit Obrázek'/>
</form>
<script type='text/javascript'>
    $('#ulozitObrazek').bind("submit", function(){
        //window.close();
    });
</script>
</body>
</html>