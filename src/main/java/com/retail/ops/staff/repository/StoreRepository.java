package com.retail.ops.staff.repository;

import com.retail.ops.staff.entity.StoreEntity;
import com.retail.ops.staff.model.StoreFormat;
import com.retail.ops.staff.model.StoreStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<StoreEntity, String> {

    boolean existsByCode(String code);

    Optional<StoreEntity> findByCode(String code);

    @Query("""
            SELECT s FROM StoreEntity s
            WHERE (:format IS NULL OR s.format = :format)
              AND (:status IS NULL OR s.status = :status)
            """)
    Page<StoreEntity> findAllByFilters(
            @Param("format") StoreFormat format,
            @Param("status") StoreStatus status,
            Pageable pageable
    );

}
