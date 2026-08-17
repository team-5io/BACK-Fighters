# DATABASE.md

## 시간대 정책

- 서버 JVM 기본 시간대는 UTC로 사용한다.
- 데이터베이스에 저장하는 날짜·시간의 기준은 `Asia/Seoul (UTC+09:00)`이다.
- 날짜·시간 생성·변환 시 시스템 기본 시간대에 의존하지 않고, `Asia/Seoul` 또는 UTC를 명시적으로 사용한다.
- API 응답은 `Asia/Seoul` 기준의 ISO-8601 오프셋 형식으로 반환한다.
- 생성일·수정일 컬럼을 갖는 JPA Entity의 상속 규칙은 [ARCHITECTURE.md](ARCHITECTURE.md)의 "Domain Model과 JPA Entity" 섹션을 따른다.

## 스키마 관리 (schema.sql)

- 해커톤 짧은 작업 기간(3일) 특성상 Flyway 같은 버전드 마이그레이션 도구는 도입하지 않는다. 대신 `src/main/resources/schema.sql` 하나로 스키마를 관리한다.
- `spring.jpa.hibernate.ddl-auto: none` + `spring.sql.init.mode: always`로 설정되어 있어, 앱을 기동할 때마다 `schema.sql`의 SQL이 그대로 실행된다.
- 모든 `CREATE TABLE`은 `IF NOT EXISTS`로 작성한다. 이미 있는 테이블은 건너뛰므로 매번 재실행해도 안전하다.
- **컬럼을 바꾼 경우**: `IF NOT EXISTS`는 기존 테이블의 컬럼 변경까지는 반영하지 않는다. 로컬에서 해당 테이블(또는 DB 전체)을 지우고 다시 기동해서 새 스키마를 적용한다.
- ERD 초안(`docs/erd.sql`)을 기준으로 작성했지만 구현하면서 필요하면 자유롭게 컬럼을 추가/수정한다. `docs/erd.sql`은 더 이상 갱신하지 않고, `schema.sql`을 최신 기준으로 삼는다.
