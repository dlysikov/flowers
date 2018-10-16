package lu.luxtrust.flowers.model;

import java.util.Objects;

public final class CertificateDetails {
    private String certificateType;
    private String ssn;
    private String givenName;
    private String surname;

    CertificateDetails() {
    }

    private CertificateDetails(String certificateType, String ssn, String givenName, String surname) {
        this.certificateType = certificateType;
        this.ssn = ssn;
        this.givenName = givenName;
        this.surname = surname;
    }

    public String getCertificateType() {
        return certificateType;
    }

    public String getSsn() {
        return ssn;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getSurname() {
        return surname;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private String certificateType;
        private String ssn;
        private String givenName;
        private String surname;

        public Builder certificateType(String certificateType) {
            this.certificateType = certificateType;
            return this;
        }

        public Builder ssn(String ssn) {
            this.ssn = ssn;
            return this;
        }

        public Builder givenName(String givenName) {
            this.givenName = givenName;
            return this;
        }

        public Builder surname(String surname) {
            this.surname = surname;
            return this;
        }

        public CertificateDetails build() {
            return new CertificateDetails(certificateType, ssn, givenName, surname);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CertificateDetails that = (CertificateDetails) o;
        return Objects.equals(certificateType, that.certificateType) &&
                Objects.equals(ssn, that.ssn) &&
                Objects.equals(givenName, that.givenName) &&
                Objects.equals(surname, that.surname);
    }

    @Override
    public int hashCode() {

        return Objects.hash(certificateType, ssn, givenName, surname);
    }
}
