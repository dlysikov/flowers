package lu.luxtrust.flowers.validator;

import lu.luxtrust.flowers.error.GlobalErrorCodes;
import lu.luxtrust.flowers.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Validator;
import java.io.IOException;

@Component
public class OrdersXMLValidator implements org.springframework.validation.Validator {

    private final Validator ordersXmlValidator;

    @Autowired
    public OrdersXMLValidator(@Qualifier("ordersValidator") Validator ordersXmlValidator) {
        this.ordersXmlValidator = ordersXmlValidator;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return UploadedFile.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        try {
            UploadedFile file = (UploadedFile) target;
            this.ordersXmlValidator.validate(new StreamSource(file.getFile().getInputStream()));
        } catch(SAXException | IOException e ) {
            errors.reject(GlobalErrorCodes.INVALID_XML_FILE, e.getMessage());
        }
    }
}
