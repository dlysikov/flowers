package lu.luxtrust.flowers.service.impl;

import lu.luxtrust.flowers.entity.enrollment.Document;
import lu.luxtrust.flowers.enums.Markers;
import lu.luxtrust.flowers.repository.DocumentRepository;
import lu.luxtrust.flowers.service.DocumentService;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentServiceImpl implements DocumentService {

    private static final Logger LOG = LoggerFactory.getLogger(DocumentServiceImpl.class);
    private static Marker auditMarker = MarkerFactory.getMarker(Markers.AUDIT.getName());

    private final DocumentRepository documentRepository;
    private final List<String> orderValidDocumentsFormat;
    private final Tika tika;

    @Autowired
    public DocumentServiceImpl(DocumentRepository documentRepository,
                               Tika tika,
                               @Value("${order.documents.format.mime-type}") String[] orderValidDocumentsFormat) {
        this.documentRepository = documentRepository;
        this.tika = tika;
        this.orderValidDocumentsFormat = Arrays.stream(orderValidDocumentsFormat).map(String::toLowerCase).collect(Collectors.toList());
    }

    @Override
    public ResponseEntity<Resource> downloadDocument(Long documentId) {
        LOG.info(auditMarker, "Retrieving document with id {}", documentId);
        Document document = this.documentRepository.findOne(documentId);
        if (document == null) {
            LOG.warn(auditMarker, "Document with id {} is not found", documentId);
            return ResponseEntity.notFound().build();
        }
        InputStream inputStream = new ByteArrayInputStream(document.getFile(), 0, document.getFile().length);

        return ResponseEntity.ok()
                .header("Content-disposition", "attachment;filename=" + document.getName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(inputStream));
    }

    @Override
    public boolean isValidOrderDocumentFormat(MultipartFile file) throws IOException {
        String mediaType = tika.detect(file.getInputStream());
        boolean valid = this.orderValidDocumentsFormat.contains(mediaType);
        if (!valid) {
            LOG.warn(auditMarker, "File [{}] has wrong type [{}] and it will not be proceeded.", file.getOriginalFilename(), mediaType);
        }
        return valid;
    }
}
