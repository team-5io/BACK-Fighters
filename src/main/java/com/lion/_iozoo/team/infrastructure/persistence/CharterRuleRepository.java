package com.lion._iozoo.team.infrastructure.persistence;

import com.lion._iozoo.team.domain.CharterRuleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CharterRuleRepository extends JpaRepository<CharterRuleEntity, Long> {
    List<CharterRuleEntity> findByTeamId(Long teamId);
    List<CharterRuleEntity> findByTeamIdAndStatus(Long teamId, CharterRuleStatus status);
    // 다른 팀 소속 규칙을 teamId 경로 파라미터로 조작하지 못하도록 teamId까지 함께 스코핑한다.
    Optional<CharterRuleEntity> findByIdAndTeamId(Long id, Long teamId);
}
