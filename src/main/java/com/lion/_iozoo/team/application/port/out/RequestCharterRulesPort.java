package com.lion._iozoo.team.application.port.out;

import com.lion._iozoo.team.application.result.CharterRule;

import java.util.List;

public interface RequestCharterRulesPort {
    // status가 null이면 draft/adopted/archived 전체를 반환한다.
    List<CharterRule> listRules(Long teamId, String status);
}
