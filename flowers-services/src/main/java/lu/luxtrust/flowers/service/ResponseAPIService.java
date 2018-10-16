package lu.luxtrust.flowers.service;

import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.entity.system.ResponseAPI;
import lu.luxtrust.flowers.entity.system.Requestor;
import lu.luxtrust.flowers.model.UploadedFiles;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;


public interface ResponseAPIService {

    ResponseAPI save(ResponseAPI responseAPI);
    ResponseAPI saveOrder(CertificateOrder order, Requestor requestor);
    ResponseAPI saveDocuments(String userExternalId, UploadedFiles documents, Requestor requestor) throws Exception;
    ResponseEntity sendBackResponse (ResponseAPI responseAPI);
    ResponseAPI findByUserExternalIdAndRequestor(String userExternalId, Requestor requestor);
    Slice<ResponseAPI> getResponseAPIsForSending(Pageable page);

}
