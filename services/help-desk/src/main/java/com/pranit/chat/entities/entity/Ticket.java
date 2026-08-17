package com.pranit.chat.entities.entity;

import com.pranit.chat.entities.constant.Category;
import com.pranit.chat.entities.constant.Priority;
import com.pranit.chat.entities.constant.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "help_desk_ticket",
        schema = "ticket",
        indexes = {
                @Index(
                        name = "idx_help_desk_ticket_username",
                        columnList = "username"
                ),
                @Index(
                        name = "idx_help_desk_ticket_username_status",
                        columnList = "username, status"
                ),
                @Index(
                        name = "idx_help_desk_ticket_category",
                        columnList = "category"
                ),
                @Index(
                        name = "idx_help_desk_ticket_created_at",
                        columnList = "created_at"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_id", unique = true, nullable = false)
    private String ticketId;

    @Column(name = "username", nullable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "summary", nullable = false, columnDefinition = "TEXT")
    private String summary;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Version
    private Long version;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}
