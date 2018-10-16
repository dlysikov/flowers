package lu.luxtrust.flowers.service.xml;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ChunkXMLReaderImplTest {

    public static final String CONTENT = "AAAAA";
    @Mock
    private XMLStreamReader reader;
    @Mock
    private Unmarshaller unmarshaller;
    @Mock
    private JAXBElement jaxbElement;

    private List<String> result;

    private ChunkXMLReaderImpl<String> target;

    @Before
    public void init() {
        this.result = new ArrayList<>();
        this.result.add(CONTENT);
        this.target = new ChunkXMLReaderImpl<>(reader, unmarshaller, String.class);
    }

    @Test
    public void readChunk() throws JAXBException {
        when(reader.getEventType()).thenReturn(XMLStreamConstants.START_ELEMENT).thenReturn(XMLStreamConstants.CHARACTERS);
        when(unmarshaller.unmarshal(reader, String.class)).thenReturn(jaxbElement);
        when(jaxbElement.getValue()).thenReturn(CONTENT);

        assertThat(target.readChunk(2)).isEqualTo(result);
        verify(unmarshaller).unmarshal(reader, String.class);
        verify(reader, times(3)).getEventType();
    }


}