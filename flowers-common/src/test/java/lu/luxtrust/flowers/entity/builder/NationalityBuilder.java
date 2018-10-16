package lu.luxtrust.flowers.entity.builder;

import lu.luxtrust.flowers.entity.common.Nationality;

public class NationalityBuilder {

    private Long id;
    private String nationalityCode = "ua";

    private Nationality nationality;

    private NationalityBuilder() {}

    public NationalityBuilder nationalityCode(String nationalityCode) {
        this.nationalityCode = nationalityCode;
        return this;
    }

    public NationalityBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public static NationalityBuilder newBuilder() {
        return new NationalityBuilder();
    }

    public Nationality build() {
        this.nationality = new Nationality();
        this.nationality.setNationalityCode(nationalityCode);
        this.nationality.setId(id);
        return nationality;
    }
}
