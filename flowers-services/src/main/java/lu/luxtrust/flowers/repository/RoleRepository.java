package lu.luxtrust.flowers.repository;

import lu.luxtrust.flowers.entity.system.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
