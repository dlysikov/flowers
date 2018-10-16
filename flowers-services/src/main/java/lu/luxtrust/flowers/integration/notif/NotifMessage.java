package lu.luxtrust.flowers.integration.notif;

import java.util.Objects;

public class NotifMessage {

    public enum MessageType {
        EMAIL, SMS, EMAIL_HTML
    }

    private String content;
    private String destination;
    private String subject;
    private MessageType type;

    public NotifMessage(String content, String destination, String subject, MessageType type) {
        this.content = content;
        this.destination = destination;
        this.subject = subject;
        this.type = type;
    }

    public NotifMessage() {

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "NotifMessage{" +
                "content='" + content + '\'' +
                ", destination='" + destination + '\'' +
                ", subject='" + subject + '\'' +
                ", type=" + type +
                '}';
    }
}
