UPDATE feeds
SET visibility = CASE
                     WHEN visibility = '공개' THEN 'PUBLIC'
                     WHEN visibility = '일촌' THEN 'CIRCLE'
                     ELSE 'PRIVATE'
    END;

-- 2) visibility 컬럼을 새로운 ENUM 정의로 변경 (PUBLIC, CIRCLE, PRIVATE 순)
ALTER TABLE feeds
    MODIFY COLUMN visibility ENUM('PUBLIC','CIRCLE','PRIVATE') NOT NULL;

-- 3) 피드에 운동 관련 필드 추가
ALTER TABLE feeds
    ADD COLUMN image            VARCHAR(500)  NULL AFTER visibility,
    ADD COLUMN exercise_type    VARCHAR(50)   NULL AFTER image,
    ADD COLUMN exercise_time    VARCHAR(50)   NULL AFTER exercise_type,
    ADD COLUMN workout_duration INT           NULL AFTER exercise_time;