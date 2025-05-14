-- ================================================
-- 1) USERS 테이블에 10명 초기 사용자 삽입
-- ================================================
INSERT INTO users (email, password, nickname, monthly_income) VALUES
  ('it0615@naver.com',  '$2a$10$eemmjZcT3TQQJy9u9ffwcRhJsQBaLLhCyLvkrTmXVRdYqLtBNQqDbO',  '차인택', 1000000),
  ('user2@example.com',  '{noop}pass2',  'user2', 3200),
  ('user3@example.com',  '{noop}pass3',  'user3', 3500),
  ('user4@example.com',  '{noop}pass4',  'user4', 2800),
  ('user5@example.com',  '{noop}pass5',  'user5', 4000),
  ('user6@example.com',  '{noop}pass6',  'user6', 3100),
  ('user7@example.com',  '{noop}pass7',  'user7', 3600),
  ('user8@example.com',  '{noop}pass8',  'user8', 2900),
  ('user9@example.com',  '{noop}pass9',  'user9', 3300),
  ('user10@example.com', '{noop}pass10','user10',3700);

-- ================================================
-- 2) POST 테이블에 10개 게시글 삽입
-- ================================================
INSERT INTO post (title, content, nickname, created_at, updated_at) VALUES
  ('제목1',  '내용1',  'user1',  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('제목2',  '내용2',  'user2',  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('제목3',  '내용3',  'user3',  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('제목4',  '내용4',  'user4',  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('제목5',  '내용5',  'user5',  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('제목6',  '내용6',  'user6',  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('제목7',  '내용7',  'user7',  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('제목8',  '내용8',  'user8',  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('제목9',  '내용9',  'user9',  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('제목10', '내용10', 'user10', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ================================================
-- 3) COMMENT 테이블에 10개 댓글 삽입
--    * 외래키 컬럼명은 post_id로 주의 *
-- ================================================
INSERT INTO comment (name, body, post_id) VALUES
  ('visitor1', '댓글1', 1),
  ('visitor2', '댓글2', 2),
  ('visitor3', '댓글3', 3),
  ('visitor4', '댓글4', 4),
  ('visitor5', '댓글5', 5),
  ('visitor6', '댓글6', 6),
  ('visitor7', '댓글7', 7),
  ('visitor8', '댓글8', 8),
  ('visitor9', '댓글9', 9),
  ('visitor10','댓글10',10);

-- ================================================
-- 4) ITEM 테이블에 10개 아이템 삽입
-- ================================================
INSERT INTO item (
  name, description, price, item_type,
  happiness_effect, fullness_effect, hydration_effect, energy_effect, stress_reduction
) VALUES
  ('사료_기본',   '기본 사료',    1000, 'FOOD',  5, 10, 0, 0, 0),
  ('사료_고급',   '고급 사료',    5000, 'FOOD', 15, 50, 0, 0, 0),
  ('물병',       '물병 아이템',  500,  'ACCESSORY', 0, 0, 20, 0, 0),
  ('장난감공',   '장난감 공',    2000, 'TOY',   20, 0, 0, 10, 0),
  ('간식_비스킷','비스킷 간식', 3000, 'FOOD', 30, 30, 0, 0, 0),
  ('낚싯대',     '낚싯대 아이템',7000, 'TOY',   0, 0, 0, 20, 0),
  ('침대',       '포근한 침대',  8000, 'ACCESSORY',0, 0, 0, 0, 10),
  ('간식_젤리',  '젤리 간식',    2500, 'FOOD', 10, 20, 0, 0, 0),
  ('인형',       '귀여운 인형',  1500, 'TOY',   25, 0, 0, 5, 0),
  ('사료_스페셜','특별 사료',    12000,'FOOD', 40, 60, 0, 0, 0);

-- ================================================
-- 5) USER_ITEM 테이블에 10개 구매 이력 삽입
-- ================================================
INSERT INTO user_item (user_id, item_id, quantity, purchase_date) VALUES
  (1,  1, 2, CURRENT_TIMESTAMP),
  (2,  2, 1, CURRENT_TIMESTAMP),
  (3,  3, 3, CURRENT_TIMESTAMP),
  (4,  4, 1, CURRENT_TIMESTAMP),
  (5,  5, 2, CURRENT_TIMESTAMP),
  (6,  6, 1, CURRENT_TIMESTAMP),
  (7,  7, 1, CURRENT_TIMESTAMP),
  (8,  8, 2, CURRENT_TIMESTAMP),
  (9,  9, 1, CURRENT_TIMESTAMP),
  (10,10,1, CURRENT_TIMESTAMP);

-- ================================================
-- 6) PET 테이블에 10개 반려동물 삽입
-- ================================================
INSERT INTO pet (
  user_id, name, pet_type, hp, fullness,
  tired, happiness, thirsty, stress,
  last_updated, last_fed_time,
  is_sleeping, sleep_start_time, sleep_end_time,
  is_walking, walk_start_time, walk_end_time
) VALUES
  (1, '펫1', 'DOG', 100, 80, 20, 70, 30, 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE, NULL, NULL, FALSE, NULL, NULL),
  (2, '펫2', 'CAT', 95,  85, 15, 75, 25,  5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE, NULL, NULL, FALSE, NULL, NULL),
  (3, '펫3', 'DOG', 90,  70, 30, 60, 35, 15, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE, NULL, NULL, FALSE, NULL, NULL),
  (4, '펫4', 'CAT', 85,  65, 40, 55, 45, 20, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE, NULL, NULL, FALSE, NULL, NULL),
  (5, '펫5', 'DOG', 80,  75, 25, 65, 20, 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE, NULL, NULL, FALSE, NULL, NULL),
  (6, '펫6', 'CAT', 70,  60, 50, 50, 50, 30, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE, NULL, NULL, FALSE, NULL, NULL),
  (7, '펫7', 'DOG', 95,  90, 10, 85, 15,  5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE, NULL, NULL, FALSE, NULL, NULL),
  (8, '펫8', 'CAT', 88,  78, 22, 68, 28, 12, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE, NULL, NULL, FALSE, NULL, NULL),
  (9, '펫9', 'DOG', 92,  82, 18, 72, 32,  8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE, NULL, NULL, FALSE, NULL, NULL),
  (10,'펫10','CAT', 87,  77, 27, 62, 42, 22, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE, NULL, NULL, FALSE, NULL, NULL);
