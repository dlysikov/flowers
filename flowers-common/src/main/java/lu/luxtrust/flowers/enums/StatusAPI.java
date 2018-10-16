package lu.luxtrust.flowers.enums;

public enum StatusAPI {

    ORDER_SAVED("ORDER_SAVED", "CertificateOrder was successfully saved"),
    WAITING_FOR_DOCUMENTS("WAITING_FOR_DOCUMENTS", "CertificateOrder was successfully saved with waiting for documents status"),
    DOCUMENTS_LOADED("DOCUMENTS_LOADED", "CertificateOrder was successfully saved and all documents are loaded"),
    DUPLICATE("DUPLICATE", "CertificateOrder was not saved due to duplicate value of  First Name, Surname and Notify Email"),
    ORDER_SAVING_ERROR("ORDER_SAVING_ERROR", "CertificateOrder was not saved due to internal server errors"),
    ORDER_ILLEGAL_STATUS("ORDER_ILLEGAL_STATUS", "CertificateOrder has already illegal status for uploading documents"),
    ORDER_NOT_FOUND("ORDER_NOT_FOUND", "CertificateOrder is not found for current user external id"),
    ACTIVATED("ACTIVATED", "Account is activated"),
    RESPONSE_SENDING_ERROR("RESPONSE_SENDING_ERROR", "Response sending was not finished successfully"),
    RESPONSE_SERVER_ERROR("RESPONSE_SERVER_ERROR", "Response sending was not finished successfully due to server error");

    private String code;
    private String msg;

    StatusAPI(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
