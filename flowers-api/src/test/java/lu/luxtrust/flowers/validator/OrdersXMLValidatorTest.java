package lu.luxtrust.flowers.validator;

import lu.luxtrust.flowers.error.GlobalErrorCodes;
import lu.luxtrust.flowers.model.UploadedFile;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import static org.mockito.Mockito.*;

import javax.xml.transform.Source;
import javax.xml.validation.Validator;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class OrdersXMLValidatorTest {

    private static final String MESSAGE = "message";

    @Mock
    private Validator ordersXmlValidator;
    @Mock
    private Errors errors;
    @Mock
    private UploadedFile uploadedFile;
    @Mock
    private MultipartFile multipartFile;

    private OrdersXMLValidator validator;

    @Before
    public void init() {
        this.validator = new OrdersXMLValidator(ordersXmlValidator);
        when(uploadedFile.getFile()).thenReturn(multipartFile);
    }

    @Test
    public void invalidXML() throws Exception {
        doThrow(new SAXException(MESSAGE)).when(ordersXmlValidator).validate(any(Source.class));
        validator.validate(uploadedFile, errors);

        ArgumentCaptor<String> errorCodeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> defaultMessageCaptor = ArgumentCaptor.forClass(String.class);

        verify(errors).reject(errorCodeCaptor.capture(), defaultMessageCaptor.capture());

        assertThat(errorCodeCaptor.getValue()).isEqualTo(GlobalErrorCodes.INVALID_XML_FILE);
        assertThat(defaultMessageCaptor.getValue()).isEqualTo(MESSAGE);
    }

    @Test
    public void supports() {
        assertThat(validator.supports(UploadedFile.class)).isTrue();
    }

}