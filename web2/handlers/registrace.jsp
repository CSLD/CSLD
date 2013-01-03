<%@ page import="com.mortennobel.imagescaling.ResampleOp" %>
<%@ page import="org.apache.tomcat.util.http.fileupload.DiskFileUpload" %>
<%@ page import="org.apache.tomcat.util.http.fileupload.FileItem" %>
<%@ page import="org.apache.tomcat.util.http.fileupload.FileUploadException" %>
<%@ page import="org.pilirion.img.DimensionConstrain" %>
<%@ page import="org.pilirion.models.user.Person" %>
<%@ page import="org.pilirion.models.user.User" %>
<%@ page import="org.pilirion.models.user.Users" %>
<%@ page import="org.pilirion.utils.DateCsld" %>
<%@ page import="org.pilirion.utils.FileUtils" %>
<%@ page import="org.pilirion.utils.Pwd" %>
<%@ page import="org.pilirion.utils.Strings" %>
<%@ page import="javax.imageio.ImageIO" %>
<%@ page import="java.awt.image.BufferedImage" %>
<%@ page import="java.io.File" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="../templates/header.jsp" %>
<%
    request.setCharacterEncoding("UTF-8");
    try {
        DiskFileUpload fu = new DiskFileUpload();
        // If file size exceeds, a FileUploadException will be thrown
        fu.setSizeMax(2000000);

        List<FileItem> fileItems = fu.parseRequest(request);
        Iterator<FileItem> itr = fileItems.iterator();

        String firstName = "", lastName = "", nickName = "", mail = "", pwd = "", pwdAgain = "",
                city = "", birth = "", description = "", imagePath = "";

        while (itr.hasNext()) {
            FileItem fi = itr.next();

            if (fi.isFormField()) {
                if (fi.getFieldName().equals("mail")) {
                    mail = Strings.stringToHTMLString(fi.getString());
                }
                if (fi.getFieldName().equals("heslo")) {
                    pwd = Strings.stringToHTMLString(fi.getString());
                }
                if (fi.getFieldName().equals("heslo_znovu")) {
                    pwdAgain = Strings.stringToHTMLString(fi.getString());
                }
                if (fi.getFieldName().equals("jmeno")) {
                    firstName = Strings.stringToHTMLString(fi.getString());
                }
                if (fi.getFieldName().equals("prijmeni")) {
                    lastName = Strings.stringToHTMLString(fi.getString());
                }
                if (fi.getFieldName().equals("nick")) {
                    nickName = Strings.stringToHTMLString(fi.getString());
                }
                if (fi.getFieldName().equals("mesto")) {
                    city = Strings.stringToHTMLString(fi.getString());
                }
                if (fi.getFieldName().equals("narozeni")) {
                    birth = Strings.stringToHTMLString(fi.getString());
                }
                if (fi.getFieldName().equals("popis")) {
                    description = Strings.stringToHTMLString(fi.getString());
                }
            }
        }

        Users users = new Users(conn);
        if(loggedUser == null){
            if (mail.equals("") || pwd.equals("") || pwdAgain.equals("") || !pwd.equals(pwdAgain) || firstName.equals("")
                    || lastName.equals("") || !Strings.isMailValid(mail)) {
                response.sendRedirect("/registrace.jsp?error=" + URLEncoder.encode("Uživatele se nepodařilo vytvořit. Nebyly zadané veškeré údaje nebo byly nekompletní.","UTF-8"));
                return;
            }
            if (!users.isNameFree(mail)) {
                response.sendRedirect("/registrace.jsp?error=" + URLEncoder.encode("Uživatele se nepodařilo vytvořit. Zadané uživatelské jméno již existuje.","UTF-8"));
                return;
            }
        }

        if(loggedUser == null || (!pwd.equals("") && pwd != null)){
            pwd = Pwd.getMD5(pwd);
        }

        itr = fileItems.iterator();
        while (itr.hasNext()) {
            FileItem fi = itr.next();

            if (!fi.isFormField()) {
                if(fi.getSize() > 0) {
                    String fileType = FileUtils.getFileType(fi.getName());
                    String filePath = Pwd.getMD5(mail);
                    ResampleOp resampleOp = new ResampleOp(new DimensionConstrain(120, 120));
                    BufferedImage imageGame = ImageIO.read(fi.getInputStream());
                    BufferedImage imageGameSized = resampleOp.filter(imageGame, null);

                    String fileTypeFull = fileType;
                    if(!fileType.equals("")){
                        fileTypeFull = "." + fileType;
                    }
                    imagePath = "/img/users/" + filePath + fileTypeFull;

                    File fNew = new File(application.getRealPath("/img/users/"), filePath + fileTypeFull);
                    ImageIO.write(imageGameSized ,fileType, fNew);
                }
            }
        }

        Date birthDate = null;
        if (birth != null && !birth.equals("")) {
            birthDate = DateCsld.parse(birth);
        }

        Person person = new Person(-1, firstName, lastName, birthDate, nickName, mail, imagePath,
                city, description);
        User userToAdd = new User(-1, person, mail, pwd, null);
        String redirectURL;
        if(loggedUser == null){
            User user;
            if (users.insertUser(userToAdd)) {
                user = users.authenticate(mail, pwd);
                session.setAttribute("csld_user", user);
                redirectURL = "/index.jsp";
            } else {
                redirectURL = "/registrace.jsp?error=" + URLEncoder.encode("Uživatele se nepodařilo vytvořit","UTF-8");
            }
        } else {
            person = new Person(loggedUser.getPerson().getId(), firstName, lastName, birthDate, nickName, mail, imagePath,
                    city, description);
            userToAdd = new User(loggedUser.getId(), person, mail, pwd, loggedUser.getRole());
            if(users.editUser(userToAdd)){
                if(pwd.equals("")){
                    pwd = loggedUser.getPassword();
                }
                if(mail.equals("")){
                    mail = loggedUser.getUserName();
                }
                userToAdd = users.authenticate(mail, pwd);
                session.setAttribute("csld_user", userToAdd);
                redirectURL = "/registrace.jsp?message=" + URLEncoder.encode("Údaje byly upraveny","UTF-8");
            } else {
                redirectURL = "/registrace.jsp?error=" + URLEncoder.encode("Údaje se nepovedlo upravit","UTF-8");
            }
        }
        response.sendRedirect(redirectURL);
    } catch (FileUploadException ex) {
        response.sendRedirect("/registrace.jsp?error=" + URLEncoder.encode("Uživatele se nepodařilo vytvořit. Příliš velká fotka.","UTF-8"));
    }
%>