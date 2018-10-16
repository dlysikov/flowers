
package lu.luxtrust.flowers.integration.lrs.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for register complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="register"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ra" type="{http://ws.lrs.luxtrust.lu/}LrsWSRegistrationAuthority" minOccurs="0"/&gt;
 *         &lt;element name="order" type="{http://ws.lrs.luxtrust.lu/}LrsWSOrder" minOccurs="0"/&gt;
 *         &lt;element name="rao" type="{http://ws.lrs.luxtrust.lu/}LrsWSSignature" minOccurs="0"/&gt;
 *         &lt;element name="version" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "register", propOrder = {
    "ra",
    "order",
    "rao",
    "version"
})
public class Register {

    protected LrsWSRegistrationAuthority ra;
    protected LrsWSOrder order;
    protected LrsWSSignature rao;
    protected String version;

    /**
     * Gets the value of the ra property.
     * 
     * @return
     *     possible object is
     *     {@link LrsWSRegistrationAuthority }
     *     
     */
    public LrsWSRegistrationAuthority getRa() {
        return ra;
    }

    /**
     * Sets the value of the ra property.
     * 
     * @param value
     *     allowed object is
     *     {@link LrsWSRegistrationAuthority }
     *     
     */
    public void setRa(LrsWSRegistrationAuthority value) {
        this.ra = value;
    }

    /**
     * Gets the value of the order property.
     * 
     * @return
     *     possible object is
     *     {@link LrsWSOrder }
     *     
     */
    public LrsWSOrder getOrder() {
        return order;
    }

    /**
     * Sets the value of the order property.
     * 
     * @param value
     *     allowed object is
     *     {@link LrsWSOrder }
     *     
     */
    public void setOrder(LrsWSOrder value) {
        this.order = value;
    }

    /**
     * Gets the value of the rao property.
     * 
     * @return
     *     possible object is
     *     {@link LrsWSSignature }
     *     
     */
    public LrsWSSignature getRao() {
        return rao;
    }

    /**
     * Sets the value of the rao property.
     * 
     * @param value
     *     allowed object is
     *     {@link LrsWSSignature }
     *     
     */
    public void setRao(LrsWSSignature value) {
        this.rao = value;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersion(String value) {
        this.version = value;
    }

}
