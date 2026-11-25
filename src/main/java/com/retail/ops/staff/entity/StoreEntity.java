package com.retail.ops.staff.entity;

import com.retail.ops.staff.model.StoreFormat;
import com.retail.ops.staff.model.StoreStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.time.Instant;

@Entity
@Table(name = "stores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoreEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36, updatable = false, nullable = false)
    String id;

    @Column(name = "code", length = 20, unique = true, nullable = false)
    String code;

    @Column(name = "name", length = 100, nullable = false)
    String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "format", length = 30, nullable = false)
    StoreFormat format;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 15, nullable = false)
    StoreStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    Instant createdAt;

    @CreatedBy
    @Column(name = "created_by", length = 36, nullable = false, updatable = false)
    String createdBy;

    @UpdateTimestamp
    @Column(name = "updated_at")
    Instant updatedAt;

    @LastModifiedBy
    @Column(name = "updated_by", length = 36)
    String updatedBy;

    @Version
    @Column(name = "version", nullable = false)
    Long version;
}
