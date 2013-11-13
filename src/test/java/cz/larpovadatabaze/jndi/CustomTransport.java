package cz.larpovadatabaze.jndi;

import javax.mail.Transport;
import javax.mail.*;
import java.io.PrintStream;
import java.io.File;
import java.util.UUID;

public class CustomTransport extends Transport {

    public CustomTransport(Session smtpSession, URLName urlName) {
        super(smtpSession, urlName);
    }

    @Override
    public void sendMessage(Message message, Address[] addresses) throws MessagingException {
        try {
            File dir = new File(new File(System.getProperty("java.io.tmpdir")), "csld");
            if (!dir.exists()) { dir.mkdirs(); }
            File file = File.createTempFile("csld", ".eml", dir);
            PrintStream out = new PrintStream(file);
            message.writeTo(out);
            out.flush();
            out.close();
            System.out.println("Mail message writen to file: " + file.getAbsolutePath());
            message.writeTo(System.out);
            System.out.println(""); // Add new line after message
        } catch (java.io.IOException e) {
            System.out.println("Nepovedlo se otevrit mail.txt");
        }
    }

    @Override
    public void connect() throws MessagingException {}

    @Override
    public void connect(String host, int port, String username, String password) throws MessagingException {}

    @Override
    public void connect(String host, String username, String password) throws MessagingException {}

    @Override
    public void close() {}
}
