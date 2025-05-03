insert into users(nickname, password, email) values ('찬영', '1234', 'test123@gmail.com');
insert into users(nickname, password, email) values ('인택', '1234', 'test1234@gmail.com');
insert into users(nickname, password, email) values ('찬일', '1234', 'test1235@gmail.com');
insert into users(nickname, password, email) values ('아웃택', '1234', 'test1236@gmail.com');

INSERT INTO pet (user_id, name, hp, fullness, tired, happiness, thirsty, stress, pet_type, last_updated) VALUES (1,'찬영',90,50, 50, 60, 70, 20, 'cat', '2025-04-29 23:00:00');
INSERT INTO pet (user_id, name, hp, fullness, tired, happiness, thirsty, stress, pet_type, last_updated) VALUES (2,'찬일',80,51, 52, 61, 15, 65, 'dog', '2025-04-29 23:00:00');
INSERT INTO pet (user_id, name, hp, fullness, tired, happiness, thirsty, stress, pet_type, last_updated) VALUES (3,'찬이',70,52, 54, 62, 100, 20, 'cat', '2025-04-29 23:00:00');
INSERT INTO pet (user_id, name, hp, fullness, tired, happiness, thirsty, stress, pet_type, last_updated) VALUES (4,'찬삼',60,53, 56, 63, 99, 30, 'dog', '2025-04-29 23:00:00');
INSERT INTO pet (user_id, name, hp, fullness, tired, happiness, thirsty, stress, pet_type, last_updated) VALUES (1,'찬사',50,54, 58, 64, 1, 50, 'cat', '2025-04-29 23:00:00');
INSERT INTO pet (user_id, name, hp, fullness, tired, happiness, thirsty, stress, pet_type, last_updated) VALUES (2,'찬오',40,55, 60, 65, 30, 35, 'cat','2025-04-29 23:00:00');
INSERT INTO pet (user_id, name, hp, fullness, tired, happiness, thirsty, stress, pet_type, last_updated) VALUES (3,'찬육',30,56, 62, 70, 64, 40, 'cat', '2025-04-29 23:00:00');
INSERT INTO pet (user_id, name, hp, fullness, tired, happiness, thirsty, stress, pet_type, last_updated) VALUES (4,'찬칠',35,57, 64, 68, 44, 75, 'cat', '2025-04-29 23:00:00');

-- Post 테이블 초기 데이터
INSERT INTO post (title, content, nickname, created_at, updated_at)
VALUES ('PetTama 게시판에 오신 것을 환영합니다', '안녕하세요! PetTama 게시판입니다. 반려동물에 관한 다양한 정보와 경험을 공유해주세요.', '관리자', '2025-04-28 12:00:00', '2025-04-28 12:00:00');

INSERT INTO post (title, content, nickname, created_at, updated_at)
VALUES ('반려견 훈련 팁 공유합니다', '안녕하세요! 저는 골든 리트리버를 키우고 있습니다. 몇 가지 유용한 훈련 팁을 공유합니다.\n\n1. 일관성 유지하기: 항상 동일한 명령어를 사용하세요.\n2. 적절한 보상: 좋은 행동에는 간식이나 칭찬으로 보상하세요.\n3. 짧은 훈련 세션: 15-20분 정도가 적당합니다.\n4. 인내심을 가지세요: 모든 개는 배우는 속도가 다릅니다.\n\n여러분의 훈련 팁도 댓글로 공유해주세요!', '강아지사랑', '2025-04-28 14:30:00', '2025-04-28 14:30:00');

INSERT INTO post (title, content, nickname, created_at, updated_at)
VALUES ('고양이 건강 관리 방법', '고양이를 건강하게 키우기 위한 몇 가지 팁입니다.\n\n1. 정기적인 동물병원 방문\n2. 적절한 사료와 영양분 공급\n3. 충분한 물 섭취 유도\n4. 실내 고양이 운동 방법\n5. 그루밍 관리\n\n여러분의 고양이는 어떻게 관리하시나요?', '냥이맘', '2025-04-29 09:15:00', '2025-04-29 09:15:00');

INSERT INTO post (title, content, nickname, created_at, updated_at)
VALUES ('햄스터 케이지 추천해주세요', '곧 햄스터를 입양할 예정입니다. 햄스터에게 좋은 케이지를 추천해주세요. 예산은 5만원 내외입니다. 감사합니다!', '햄찌러버', '2025-04-29 18:45:00', '2025-04-29 18:45:00');

INSERT INTO post (title, content, nickname, created_at, updated_at)
VALUES ('우리 강아지 사진 공유합니다', '우리 코기 강아지 사진입니다. 이름은 콩이에요. 너무 귀엽죠? 여러분의 반려동물 사진도 댓글로 공유해주세요!', '코기맘', '2025-04-30 11:20:00', '2025-04-30 11:20:00');

-- Comment 테이블 초기 데이터
INSERT INTO comment (post_id, name, body)
VALUES (2, '퍼피맘', '좋은 정보 감사합니다! 저는 클리커 훈련도 효과가 좋았어요.');

INSERT INTO comment (post_id, name, body)
VALUES (2, '개린이', '훈련할 때 인내심이 정말 중요한 것 같아요. 저희 강아지는 배우는데 시간이 좀 걸리네요.');

INSERT INTO comment (post_id, name, body)
VALUES (3, '집사1', '저는 고양이 자동 급수기를 사용하는데 물 섭취량이 확실히 늘었어요! 추천합니다.');

INSERT INTO comment (post_id, name, body)
VALUES (3, '고양이집사', '그루밍 브러시 추천해주실 수 있나요? 저희 고양이가 털이 많아서요.');

INSERT INTO comment (post_id, name, body)
VALUES (4, '햄스터전문가', '샌드위치 케이지보다는 수평 면적이 넓은 케이지를 추천드려요. 햄스터는 터널 파는 것을 좋아하거든요.');

INSERT INTO comment (post_id, name, body)
VALUES (4, '소동물러버', '디쏘 케이지가 가성비가 좋아요. 다이소에서 파는 수납함으로 직접 만드는 방법도 있습니다.');

INSERT INTO comment (post_id, name, body)
VALUES (5, '멍뭉이', '너무 귀여워요! 저희 푸들도 사진 올릴게요~');

INSERT INTO comment (post_id, name, body)
VALUES (5, '댕댕이', '콩이 너무 귀엽네요! 코기 엉덩이가 정말 사랑스러워요 ㅎㅎ');