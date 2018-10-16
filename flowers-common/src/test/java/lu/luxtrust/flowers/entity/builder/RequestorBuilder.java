package lu.luxtrust.flowers.entity.builder;

import lu.luxtrust.flowers.entity.system.Requestor;
import lu.luxtrust.flowers.entity.system.RequestorConfiguration;

public class RequestorBuilder {
    private Long id;
    private String name = "dg sante";
    private RequestorConfiguration config = RequestorConfigurationBuilder.newBuilder().build();
    private Requestor target;
    private String ssn;

    private RequestorBuilder() {
    }

    public static RequestorBuilder newBuilder() {
        return new RequestorBuilder();
    }

    public RequestorBuilder config(RequestorConfiguration config) {
        this.config = config;
        return this;
    }

    public RequestorBuilder ssn(String ssn) {
        this.ssn = ssn;
        return this;
    }

    public RequestorBuilder name(String name) {
        this.name = name;
        return this;
    }

    public RequestorBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public Requestor build() {
        this.target = new Requestor();
        this.target.setId(id);
        this.target.setName(name);
        this.target.setConfig(config);
        this.target.setCsn(ssn);
        return this.target;
    }
}
