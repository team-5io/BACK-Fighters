# ARCHITECTURE.md

## 목적

이 문서는 Spring Server의 구조와 의존성 규칙을 정의한다.

본 서버는 하나의 Spring Boot 애플리케이션으로 배포되는 모듈형 모놀리스이다. 도메인별 담당자는 자신의 도메인 패키지를 소유하고, 다른 도메인의 내부 구현을 직접 수정하거나 참조하지 않는다.

## 목표

- 비즈니스 규칙을 중심에 둔다.
- HTTP, Spring, JPA, DB, 외부 API는 구현 세부사항으로 분리한다.
- 변경 영향 범위를 줄이고 도메인 간 결합을 낮춘다.

## 도메인 소유권

- 각 도메인은 자신의 패키지 내부 코드와 데이터 모델만 수정한다.
- 다른 도메인의 내부 Service, JPA Entity, Repository, Adapter를 직접 수정하거나 참조하지 않는다.
- 타 도메인 조회가 필요하면 요청 도메인이 필요한 최소 Port와 응답 DTO를 정의한다.
- 데이터를 소유한 대상 도메인은 해당 Port를 구현하는 조회 Adapter를 자기 Infrastructure에 두고, 자기 Domain Repository와 Persistence Adapter, Spring Data JPA Repository를 통해 필요한 값만 조회한다.
- 이 간소화 방식의 타 도메인 Adapter 추가·수정은 대상 도메인 담당자의 사전 동의가 있어야 한다.
- 공통화가 필요하더라도 특정 도메인 규칙을 임의로 Global 영역으로 이동하지 않는다.

## 공유 플랫폼 영역

- `global/`은 도메인 모듈이 아닌 공유 플랫폼 영역이다.
- 인증 정보 추출, Security Context 처리, 공통 예외 응답, 공통 설정처럼 도메인에 종속되지 않는 기술 책임만 둔다.
- 도메인 비즈니스 규칙, 도메인 데이터와 상태, JPA Entity, Repository, 도메인별 Application API를 소유하지 않는다.
- `global/`은 도메인 모듈을 참조하거나 의존하지 않는다. 도메인 모듈은 공통 기술 기능이 필요한 경우에만 `global/`에 의존할 수 있다.

## 구조

프로젝트는 레이어드 구조를 기본으로 한다. 외부 시스템 연동, 영속성 구현 교체 가능성, 또는 도메인 간 공개 계약이 필요한 도메인에 한하여 Port/Adapter 구조를 적용한다. 단순한 내부 기능만 가진 도메인은 레이어드 구조를 유지한다.

```text
presentation / adapter-in
            ↓
       application
            ↓
         domain
            ↑
infrastructure / adapter-out
```

의존성은 기본적으로 안쪽을 향한다.

```text
presentation → application → domain
infrastructure → application 또는 domain
```

## 계층 책임

| 계층 | 역할 | 포함하면 안 되는 것 |
| --- | --- | --- |
| presentation / adapter-in | HTTP 요청·응답, 요청 검증, 인증 정보 전달, Command 생성, UseCase 호출 | 도메인 규칙, DB 직접 접근 |
| application | 유스케이스 조립, 트랜잭션, Port 호출, 도메인 객체 호출, 이벤트 발행, 실행 권한 판단 | HTTP 세부사항, JPA Entity 직접 의존, 복잡한 규칙 구현 |
| domain | 도메인 모델, Aggregate, 상태 변경, 비즈니스 규칙, 도메인 예외 | Spring, JPA, HTTP, 외부 API 의존 |
| infrastructure / adapter-out | JPA, DB, 외부 API, 메시징, 파일 저장소, Port 구현 | Presentation 책임, 도메인 규칙 판단 |

## UseCase와 Service

- 오퍼레이션 하나당 `application.usecase` 하위에 `<Operation>UseCase` 인터페이스를 두고, `application.service` 하위의 `<Operation>Service`가 이를 구현한다.
- Controller는 구현체(`<Operation>Service`)가 아니라 `<Operation>UseCase` 인터페이스에만 의존한다.
- 오퍼레이션 하나가 여러 단계(예: 로그인 인증 + 토큰 발급)로 이뤄지면, 그 조합도 해당 UseCase 구현체 안에서 끝내고 Controller가 여러 Service를 순서대로 호출하지 않는다.
- 조회 전용 UseCase는 `application.query` 하위의 `<Domain>QueryUseCase`로 분리할 수 있다. Query는 상태를 변경하지 않는다.

```text
user/application/
├─ usecase/
│  ├─ SignupUseCase.java
│  ├─ LoginUseCase.java
│  ├─ LogoutUseCase.java
│  └─ UpdateProfileUseCase.java
└─ service/
   ├─ SignupService.java   (implements SignupUseCase)
   ├─ LoginService.java    (implements LoginUseCase)
   ├─ LogoutService.java   (implements LogoutUseCase)
   └─ UpdateProfileService.java (implements UpdateProfileUseCase)
```

## Command, Query와 API DTO

- Command는 `application.command`에 둔다. UseCase의 입력으로 사용한다.
- 여러 값을 함께 반환해야 하면 `application.result`에 `<Operation>Result`를 둔다(예: `LoginResult(User user, String accessToken)`).
- Request와 Response는 HTTP API 계약이므로 `presentation.api.request`, `presentation.api.response`에 둔다.
- Controller는 Request를 Command로 변환해 UseCase를 호출하고, UseCase의 반환값(도메인 객체 또는 Result)을 Response로 변환한다.
- Application과 Domain은 Presentation의 Request, Response, Swagger 어노테이션을 직접 참조하지 않는다.

## 인증과 인가

- 인증 정보 추출과 Security Context 처리는 `global/security`가 담당한다(`JwtAuthenticationFilter`가 토큰을 검증해 `AuthUser`를 `SecurityContext`에 채운다).
- Controller는 `@AuthenticationPrincipal AuthUser`로 현재 사용자를 받는다. 클라이언트가 보낸 ID·역할 값(헤더, 바디 등)을 인증의 최종 근거로 사용하지 않는다.
- 유스케이스 실행 권한 판단(리소스 소유권, RACI 역할 등)은 Application 또는 Domain이 담당한다. Controller에 비즈니스 권한 규칙을 두지 않는다.

## 트랜잭션

- 트랜잭션의 시작과 종료는 Application 계층이 담당한다.
- 하나의 유스케이스는 필요한 상태 변경을 하나의 트랜잭션 경계 안에서 처리한다.
- 외부 API 호출(AI 리뷰·번역 등), 메시지 발행 등 네트워크 I/O를 DB 트랜잭션에 장시간 포함하지 않는다.

## Port와 Adapter

- Application 또는 Domain은 외부 구현체가 아닌 Port에 의존한다.
- Infrastructure Adapter는 Port를 구현하여 DB, 외부 API, 다른 도메인과 연결한다.
- 외부 구현 기술이 변경되어도 Port 계약이 유지되면 핵심 비즈니스 로직은 변경하지 않는다.

```text
Application Service
→ Outbound Port
→ Infrastructure Adapter
→ DB / External API / Other Domain
```

## Domain Model과 JPA Entity

- Domain Model은 비즈니스 규칙을 표현한다.
- JPA Entity는 DB 저장 구조를 표현한다.
- 두 모델의 변환은 Persistence Adapter 또는 Mapper 내부에서 수행한다.
- Domain은 JPA 어노테이션과 영속성 기술을 알지 못한다.
- 생성일·수정일이 필요한 JPA Entity는 직접 필드를 선언하지 않고 `global.infrastructure.persistence.BaseTimeEntity`를 상속한다.

### Mapper 규칙

- Domain Model과 JPA Entity 간 변환은 MapStruct를 사용한다.
- Mapper는 `infrastructure.persistence` 하위에 둔다.
- 모든 Mapper는 `global.infrastructure.config.MapStructConfig`를 사용한다.
- Domain Model은 private 생성자와 public Builder를 사용한다.
- Application과 Domain 계층은 JPA Entity를 직접 참조하지 않는다.

## 예외 처리

- 도메인 규칙 위반은 도메인 예외와 ErrorCode로 표현한다. 세부 규칙은 [ERROR_HANDLING.md](ERROR_HANDLING.md) 참고.
- Domain Exception은 HTTP 상태 코드에 의존하지 않는다.
- HTTP 상태 코드와 API 오류 응답 변환은 `GlobalExceptionHandler`가 담당한다.
- 비밀번호는 반드시 `PasswordEncoder`로 인코딩해 저장하며, 평문 비교를 하지 않는다.

## 코드 리뷰 체크리스트

1. 비즈니스 규칙과 기술 세부사항이 분리되어 있는가?
2. 의존성이 Application과 Domain 방향을 침범하지 않는가?
3. Domain이 Spring, JPA, HTTP, 외부 API를 알고 있지 않은가?
4. 다른 도메인의 내부 모델을 직접 참조하거나 수정하지 않는가?
5. 인증 판단이 클라이언트가 보낸 값이 아니라 검증된 토큰(`AuthUser`)에 근거하는가?
6. 예외가 `ApplicationException` 계열로 던져져 `GlobalExceptionHandler`가 처리할 수 있는가?
7. 비밀번호·시크릿 등이 평문으로 저장·비교되지 않는가?
