package lu.luxtrust.flowers.job;

import lu.luxtrust.flowers.entity.system.ResponseAPI;
import lu.luxtrust.flowers.service.ResponseAPIService;
import lu.luxtrust.flowers.service.impl.CertificateOrderServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ResponseAPIJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(CertificateOrderServiceImpl.class);

    @Autowired
    private ResponseAPIService responseAPIService;

    @Value("${job.api.cursor-size:100}")
    private int cursorSize;

    @Scheduled(cron = "${job.api.response.period:0 0/10 * ? * *}")
    public void sendResponse() {

        Slice<ResponseAPI> iterator;
        int pageNumber = 0;

        do {
            iterator = responseAPIService.getResponseAPIsForSending(new PageRequest(pageNumber++, cursorSize));
            if (iterator.hasContent()) {
                for (ResponseAPI responseAPI : iterator.getContent()) {
                    LOGGER.info("Sending back failed response for user external id [{}]", responseAPI.getUserExternalId());
                    ResponseEntity responseEntity = responseAPIService.sendBackResponse(responseAPI);
                    LOGGER.info("The response is [{}] for user external id [{}]", responseEntity, responseAPI.getUserExternalId());
                }
            }
        } while (iterator.hasNext());
    }

}
