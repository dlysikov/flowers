package lu.luxtrust.flowers.service.xml;

import lu.luxtrust.flowers.exception.FlowersException;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ChunkXMLReaderImpl<T> implements ChunkXMLReader<T> {

    private final XMLStreamReader reader;
    private final Unmarshaller unmarshaller;
    private final Class<T> targetType;

    ChunkXMLReaderImpl(XMLStreamReader reader, Unmarshaller unmarshaller, Class<T> targetType) {
        this.reader = reader;
        this.targetType = targetType;
        this.unmarshaller = unmarshaller;
    }

    @Override
    public List<T> readChunk(int chunkSize) {
        try {
            List<T> chunk = new ArrayList<>(chunkSize);

            while (chunkSize-- > 0 && reader.getEventType() == XMLStreamConstants.START_ELEMENT) {
                JAXBElement<T> pt = unmarshaller.unmarshal(reader, targetType);

                chunk.add(pt.getValue());

                if (reader.getEventType() == XMLStreamConstants.CHARACTERS) {
                    reader.next();
                }
            }

            return chunk;
        } catch (XMLStreamException | JAXBException e) {
            throw new FlowersException(e);
        }
    }

    @Override
    public void close() throws Exception {
        if (reader != null) {
            this.reader.close();
        }
    }
}
