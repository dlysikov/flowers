package lu.luxtrust.flowers.rule;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import org.junit.rules.ExternalResource;
import org.springframework.util.StringUtils;

import javax.mail.internet.MimeMessage;
import java.net.ServerSocket;

public class SmtpServerRule extends ExternalResource {

    static {
        initSmtpPort();
    }

    public static void initSmtpPort() {
        try {
            if (StringUtils.isEmpty(System.getProperty("test.mail.port"))) {
                ServerSocket s = new ServerSocket(0);
                System.setProperty("test.mail.port", Integer.toString(s.getLocalPort()));
                s.close();
            }
        } catch (Exception e) {
            System.setProperty("test.mail.port", Integer.toString(8356));
        }
    }

    private GreenMail smtpServer;
    private int port;

    public SmtpServerRule() {
        this.port = Integer.parseInt(System.getProperty("test.mail.port"));
    }

    public SmtpServerRule(int port) {
        this.port = port;
    }

    @Override
    protected void before() throws Throwable {
        super.before();
        smtpServer = new GreenMail(new ServerSetup(port, null, "smtp"));
        smtpServer.start();
    }

    public MimeMessage[] getMessages() {
        return smtpServer.getReceivedMessages();
    }

    @Override
    protected void after() {
        super.after();
        smtpServer.stop();
    }
}
