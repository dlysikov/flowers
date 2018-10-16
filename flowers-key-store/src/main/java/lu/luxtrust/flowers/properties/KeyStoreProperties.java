package lu.luxtrust.flowers.properties;


import lu.luxtrust.flowers.properties.validation.KeyStorePropsFilled;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.validation.annotation.Validated;

@Validated
@KeyStorePropsFilled
public class KeyStoreProperties {

    public enum KeyStoreSpiType {
        FILE, HSM, NONE
    }

    private KeyStoreSpiType type;

    private HSMProps hsm;

    private FileProps file;

    public KeyStoreSpiType getType() {
        return type;
    }

    public void setType(KeyStoreSpiType type) {
        this.type = type;
    }

    public HSMProps getHsm() {
        return hsm;
    }

    public void setHsm(HSMProps hsm) {
        this.hsm = hsm;
    }

    public FileProps getFile() {
        return file;
    }

    public void setFile(FileProps file) {
        this.file = file;
    }

    public static class HSMProps {
        @NotEmpty
        private String tokenLabel;

        @NotEmpty
        private String password;

        public String getTokenLabel() {
            return tokenLabel;
        }

        public void setTokenLabel(String slotNumber) {
            this.tokenLabel = slotNumber;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class FileProps {
        @NotEmpty
        private String keystoreType;
        @NotEmpty
        private String keystorePath;
        @NotEmpty
        private String keystorePassword;

        public String getKeystoreType() {
            return keystoreType;
        }

        public void setKeystoreType(String keystoreType) {
            this.keystoreType = keystoreType;
        }

        public String getKeystorePath() {
            return keystorePath;
        }

        public void setKeystorePath(String keystorePath) {
            this.keystorePath = keystorePath;
        }

        public String getKeystorePassword() {
            return keystorePassword;
        }

        public void setKeystorePassword(String keystorePassword) {
            this.keystorePassword = keystorePassword;
        }
    }
}

