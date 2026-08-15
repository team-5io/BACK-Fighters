package com.lion._iozoo.user.infrastructure.persistence.entity;

import com.lion._iozoo.global.infrastructure.persistence.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "public_id", nullable = false, unique = true, updatable = false, length = 36)
    private UUID publicId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column
    private String timezone;

    @Column
    private String language;

    @Builder
    private UserEntity(Long id, UUID publicId, String email, String password, String name, String timezone, String language) {
        this.id = id;
        this.publicId = publicId;
        this.email = email;
        this.password = password;
        this.name = name;
        this.timezone = timezone;
        this.language = language;
    }
}