package id.hardana.ejb.webpersonal.util;

import java.util.Properties;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author Trisna
 */
public class Util {

    private static Properties getInitProperties() {
        Properties result = new Properties();
        result.setProperty("java.naming.factory.initial",
                "com.sun.enterprise.naming.SerialInitContextFactory");
        result.setProperty("java.naming.factory.url.pkgs",
                "com.sun.enterprise.naming");
        result.setProperty("java.naming.factory.state",
                "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");
        result.setProperty("org.omg.CORBA.ORBInitialHost", "127.0.0.1");
        result.setProperty("org.omg.CORBA.ORBInitialPort", "3700");
        return result;
    }

    public static Object lookup(String name) throws NamingException {
        InitialContext context = new InitialContext(getInitProperties());
        Object lookupObject = context.lookup(name);
        return lookupObject;
    }
}
