package lu.luxtrust.flowers.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface DocumentService {
    ResponseEntity<Resource> downloadDocument(Long documentId);

    boolean isValidOrderDocumentFormat(MultipartFile file) throws IOException;
}
