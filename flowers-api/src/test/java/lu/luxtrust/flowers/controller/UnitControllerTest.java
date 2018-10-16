package lu.luxtrust.flowers.controller;

import lu.luxtrust.flowers.entity.enrollment.CompanyIdentifier;
import lu.luxtrust.flowers.entity.enrollment.Unit;
import lu.luxtrust.flowers.entity.system.Requestor;
import lu.luxtrust.flowers.exception.FlowersException;
import lu.luxtrust.flowers.model.FieldFilter;
import lu.luxtrust.flowers.model.PageParams;
import lu.luxtrust.flowers.model.PageResponse;
import lu.luxtrust.flowers.repository.UnitRepository;
import lu.luxtrust.flowers.security.RestAuthenticationToken;
import lu.luxtrust.flowers.service.UnitService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UnitControllerTest {

    private static final Long UNIT_ID = 2L;
    private static final Long AUTH_ID = 1L;
    private static final CompanyIdentifier.Type COMPANY_IDENTIFIER_TYPE = CompanyIdentifier.Type.OTHER;
    private static final String COMPANY_IDENTIFIER_VALUE = "111111";

    @Mock
    private UnitService unitService;
    @Mock
    private UnitRepository unitRepository;
    @Mock
    private RestAuthenticationToken authentication;
    @Mock
    private Requestor requestor;
    @Mock
    private CompanyIdentifier companyIdentifier;
    @Mock
    private Unit unit;
    @Mock
    private PageParams pageParams;
    @Mock
    private List<FieldFilter> filters;

    private UnitController target;

    @Before
    public void init() {
        this.target = new UnitController(unitService, unitRepository);
        when(unit.getRequestor()).thenReturn(requestor);
        when(authentication.getId()).thenReturn(AUTH_ID);
        when(unit.getIdentifier()).thenReturn(companyIdentifier);
        when(companyIdentifier.getValue()).thenReturn(COMPANY_IDENTIFIER_VALUE);
        when(companyIdentifier.getType()).thenReturn(COMPANY_IDENTIFIER_TYPE);
        when(unit.getId()).thenReturn(UNIT_ID);
    }

    @Test
    public void findAllForAuthenticated() {
        when(unitService.findAll(any(PageParams.class))).thenReturn(Collections.emptyList());
        when(unitService.count(filters)).thenReturn(0L);
        when(pageParams.getFilter()).thenReturn(filters);

        ResponseEntity<PageResponse<Unit>> response = target.findAllForAuthenticated(authentication, pageParams);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).isSameAs(Collections.emptyList());
        assertThat(response.getBody().getTotalCount()).isEqualTo(0L);
        verify(unitService).findAll(pageParams);
        verify(unitService).count(filters);
    }

    @Test(expected = FlowersException.class)
    public void createUnit_duplicate() {
        when(unitRepository.existsByIdentifierTypeAndIdentifierValue(COMPANY_IDENTIFIER_TYPE, COMPANY_IDENTIFIER_VALUE)).thenReturn(Boolean.TRUE);
        target.createUnit(unit, authentication);
    }

    @Test
    public void createUnit() {
        when(unitRepository.save(unit)).thenReturn(unit);
        ResponseEntity<Unit> response = target.createUnit(this.unit, authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(unit);

        verify(unitRepository).existsByIdentifierTypeAndIdentifierValue(COMPANY_IDENTIFIER_TYPE, COMPANY_IDENTIFIER_VALUE);
        verify(unitRepository).save(unit);
    }

    @Test
    public void isModifiable_notFound() {
        when(unitRepository.exists(UNIT_ID)).thenReturn(Boolean.FALSE);
        ResponseEntity<Boolean> response = target.isModifiable(UNIT_ID, authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(unitRepository).exists(UNIT_ID);
        verify(unitService, never()).isValuableDataModifiable(UNIT_ID);
    }

    @Test
    public void isModifiable() {
        when(unitService.isValuableDataModifiable(UNIT_ID)).thenReturn(Boolean.TRUE);
        when(unitRepository.exists(UNIT_ID)).thenReturn(Boolean.TRUE);
        ResponseEntity<Boolean> response = target.isModifiable(UNIT_ID, authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isTrue();
        verify(unitRepository).exists(UNIT_ID);
        verify(unitService).isValuableDataModifiable(UNIT_ID);
    }

    @Test(expected = FlowersException.class)
    public void updateUnit_Duplicate() {
        when(unitRepository.existsByIdentifierTypeAndIdentifierValueAndIdNot(COMPANY_IDENTIFIER_TYPE, COMPANY_IDENTIFIER_VALUE, UNIT_ID)).thenReturn(Boolean.TRUE);
        target.updateUnit(UNIT_ID, unit, authentication);
    }

    @Test(expected = FlowersException.class)
    public void updateUnit_NotModifiable() {
        when(unitRepository.existsByIdentifierTypeAndIdentifierValueAndIdNot(COMPANY_IDENTIFIER_TYPE, COMPANY_IDENTIFIER_VALUE, UNIT_ID)).thenReturn(Boolean.FALSE);
        when(unitService.changesCanBeApplied(unit)).thenReturn(Boolean.FALSE);
        target.updateUnit(UNIT_ID, unit, authentication);
    }

    @Test
    public void updateUnit() {
        when(unitRepository.existsByIdentifierTypeAndIdentifierValueAndIdNot(COMPANY_IDENTIFIER_TYPE, COMPANY_IDENTIFIER_VALUE, UNIT_ID)).thenReturn(Boolean.FALSE);
        when(unitService.changesCanBeApplied(unit)).thenReturn(Boolean.TRUE);
        when(unitRepository.save(unit)).thenReturn(unit);
        ResponseEntity<Unit> response = target.updateUnit(UNIT_ID, unit, authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(unit);
        verify(unitRepository).existsByIdentifierTypeAndIdentifierValueAndIdNot(COMPANY_IDENTIFIER_TYPE, COMPANY_IDENTIFIER_VALUE, UNIT_ID);
        verify(unitService).changesCanBeApplied(unit);
        verify(unitRepository).save(unit);
    }

    @Test(expected = FlowersException.class)
    public void deleteUnit_NotPossible() {
        when(unitRepository.exists(UNIT_ID)).thenReturn(Boolean.TRUE);
        when(unitService.canBeDeleted(UNIT_ID)).thenReturn(Boolean.FALSE);
        target.deleteUnit(UNIT_ID, authentication);
    }

    @Test
    public void deletetUnit() {
        when(unitRepository.exists(UNIT_ID)).thenReturn(Boolean.TRUE);
        when(unitService.canBeDeleted(UNIT_ID)).thenReturn(Boolean.TRUE);
        ResponseEntity response = target.deleteUnit(UNIT_ID, authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();

        verify(unitRepository).exists(UNIT_ID);
        verify(unitService).canBeDeleted(UNIT_ID);
        verify(unitRepository).delete(UNIT_ID);
    }

    @Test
    public void deleteUnit_NotFound() {
        when(unitRepository.exists(UNIT_ID)).thenReturn(Boolean.FALSE);
        ResponseEntity response = target.deleteUnit(UNIT_ID, authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        verify(unitRepository).exists(UNIT_ID);
        verify(unitRepository, never()).delete(UNIT_ID);
    }

    @Test
    public void getUnit_NotFound() {
        when(unitRepository.findOne(UNIT_ID)).thenReturn(null);
        ResponseEntity<Unit> response = target.getUnit(UNIT_ID, authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(unitRepository).findOne(UNIT_ID);
    }

    @Test
    public void getUnit() {
        when(unitRepository.findOne(UNIT_ID)).thenReturn(unit);
        ResponseEntity<Unit> response = target.getUnit(UNIT_ID, authentication);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(unit);
        verify(unitRepository).findOne(UNIT_ID);
    }
}