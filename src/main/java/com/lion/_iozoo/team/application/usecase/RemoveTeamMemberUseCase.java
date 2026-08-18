package com.lion._iozoo.team.application.usecase;

public interface RemoveTeamMemberUseCase {
    void remove(Long teamId, Long requesterId, Long memberId);
}
