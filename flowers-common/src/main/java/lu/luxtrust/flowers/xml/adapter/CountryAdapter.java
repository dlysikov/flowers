package lu.luxtrust.flowers.xml.adapter;

import lu.luxtrust.flowers.entity.common.Country;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class CountryAdapter extends XmlAdapter<String, Country> {
    @Override
    public Country unmarshal(String v) {
        Country c = new Country();
        c.setCountryCode(v);
        return c;
    }

    @Override
    public String marshal(Country v) {
        return v.getCountryCode();
    }
}
