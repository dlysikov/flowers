package lu.luxtrust.flowers.service.impl;

import org.junit.Test;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class OrderRandomsGeneratorGeneratorImplTest {

    private OrderRandomsGeneratorGeneratorImpl target = new OrderRandomsGeneratorGeneratorImpl("file:/dev/urandom");

    @Test
    public void hash() {
        assertThat(target.hash().length()).isEqualTo(64);
    }

    @Test
    public void codeWithDigits() {
        int length = 6;
        String result = target.codeWithDigits(length);
        assertThat(result.length()).isEqualTo(length);
        for (int i = 0;i < length;i++) {
            assertThat(Integer.parseInt(String.valueOf(result.charAt(i)))).isBetween(0, 9);
        }
    }

}