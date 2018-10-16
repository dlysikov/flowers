package lu.luxtrust.flowers.repository;

import lu.luxtrust.flowers.entity.system.Requestor;
import lu.luxtrust.flowers.entity.system.RequestorConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface RequestorRepository extends JpaRepository<Requestor, Long> {

    @Query("select s.config from Requestor s where s.id = ?1")
    RequestorConfiguration findConfiguration(Long serviceId);

    @Query("select s from Requestor s where s.csn = ?1")
    Requestor findRequestorByCsn(String csn);
}
