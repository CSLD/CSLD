package cz.larpovadatabaze.test;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/** Factory which create JNDI object for test pruposes
 * @author benzin
 */
public class RegisterJNDIFactory {

	public final static void propagateJNDIObject(final String subcontext, final Object bean) {
		try {
			// Create initial context
			System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
			System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");            
			InitialContext ic = new InitialContext();
			final String[] cont;
			StringBuffer buf = new StringBuffer("");
			if (subcontext.contains(":")) {
				String[] contexts = subcontext.split(":");
                ic.createSubcontext(contexts[0]);
				buf.append(contexts[0]).append(":");
				cont = contexts[1].split("/");
			} else {
                cont = subcontext.split("/");
            }
			for (int i = 0; i < cont.length; i++) {
				if (!"".equals(cont[i])) {
					buf.append(cont[i]);
					if (i == cont.length - 1) {	ic.bind(buf.toString(), bean); }		
					else {
						ic.createSubcontext(buf.toString());
						buf.append("/");
					}
				}
			}			
		} catch (NamingException ex) {
			System.err.println(String.format("Problem with create object for subcontext %s: %s",
					subcontext, ex.toString()));
		}
	}
}
