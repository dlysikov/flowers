package lu.luxtrust.flowers.controller;

import lu.luxtrust.flowers.entity.common.Country;
import lu.luxtrust.flowers.entity.common.Nationality;
import lu.luxtrust.flowers.entity.system.Requestor;
import lu.luxtrust.flowers.repository.UnitRepository;
import lu.luxtrust.flowers.service.StaticDataService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class StaticDataControllerTest {

    @Mock
    private StaticDataService staticDataService;
    @Mock
    private List<Country> countries;
    @Mock
    private UnitRepository unitRepository;
    @Mock
    private List<Nationality> nationalities;
    @Mock
    private List<Requestor> services;

    private StaticDataController target;

    @Before
    public void init() {
        this.target = new StaticDataController(staticDataService, unitRepository);
    }

    @Test
    public void getAllCountries() {
        when(staticDataService.getAllCountries()).thenReturn(countries);

        ResponseEntity<List<Country>> response = target.getAllCountries();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(countries);
    }

    @Test
    public void getAllNationalities() {
        when(staticDataService.getAllNationalities()).thenReturn(nationalities);

        ResponseEntity<List<Nationality>> response = target.getAllNationalities();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(nationalities);
    }

    @Test
    public void getAllServices() {
        when(staticDataService.getAllServices()).thenReturn(services);

        ResponseEntity<List<Requestor>> response = target.getAllServices();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(services);
    }
}