package com.lion._iozoo.docpr.application.result;

public record DocumentLionGatewayResult(boolean hasConflict, boolean isConsistent, boolean violatesCharter, String evidence) {
}
