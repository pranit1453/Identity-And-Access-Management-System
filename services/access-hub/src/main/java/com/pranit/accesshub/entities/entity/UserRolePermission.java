package com.pranit.accesshub.entities.entity;

import com.pranit.accesshub.audit.entity.AuditEntity;
import com.pranit.accesshub.entities.constant.PermissionEffect;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "user_role_permissions",
        schema = "auth",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_role_permission",
                        columnNames = {
                                "user_role_id", "permission_id"
                        }
                )
        },
        indexes = {
                @Index(name = "idx_user_role_permission_user_role", columnList = "user_role_id"),
                @Index(name = "idx_user_role_permission_permission", columnList = "permission_id")
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
public class UserRolePermission extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userRolePermissionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_role_id",
            foreignKey = @ForeignKey(name = "fk_user_role_permission_user_role")
    )
    private UserRole userRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "permission_id",
            foreignKey = @ForeignKey(name = "fk_user_role_permission_permission")
    )
    private Permission permission;

    @Enumerated(EnumType.STRING)
    @Column(name = "effect", nullable = false)
    private PermissionEffect effect;

    @Version
    @Column(nullable = false)
    private Long version;
}
