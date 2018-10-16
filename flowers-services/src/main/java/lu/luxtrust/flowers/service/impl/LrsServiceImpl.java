package lu.luxtrust.flowers.service.impl;

import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.entity.enrollment.Unit;
import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.entity.enrollment.ESealOrder;
import lu.luxtrust.flowers.integration.lrs.ws.*;
import lu.luxtrust.flowers.exception.FlowersException;
import lu.luxtrust.flowers.service.LrsProductHandler;
import lu.luxtrust.flowers.service.LrsService;
import lu.luxtrust.flowers.service.LrsStatusHandler;
import lu.luxtrust.flowers.service.util.SignatureTextGenerator;
import lu.luxtrust.pkix.pkcs.p7.HashAlgorithm;
import lu.luxtrust.pkix.pkcs.p7.PKCS7;
import lu.luxtrust.pkix.pkcs.p7.exceptions.PKIX_PKCS7_Exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.GregorianCalendar;

@Service
public class LrsServiceImpl implements LrsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LrsServiceImpl.class);
    private static final String SIGN_TEMPLATE_VERSION = "2.0";
    private static final String LANG = "EN";

    private Key lrsKey;
    private X509Certificate lrsCertificate;
    private LrsWS lrs;
    private LrsWSRegistrationAuthority wsRA;
    private SignatureTextGenerator generator;

    @Autowired
    public LrsServiceImpl(Key lrsKey, X509Certificate lrsCertificate, LrsWS lrs, LrsWSRegistrationAuthority wsRA, SignatureTextGenerator generator) {
        this.lrsKey = lrsKey;
        this.lrsCertificate = lrsCertificate;
        this.lrs = lrs;
        this.wsRA = wsRA;
        this.generator = generator;
    }

    @Override
    public String register(CertificateOrder order) throws Exception {
        LrsWSOrder wsOrder = createLrsOrder(order);
        return register(wsOrder);
    }

    public String register(ESealOrder order) throws Exception {
        LrsWSOrder wsOrder = createLrsOrder(order);
        return register(wsOrder);
    }

    private String register(LrsWSOrder wsOrder) throws Exception {
        String text = generator.generateRaoRegisterText(wsRA, wsOrder);
        LrsWSSignature wsSignature = getSignature(text, LANG);
        LrsWSRegisterResult result = lrs.register(wsRA, wsOrder, wsSignature, SIGN_TEMPLATE_VERSION);
        String lrsOrderNumber = result.getOrderNumber();
        if (lrsOrderNumber == null) {
            LOGGER.error("failed to send order {} to lrs, reason: {}", wsOrder.getArchive(), result.getErrorMessage());
            throw new FlowersException(result.getErrorMessage(), "LRS_REGISTER_FAILURE");
        }
        LOGGER.info("order {} sent to lrs, lrs_order_number = {}", wsOrder.getArchive(), lrsOrderNumber);
        return lrsOrderNumber;
    }

    @Override
    public void pollStatus(LrsStatusHandler handler) {
        lrs.getStatusAsync(wsRA, handler.getLrsOrderNumber(), handler);
    }

    public void enrichProductInfo(LrsProductHandler handler) {
        lrs.getProductAsync(wsRA, handler.getLrsOrderNumber(), handler);
    }

    private LrsWSSignature getSignature(String text, String lang) throws Exception {
        LrsWSSignature wsSignature = new LrsWSSignature();
        wsSignature.setLanguage(lang);
        wsSignature.setText(text);
        return signOrder(wsSignature);
    }

    private LrsWSOrder createLrsOrder(CertificateOrder order) throws Exception {
        LrsWSSubject subject = new LrsWSSubject();

        GregorianCalendar c = new GregorianCalendar();
        c.setTime(order.getHolder().getBirthDate());
        XMLGregorianCalendar bday = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        subject.setBirthDate(bday);

        subject.setEmail1(order.getHolder().geteMail());
        subject.setGivenName(order.getHolder().getFirstName());
        subject.setSurName(order.getHolder().getSurName());
        subject.setNationality(order.getHolder().getNationality().getNationalityCode().toUpperCase());

        LrsWSSigningServer signingServer = new LrsWSSigningServer();
        signingServer.setSubject(subject);
        switch (order.getDevice()) {
            case TOKEN:
                signingServer.setDeviceType(LrsWSDeviceTypeEnum.DPGO_6);
                signingServer.setDeviceSerialNumber(order.getTokenSerialNumber());
                break;
            case MOBILE:
                signingServer.setDeviceType(LrsWSDeviceTypeEnum.DPM);
                break;
            default:
                throw new FlowersException("unsupported device type: " + order.getDevice());
        }

        LrsWSProduct product = new LrsWSProduct();
        switch (order.getHolder().getCertificateType()) {
            case PRIVATE:
                product.setProfile(LrsWSProfileEnum.PRIVATE);
                break;
            case PROFESSIONAL_PERSON:
                product.setProfile(LrsWSProfileEnum.PROFESSIONAL);
                subject.setOrganisation(getLrsWSOrganisation(order.getUnit()));
                subject.getOrganisation().setDepartment(order.getDepartment());
                break;
            case PROFESSIONAL_CERTIFICATE_ADMINISTRATOR:
                product.setProfile(LrsWSProfileEnum.ADMINISTRATOR);
                subject.setOrganisation(getLrsWSOrganisation(order.getUnit()));
                subject.getOrganisation().setDepartment(order.getDepartment());
                break;
            default:
                throw new FlowersException("unsupported certificate type: " + order.getHolder().getCertificateType());
        }
        product.setPublish(order.getPublish());
        product.setSigningServer(signingServer);

        LrsWSOrder result = new LrsWSOrder();
        result.setProduct(product);

        // Providing missing information for re-key
        LrsWSPayment payment = new LrsWSPayment();
        payment.setMode(LrsWSPaymentModeEnum.LRA);
        result.setPayment(payment);
        LrsWSAddress address = new LrsWSAddress();

        String city = order.getAddress().getCity();
        // dirty hack due to stupid lrs checks
        if ((order.getAddress().getPostCode() + " " + city).length() > 35) {
            city = city.substring(0, 35 - order.getAddress().getPostCode().length() - 1);
        }
        address.setLocality(city);

        address.setCountry(order.getAddress().getCountry().getCountryCode().toUpperCase());
        address.setOrganisation(order.getAddress().getName());
        address.setPostalCode(order.getAddress().getPostCode());
        address.setStreet2(order.getAddress().getAddressLine2());
        address.setStreet1(order.getAddress().getStreetAndHouseNo());

        LrsWSDelivery delivery = new LrsWSDelivery();
        delivery.setMail(address);
        delivery.setEmail(order.getHolder().getNotifyEmail());
        delivery.setActivationCode(order.getHolder().getActivationCode());
        delivery.setCellPhone(order.getHolder().getPhoneNumber());
        delivery.setMobile(order.getHolder().getPhoneNumber());
        result.setCodeChannel(delivery);
        result.setArchive(Long.toString(order.getId()));
        result.setSignedGTC(order.getAcceptedGTC());

        switch (order.getHolder().getCertificateLevel()) {
            case LCP:
                result.setCertLevel("Lightweight");
                break;
            case QCP:
                result.setCertLevel("Qualified");
                break;
            case NCP:
                result.setCertLevel("Advanced");
                break;
            default:
                throw new FlowersException("Unsupported cert level: " + order.getHolder().getCertificateLevel());
        }
        result.setRequestor(order.getUnit().getRequestor().getName());
        return result;
    }

    private LrsWSOrder createLrsOrder(ESealOrder order) throws Exception {
        LrsWSSubject subject = new LrsWSSubject();

        User firstManager = order.geteSealManagers().get(0);
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(firstManager.getBirthDate());
        XMLGregorianCalendar bday = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        subject.setBirthDate(bday);

        subject.setEmail1(firstManager.getEmail());
        subject.setGivenName(firstManager.getFirstName());
        subject.setSurName(firstManager.getSurName());
        subject.setNationality(firstManager.getNationality().getNationalityCode().toUpperCase());

        LrsWSSigningServer signingServer = new LrsWSSigningServer();
        signingServer.setSubject(subject);
        signingServer.setDeviceType(LrsWSDeviceTypeEnum.DPM);

        LrsWSProduct product = new LrsWSProduct();
        product.setProfile(LrsWSProfileEnum.ESEAL);
        subject.setOrganisation(getLrsWSOrganisation(order.getUnit()));
        subject.getOrganisation().setDepartment(order.getOrganisationalUnit());

        product.setPublish(order.getPublish());
        product.setSigningServer(signingServer);

        LrsWSOrder result = new LrsWSOrder();
        result.setProduct(product);

        // Providing missing information for re-key
        LrsWSPayment payment = new LrsWSPayment();
        payment.setMode(LrsWSPaymentModeEnum.LRA);
        result.setPayment(payment);
        LrsWSAddress address = new LrsWSAddress();

        String city = order.getUnit().getCity();
        // dirty hack due to stupid lrs checks
        if ((order.getUnit().getPostCode() + " " + city).length() > 35) {
            city = city.substring(0, 35 - order.getUnit().getPostCode().length() - 1);
        }
        address.setLocality(city);

        address.setCountry(order.getUnit().getCountry().getCountryCode().toUpperCase());
        address.setOrganisation(order.getUnit().getCompanyName());
        address.setPostalCode(order.getUnit().getPostCode());
        address.setStreet1(order.getUnit().getStreetAndHouseNo());

        LrsWSDelivery delivery = new LrsWSDelivery();
        delivery.setMail(address);
        delivery.setEmail(firstManager.getEmail());
        delivery.setActivationCode("00000");
        delivery.setMobile(firstManager.getPhoneNumber());
        result.setCodeChannel(delivery);
        result.setArchive(Long.toString(order.getId()));
        result.setSignedGTC(true);

        result.setCertLevel("Qualified");
        result.setRequestor(order.getUnit().getRequestor().getName());
        return result;
    }

    private LrsWSOrganisation getLrsWSOrganisation(Unit unit) {
        LrsWSOrganisation organisation = new LrsWSOrganisation();
        organisation.setCountry(unit.getCountry().getCountryCode().toUpperCase());

        organisation.setName(unit.getCommonName());
        String identifierValue = unit.getIdentifier().getValue();
        switch (unit.getIdentifier().getType()) {
            case VAT:
                organisation.setTva(identifierValue);
                break;
            case RCSL:
                organisation.setRcsl(identifierValue);
                break;
            case OTHER:
                organisation.setOther(identifierValue);
                break;
            default:
                throw new FlowersException("unsupported identifier type: " + unit.getIdentifier().getType());
        }
        return organisation;
    }

    private LrsWSSignature signOrder(LrsWSSignature signature) throws CertificateException,  IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException, PKIX_PKCS7_Exception {
        String text = signature.getText();
        Signature rsa = Signature.getInstance("SHA256withRSA");
        rsa.initSign((PrivateKey) lrsKey);
        InputStream stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8.name()));
        BufferedInputStream bufin = new BufferedInputStream(stream);
        byte[] buffer = new byte[1024];
        int len;
        while (bufin.available() != 0) {
            len = bufin.read(buffer);
            rsa.update(buffer, 0, len);
        }
        bufin.close();
        byte[] realSig = rsa.sign();
        if (realSig == null || realSig.length == 0) {
            throw new FlowersException("Failed to generate signature");
        }
        PKCS7 pkcs7 = new PKCS7();
        byte[] pkcs7Signature = pkcs7.createPKCS7(realSig, lrsCertificate.getEncoded(), HashAlgorithm.SHA256, text.getBytes());
        signature.setEncoding(pkcs7Signature);
        LrsWSCertificate certificate = new LrsWSCertificate();
        certificate.setEncoded(lrsCertificate.getEncoded());
        certificate.setIssuerDN(lrsCertificate.getIssuerDN().toString());
        certificate.setSerial(lrsCertificate.getSerialNumber().toString());
        signature.setCertificate(certificate);
        String encodedText = new String(Base64.getEncoder().encode(text.getBytes("UTF-8")));
        signature.setText(encodedText);
        return signature;
    }
}
