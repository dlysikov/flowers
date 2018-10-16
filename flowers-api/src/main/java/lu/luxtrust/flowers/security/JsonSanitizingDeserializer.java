package lu.luxtrust.flowers.security;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.boot.jackson.JsonComponent;
import org.thymeleaf.util.StringUtils;

import java.io.IOException;

@JsonComponent
public class JsonSanitizingDeserializer extends JsonDeserializer<String> implements ContextualDeserializer {
    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String value = jsonParser.getValueAsString();
        if (StringUtils.isEmpty(value)) {
            return value;
        } else {
            return Jsoup.clean(value, Whitelist.basic());
        }
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) {
        return this;
    }
}
