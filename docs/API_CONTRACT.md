# API_CONTRACT.md

## 기본 경로

- Controller 경로에 별도 접두사(`/api` 등)를 붙이지 않는다. 노션 [Doc PR API 명세서](https://app.notion.com/p/3b3ae0172cdb8150a7c2dda116a12e0f)와 기존 구현(`/auth`, `/users`)이 이미 이 방식을 따른다.

```text
/auth/login
/users/me
/documents
```

## URI와 HTTP Method

- URI는 리소스 중심의 복수형 명사를 사용한다.
- URI에 동사를 사용하지 않는다.
- 하위 리소스는 부모 리소스 경로 아래에 표현한다.

```text
GET    /documents/{documentId}
POST   /documents
PATCH  /documents/{documentId}
DELETE /documents/{documentId}

GET    /doc-prs/{prId}/history
POST   /doc-prs/{prId}/human-reviews
```

- 상태 변경처럼 리소스 변경만으로 의미를 표현하기 어려운 작업은 하위 경로를 사용할 수 있다.

```text
POST /doc-prs/{prId}/approve
POST /doc-prs/{prId}/reject
```

## 요청 규칙

### Header

```http
Content-Type: application/json
Authorization: Bearer {accessToken}
```

- `/auth/**`를 제외한 보호 리소스는 Access Token을 사용한다.

### 입력 검증

- Request DTO에 Bean Validation(`@Valid`)을 사용한다.
- 검증 실패는 `400 Bad Request`로 응답한다(`GlobalExceptionHandler`가 처리).

### 날짜와 시간

- 날짜: `yyyy-MM-dd`
- API 날짜·시간은 `Asia/Seoul (UTC+09:00)` 기준의 ISO-8601 오프셋 형식을 사용한다.

```text
2026-08-14
2026-08-14T14:30:00+09:00
```

### 페이지네이션

목록 API가 필요해지면 `Page`, `Slice`, `Cursor` 중 하나를 선택한다.

| 방식 | 선택 기준 | 요청 | 응답 |
| --- | --- | --- | --- |
| `Page` | 페이지 번호 이동과 전체 항목·페이지 수가 필요할 때 | `page`, `size` | `content`, `page`, `size`, `totalElements`, `totalPages`, `hasNext`, `hasPrevious` |
| `Slice` | 전체 개수 없이 더보기 UI만 필요할 때 | `page`, `size` | `content`, `page`, `size`, `hasNext` |
| `Cursor` | 데이터가 계속 추가되어 offset 조회 중 중복·누락 가능성이 있을 때 | 첫 요청은 cursor 생략, 이후 `cursor`, `size` | `content`, `hasNext`, `nextCursor` |

- `page`는 0부터 시작한다.
- 한 API에서 offset 기반 `page`와 cursor를 동시에 사용하지 않는다.

## 성공 응답 형식

응답 본문이 있는 성공 응답은 `global.presentation.GlobalApiResponse`를 사용한다.

```json
{
  "status": 200,
  "code": "USER_200_1",
  "message": "로그인에 성공했습니다.",
  "data": {}
}
```

| 필드 | 설명 |
| --- | --- |
| `status` | HTTP 상태 코드 |
| `code` | 공통 또는 도메인별 성공 코드 |
| `message` | 성공 메시지 |
| `data` | 실제 응답 데이터. 없으면 `null` |

### Controller 응답 조립

- 모든 응답 본문은 `global.presentation.GlobalApiResponse`를 사용한다. 도메인별 공통 응답 Wrapper를 중복 생성하지 않는다.
- 성공 코드는 각 도메인의 `presentation.api.common.<Domain>ResponseCode` enum이 `ResponseCode`를 구현해 소유한다.
- `200 OK`는 `GlobalApiResponse.ok(code, data)`, `201 Created`는 `GlobalApiResponse.created(code, data)`를 사용한다.

```java
return GlobalApiResponse.ok(UserResponseCode.LOGIN_SUCCESS, LoginResponse.of(user, accessToken));
```

## 오류 응답 형식

```json
{
  "timestamp": "2026-08-14T14:30:00",
  "status": 401,
  "code": "USER_401_1",
  "message": "이메일 또는 비밀번호가 일치하지 않습니다.",
  "traceId": "a1b2c3d4",
  "details": {}
}
```

| 필드 | 설명 |
| --- | --- |
| `timestamp` | 오류 발생 시간 |
| `status` | HTTP 상태 코드 |
| `code` | 공통 또는 도메인 ErrorCode |
| `message` | 오류 메시지 |
| `traceId` | 서버 로그 추적용 식별자(`X-Trace-Id` 응답 헤더와 동일) |
| `details` | 검증 오류 또는 추가 문맥 정보 |

- 서버 내부 예외 정보, SQL, Stack Trace는 응답에 노출하지 않는다.
- 로그인 실패는 "이메일 없음"과 "비밀번호 불일치"를 구분하지 않고 동일한 메시지로 응답한다(계정 열거 공격 방지).

## HTTP 상태 코드 기준

| 상태 | 사용 기준 |
| --- | --- |
| `200 OK` | 조회, 수정, 처리 성공 |
| `201 Created` | 신규 리소스 생성 성공 |
| `400 Bad Request` | 요청 형식, 필수값, 검증 실패 |
| `401 Unauthorized` | 토큰 없음, 토큰 만료, 토큰 검증 실패, 로그인 실패 |
| `403 Forbidden` | 인증은 되었지만 권한 또는 소유권 없음 |
| `404 Not Found` | 대상 리소스 없음 |
| `409 Conflict` | 중복 생성(이메일 중복 등), 현재 상태와 충돌 |
| `500 Internal Server Error` | 처리되지 않은 서버 내부 오류 |

## ErrorCode 규칙

- ErrorCode는 HTTP 상태 코드, 코드, 메시지를 함께 가진다.
- 공통 오류는 `COMMON_` 접두사를 사용한다(`global.exception.CommonErrorCode`).
- 도메인 오류는 도메인별 접두사를 사용한다(`USER_` 등). 세부 규칙은 [ERROR_HANDLING.md](ERROR_HANDLING.md) 참고.

```text
COMMON_400_1
COMMON_401_1
COMMON_403_1
COMMON_404_1
COMMON_409_1
COMMON_500_1

USER_401_1
USER_404_1
USER_409_1
```

## 인증과 인가

- Access Token은 `Authorization: Bearer {token}` 형식을 사용한다.
- 인증 실패는 `401 Unauthorized`로 응답한다.
- 인가(RACI 권한 등)는 Security 설정만으로 처리하지 않고 UseCase/Service 내부에서 리소스별 권한을 검증한다.
- 권한 부족 또는 타인의 리소스 접근은 `403 Forbidden`으로 응답한다.

## API 변경과 호환성

- 기존 응답 필드는 제거하거나 이름을 변경하지 않는다.
- 필드 추가는 기존 클라이언트에 영향을 주지 않는 경우에만 허용한다.
- Request 필수값 추가는 호환성을 깨는 변경으로 본다.
- API 변경 시 노션 [Doc PR API 명세서](https://app.notion.com/p/3b3ae0172cdb8150a7c2dda116a12e0f)도 함께 갱신한다.

## Swagger(OpenAPI) 문서화

- Controller를 새로 작성하거나 엔드포인트를 추가·수정할 때는 springdoc-openapi 어노테이션을 함께 작성한다.
- Controller 클래스에는 `@Tag(name = "도메인 이름", description = "...")`를 붙인다.
- 각 핸들러 메서드에는 `@Operation(summary = "...", description = "...")`을 붙인다.
- Request/Response DTO 필드에는 `@Schema(description = "...", example = "...")`를 붙인다.
- `/swagger-ui/index.html`에서 실제로 렌더링되는지 확인한 뒤 커밋한다.
