
package lu.luxtrust.flowers.integration.lrs.ws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LrsWSDeviceTypeEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="LrsWSDeviceTypeEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="DPGO_6"/&gt;
 *     &lt;enumeration value="DPM"/&gt;
 *     &lt;enumeration value="DP_780"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "LrsWSDeviceTypeEnum")
@XmlEnum
public enum LrsWSDeviceTypeEnum {

    DPGO_6,
    DPM,
    DP_780;

    public String value() {
        return name();
    }

    public static LrsWSDeviceTypeEnum fromValue(String v) {
        return valueOf(v);
    }

}
