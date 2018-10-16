package lu.luxtrust.flowers.service.xml;

import lu.luxtrust.flowers.exception.FlowersException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;

@Component
public class ChunkXMLReaderFactory<T> {
    private final XMLInputFactory xmlInputFactory;
    private final Unmarshaller unmarshaller;

    @Autowired
    public ChunkXMLReaderFactory(XMLInputFactory xmlInputFactory, Unmarshaller unmarshaller) {
        this.xmlInputFactory = xmlInputFactory;
        this.unmarshaller = unmarshaller;
    }

    public ChunkXMLReader<T> newReader(InputStream inputStream, Class<T> targetType, String groupElement) {
        try {
            XMLStreamReader streamReader = xmlInputFactory.createXMLStreamReader(inputStream);

            streamReader.nextTag();
            streamReader.require(XMLStreamConstants.START_ELEMENT, null, groupElement);
            streamReader.nextTag();

            return new ChunkXMLReaderImpl<>(streamReader, unmarshaller, targetType);
        } catch (XMLStreamException e) {
            throw new FlowersException(e);
        }
    }
}
