<%@ page import="java.security.MessageDigest" %>
<%@ page import="java.sql.Statement" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="javax.mail.*" %>
<%@ page import="javax.mail.internet.*" %>
<%@ page import="javax.activation.*" %>
<%@include file="/templates/header.jsp" %>
<%
    String mail = request.getParameter("mail");
    Persons users = new Persons(conn);
    Person user = users.getPersonByMail(mail);
    if (user != null) {
        String toGenerate = "gfdertpoitzuohjmnuorjkewsktrwols";
        int userId = user.getId();

        byte[] bytesOfMessage = toGenerate.getBytes("UTF-8");
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] theDigest = md.digest(bytesOfMessage);
        String uniqueKey = new String(theDigest);

        String sql = "insert into email_authentication values(" + String.valueOf(userId) + ",'" + uniqueKey + "')";
        Statement stmt = conn.createStatement();
        stmt.execute(sql);

        String host = "localhost";
        String to = mail;
        String from = "info@csldb.cz";
        String subject = "Zapomenuté heslo";
        String messageText = "Pokud jste o nové heslo nežádali, tento mail ignorujte. Jinak klikněte na následující odkaz:" +
                " http://www.csldb.cz/user/changeForgottenPassword/" + String.valueOf(userId)  + "/" + uniqueKey;

        boolean sessionDebug = false;
        // Create some properties and get the default Session.
        Properties props = System.getProperties();
        props.put("mail.host", host);
        props.put("mail.transport.protocol", "smtp");
        Session mailSession = Session.getDefaultInstance(props, null);

        // Set debug on the Session
        // Passing false will not echo debug info, and passing True will.

        mailSession.setDebug(sessionDebug);

        // Instantiate a new MimeMessage and fill it with the
        // required information.

        Message msg = new MimeMessage(mailSession);
        msg.setFrom(new InternetAddress(from));
        InternetAddress[] address = {new InternetAddress(to)};
        msg.setRecipients(Message.RecipientType.TO, address);
        msg.setSubject(subject);
        msg.setSentDate(new Date());
        msg.setText(messageText);

        // Hand the message to the default transport service
        // for delivery.

        Transport.send(msg);
        response.sendRedirect("/game/list");
    } else {
        response.sendRedirect("/user/forgotPass/err");
    }
%>