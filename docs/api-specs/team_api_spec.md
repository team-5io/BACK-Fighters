# 팀(Team) API 명세서

> 아래 각 `## ` 섹션이 Notion 하위 페이지 1개(엔드포인트 1개)에 대응합니다. 섹션별로 그대로 복사해서 각 페이지에 붙여넣으세요.
> 공통 성공 응답 포맷: `{ "status", "code", "message", "data" }` (`GlobalApiResponse`).
> 공통 실패 응답 포맷: `{ "timestamp", "status", "code", "message", "traceId", "details" }`
> 모든 API는 `Authorization: Bearer {AccessToken}` 헤더가 필요합니다 (미인증 시 `401 COMMON_401_1`).
> 생성류 응답(팀 생성, 팀원 초대)은 바디의 `status` 필드가 `201`이어도, 컨트롤러가 `ResponseEntity` 없이 반환해 실제 HTTP 상태 코드는 `200`입니다.
> Team 도메인 응답은 아직 `id`/`userId`를 내부 PK(Long) 그대로 노출합니다 — User 도메인처럼 `publicId`(UUID)로 바뀌지 않았습니다 (후속 과제).

---

## 1. 팀 생성

`POST /teams`

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {AccessToken}` 형식의 사용자 인증 토큰입니다. |

Request Body

```json
{
  "name": "5iozoo 팀"
}
```

| name | type | required | description |
| --- | --- | --- | --- |
| `name` | `String` | `true` | 팀(프로젝트) 이름입니다. |

# **[response]**

### 성공코드

| HTTP 상태 | 설명 |
| --- | --- |
| `200 OK` | 팀 생성 성공. 생성 요청자는 자동으로 해당 팀의 `ADMIN`으로 등록됩니다. |

Response Body

```json
{
  "status": 201,
  "code": "TEAM_201_1",
  "message": "팀이 생성되었습니다.",
  "data": {
    "id": 1,
    "name": "5iozoo 팀"
  }
}
```

### Response Field

| name | 설명 |
| --- | --- |
| `data.id` | 생성된 팀의 ID입니다. |
| `data.name` | 팀 이름입니다. |

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `400 Bad Request` | `COMMON_400_1` | 입력값이 올바르지 않습니다. | `name`이 비어있는 경우 |
| `401 Unauthorized` | `COMMON_401_1` | 인증이 필요합니다. | AccessToken이 없거나 유효하지 않은 경우 |

---

## 2. 팀원 초대

`POST /teams/{teamId}/invitations`

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {AccessToken}` 형식의 사용자 인증 토큰입니다. |

Request Parameter

| name | description |
| --- | --- |
| `teamId` | 초대할 팀의 ID입니다. |

Request Body

```json
{
  "email": "invitee@example.com"
}
```

| name | type | required | description |
| --- | --- | --- | --- |
| `email` | `String` | `true` | 초대할 유저의 가입된 이메일입니다. |

# **[response]**

### 성공코드

| HTTP 상태 | 설명 |
| --- | --- |
| `200 OK` | 초대 성공. 초대된 유저는 `MEMBER` 역할로 즉시 팀에 등록됩니다 (별도 수락 절차 없음). |

Response Body

```json
{
  "status": 201,
  "code": "TEAM_201_2",
  "message": "팀원을 초대했습니다.",
  "data": {
    "userId": 20,
    "role": "MEMBER",
    "joinedAt": "2026-08-16T14:00:00"
  }
}
```

### Response Field

| name | 설명 |
| --- | --- |
| `data.userId` | 초대된 유저의 ID입니다. |
| `data.role` | 팀 내 역할입니다. 초대 직후에는 항상 `MEMBER`입니다 (`MEMBER` / `ADMIN`). |
| `data.joinedAt` | 팀에 합류한 시각입니다. |

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `400 Bad Request` | `COMMON_400_1` | 입력값이 올바르지 않습니다. | 이메일 형식 오류 등 |
| `401 Unauthorized` | `COMMON_401_1` | 인증이 필요합니다. | AccessToken이 없거나 유효하지 않은 경우 |
| `403 Forbidden` | `COMMON_403_1` | 접근 권한이 없습니다. | 요청자가 해당 팀의 `ADMIN`이 아닌 경우 (팀원이 아닌 경우 포함) |
| `404 Not Found` | `TEAM_404_1` | 가입되지 않은 이메일입니다. | 초대하려는 이메일로 가입된 유저가 없는 경우 |
| `409 Conflict` | `TEAM_409_1` | 이미 팀에 소속된 유저입니다. | 이미 해당 팀의 팀원인 유저를 다시 초대한 경우 |

---

## 3. 팀원 추방/탈퇴

`DELETE /teams/{teamId}/members/{memberId}`

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {AccessToken}` 형식의 사용자 인증 토큰입니다. |

Request Parameter

| name | description |
| --- | --- |
| `teamId` | 대상 팀의 ID입니다. |
| `memberId` | 추방/탈퇴시킬 대상 유저의 ID입니다 (팀원 레코드 ID가 아닌 유저 ID). |

# **[response]**

### 성공코드

| HTTP 상태 | 설명 |
| --- | --- |
| `200 OK` | 삭제 성공. 요청자 본인이 `memberId`와 같으면 본인 탈퇴, 다르면 요청자가 해당 팀 `ADMIN`이어야 합니다. |

Response Body

```json
{
  "status": 200,
  "code": "TEAM_200_1",
  "message": "팀원을 삭제했습니다.",
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
| `401 Unauthorized` | `COMMON_401_1` | 인증이 필요합니다. | AccessToken이 없거나 유효하지 않은 경우 |
| `403 Forbidden` | `COMMON_403_1` | 접근 권한이 없습니다. | 요청자가 본인도 아니고 해당 팀의 `ADMIN`도 아닌 경우 |
| `404 Not Found` | `TEAM_404_3` | 팀에 소속되지 않은 유저입니다. | `memberId`가 해당 팀 소속이 아닌 경우 |

---

## 4. 팀원 목록 및 역할 조회

`GET /teams/{teamId}/members`

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {AccessToken}` 형식의 사용자 인증 토큰입니다. |

Request Parameter

| name | description |
| --- | --- |
| `teamId` | 조회할 팀의 ID입니다. |

# **[response]**

### 성공코드

| HTTP 상태 | 설명 |
| --- | --- |
| `200 OK` | 조회 성공. 요청자가 해당 팀의 팀원(`MEMBER` 또는 `ADMIN`)이어야 합니다. |

Response Body

```json
{
  "status": 200,
  "code": "TEAM_200_2",
  "message": "팀원 목록을 조회했습니다.",
  "data": [
    { "userId": 10, "role": "ADMIN", "joinedAt": "2026-08-14T10:00:00" },
    { "userId": 20, "role": "MEMBER", "joinedAt": "2026-08-16T14:00:00" }
  ]
}
```

### Response Field

| name | 설명 |
| --- | --- |
| `data[].userId` | 팀원의 유저 ID입니다. |
| `data[].role` | 팀 내 역할입니다 (`MEMBER` / `ADMIN`). |
| `data[].joinedAt` | 팀에 합류한 시각입니다. |

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `401 Unauthorized` | `COMMON_401_1` | 인증이 필요합니다. | AccessToken이 없거나 유효하지 않은 경우 |
| `403 Forbidden` | `COMMON_403_1` | 접근 권한이 없습니다. | 요청자가 해당 팀 소속이 아닌 경우 |
