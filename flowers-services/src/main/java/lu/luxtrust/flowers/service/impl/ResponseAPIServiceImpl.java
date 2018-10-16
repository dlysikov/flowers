package lu.luxtrust.flowers.service.impl;

import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.entity.enrollment.Unit;
import lu.luxtrust.flowers.entity.system.ResponseAPI;
import lu.luxtrust.flowers.entity.system.Requestor;
import lu.luxtrust.flowers.enums.Markers;
import lu.luxtrust.flowers.enums.StatusAPI;
import lu.luxtrust.flowers.exception.FlowersException;
import lu.luxtrust.flowers.model.UploadedFiles;
import lu.luxtrust.flowers.repository.*;
import lu.luxtrust.flowers.service.CertificateOrderService;
import lu.luxtrust.flowers.service.ResponseAPIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ResponseAPIServiceImpl implements ResponseAPIService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CertificateOrderServiceImpl.class);
    private static Marker auditMarker = MarkerFactory.getMarker(Markers.AUDIT.getName());

    @Autowired
    private ResponseAPIRepository responseAPIRepository;
    @Autowired
    private RequestorRepository requestorRepository;
    @Autowired
    private CertificateOrderRepository orderRepository;
    @Autowired
    private CertificateOrderService certificateOrderService;
    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    @Qualifier("restTemplateFactory")
    private RestTemplate restTemplate;

    public ResponseAPI save(ResponseAPI responseAPI) {
        responseAPI.setCurrentDate();
        responseAPIRepository.save(responseAPI);
        return responseAPI;
    }

    @Override
    public ResponseAPI findByUserExternalIdAndRequestor(String userExternalId, Requestor requestor) {
        return responseAPIRepository.findByUserExternalIdAndRequestor(userExternalId, requestor.getId());
    }

    @Override
    public ResponseEntity sendBackResponse(ResponseAPI responseAPI) {
        LOGGER.info("Preparing for sending back response for CertificateOrder with user external id [{}]", responseAPI.getUserExternalId());
        ResponseEntity response = null;
        HttpEntity<ResponseAPI> responseAPIHttpEntity = new HttpEntity<>(responseAPI);

        Requestor requestor = requestorRepository.findRequestorByCsn(responseAPI.getServiceSSN());
        if (requestor != null) {
            LOGGER.info("Sending back response for URL {} for user external id [{}]", requestor.getConfig().getResponseURL(), responseAPI.getUserExternalId());
            try {
                responseAPI.setStatus(StatusAPI.ACTIVATED);
                response = restTemplate.postForEntity(requestor.getConfig().getResponseURL(), responseAPIHttpEntity, String.class);
                LOGGER.info("The response from server has status code [{}] for user external id [{}]", response.getStatusCode(), responseAPI.getUserExternalId());

                if (!response.getStatusCode().is2xxSuccessful()) {
                    responseAPI.setStatus(StatusAPI.RESPONSE_SERVER_ERROR);
                }
            } catch (Exception ex) {
                responseAPI.setStatus(StatusAPI.RESPONSE_SENDING_ERROR);

            }
            save(responseAPI);
        } else {
            LOGGER.warn("No Service Instance was found for Service SSN [{}] and user external id [{}]", responseAPI.getServiceSSN(), responseAPI.getUserExternalId());
        }
        return response;
    }

    @Override
    public Slice<ResponseAPI> getResponseAPIsForSending(Pageable page) {
        return responseAPIRepository.getResponseAPIsForSending(page);
    }

    @Override
    public ResponseAPI saveOrder(CertificateOrder order, Requestor requestor) {
        ResponseAPI responseAPI = new ResponseAPI();
        responseAPI.setUserExternalId(order.getUserExternalId());
        responseAPI.setServiceSSN(requestor.getCsn());
        responseAPI.setRequestor(requestor);
        Unit unit;
        List<CertificateOrder> existedOrderList = certificateOrderService.findDuplicates(Collections.singleton(order));
        if (existedOrderList.size() > 0 || orderRepository.existsByUserExternalIdAndUnitRequestor(order.getUserExternalId(), requestor)) {
            responseAPI.setStatus(StatusAPI.DUPLICATE);
            LOGGER.warn("CertificateOrder with User External Id {} [First name [{}], Surname [{}], Notify Email [{}]] already exists",
                    order.getUserExternalId(), order.getHolder().getFirstName(), order.getHolder().getSurName(), order.getHolder().getNotifyEmail());
        } else {
            try {
                unit = unitRepository.findByIdentifier(order.getUnit().getIdentifier());
                if(unit == null) {
                    order.getUnit().setRequestor(requestor);
                    unit = unitRepository.save(order.getUnit());
                    LOGGER.info("New Unit[type = [{}], value = [{}]] was saved during CertificateOrder[User External Id [{}]] saving", order.getUnit().getIdentifier().getType(), order.getUnit().getIdentifier().getValue(), order.getUserExternalId());
                } else {
                    if(!unit.getRequestor().equals(requestor)) {
                        LOGGER.warn("The requestor of Unit[{}] is not similar to requestor of CertificateOrder[{}]", unit.getRequestor(), requestor);
                        throw new FlowersException("Requestors of CertificateOrder does not match to requestor of Unit");
                    }
                }
                order.setUnit(unit);
                if (order.getHolder().getWaitDocuments()) {
                    certificateOrderService.saveOrder(order);
                    responseAPI.setStatus(StatusAPI.WAITING_FOR_DOCUMENTS);
                } else {
                    certificateOrderService.notifyOrderHolder(certificateOrderService.saveOrder(order));
                    responseAPI.setStatus(StatusAPI.ORDER_SAVED);
                }
                save(responseAPI);
                LOGGER.info("CertificateOrder with User External Id {} was successfully saved in status {}", order.getUserExternalId(), order.getStatus());
            } catch (Exception ex) {
                responseAPI.setStatus(StatusAPI.ORDER_SAVING_ERROR);
                LOGGER.error("There is error during processing order with User External Id {}: ", order.getUserExternalId(), ex);
            }
        }
        return responseAPI;
    }

    @Override
    public ResponseAPI saveDocuments(String userExternalId, UploadedFiles documents, Requestor requestor) throws Exception {
        ResponseAPI responseAPI = findByUserExternalIdAndRequestor(userExternalId, requestor);
        CertificateOrder order = orderRepository.findByUserExternalIdAndRequestor(userExternalId, requestor.getId());

        Map<String, InputStream> fileName2Stream = new HashMap<>();
        for (MultipartFile file : documents.getFile()) {
            fileName2Stream.put(file.getOriginalFilename(), file.getInputStream());
        }
        certificateOrderService.uploadDocumentsToOrderWithRemove(fileName2Stream, order.getId());
        certificateOrderService.notifyOrderHolder(order);
        responseAPI.setStatus(StatusAPI.DOCUMENTS_LOADED);
        save(responseAPI);
        LOGGER.info(auditMarker, "The Documents was successfully loaded for CertificateOrder with id {} and User External Id {}", order.getId(), order.getUserExternalId());

        return responseAPI;
    }
}
