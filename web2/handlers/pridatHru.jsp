<%@ page import="org.apache.tomcat.util.http.fileupload.DiskFileUpload" %>
<%@ page import="org.apache.tomcat.util.http.fileupload.FileItem" %>
<%@ page import="org.apache.tomcat.util.http.fileupload.FileUploadException" %>
<%@ page import="org.pilirion.models.game.Authors" %>
<%@ page import="org.pilirion.models.game.Author" %>
<%@ page import="org.pilirion.models.game.Games" %>
<%@ page import="org.pilirion.models.game.Game" %>
<%@ page import="org.pilirion.models.game.Label" %>
<%@ page import="org.pilirion.models.game.Labels" %>
<%@ page import="org.pilirion.utils.FileUtils" %>
<%@ page import="org.pilirion.utils.Pwd" %>
<%@ page import="org.pilirion.utils.Strings" %>
<%@ page import="java.io.File" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.List" %>
<%@ page import="org.pilirion.exceptions.ItemDoesNotExists" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="../templates/header.jsp" %>
<%
    try {
        request.setCharacterEncoding("UTF-8");
        String gameName = "", gameImage = "", playersAmount = "0", men = "0", women = "0",
                hours = "0", days = "0", gameDescription = "", year = "", amountBoth = "";
        List<String> labelsList = new ArrayList<String>();
        DiskFileUpload fu = new DiskFileUpload();
        // If file size exceeds, a FileUploadException will be thrown
        fu.setSizeMax(2000000);

        List fileItems = fu.parseRequest(request);
        Iterator itr = fileItems.iterator();
        List<String> authorsNames = new ArrayList<String>();

        Games games = new Games(conn);
        while (itr.hasNext()) {
            FileItem fi = (FileItem) itr.next();

            if (fi.isFormField()) {
                if (fi.getFieldName().equals("nazev")) {
                    gameName = Strings.stringToHTMLString(fi.getString());
                } else if (fi.getFieldName().startsWith("autor")) {
                    if(!fi.getString().equals("")){
                        authorsNames.add(Strings.stringToHTMLString(fi.getString()));
                    }
                } else if (fi.getFieldName().equals("pocet_hracu")) {
                    playersAmount = Strings.stringToHTMLString(fi.getString());
                } else if (fi.getFieldName().equals("pocet_muzu")) {
                    men = Strings.stringToHTMLString(fi.getString());
                } else if (fi.getFieldName().equals("pocet_zen")) {
                    women = Strings.stringToHTMLString(fi.getString());
                } else if (fi.getFieldName().equals("hodiny")) {
                    hours = Strings.stringToHTMLString(fi.getString());
                } else if (fi.getFieldName().equals("dny")) {
                    days = Strings.stringToHTMLString(fi.getString());
                } else if (fi.getFieldName().equals("popis")) {
                    gameDescription = Strings.stringToHTMLString(fi.getString());
                } else if (fi.getFieldName().equals("rok_vytvoreni")) {
                    year = Strings.stringToHTMLString(fi.getString());
                } else if (fi.getFieldName().equals("pocet_obojetne")) {
                    amountBoth = Strings.stringToHTMLString(fi.getString());
                } else if (fi.getFieldName().equals("jmeno_autora")) {
                } else if (fi.getFieldName().equals("prijmeni_autora")) {
                } else if (fi.getFieldName().equals("prezdivka_autora")) {
                } else if (fi.getFieldName().equals("nazev_stitku")) {
                } else if (fi.getFieldName().equals("popis_stitku")) {
                } else if (fi.getFieldName().equals("pridat_hru")) {
                } else if (fi.getFieldName().equals("skupina_autora")) {
                } else {
                    labelsList.add(fi.getFieldName());
                }
            }
        }
        try{
            games.getByName(gameName);
            response.sendRedirect("/pridatHru.jsp?error=" +
                    URLEncoder.encode("Hru se nepovedlo přidat. Hra se stejným jménem již existuje.", "UTF-8"));
            return;
        } catch(ItemDoesNotExists ex) {
        }

        // Test if the name exists.
        Labels labels = new Labels(conn);
        List<Label> labelListLabel = labels.getLabelsByNames(labelsList);
        boolean containsRequired = false;
        for (Label label : labelListLabel) {
            if (label.isRequires()) {
                containsRequired = true;
            }
        }
        if (gameName.equals("") || authorsNames.size() == 0 || !containsRequired) {
            response.sendRedirect("/pridatHru.jsp?error=" +
                    URLEncoder.encode("Hru se nepovedlo přidat. Nebyly zadané všechny potřebné údaje.", "UTF-8"));
            return;
        }

        itr = fileItems.iterator();
        while (itr.hasNext()) {
            FileItem fi = (FileItem) itr.next();

            System.out.println("FileName " + fi.getName());
            if (!fi.isFormField() && !fi.getName().equals("")) {
                String fileType = FileUtils.getFileType(fi.getName());
                String filePath = Pwd.getMD5(gameName);
                System.out.println("FilePath " + filePath);
                System.out.println("FileType " + fileType);
                gameImage = "/img/games/" + filePath + fileType;
                File fNew = new File(application.getRealPath("/img/games/"), filePath + fileType);
                System.out.println("Final Path " + fNew.getAbsolutePath());
                try {
                    System.out.println("Fi " + fi);
                    fi.write(fNew);
                } catch (Exception e) {
                    e.printStackTrace();
                    response.sendRedirect("/pridatHru.jsp?error=" +
                            URLEncoder.encode("Hru se nepovedlo přidat. Chyba při nahrávání souboru", "UTF-8"));
                    return;
                }
            }
        }

        Authors authors = new Authors(conn);
        if (!gameName.equals("") && authorsNames.size() > 0 && labelsList.size() > 0) {
            List<Author> authorsList = authors.getAuthorsByNames(authorsNames);

            int menInt, womenInt, hoursInt, daysInt, players, yearInt, amountBothInt;
            try {
                menInt = Integer.parseInt(men);
            } catch (NumberFormatException ex) {
                menInt = 0;
            }
            try {
                womenInt = Integer.parseInt(women);
            } catch (NumberFormatException ex) {
                womenInt = 0;
            }
            try {
                hoursInt = Integer.parseInt(hours);
            } catch (NumberFormatException ex) {
                hoursInt = 0;
            }
            try {
                daysInt = Integer.parseInt(days);
            } catch (NumberFormatException ex) {
                daysInt = 0;
            }
            try {
                players = Integer.parseInt(playersAmount);
            } catch (NumberFormatException ex) {
                players = 0;
            }
            try {
                yearInt = Integer.parseInt(year);
            } catch (NumberFormatException ex) {
                yearInt = Calendar.getInstance().get(Calendar.YEAR);
            }
            try {
                amountBothInt = Integer.parseInt(amountBoth);
            } catch (NumberFormatException ex) {
                amountBothInt = 0;
            }

            Game game = new Game(-1, gameName, gameImage, menInt, womenInt, amountBothInt,
                    hoursInt, daysInt, yearInt, gameDescription,
                    null, players, authorsList, null, null, labelListLabel);
            game.setUserWhoAddedGame(loggedUser.getId());
            if (games.insertGame(game)) {
                response.sendRedirect("/pridatHru.jsp?message=" + URLEncoder.encode("Hra byla úspěšně přidána.", "UTF-8"));
                return;
            }
        }

        response.sendRedirect("/pridatHru.jsp?error=" + URLEncoder.encode("Hru se nepovedlo přidat. Nebyly zadané všechny potřebné údaje.", "UTF-8"));
    } catch (FileUploadException ex) {
        ex.printStackTrace();
        response.sendRedirect("/pridatHru.jsp?error=" + URLEncoder.encode("Hru se nepovedlo přidat. Příliš velký obrázek.", "UTF-8"));
    }
%>