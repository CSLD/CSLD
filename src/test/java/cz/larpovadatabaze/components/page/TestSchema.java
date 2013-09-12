package cz.larpovadatabaze.components.page;

import cz.larpovadatabaze.entities.*;
import org.hibernate.cfg.Configuration;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 7.9.13
 * Time: 9:34
 */
public class TestSchema {
    public static void setTestConfig(){
        Configuration config = new Configuration().
                setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect").
                setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver").
                setProperty("hibernate.connection.url", "jdbc:hsqldb:mem:csld").
                setProperty("hibernate.connection.username", "sa").
                setProperty("hibernate.connection.password", "").
                setProperty("hibernate.connection.pool_size", "1").
                setProperty("hibernate.connection.autocommit", "true").
                setProperty("hibernate.cache.provider_class", "org.hibernate.cache.HashtableCacheProvider").
                setProperty("hibernate.hbm2ddl.auto", "create-drop").
                setProperty("hibernate.show_sql", "true").

                addClass(Comment.class).
                addClass(CommentPK.class).
                addClass(CsldGroup.class).
                addClass(CsldUser.class).
                addClass(EmailAuthentication.class).
                addClass(FbUser.class).
                addClass(Game.class).
                addClass(GroupHasMember.class).
                addClass(GroupHasMemberPK.class).
                addClass(Image.class).
                addClass(Label.class).
                addClass(Person.class).
                addClass(Photo.class).
                addClass(Rating.class).
                addClass(RatingPK.class).
                addClass(UserPlayedGame.class).
                addClass(UserPlayedGamePK.class).
                addClass(Video.class);
    }
}