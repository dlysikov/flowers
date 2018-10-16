package lu.luxtrust.flowers.service.impl;

import com.google.common.collect.Lists;
import lu.luxtrust.flowers.entity.enrollment.Unit;
import lu.luxtrust.flowers.entity.system.Requestor;
import lu.luxtrust.flowers.model.FieldFilter;
import lu.luxtrust.flowers.model.PageParams;
import lu.luxtrust.flowers.repository.UnitRepository;
import lu.luxtrust.flowers.service.PredicateProducer;
import lu.luxtrust.flowers.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UnitServiceImpl implements UnitService {

    private final UnitRepository unitRepository;

    @Autowired
    public UnitServiceImpl(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    @Override
    public List<Unit> findAll(PageParams params) {
        Sort sortOrder = new Sort(Sort.Direction.DESC, "id");
        if (params.getPageSize() != null && params.getPageNumber() != null) {
            PageRequest pageRequest = new PageRequest(params.getPageNumber() - 1, params.getPageSize(), sortOrder);
            return Lists.newArrayList(unitRepository.findAll(SearchSpecifications.filterQuery(params.getFilter()), pageRequest));
        } else {
            return unitRepository.findAll(SearchSpecifications.filterQuery(params.getFilter()), sortOrder);
        }
    }

    @Override
    public long count(List<FieldFilter> filters) {
        return unitRepository.count(SearchSpecifications.filterQuery(filters));
    }

    @Override
    public boolean isValuableDataModifiable(Long unitId) {
        return unitRepository.findRelationsCount(unitId) == 0;
    }

    @Override
    public boolean changesCanBeApplied(Unit unit) {
        Unit original = unitRepository.findOne(unit.getId());
        return isValuableDataModifiable(unit.getId()) || Unit.equalsWithoutOptional(unit, original);
    }

    @Override
    public boolean canBeDeleted(Long unitId) {
        return isValuableDataModifiable(unitId);
    }
}
