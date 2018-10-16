package lu.luxtrust.flowers.model;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

public class ESealCredentials {
    @NotEmpty
    private String sealId;
    @NotEmpty
    private String initialPassword;
    @NotEmpty
    @Length(min = 6)
    private String newPassword;
    private String keyId;

    public String getSealId() {
        return sealId;
    }

    public void setSealID(String sealID) {
        this.sealId = sealId;
    }

    public String getInitialPassword() {
        return initialPassword;
    }

    public void setInitialPassword(String initialPassword) {
        this.initialPassword = initialPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }
}
