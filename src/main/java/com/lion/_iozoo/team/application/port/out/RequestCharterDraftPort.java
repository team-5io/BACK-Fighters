package com.lion._iozoo.team.application.port.out;

import com.lion._iozoo.team.application.result.CharterRuleDraft;

import java.util.List;

public interface RequestCharterDraftPort {
    // 실패(연결 실패/타임아웃/비2xx) 시 CharterDraftFailedException을 던진다.
    List<CharterRuleDraft> requestDraft(Long teamId);
}
