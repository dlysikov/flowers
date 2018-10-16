
package lu.luxtrust.flowers.integration.lrs.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LrsWSPayment complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LrsWSPayment"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Mode" type="{http://ws.lrs.luxtrust.lu/}LrsWSPaymentModeEnum"/&gt;
 *         &lt;element name="Voucher" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LrsWSPayment", propOrder = {
    "mode",
    "voucher"
})
public class LrsWSPayment {

    @XmlElement(name = "Mode", required = true)
    @XmlSchemaType(name = "string")
    protected LrsWSPaymentModeEnum mode;
    @XmlElement(name = "Voucher")
    protected String voucher;

    /**
     * Gets the value of the mode property.
     * 
     * @return
     *     possible object is
     *     {@link LrsWSPaymentModeEnum }
     *     
     */
    public LrsWSPaymentModeEnum getMode() {
        return mode;
    }

    /**
     * Sets the value of the mode property.
     * 
     * @param value
     *     allowed object is
     *     {@link LrsWSPaymentModeEnum }
     *     
     */
    public void setMode(LrsWSPaymentModeEnum value) {
        this.mode = value;
    }

    /**
     * Gets the value of the voucher property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVoucher() {
        return voucher;
    }

    /**
     * Sets the value of the voucher property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVoucher(String value) {
        this.voucher = value;
    }

}
