package com.lion._iozoo.team.infrastructure.persistence;

import com.lion._iozoo.global.infrastructure.persistence.BaseTimeEntity;
import com.lion._iozoo.team.domain.CharterRuleStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "charter_rules")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CharterRuleEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CharterRuleStatus status;

    @Builder
    private CharterRuleEntity(Long id, Long teamId, String title, String content, CharterRuleStatus status) {
        this.id = id;
        this.teamId = teamId;
        this.title = title;
        this.content = content;
        this.status = status;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void adopt() {
        this.status = CharterRuleStatus.ADOPTED;
    }
}
