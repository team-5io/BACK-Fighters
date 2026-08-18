package com.lion._iozoo.user.infrastructure.adapter;

import com.lion._iozoo.team.application.port.out.LoadUserSummaryPort;
import com.lion._iozoo.team.application.port.out.UserSummary;
import com.lion._iozoo.user.infrastructure.persistence.entity.UserEntity;
import com.lion._iozoo.user.infrastructure.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Consumer: team
 * Purpose: 팀원 목록/초대 응답에 이름·이메일을 채우기 위한 유저 정보 일괄 조회
 */
@Component
@RequiredArgsConstructor
public class TeamUserSummaryLookupAdapter implements LoadUserSummaryPort {

    private final UserJpaRepository userJpaRepository;

    @Override
    public Map<Long, UserSummary> loadSummariesByUserIds(List<Long> userIds) {
        return userJpaRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(
                        UserEntity::getId,
                        entity -> new UserSummary(entity.getName(), entity.getEmail())));
    }
}
