package com.lion._iozoo.team.application.port.out;

import com.lion._iozoo.team.application.result.CharterRule;

import java.util.List;

public interface RequestCharterDraftPort {
    // 실패(연결 실패/타임아웃/비2xx) 시 CharterGatewayFailedException을 던진다.
    List<CharterRule> requestDraft(Long teamId);
}
