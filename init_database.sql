-- LabelEven 데이터베이스 초기화 스크립트 (AWS RDS MariaDB)
-- MySQL 클라이언트 또는 DBeaver에서 실행하세요

-- 연결 명령어:
-- mysql -h ac4e0dfc165124d80a3c52c7409e1675-174437012.ap-northeast-2.elb.amazonaws.com -P 3306 -u root -p

-- 데이터베이스 생성 (있으면 스킵)
CREATE DATABASE IF NOT EXISTS labeleven 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 데이터베이스 선택
USE labeleven;

-- 기존 테이블 확인
SHOW TABLES;

-- 현재 데이터베이스 확인
SELECT DATABASE();

-- 테이블이 자동 생성되므로 여기서는 확인만 합니다
SELECT 'Database labeleven is ready for AWS RDS!' as status;
