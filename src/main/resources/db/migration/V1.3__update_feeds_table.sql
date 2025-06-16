-- V1.3__update_feeds_table.sql
-- 1) 기존 visibility 값 매핑 (한글 → 영문)
UPDATE feeds
SET visibility = CASE
                     WHEN visibility = '공개' THEN 'PUBLIC'
                     ELSE 'PRIVATE'
    END;

-- 2) visibility 컬럼을 새로운 ENUM 정의로 변경
ALTER TABLE feeds
    MODIFY COLUMN visibility ENUM('PUBLIC','PRIVATE') NOT NULL;

-- 3) 피드에 운동 관련 필드 추가
ALTER TABLE feeds
    ADD COLUMN image VARCHAR(500)       NULL AFTER visibility,
  ADD COLUMN exercise_type VARCHAR(50) NULL AFTER image,
  ADD COLUMN exercise_time VARCHAR(50) NULL AFTER exercise_type,
  ADD COLUMN workout_duration INT     NULL AFTER exercise_time;
