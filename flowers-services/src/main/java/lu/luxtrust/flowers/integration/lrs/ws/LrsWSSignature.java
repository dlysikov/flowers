
package lu.luxtrust.flowers.integration.lrs.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LrsWSSignature complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LrsWSSignature"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Text" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Language" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Certificate" type="{http://ws.lrs.luxtrust.lu/}LrsWSCertificate"/&gt;
 *         &lt;element name="Encoding" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LrsWSSignature", propOrder = {
    "text",
    "language",
    "certificate",
    "encoding"
})
public class LrsWSSignature {

    @XmlElement(name = "Text", required = true)
    protected String text;
    @XmlElement(name = "Language", required = true, defaultValue = "FR")
    protected String language;
    @XmlElement(name = "Certificate", required = true)
    protected LrsWSCertificate certificate;
    @XmlElement(name = "Encoding", required = true)
    protected byte[] encoding;

    /**
     * Gets the value of the text property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the value of the text property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setText(String value) {
        this.text = value;
    }

    /**
     * Gets the value of the language property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the value of the language property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLanguage(String value) {
        this.language = value;
    }

    /**
     * Gets the value of the certificate property.
     * 
     * @return
     *     possible object is
     *     {@link LrsWSCertificate }
     *     
     */
    public LrsWSCertificate getCertificate() {
        return certificate;
    }

    /**
     * Sets the value of the certificate property.
     * 
     * @param value
     *     allowed object is
     *     {@link LrsWSCertificate }
     *     
     */
    public void setCertificate(LrsWSCertificate value) {
        this.certificate = value;
    }

    /**
     * Gets the value of the encoding property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getEncoding() {
        return encoding;
    }

    /**
     * Sets the value of the encoding property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setEncoding(byte[] value) {
        this.encoding = value;
    }

}
