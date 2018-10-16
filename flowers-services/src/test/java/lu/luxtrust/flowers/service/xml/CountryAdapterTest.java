package lu.luxtrust.flowers.service.xml;

import lu.luxtrust.flowers.entity.common.Country;
import lu.luxtrust.flowers.entity.builder.CountryBuilder;
import lu.luxtrust.flowers.xml.adapter.CountryAdapter;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CountryAdapterTest {

    private CountryAdapter target;

    @Before
    public void init() {
        this.target = new CountryAdapter();
    }


    @Test
    public void unmarshal() {
        Country ua = target.unmarshal("ua");
        assertThat(ua).isNotNull();
        assertThat(ua.getCountryCode()).isEqualTo("ua");
    }

    @Test
    public void marshal() {
        Country ua = CountryBuilder.newBuilder().countryCode("ua").build();
        assertThat(target.marshal(ua)).isEqualTo("ua");
    }
}