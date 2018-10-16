package lu.luxtrust.flowers.service.impl;

import lu.luxtrust.flowers.entity.builder.*;
import lu.luxtrust.flowers.entity.enrollment.*;
import lu.luxtrust.flowers.enums.CertificateType;
import lu.luxtrust.flowers.enums.Device;
import lu.luxtrust.flowers.integration.lrs.ws.*;
import lu.luxtrust.flowers.exception.FlowersException;
import lu.luxtrust.flowers.service.LrsService;
import lu.luxtrust.flowers.service.util.SignatureTextGenerator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;

@RunWith(MockitoJUnitRunner.class)
public class LrsServiceImplTest {

    @Mock private LrsWS lrsWs;
    private LrsWSRegistrationAuthority wsRA = new LrsWSRegistrationAuthority();
    private SignatureTextGenerator generator = new SignatureTextGenerator();

    private LrsService service;
    private KeyStore.PrivateKeyEntry lrsKey;

    @Before
    public void init() throws Exception {
        wsRA.setNetworkID("1");
        wsRA.setOperatorID("2");
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(this.getClass().getResourceAsStream("/flowers-keystore.jks"), "LuxTrust".toCharArray());
        lrsKey = (KeyStore.PrivateKeyEntry) ks.getEntry("jmeterrao", new KeyStore.PasswordProtection("changeit".toCharArray()));
        service = new LrsServiceImpl(lrsKey.getPrivateKey(), (X509Certificate) lrsKey.getCertificate(), lrsWs, wsRA, generator);
    }

    @Test
    public void registerHappyPath() throws Exception {
        CertificateOrder order = filledOrder();

        final List<Object> args = new ArrayList<>();
        doAnswer(invocationOnMock -> {
            args.addAll(Arrays.asList(invocationOnMock.getArguments()));
            LrsWSRegisterResult wsResult = new LrsWSRegisterResult();
            wsResult.setOrderNumber("100500");
            return wsResult;
        }).when(lrsWs).register(any(LrsWSRegistrationAuthority.class), any(LrsWSOrder.class), any(LrsWSSignature.class), anyString());
        String lrsOrderNumber = service.register(order);

        assertThat(args.size()).isEqualTo(4);
        assertThat(lrsOrderNumber).isEqualTo("100500");

        LrsWSRegistrationAuthority authority = (LrsWSRegistrationAuthority) args.get(0);
        assertThat(authority.getNetworkID()).isEqualTo(wsRA.getNetworkID());
        assertThat(authority.getOperatorID()).isEqualTo(wsRA.getOperatorID());

        LrsWSOrder wsOrder = (LrsWSOrder) args.get(1);
        LrsWSProduct product = wsOrder.getProduct();
        assertThat(product.getProfile()).isEqualTo(LrsWSProfileEnum.PROFESSIONAL);
        assertThat(product.isPublish()).isEqualTo(order.getPublish());

        LrsWSSigningServer signingServer = product.getSigningServer();
        assertThat(signingServer.getDeviceType()).isEqualTo(LrsWSDeviceTypeEnum.DPM);
        assertThat(signingServer.getDeviceSerialNumber()).isNull();

        LrsWSSubject subject = signingServer.getSubject();
        assertThat(subject.getBirthDate().toGregorianCalendar().getTime().getTime()).isEqualTo(order.getHolder().getBirthDate().getTime());
        assertThat(subject.getEmail1()).isEqualTo(order.getHolder().geteMail());
        assertThat(subject.getGivenName()).isEqualTo(order.getHolder().getFirstName());
        assertThat(subject.getSurName()).isEqualTo(order.getHolder().getSurName());
        assertThat(subject.getNationality()).isEqualTo(order.getHolder().getNationality().getNationalityCode().toUpperCase());

        LrsWSOrganisation organisation = subject.getOrganisation();
        assertThat(organisation.getCountry()).isEqualTo(order.getUnit().getCountry().getCountryCode().toUpperCase());
        assertThat(organisation.getDepartment()).isEqualTo(order.getDepartment());
        assertThat(organisation.getName()).isEqualTo(order.getUnit().getCommonName());
        assertThat(organisation.getTva()).isNull();
        assertThat(organisation.getRcsl()).isNull();
        assertThat(organisation.getOther()).isEqualTo(order.getUnit().getIdentifier().getValue());

        LrsWSPayment payment = wsOrder.getPayment();
        assertThat(payment.getMode()).isEqualTo(LrsWSPaymentModeEnum.LRA);

        LrsWSDelivery delivery = wsOrder.getCodeChannel();
        assertThat(delivery.getEmail()).isEqualTo(order.getHolder().getNotifyEmail());
        assertThat(delivery.getActivationCode()).isEqualTo(order.getHolder().getActivationCode());
        assertThat(delivery.getCellPhone()).isEqualTo(order.getHolder().getPhoneNumber());
        assertThat(delivery.getMobile()).isEqualTo(order.getHolder().getPhoneNumber());

        LrsWSAddress wsAddress = delivery.getMail();
        assertThat(wsAddress.getLocality()).isEqualTo(order.getAddress().getCity());
        assertThat(wsAddress.getCountry()).isEqualTo(order.getAddress().getCountry().getCountryCode().toUpperCase());
        assertThat(wsAddress.getOrganisation()).isEqualTo(order.getAddress().getName());
        assertThat(wsAddress.getPostalCode()).isEqualTo(order.getAddress().getPostCode());
        assertThat(wsAddress.getStreet2()).isEqualTo(order.getAddress().getAddressLine2());
        assertThat(wsAddress.getStreet1()).isEqualTo(order.getAddress().getStreetAndHouseNo());

        assertThat(wsOrder.getArchive()).isEqualTo("100500");
        assertThat(wsOrder.isSignedGTC()).isTrue();
        assertThat(wsOrder.getCertLevel()).isEqualTo("Lightweight");
        assertThat(wsOrder.getRequestor()).isEqualTo("dg sante");

        LrsWSSignature signature = (LrsWSSignature) args.get(2);
        String text = new String(Base64.getDecoder().decode(signature.getText().getBytes("UTF-8")));
        assertThat(text).isEqualTo("I undersigned 2 / 1 hereby validate the order for the LuxTrust product Signing Server PROFESSIONAL and the correctness of the client's personal information.\n\r"+
        "Name: holder surname\n\rFirstname: holder first name\n\rNationality: UA\n\rBirthdate: 10/10/1998\n\rSubject Serial Number: \n\r"+
        "Organisation name: common name\n\rDepartement: Company department\n\rCountry: UA\n\r423423423423\n\rMobile phone number for delivery of LT codes: +380931234567\n\r"+
        "Delivery address:\n\rname\n\rline 2\n\r, Address streetAndHouseNo\n\rpost code Address city\n\rUA\n\rType of OTP Generator: DPM\n\r"+
        "Number of OTP Generator: \n\rGeneral Terms and Conditions signed: yes\n\rPublication in LuxTrust directory: no");
    }

    @Test(expected = FlowersException.class)
    public void registerWhenLrsReturnsError() throws Exception {
        CertificateOrder order = filledOrder();
        doAnswer(invocationOnMock -> {
            LrsWSRegisterResult wsResult = new LrsWSRegisterResult();
            wsResult.setErrorMessage("trololo");
            return wsResult;
        }).when(lrsWs).register(any(LrsWSRegistrationAuthority.class), any(LrsWSOrder.class), any(LrsWSSignature.class), anyString());
        service.register(order);
    }

    @Test
    public void registerCutCityIfNecessary() throws Exception {
        CertificateOrder order = filledOrder();
        order.getAddress().setPostCode("15___characters");
        order.getAddress().setCity("25_____________characters");

        final List<LrsWSOrder> wsOrders = new ArrayList<>();
        doAnswer(invocationOnMock -> {
            wsOrders.add((LrsWSOrder) invocationOnMock.getArguments()[1]);
            LrsWSRegisterResult wsResult = new LrsWSRegisterResult();
            wsResult.setOrderNumber("100501");
            return wsResult;
        }).when(lrsWs).register(any(LrsWSRegistrationAuthority.class), any(LrsWSOrder.class), any(LrsWSSignature.class), anyString());
        service.register(order);

        assertThat(wsOrders.size()).isEqualTo(1);
        assertThat(wsOrders.get(0).getCodeChannel().getMail().getLocality()).isEqualTo("25_____________char");
    }

    @Test
    public void registerForPrivateCompanyNotFilled() throws Exception {
        CertificateOrder order = filledOrder();
        order.getHolder().setCertificateType(CertificateType.PRIVATE);
        order.setDevice(Device.TOKEN);

        final List<LrsWSOrder> wsOrders = new ArrayList<>();
        doAnswer(invocationOnMock -> {
            wsOrders.add((LrsWSOrder) invocationOnMock.getArguments()[1]);
            LrsWSRegisterResult wsResult = new LrsWSRegisterResult();
            wsResult.setOrderNumber("100502");
            return wsResult;
        }).when(lrsWs).register(any(LrsWSRegistrationAuthority.class), any(LrsWSOrder.class), any(LrsWSSignature.class), anyString());
        service.register(order);

        assertThat(wsOrders.size()).isEqualTo(1);
        LrsWSOrder wsOrder = wsOrders.get(0);
        assertThat(wsOrder.getProduct().getSigningServer().getSubject().getOrganisation()).isNull();
        assertThat(wsOrder.getProduct().getSigningServer().getDeviceType()).isEqualTo(LrsWSDeviceTypeEnum.DPGO_6);
    }

    @Test
    public void registerCheckVat() throws Exception {
        CertificateOrder order = filledOrder();
        order.getUnit().getIdentifier().setType(CompanyIdentifier.Type.VAT);

        final List<LrsWSOrder> wsOrders = new ArrayList<>();
        doAnswer(invocationOnMock -> {
            wsOrders.add((LrsWSOrder) invocationOnMock.getArguments()[1]);
            LrsWSRegisterResult wsResult = new LrsWSRegisterResult();
            wsResult.setOrderNumber("100503");
            return wsResult;
        }).when(lrsWs).register(any(LrsWSRegistrationAuthority.class), any(LrsWSOrder.class), any(LrsWSSignature.class), anyString());
        service.register(order);

        assertThat(wsOrders.size()).isEqualTo(1);
        LrsWSOrder wsOrder = wsOrders.get(0);
        assertThat(wsOrder.getProduct().getSigningServer().getSubject().getOrganisation().getTva()).isEqualTo(order.getUnit().getIdentifier().getValue());
        assertThat(wsOrder.getProduct().getSigningServer().getSubject().getOrganisation().getRcsl()).isNull();
        assertThat(wsOrder.getProduct().getSigningServer().getSubject().getOrganisation().getOther()).isNull();
    }

    @Test
    public void registerCheckRcsl() throws Exception {
        CertificateOrder order = filledOrder();
        order.getUnit().getIdentifier().setType(CompanyIdentifier.Type.RCSL);
        order.getHolder().setCertificateType(CertificateType.PROFESSIONAL_CERTIFICATE_ADMINISTRATOR);

        final List<LrsWSOrder> wsOrders = new ArrayList<>();
        doAnswer(invocationOnMock -> {
            wsOrders.add((LrsWSOrder) invocationOnMock.getArguments()[1]);
            LrsWSRegisterResult wsResult = new LrsWSRegisterResult();
            wsResult.setOrderNumber("100504");
            return wsResult;
        }).when(lrsWs).register(any(LrsWSRegistrationAuthority.class), any(LrsWSOrder.class), any(LrsWSSignature.class), anyString());
        service.register(order);

        assertThat(wsOrders.size()).isEqualTo(1);
        LrsWSOrder wsOrder = wsOrders.get(0);
        assertThat(wsOrder.getProduct().getSigningServer().getSubject().getOrganisation().getRcsl()).isEqualTo(order.getUnit().getIdentifier().getValue());
        assertThat(wsOrder.getProduct().getSigningServer().getSubject().getOrganisation().getTva()).isNull();
        assertThat(wsOrder.getProduct().getSigningServer().getSubject().getOrganisation().getOther()).isNull();
    }

    private CertificateOrder filledOrder() {
        Holder holder = HolderBuilder.newBuilder().nationality(NationalityBuilder.newBuilder().build()).build();
        Unit unit = UnitBuilder.newBuilder().build();
        Address address = AddressBuilder.newBuilder().country(CountryBuilder.newBuilder().build()).build();

        return CertificateOrderBuilder.newBuilder().id(100500L).acceptedGTC(Boolean.TRUE).holder(holder).unit(unit).address(address).build();
    }

}