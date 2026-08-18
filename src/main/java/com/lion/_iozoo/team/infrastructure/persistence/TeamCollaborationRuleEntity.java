package com.lion._iozoo.team.infrastructure.persistence;

import com.lion._iozoo.global.infrastructure.persistence.BaseTimeEntity;
import com.lion._iozoo.team.domain.CollaborationRuleStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "team_collaboration_charters")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamCollaborationRuleEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CollaborationRuleStatus status;

    @Builder
    private TeamCollaborationRuleEntity(Long id, Long teamId, String content, CollaborationRuleStatus status) {
        this.id = id;
        this.teamId = teamId;
        this.content = content;
        this.status = status;
    }

    public void update(String content, CollaborationRuleStatus status) {
        this.content = content;
        this.status = status;
    }
}
