package lu.luxtrust.flowers.entity.builder;

import lu.luxtrust.flowers.entity.system.Requestor;
import lu.luxtrust.flowers.entity.system.ResponseAPI;
import lu.luxtrust.flowers.enums.StatusAPI;

import java.math.BigInteger;

public class ResponseApiBuilder {

    private String userExternalId;
    private String serviceSSN;
    private StatusAPI statusAPI;
    private Requestor requestor;
    private ResponseAPI target;


    public static ResponseApiBuilder newBuilder() {
        return new ResponseApiBuilder();
    }

    public ResponseApiBuilder requestor(Requestor requestor) {
        this.requestor = requestor;
        return this;
    }

    public ResponseApiBuilder userExternalId(String userExternalId) {
        this.userExternalId = userExternalId;
        return this;
    }

    public ResponseApiBuilder serviceSSN(String serviceSSN) {
        this.serviceSSN = serviceSSN;
        return this;
    }

    public ResponseApiBuilder status(StatusAPI statusAPI) {
        this.statusAPI = statusAPI;
        return this;
    }

    public ResponseAPI build() {
        this.target = new ResponseAPI();
        this.target.setUserExternalId(userExternalId);
        this.target.setServiceSSN(serviceSSN);
        this.target.setStatus(statusAPI);
        this.target.setRequestor(requestor);
        return this.target;
    }
}
