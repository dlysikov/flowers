package lu.luxtrust.flowers.service.xml;

import lu.luxtrust.flowers.exception.FlowersException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ChunkXMLReaderFactoryTest {

    @Mock
    private XMLInputFactory xmlInputFactory;
    @Mock
    private Unmarshaller unmarshaller;
    @Mock
    private XMLStreamReader xmlStreamReader;
    @Mock
    private InputStream inputStream;

    private ChunkXMLReaderFactory target;

    @Before
    public void init() throws Exception {
        this.target = new ChunkXMLReaderFactory(xmlInputFactory, unmarshaller);
        when(xmlInputFactory.createXMLStreamReader(any(InputStream.class))).thenReturn(xmlStreamReader);
    }

    @Test(expected = FlowersException.class)
    public void expectException() throws Exception {
       when(xmlInputFactory.createXMLStreamReader(any(InputStream.class))).thenThrow(new XMLStreamException());
       target.newReader(inputStream, String.class, "gropu");
    }

    @Test
    public void newReader() throws XMLStreamException {
        ChunkXMLReader reader = target.newReader(inputStream, String.class, "gropu");
        verify(xmlStreamReader, times(2)).nextTag();
        verify(xmlStreamReader).require(XMLStreamConstants.START_ELEMENT, null, "gropu");

        assertThat(reader).isNotNull();
    }


}