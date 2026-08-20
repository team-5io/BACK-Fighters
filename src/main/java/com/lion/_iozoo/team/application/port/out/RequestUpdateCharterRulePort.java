package com.lion._iozoo.team.application.port.out;

import com.lion._iozoo.team.application.result.CharterRule;

public interface RequestUpdateCharterRulePort {
    CharterRule updateRule(String ruleId, String title, String content);
}
