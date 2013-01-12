<%@ page import="org.pilirion.db.DbConnection" %>
<%@ page import="org.pilirion.models.user.User" %>
<%@ page import="org.pilirion.models.user.*" %>
<%@ page import="org.pilirion.url.Url" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="org.pilirion.exceptions.WrongCookie" %>
<%
    Connection conn = null;
    if(conn == null){
        String dbString = "jdbc:postgresql://localhost/csld_test";
        String userName = "csld";
        String password = "hesloKDbCsldTxt";

        conn = DbConnection.create(dbString, userName, password);
    }

    User loggedUser = (User) session.getAttribute("csld_user");
    if(loggedUser == null){
        Login login = new Login(conn);
        Cookie[] allCookies = request.getCookies();
        if(allCookies != null){
            for(Cookie cookie: allCookies){
                if(cookie.getName().equals("csldUserLoginCookie")) {
                    try{
                        User user = login.getUserByCookie(cookie.getValue());
                        if(user != null){
                            loggedUser = user;
                            session.setAttribute("csld_user", user);
                        }
                    } catch (WrongCookie ex){}
                }
            }
        }
    }


    Role role;
    Roles roles = new Roles(conn);
    Role_Has_Resources roleHasResource = new Role_Has_Resources(conn);
    Resources resources = new Resources(conn);
    String url = request.getServletPath();
    Url uUrl = new Url(url);
    String baseUrl = uUrl.getBasePathStripped();
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

    if(!roleHasResource.hasRoleResource(role, baseUrl)){
        if(resource == null){
            RequestDispatcher request_Dispatcher=request.getRequestDispatcher("/");
            request_Dispatcher.forward(request, response);
            return;
        } else {
            RequestDispatcher request_Dispatcher=request.getRequestDispatcher(resource.getDefault());
            request_Dispatcher.forward(request, response);
            return;
        }
    }

%>
