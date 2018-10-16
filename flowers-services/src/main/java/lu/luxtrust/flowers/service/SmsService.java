package lu.luxtrust.flowers.service;

import lu.luxtrust.flowers.model.SMS;

import java.util.List;

public interface SmsService {
    SMS send(SMS sms);
    List<SMS> send(List<SMS> sms);
}
