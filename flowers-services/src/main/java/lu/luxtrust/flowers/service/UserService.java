package lu.luxtrust.flowers.service;

import lu.luxtrust.flowers.entity.enrollment.Unit;
import lu.luxtrust.flowers.entity.system.Role;
import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.enums.RoleType;
import lu.luxtrust.flowers.model.FieldFilter;
import lu.luxtrust.flowers.model.PageParams;

import java.util.Collection;
import java.util.List;

public interface UserService {
    User findActiveBySSN(String ssn);
    List<String> findDiaEmailsByUnit(Unit unit);
    List<String> findEmailsByRole(RoleType role);
    List<User> findAll(PageParams params, List<Role> roles);
    long count(List<FieldFilter> filters, List<Role> roles);
    List<Role> managedBy(Collection<RoleType> role);
    User save (User user);
}
