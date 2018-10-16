package lu.luxtrust.flowers.model;

public class SMS {

    public enum Status {
        SENT, FAILED, NEW
    }

    private String content;
    private String mobileNumber;
    private String subject;
    private Status status = Status.NEW;

    public SMS(String content, String mobileNumber, String subject) {
        this.content = content;
        this.mobileNumber = mobileNumber;
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SMS{" +
                "content='" + content + '\'' +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", subject='" + subject + '\'' +
                ", status=" + status +
                '}';
    }
}
