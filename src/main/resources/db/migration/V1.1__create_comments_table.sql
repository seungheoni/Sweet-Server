-- 댓글(Comment) 테이블 생성
CREATE TABLE comments (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          feed_id BIGINT NOT NULL,
                          user_id BIGINT NOT NULL,
                          text TEXT NOT NULL,
                          created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (feed_id) REFERENCES feeds(id),
                          FOREIGN KEY (user_id) REFERENCES users(id)
);