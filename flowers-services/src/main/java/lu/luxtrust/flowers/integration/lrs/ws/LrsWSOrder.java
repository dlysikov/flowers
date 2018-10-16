
package lu.luxtrust.flowers.integration.lrs.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LrsWSOrder complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LrsWSOrder"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Number" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Requestor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="CertLevel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Archive" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="SignedGTC" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="Payment" type="{http://ws.lrs.luxtrust.lu/}LrsWSPayment"/&gt;
 *         &lt;element name="Product" type="{http://ws.lrs.luxtrust.lu/}LrsWSProduct"/&gt;
 *         &lt;element name="CodeChannel" type="{http://ws.lrs.luxtrust.lu/}LrsWSDelivery"/&gt;
 *         &lt;element name="ClientSignature" type="{http://ws.lrs.luxtrust.lu/}LrsWSSignature" minOccurs="0"/&gt;
 *         &lt;element name="TutorSignature" type="{http://ws.lrs.luxtrust.lu/}LrsWSSignature" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LrsWSOrder", propOrder = {
    "number",
    "requestor",
    "certLevel",
    "archive",
    "signedGTC",
    "payment",
    "product",
    "codeChannel",
    "clientSignature",
    "tutorSignature"
})
public class LrsWSOrder {

    @XmlElement(name = "Number")
    protected String number;
    @XmlElement(name = "Requestor")
    protected String requestor;
    @XmlElement(name = "CertLevel")
    protected String certLevel;
    @XmlElement(name = "Archive")
    protected String archive;
    @XmlElement(name = "SignedGTC", defaultValue = "false")
    protected boolean signedGTC;
    @XmlElement(name = "Payment", required = true)
    protected LrsWSPayment payment;
    @XmlElement(name = "Product", required = true)
    protected LrsWSProduct product;
    @XmlElement(name = "CodeChannel", required = true)
    protected LrsWSDelivery codeChannel;
    @XmlElement(name = "ClientSignature")
    protected LrsWSSignature clientSignature;
    @XmlElement(name = "TutorSignature")
    protected LrsWSSignature tutorSignature;

    /**
     * Gets the value of the number property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumber() {
        return number;
    }

    /**
     * Sets the value of the number property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumber(String value) {
        this.number = value;
    }

    /**
     * Gets the value of the requestor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestor() {
        return requestor;
    }

    /**
     * Sets the value of the requestor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestor(String value) {
        this.requestor = value;
    }

    /**
     * Gets the value of the certLevel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCertLevel() {
        return certLevel;
    }

    /**
     * Sets the value of the certLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCertLevel(String value) {
        this.certLevel = value;
    }

    /**
     * Gets the value of the archive property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArchive() {
        return archive;
    }

    /**
     * Sets the value of the archive property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArchive(String value) {
        this.archive = value;
    }

    /**
     * Gets the value of the signedGTC property.
     * 
     */
    public boolean isSignedGTC() {
        return signedGTC;
    }

    /**
     * Sets the value of the signedGTC property.
     * 
     */
    public void setSignedGTC(boolean value) {
        this.signedGTC = value;
    }

    /**
     * Gets the value of the payment property.
     * 
     * @return
     *     possible object is
     *     {@link LrsWSPayment }
     *     
     */
    public LrsWSPayment getPayment() {
        return payment;
    }

    /**
     * Sets the value of the payment property.
     * 
     * @param value
     *     allowed object is
     *     {@link LrsWSPayment }
     *     
     */
    public void setPayment(LrsWSPayment value) {
        this.payment = value;
    }

    /**
     * Gets the value of the product property.
     * 
     * @return
     *     possible object is
     *     {@link LrsWSProduct }
     *     
     */
    public LrsWSProduct getProduct() {
        return product;
    }

    /**
     * Sets the value of the product property.
     * 
     * @param value
     *     allowed object is
     *     {@link LrsWSProduct }
     *     
     */
    public void setProduct(LrsWSProduct value) {
        this.product = value;
    }

    /**
     * Gets the value of the codeChannel property.
     * 
     * @return
     *     possible object is
     *     {@link LrsWSDelivery }
     *     
     */
    public LrsWSDelivery getCodeChannel() {
        return codeChannel;
    }

    /**
     * Sets the value of the codeChannel property.
     * 
     * @param value
     *     allowed object is
     *     {@link LrsWSDelivery }
     *     
     */
    public void setCodeChannel(LrsWSDelivery value) {
        this.codeChannel = value;
    }

    /**
     * Gets the value of the clientSignature property.
     * 
     * @return
     *     possible object is
     *     {@link LrsWSSignature }
     *     
     */
    public LrsWSSignature getClientSignature() {
        return clientSignature;
    }

    /**
     * Sets the value of the clientSignature property.
     * 
     * @param value
     *     allowed object is
     *     {@link LrsWSSignature }
     *     
     */
    public void setClientSignature(LrsWSSignature value) {
        this.clientSignature = value;
    }

    /**
     * Gets the value of the tutorSignature property.
     * 
     * @return
     *     possible object is
     *     {@link LrsWSSignature }
     *     
     */
    public LrsWSSignature getTutorSignature() {
        return tutorSignature;
    }

    /**
     * Sets the value of the tutorSignature property.
     * 
     * @param value
     *     allowed object is
     *     {@link LrsWSSignature }
     *     
     */
    public void setTutorSignature(LrsWSSignature value) {
        this.tutorSignature = value;
    }

}
