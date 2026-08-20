package com.lion._iozoo.team.application.port.out;

import java.util.List;
import java.util.UUID;

public interface RequestAdoptCharterRulesPort {
    // AI-Fighters 계약에 채택 응답 바디가 명시돼 있지 않아 성공 여부만 반환한다.
    // 최신 상태가 필요하면 클라이언트가 목록 조회(GET /teams/{teamId}/charter/rules)를 다시 호출한다.
    void adopt(Long teamId, List<String> ruleIds, UUID adoptedBy);
}
