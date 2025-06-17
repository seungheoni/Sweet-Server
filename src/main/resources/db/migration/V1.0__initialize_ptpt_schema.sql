-- PTPT 프로젝트 초기 스키마 생성 Flyway 마이그레이션 스크립트

-- USERS 테이블: 사용자 정보
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,                       -- 기본 키, 자동 증가
    email VARCHAR(255) NOT NULL,                              -- 사용자 이메일 주소 (최대 255자)
    username VARCHAR(30) NOT NULL,                            -- 고유 사용자명 (최대 30자)
    password VARCHAR(255) NOT NULL,                           -- 해시 처리된 비밀번호
    bio VARCHAR(150),                                         -- 사용자 소개글 (최대 150자)
    profile_image VARCHAR(255),                               -- 프로필 이미지 URL
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,            -- 생성 시간
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,  -- 수정 시간
    UNIQUE (email),                                           -- 이메일 중복 방지
    UNIQUE (username)                                         -- 사용자명 중복 방지
);

-- FEEDS 테이블: 피드(게시글)
CREATE TABLE feeds (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,                       -- 기본 키, 자동 증가
    user_id BIGINT NOT NULL,                                     -- 작성자 (USERS.id 참조)
    title VARCHAR(100),                                       -- 피드 제목 (선택 입력, 최대 100자)
    content TEXT,                                             -- 피드 내용 (최대 2200자, 문자열, 이모지 및 제한된 서식 허용)
    visibility ENUM('비공개','일촌','공개') NOT NULL,         -- 공개 범위: "비공개", "일촌", "공개"
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,            -- 피드 작성 시간
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,  -- 피드 수정 시간
    FOREIGN KEY (user_id) REFERENCES users(id)                -- 외래 키: USERS 테이블의 id 참조
);

-- FEED_IMAGES 테이블: 피드에 첨부된 이미지
CREATE TABLE feed_images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,                       -- 기본 키, 자동 증가
    feed_id BIGINT NOT NULL,                                     -- 피드 참조 (FEEDS.id 참조)
    image_url VARCHAR(500) NOT NULL,                          -- 이미지 URL (최대 500자)
    FOREIGN KEY (feed_id) REFERENCES feeds(id)                -- 외래 키: FEEDS 테이블의 id 참조
);

-- EXERCISE_DETAILS 테이블: 운동 상세 정보 (FEEDS와 1:1 관계)
CREATE TABLE exercise_details (
    feed_id BIGINT PRIMARY KEY,                                  -- 기본 키이자 FEEDS와 1:1 매핑 (FEEDS.id 참조)
    duration VARCHAR(50),                                     -- 총 운동 시간 (예: "30분")
    location VARCHAR(255),                                    -- location (최대 255자)
    FOREIGN KEY (feed_id) REFERENCES feeds(id)                -- 외래 키: FEEDS 테이블의 id 참조
);

-- FEED_EXERCISE_TYPES 테이블: 피드별 다중 운동 타입 (1:N 관계)
CREATE TABLE feed_exercise_types (
    feed_id BIGINT NOT NULL,                                     -- 피드 참조 (FEEDS.id)
    exercise_type VARCHAR(50) NOT NULL,                       -- 운동 타입 (예: running, swimming, cycling 등)
    PRIMARY KEY (feed_id, exercise_type),                     -- 복합 기본 키
    FOREIGN KEY (feed_id) REFERENCES feeds(id)                -- 외래 키: FEEDS 테이블의 id 참조
);

-- FEED_TAGS 테이블: 피드 관련 태그 (피드당 최대 30개, 각 태그 최대 30자)
CREATE TABLE feed_tags (
    feed_id BIGINT NOT NULL,                                     -- 피드 참조 (FEEDS.id)
    tag VARCHAR(30) NOT NULL,                                 -- 태그 (최대 30자)
    PRIMARY KEY (feed_id, tag),                               -- 복합 기본 키 (중복 방지)
    FOREIGN KEY (feed_id) REFERENCES feeds(id)                -- 외래 키: FEEDS 테이블의 id 참조
);

-- FOLLOWS 테이블: 팔로우/팔로잉 관계 (유저당 최대 7500명 제한은 애플리케이션 로직에서 처리)
CREATE TABLE follows (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,                       -- 기본 키, 자동 증가
    follower_id BIGINT NOT NULL,                                 -- 팔로우 요청자 (USERS.id 참조)
    following_id BIGINT NOT NULL,                                -- 팔로잉 대상 (USERS.id 참조)
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,            -- 팔로우 생성 시간
    FOREIGN KEY (follower_id) REFERENCES users(id),           -- 외래 키: USERS 테이블의 id 참조
    FOREIGN KEY (following_id) REFERENCES users(id)           -- 외래 키: USERS 테이블의 id 참조
);

-- ANNOUNCEMENTS 테이블: 공지사항
CREATE TABLE announcements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,                       -- 기본 키, 자동 증가
    title VARCHAR(100) NOT NULL,                              -- 공지 제목 (최대 100자)
    content TEXT,                                             -- 공지 내용 (최대 2200자)
    banner_image VARCHAR(500),                                -- 배너 이미지 URL (최대 500자)
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP             -- 공지 작성 시간
);

-- SOCIAL_GROUPS 테이블: 모임 정보
CREATE TABLE social_groups (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,                       -- 기본 키, 자동 증가
    name VARCHAR(100) NOT NULL,                               -- 그룹 이름 (최대 100자)
    description TEXT,                                         -- 그룹 설명 (최대 2200자)
    created_by BIGINT NOT NULL,                                  -- 그룹 생성자 (USERS.id 참조)
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,            -- 그룹 생성 시간
    FOREIGN KEY (created_by) REFERENCES users(id)            -- 외래 키: USERS 테이블의 id 참조
);

-- SOCIAL_GROUPS_MEMBERS 테이블: 모임 참여자 정보
CREATE TABLE social_groups_members (
   group_id BIGINT NOT NULL,                                    -- 그룹 참조 (SOCIAL_GROUPS.id)
   user_id BIGINT NOT NULL,                                     -- 사용자 참조 (USERS.id)
   joined_at DATETIME DEFAULT CURRENT_TIMESTAMP,           -- 그룹 가입 시간
   PRIMARY KEY (group_id, user_id),                          -- 복합 기본 키
   FOREIGN KEY (group_id) REFERENCES social_groups(id),      -- 외래 키: SOCIAL_GROUPS 테이블의 id 참조
   FOREIGN KEY (user_id) REFERENCES users(id)                -- 외래 키: USERS 테이블의 id 참조
);

-- FEED_LIKES 테이블: 피드 좋아요 내역
CREATE TABLE feed_likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,                       -- 기본 키, 자동 증가
    feed_id BIGINT NOT NULL,                                     -- 피드 참조 (FEEDS.id)
    user_id BIGINT NOT NULL,                                     -- 좋아요를 누른 사용자 (USERS.id)
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,            -- 좋아요 생성 시간
    FOREIGN KEY (feed_id) REFERENCES feeds(id),               -- 외래 키: FEEDS 테이블의 id 참조
    FOREIGN KEY (user_id) REFERENCES users(id)                -- 외래 키: USERS 테이블의 id 참조
);

-- 관리자 기본 계정 데이터: 기본 관리자 계정 생성
INSERT INTO users (email, username, password, bio, profile_image, created_at, updated_at)
VALUES ('admin@ptpt.com', 'admin', 'qwer1234', '관리자 계정', '', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);