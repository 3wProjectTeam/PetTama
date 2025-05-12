/**
 * PetTama - 간소화된 자바스크립트 파일
 * 서버 측 Spring Security 인증을 활용
 */

document.addEventListener('DOMContentLoaded', function() {
    // DOM 요소
    const petListUl = document.getElementById('petList');
    const petDetailsSection = document.querySelector('.pet-details-section');
    const petNameInput = document.getElementById('petNameInput');
    const createPetButton = document.getElementById('createPetButton');
    const creationStatusP = document.getElementById('creationStatus');
    const actionStatusP = document.getElementById('actionStatus');
    const actionButtons = document.querySelectorAll('.action-button');
    const petDetailName = document.getElementById('petDetailName');
    const petTypeOptions = document.querySelectorAll('.pet-type-option');
    const userNicknameDisplay = document.getElementById('userNicknameDisplay');

    // 스탯 관련 요소
    const statBars = {
        hp: document.getElementById('hpBar'),
        fullness: document.getElementById('fullnessBar'),
        happiness: document.getElementById('happinessBar'),
        tired: document.getElementById('tiredBar'),
        thirsty: document.getElementById('thirstyBar'),
        stress: document.getElementById('stressBar')
    };

    const statValues = {
        hp: document.getElementById('hpValue'),
        fullness: document.getElementById('fullnessValue'),
        happiness: document.getElementById('happinessValue'),
        tired: document.getElementById('tiredValue'),
        thirsty: document.getElementById('thirstyValue'),
        stress: document.getElementById('stressValue')
    };

    // 펫 상태 관련 요소
    const petImage = document.querySelector('.pet-image');
    const petStateBadge = document.querySelector('.pet-state-badge');
    const petRecommendation = document.getElementById('petRecommendation');

    // 모달 관련 요소 (먹이 선택 모달)
    const feedModal = document.getElementById('feedModal');
    const feedItemsList = document.getElementById('feedItemsList');
    const cancelFeedButton = document.getElementById('cancelFeedButton');

    // 상태 저장 변수
    let currentUserId = null;
    let currentPetId = null;
    let petsData = [];
    let selectedPetType = "DOG"; // 기본 선택 펫 타입
    let feedCooldownTimer = null; // 먹이 쿨타임 타이머
    let sleepTimerInterval = null; // 수면 타이머

    // 펫 이모지 매핑
    const petEmojis = {
        'DOG': '🐶',
        'CAT': '🐱',
        'default': '🐱'
    };

    // 상태 이모지 매핑
    const stateEmojis = {
        'HAPPY': '😊',
        'HUNGRY': '🍽️',
        'TIRED': '😴',
        'BORED': '😐',
        'STRESSED': '😰',
        'THIRSTY': '💧',
        'SICK': '🤒',
        'CRITICAL': '⚠️'
    };

    // 상태 한글 이름 매핑
    const stateNames = {
        'HAPPY': '행복함',
        'HUNGRY': '배고픔',
        'TIRED': '피곤함',
        'BORED': '지루함',
        'STRESSED': '스트레스',
        'THIRSTY': '목마름',
        'SICK': '아픔',
        'CRITICAL': '위험!'
    };

    // API 경로
    const API_BASE_URL = '/api/user-nums';

    // === 유틸리티 함수 ===

    /**
     * 상태 메시지 표시
     * @param {HTMLElement} element - 상태 메시지를 표시할 요소
     * @param {string} message - 메시지 내용
     * @param {string} type - 메시지 타입 (success, error, info)
     * @param {number} duration - 표시 시간(ms), 0이면 계속 표시
     */
    function showStatusMessage(element, message, type, duration) {
        if (!element) return;

        type = type || 'info';
        duration = duration || 3000;

        // 기존 클래스 제거
        element.classList.remove('success', 'error', 'info');

        // 새 클래스 및 메시지 설정
        element.classList.add(type);
        element.textContent = message;

        // 일정 시간 후 메시지 숨김
        if (duration > 0) {
            setTimeout(function() {
                element.textContent = '';
                element.classList.remove(type);
            }, duration);
        }
    }

    /**
     * API 요청 처리 - 리다이렉트 처리 추가
     * @param {string} url - API 엔드포인트
     * @param {Object} options - fetch 옵션
     * @returns {Promise<any>} - API 응답
     */
// API 요청 처리 함수 수정 - 더 자세한 오류 처리
    async function fetchAPI(url, options) {
        try {
            console.log(`API 요청: ${url}`, options);

            // 리다이렉트 처리 옵션 추가
            const fetchOptions = {
                ...options,
                redirect: 'follow', // 서버 리다이렉트를 따르도록 설정
                credentials: 'include' // 인증 정보(쿠키) 포함
            };

            const response = await fetch(url, fetchOptions);
            console.log(`응답 상태: ${response.status} ${response.statusText}`);

            // 서버 리다이렉트 확인
            if (response.redirected) {
                console.log(`리다이렉트 감지: ${response.url}`);
                window.location.href = response.url;
                return null;
            }

            // 인증 오류 시 로그인 페이지로 리디렉션
            if (response.status === 401 || response.status === 403) {
                console.log('인증 오류 감지');
                // 현재 URL을 redirect 파라미터로 전달
                const currentPath = encodeURIComponent(window.location.pathname);
                window.location.href = `/auth/login?redirect=${currentPath}`;
                throw new Error('로그인이 필요합니다');
            }

            if (!response.ok) {
                // 응답 본문 가져오기 시도
                let errorText;
                try {
                    errorText = await response.text();
                } catch (e) {
                    errorText = `응답 본문을 읽을 수 없음: ${e.message}`;
                }

                console.error(`API 오류: ${response.status} ${response.statusText}`, errorText);
                throw new Error(errorText || `API 요청 실패: ${response.status} ${response.statusText}`);
            }

            // JSON 파싱 시도
            try {
                const data = await response.json();
                console.log('API 응답 데이터:', data);
                return data;
            } catch (e) {
                console.error('JSON 파싱 오류:', e);
                throw new Error(`응답 데이터 처리 오류: ${e.message}`);
            }
        } catch (error) {
            console.error('API 요청 오류:', error);
            throw error;
        }
    }

// 현재 로그인한 사용자 정보 가져오기 함수 수정
    async function fetchCurrentUser() {
        try {
            // 로그인 정보 가져오기 전에 상태 표시
            if (petListUl) {
                petListUl.innerHTML = '<li class="loading">사용자 정보를 불러오는 중...</li>';
            }

            const response = await fetch('/api/auth/user');

            // 응답 상태 확인
            if (!response.ok) {
                if (response.status === 401 || response.status === 403) {
                    petListUl.innerHTML = '<li class="error">로그인이 필요합니다.</li>';
                    return null;
                }
                const errorText = await response.text();
                throw new Error(errorText || `서버 오류: ${response.status}`);
            }

            const user = await response.json();

            // 리다이렉트된 경우 (fetchAPI 내부에서 처리)
            if (!user) return null;

            console.log('사용자 정보 로드 성공:', user);
            currentUserId = user.id;

            // 닉네임 표시
            if (userNicknameDisplay) {
                userNicknameDisplay.textContent = user.nickname;
            }

            // 펫 생성 섹션 헤더 업데이트
            const petCreationHeader = document.querySelector('.pet-creation h2');
            if (petCreationHeader) {
                petCreationHeader.textContent = `새 펫 만들기 (${user.nickname}님)`;
            }

            // 환영 메시지 추가
            displayWelcomeMessage(user.nickname);

            // 펫 목록 자동 로드
            if (user.id) {
                fetchPets(user.id);
            } else {
                console.error('사용자 ID가 없습니다.');
                petListUl.innerHTML = '<li class="error">사용자 정보가 올바르지 않습니다.</li>';
            }

            return user;
        } catch (error) {
            console.error('사용자 정보 가져오기 실패:', error);
            if (petListUl) {
                petListUl.innerHTML = '<li class="error">사용자 정보를 불러오는데 실패했습니다.</li>';
            }
            return null;
        }
    }

    /**
     * 사용자의 모든 펫 가져오기
     * @param {number} userId - 사용자 ID
     */
// 사용자의 모든 펫 가져오기 함수 수정
    async function fetchPets(userId) {
        try {
            // API 호출 전에 상태 메시지 표시
            petListUl.innerHTML = '<li class="loading">펫 목록을 불러오는 중...</li>';

            // API 호출
            const response = await fetch(`${API_BASE_URL}/${userId}`);

            // 응답 상태 확인
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || `서버 오류: ${response.status}`);
            }

            // 응답 데이터 파싱
            const pets = await response.json();

            // 로그 추가
            console.log('펫 목록 로드 성공:', pets);

            // petType이 없는 경우 기본값 설정
            petsData = pets.map(function(pet, index) {
                if (!pet.petType) {
                    pet.petType = 'CAT';
                }
                return {...pet};
            });

            displayPetList(petsData);
        } catch (error) {
            console.error('펫 목록 로드 실패:', error);
            showStatusMessage(creationStatusP, "펫 목록을 불러오는데 실패했습니다: " + error.message, 'error');
            petListUl.innerHTML = '<li class="error">펫 목록을 불러오는데 실패했습니다. 다시 시도해주세요.</li>';

            // 오류 세부정보 로깅
            if (error.message) {
                console.log('오류 메시지:', error.message);
            }
            if (error.stack) {
                console.log('오류 스택:', error.stack);
            }
        }
    }

    /**
     * 특정 펫의 상세 정보 가져오기
     * @param {number} userId - 사용자 ID
     * @param {number} petId - 펫 ID
     */
    async function fetchPetDetails(userId, petId) {
        try {
            const pet = await fetchAPI(`${API_BASE_URL}/${userId}/pets/${petId}`);

            // petType이 없는 경우 기본값 설정
            if (!pet.petType) {
                pet.petType = 'CAT';
            }

            // 펫 상세 정보 표시
            displayPetDetails(pet);

            // 현재 펫 ID 저장
            currentPetId = petId;

            // 펫 상세 섹션 표시
            petDetailsSection.style.display = 'block';

            // 선택한 펫 목록 항목 강조
            highlightSelectedPet(petId);

            // 먹이 아이템 사용 가능 여부 확인
            checkFeedAvailability(currentUserId);
        } catch (error) {
            console.error('펫 상세 정보 로드 실패:', error);
            showStatusMessage(actionStatusP, "펫 정보를 불러오는데 실패했습니다", 'error');
        }
    }

    /**
     * 펫 목록 화면에 표시
     * @param {Array} pets - 펫 목록 데이터
     */
    function displayPetList(pets) {
        petListUl.innerHTML = '';

        if (pets.length === 0) {
            petListUl.innerHTML = '<li>등록된 펫이 없습니다.</li>';
            return;
        }

        pets.forEach(function(pet) {
            const li = document.createElement('li');
            // 펫 타입에 따른 이모지 표시
            const petEmoji = petEmojis[pet.petType] || petEmojis.default;
            li.textContent = `${petEmoji} ${pet.name}`;
            li.dataset.petId = pet.id;

            // 펫 클릭 시 상세 정보 표시
            li.addEventListener('click', function() {
                fetchPetDetails(currentUserId, pet.id);
            });

            petListUl.appendChild(li);
        });
    }

    /**
     * 선택한 펫 목록 항목 강조
     * @param {number} petId - 선택한 펫 ID
     */
    function highlightSelectedPet(petId) {
        // 모든 항목에서 선택 클래스 제거
        document.querySelectorAll('#petList li').forEach(function(item) {
            item.classList.remove('selected');
        });

        // 선택한 항목에 클래스 추가
        const selectedItem = document.querySelector('#petList li[data-pet-id="' + petId + '"]');
        if (selectedItem) {
            selectedItem.classList.add('selected');
        }
    }

    /**
     * 펫 상세 정보 화면에 표시
     * @param {Object} pet - 펫 데이터
     */
    function displayPetDetails(pet) {
        // 펫 이름 표시
        petDetailName.textContent = pet.name;

        // 스탯 값 및 바 업데이트
        updateStatBar('hp', pet.hp);
        updateStatBar('fullness', pet.fullness);
        updateStatBar('happiness', pet.happiness);
        updateStatBar('tired', pet.tired);
        updateStatBar('thirsty', pet.thirsty);
        updateStatBar('stress', pet.stress);

        // 상태 정보 업데이트
        updatePetState(pet);

        // 먹이 버튼 쿨타임 상태 업데이트
        updateFeedButtonCooldown(pet);

        // 수면 상태 확인 및 처리
        if (pet.sleeping) {
            // 수면 종료 시간 가져오기
            const sleepEndTime = new Date(pet.sleepEndTime);
            const now = new Date();

            // 수면 상태가 종료되었는지 확인
            if (now >= sleepEndTime) {
                // 수면 종료됨 - 버튼 활성화
                toggleActionButtons(false);
            } else {
                // 아직 수면 중 - 버튼 비활성화
                toggleActionButtons(true, sleepEndTime);

                // 수면 중 상태 표시
                petStateBadge.textContent = '수면 중';
                petStateBadge.className = 'pet-state-badge';
                petStateBadge.classList.add('sleeping');

                // 수면 아이콘으로 변경
                petImage.textContent = '💤';

                // 추천 메시지 변경
                petRecommendation.textContent = '펫이 자고 있습니다. 깨우지 말고 기다려주세요.';

                // 수면 타이머 시작
                startSleepTimer(sleepEndTime);
            }
        } else {
            // 수면 중이 아님 - 버튼 활성화
            toggleActionButtons(false);
        }
    }

    /**
     * 스탯 바 및 값 업데이트
     * @param {string} statName - 스탯 이름
     * @param {number} value - 스탯 값
     */
    function updateStatBar(statName, value) {
        const bar = statBars[statName];
        const valueElement = statValues[statName];

        if (bar && valueElement) {
            // 값 표시
            valueElement.textContent = value;

            // 바 너비 설정 (0-100%)
            bar.style.width = value + "%";

            // 낮은 값/높은 값에 따른 스타일 변경
            if (statName === 'hp' || statName === 'fullness' || statName === 'happiness') {
                // 이 스탯들은 높을수록 좋음
                if (value <= 20) {
                    bar.style.backgroundColor = '#EF5350'; // 위험
                } else if (value <= 50) {
                    bar.style.backgroundColor = '#FFA726'; // 주의
                } else {
                    bar.style.backgroundColor = ''; // 기본 색상
                }
            } else {
                // 이 스탯들은 낮을수록 좋음 (tired, thirsty, stress)
                if (value >= 80) {
                    bar.style.backgroundColor = '#EF5350'; // 위험
                } else if (value >= 50) {
                    bar.style.backgroundColor = '#FFA726'; // 주의
                } else {
                    bar.style.backgroundColor = ''; // 기본 색상
                }
            }
        }
    }

    /**
     * 펫 상태 정보 업데이트
     * @param {Object} pet - 펫 데이터
     */
    function updatePetState(pet) {
        // 수면 중이면 별도 처리
        if (pet.sleeping) {
            return;
        }

        // FSM에서 상태 정보가 오는 경우
        const state = pet.state || determineState(pet);
        const recommendation = pet.recommendation || getDefaultRecommendation(state);

        // 펫 타입에 맞는 이모지 가져오기
        const petTypeEmoji = petEmojis[pet.petType] || petEmojis.default;

        // 상태 뱃지 업데이트
        petStateBadge.textContent = stateNames[state] || '정상';
        petStateBadge.className = 'pet-state-badge'; // 기존 클래스 초기화
        petStateBadge.classList.add(state.toLowerCase());

        // 펫 이미지 업데이트 (상태 이모지와 펫 타입 이모지 조합)
        if (state === 'CRITICAL' || state === 'SICK') {
            petImage.textContent = stateEmojis[state];
        } else {
            petImage.textContent = petTypeEmoji;
        }

        // 추천 메시지 업데이트
        petRecommendation.textContent = recommendation;
    }

    /**
     * 먹이 버튼의 쿨타임 상태 업데이트
     * @param {Object} pet - 펫 데이터
     */
    function updateFeedButtonCooldown(pet) {
        const feedButton = document.querySelector('.action-button[data-action="feed"]');
        if (!feedButton) return; // 버튼이 없는 경우 종료

        const feedActionLabel = feedButton.querySelector('.action-label');
        const originalLabel = "밥 주기";

        // 먹이 버튼 초기화
        feedButton.disabled = false;
        feedButton.classList.remove('cooldown');
        feedActionLabel.innerHTML = originalLabel;

        // 마지막 먹이 시간이 있는 경우 쿨타임 계산
        if (pet.lastFedTime) {
            const lastFed = new Date(pet.lastFedTime);
            const now = new Date();
            const diffMs = now - lastFed;
            const diffHours = diffMs / (1000 * 60 * 60);

            // 5시간 쿨타임 적용
            if (diffHours < 5) {
                // 남은 시간 계산 (시:분 형식, 초 단위 제외)
                const remainingMs = (5 * 60 * 60 * 1000) - diffMs;
                const remainingHours = Math.floor(remainingMs / (1000 * 60 * 60));
                const remainingMinutes = Math.floor((remainingMs % (1000 * 60 * 60)) / (1000 * 60));

                // 남은 시간 형식화 (초 단위 표시 제거)
                let timeText = '';
                if (remainingHours > 0) {
                    timeText = `${remainingHours}시간 ${remainingMinutes}분 후`;
                } else {
                    timeText = `${remainingMinutes}분 후`;
                }

                // 먹이 버튼 비활성화 및 남은 시간 표시
                feedButton.disabled = true;
                feedButton.classList.add('cooldown');
                feedActionLabel.innerHTML = `밥 주기<br><span class="cooldown-text">${timeText}</span>`;

                // 기존 타이머가 있으면 정리
                if (feedCooldownTimer) {
                    clearInterval(feedCooldownTimer);
                }

                // 1분마다 남은 시간 업데이트하는 타이머 설정 (1000ms * 60 = 60000ms = 1분)
                feedCooldownTimer = setInterval(() => {
                    const updatedNow = new Date();
                    const updatedDiffMs = updatedNow - lastFed;
                    const updatedRemainingMs = (5 * 60 * 60 * 1000) - updatedDiffMs;

                    if (updatedRemainingMs <= 0) {
                        // 쿨타임 종료
                        clearInterval(feedCooldownTimer);
                        feedCooldownTimer = null;

                        // 버튼 상태 복원
                        feedButton.disabled = false;
                        feedButton.classList.remove('cooldown');
                        feedActionLabel.innerHTML = originalLabel;
                        return;
                    }

                    // 남은 시간 업데이트 (초 단위 표시 제거)
                    const updatedHours = Math.floor(updatedRemainingMs / (1000 * 60 * 60));
                    const updatedMinutes = Math.floor((updatedRemainingMs % (1000 * 60 * 60)) / (1000 * 60));

                    let updatedTimeText = '';
                    if (updatedHours > 0) {
                        updatedTimeText = `${updatedHours}시간 ${updatedMinutes}분 후`;
                    } else {
                        updatedTimeText = `${updatedMinutes}분 후`;
                    }

                    // 버튼 텍스트 업데이트
                    feedActionLabel.innerHTML = `밥 주기<br><span class="cooldown-text">${updatedTimeText}</span>`;
                }, 60000); // 1분마다 업데이트 (1000ms * 60 = 60000ms)

            } else {
                // 쿨타임이 지났으면 타이머 초기화
                if (feedCooldownTimer) {
                    clearInterval(feedCooldownTimer);
                    feedCooldownTimer = null;
                }
            }
        }
    }

    /**
     * 액션 버튼 활성화/비활성화 토글
     * @param {boolean} disabled - 버튼 비활성화 여부
     * @param {Date} [endTime] - 수면 종료 시간 (선택적)
     */
    function toggleActionButtons(disabled, endTime) {
        const actionButtons = document.querySelectorAll('.action-button');

        actionButtons.forEach(button => {
            button.disabled = disabled;

            if (disabled) {
                button.classList.add('cooldown');

                // 수면 버튼인 경우 특별 처리
                if (button.dataset.action === 'sleep') {
                    const actionLabel = button.querySelector('.action-label');
                    if (actionLabel) {
                        // 남은 시간 계산 및 표시
                        if (endTime) {
                            const now = new Date();
                            const diffMs = endTime - now;
                            const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
                            const diffMinutes = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60));

                            actionLabel.innerHTML = `자는 중...<br><span class="cooldown-text">${diffHours}시간 ${diffMinutes}분 후 기상</span>`;
                        } else {
                            actionLabel.innerHTML = '자는 중...';
                        }
                    }
                }
            } else {
                button.classList.remove('cooldown');

                // 수면 버튼 원래대로 복원
                if (button.dataset.action === 'sleep') {
                    const actionLabel = button.querySelector('.action-label');
                    if (actionLabel) {
                        actionLabel.textContent = '재우기';
                    }
                }
            }
        });
    }

    /**
     * 수면 타이머 시작
     * @param {Date} sleepEndTime - 수면 종료 시간
     */
    function startSleepTimer(sleepEndTime) {
        // 기존 타이머가 있으면 정리
        if (sleepTimerInterval) {
            clearInterval(sleepTimerInterval);
        }

        // 1분마다 업데이트 (60 * 1000 = 1분)
        sleepTimerInterval = setInterval(() => {
            updateSleepTimerDisplay(sleepEndTime);
        }, 60000);

        // 초기 업데이트
        updateSleepTimerDisplay(sleepEndTime);
    }

    /**
     * 수면 타이머 표시 업데이트
     * @param {Date} sleepEndTime - 수면 종료 시간
     */
    function updateSleepTimerDisplay(sleepEndTime) {
        const now = new Date();
        const endTime = new Date(sleepEndTime);

        // 수면이 종료되었는지 확인
        if (now >= endTime) {
            // 타이머 중지
            clearInterval(sleepTimerInterval);
            sleepTimerInterval = null;

            // 최신 정보 가져오기
            if (currentUserId && currentPetId) {
                fetchPetDetails(currentUserId, currentPetId);
            }

            return;
        }

        // 남은 시간 계산
        const diffMs = endTime - now;
        const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
        const diffMinutes = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60));

        // 수면 버튼 텍스트 업데이트
        const sleepButton = document.querySelector('.action-button[data-action="sleep"]');
        if (sleepButton) {
            const actionLabel = sleepButton.querySelector('.action-label');
            if (actionLabel) {
                actionLabel.innerHTML = `자는 중...<br><span class="cooldown-text">${diffHours}시간 ${diffMinutes}분 후 기상</span>`;
            }
        }

        // 추천 메시지 업데이트
        const petRecommendation = document.getElementById('petRecommendation');
        if (petRecommendation) {
            petRecommendation.textContent = `펫이 자고 있습니다. ${diffHours}시간 ${diffMinutes}분 후에 깨어납니다.`;
        }
    }

    /**
     * 수면 상태 관련 에러 처리
     * @param {string} errorMessage - 에러 메시지
     * @returns {boolean} - 처리 여부
     */
    function handleSleepError(errorMessage) {
        // "펫이 자고 있습니다" 메시지를 포함하는 오류 확인
        if (errorMessage.includes("펫이 자고 있습니다")) {
            // 시간 정보 추출 (형식: "펫이 자고 있습니다. YYYY-MM-DD HH:MM:SS까지 깨울 수 없습니다.")
            const dateTimeMatch = errorMessage.match(/(\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2})/);

            if (dateTimeMatch && dateTimeMatch[1]) {
                const sleepEndTimeStr = dateTimeMatch[1];
                const sleepEndTime = new Date(sleepEndTimeStr.replace(' ', 'T') + '.000+09:00'); // UTC+9 (한국 시간대) 처리

                // 현재 시간과 종료 시간의 차이 계산
                const now = new Date();
                const diffMs = sleepEndTime - now;

                if (diffMs > 0) {
                    const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
                    const diffMinutes = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60));

                    // 버튼 비활성화 및 상태 업데이트
                    toggleActionButtons(true, sleepEndTime);

                    // 수면 상태 시각적 표시
                    petStateBadge.textContent = '수면 중';
                    petStateBadge.className = 'pet-state-badge sleeping';

                    // 수면 아이콘으로 변경
                    petImage.textContent = '💤';

                    // 수면 상태 메시지 표시
                    petRecommendation.textContent = `펫이 자고 있습니다. ${diffHours}시간 ${diffMinutes}분 후에 깨어납니다.`;

                    // 수면 타이머 시작
                    startSleepTimer(sleepEndTime);

                    return true; // 처리 완료
                }
            }
        }

        return false; // 처리되지 않음
    }

    /**
     * 펫 상태 결정 (백엔드 상태 정보가 없는 경우 대비)
     * @param {Object} pet - 펫 데이터
     * @returns {string} - 결정된 상태
     */
    function determineState(pet) {
        // 위험한 상태 먼저 확인
        if (pet.hp <= 10) return 'CRITICAL';

        if (pet.fullness <= 20) return 'HUNGRY';
        if (pet.thirsty >= 80) return 'THIRSTY';
        if (pet.tired >= 80) return 'TIRED';
        if (pet.stress >= 80) return 'STRESSED';
        if (pet.happiness <= 20) return 'BORED';

        // 건강한 상태
        return 'HAPPY';
    }

    /**
     * 상태별 기본 추천 메시지
     * @param {string} state - 펫 상태
     * @returns {string} - 추천 메시지
     */
    function getDefaultRecommendation(state) {
        switch (state) {
            case 'HUNGRY':
                return '펫이 배고파합니다! 상점에서 음식을 구매해 주세요.';
            case 'THIRSTY':
                return '펫이 목말라합니다! 물을 주세요.';
            case 'TIRED':
                return '펫이 피곤해합니다! 휴식이 필요합니다.';
            case 'BORED':
                return '펫이 지루해합니다! 놀아주세요.';
            case 'STRESSED':
                return '펫이 스트레스를 받고 있습니다! 빗질이나 산책을 해주세요.';
            case 'SICK':
                return '펫이 아픕니다! 모든 상태를 관리해주세요.';
            case 'CRITICAL':
                return '경고! 펫이 위험한 상태입니다! 즉시 관리가 필요합니다!';
            case 'HAPPY':
                return '펫이 행복해합니다! 잘 돌보고 계시네요!';
            default:
                return '펫을 정기적으로 확인해주세요.';
        }
    }

    /**
     * 펫 생성
     * @param {string} name - 펫 이름
     * @param {string} petType - 펫 타입
     */
    async function createPet(name, petType) {
        try {
            showStatusMessage(creationStatusP, '펫 생성 중...', 'info');
            console.log(`펫 생성 요청: 이름=${name}, 타입=${petType}`); // 디버깅용

            const newPet = await fetchAPI(
                `${API_BASE_URL}/${currentUserId}?name=${encodeURIComponent(name)}&petType=${encodeURIComponent(petType)}`,
                { method: 'POST' }
            );

            showStatusMessage(creationStatusP, `"${newPet.name}" 생성 완료!`, 'success');
            petNameInput.value = ''; // 입력 필드 초기화

            // 펫 목록 새로고침
            fetchPets(currentUserId);
        } catch (error) {
            console.error('펫 생성 실패:', error);
            showStatusMessage(creationStatusP, '펫 생성 실패: ' + error.message, 'error');
        }
    }

    /**
     * 펫 액션 수행
     * @param {number} petId - 펫 ID
     * @param {string} action - 액션 (feed, play, sleep 등)
     */
    async function performPetAction(petId, action) {
        try {
            showStatusMessage(actionStatusP, getActionName(action) + " 중...", 'info');

            // 버튼 애니메이션
            const actionButton = document.querySelector(`.action-button[data-action="${action}"]`);
            if (actionButton) {
                actionButton.classList.add('action-success');
                setTimeout(() => {
                    actionButton.classList.remove('action-success');
                }, 500);
            }

            // API 요청
            const updatedPet = await fetchAPI(
                `${API_BASE_URL}/${currentUserId}/pets/${petId}/${action}`,
                { method: 'PUT' }
            );

            // petType이 없는 경우 기존 데이터에서 가져오기
            if (!updatedPet.petType) {
                const existingPet = petsData.find(p => p.id === petId);
                updatedPet.petType = existingPet ? existingPet.petType : 'CAT';
            }

            displayPetDetails(updatedPet);

            // 성공 메시지
            if (action === 'sleep') {
                showStatusMessage(actionStatusP, `"${getActionName(action)}" 완료! 펫이 8시간 동안 잠을 잡니다.`, 'success');
            } else {
                showStatusMessage(actionStatusP, `"${getActionName(action)}" 완료!`, 'success');
            }

            // 로컬 펫 데이터 업데이트
            const index = petsData.findIndex(p => p.id === petId);
            if (index !== -1) {
                petsData[index] = {...petsData[index], ...updatedPet};
            }
        } catch (error) {
            console.error(`${action} 액션 실패:`, error);

            // 수면 상태 에러 특별 처리
            if (!handleSleepError(error.message)) {
                showStatusMessage(actionStatusP, `${getActionName(action)} 실패: ${error.message}`, 'error');
            }
        }
    }

    /**
     * 사용자의 먹이 아이템 목록 불러오기
     * @param {number} userId - 사용자 ID
     */
    async function loadFeedItems(userId) {
        try {
            const feedItemsList = document.getElementById('feedItemsList');
            feedItemsList.innerHTML = '<div class="loading">먹이 아이템을 불러오는 중...</div>';

            // 사용자 아이템 목록 불러오기
            const userItems = await fetchAPI(`/api/items/user/${userId}`);

            // 먹이 타입 아이템만 필터링
            const feedItems = userItems.filter(item =>
                item.item.itemType === 'FOOD' && item.quantity > 0
            );

            if (feedItems.length === 0) {
                feedItemsList.innerHTML = `
                    <div class="empty-message">
                        <p>사용 가능한 먹이 아이템이 없습니다.</p>
                        <a href="/shop" class="primary-btn">상점으로 이동</a>
                    </div>
                `;
                return;
            }

            // 먹이 아이템 목록 렌더링
            const itemsHtml = feedItems.map(userItem => {
                const item = userItem.item;
                return `
                    <div class="feed-item" data-item-id="${item.id}">
                        <div class="feed-item-icon">🍗</div>
                        <div class="feed-item-info">
                            <div class="feed-item-name">${item.name}</div>
                            <div class="feed-item-description">${item.description}</div>
                            <div class="feed-item-effects">
                                <span>포만감 +${item.fullnessEffect}</span>
                                ${item.happinessEffect > 0 ? `<span>행복도 +${item.happinessEffect}</span>` : ''}
                            </div>
                        </div>
                        <div class="feed-item-quantity">
                            ${userItem.quantity}개
                        </div>
                        <button class="use-feed-button primary-btn" data-item-id="${item.id}">
                            사용하기
                        </button>
                    </div>
                `;
            }).join('');

            feedItemsList.innerHTML = itemsHtml;

            // 아이템 사용 버튼에 이벤트 리스너 추가
            document.querySelectorAll('.use-feed-button').forEach(button => {
                button.addEventListener('click', function() {
                    const itemId = parseInt(this.dataset.itemId, 10);
                    useFeedItem(currentPetId, itemId);
                    document.getElementById('feedModal').style.display = 'none';
                });
            });
        } catch (error) {
            console.error('먹이 아이템 로드 실패:', error);
            document.getElementById('feedItemsList').innerHTML = `
                <div class="error-message">먹이 아이템을 불러오는데 실패했습니다: ${error.message}</div>
            `;
        }
    }

    /**
     * 먹이 아이템 사용
     * @param {number} petId - 펫 ID
     * @param {number} itemId - 아이템 ID
     */
    async function useFeedItem(petId, itemId) {
        try {
            showStatusMessage(actionStatusP, "먹이를 주는 중...", 'info');

            // 아이템 사용 API 호출
            const updatedPet = await fetchAPI(
                `/api/items/use/${currentUserId}/${petId}/${itemId}`,
                { method: 'POST' }
            );

            // 펫 정보 업데이트
            displayPetDetails(updatedPet);

            // 성공 메시지
            showStatusMessage(actionStatusP, "먹이 주기 완료!", 'success');

            // 로컬 펫 데이터 업데이트
            const index = petsData.findIndex(p => p.id === petId);
            if (index !== -1) {
                petsData[index] = {...petsData[index], ...updatedPet};
            }

            // 성공 시 사용자 인벤토리 다시 로드 (다음 번 사용을 위해)
            checkFeedAvailability(currentUserId);
        } catch (error) {
            console.error('먹이 주기 실패:', error);
            // 수면 상태 에러 특별 처리
            if (!handleSleepError(error.message)) {
                showStatusMessage(actionStatusP, `먹이 주기 실패: ${error.message}`, 'error');
            }
        }
    }

    /**
     * 먹이 아이템 사용 가능 여부 확인
     * @param {number} userId - 사용자 ID
     */
    async function checkFeedAvailability(userId) {
        try {
            // 사용자 아이템 목록 불러오기
            const userItems = await fetchAPI(`/api/items/user/${userId}`);

            // 먹이 타입 아이템 확인
            const hasFeedItems = userItems.some(item =>
                item.item.itemType === 'FOOD' && item.quantity > 0
            );

            // 먹이 주기 버튼 상태 설정
            const feedButton = document.querySelector('.action-button[data-action="feed"]');
            if (feedButton) {
                if (!hasFeedItems) {
                    feedButton.title = '먹이 아이템이 없습니다. 상점에서 구매하세요.';
                } else {
                    feedButton.title = '인벤토리에서 먹이를 선택하여 사용합니다.';
                }
            }
        } catch (error) {
            console.error('먹이 아이템 확인 실패:', error);
        }
    }

    /**
     * 액션 이름 가져오기
     * @param {string} action - 액션 코드
     * @returns {string} - 액션 한글 이름
     */
    function getActionName(action) {
        const actionNames = {
            'feed': '밥 주기',
            'water': '물 주기',
            'play': '놀아주기',
            'brush': '빗질하기',
            'sleep': '재우기',
            'snack': '간식 주기',
            'walk': '산책하기'
        };

        return actionNames[action] || action;
    }

    // === 이벤트 리스너 설정 ===

    // 펫 타입 선택 이벤트
    if (petTypeOptions.length > 0) {
        petTypeOptions.forEach(function(option) {
            option.addEventListener('click', function() {
                // 기존 선택 해제
                petTypeOptions.forEach(opt => opt.classList.remove('selected'));

                // 새로운 선택 적용
                this.classList.add('selected');

                // 선택된 펫 타입 저장
                selectedPetType = this.dataset.petType;
                console.log('펫 타입 선택됨:', selectedPetType); // 디버깅용
            });
        });
    }

    // 펫 생성 버튼
    if (createPetButton) {
        createPetButton.addEventListener('click', function() {
            const petName = petNameInput.value.trim();

            if (!petName) {
                showStatusMessage(creationStatusP, '펫 이름을 입력하세요.', 'error');
                return;
            }

            if (!currentUserId) {
                showStatusMessage(creationStatusP, '로그인이 필요합니다.', 'error');
                return;
            }

            // 현재 선택된 펫 타입 가져오기
            const selectedElement = document.querySelector('.pet-type-option.selected');
            const petType = selectedElement ? selectedElement.dataset.petType : "DOG";

            console.log(`펫 생성 시도: 이름=${petName}, 타입=${petType}`); // 디버깅용
            createPet(petName, petType);
        });
    }

    // 펫 관리 액션 버튼들
    actionButtons.forEach(function(button) {
        button.addEventListener('click', function() {
            if (!currentUserId || !currentPetId) {
                showStatusMessage(actionStatusP, '먼저 펫을 선택해주세요.', 'error');
                return;
            }

            const action = button.dataset.action;

            // 먹이 주기는 모달을 통해 아이템 선택
            if (action === 'feed') {
                // 먹이 선택 모달 표시 전에 먹이 아이템 목록 불러오기
                loadFeedItems(currentUserId);
                document.getElementById('feedModal').style.display = 'flex';
            } else {
                // 다른 액션은 기존 방식대로 처리
                performPetAction(currentPetId, action);
            }
        });
    });

    // 먹이 선택 취소 버튼 이벤트 리스너
    if (cancelFeedButton) {
        cancelFeedButton.addEventListener('click', function() {
            feedModal.style.display = 'none';
        });
    }

    // 모달 외부 클릭 시 닫기
    if (feedModal) {
        feedModal.addEventListener('click', function(e) {
            if (e.target === feedModal) {
                feedModal.style.display = 'none';
            }
        });
    }

    // 페이지 로드 시 사용자 정보 및 펫 목록 로드
    fetchCurrentUser();

    // 페이지 벗어날 때 타이머 정리
    window.addEventListener('beforeunload', function() {
        if (feedCooldownTimer) {
            clearInterval(feedCooldownTimer);
        }

        if (sleepTimerInterval) {
            clearInterval(sleepTimerInterval);
        }
    });
});

// 로그인 상태 표시
document.addEventListener('DOMContentLoaded', async function() {
    const authButtons = document.getElementById('authButtons');

    try {
        const response = await fetch('/api/auth/user');

        if (response.ok) {
            // 로그인된 상태
            const user = await response.json();
            authButtons.innerHTML = `
                <form id="logoutForm" action="/api/auth/logout" method="post" style="display:inline;">
                    <button type="submit" class="navigation-button">로그아웃</button>
                </form>
            `;
        } else {
            // 로그인되지 않은 상태
            authButtons.innerHTML = `<a href="/auth/login" class="navigation-button">로그인</a>`;
        }
    } catch (error) {
        console.error('로그인 상태 확인 실패:', error);
    }
});

/**
 * 환영 메시지 표시
 * @param {string} nickname - 사용자 닉네임
 */
function displayWelcomeMessage(nickname) {
    const welcomeMsg = document.createElement('div');
    welcomeMsg.className = 'welcome-message';
    welcomeMsg.innerHTML = `<p>${nickname}님, 환영합니다! 펫을 선택하거나 새 펫을 만들어보세요.</p>`;

    const petListSection = document.querySelector('.pet-list-section');
    if (petListSection) {
        // 기존 환영 메시지가 있으면 제거
        const existingMsg = petListSection.querySelector('.welcome-message');
        if (existingMsg) {
            existingMsg.remove();
        }
        petListSection.prepend(welcomeMsg);
    }
}

/**
 * 타이머 시간 형식화
 * @param {number} milliseconds - 밀리초
 * @returns {string} - 형식화된 시간 문자열
 */
function formatTimeRemaining(milliseconds) {
    const hours = Math.floor(milliseconds / (1000 * 60 * 60));
    const minutes = Math.floor((milliseconds % (1000 * 60 * 60)) / (1000 * 60));
    const seconds = Math.floor((milliseconds % (1000 * 60)) / 1000);

    if (hours > 0) {
        return `${hours}시간 ${minutes}분 후`;
    } else if (minutes > 0) {
        return `${minutes}분 ${seconds}초 후`;
    } else {
        return `${seconds}초 후`;
    }
}