
package lu.luxtrust.flowers.integration.lrs.ws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LrsWSOrderStatusEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="LrsWSOrderStatusEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="WAITING"/&gt;
 *     &lt;enumeration value="PRODUCED"/&gt;
 *     &lt;enumeration value="ONGOING"/&gt;
 *     &lt;enumeration value="CANCELED"/&gt;
 *     &lt;enumeration value="ACTIVATED"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "LrsWSOrderStatusEnum")
@XmlEnum
public enum LrsWSOrderStatusEnum {

    WAITING,
    PRODUCED,
    ONGOING,
    CANCELED,
    ACTIVATED;

    public String value() {
        return name();
    }

    public static LrsWSOrderStatusEnum fromValue(String v) {
        return valueOf(v);
    }

}
