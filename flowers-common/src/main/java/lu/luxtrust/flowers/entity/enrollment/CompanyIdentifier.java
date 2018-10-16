package lu.luxtrust.flowers.entity.enrollment;

import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import java.util.Objects;

@Embeddable
@XmlAccessorType(XmlAccessType.FIELD)
public class CompanyIdentifier {

    public enum Type {
        VAT, RCSL, OTHER
    }

    @XmlAttribute
    @Enumerated(EnumType.STRING)
    @Column(name = "identifier_type")
    private Type type;

    @XmlAttribute
    @Length(max = 15)
    @Column(name = "identifier_value")
    private String value;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompanyIdentifier that = (CompanyIdentifier) o;
        return type == that.type &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }
}
