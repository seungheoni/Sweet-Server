# 🏋️‍♂️ PTPT 백엔드 서버

## 📌 개요
- PTPT 애플리케이션의 백엔드 서버를 설계 및 구현

## 📌 아키텍처 및 기술 스택

### 📊 데이터베이스 설계 (ERD)
- https://www.notion.so/f-lab/ERD-1cfb4a3c45f1802c80a8d3d842335be0?pvs=4

###  데이터 유효성 검사 정책
- https://www.notion.so/f-lab/1d0b4a3c45f180d8a9baee8520589809?pvs=4

### MockAPI 작성
- API 명세: https://www.notion.so/f-lab/API-1cbb4a3c45f180e69a09fafb765531a9?pvs=4
- 서버실행 후, [여기](http://localhost:8080/swagger-ui/index.html#/) 에서 swagger문서 확인 가능합니다.

### dbms 실행 방법
1. 터미널 실행후 프로젝트 최상단 경로 `Sweet-Server/docker/dbms` 로 이동
2. 해당 경로에서 `docker-compose up -d`

### 🛠️ 기술 스택
- Java 17
- Spring Boot
- Spring Data JPA
- MySQL