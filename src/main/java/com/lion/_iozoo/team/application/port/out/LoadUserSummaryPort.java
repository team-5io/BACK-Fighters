package com.lion._iozoo.team.application.port.out;

import java.util.List;
import java.util.Map;

public interface LoadUserSummaryPort {
    Map<Long, UserSummary> loadSummariesByUserIds(List<Long> userIds);
}
