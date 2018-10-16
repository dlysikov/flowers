
package lu.luxtrust.flowers.integration.lrs.ws;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the lu.luxtrust.flowers.integration.lrs.ws package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Cancel_QNAME = new QName("http://ws.lrs.luxtrust.lu/", "cancel");
    private final static QName _CancelResponse_QNAME = new QName("http://ws.lrs.luxtrust.lu/", "cancelResponse");
    private final static QName _GetOrder_QNAME = new QName("http://ws.lrs.luxtrust.lu/", "getOrder");
    private final static QName _GetOrderResponse_QNAME = new QName("http://ws.lrs.luxtrust.lu/", "getOrderResponse");
    private final static QName _GetProduct_QNAME = new QName("http://ws.lrs.luxtrust.lu/", "getProduct");
    private final static QName _GetProductResponse_QNAME = new QName("http://ws.lrs.luxtrust.lu/", "getProductResponse");
    private final static QName _GetStatus_QNAME = new QName("http://ws.lrs.luxtrust.lu/", "getStatus");
    private final static QName _GetStatusResponse_QNAME = new QName("http://ws.lrs.luxtrust.lu/", "getStatusResponse");
    private final static QName _GetVersion_QNAME = new QName("http://ws.lrs.luxtrust.lu/", "getVersion");
    private final static QName _GetVersionResponse_QNAME = new QName("http://ws.lrs.luxtrust.lu/", "getVersionResponse");
    private final static QName _Register_QNAME = new QName("http://ws.lrs.luxtrust.lu/", "register");
    private final static QName _RegisterResponse_QNAME = new QName("http://ws.lrs.luxtrust.lu/", "registerResponse");
    private final static QName _SendCodes_QNAME = new QName("http://ws.lrs.luxtrust.lu/", "sendCodes");
    private final static QName _SendCodesResponse_QNAME = new QName("http://ws.lrs.luxtrust.lu/", "sendCodesResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: lu.luxtrust.flowers.integration.lrs.ws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Cancel }
     * 
     */
    public Cancel createCancel() {
        return new Cancel();
    }

    /**
     * Create an instance of {@link CancelResponse }
     * 
     */
    public CancelResponse createCancelResponse() {
        return new CancelResponse();
    }

    /**
     * Create an instance of {@link GetOrder }
     * 
     */
    public GetOrder createGetOrder() {
        return new GetOrder();
    }

    /**
     * Create an instance of {@link GetOrderResponse }
     * 
     */
    public GetOrderResponse createGetOrderResponse() {
        return new GetOrderResponse();
    }

    /**
     * Create an instance of {@link GetProduct }
     * 
     */
    public GetProduct createGetProduct() {
        return new GetProduct();
    }

    /**
     * Create an instance of {@link GetProductResponse }
     * 
     */
    public GetProductResponse createGetProductResponse() {
        return new GetProductResponse();
    }

    /**
     * Create an instance of {@link GetStatus }
     * 
     */
    public GetStatus createGetStatus() {
        return new GetStatus();
    }

    /**
     * Create an instance of {@link GetStatusResponse }
     * 
     */
    public GetStatusResponse createGetStatusResponse() {
        return new GetStatusResponse();
    }

    /**
     * Create an instance of {@link GetVersion }
     * 
     */
    public GetVersion createGetVersion() {
        return new GetVersion();
    }

    /**
     * Create an instance of {@link GetVersionResponse }
     * 
     */
    public GetVersionResponse createGetVersionResponse() {
        return new GetVersionResponse();
    }

    /**
     * Create an instance of {@link Register }
     * 
     */
    public Register createRegister() {
        return new Register();
    }

    /**
     * Create an instance of {@link RegisterResponse }
     * 
     */
    public RegisterResponse createRegisterResponse() {
        return new RegisterResponse();
    }

    /**
     * Create an instance of {@link SendCodes }
     * 
     */
    public SendCodes createSendCodes() {
        return new SendCodes();
    }

    /**
     * Create an instance of {@link SendCodesResponse }
     * 
     */
    public SendCodesResponse createSendCodesResponse() {
        return new SendCodesResponse();
    }

    /**
     * Create an instance of {@link LrsWSRegistrationAuthority }
     * 
     */
    public LrsWSRegistrationAuthority createLrsWSRegistrationAuthority() {
        return new LrsWSRegistrationAuthority();
    }

    /**
     * Create an instance of {@link LrsWSProductResult }
     * 
     */
    public LrsWSProductResult createLrsWSProductResult() {
        return new LrsWSProductResult();
    }

    /**
     * Create an instance of {@link LrsWSProduct }
     * 
     */
    public LrsWSProduct createLrsWSProduct() {
        return new LrsWSProduct();
    }

    /**
     * Create an instance of {@link LrsWSSigningServer }
     * 
     */
    public LrsWSSigningServer createLrsWSSigningServer() {
        return new LrsWSSigningServer();
    }

    /**
     * Create an instance of {@link LrsWSSubject }
     * 
     */
    public LrsWSSubject createLrsWSSubject() {
        return new LrsWSSubject();
    }

    /**
     * Create an instance of {@link LrsWSOrganisation }
     * 
     */
    public LrsWSOrganisation createLrsWSOrganisation() {
        return new LrsWSOrganisation();
    }

    /**
     * Create an instance of {@link LrsWSCertificate }
     * 
     */
    public LrsWSCertificate createLrsWSCertificate() {
        return new LrsWSCertificate();
    }

    /**
     * Create an instance of {@link LrsWSSignature }
     * 
     */
    public LrsWSSignature createLrsWSSignature() {
        return new LrsWSSignature();
    }

    /**
     * Create an instance of {@link LrsWSCancelResult }
     * 
     */
    public LrsWSCancelResult createLrsWSCancelResult() {
        return new LrsWSCancelResult();
    }

    /**
     * Create an instance of {@link LrsWSOrderResult }
     * 
     */
    public LrsWSOrderResult createLrsWSOrderResult() {
        return new LrsWSOrderResult();
    }

    /**
     * Create an instance of {@link LrsWSCodesResult }
     * 
     */
    public LrsWSCodesResult createLrsWSCodesResult() {
        return new LrsWSCodesResult();
    }

    /**
     * Create an instance of {@link LrsWSOrder }
     * 
     */
    public LrsWSOrder createLrsWSOrder() {
        return new LrsWSOrder();
    }

    /**
     * Create an instance of {@link LrsWSPayment }
     * 
     */
    public LrsWSPayment createLrsWSPayment() {
        return new LrsWSPayment();
    }

    /**
     * Create an instance of {@link LrsWSDelivery }
     * 
     */
    public LrsWSDelivery createLrsWSDelivery() {
        return new LrsWSDelivery();
    }

    /**
     * Create an instance of {@link LrsWSAddress }
     * 
     */
    public LrsWSAddress createLrsWSAddress() {
        return new LrsWSAddress();
    }

    /**
     * Create an instance of {@link LrsWSRegisterResult }
     * 
     */
    public LrsWSRegisterResult createLrsWSRegisterResult() {
        return new LrsWSRegisterResult();
    }

    /**
     * Create an instance of {@link LrsWSStatusResult }
     * 
     */
    public LrsWSStatusResult createLrsWSStatusResult() {
        return new LrsWSStatusResult();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Cancel }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.lrs.luxtrust.lu/", name = "cancel")
    public JAXBElement<Cancel> createCancel(Cancel value) {
        return new JAXBElement<Cancel>(_Cancel_QNAME, Cancel.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CancelResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.lrs.luxtrust.lu/", name = "cancelResponse")
    public JAXBElement<CancelResponse> createCancelResponse(CancelResponse value) {
        return new JAXBElement<CancelResponse>(_CancelResponse_QNAME, CancelResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetOrder }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.lrs.luxtrust.lu/", name = "getOrder")
    public JAXBElement<GetOrder> createGetOrder(GetOrder value) {
        return new JAXBElement<GetOrder>(_GetOrder_QNAME, GetOrder.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetOrderResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.lrs.luxtrust.lu/", name = "getOrderResponse")
    public JAXBElement<GetOrderResponse> createGetOrderResponse(GetOrderResponse value) {
        return new JAXBElement<GetOrderResponse>(_GetOrderResponse_QNAME, GetOrderResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetProduct }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.lrs.luxtrust.lu/", name = "getProduct")
    public JAXBElement<GetProduct> createGetProduct(GetProduct value) {
        return new JAXBElement<GetProduct>(_GetProduct_QNAME, GetProduct.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetProductResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.lrs.luxtrust.lu/", name = "getProductResponse")
    public JAXBElement<GetProductResponse> createGetProductResponse(GetProductResponse value) {
        return new JAXBElement<GetProductResponse>(_GetProductResponse_QNAME, GetProductResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetStatus }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.lrs.luxtrust.lu/", name = "getStatus")
    public JAXBElement<GetStatus> createGetStatus(GetStatus value) {
        return new JAXBElement<GetStatus>(_GetStatus_QNAME, GetStatus.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetStatusResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.lrs.luxtrust.lu/", name = "getStatusResponse")
    public JAXBElement<GetStatusResponse> createGetStatusResponse(GetStatusResponse value) {
        return new JAXBElement<GetStatusResponse>(_GetStatusResponse_QNAME, GetStatusResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetVersion }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.lrs.luxtrust.lu/", name = "getVersion")
    public JAXBElement<GetVersion> createGetVersion(GetVersion value) {
        return new JAXBElement<GetVersion>(_GetVersion_QNAME, GetVersion.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetVersionResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.lrs.luxtrust.lu/", name = "getVersionResponse")
    public JAXBElement<GetVersionResponse> createGetVersionResponse(GetVersionResponse value) {
        return new JAXBElement<GetVersionResponse>(_GetVersionResponse_QNAME, GetVersionResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Register }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.lrs.luxtrust.lu/", name = "register")
    public JAXBElement<Register> createRegister(Register value) {
        return new JAXBElement<Register>(_Register_QNAME, Register.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegisterResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.lrs.luxtrust.lu/", name = "registerResponse")
    public JAXBElement<RegisterResponse> createRegisterResponse(RegisterResponse value) {
        return new JAXBElement<RegisterResponse>(_RegisterResponse_QNAME, RegisterResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendCodes }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.lrs.luxtrust.lu/", name = "sendCodes")
    public JAXBElement<SendCodes> createSendCodes(SendCodes value) {
        return new JAXBElement<SendCodes>(_SendCodes_QNAME, SendCodes.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendCodesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://ws.lrs.luxtrust.lu/", name = "sendCodesResponse")
    public JAXBElement<SendCodesResponse> createSendCodesResponse(SendCodesResponse value) {
        return new JAXBElement<SendCodesResponse>(_SendCodesResponse_QNAME, SendCodesResponse.class, null, value);
    }

}
