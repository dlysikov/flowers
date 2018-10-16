
package lu.luxtrust.flowers.integration.lrs.ws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LrsWSProfileEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="LrsWSProfileEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="ESEAL"/&gt;
 *     &lt;enumeration value="CORPORATE"/&gt;
 *     &lt;enumeration value="PRIVATE"/&gt;
 *     &lt;enumeration value="PROFESSIONAL"/&gt;
 *     &lt;enumeration value="ADMINISTRATOR"/&gt;
 *     &lt;enumeration value="PRIVACY"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "LrsWSProfileEnum")
@XmlEnum
public enum LrsWSProfileEnum {

    ESEAL,
    CORPORATE,
    PRIVATE,
    PROFESSIONAL,
    ADMINISTRATOR,
    PRIVACY;

    public String value() {
        return name();
    }

    public static LrsWSProfileEnum fromValue(String v) {
        return valueOf(v);
    }

}
