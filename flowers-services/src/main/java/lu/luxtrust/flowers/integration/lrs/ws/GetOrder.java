
package lu.luxtrust.flowers.integration.lrs.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getOrder complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getOrder"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ra" type="{http://ws.lrs.luxtrust.lu/}LrsWSRegistrationAuthority" minOccurs="0"/&gt;
 *         &lt;element name="ssn" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getOrder", propOrder = {
    "ra",
    "ssn"
})
public class GetOrder {

    protected LrsWSRegistrationAuthority ra;
    protected String ssn;

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
     * Gets the value of the ssn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSsn() {
        return ssn;
    }

    /**
     * Sets the value of the ssn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSsn(String value) {
        this.ssn = value;
    }

}
