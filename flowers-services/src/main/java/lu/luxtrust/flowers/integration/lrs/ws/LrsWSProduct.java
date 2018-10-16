
package lu.luxtrust.flowers.integration.lrs.ws;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for LrsWSProduct complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LrsWSProduct"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Profile" type="{http://ws.lrs.luxtrust.lu/}LrsWSProfileEnum"/&gt;
 *         &lt;element name="SubjectSerialNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="InitialOrder" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="LastOrder" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="SigningServer" type="{http://ws.lrs.luxtrust.lu/}LrsWSSigningServer" minOccurs="0"/&gt;
 *         &lt;element name="Certificate" type="{http://ws.lrs.luxtrust.lu/}LrsWSCertificate" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="Publish" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LrsWSProduct", propOrder = {
    "profile",
    "subjectSerialNumber",
    "initialOrder",
    "lastOrder",
    "signingServer",
    "certificate",
    "publish"
})
public class LrsWSProduct {

    @XmlElement(name = "Profile", required = true)
    @XmlSchemaType(name = "string")
    protected LrsWSProfileEnum profile;
    @XmlElement(name = "SubjectSerialNumber")
    protected String subjectSerialNumber;
    @XmlElement(name = "InitialOrder")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar initialOrder;
    @XmlElement(name = "LastOrder")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastOrder;
    @XmlElement(name = "SigningServer")
    protected LrsWSSigningServer signingServer;
    @XmlElement(name = "Certificate")
    protected List<LrsWSCertificate> certificate;
    @XmlElement(name = "Publish")
    protected boolean publish;

    /**
     * Gets the value of the profile property.
     * 
     * @return
     *     possible object is
     *     {@link LrsWSProfileEnum }
     *     
     */
    public LrsWSProfileEnum getProfile() {
        return profile;
    }

    /**
     * Sets the value of the profile property.
     * 
     * @param value
     *     allowed object is
     *     {@link LrsWSProfileEnum }
     *     
     */
    public void setProfile(LrsWSProfileEnum value) {
        this.profile = value;
    }

    /**
     * Gets the value of the subjectSerialNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubjectSerialNumber() {
        return subjectSerialNumber;
    }

    /**
     * Sets the value of the subjectSerialNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubjectSerialNumber(String value) {
        this.subjectSerialNumber = value;
    }

    /**
     * Gets the value of the initialOrder property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getInitialOrder() {
        return initialOrder;
    }

    /**
     * Sets the value of the initialOrder property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setInitialOrder(XMLGregorianCalendar value) {
        this.initialOrder = value;
    }

    /**
     * Gets the value of the lastOrder property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLastOrder() {
        return lastOrder;
    }

    /**
     * Sets the value of the lastOrder property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLastOrder(XMLGregorianCalendar value) {
        this.lastOrder = value;
    }

    /**
     * Gets the value of the signingServer property.
     * 
     * @return
     *     possible object is
     *     {@link LrsWSSigningServer }
     *     
     */
    public LrsWSSigningServer getSigningServer() {
        return signingServer;
    }

    /**
     * Sets the value of the signingServer property.
     * 
     * @param value
     *     allowed object is
     *     {@link LrsWSSigningServer }
     *     
     */
    public void setSigningServer(LrsWSSigningServer value) {
        this.signingServer = value;
    }

    /**
     * Gets the value of the certificate property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the certificate property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCertificate().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LrsWSCertificate }
     * 
     * 
     */
    public List<LrsWSCertificate> getCertificate() {
        if (certificate == null) {
            certificate = new ArrayList<LrsWSCertificate>();
        }
        return this.certificate;
    }

    /**
     * Gets the value of the publish property.
     * 
     */
    public boolean isPublish() {
        return publish;
    }

    /**
     * Sets the value of the publish property.
     * 
     */
    public void setPublish(boolean value) {
        this.publish = value;
    }

}
