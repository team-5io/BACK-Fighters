package com.lion._iozoo.team.application.command;

import com.lion._iozoo.team.domain.CollaborationRuleStatus;

public record UpsertCollaborationRuleCommand(String content, CollaborationRuleStatus status) {
}
