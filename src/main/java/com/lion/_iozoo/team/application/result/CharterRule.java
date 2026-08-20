package com.lion._iozoo.team.application.result;

// AI-Fighters가 규칙별로 저장·관리하는 협업 규칙(Charter Rule). id는 AI-Fighters 자체 PK(uuid)이고,
// status는 AI가 새 값을 추가해도 깨지지 않도록 enum이 아닌 문자열 그대로 통과시킨다(draft/adopted/archived).
public record CharterRule(String id, String status, String title, String content) {
}
