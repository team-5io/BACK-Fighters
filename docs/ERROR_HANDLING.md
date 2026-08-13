# ERROR_HANDLING.md

## 목적

도메인별 오류 코드와 예외를 일관되게 관리하고, 클라이언트에 안정적인 오류 응답을 제공한다.

## 책임 분리

```text
<Domain>ErrorCode
  └─ HTTP 상태, 오류 코드, 기본 메시지 정의

<Domain> 예외 클래스
  └─ 도메인 의미와 오류 발생 맥락 정의

global.exception
  └─ HTTP 성격별 기반 예외(BadRequestException, NotFoundException, ConflictException,
     ForbiddenException, UnauthorizedException)와 공통 ErrorCode(CommonErrorCode) 제공

GlobalExceptionHandler (global.presentation)
  └─ ApplicationException의 ErrorCode를 공통 HTTP 오류 응답(GlobalApiErrorResponse)으로 변환
```

`GlobalExceptionHandler`는 도메인별 예외를 알 필요가 없다. 모든 도메인 예외는 `global.exception`의 기반 예외(`BusinessException`의 하위 클래스)를 상속하고 `ErrorCode`를 제공해야 한다.

## ErrorCode

각 도메인은 자신의 오류 코드를 `<Domain>ErrorCode` enum으로 소유한다.

```java
@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    EMAIL_DUPLICATE(HttpStatus.CONFLICT, "USER_409_1", "이미 가입된 이메일입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "USER_401_1", "이메일 또는 비밀번호가 일치하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_404_1", "사용자를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
```

오류 코드 형식은 `<DOMAIN>_<HTTP_STATUS>_<SEQUENCE>`를 사용한다.

```text
USER_409_1
DOCPR_404_1
DOCUMENT_403_1
```

## 커스텀 예외

도메인 규칙 위반은 공통 예외를 직접 사용하지 않고, 의미가 드러나는 도메인 예외로 표현한다.

```java
public class EmailDuplicateException extends ConflictException {
    public EmailDuplicateException() {
        super(UserErrorCode.EMAIL_DUPLICATE);
    }
}
```

식별자나 추가 정보가 필요한 경우에는 `addContext`로 오류 맥락을 기록한다.

```java
public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(Long userId) {
        super(UserErrorCode.USER_NOT_FOUND);
        addContext("userId", userId);
    }
}
```

## 공통 예외 기반 클래스

`global.exception`의 기반 예외는 도메인 ErrorCode를 받는 `protected` 생성자를 제공한다.

```text
BadRequestException
NotFoundException
ConflictException
ForbiddenException
UnauthorizedException
```

## HTTP 상태 선택

| 기반 예외 | 사용 시점 |
| --- | --- |
| `BadRequestException` | 도메인 입력값 또는 상태가 유효하지 않음 |
| `NotFoundException` | 요청한 도메인 리소스를 찾을 수 없음 |
| `ConflictException` | 현재 상태 또는 동시성 충돌로 요청을 수행할 수 없음(중복 이메일 등) |
| `ForbiddenException` | 인증은 되었지만 도메인 권한 또는 접근 권한이 없음 |
| `UnauthorizedException` | 인증되지 않음, 토큰이 없거나 유효하지 않음 |

## 도메인 적용 요청 양식

다른 도메인의 담당자에게 아래 형식으로 변경을 요청한다.

```text
[대상 도메인]
<documents | docprs 등>

[변경 요청]
1. <Domain>ErrorCode implements ErrorCode enum 추가
2. 도메인 의미가 드러나는 커스텀 예외 추가
3. 기존 공통 예외(IllegalArgumentException 등) 직접 호출을 도메인 예외로 교체

[주의 사항]
- GlobalExceptionHandler는 변경하지 않습니다.
- 다른 도메인의 Entity, Repository, Service를 직접 참조하지 않습니다.
```

## 현재 적용 상태

| 도메인 | 상태 |
| --- | --- |
| `user` | `UserErrorCode`, `EmailDuplicateException`, `InvalidCredentialsException`, `UserNotFoundException` 적용됨 |
