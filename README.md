# LabelEven Backend

수출용 라벨 규격 진단 시스템 백엔드 API

## 기술 스택

- Java 17
- Spring Boot 3.2.0
- Spring Security + JWT
- Spring Data JPA
- MariaDB
- Maven

## 프로젝트 구조

```
labelevenBE/
├── src/
│   ├── main/
│   │   ├── java/com/labeleven/
│   │   │   ├── config/          # 설정 클래스
│   │   │   ├── controller/      # REST 컨트롤러
│   │   │   ├── dto/             # 데이터 전송 객체
│   │   │   ├── entity/          # JPA 엔티티
│   │   │   ├── repository/      # JPA 리포지토리
│   │   │   ├── service/         # 비즈니스 로직
│   │   │   ├── security/        # JWT 인증
│   │   │   ├── exception/       # 예외 처리
│   │   │   └── LabelEvenApplication.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
└── pom.xml
```

## 설정

1. MariaDB 데이터베이스 생성:
```sql
CREATE DATABASE labeleven CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. pplication.yml 수정:
   - 데이터베이스 접속 정보
   - JWT 시크릿 키
   - AWS S3 정보 (선택사항)

## 실행

```bash
# Maven으로 빌드
mvn clean install

# 실행
mvn spring-boot:run
```

## API 엔드포인트

### 인증 (Auth)
- POST /api/auth/login - 로그인
- POST /api/auth/register - 회원가입
- GET /api/auth/check-username - 사용자명 중복 확인
- GET /api/auth/check-email - 이메일 중복 확인

### 프로젝트 (Project)
- POST /api/projects/upload - 프로젝트 생성 및 파일 업로드
- GET /api/projects - 사용자의 프로젝트 목록 조회
- GET /api/projects/{id} - 프로젝트 상세 조회
- DELETE /api/projects/{id} - 프로젝트 삭제
- POST /api/projects/{id}/images - 이미지 업로드

### 라벨 데이터 (Label Data)
- GET /api/label-data/project/{projectId} - 프로젝트의 라벨 데이터 조회
- GET /api/label-data/{id} - 라벨 데이터 상세 조회

### 사용자 (User)
- GET /api/users/me - 현재 사용자 정보 조회

## 인증

모든 API 요청(로그인/회원가입 제외)은 JWT 토큰이 필요합니다:

```
Authorization: Bearer {token}
```

## 라이센스

MIT
