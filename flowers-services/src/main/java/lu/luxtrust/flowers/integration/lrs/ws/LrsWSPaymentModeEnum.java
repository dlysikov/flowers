
package lu.luxtrust.flowers.integration.lrs.ws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LrsWSPaymentModeEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="LrsWSPaymentModeEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="VOUCHER"/&gt;
 *     &lt;enumeration value="LRA"/&gt;
 *     &lt;enumeration value="NPAY"/&gt;
 *     &lt;enumeration value="ONLINE"/&gt;
 *     &lt;enumeration value="DOMICILIATION"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "LrsWSPaymentModeEnum")
@XmlEnum
public enum LrsWSPaymentModeEnum {

    VOUCHER,
    LRA,
    NPAY,
    ONLINE,
    DOMICILIATION;

    public String value() {
        return name();
    }

    public static LrsWSPaymentModeEnum fromValue(String v) {
        return valueOf(v);
    }

}
