package lu.luxtrust.flowers.service.xml;

import lu.luxtrust.flowers.entity.common.Nationality;
import lu.luxtrust.flowers.entity.builder.NationalityBuilder;
import lu.luxtrust.flowers.xml.adapter.NationalityAdapter;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NationalityAdapterTest {

    private NationalityAdapter target;

    @Before
    public void init() {
        this.target = new NationalityAdapter();
    }

    @Test
    public void unmarshal() {
        Nationality ua = target.unmarshal("ua");
        assertThat(ua).isNotNull();
        assertThat(ua.getNationalityCode()).isEqualTo("ua");
    }

    @Test
    public void marshal() {
        Nationality ua = NationalityBuilder.newBuilder().nationalityCode("ua").build();
        assertThat(target.marshal(ua)).isEqualTo("ua");
    }
}