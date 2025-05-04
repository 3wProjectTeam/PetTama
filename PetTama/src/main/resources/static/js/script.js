/**
 * PetTama - 메인 자바스크립트 파일
 * 펫 관리 및 FSM(Finite State Machine) 기반 상태 관리
 */

document.addEventListener('DOMContentLoaded', function() {
    // DOM 요소
    const userIdInput = document.getElementById('userIdInput');
    const loadPetsButton = document.getElementById('loadPetsButton');
    const petListUl = document.getElementById('petList');
    const petDetailsSection = document.querySelector('.pet-details-section');
    const petNameInput = document.getElementById('petNameInput');
    const createPetButton = document.getElementById('createPetButton');
    const creationStatusP = document.getElementById('creationStatus');
    const actionStatusP = document.getElementById('actionStatus');
    const actionButtons = document.querySelectorAll('.action-button');
    const petDetailName = document.getElementById('petDetailName');
    const petTypeOptions = document.querySelectorAll('.pet-type-option');

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

    // 상태 저장 변수
    let currentUserId = null;
    let currentPetId = null;
    let petsData = [];
    let selectedPetType = "DOG"; // 기본 선택 펫 타입

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
     * API 요청 처리
     * @param {string} url - API 엔드포인트
     * @param {Object} options - fetch 옵션
     * @returns {Promise<any>} - API 응답
     */
    async function fetchWithErrorHandling(url, options) {
        options = options || {};

        try {
            const response = await fetch(url, options);

            if (!response.ok) {
                const errorData = await response.json().catch(function() {
                    return { message: '알 수 없는 오류가 발생했습니다.' };
                });
                throw new Error(errorData.message || "HTTP 오류! 상태: " + response.status);
            }

            return await response.json();
        } catch (error) {
            console.error('API 요청 오류:', error);
            throw error;
        }
    }

    // === 메인 기능 함수 ===

    /**
     * 사용자의 모든 펫 가져오기
     * @param {number} userId - 사용자 ID
     */
    async function fetchPets(userId) {
        try {
            const pets = await fetchWithErrorHandling(API_BASE_URL + "/" + userId);

            // 추가 필드가 없는 경우 id 필드 추가 (API 수정 전까지 임시 조치)
            petsData = pets.map(function(pet, index) {
                // petType이 없는 경우 기본값 설정
                if (!pet.petType) {
                    pet.petType = 'CAT';
                }

                return {
                    ...pet,
                    id: pet.id || index + 1 // id가 없으면 인덱스로 대체
                };
            });

            displayPetList(petsData);
        } catch (error) {
            showStatusMessage(creationStatusP, "펫 목록을 불러오는데 실패했습니다: " + error.message, 'error');
            petListUl.innerHTML = '<li>펫 목록을 불러오는데 실패했습니다.</li>';
        }
    }

    /**
     * 특정 펫의 상세 정보 가져오기
     * @param {number} userId - 사용자 ID
     * @param {number} petId - 펫 ID
     */
    async function fetchPetDetails(userId, petId) {
        try {
            // API에서 최신 정보 가져오기
            const pet = await fetchWithErrorHandling(API_BASE_URL + "/" + userId + "/pets/" + petId);

            // id 추가 (필요한 경우)
            if (!pet.id) {
                pet.id = petId;
            }

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
        } catch (error) {
            showStatusMessage(actionStatusP, "펫 정보를 불러오는데 실패했습니다: " + error.message, 'error');
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
        // 상태가 심각할 때는 상태 이모지, 그렇지 않으면 펫 타입 이모지
        if (state === 'CRITICAL' || state === 'SICK') {
            petImage.textContent = stateEmojis[state];
        } else {
            petImage.textContent = petTypeEmoji;
        }

        // 추천 메시지 업데이트
        petRecommendation.textContent = recommendation;
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
                return '펫이 배고파합니다! 밥을 주세요.';
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
     * @param {number} userId - 사용자 ID
     * @param {string} name - 펫 이름
     * @param {string} petType - 펫 타입
     */
    async function createPet(userId, name, petType) {
        try {
            showStatusMessage(creationStatusP, '펫 생성 중...', 'info');

            const newPet = await fetchWithErrorHandling(
                API_BASE_URL + "/" + userId + "?name=" + encodeURIComponent(name) + "&petType=" + encodeURIComponent(petType),
                { method: 'POST' }
            );

            showStatusMessage(creationStatusP, '"' + newPet.name + '" 생성 완료!', 'success');
            petNameInput.value = ''; // 입력 필드 초기화

            // 펫 목록 새로고침
            fetchPets(userId);
        } catch (error) {
            showStatusMessage(creationStatusP, '펫 생성 실패: ' + error.message, 'error');
        }
    }

    /**
     * 펫 액션 수행
     * @param {number} userId - 사용자 ID
     * @param {number} petId - 펫 ID
     * @param {string} action - 액션 (feed, play, sleep 등)
     */
    async function performPetAction(userId, petId, action) {
        try {
            showStatusMessage(actionStatusP, getActionName(action) + " 중...", 'info');

            // 버튼 애니메이션
            const actionButton = document.querySelector('.action-button[data-action="' + action + '"]');
            if (actionButton) {
                actionButton.classList.add('action-success');
                setTimeout(function() {
                    actionButton.classList.remove('action-success');
                }, 500);
            }

            // API 요청 (각 액션에 맞는 엔드포인트 사용)
            const updatedPet = await fetchWithErrorHandling(
                API_BASE_URL + "/" + userId + "/pets/" + petId + "/" + action,
                { method: 'PUT' }
            );

            // 펫 정보 업데이트
            if (!updatedPet.id) {
                updatedPet.id = petId;
            }

            // petType이 없는 경우 기존 데이터에서 가져오기
            if (!updatedPet.petType) {
                const existingPet = petsData.find(p => p.id === petId);
                updatedPet.petType = existingPet ? existingPet.petType : 'CAT';
            }

            displayPetDetails(updatedPet);

            // 성공 메시지
            showStatusMessage(
                actionStatusP,
                '"' + getActionName(action) + '" 완료!',
                'success'
            );

            // 로컬 펫 데이터 업데이트
            const index = petsData.findIndex(function(p) { return p.id === petId; });
            if (index !== -1) {
                petsData[index] = Object.assign({}, petsData[index], updatedPet);
            }

        } catch (error) {
            showStatusMessage(
                actionStatusP,
                getActionName(action) + ' 실패: ' + error.message,
                'error'
            );
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
    petTypeOptions.forEach(function(option) {
        option.addEventListener('click', function() {
            // 기존 선택 해제
            petTypeOptions.forEach(opt => opt.classList.remove('selected'));

            // 새로운 선택 적용
            this.classList.add('selected');

            // 선택된 펫 타입 저장
            selectedPetType = this.dataset.petType;
        });
    });

    // 펫 목록 불러오기 버튼
    loadPetsButton.addEventListener('click', function() {
        const userId = parseInt(userIdInput.value, 10);
        if (userId) {
            currentUserId = userId;
            petDetailsSection.style.display = 'none'; // 상세 정보 숨기기
            fetchPets(currentUserId);
        } else {
            showStatusMessage(creationStatusP, '유효한 사용자 ID를 입력하세요.', 'error');
        }
    });

    // 펫 생성 버튼
    createPetButton.addEventListener('click', function() {
        const userId = parseInt(userIdInput.value, 10);
        const petName = petNameInput.value.trim();

        if (!userId) {
            showStatusMessage(creationStatusP, '유효한 사용자 ID를 입력하세요.', 'error');
        } else if (!petName) {
            showStatusMessage(creationStatusP, '펫 이름을 입력하세요.', 'error');
        } else {
            createPet(userId, petName, selectedPetType);
        }
    });

    // 펫 관리 액션 버튼들
    actionButtons.forEach(function(button) {
        button.addEventListener('click', function() {
            if (!currentUserId || !currentPetId) {
                showStatusMessage(actionStatusP, '먼저 펫을 선택해주세요.', 'error');
                return;
            }

            const action = button.dataset.action;
            performPetAction(currentUserId, currentPetId, action);
        });
    });

    // 페이지 로드 시 초기화 (선택 사항)
    const initialUserId = parseInt(userIdInput.value, 10);
    if (initialUserId) {
        currentUserId = initialUserId;
        fetchPets(currentUserId);
    }
    const userProfileLink = document.getElementById('userProfileLink');
    if (userProfileLink) {
        userProfileLink.href = `/users/${currentUserId}`;
        userProfileLink.style.display = 'inline-block';
    }
});