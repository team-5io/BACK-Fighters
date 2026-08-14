package com.lion._iozoo.team.infrastructure.persistence;

import com.lion._iozoo.team.domain.TeamRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "team_members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamRole role;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @Builder
    private TeamMemberEntity(Long id, Long teamId, Long userId, TeamRole role, LocalDateTime joinedAt) {
        this.id = id;
        this.teamId = teamId;
        this.userId = userId;
        this.role = role;
        this.joinedAt = joinedAt;
    }
}
