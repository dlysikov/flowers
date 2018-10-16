package lu.luxtrust.flowers.repository;

import lu.luxtrust.flowers.entity.system.ResponseAPI;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResponseAPIRepository extends JpaRepository<ResponseAPI, Long> {

    @Query("select rapi from ResponseAPI rapi join rapi.requestor r where rapi.userExternalId = :userExternalId and r.id=:requestorId")
    ResponseAPI findByUserExternalIdAndRequestor(@Param("userExternalId") String userExternalId, @Param("requestorId") Long requestorId);

    @Query("select rapi from ResponseAPI rapi where rapi.status in ('RESPONSE_SENDING_ERROR', 'RESPONSE_SERVER_ERROR')")
    Slice<ResponseAPI> getResponseAPIsForSending(Pageable page);

}
