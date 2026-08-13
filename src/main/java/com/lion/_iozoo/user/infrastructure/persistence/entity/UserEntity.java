package com.lion._iozoo.user.infrastructure.persistence.entity;

import com.lion._iozoo.global.infrastructure.persistence.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    private UserEntity(Long id, String email, String password, String name, String timezone, String language) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.timezone = timezone;
        this.language = language;
    }
}