# 문서(Document) API 명세서

> 아래 각 `## ` 섹션이 Notion 하위 페이지 1개(엔드포인트 1개)에 대응합니다. 섹션별로 그대로 복사해서 각 페이지에 붙여넣으세요.
> 공통 성공 응답 포맷: `{ "status", "code", "message", "data" }` (`GlobalApiResponse`).
> 공통 실패 응답 포맷: `{ "timestamp", "status", "code", "message", "traceId", "details" }`
> 모든 API는 `Authorization: Bearer {AccessToken}` 헤더가 필요합니다 (미인증 시 `401 COMMON_401_1`).
> 생성류 응답(문서 생성)은 바디의 `status` 필드가 `201`이어도, 컨트롤러가 `ResponseEntity` 없이 반환해 실제 HTTP 상태 코드는 `200`입니다.
> `restricted=true`인 문서는 작성자 본인 결과에만 노출됩니다 (목록/검색 쿼리에서 필터링). RACI 배정 API가 아직 없어 현재는 어떤 문서도 `restricted=true`로 생성할 수 없습니다.

---

## 1. 문서 생성

`POST /documents`

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {AccessToken}` 형식의 사용자 인증 토큰입니다. |

Request Body

```json
{
  "teamId": 1,
  "title": "온보딩 가이드",
  "content": "문서 내용"
}
```

| name | type | required | description |
| --- | --- | --- | --- |
| `teamId` | `Long` | `true` | 문서가 속할 팀 ID입니다. |
| `title` | `String` | `true` | 문서 제목입니다. |
| `content` | `String` | `false` | 문서 내용입니다. |

# **[response]**

### 성공코드

| HTTP 상태 | 설명 |
| --- | --- |
| `200 OK` | 생성 성공. 요청자는 해당 팀의 팀원이어야 하며, 요청자가 문서의 작성자(R)로 등록됩니다. 항상 `DRAFT` 상태로 생성됩니다. |

Response Body

```json
{
  "status": 201,
  "code": "DOCUMENT_201_1",
  "message": "문서가 생성되었습니다.",
  "data": {
    "id": 100,
    "teamId": 1,
    "authorId": 10,
    "title": "온보딩 가이드",
    "content": "문서 내용",
    "status": "DRAFT",
    "restricted": false
  }
}
```

### Response Field

| name | 설명 |
| --- | --- |
| `data.id` | 생성된 문서 ID입니다. |
| `data.teamId` | 소속 팀 ID입니다. |
| `data.authorId` | 작성자(R) 유저 ID입니다. 생성 요청자로 자동 설정됩니다. |
| `data.title` | 문서 제목입니다. |
| `data.content` | 문서 내용입니다. |
| `data.status` | 문서 상태입니다 (`DRAFT` / `OFFICIAL`). 생성 시 항상 `DRAFT`. |
| `data.restricted` | 지정 참여자 전용 문서 여부입니다. 이 API로는 항상 `false`로 생성됩니다 (RACI 배정 API 미구현). |

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `400 Bad Request` | `COMMON_400_1` | 입력값이 올바르지 않습니다. | `teamId`, `title` 누락 등 Bean Validation 실패 |
| `401 Unauthorized` | `COMMON_401_1` | 인증이 필요합니다. | AccessToken이 없거나 유효하지 않은 경우 |
| `403 Forbidden` | `COMMON_403_1` | 접근 권한이 없습니다. | 요청자가 `teamId` 팀의 팀원이 아닌 경우 |

---

## 2. 문서 편집

`PATCH /documents/{documentId}`

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {AccessToken}` 형식의 사용자 인증 토큰입니다. |

Request Parameter

| name | description |
| --- | --- |
| `documentId` | 수정할 문서의 ID입니다. |

Request Body

```json
{
  "title": "수정된 제목",
  "content": "수정된 내용"
}
```

| name | type | required | description |
| --- | --- | --- | --- |
| `title` | `String` | `true` | 수정할 제목입니다. |
| `content` | `String` | `false` | 수정할 내용입니다. |

# **[response]**

### 성공코드

| HTTP 상태 | 설명 |
| --- | --- |
| `200 OK` | 수정 성공. 문서 작성자(R) 본인만 가능하며, `DRAFT` 상태의 문서만 편집할 수 있습니다. |

Response Body

```json
{
  "status": 200,
  "code": "DOCUMENT_200_1",
  "message": "문서가 수정되었습니다.",
  "data": {
    "id": 100,
    "teamId": 1,
    "authorId": 10,
    "title": "수정된 제목",
    "content": "수정된 내용",
    "status": "DRAFT",
    "restricted": false
  }
}
```

### Response Field

| name | 설명 |
| --- | --- |
| `data.id` | 문서 ID입니다. |
| `data.teamId` | 소속 팀 ID입니다. |
| `data.authorId` | 작성자(R) 유저 ID입니다. |
| `data.title` | 수정된 제목입니다. |
| `data.content` | 수정된 내용입니다. |
| `data.status` | 문서 상태입니다 (편집 후에도 `DRAFT` 유지). |
| `data.restricted` | 지정 참여자 전용 문서 여부입니다. |

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `400 Bad Request` | `COMMON_400_1` | 입력값이 올바르지 않습니다. | `title` 누락 등 |
| `400 Bad Request` | `DOCUMENT_400_1` | 초안 상태의 문서만 편집할 수 있습니다. | 문서 상태가 `DRAFT`가 아닌 경우 (예: 이미 `OFFICIAL`) |
| `401 Unauthorized` | `COMMON_401_1` | 인증이 필요합니다. | AccessToken이 없거나 유효하지 않은 경우 |
| `403 Forbidden` | `COMMON_403_1` | 접근 권한이 없습니다. | 요청자가 문서가 속한 팀의 팀원이 아닌 경우 |
| `403 Forbidden` | `DOCUMENT_403_1` | 해당 문서에 대한 접근 권한이 없습니다. | 요청자가 문서 작성자(R)가 아닌 경우 |
| `404 Not Found` | `DOCUMENT_404_1` | 문서를 찾을 수 없습니다. | `documentId`에 해당하는 문서가 없는 경우 |

---

## 3. 문서 삭제·보관

`DELETE /documents/{documentId}`

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {AccessToken}` 형식의 사용자 인증 토큰입니다. |

Request Parameter

| name | description |
| --- | --- |
| `documentId` | 삭제할 문서의 ID입니다. |

# **[response]**

### 성공코드

| HTTP 상태 | 설명 |
| --- | --- |
| `200 OK` | 삭제 성공. 문서 작성자(R) 또는 팀 관리자(ADMIN)만 가능하며, 문서 상태와 무관하게(초안·공식 모두) 삭제할 수 있습니다. |

Response Body

```json
{
  "status": 200,
  "code": "DOCUMENT_200_2",
  "message": "문서가 삭제되었습니다.",
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
| `403 Forbidden` | `COMMON_403_1` | 접근 권한이 없습니다. | 요청자가 문서가 속한 팀의 팀원이 아니거나, 작성자(R)도 아니고 팀 `ADMIN`도 아닌 경우 |
| `404 Not Found` | `DOCUMENT_404_1` | 문서를 찾을 수 없습니다. | `documentId`에 해당하는 문서가 없는 경우 (동시 삭제로 이미 지워진 경우 포함) |

---

## 4. 문서 목록 조회

`GET /documents`

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {AccessToken}` 형식의 사용자 인증 토큰입니다. |

Request Query Parameter

| name | type | required | description |
| --- | --- | --- | --- |
| `teamId` | `Long` | `true` | 조회할 팀 ID입니다. |
| `page` | `Integer` | `false` | 페이지 번호, 0부터 시작 (기본값 0) |
| `size` | `Integer` | `false` | 페이지당 개수 (기본값 20) |

# **[response]**

### 성공코드

| HTTP 상태 | 설명 |
| --- | --- |
| `200 OK` | 조회 성공. 요청자는 `teamId` 팀의 팀원이어야 합니다. `restricted=true`인 문서는 작성자 본인에게만 노출되고, 그 외 팀원에게는 목록에서 제외됩니다. |

Response Body

```json
{
  "status": 200,
  "code": "DOCUMENT_200_3",
  "message": "문서 목록을 조회했습니다.",
  "data": {
    "content": [
      {
        "id": 100,
        "teamId": 1,
        "authorId": 10,
        "title": "온보딩 가이드",
        "content": "문서 내용",
        "status": "DRAFT",
        "restricted": false
      }
    ],
    "number": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1,
    "first": true,
    "last": true,
    "empty": false
  }
}
```

### Response Field

| name | 설명 |
| --- | --- |
| `data.content` | 문서 목록입니다. 각 항목은 문서 생성/편집 API와 동일한 구조입니다. |
| `data.number` | 현재 페이지 번호입니다 (0부터 시작). |
| `data.size` | 페이지당 개수입니다. |
| `data.totalElements` | 전체 문서 개수입니다 (restricted 필터링 반영됨). |
| `data.totalPages` | 전체 페이지 수입니다. |
| `data.first` / `data.last` | 첫/마지막 페이지 여부입니다. |
| `data.empty` | 결과가 비어있는지 여부입니다. |

Spring Data `Page` 기본 직렬화 형식이라 `pageable`, `sort`, `numberOfElements` 등의 필드가 실제 응답에는 추가로 더 포함됩니다.

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `400 Bad Request` | `COMMON_400_1` | 입력값이 올바르지 않습니다. | `teamId` 쿼리 파라미터 누락 |
| `401 Unauthorized` | `COMMON_401_1` | 인증이 필요합니다. | AccessToken이 없거나 유효하지 않은 경우 |
| `403 Forbidden` | `COMMON_403_1` | 접근 권한이 없습니다. | 요청자가 `teamId` 팀의 팀원이 아닌 경우 |

---

## 5. 문서 검색

`GET /documents/search`

# **[request]**

Request Header

| name | description |
| --- | --- |
| `Authorization` | `Bearer {AccessToken}` 형식의 사용자 인증 토큰입니다. |

Request Query Parameter

| name | type | required | description |
| --- | --- | --- | --- |
| `teamId` | `Long` | `true` | 검색할 팀 ID입니다. |
| `keyword` | `String` | `true` | 제목·내용에서 검색할 키워드입니다. `%`, `_` 등 SQL LIKE 와일드카드 문자는 리터럴로 취급되도록 서버에서 이스케이프 처리됩니다. |
| `page` | `Integer` | `false` | 페이지 번호, 0부터 시작 (기본값 0) |
| `size` | `Integer` | `false` | 페이지당 개수 (기본값 20) |

# **[response]**

### 성공코드

| HTTP 상태 | 설명 |
| --- | --- |
| `200 OK` | 검색 성공. 요청자는 `teamId` 팀의 팀원이어야 합니다. 제목 또는 내용에 키워드가 포함된 문서를 반환하며, `restricted=true`인 문서는 작성자 본인 결과에만 포함됩니다. |

Response Body

```json
{
  "status": 200,
  "code": "DOCUMENT_200_4",
  "message": "문서 검색 결과를 조회했습니다.",
  "data": {
    "content": [
      {
        "id": 100,
        "teamId": 1,
        "authorId": 10,
        "title": "온보딩 가이드",
        "content": "검색어가 포함된 내용",
        "status": "DRAFT",
        "restricted": false
      }
    ],
    "number": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1,
    "first": true,
    "last": true,
    "empty": false
  }
}
```

### Response Field

| name | 설명 |
| --- | --- |
| `data.content` | 검색된 문서 목록입니다. |
| `data.number` / `data.size` | 현재 페이지 번호 / 페이지당 개수입니다. |
| `data.totalElements` / `data.totalPages` | 전체 검색 결과 수 / 전체 페이지 수입니다. |
| `data.first` / `data.last` / `data.empty` | 첫/마지막 페이지 여부, 결과가 비어있는지 여부입니다. |

문서 목록 조회와 동일하게 Spring Data `Page` 기본 직렬화 형식이라 `pageable`, `sort` 등의 필드가 실제 응답에는 추가로 더 포함됩니다.

### 실패 코드

| HTTP 상태 | code | message | 설명 |
| --- | --- | --- | --- |
| `400 Bad Request` | `COMMON_400_1` | 입력값이 올바르지 않습니다. | `teamId` 또는 `keyword` 쿼리 파라미터 누락 |
| `401 Unauthorized` | `COMMON_401_1` | 인증이 필요합니다. | AccessToken이 없거나 유효하지 않은 경우 |
| `403 Forbidden` | `COMMON_403_1` | 접근 권한이 없습니다. | 요청자가 `teamId` 팀의 팀원이 아닌 경우 |
