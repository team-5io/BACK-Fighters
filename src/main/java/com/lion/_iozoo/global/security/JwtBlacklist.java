package com.lion._iozoo.global.security;

import org.springframework.stereotype.Component;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtBlacklist {

    // 동시성 문제가 없는 안전한 Set 자료구조로 블랙리스트 명단 생성
    private final Set<String> blacklist = ConcurrentHashMap.newKeySet();

    // 토큰을 블랙리스트에 추가 (로그아웃 시 호출)
    public void add(String token) {
        blacklist.add(token);
    }

    // 이 토큰이 블랙리스트에 있는지 확인 (이후 보안 필터에서 사용)
    public boolean isBlacklisted(String token) {
        return blacklist.contains(token);
    }
}