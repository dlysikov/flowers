package lu.luxtrust.flowers.service.impl;

import com.google.common.collect.Lists;
import lu.luxtrust.flowers.entity.enrollment.Unit;
import lu.luxtrust.flowers.entity.system.Role;
import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.enums.Markers;
import lu.luxtrust.flowers.enums.RoleType;
import lu.luxtrust.flowers.exception.FlowersException;
import lu.luxtrust.flowers.model.FieldFilter;
import lu.luxtrust.flowers.model.PageParams;
import lu.luxtrust.flowers.repository.RoleRepository;
import lu.luxtrust.flowers.repository.UserRepository;
import lu.luxtrust.flowers.service.PredicateProducer;
import lu.luxtrust.flowers.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static lu.luxtrust.flowers.enums.RoleType.*;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);
    private static Marker auditMarker = MarkerFactory.getMarker(Markers.AUDIT.getName());

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public User findActiveBySSN(String ssn) {
        User bySsn = userRepository.findBySsn(ssn);
        if (bySsn != null) {
            bySsn.getRoles().size();
        }
        return bySsn;
    }

    @Override
    public List<String> findDiaEmailsByUnit(Unit unit) {
        return userRepository.findEmailsByRoleAndUnit(RoleType.DIA, unit.getId());
    }

    @Override
    public List<String> findEmailsByRole(RoleType role) {
        return userRepository.findEmailsByRole(role);
    }

    @Override
    public List<User> findAll(PageParams params, List<Role> roles) {
        Sort sortOrder = new Sort(Sort.Direction.DESC, "id");
        PredicateProducer pp = getRolesPredicateProduser(roles);
        Specification<User> query = SearchSpecifications.filterQuery(params.getFilter(), true, pp);
        if (params.getPageSize() != null && params.getPageNumber() != null) {
            PageRequest pageRequest = new PageRequest(params.getPageNumber() - 1, params.getPageSize(), sortOrder);
            return Lists.newArrayList(userRepository.findAll(query, pageRequest));
        } else {
            return userRepository.findAll(query, sortOrder);
        }
    }

    @Override
    public long count(List<FieldFilter> filters, List<Role> roles) {
        PredicateProducer pp = getRolesPredicateProduser(roles);
        return userRepository.count(SearchSpecifications.filterQuery(filters, true, pp));
    }

    private PredicateProducer getRolesPredicateProduser(List<Role> roles) {
        return (cb, root) -> Optional.of(root.join("roles").get("id").in(roles.stream().map(Role::getId).collect(Collectors.toList())));
    }

    @Override
    public List<Role> managedBy(Collection<RoleType> roles) {
        List<Role> all = roleRepository.findAll();
        Map<RoleType, Role> map = new HashMap<>();
        for (Role role : all) {
            map.put(role.getRoleType(), role);
        }
        ArrayList<Role> result = new ArrayList<>();
        if (roles.contains(FLOWERS_ADMIN)) {
            result.add(map.get(CSD_AGENT));
            result.add(map.get(DIA));
            result.add(map.get(FLOWERS_ADMIN));
            result.add(map.get(ESEAL_OFFICER));
        }
        if (roles.contains(ESEAL_OFFICER)) {
            result.add(map.get(ESEAL_MANAGER));
        }
        return result;
    }

    @Override
    public User save(User user) {

        for (Role role : user.getRoles()) {
            if (user.getUnit() == null &&
                    (role.getRoleType().equals(RoleType.DIA) || role.getRoleType().equals(RoleType.ESEAL_MANAGER))) {
                LOG.warn("Incorrect RoleType [{}] for user WITH userId [{}] WITHOUT Unit", role.getRoleType(), user.getId());
                throw new FlowersException("Incorrect RoleType for user without unit");
            } else if (user.getUnit() != null &&
                    (!role.getRoleType().equals(RoleType.DIA) && !role.getRoleType().equals(RoleType.ESEAL_MANAGER))) {
                LOG.warn("Incorrect RoleType [{}] for user with userId [{}] WITH Unit", role.getRoleType(), user.getId());
                throw new FlowersException("Incorrect RoleType for user with unit");
            }
        }

        return userRepository.save(user);
    }
}
