<%@ page import="com.mortennobel.imagescaling.ResampleOp" %>
<%@ page import="org.apache.tomcat.util.http.fileupload.DiskFileUpload" %>
<%@ page import="org.apache.tomcat.util.http.fileupload.FileItem" %>
<%@ page import="org.pilirion.img.DimensionConstrain" %>
<%@ page import="org.pilirion.models.game.Game" %>
<%@ page import="org.pilirion.models.game.Games" %>
<%@ page import="org.pilirion.utils.FileUtils" %>
<%@ page import="org.pilirion.utils.Pwd" %>
<%@ page import="javax.imageio.ImageIO" %>
<%@ page import="java.awt.image.BufferedImage" %>
<%@ page import="java.io.File" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="../templates/header.jsp" %>
<%
    request.setCharacterEncoding("UTF-8");

    DiskFileUpload fu = new DiskFileUpload();
    // If file size exceeds, a FileUploadException will be thrown
    fu.setSizeMax(2000000);
    int gameId = -1;

    List<FileItem> fileItems = fu.parseRequest(request);
    Iterator<FileItem> itr = fileItems.iterator();
    while (itr.hasNext()) {
        FileItem fi = itr.next();

        if (fi.isFormField()) {
            if (fi.getFieldName().equals("gameId")) {
                gameId = Integer.parseInt(fi.getString());
            }
        }
    }
    Games games = new Games(conn);
    Game game = games.getById(gameId);
    String gameName = game.getName(), gameImage= "";

    itr = fileItems.iterator();
    while (itr.hasNext()) {
        FileItem fi = itr.next();

        if (!fi.isFormField()) {
            if(fi.getSize() > 0) {
                System.out.println(gameName);
                String fileType = FileUtils.getFileType(fi.getName());
                String filePath = Pwd.getMD5(gameName);
                ResampleOp resampleOp = new ResampleOp(new DimensionConstrain(120, 120));
                BufferedImage imageGame = ImageIO.read(fi.getInputStream());
                BufferedImage imageGameSized = resampleOp.filter(imageGame, null);

                String fileTypeFull = fileType;
                if(!fileType.equals("")){
                    fileTypeFull = "." + fileType;
                }
                gameImage = "/img/games/" + filePath + fileTypeFull;

                File fNew = new File(application.getRealPath("/img/games/"), filePath + fileTypeFull);
                ImageIO.write(imageGameSized ,fileType, fNew);
            }
        }
    }

    System.out.println(gameImage);

    game.setImage(gameImage);
    games.editGame(game);
%>