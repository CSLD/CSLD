<%@ page import="org.pilirion.db.DbConnection" %>
<%@ page import="org.pilirion.models.user.User" %>
<%@ page import="org.pilirion.models.user.*" %>
<%@ page import="org.pilirion.url.Url" %>
<%@ page import="java.sql.Connection" %>
<%
    Connection conn = (Connection) session.getAttribute("csld_dbConn");
    if(conn == null){
        String dbString = "jdbc:postgresql://localhost/csld";
        String userName = "csld";
        String password = "hesloKDbCsldTxt";

        conn = DbConnection.create(dbString, userName, password);
        session.setAttribute("csld_dbConn", conn);
    }

    User loggedUser = (User) session.getAttribute("csld_user");
    Role role;
    Roles roles = new Roles(conn);
    Role_Has_Resources roleHasResource = new Role_Has_Resources(conn);
    Resources resources = new Resources(conn);
    String url = request.getServletPath();
    Url uUrl = new Url(url);
    String baseUrl = uUrl.getBasePathStripped();
    System.out.println(baseUrl);
    Resource resource = resources.getResourceByPath(baseUrl);
    Settings settingsCsld;
    if(loggedUser == null){
        int guestRole = 1;
        role = roles.getById(guestRole);
        settingsCsld = new Settings(conn, -1);
    } else {
        role = loggedUser.getRole();
        settingsCsld = new Settings(conn, loggedUser.getId());
    }

    System.out.println(role);
    if(!roleHasResource.hasRoleResource(role, baseUrl)){
        System.out.println(resource);
        RequestDispatcher request_Dispatcher=request.getRequestDispatcher(resource.getDefault());
        request_Dispatcher.forward(request, response);
        return;
    }

%>