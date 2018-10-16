package lu.luxtrust.flowers.repository;

import lu.luxtrust.flowers.entity.enrollment.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    Long countByHolderId(Long holderId);

    @Modifying
    @Query("delete from Document d where d.holder.id=?1")
    void removeAllForHolder(Long holderId);

    @Modifying
    @Query("delete from Document d where d.holder.id = :holderId and d.name = :documentName ")
    int removeDocumentByName(@Param("holderId") Long holderId, @Param("documentName") String documentName);
}
