package lu.luxtrust.flowers.service.impl;

import lu.luxtrust.flowers.AbstractWithSpringContextTest;
import lu.luxtrust.flowers.entity.builder.*;
import lu.luxtrust.flowers.entity.common.Country;
import lu.luxtrust.flowers.entity.enrollment.*;
import lu.luxtrust.flowers.entity.system.*;
import lu.luxtrust.flowers.enums.StatusAPI;
import lu.luxtrust.flowers.model.UploadedFiles;
import lu.luxtrust.flowers.rule.SmtpServerRule;
import lu.luxtrust.flowers.service.ResponseAPIService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class ResponseAPIServiceImplTest  extends AbstractWithSpringContextTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ResponseAPIService responseAPIService;

    private Requestor requestor;
    private RequestorConfiguration requestorConfig;
    private Unit unit;

    @Rule
    public SmtpServerRule smtpServerRule = new SmtpServerRule();

    @Before
    public void init() {
        requestorConfig = entityManager.persist(RequestorConfigurationBuilder.newBuilder().build());
        requestor = entityManager.persist(RequestorBuilder.newBuilder().config(requestorConfig).name("test").ssn("111").build());
        Country country = CountryBuilder.newBuilder().countryCode("ad").build();
        entityManager.persist(country);
        this.unit = UnitBuilder.newBuilder().identifierType(CompanyIdentifier.Type.OTHER).identifierValue("123").city("Kiev").country(country).commonName("qqq")
                .companyName("qqq").postCode("123456").streetAndHouseNo("qqq")
                .requestor(requestor).build();
        entityManager.persist(unit);
    }

    @Test
    public void save(){
        ResponseAPI responseAPI = ResponseApiBuilder.newBuilder().userExternalId("111").serviceSSN("222").build();
        entityManager.persist(responseAPI);
        ResponseAPI savedResponseAPI = responseAPIService.save(responseAPI);
        assertThat(savedResponseAPI).isNotNull();
        assertThat(savedResponseAPI).isEqualTo(responseAPI);
    }

    @Test
    public void saveOrderWithDuplicate() {
        Holder holder = HolderBuilder.newBuilder().notifyEmail("test").firstName("Dima").surName("Lysikov").waitDocuments(false).build();
        CertificateOrder order = CertificateOrderBuilder.newBuilder().userExternalId("111").holder(holder).unit(null).build();
        entityManager.persist(order);
        ResponseAPI responseAPI = responseAPIService.saveOrder(order, requestor);
        assertThat(responseAPI.getStatus()).isEqualTo(StatusAPI.DUPLICATE);
    }

    @Test
    public void saveOrderWithWaitDocument() {
        Holder holder = HolderBuilder.newBuilder().notifyEmail("test").firstName("Dima").surName("Lysikov").waitDocuments(true).build();
        CertificateOrder order = CertificateOrderBuilder.newBuilder().holder(holder).userExternalId("qqq").build();
        ResponseAPI responseAPI = responseAPIService.saveOrder(order, requestor);
        assertThat(responseAPI.getStatus()).isEqualTo(StatusAPI.WAITING_FOR_DOCUMENTS);
    }

    @Test
    public void saveOrder() {
        Holder holder = HolderBuilder.newBuilder().notifyEmail("test").firstName("Dima").surName("Lysikov").waitDocuments(false).build();
        CertificateOrder order = CertificateOrderBuilder.newBuilder().holder(holder).unit(this.unit).userExternalId("qqq").build();
        ResponseAPI responseAPI = responseAPIService.saveOrder(order, requestor);
        assertThat(responseAPI.getStatus()).isEqualTo(StatusAPI.ORDER_SAVED);
    }

    @Test
    public void uploadDocuments() throws Exception {
        ResponseAPI responseAPI = ResponseApiBuilder.newBuilder().requestor(requestor).userExternalId("qqq").serviceSSN("222").build();
        entityManager.persist(responseAPI);
        Holder h = HolderBuilder.newBuilder().build();
        CertificateOrder o = CertificateOrderBuilder.newBuilder().unit(unit).holder(h).userExternalId("qqq").build();
        entityManager.persist(o);

        UploadedFiles uploadedFiles = new UploadedFiles();
        MultipartFile [] files = {new MockMultipartFile("Panda", getClass().getResourceAsStream("/orders.xml"))};
        uploadedFiles.setFile(files);

        ResponseAPI response = responseAPIService.saveDocuments("qqq", uploadedFiles, requestor);
        assertThat(response.getStatus()).isEqualTo(StatusAPI.DOCUMENTS_LOADED);
    }

    @Test
    public void findByUserExternalId(){
        ResponseAPI responseAPI = ResponseApiBuilder.newBuilder().requestor(requestor).userExternalId("qqq").serviceSSN("222").build();
        entityManager.persist(responseAPI);
        ResponseAPI savedResponseAPI = responseAPIService.findByUserExternalIdAndRequestor("qqq", requestor);
        assertThat(savedResponseAPI).isNotNull();
        assertThat(savedResponseAPI.getServiceSSN()).isEqualTo("222");
        assertThat(savedResponseAPI).isEqualTo(responseAPI);
    }

    @Test
    public void getResponseAPIsForSending() {
        ResponseAPI responseAPISendingError = ResponseApiBuilder.newBuilder().userExternalId("qqq").status(StatusAPI.RESPONSE_SENDING_ERROR).build();
        ResponseAPI responseAPIServerError = ResponseApiBuilder.newBuilder().userExternalId("aaa").status(StatusAPI.RESPONSE_SERVER_ERROR).build();
        ResponseAPI responseAPIOrderSaved = ResponseApiBuilder.newBuilder().userExternalId("zzz").status(StatusAPI.ORDER_SAVED).build();

        entityManager.persist(responseAPISendingError);
        entityManager.persist(responseAPIServerError);
        entityManager.persist(responseAPIOrderSaved);

        Slice<ResponseAPI> iterator;
        iterator = responseAPIService.getResponseAPIsForSending(new PageRequest(0, 10));
        List<ResponseAPI> responseAPIList = iterator.getContent();
        assertThat(responseAPIList).isNotNull();
        assertThat(responseAPIList.size()).isEqualTo(2);
        assertThat(responseAPIList.get(0).getStatus()).isIn(StatusAPI.RESPONSE_SENDING_ERROR, StatusAPI.RESPONSE_SERVER_ERROR);
        assertThat(responseAPIList.get(1).getStatus()).isIn(StatusAPI.RESPONSE_SENDING_ERROR, StatusAPI.RESPONSE_SERVER_ERROR);

    }

}
