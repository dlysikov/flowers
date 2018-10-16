package lu.luxtrust.flowers.service.util;

import lu.luxtrust.flowers.entity.builder.CountryBuilder;
import lu.luxtrust.flowers.entity.common.Country;
import lu.luxtrust.flowers.entity.common.CountryVatConfig;
import lu.luxtrust.flowers.repository.CountryVatConfigRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CountryVatConfigHolderTest {

    private static final Country UA = CountryBuilder.newBuilder().countryCode("ua").build();
    private static final Country LU = CountryBuilder.newBuilder().countryCode("lu").build();

    @Mock private CountryVatConfigRepository repository;

    @Test
    public void test() {
        CountryVatConfig config = new CountryVatConfig();
        config.setCountry(UA);
        config.setVatPattern("pattern");
        when(repository.findAll()).thenReturn(Collections.singletonList(config));

        CountryVatConfigHolder holder = new CountryVatConfigHolder(repository);

        assertThat(holder.isRcslValid(UA)).isTrue();
        assertThat(holder.isRcslValid(LU)).isFalse();

        assertThat(holder.getVatPattern("ua")).isNotNull();
        assertThat(holder.getVatPattern("lu")).isNull();
    }

}