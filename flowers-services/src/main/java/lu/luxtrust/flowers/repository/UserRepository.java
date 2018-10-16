package lu.luxtrust.flowers.repository;

import lu.luxtrust.flowers.entity.system.Role;
import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    boolean existsBySsn(String ssn);

    @Query("select u.id from User u where u.ssn = ?1")
    Long findUserIdBySsn(String ssn);

    @Query("select u from User u left join fetch u.unit left join fetch u.defaultLanguage where u.ssn = ?1")
    User findBySsn(String ssn);

    @Query("select u.email from User u, IN (u.roles) r where r.roleType=?1 and u.unit.id=?2")
    List<String> findEmailsByRoleAndUnit(RoleType roleType, Long unitId);

    @Query("select u.email from User u, IN (u.roles) r where r.roleType=?1")
    List<String> findEmailsByRole(RoleType roleType);

    @Query("select u.roles from User u where u.id = ?1")
    Set<Role> findRolesByUserId(Long userId);

    @Query("select u from User u, IN (u.roles) r where r.roleType=?1 and u.unit.id=?2")
    List<User> findByRoleAndUnit(RoleType roleType, Long unitId);

    @Query("select u from User u, IN (u.roles) r where r.roleType=?1")
    List<User> findByRole(RoleType roleType);
}
