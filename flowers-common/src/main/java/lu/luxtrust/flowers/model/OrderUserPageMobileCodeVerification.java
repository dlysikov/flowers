package lu.luxtrust.flowers.model;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

public class OrderUserPageMobileCodeVerification {
    @NotEmpty
    @Length(min = 64, max = 64)
    private String pageHash;

    @NotEmpty
    @Length(min = 7, max = 7)
    private String mobileCode;

    public String getPageHash() {
        return pageHash;
    }

    public void setPageHash(String pageHash) {
        this.pageHash = pageHash;
    }

    public String getMobileCode() {
        return mobileCode;
    }

    public void setMobileCode(String mobileCode) {
        this.mobileCode = mobileCode;
    }
}
