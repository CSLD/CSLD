package cz.larpovadatabaze.common.utils;

import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

/**
 * Created by IntelliJ IDEA.
 * User: Jakub Balhar
 * Date: 14.5.13
 * Time: 15:45
 */
public class HbUtils {
        public static <T>  T deproxy (T obj) {
            if (obj == null)
                return obj;
            if (obj instanceof HibernateProxy) {
                // Unwrap Proxy;
                //      -- loading, if necessary.
                HibernateProxy proxy = (HibernateProxy) obj;
                LazyInitializer li = proxy.getHibernateLazyInitializer();
                return (T)  li.getImplementation();
            }
            return obj;
        }


        public static boolean isProxy (Object obj) {
            if (obj instanceof HibernateProxy)
                return true;
            return false;
        }
}
