package com.lion._iozoo.user.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
public class User {
    private final Long id;
    private final UUID publicId;
    private final String email;
    private final String password;
    private String name;
    private String timezone;
    private String language;

    @Builder
    private User(Long id, UUID publicId, String email, String password, String name, String timezone, String language) {
        this.id = id;
        this.publicId = publicId;
        this.email = email;
        this.password = password;
        this.name = name;
        this.timezone = timezone;
        this.language = language;
    }

    public void updateProfile(String name, String timezone, String language) {
        this.name = name;
        this.timezone = timezone;
        this.language = language;
    }

    // 도메인 규칙: 비밀번호 암호화 등의 핵심 비즈니스 로직 추가가능
}