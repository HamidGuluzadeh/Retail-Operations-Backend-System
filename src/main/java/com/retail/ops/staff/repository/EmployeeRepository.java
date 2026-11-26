package com.retail.ops.staff.repository;

import com.retail.ops.staff.entity.EmployeeEntity;
import com.retail.ops.staff.model.EmployeeRole;
import com.retail.ops.staff.model.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, String> {

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumberAndIdNot(String phoneNumber, String employeeId);

    boolean existsByStoreIdAndStatusNot(String storeId, EmployeeStatus status);

    @EntityGraph(attributePaths = "store")
    @Query("""
            SELECT e FROM EmployeeEntity e
            WHERE (:storeId IS NULL OR e.store.id = :storeId)
              AND (:role IS NULL OR e.role = :role)
              AND (:status IS NULL OR e.status = :status)
            """)
    Page<EmployeeEntity> findAllByFilters(
            @Param("storeId") String storeId,
            @Param("role") EmployeeRole role,
            @Param("status") EmployeeStatus status,
            Pageable pageable
    );

}
