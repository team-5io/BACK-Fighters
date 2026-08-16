# 계정(User) API 명세서

> 아래 각 `## ` 섹션이 Notion 하위 페이지 1개(엔드포인트 1개)에 대응합니다. 섹션별로 그대로 복사해서 각 페이지에 붙여넣으세요.
> 공통 성공 응답 포맷: `{ "status", "code", "message", "data" }` (`GlobalApiResponse`).
> 공통 실패 응답 포맷: `{ "timestamp", "status", "code", "message", "traceId", "details" }`
> `회원가입`/`로그인`/`로그아웃`은 `/auth/**`로 `SecurityConfig`에서 permitAll — `Authorization` 헤더가 필요 없습니다.
> 생성류 응답(회원가입)은 바디의 `status` 필드가 `201`이어도, 컨트롤러가 `ResponseEntity` 없이 반환해 실제 HTTP 상태 코드는 `200`입니다.

---

## 1. 회원가입

`POST /auth/signup`

# **[request]**

Request Header

| name | description |
| --- | --- |
| - | 인증이 필요 없는 공개 엔드포인트입니다 (`Authorization` 헤더 불필요). |

Request Body

```json
{
  "email": "user@example.com",
  "password": "password1234",
  "name": "홍길동"
}
```

| name | type | required | description |
| --- | --- | --- | --- |
| `email` | `String` | `true` | 이메일 형식이어야 합니다. |
| `password` | `String` | `true` | 8자 이상이어야 합니다. |
| `name` | `String` | `true` | 사용자 이름입니다. |

# **[response]**

### 성공코드

| HTTP 상태 | 설명 |
| --- | --- |
| `200 OK` | 회원가입 성공. 응답 바디의 `status` 필드 값은 `201`이지만 실제 HTTP 상태 코드는 `200`입니다. |

Response Body

```json
{
  "status": 201,
  "code": "USER_201_1",
  "message": "회원가입에 성공했습니다.",
  "data": {
    "publicId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "email": "user@example.com",
    "name": "홍길동"
  }
}
```

### Response Field

| name | 설명 |
| --- | --- |
| `data.publicId` | 외부(AI 서버·프론트) 노출용 사용자 식별자(UUID)입니다. 내부 PK(Long)는 응답에 노출되지 않습니다. |
| `data.email` | 가입한 이메일입니다. |
| `data.name` | 사용자 이름입니다. |

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `400 Bad Request` | `COMMON_400_1` | 입력값이 올바르지 않습니다. | 이메일 형식 오류, 비밀번호 8자 미만 등 Bean Validation 실패 |
| `409 Conflict` | `USER_409_1` | 이미 가입된 이메일입니다. | 이미 존재하는 이메일로 가입 시도 |

---

## 2. 로그인

`POST /auth/login`

# **[request]**

Request Header

| name | description |
| --- | --- |
| - | 인증이 필요 없는 공개 엔드포인트입니다. |

Request Body

```json
{
  "email": "user@example.com",
  "password": "password1234"
}
```

| name | type | required | description |
| --- | --- | --- | --- |
| `email` | `String` | `true` | 이메일 형식이어야 합니다. |
| `password` | `String` | `true` | 비밀번호입니다. |

# **[response]**

### 성공코드

| HTTP 상태 | 설명 |
| --- | --- |
| `200 OK` | 로그인 성공. `accessToken`을 발급합니다. |

Response Body

```json
{
  "status": 200,
  "code": "USER_200_1",
  "message": "로그인에 성공했습니다.",
  "data": {
    "publicId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "email": "user@example.com",
    "name": "홍길동",
    "accessToken": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

### Response Field

| name | 설명 |
| --- | --- |
| `data.publicId` | 외부 노출용 사용자 식별자(UUID)입니다. |
| `data.email` | 로그인한 이메일입니다. |
| `data.name` | 사용자 이름입니다. |
| `data.accessToken` | 이후 요청의 `Authorization: Bearer {accessToken}` 헤더에 사용할 JWT입니다. |

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `400 Bad Request` | `COMMON_400_1` | 입력값이 올바르지 않습니다. | 이메일 형식 오류 등 |
| `401 Unauthorized` | `USER_401_1` | 이메일 또는 비밀번호가 일치하지 않습니다. | 이메일 미가입 또는 비밀번호 불일치 (사용자 열거 방지를 위해 동일 메시지) |

---

## 3. 로그아웃

`POST /auth/logout`

# **[request]**

Request Header

| name | description |
| --- | --- |
| - | `/auth/**`는 permitAll이라 헤더로는 인증하지 않습니다. 무효화할 토큰은 Body로 직접 전달합니다. |

Request Body

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

| name | type | required | description |
| --- | --- | --- | --- |
| `accessToken` | `String` | `true` | 무효화(블랙리스트 등록)할 액세스 토큰입니다. |

# **[response]**

### 성공코드

| HTTP 상태 | 설명 |
| --- | --- |
| `200 OK` | 로그아웃 성공. 전달받은 토큰이 블랙리스트에 등록되어 이후 요청에 사용할 수 없습니다. |

Response Body

```json
{
  "status": 200,
  "code": "USER_200_2",
  "message": "로그아웃되었습니다.",
  "data": null
}
```

### Response Field

| name | 설명 |
| --- | --- |
| `data` | 없음 (`null`) |

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `400 Bad Request` | `COMMON_400_1` | 입력값이 올바르지 않습니다. | `accessToken`이 비어있는 경우 |

---

## 4. 개인 프로필 설정

`PATCH /users/me`

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {AccessToken}` 형식의 사용자 인증 토큰입니다. |

Request Body

```json
{
  "name": "홍길동",
  "timezone": "Asia/Seoul",
  "language": "ko"
}
```

| name | type | required | description |
| --- | --- | --- | --- |
| `name` | `String` | `true` | 변경할 이름입니다. |
| `timezone` | `String` | `false` | 예: `Asia/Seoul` |
| `language` | `String` | `false` | 선호 언어. 예: `ko`, `en`, `vi` |

# **[response]**

### 성공코드

| HTTP 상태 | 설명 |
| --- | --- |
| `200 OK` | 프로필 수정 성공 |

Response Body

```json
{
  "status": 200,
  "code": "USER_200_3",
  "message": "프로필이 수정되었습니다.",
  "data": {
    "publicId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "email": "user@example.com",
    "name": "홍길동",
    "timezone": "Asia/Seoul",
    "language": "ko"
  }
}
```

### Response Field

| name | 설명 |
| --- | --- |
| `data.publicId` | 외부 노출용 사용자 식별자(UUID)입니다. |
| `data.email` | 이메일입니다 (변경 불가). |
| `data.name` | 변경된 이름입니다. |
| `data.timezone` | 변경된 타임존입니다. |
| `data.language` | 변경된 선호 언어입니다. |

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `400 Bad Request` | `COMMON_400_1` | 입력값이 올바르지 않습니다. | `name`이 비어있는 경우 |
| `401 Unauthorized` | `COMMON_401_1` | 인증이 필요합니다. | AccessToken이 없거나 유효하지 않은 경우 |
