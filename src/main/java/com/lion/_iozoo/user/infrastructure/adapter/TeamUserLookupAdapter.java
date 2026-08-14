package com.lion._iozoo.user.infrastructure.adapter;

import com.lion._iozoo.team.application.port.out.LoadUserIdByEmailPort;
import com.lion._iozoo.user.application.port.out.LoadUserPort;
import com.lion._iozoo.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Consumer: team
 * Purpose: 팀원 초대 시 이메일로 가입된 유저인지 확인
 */
@Component
@RequiredArgsConstructor
public class TeamUserLookupAdapter implements LoadUserIdByEmailPort {

    private final LoadUserPort loadUserPort;

    @Override
    public Optional<Long> loadUserIdByEmail(String email) {
        return loadUserPort.loadUserByEmail(email).map(User::getId);
    }
}
