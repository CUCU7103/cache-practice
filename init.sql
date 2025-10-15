-- 데이터베이스가 존재하지 않으면 생성하고, 해당 데이터베이스를 사용합니다.
-- docker-compose.yml에서 MYSQL_DATABASE로 이미 생성되지만, 스크립트의 독립성을 위해 추가할 수 있습니다.
-- 이 세션의 문자셋을 utf8mb4로 설정합니다.
SET NAMES 'utf8mb4';

USE cache_practice;

-- 영화 정보를 저장하는 'movie' 테이블 생성
CREATE TABLE movie (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       title VARCHAR(100) NOT NULL,
                       description TEXT,
                       genre VARCHAR(50),
                       release_date DATE,
                       duration INT
)  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 영화 상영 스케줄 정보를 저장하는 'movie_schedule' 테이블 생성
CREATE TABLE movie_schedule (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                date DATE,
                                time TIME,
                                theater VARCHAR(255),
                                movie_id BIGINT NOT NULL
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 1. 영화 데이터 삽입
INSERT INTO movie (title, description, genre, release_date, duration) VALUES
                                                                          ('인셉션', '타인의 꿈에 들어가 생각을 훔치는 특수요원들의 이야기', 'SF, 액션', '2010-07-21', 148),
                                                                          ('기생충', '전원백수인 기택네 장남 기우가 고액 과외 면접을 보러 가면서 시작되는 예기치 않은 사건', '드라마, 스릴러', '2019-05-30', 132),
                                                                          ('어벤져스: 엔드게임', '인피니티 워 이후, 살아남은 어벤져스 멤버들이 타노스와의 마지막 전투를 준비한다', '액션, SF', '2019-04-24', 181),
                                                                          ('라라랜드', '꿈을 꾸는 사람들을 위한 별들의 도시, 라라랜드에서 펼쳐지는 두 남녀의 열정과 사랑', '뮤지컬, 로맨스', '2016-12-07', 128),
                                                                          ('쇼생크 탈출', '억울하게 살인 누명을 쓴 은행원이 감옥에서 희망을 잃지 않고 자유를 향한 탈출을 감행한다', '드라마', '1995-01-28', 142);

-- 2. 영화 스케줄 데이터 삽입
INSERT INTO movie_schedule (movie_id, date, time, theater) VALUES
                                                               (1, '2025-10-20', '10:30:00', '1관'),
                                                               (1, '2025-10-20', '14:00:00', '1관'),
                                                               (1, '2025-10-21', '17:30:00', '3관'),
                                                               (2, '2025-10-20', '11:00:00', '2관'),
                                                               (2, '2025-10-21', '15:20:00', '2관'),
                                                               (2, '2025-10-22', '20:00:00', '5관'),
                                                               (3, '2025-10-20', '09:00:00', 'IMAX관'),
                                                               (3, '2025-10-20', '13:00:00', 'IMAX관'),
                                                               (3, '2025-10-21', '21:00:00', '4DX관'),
                                                               (4, '2025-10-22', '12:30:00', '7관'),
                                                               (4, '2025-10-22', '18:00:00', '7관'),
                                                               (4, '2025-10-23', '14:10:00', '8관'),
                                                               (5, '2025-10-25', '10:00:00', '프리미엄관'),
                                                               (5, '2025-10-26', '16:40:00', '프리미엄관'),
                                                               (5, '2025-10-27', '19:00:00', '10관');
