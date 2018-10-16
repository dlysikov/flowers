package lu.luxtrust.flowers.service.xml;

import java.util.List;

public interface ChunkXMLReader<T> extends AutoCloseable {
    List<T> readChunk(int chunkSize);
}
