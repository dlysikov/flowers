package lu.luxtrust.flowers.service;

import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.entity.system.Requestor;
import lu.luxtrust.flowers.model.ImportResult;

import java.io.InputStream;

public interface OrderBatchService {

    ImportResult<CertificateOrder> processOrdersXML(InputStream inputStream, Long issuerId);
}
