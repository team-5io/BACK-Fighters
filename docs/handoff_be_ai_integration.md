# BE ↔ AI 연동 인수인계

담당: 김민섭 (AI) — 재원님 BE 단독 부담 완화를 위해 AI↔BE 연동 구간 대신 진행

## 버전 이력

| 버전 | 날짜 | 변경 내용 |
|---|---|---|
| v1.0.0 | 2026-08-19 | 최초 작성 — Translation/DocumentLion/Writing Assistant/Charter AI 연동 작업 기준 |

## Done (v1.0.0 기준, 2026-08-19)

### 1. Dev-aware Translation — 블록 단위 전환
- FE가 블록마다 개별 호출하도록 `POST /{documentId}/translations`에 `blockId` 필드 추가
- 캐시 키를 `(documentId, targetLanguage)` → `(documentId, blockId, targetLanguage)`로 세분화 (블록별 호출 시 UniqueConstraint 충돌 나던 문제 해결)
- `type: "code"` 블록 스킵은 FE 책임(BE는 블록 type을 안 받음)
- AI-Fighters PR [#19](https://github.com/team-5io/AI-Fighters/pull/19) · BE PR [#97](https://github.com/team-5io/BACK-Fighters/pull/97)

### 2. DocumentLion — AI 리뷰 요청/조회 신규 구현
- `POST /doc-prs/{prId}/ai-review` — 수동 리뷰 요청(재요청 시 기존 결과 덮어씀)
- `GET /doc-prs/{prId}/ai-review` — 저장된 최신 결과 조회
- 이미 설계돼 있던 `ai_reviews` 테이블 활용, AI의 `issues[]`를 `hasConflict`/`isConsistent`/`violatesCharter` 불리언 3개 + `evidence` 텍스트로 요약해 저장
- AI-Fighters PR [#21](https://github.com/team-5io/AI-Fighters/pull/21) · BE PR [#103](https://github.com/team-5io/BACK-Fighters/pull/103) / [#104](https://github.com/team-5io/BACK-Fighters/pull/104)

### 3. AI Writing Assistant — 제안 요청 신규 구현
- `POST /documents/{documentId}/writing-assistant/suggestions` — 저장 없이 제안 목록만 그대로 통과(stateless 프록시)
- AI-Fighters PR [#22](https://github.com/team-5io/AI-Fighters/pull/22) · BE PR [#106](https://github.com/team-5io/BACK-Fighters/pull/106)

### 4. Team Collaboration Charter — AI 초안 생성 신규 구현
- `POST /teams/{teamId}/charter/draft` — 기존 `UpsertCollaborationRuleUseCase`(수동 편집, `PUT /{teamId}/charter`) 재사용
- AI-Fighters PR [#22](https://github.com/team-5io/AI-Fighters/pull/22) · BE PR [#108](https://github.com/team-5io/BACK-Fighters/pull/108) / [#109](https://github.com/team-5io/BACK-Fighters/pull/109)

### 5. ID 타입 버그 수정 (Translation/DocumentLion/Charter 공통)
- `documentId`/`docPrId`/`teamId`가 AI 쪽 스키마에 `uuid`로 잘못 잡혀있던 것 발견·수정. BE의 Document/DocPr/Team은 User와 달리 `publicId`가 없고 내부 PK(`Long`)를 그대로 쓴다 — 그대로 뒀으면 BE가 실제로 호출하는 순간 422가 났을 버그. `requestedBy`(userId)만 `publicId`(UUID)로 정상.
- AI-Fighters PR [#21](https://github.com/team-5io/AI-Fighters/pull/21)

### 6. 배포 준비
- AI-Fighters EC2 배포를 api+db 번들 docker-compose로 구성 — 별도 RDS 프로비저닝 없이 인스턴스 하나면 됨. AI-Fighters PR [#20](https://github.com/team-5io/AI-Fighters/pull/20)
- AI-Fighters `develop` → `main` 배포 완료 (PR [#23](https://github.com/team-5io/AI-Fighters/pull/23))
- BE `develop` → `main`은 PR [#110](https://github.com/team-5io/BACK-Fighters/pull/110) 승인 대기 중 — main ruleset이 승인 리뷰 1개를 요구해서 재원님(또는 팀원) 승인 필요

## 의도적으로 범위에서 뺀 것 (중요 — 다음 작업자가 이유를 알아야 함)

### DocumentLion 자동 트리거
Doc PR 제출 시 자동으로 AI 리뷰가 도는 기능은 이번에 구현하지 않았다. `DocPrStatus`에 `AI_REVIEW`/`HUMAN_REVIEW`가 enum 값으로 정의는 돼 있지만, `DocPr` 도메인에 이 상태로 전환하는 메서드가 하나도 없어서 **완전히 죽어있는 상태값**이다(`approve()`는 `isTerminal()`만 체크해서 `CREATED`에서 바로 승인 가능). 이걸 살리려면 기존에 이미 잘 도는 `CreateDocPrService`/승인 플로우를 건드려야 해서, 별도로 신중하게 진행하는 게 안전하다고 판단해 이번 범위에서 뺐다. **지금은 `POST /doc-prs/{prId}/ai-review`를 FE가 수동으로 호출해야만 리뷰가 돈다.**

### Charter 개별 규칙 조작
AI는 규칙을 여러 개(각각 독립된 id/title/description)로 반환하지만, BE `team_collaboration_charters`는 이미 배포된 `PUT /{teamId}/charter`가 "팀당 단일 텍스트" 구조로 쓰고 있어서, AI가 준 여러 규칙을 번호 매긴 목록 텍스트 하나로 합쳐 기존 구조에 얹었다. **PATCH로 개별 규칙만 수정하거나 일부만 선택 채택하는 것은 지원하지 않는다** — 필요하면 스키마 확장(팀당 여러 행 구조로 마이그레이션)이 별도로 필요하다.

## 확인이 필요한 부분

- **`AI_GATEWAY_BASE_URL`이 아직 비어있다.** `application.yaml`에 기본값이 없어서 채워지기 전까지 BE→AI 호출은 전부 502로 처리된다. AI-Fighters EC2 인스턴스 생성 + GitHub Secrets(`EC2_HOST`/`EC2_USER`/`EC2_SSH_KEY`/`EC2_DEPLOY_PATH`) 등록이 먼저 끝나야 한다 (`docs/deploy.md` in AI-Fighters 참고).
- **Document Graph API(`GET /documents/{id}/graph`)가 BE에 없다.** 이게 없어서 DocumentLion은 지금 `charter_violation`만 실제로 검사하고 `conflict`/`inconsistency`는 항상 이슈 없음으로 나온다. Writing Assistant의 "관련 문서 맥락 인용" 기능도 같은 이유로 AI 쪽에 아직 없다.
- **FE 반영 여부** — 번역 API `blockId` 필수 추가, DocumentLion/Writing Assistant/Charter 신규 엔드포인트 4개, Charter 응답이 "합쳐진 텍스트 하나"라는 점을 FE와 맞춰야 한다.
- **로컬 개발 DB**: `.env`에 `SPRING_DATASOURCE_PASSWORD` 필요(`spring-dotenv`가 자동 로드), 로컬 MySQL에 `5iozoo_db` 데이터베이스가 미리 있어야 `DocPrApplicationTests.contextLoads()`가 통과한다.

## 관련 링크

- AI-Fighters PR: [#19](https://github.com/team-5io/AI-Fighters/pull/19) [#20](https://github.com/team-5io/AI-Fighters/pull/20) [#21](https://github.com/team-5io/AI-Fighters/pull/21) [#22](https://github.com/team-5io/AI-Fighters/pull/22) [#23](https://github.com/team-5io/AI-Fighters/pull/23)
- BACK-Fighters PR: [#97](https://github.com/team-5io/BACK-Fighters/pull/97) [#103](https://github.com/team-5io/BACK-Fighters/pull/103) [#104](https://github.com/team-5io/BACK-Fighters/pull/104) [#106](https://github.com/team-5io/BACK-Fighters/pull/106) [#108](https://github.com/team-5io/BACK-Fighters/pull/108) [#109](https://github.com/team-5io/BACK-Fighters/pull/109) [#110](https://github.com/team-5io/BACK-Fighters/pull/110)
- AI-Fighters API 계약: `docs/api_contract.md`
- AI-Fighters 배포 가이드: `docs/deploy.md`
