-- V1.2__update_users_table.sql
-- 1. username → nickname 컬럼명 변경
-- 2. profile_image 길이 확장
-- 3. 개인 정보(이름·연령대·성별·지역·선호 그룹 크기) 컬럼 추가
-- 4. 소셜·동의·상태·로그인 관련 컬럼 추가
-- 5. user_type, is_active, is_email_verified 기본값 설정
-- 6. 인덱스·유니크 제약 조건 추가

ALTER TABLE users
    -- 컬럼명 변경 및 길이 조정
    CHANGE COLUMN username             nickname VARCHAR(30) NOT NULL COMMENT '사용자 닉네임 (표시명)',
    MODIFY COLUMN profile_image        VARCHAR(500)     NULL    COMMENT '프로필 이미지 URL',

    -- 개인 정보 추가
    ADD COLUMN age_group               VARCHAR(20)      NULL    COMMENT '연령대 (10대, 20대, ...)',
    ADD COLUMN gender                  VARCHAR(1)          NULL    COMMENT '성별 (M, F)',
    ADD COLUMN region                  VARCHAR(50)      NULL    COMMENT '지역 (서울, 경기 등)',
    ADD COLUMN preferred_group_size    INT              NULL    COMMENT '선호 운동 그룹 인원',

    -- 소셜 로그인 컬럼 추가
    ADD COLUMN social_id               VARCHAR(100)     NULL    COMMENT '소셜 플랫폼 사용자 ID',
    ADD COLUMN social_type             VARCHAR(20)      NULL    COMMENT '소셜 플랫폼 타입 (KAKAO, GOOGLE, NAVER)',
    ADD COLUMN social_profile_image    VARCHAR(500)     NULL    COMMENT '소셜 프로필 이미지 URL',

    -- 전화번호
    ADD COLUMN phone_number            VARCHAR(20)      NULL    COMMENT '전화번호',

    -- 사용자 타입 및 계정 상태
    ADD COLUMN user_type               VARCHAR(20)      NOT NULL DEFAULT 'NORMAL' COMMENT '사용자 타입 (NORMAL: 일반가입, SOCIAL: 소셜가입)',
    ADD COLUMN is_active               BOOLEAN          NOT NULL DEFAULT TRUE    COMMENT '계정 활성화 상태',
    ADD COLUMN is_email_verified       BOOLEAN          NOT NULL DEFAULT FALSE   COMMENT '이메일 인증 상태',

    -- 개인정보 동의
    ADD COLUMN agree_terms             BOOLEAN          NULL    DEFAULT FALSE COMMENT '서비스 이용약관 동의',
    ADD COLUMN agree_privacy           BOOLEAN          NULL    DEFAULT FALSE COMMENT '개인정보처리방침 동의',
    ADD COLUMN agree_marketing         BOOLEAN          NULL    DEFAULT FALSE COMMENT '마케팅 정보 수신 동의 (선택)',

    -- 마지막 로그인 정보
    ADD COLUMN last_login_at           TIMESTAMP        NULL    COMMENT '마지막 로그인 시간',
    ADD COLUMN last_login_ip           VARCHAR(45)      NULL    COMMENT '마지막 로그인 IP'
;

-- 인덱스 추가
CREATE INDEX idx_users_nickname      ON users (nickname);
CREATE INDEX idx_users_created_at    ON users (created_at);
CREATE INDEX idx_users_last_login    ON users (last_login_at);
CREATE INDEX idx_users_social_info   ON users (social_id, social_type);
CREATE INDEX idx_users_user_type     ON users (user_type);

-- 소셜 계정 중복 방지 유니크 제약
ALTER TABLE users
    ADD CONSTRAINT uk_users_social_account UNIQUE (social_id, social_type);
