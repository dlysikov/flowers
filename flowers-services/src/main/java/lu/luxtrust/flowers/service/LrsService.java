package lu.luxtrust.flowers.service;

import lu.luxtrust.flowers.entity.enrollment.ESealOrder;
import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;

public interface LrsService {

    String register(CertificateOrder order) throws Exception;

    String register(ESealOrder order) throws Exception;

    void pollStatus(LrsStatusHandler handler);

    void enrichProductInfo(LrsProductHandler handler);

}
