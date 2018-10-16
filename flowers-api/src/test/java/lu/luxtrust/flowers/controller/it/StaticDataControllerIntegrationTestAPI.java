package lu.luxtrust.flowers.controller.it;

import io.restassured.http.ContentType;
import lu.luxtrust.flowers.entity.system.Requestor;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.*;

public class StaticDataControllerIntegrationTestAPI extends AbstractIntegrationTestAPI {

    @Test
    public void getCountries() {
        List<String> countries = entityManager.createQuery("select c.countryCode from Country c", String.class).getResultList();

        when().
                get("/api/static/countries").
        then().
                statusCode(HttpStatus.OK.value()).
                contentType(ContentType.JSON).
                body("size()", equalTo(countries.size())).
                body("countryCode", hasItems(countries.toArray(new String[countries.size()])));
    }

    @Test
    public void getNationalities() {
        List<String> nationalities = entityManager.createQuery("select n.nationalityCode from Nationality n", String.class).getResultList();

        when().
                get("/api/static/nationalities").
        then().
                statusCode(HttpStatus.OK.value()).
                contentType(ContentType.JSON).
                body("size()", equalTo(nationalities.size())).
                body("nationalityCode", hasItems(nationalities.toArray(new String[nationalities.size()])));
    }

    @Test
    public void getServices() {
        List<String> services = entityManager.createQuery("select s.name from Requestor s", String.class).getResultList();

        when().
                get("/api/static/services").
        then().
                statusCode(HttpStatus.OK.value()).
                contentType(ContentType.JSON).
                body("size()", equalTo(services.size())).
                body("name", hasItems(services.toArray(new String[services.size()])));
    }

    @Test
    public void getLanguages() {
        List<String> langs = entityManager.createQuery("select l.twoCharsCode from Language l", String.class).getResultList();

        when().
                get("/api/static/languages").
        then().
                statusCode(HttpStatus.OK.value()).
                contentType(ContentType.JSON).
                body("size()", equalTo(langs.size())).
                body("twoCharsCode", hasItems(langs.toArray(new String[langs.size()])));
    }

    @Test
    public void getConfiguration() {
        Requestor service = entityManager.createQuery("select s from Requestor s", Requestor.class).getResultList().get(0);

        when().
                get("/api/static/services/config/" + service.getId()).
        then().
                statusCode(HttpStatus.OK.value()).
                contentType(ContentType.JSON).
                body("id", equalTo(service.getConfig().getId().intValue())).
                body("shortFlow", is(service.getConfig().getShortFlow())).
                body("remoteId", is(service.getConfig().getRemoteId()));
    }
}
