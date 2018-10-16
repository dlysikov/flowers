package lu.luxtrust.flowers.security.orely;

import java.math.BigInteger;
import java.util.Objects;

public final class OrelyTrustedCertificate {

    private final String issuer;
    private final BigInteger certificateSerialNumber;

    public OrelyTrustedCertificate(String issuer, BigInteger certificateSerialNumber) {
        this.issuer = issuer;
        this.certificateSerialNumber = certificateSerialNumber;
    }

    public String getIssuer() {
        return issuer;
    }

    public BigInteger getCertificateSerialNumber() {
        return certificateSerialNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrelyTrustedCertificate that = (OrelyTrustedCertificate) o;
        return Objects.equals(issuer, that.issuer) &&
                Objects.equals(certificateSerialNumber, that.certificateSerialNumber);
    }

    @Override
    public int hashCode() {

        return Objects.hash(issuer, certificateSerialNumber);
    }
}
