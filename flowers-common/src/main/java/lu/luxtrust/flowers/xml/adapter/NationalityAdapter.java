package lu.luxtrust.flowers.xml.adapter;

import lu.luxtrust.flowers.entity.common.Nationality;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class NationalityAdapter extends XmlAdapter<String, Nationality> {

    @Override
    public Nationality unmarshal(String v) {
        Nationality n = new Nationality();
        n.setNationalityCode(v);
        return n;
    }

    @Override
    public String marshal(Nationality v) {
        return v.getNationalityCode();
    }
}
