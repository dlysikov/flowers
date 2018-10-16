package lu.luxtrust.flowers.service;

import lu.luxtrust.flowers.entity.enrollment.Unit;
import lu.luxtrust.flowers.entity.system.Requestor;
import lu.luxtrust.flowers.model.FieldFilter;
import lu.luxtrust.flowers.model.PageParams;
import java.util.List;

public interface UnitService {
    List<Unit> findAll(PageParams params);
    boolean isValuableDataModifiable(Long unitId);
    boolean changesCanBeApplied(Unit unit);
    boolean canBeDeleted(Long unitId);
    long count(List<FieldFilter> filters);
}
