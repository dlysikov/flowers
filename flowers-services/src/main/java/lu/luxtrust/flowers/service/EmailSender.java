package lu.luxtrust.flowers.service;

public interface EmailSender<T extends EmailSender> {

    T newMessage();

    T subject(String subject);

    T to(String[] to);

    T to(String to);

    T text(String text);

    void send();
}
