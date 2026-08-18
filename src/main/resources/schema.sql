-- Doc PR 스키마 (docs/erd.sql 초안 기준, MySQL 8)
-- 앱 기동 시마다 실행됨 (spring.sql.init.mode: always). 전부 CREATE TABLE IF NOT EXISTS라 재실행해도 안전하지만,
-- 이미 만들어진 테이블의 컬럼 변경은 반영되지 않으므로 컬럼을 바꿨다면 로컬에서 해당 테이블/DB를 직접 지우고 재기동할 것.

-- =========================================================
-- 계정 및 팀 관리
-- =========================================================

CREATE TABLE IF NOT EXISTS users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    public_id   CHAR(36)     NOT NULL COMMENT '외부(AI 서버/프론트) 노출용 ID, 내부 PK(id)는 그대로 유지',
    email       VARCHAR(255) NOT NULL,
    password    VARCHAR(255) NOT NULL COMMENT 'BCrypt 인코딩된 비밀번호',
    name        VARCHAR(100) NOT NULL,
    timezone    VARCHAR(50)  NULL COMMENT '예: Asia/Seoul',
    language    VARCHAR(10)  NULL COMMENT '선호 언어, 예: ko, en, vi',
    created_at  DATETIME     NOT NULL,
    updated_at  DATETIME     NOT NULL,
    UNIQUE KEY uk_users_email (email),
    UNIQUE KEY uk_users_public_id (public_id)
) ENGINE=InnoDB COMMENT '회원';

CREATE TABLE IF NOT EXISTS teams (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    created_at  DATETIME     NOT NULL,
    updated_at  DATETIME     NOT NULL
) ENGINE=InnoDB COMMENT '팀(프로젝트) 공간';

CREATE TABLE IF NOT EXISTS team_members (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    team_id     BIGINT       NOT NULL,
    user_id     BIGINT       NOT NULL,
    role        ENUM('MEMBER', 'ADMIN') NOT NULL DEFAULT 'MEMBER' COMMENT 'ADMIN=팀 관리자',
    joined_at   DATETIME     NOT NULL,
    UNIQUE KEY uk_team_members_team_user (team_id, user_id),
    CONSTRAINT fk_team_members_team FOREIGN KEY (team_id) REFERENCES teams (id),
    CONSTRAINT fk_team_members_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB COMMENT '팀 소속(팀원 목록/역할)';

-- =========================================================
-- Doc PR 기반 문서 관리
-- =========================================================

CREATE TABLE IF NOT EXISTS documents (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    team_id       BIGINT       NOT NULL,
    author_id     BIGINT       NOT NULL COMMENT '최초 작성자',
    title         VARCHAR(255) NOT NULL,
    content       LONGTEXT     NULL COMMENT '블록 본문에서 파생된 평문 캐시(검색용) — 진짜 본문은 blocks 테이블',
    status        ENUM('DRAFT', 'OFFICIAL') NOT NULL DEFAULT 'DRAFT',
    is_restricted BOOLEAN      NOT NULL DEFAULT FALSE COMMENT '지정 참여자 전용 문서 여부(RACI 배정 시 TRUE)',
    created_at    DATETIME     NOT NULL,
    updated_at    DATETIME     NOT NULL,
    CONSTRAINT fk_documents_team FOREIGN KEY (team_id) REFERENCES teams (id),
    CONSTRAINT fk_documents_author FOREIGN KEY (author_id) REFERENCES users (id)
) ENGINE=InnoDB COMMENT '문서';

CREATE TABLE IF NOT EXISTS blocks (
    id              VARCHAR(64)  PRIMARY KEY COMMENT 'FE가 생성하는 블록 id',
    document_id     BIGINT       NOT NULL,
    parent_block_id VARCHAR(64)  NULL COMMENT '상위 블록 id(최상위면 NULL). 문서 저장 시 전체 삭제 후 재삽입하므로 FK 제약 없음',
    sort_order      INT          NOT NULL COMMENT '같은 부모 안에서의 순서',
    type            VARCHAR(20)  NOT NULL COMMENT 'paragraph/heading1~3/bulleted/numbered/todo/toggle/quote/code/divider',
    content         TEXT         NULL,
    checked         BOOLEAN      NULL COMMENT 'todo 전용',
    collapsed       BOOLEAN      NULL COMMENT 'toggle 전용',
    language        VARCHAR(20)  NULL COMMENT 'code 전용',
    CONSTRAINT fk_blocks_document FOREIGN KEY (document_id) REFERENCES documents (id),
    INDEX idx_blocks_document (document_id)
) ENGINE=InnoDB COMMENT '문서 블록(노션 스타일 에디터)';

CREATE TABLE IF NOT EXISTS document_versions (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    document_id  BIGINT   NOT NULL,
    version_no   INT      NOT NULL,
    content      LONGTEXT NOT NULL COMMENT '해당 버전 시점의 스냅샷',
    doc_pr_id    BIGINT   NULL COMMENT '이 버전을 만든 Doc PR (초기 버전은 NULL 가능)',
    created_at   DATETIME NOT NULL,
    UNIQUE KEY uk_document_versions_doc_version (document_id, version_no),
    CONSTRAINT fk_document_versions_document FOREIGN KEY (document_id) REFERENCES documents (id)
) ENGINE=InnoDB COMMENT '문서 버전별 변경 이력';

CREATE TABLE IF NOT EXISTS document_relations (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    source_document_id  BIGINT NOT NULL,
    target_document_id  BIGINT NOT NULL,
    relation_type       ENUM('PARENT', 'CHILD', 'REFERENCE', 'DEPENDENCY') NOT NULL,
    created_at          DATETIME NOT NULL,
    CONSTRAINT fk_document_relations_source FOREIGN KEY (source_document_id) REFERENCES documents (id),
    CONSTRAINT fk_document_relations_target FOREIGN KEY (target_document_id) REFERENCES documents (id)
) ENGINE=InnoDB COMMENT '문서 관계 그래프(상위/하위/참조/의존) - Document Graph, Impact Analysis 기반';

CREATE TABLE IF NOT EXISTS document_raci (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    document_id  BIGINT   NOT NULL,
    user_id      BIGINT   NOT NULL,
    raci_role    ENUM('R', 'A', 'C', 'I') NOT NULL,
    assigned_by  BIGINT   NOT NULL COMMENT '지정한 팀 관리자',
    assigned_at  DATETIME NOT NULL,
    UNIQUE KEY uk_document_raci_doc_user (document_id, user_id),
    CONSTRAINT fk_document_raci_document FOREIGN KEY (document_id) REFERENCES documents (id),
    CONSTRAINT fk_document_raci_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_document_raci_assigner FOREIGN KEY (assigned_by) REFERENCES users (id)
) ENGINE=InnoDB COMMENT '문서별 RACI 역할 배정';

CREATE TABLE IF NOT EXISTS doc_prs (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    document_id       BIGINT   NOT NULL,
    requester_id      BIGINT   NOT NULL COMMENT '작성자(R)',
    approver_id       BIGINT   NOT NULL COMMENT '승인권자(A)',
    next_assignee_id  BIGINT   NULL COMMENT 'Follow-the-Sun 다음 작업자',
    proposed_content  LONGTEXT NOT NULL COMMENT '이 Doc PR이 제안하는 변경 내용',
    status            ENUM('CREATED', 'AI_REVIEW', 'HUMAN_REVIEW', 'APPROVED', 'REJECTED', 'RESUBMITTED', 'REVIEWER_NEEDED', 'MERGED') NOT NULL DEFAULT 'CREATED',
    is_exception_merge BOOLEAN NOT NULL DEFAULT FALSE COMMENT '차단 조건 무시하고 예외적으로 머지됐는지',
    exception_reason  VARCHAR(500) NULL COMMENT '예외 머지 사유',
    merged_at         DATETIME NULL,
    created_at        DATETIME NOT NULL,
    updated_at        DATETIME NOT NULL,
    CONSTRAINT fk_doc_prs_document FOREIGN KEY (document_id) REFERENCES documents (id),
    CONSTRAINT fk_doc_prs_requester FOREIGN KEY (requester_id) REFERENCES users (id),
    CONSTRAINT fk_doc_prs_approver FOREIGN KEY (approver_id) REFERENCES users (id),
    CONSTRAINT fk_doc_prs_next_assignee FOREIGN KEY (next_assignee_id) REFERENCES users (id)
) ENGINE=InnoDB COMMENT 'Doc PR (문서 초안 → 검토 → 머지 워크플로우)';

CREATE TABLE IF NOT EXISTS doc_pr_status_histories (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    doc_pr_id   BIGINT       NOT NULL,
    from_status VARCHAR(30)  NULL,
    to_status   VARCHAR(30)  NOT NULL,
    actor_id    BIGINT       NOT NULL COMMENT '상태를 변경한 사용자',
    reason      VARCHAR(500) NULL,
    created_at  DATETIME     NOT NULL,
    CONSTRAINT fk_doc_pr_status_histories_doc_pr FOREIGN KEY (doc_pr_id) REFERENCES doc_prs (id),
    CONSTRAINT fk_doc_pr_status_histories_actor FOREIGN KEY (actor_id) REFERENCES users (id)
) ENGINE=InnoDB COMMENT 'Doc PR 상태 전이 이력(생성/AI리뷰/사람리뷰/반려/재제출/확정 등)';

CREATE TABLE IF NOT EXISTS doc_pr_reviews (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    doc_pr_id   BIGINT   NOT NULL,
    reviewer_id BIGINT   NOT NULL COMMENT 'C 또는 A',
    comment     TEXT     NOT NULL,
    created_at  DATETIME NOT NULL,
    CONSTRAINT fk_doc_pr_reviews_doc_pr FOREIGN KEY (doc_pr_id) REFERENCES doc_prs (id),
    CONSTRAINT fk_doc_pr_reviews_reviewer FOREIGN KEY (reviewer_id) REFERENCES users (id)
) ENGINE=InnoDB COMMENT '사람 리뷰 의견';

CREATE TABLE IF NOT EXISTS ai_reviews (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    doc_pr_id         BIGINT   NOT NULL,
    has_conflict      BOOLEAN  NOT NULL DEFAULT FALSE COMMENT '연결 문서와의 상충 여부',
    is_consistent     BOOLEAN  NOT NULL DEFAULT TRUE COMMENT '기존 결정과의 정합성',
    violates_charter  BOOLEAN  NOT NULL DEFAULT FALSE COMMENT '협업 규칙 위반 여부',
    evidence          TEXT     NULL COMMENT '검토 근거',
    reviewed_at       DATETIME NOT NULL,
    UNIQUE KEY uk_ai_reviews_doc_pr (doc_pr_id),
    CONSTRAINT fk_ai_reviews_doc_pr FOREIGN KEY (doc_pr_id) REFERENCES doc_prs (id)
) ENGINE=InnoDB COMMENT 'DocumentLion AI 리뷰 결과';

CREATE TABLE IF NOT EXISTS cio_reviews (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    doc_pr_id    BIGINT   NOT NULL,
    ai_feature   ENUM('WRITING_ASSISTANT', 'DOCUMENT_LION', 'TRANSLATION') NOT NULL,
    is_approved  BOOLEAN  NOT NULL COMMENT 'CIO 2차 검토 승인 여부',
    reason       VARCHAR(500) NULL,
    reviewed_at  DATETIME NOT NULL,
    CONSTRAINT fk_cio_reviews_doc_pr FOREIGN KEY (doc_pr_id) REFERENCES doc_prs (id)
) ENGINE=InnoDB COMMENT 'AI 제안 결과 CIO 2차 검토 (Doc PR 승인/Merge와는 무관, 품질 검수용)';

CREATE TABLE IF NOT EXISTS notifications (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id           BIGINT       NOT NULL COMMENT '수신자',
    type              ENUM('REVIEW_REQUESTED', 'STATUS_CHANGED', 'NEXT_ASSIGNEE_ASSIGNED') NOT NULL,
    message           VARCHAR(500) NOT NULL,
    related_doc_pr_id BIGINT       NULL,
    is_read           BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at        DATETIME     NOT NULL,
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_notifications_doc_pr FOREIGN KEY (related_doc_pr_id) REFERENCES doc_prs (id)
) ENGINE=InnoDB COMMENT '알림';

-- =========================================================
-- 언어 보더 해소
-- =========================================================

CREATE TABLE IF NOT EXISTS translations (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    document_id        BIGINT       NOT NULL,
    block_id           VARCHAR(64)  NOT NULL COMMENT '블록 단위로 번역하므로 blocks.id 참조 (FK 제약은 걸지 않음, blocks와 동일한 이유)',
    source_language    VARCHAR(10)  NOT NULL,
    target_language    VARCHAR(10)  NOT NULL,
    translated_content LONGTEXT     NOT NULL COMMENT '코드블록/식별자는 원문 보존, 나머지만 번역',
    preserved_terms    TEXT         NULL COMMENT '원문 그대로 보존된 코드 토큰 목록(콤마 구분), AI 응답의 preservedTerms',
    created_at         DATETIME     NOT NULL,
    CONSTRAINT fk_translations_document FOREIGN KEY (document_id) REFERENCES documents (id),
    INDEX idx_translations_document_block_lang (document_id, block_id, target_language)
) ENGINE=InnoDB COMMENT 'Dev-aware Translation 결과';

-- =========================================================
-- 문화 보더 해소
-- =========================================================

CREATE TABLE IF NOT EXISTS team_collaboration_charters (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    team_id     BIGINT   NOT NULL,
    content     LONGTEXT NOT NULL COMMENT 'AI 생성 초안 또는 팀이 수정·확정한 협업 규칙',
    status      ENUM('DRAFT', 'ADOPTED') NOT NULL DEFAULT 'DRAFT',
    created_at  DATETIME NOT NULL,
    updated_at  DATETIME NOT NULL,
    UNIQUE KEY uk_team_collaboration_charters_team (team_id),
    CONSTRAINT fk_team_collaboration_charters_team FOREIGN KEY (team_id) REFERENCES teams (id)
) ENGINE=InnoDB COMMENT '팀별 협업 규칙 헌장 (DocumentLion 결재 기준으로 사용)';

-- =========================================================
-- 비기능요구사항
-- =========================================================

CREATE TABLE IF NOT EXISTS document_access_logs (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    document_id    BIGINT   NOT NULL,
    user_id        BIGINT   NOT NULL,
    access_result  ENUM('SUCCESS', 'DENIED') NOT NULL,
    accessed_at    DATETIME NOT NULL,
    CONSTRAINT fk_document_access_logs_document FOREIGN KEY (document_id) REFERENCES documents (id),
    CONSTRAINT fk_document_access_logs_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB COMMENT '지정 참여자 전용 문서 접근 감사 로그';
