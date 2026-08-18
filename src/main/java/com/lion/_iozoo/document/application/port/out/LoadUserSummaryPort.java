package com.lion._iozoo.document.application.port.out;

import java.util.List;
import java.util.Map;

public interface LoadUserSummaryPort {
    Map<Long, UserSummary> loadSummariesByUserIds(List<Long> userIds);
}
