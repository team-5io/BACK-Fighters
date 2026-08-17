package com.lion._iozoo.team.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamCollaborationRuleRepository extends JpaRepository<TeamCollaborationRuleEntity, Long> {
    Optional<TeamCollaborationRuleEntity> findByTeamId(Long teamId);
}
