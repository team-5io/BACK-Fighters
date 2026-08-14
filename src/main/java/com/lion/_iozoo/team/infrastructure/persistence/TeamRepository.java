package com.lion._iozoo.team.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<TeamEntity, Long> {
}
