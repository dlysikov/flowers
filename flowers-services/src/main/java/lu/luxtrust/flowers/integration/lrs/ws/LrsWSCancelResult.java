
package lu.luxtrust.flowers.integration.lrs.ws;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LrsWSCancelResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LrsWSCancelResult"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;element name="canceled" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *           &lt;element name="ErrorMessage" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LrsWSCancelResult", propOrder = {
    "canceledOrErrorMessage"
})
public class LrsWSCancelResult {

    @XmlElements({
        @XmlElement(name = "canceled", type = Boolean.class),
        @XmlElement(name = "ErrorMessage", type = String.class)
    })
    protected List<Serializable> canceledOrErrorMessage;

    /**
     * Gets the value of the canceledOrErrorMessage property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the canceledOrErrorMessage property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCanceledOrErrorMessage().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Boolean }
     * {@link String }
     * 
     * 
     */
    public List<Serializable> getCanceledOrErrorMessage() {
        if (canceledOrErrorMessage == null) {
            canceledOrErrorMessage = new ArrayList<Serializable>();
        }
        return this.canceledOrErrorMessage;
    }

}
