
package lu.luxtrust.flowers.integration.lrs.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LrsWSOrganisation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LrsWSOrganisation"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Country" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Rcsl" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Tva" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Other" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Department" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LrsWSOrganisation", propOrder = {
    "name",
    "country",
    "rcsl",
    "tva",
    "other",
    "department"
})
public class LrsWSOrganisation {

    @XmlElement(name = "Name", required = true)
    protected String name;
    @XmlElement(name = "Country", required = true)
    protected String country;
    @XmlElement(name = "Rcsl")
    protected String rcsl;
    @XmlElement(name = "Tva")
    protected String tva;
    @XmlElement(name = "Other")
    protected String other;
    @XmlElement(name = "Department")
    protected String department;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the country property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the value of the country property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountry(String value) {
        this.country = value;
    }

    /**
     * Gets the value of the rcsl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRcsl() {
        return rcsl;
    }

    /**
     * Sets the value of the rcsl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRcsl(String value) {
        this.rcsl = value;
    }

    /**
     * Gets the value of the tva property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTva() {
        return tva;
    }

    /**
     * Sets the value of the tva property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTva(String value) {
        this.tva = value;
    }

    /**
     * Gets the value of the other property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOther() {
        return other;
    }

    /**
     * Sets the value of the other property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOther(String value) {
        this.other = value;
    }

    /**
     * Gets the value of the department property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDepartment() {
        return department;
    }

    /**
     * Sets the value of the department property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDepartment(String value) {
        this.department = value;
    }

}
