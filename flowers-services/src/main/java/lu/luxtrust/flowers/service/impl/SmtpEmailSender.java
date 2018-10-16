package lu.luxtrust.flowers.service.impl;

import lu.luxtrust.flowers.exception.FlowersException;
import lu.luxtrust.flowers.service.EmailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;

@Scope("prototype")
@Component("smtpEmailSender")
public class SmtpEmailSender implements EmailSender<SmtpEmailSender> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmtpEmailSender.class);

    public static final boolean HTML = Boolean.TRUE;
    public static final String EMAIL_ENCODING = "UTF-8";

    private MimeMessageHelper message;
    private JavaMailSender sender;
    private List<MimeMessage> mimeMessages;

    @Autowired
    public SmtpEmailSender(JavaMailSender sender) {
        this.sender = sender;
        this.mimeMessages = new ArrayList<>();
    }

    @Override
    public SmtpEmailSender subject(String subject) {
        try {
            this.message.setSubject(subject);
            return this;
        } catch (MessagingException e) {
            LOGGER.error(e.getMessage(), e);
            throw new FlowersException(e, e.getMessage());
        }
    }

    @Override
    public SmtpEmailSender to(String[] to) {
        try {
            this.message.setTo(to);
            return this;
        } catch (MessagingException e) {
            LOGGER.error(e.getMessage(), e);
            throw new FlowersException(e, e.getMessage());
        }
    }

    @Override
    public SmtpEmailSender to(String to) {
        return to(new String[]{to});
    }

    @Override
    public SmtpEmailSender text(String text) {
        try {
            this.message.setText(text, HTML);
            return this;
        } catch (MessagingException e) {
            LOGGER.error(e.getMessage(), e);
            throw new FlowersException(e, e.getMessage());
        }
    }


    @Override
    public SmtpEmailSender newMessage() {
        MimeMessage mimeMessage = sender.createMimeMessage();
        this.message = new MimeMessageHelper(mimeMessage, EMAIL_ENCODING);
        this.mimeMessages.add(mimeMessage);
        return this;
    }

    @Override
    public void send() {
        this.sender.send(mimeMessages.toArray(new MimeMessage[mimeMessages.size()]));
        this.mimeMessages.clear();
    }
}
