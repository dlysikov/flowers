package lu.luxtrust.flowers.service.util;

import lu.luxtrust.flowers.integration.lrs.ws.LrsWSOrder;
import lu.luxtrust.flowers.integration.lrs.ws.LrsWSRegistrationAuthority;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class SignatureTextGenerator {
    public static class DateConverter {
        private String format;

        public DateConverter(String format) {
            this.format = format;
        }

        public String format(java.util.Date cal) {
            return new SimpleDateFormat(format).format(cal);
        }

        public String format(XMLGregorianCalendar cal) {
            return this.format(cal.toGregorianCalendar().getTime());
        }
    }

    public static class LabelFormat {

        public String print(String label, String value) {
            StringBuilder text = new StringBuilder();
            if (value != null) {
                if (label != null && label.length() > 0) {
                    text.append(label);
                    text.append(": ");
                }
                text.append(value);
                text.append("\n\r");
            }
            return text.toString();
        }

        public String printIf(boolean cond, String value) {
            if (cond) return value;
            else return "";
        }

        public String printIfNot(boolean cond, String value) {
            return printIf(!cond, value);
        }

        public String crlf() {
            return "\n\r";
        }
    }

    private String generateContent(Map<String, Object> data, String template) {
        VelocityEngine engine = new VelocityEngine();
        engine.init();
        VelocityContext context = new VelocityContext();
        for (String name : data.keySet()) {
            context.put(name, data.get(name));
        }
        context.put("simpleDateFormat", new DateConverter("dd/MM/yyyy"));
        context.put("label", new LabelFormat());
        StringWriter writer = new StringWriter();
        engine.evaluate(context, writer, "sign-content", template);
        return writer.toString();
    }

    public String generateRaoRegisterText(LrsWSRegistrationAuthority ra, LrsWSOrder order) {
        String convertedDeviceType = null;
        switch (order.getProduct().getSigningServer().getDeviceType()) {
            case DPGO_6:
                convertedDeviceType = "DPGO6";
                break;
            case DPM:
                convertedDeviceType = "DPM";
                break;
            case DP_780:
                convertedDeviceType = "DP780";
                break;
        }

        String template = "I undersigned $!ra.OperatorID / $!ra.NetworkID hereby validate the order for the LuxTrust " +
                "product Signing Server $!order.Product.Profile and the correctness of the client's personal information." +
                "$label.crlf()Name: $!order.Product.SigningServer.Subject.SurName$label.crlf()Firstname: " +
                "$!order.Product.SigningServer.Subject.GivenName$label.crlf()Nationality: $!order.Product.SigningServer.Subject." +
                "Nationality$label.crlf()Birthdate: $simpleDateFormat.format($!order.Product.SigningServer.Subject.BirthDate)" +
                "$label.crlf()Subject Serial Number: $!order.Product.SubjectSerialNumber$label.crlf()$label.print(\"Organisation name\"," +
                "$!order.Product.SigningServer.Subject.Organisation.Name)$label.print(\"Departement\",$!order.Product.SigningServer." +
                "Subject.Organisation.Department)$label.print(\"Country\",$!order.Product.SigningServer.Subject.Organisation.Country)" +
                "$label.print(\"VAT\",$!order.Product.SigningServer.Subject.Organisation.Tva)$label.print(\"RCSL\",$!order.Product." +
                "SigningServer.Subject.Organisation.Rcsl)$label.print(null,$!order.Product.SigningServer.Subject.Organisation.Other)" +
                "$label.print(\"Mobile phone number for delivery of LT codes\",$!order.CodeChannel.Mobile)Delivery address:$label." +
                "crlf()$label.print(null,$!order.CodeChannel.Mail.Organisation)$label.print(null,$!order.CodeChannel.Mail.Street2)$" +
                "!order.CodeChannel.Mail.StreetNO, $!order.CodeChannel.Mail.Street1$label.crlf()$!order.CodeChannel.Mail.PostalCode $" +
                "!order.CodeChannel.Mail.Locality$label.crlf()$!order.CodeChannel.Mail.Country$label.crlf()" +
                "Type of OTP Generator: $!convertedDeviceType$label.crlf()" +
                "Number of OTP Generator: $label.print(null,$!order.Product.SigningServer.DeviceSerialNumber)#if (\"$!order.Product.SigningServer.DeviceSerialNumber\"==\"\")$label.crlf()#{end}General " +
                "Terms and Conditions signed: $label.printIf($!order.isSignedGTC(),\"yes\")$label.printIfNot($!order.isSignedGTC(),\"no\")$label.crlf()" +
                "Publication in LuxTrust directory: $label.printIf($!order.Product.isPublish(),\"yes\")$label.printIfNot($!order.Product.isPublish(),\"no\")";

        Map<String, Object> data = new HashMap<>();
        data.put("order", order);
        data.put("ra", ra);
        data.put("convertedDeviceType", convertedDeviceType);
        return generateContent(data, template);
    }

}
