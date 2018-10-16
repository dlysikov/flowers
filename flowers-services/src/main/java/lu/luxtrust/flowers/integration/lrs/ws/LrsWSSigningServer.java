
package lu.luxtrust.flowers.integration.lrs.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LrsWSSigningServer complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LrsWSSigningServer"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Subject" type="{http://ws.lrs.luxtrust.lu/}LrsWSSubject" minOccurs="0"/&gt;
 *         &lt;element name="DeviceType" type="{http://ws.lrs.luxtrust.lu/}LrsWSDeviceTypeEnum"/&gt;
 *         &lt;element name="DeviceSerialNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LrsWSSigningServer", propOrder = {
    "subject",
    "deviceType",
    "deviceSerialNumber"
})
public class LrsWSSigningServer {

    @XmlElement(name = "Subject")
    protected LrsWSSubject subject;
    @XmlElement(name = "DeviceType", required = true)
    @XmlSchemaType(name = "string")
    protected LrsWSDeviceTypeEnum deviceType;
    @XmlElement(name = "DeviceSerialNumber")
    protected String deviceSerialNumber;

    /**
     * Gets the value of the subject property.
     * 
     * @return
     *     possible object is
     *     {@link LrsWSSubject }
     *     
     */
    public LrsWSSubject getSubject() {
        return subject;
    }

    /**
     * Sets the value of the subject property.
     * 
     * @param value
     *     allowed object is
     *     {@link LrsWSSubject }
     *     
     */
    public void setSubject(LrsWSSubject value) {
        this.subject = value;
    }

    /**
     * Gets the value of the deviceType property.
     * 
     * @return
     *     possible object is
     *     {@link LrsWSDeviceTypeEnum }
     *     
     */
    public LrsWSDeviceTypeEnum getDeviceType() {
        return deviceType;
    }

    /**
     * Sets the value of the deviceType property.
     * 
     * @param value
     *     allowed object is
     *     {@link LrsWSDeviceTypeEnum }
     *     
     */
    public void setDeviceType(LrsWSDeviceTypeEnum value) {
        this.deviceType = value;
    }

    /**
     * Gets the value of the deviceSerialNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeviceSerialNumber() {
        return deviceSerialNumber;
    }

    /**
     * Sets the value of the deviceSerialNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeviceSerialNumber(String value) {
        this.deviceSerialNumber = value;
    }

}
