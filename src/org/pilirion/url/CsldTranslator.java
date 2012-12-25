package org.pilirion.url;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 21.6.12
 * Time: 12:26
 */
public class CsldTranslator extends Translator {
    static Translator translator = null;

    CsldTranslator(){

    }

    public static Translator getTranslator(){
         if(translator == null){
             CsldTranslator trans = new CsldTranslator();
             trans.fillRoutes();
             translator = trans;
         }
        return translator;
    }

    void fillRoutes(){
        Route userDetail = new Route("/uzivatel/:id","/uzivatel.jsp");
        addRoute(userDetail);

        Route userLogin = new Route("/handlers/prihlaseni","/handlers/prihlaseni.jsp");
        addRoute(userLogin);

        Route userRegister = new Route("/registrace", "/registrace.jsp");
        addRoute(userRegister);

        Route userList = new Route("/uzivatele", "/uzivatele.jsp");
        addRoute(userList);

        Route authorsList = new Route("/autori", "/autori.jsp");
        addRoute(authorsList);

        Route gameList = new Route("/zebricky", "/zebricky.jsp");
        addRoute(gameList);

        Route gameDetail = new Route("/hra/:id", "/hra.jsp");
        addRoute(gameDetail);

        Route gameInsert = new Route("/pridatHru", "/pridatHru.jsp");
        addRoute(gameInsert);

        Route search = new Route("/hledani","/hledani.jsp");
        addRoute(search);

        Route about = new Route("/oDatabazi","/oDatabazi.jsp");
        addRoute(about);


        Route gameListIndex = new Route("/", "/index.jsp");
        addRoute(gameListIndex);
    }
}
