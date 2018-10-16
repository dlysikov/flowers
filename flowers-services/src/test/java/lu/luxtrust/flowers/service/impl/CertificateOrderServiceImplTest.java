package lu.luxtrust.flowers.service.impl;

import lu.luxtrust.flowers.AbstractWithSpringContextTest;
import lu.luxtrust.flowers.entity.builder.*;
import lu.luxtrust.flowers.entity.common.Country;
import lu.luxtrust.flowers.entity.common.Nationality;
import lu.luxtrust.flowers.entity.enrollment.*;
import lu.luxtrust.flowers.entity.system.Role;
import lu.luxtrust.flowers.entity.system.Requestor;
import lu.luxtrust.flowers.entity.system.RequestorConfiguration;
import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.enums.CertificateLevel;
import lu.luxtrust.flowers.enums.OrderStatus;
import lu.luxtrust.flowers.enums.RoleType;
import lu.luxtrust.flowers.integration.lrs.mock.LrsWsMock;
import lu.luxtrust.flowers.integration.lrs.ws.*;
import lu.luxtrust.flowers.repository.CertificateOrderRepository;
import lu.luxtrust.flowers.rule.SmtpServerRule;
import lu.luxtrust.flowers.service.CertificateOrderService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.Query;
import javax.xml.ws.AsyncHandler;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static lu.luxtrust.flowers.enums.OrderStatus.*;
import static org.assertj.core.api.Assertions.anyOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CertificateOrderServiceImplTest extends AbstractWithSpringContextTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private CertificateOrderService certificateOrderService;
    @Autowired
    private CertificateOrderRepository orderRepository;
    @Autowired
    private LrsWS lrsWS;

    private LrsWSRegisterResult lrsWSRegisterResult;

    private User issuer;
    private Requestor requestor;
    private Requestor requestor2;
    private Country country;
    private Unit unit;
    private Nationality nationality;

    @Rule
    public SmtpServerRule smtpServerRule = new SmtpServerRule();

    @Before
    public void init() {
        Set<Role> roles = new HashSet<>();
        Role role = new Role();
        role.setRoleType(RoleType.FLOWERS_ADMIN);
        entityManager.persist(role);
        roles.add(role);

        Role csd = new Role();
        csd.setRoleType(RoleType.CSD_AGENT);
        csd = entityManager.persist(csd);
        entityManager.persist(UserBuilder.newBuilder().unit(unit).ssn("42").roles(Collections.singleton(csd)).build());

        issuer = UserBuilder.newBuilder().roles(roles).build();
        issuer = entityManager.persist(issuer);

        RequestorConfiguration config = new RequestorConfiguration();
        config.setValidatedBy(RequestorConfiguration.ValidatedBy.CSD);
        config = entityManager.persist(config);
        requestor = RequestorBuilder.newBuilder().name("test").config(config).build();
        requestor = entityManager.persist(requestor);

        RequestorConfiguration config2 = new RequestorConfiguration();
        config2.setValidatedBy(RequestorConfiguration.ValidatedBy.DIA);
        config2 = entityManager.persist(config2);
        requestor2 = RequestorBuilder.newBuilder().name("test2").config(config2).build();
        requestor2 = entityManager.persist(requestor2);

        country = entityManager.persist(CountryBuilder.newBuilder().build());
        nationality = entityManager.persist(NationalityBuilder.newBuilder().build());
        unit = entityManager.persist(UnitBuilder.newBuilder().requestor(requestor).country(country).build());

        Role dia = new Role();
        dia.setRoleType(RoleType.DIA);
        dia = entityManager.persist(dia);
        entityManager.persist(UserBuilder.newBuilder().unit(unit).ssn("100500").roles(Collections.singleton(dia)).build());

        this.lrsWSRegisterResult = Mockito.mock(LrsWSRegisterResult.class);
        when(lrsWS.register(any(LrsWSRegistrationAuthority.class), any(LrsWSOrder.class), any(LrsWSSignature.class), anyString())).thenReturn(lrsWSRegisterResult);

    }

    @Test
    public void saveOrder() {
        CertificateOrder testOrder = CertificateOrderBuilder.newBuilder().status(null).issuer(issuer).build();
        CertificateOrder result = this.certificateOrderService.saveOrder(testOrder);
        assertThat(result.getStatus()).isEqualTo(OrderStatus.DRAFT);
        assertThat(result.getHolder()).isEqualTo(testOrder.getHolder());
        assertThat(result.getAddress()).isEqualTo(testOrder.getAddress());

        CertificateOrder resultOrder = entityManager.find(CertificateOrder.class, result.getId());
        assertThat(resultOrder).isNotNull();
        assertThat(resultOrder.getStatus()).isEqualTo(OrderStatus.DRAFT);
    }

    @Test
    public void findDuplicatesEmptyOrders() {
        assertThat(certificateOrderService.findDuplicates(new ArrayList<>())).isEmpty();
    }

    @Test
    public void sendToLRSFromRemoteIdentificationStatus() throws Exception {
        CertificateOrder o = CertificateOrderBuilder.newBuilder().unit(unit).status(OrderStatus.REMOTE_IDENTIFICATION_REQUIRED).issuer(issuer).build();
        o.getAddress().setCountry(country);
        o.getHolder().setNationality(nationality);
        entityManager.persist(o);

        when(lrsWSRegisterResult.getOrderNumber()).thenReturn("ORDER_1");

        certificateOrderService.sendToLrs(o);

        CertificateOrder result = entityManager.find(CertificateOrder.class, o.getId());
        assertThat(result.getStatus()).isEqualTo(SENT_TO_LRS);
        assertThat(result.getLrsOrderNumber()).isEqualTo("ORDER_1");
    }

    @Test
    public void sendToLRSFromRemoteFace2FaceStatus() throws Exception {
        CertificateOrder o = CertificateOrderBuilder.newBuilder().unit(unit).status(OrderStatus.FACE_2_FACE_REQUIRED).issuer(issuer).build();
        o.getAddress().setCountry(country);
        o.getHolder().setNationality(nationality);
        entityManager.persist(o);
        when(lrsWSRegisterResult.getOrderNumber()).thenReturn("ORDER_2");
        certificateOrderService.sendToLrs(o);

        CertificateOrder result = entityManager.find(CertificateOrder.class, o.getId());
        assertThat(result.getStatus()).isEqualTo(SENT_TO_LRS);
        assertThat(result.getLrsOrderNumber()).isEqualTo("ORDER_2");
    }

    @Test
    public void reject() throws Exception {
        CertificateOrder o = CertificateOrderBuilder.newBuilder().unit(unit).status(OrderStatus.DRAFT).issuer(issuer).build();
        entityManager.persist(o);
        certificateOrderService.reject(o);

        CertificateOrder result = entityManager.find(CertificateOrder.class, o.getId());
        assertThat(result.getStatus()).isEqualTo(REJECTED);
    }

    @Test
    public void findDuplicates() {
        Date birthDate = new Date();
        Holder holder = HolderBuilder.newBuilder().firstName("ivan").surName("john").birthDate(birthDate).build();
        CertificateOrder testOrder = CertificateOrderBuilder.newBuilder().unit(unit).status(null).issuer(issuer).holder(holder).build();

        CertificateOrder result = this.orderRepository.save(testOrder);

        List<CertificateOrder> duplicates = this.certificateOrderService.findDuplicates(Arrays.asList(result));
        assertThat(duplicates).hasSize(1);
        assertThat(duplicates.get(0).getId()).isEqualTo(result.getId());
        assertThat(duplicates.get(0).getHolder()).isEqualTo(result.getHolder());
    }

    @Test
    public void saveAll() throws Exception {
        CertificateOrder testOrder = CertificateOrderBuilder.newBuilder().unit(unit).status(null).issuer(issuer).build();
        List<CertificateOrder> toSave = new ArrayList<>();
        toSave.add(testOrder);
        this.certificateOrderService.saveAll(toSave);

        Query query = entityManager.getEntityManager().createQuery("select o from CertificateOrder o where o.holder.id=?1");
        query.setParameter(1, testOrder.getHolder().getId());
        CertificateOrder result = (CertificateOrder) query.getSingleResult();

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(OrderStatus.DRAFT);
        assertThat(result.getHolder()).isEqualTo(testOrder.getHolder());
        assertThat(result.getAddress()).isEqualTo(testOrder.getAddress());
    }

    @Test
    public void findOrderToValidateByUser() {
        CertificateOrder o = CertificateOrderBuilder.newBuilder().unit(unit).issuer(issuer).build();
        entityManager.persist(o);
        OrderUserValidatePage p = OrderUserValidatePageBuilder.newBuilder().order(o).build();
        entityManager.persist(p);

        CertificateOrder result = this.certificateOrderService.findOrderToValidateByUser(p.getPageHash());
        assertThat(result.getId()).isEqualTo(o.getId());
    }

    @Test
    public void findOrderToValidateByUserExpiredPage() {
        CertificateOrder o = CertificateOrderBuilder.newBuilder().unit(unit).issuer(issuer).build();
        entityManager.persist(o);
        OrderUserValidatePage p = OrderUserValidatePageBuilder.newBuilder().order(o).expirationTime(new Date(new Date().getTime() - 11111111L)).build();
        entityManager.persist(p);

        assertThat(this.certificateOrderService.findOrderToValidateByUser(p.getPageHash())).isNull();
    }

    @Test
    public void notifyOrderHolder() throws Exception {
        CertificateOrder o = CertificateOrderBuilder.newBuilder().unit(unit).status(OrderStatus.DRAFT).issuer(issuer).build();
        entityManager.persist(o);
        CertificateOrder order = certificateOrderService.notifyOrderHolder(o);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.USER_DRAFT);
    }

    @Test
    public void validateOrderFace2FaceRequired() throws Exception {
        CertificateOrder o = CertificateOrderBuilder.newBuilder().unit(unit).status(OrderStatus.USER_DRAFT).issuer(issuer).build();
        o.getHolder().setCertificateLevel(CertificateLevel.NCP);
        entityManager.persist(o);
        OrderUserValidatePage page = OrderUserValidatePageBuilder.newBuilder().order(o).build();
        entityManager.persist(page);

        CertificateOrder order = certificateOrderService.validateByUser(o);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.FACE_2_FACE_REQUIRED);
    }

    @Test
    public void validateOrderRemoteIdRequired() throws Exception {
        CertificateOrder o = CertificateOrderBuilder.newBuilder().unit(unit).status(OrderStatus.USER_DRAFT).issuer(issuer).remoteId(Boolean.TRUE).build();
        o.getHolder().setCertificateLevel(CertificateLevel.NCP);
        entityManager.persist(o);
        OrderUserValidatePage page = OrderUserValidatePageBuilder.newBuilder().order(o).build();
        entityManager.persist(page);

        certificateOrderService.notifyOrderHolder(o);

        CertificateOrder order = certificateOrderService.validateByUser(o);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.REMOTE_IDENTIFICATION_REQUIRED);
    }

    @Test
    public void validateOrderDiaSigningRequired() throws Exception {
        CertificateOrder o = CertificateOrderBuilder.newBuilder().unit(unit).status(OrderStatus.USER_DRAFT).issuer(issuer).build();
        unit.setRequestor(requestor2);
        entityManager.persist(o);
        OrderUserValidatePage page = OrderUserValidatePageBuilder.newBuilder().order(o).build();
        entityManager.persist(page);

        certificateOrderService.notifyOrderHolder(o);

        CertificateOrder order = certificateOrderService.validateByUser(o);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DIA_SIGNING_REQUIRED);
    }

    @Test
    public void uploadDocumentsToOrder() throws Exception {
        CertificateOrder o = CertificateOrderBuilder.newBuilder().unit(unit).issuer(issuer).remoteId(Boolean.TRUE).build();
        entityManager.persist(o);

        Map<String, InputStream> file = new HashMap<>();
        file.put("texs.xml", getClass().getResourceAsStream("/orders.xml"));
        List<Document> documents = certificateOrderService.uploadDocumentsToOrder(file, o.getId());

        assertThat(documents).hasSize(1);
        assertThat(documents.get(0).getName()).isEqualTo("texs.xml");
        assertThat(documents.get(0).getFile()).isNotNull();
        assertThat(documents.get(0).getHolder()).isNotNull();
        assertThat(documents.get(0).getHolder().getId()).isEqualTo(o.getId());
    }

    @Test
    public void findById() {
        CertificateOrder o = CertificateOrderBuilder.newBuilder().issuer(issuer).build();
        entityManager.persist(o);

        assertThat(certificateOrderService.findById(o.getId())).isEqualTo(o);
    }

    @Test
    public void removeDocumentByNameTest() throws IOException {
        CertificateOrder o = CertificateOrderBuilder.newBuilder().unit(unit).issuer(issuer).remoteId(Boolean.TRUE).build();
        entityManager.persist(o);
        String fileName = "texs.xml";
        Map<String, InputStream> file = new HashMap<>();
        file.put(fileName, getClass().getResourceAsStream("/orders.xml"));
        certificateOrderService.uploadDocumentsToOrder(file, o.getId());
        int deleteCount = certificateOrderService.removeDocumentByName(o.getHolder().getId(), fileName);
        assertThat(deleteCount).isEqualTo(1);
    }

    @Test
    public void updateStatusFetchDetailsOnProducedState() {
        initProductHandler("100500");
        CertificateOrder o = CertificateOrderBuilder.newBuilder().unit(unit).status(LRS_ONGOING).issuer(issuer).lrsOrderNumber("100500").build();
        o.getHolder().setCertificateLevel(CertificateLevel.NCP);
        entityManager.persist(o);

        certificateOrderService.updateStatus("100500", LRS_ONGOING, LRS_PRODUCED);

        String ssn = orderRepository.getSsnByLrsOrderNumber("100500");
        assertThat(ssn).isEqualTo("666");

        certificateOrderService.updateStatus("100500", LRS_PRODUCED, LRS_ACTIVATED);

        verify(lrsWS, times(1)).getProductAsync(any(LrsWSRegistrationAuthority.class), eq("100500"), any(AsyncHandler.class));
    }

    @Test
    public void updateStatusFetchDetailsOnActivatedState() {
        initProductHandler("42");
        CertificateOrder o = CertificateOrderBuilder.newBuilder().ssn(null).unit(unit).status(OrderStatus.LRS_ONGOING).issuer(issuer).lrsOrderNumber("42").build();
        o.getHolder().setCertificateLevel(CertificateLevel.NCP);
        entityManager.persist(o);

        certificateOrderService.updateStatus("42", LRS_ONGOING, LRS_ACTIVATED);

        String ssn = orderRepository.getSsnByLrsOrderNumber("42");
        assertThat(ssn).isEqualTo("666");
    }

    private void initProductHandler(String lrsOrderNumber) {
        when(lrsWS.getProductAsync(any(LrsWSRegistrationAuthority.class), eq(lrsOrderNumber), any(AsyncHandler.class))).then(invocationOnMock -> {
            AsyncHandler<GetProductResponse> handler = (AsyncHandler<GetProductResponse>) invocationOnMock.getArguments()[2];

            LrsWSCertificate cert = new LrsWSCertificate();
            cert.setSerial("42");
            LrsWSProduct product = new LrsWSProduct() {
                @Override
                public List<LrsWSCertificate> getCertificate() {
                    return Collections.singletonList(cert);
                }
            };
            product.setSubjectSerialNumber("666");

            LrsWSProductResult result = new LrsWSProductResult();
            result.setProduct(product);
            GetProductResponse response = new GetProductResponse();
            response.setReturn(result);

            handler.handleResponse(new LrsWsMock.MockResponse<>(response));
            return null;
        });
    }
}