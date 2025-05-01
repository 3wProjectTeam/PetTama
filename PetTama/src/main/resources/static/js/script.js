document.addEventListener('DOMContentLoaded', () => {
    const userIdInput = document.getElementById('userIdInput');
    const loadPetsButton = document.getElementById('loadPetsButton');
    const petListUl = document.getElementById('petList');
    const petDetailsSection = document.querySelector('.pet-details-section');
    const petNameSpan = document.getElementById('petName');
    const petHpSpan = document.getElementById('petHp');
    const petFullnessSpan = document.getElementById('petFullness');
    const petSleepinessSpan = document.getElementById('petSleepiness');
    const petHappinessSpan = document.getElementById('petHappiness');
    const actionButtons = document.querySelectorAll('.action-button');
    const actionStatusP = document.getElementById('actionStatus');

    const petNameInput = document.getElementById('petNameInput');
    const createPetButton = document.getElementById('createPetButton');
    const creationStatusP = document.getElementById('creationStatus');

    let currentUserId = null;
    let currentPetId = null;
    let petsData = []; // To store fetched pets with their IDs

    // --- Helper Functions ---
    const API_BASE_URL = '/api/user-nums'; // 백엔드 API 기본 경로

    async function fetchPets(userId) {
        try {
            const response = await fetch(`${API_BASE_URL}/${userId}`);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            // 백엔드 API가 Pet ID를 반환하도록 수정 필요 가정
            // 예시: [{id: 1, name: '...', hp: ...}, {id: 2, ...}]
            petsData = await response.json();
            displayPetList(petsData);
        } catch (error) {
            console.error('Error fetching pets:', error);
            petListUl.innerHTML = '<li>펫 목록을 불러오는 데 실패했습니다.</li>';
        }
    }

    async function fetchPetDetails(userId, petId) {
        try {
            // PetGetDto에는 ID가 없으므로, petsData에서 해당 ID의 펫 찾기
            const selectedPet = petsData.find(pet => pet.id === petId); // petsData에 id 필드 필요
            if (!selectedPet) {
                console.error(`Pet with ID ${petId} not found in local data.`);
                petDetailsSection.style.display = 'none';
                return;
            }

            // 실제 상세 정보는 API 호출로 다시 가져올 수도 있음 (선택 사항)
            // const response = await fetch(`${API_BASE_URL}/${userId}/pets/${petId}`);
            // if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            // const petDetails = await response.json();

            // 여기서는 로컬 데이터 사용 (petsData에 필요한 모든 정보가 있다고 가정)
            displayPetDetails(selectedPet);
            petDetailsSection.style.display = 'block';
            currentPetId = petId; // 현재 선택된 펫 ID 저장


        } catch (error) {
            console.error('Error fetching pet details:', error);
            petDetailsSection.style.display = 'none';
        }
    }

    function displayPetList(pets) {
        petListUl.innerHTML = ''; // 기존 목록 초기화
        if (pets.length === 0) {
            petListUl.innerHTML = '<li>펫이 없습니다.</li>';
            return;
        }
        pets.forEach((pet, index) => { // 백엔드 응답에 id가 있다고 가정
            const li = document.createElement('li');
            // Pet ID를 data 속성으로 저장 (petsData에 id가 있어야 함)
            // 백엔드에서 Pet 엔티티 또는 별도 DTO에 id를 포함시켜야 함
            const petId = pet.id; // petsData에 id가 있다고 가정
            li.textContent = pet.name;
            li.dataset.petId = petId; // 데이터 속성에 펫 ID 저장
            li.addEventListener('click', () => {
                fetchPetDetails(currentUserId, petId);
            });
            petListUl.appendChild(li);
        });
    }

    function displayPetDetails(pet) {
        petNameSpan.textContent = pet.name;
        petHpSpan.textContent = pet.hp;
        petFullnessSpan.textContent = pet.fullness;
        petSleepinessSpan.textContent = pet.sleepiness;
        petHappinessSpan.textContent = pet.happiness;
    }

    async function performPetAction(userId, petId, action) {
        actionStatusP.textContent = `${action} 실행 중...`;
        try {
            // PetService의 해당 메소드에 대한 API 엔드포인트 필요
            const response = await fetch(`${API_BASE_URL}/${userId}/pets/${petId}/${action}`, {
                method: 'POST', // 대부분의 액션은 POST 또는 PUT일 수 있음
                // 필요하다면 headers나 body 추가
            });
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const updatedPet = await response.json();
            displayPetDetails(updatedPet); // UI 업데이트
            actionStatusP.textContent = `${petNameSpan.textContent}에게 ${action} 완료!`;

            // 로컬 petsData 업데이트 (선택 사항)
            const index = petsData.findIndex(p => p.id === petId);
            if (index !== -1) {
                petsData[index] = { ...petsData[index], ...updatedPet }; // id 유지하며 업데이트
            }

        } catch (error) {
            console.error(`Error performing ${action}:`, error);
            actionStatusP.textContent = `${action} 실패: ${error.message}`;
        }
    }

    async function createPet(userId, name) {
        creationStatusP.textContent = '펫 생성 중...';
        if (!name) {
            creationStatusP.textContent = '펫 이름을 입력해주세요.';
            return;
        }
        try {
            const response = await fetch(`${API_BASE_URL}/${userId}?name=${encodeURIComponent(name)}`, {
                method: 'POST',
            });
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const newPet = await response.json();
            creationStatusP.textContent = `"${newPet.name}" 생성 완료!`;
            petNameInput.value = ''; // 입력 필드 초기화
            fetchPets(userId); // 펫 목록 새로고침
        } catch (error) {
            console.error('Error creating pet:', error);
            creationStatusP.textContent = `펫 생성 실패: ${error.message}`;
        }
    }


    // --- Event Listeners ---
    loadPetsButton.addEventListener('click', () => {
        const userId = userIdInput.value;
        if (userId) {
            currentUserId = parseInt(userId, 10);
            petDetailsSection.style.display = 'none'; // 상세 정보 숨기기
            fetchPets(currentUserId);
        } else {
            petListUl.innerHTML = '<li>유저 ID를 입력하세요.</li>';
        }
    });

    actionButtons.forEach(button => {
        button.addEventListener('click', () => {
            if (currentUserId && currentPetId) {
                const action = button.dataset.action;
                performPetAction(currentUserId, currentPetId, action);
            } else {
                actionStatusP.textContent = '먼저 펫을 선택해주세요.';
            }
        });
    });

    createPetButton.addEventListener('click', () => {
        const userId = userIdInput.value;
        const petName = petNameInput.value.trim();
        if (userId && petName) {
            createPet(parseInt(userId, 10), petName);
        } else if (!userId) {
            creationStatusP.textContent = 'User ID를 입력해주세요.';
        } else {
            creationStatusP.textContent = '펫 이름을 입력해주세요.';
        }
    });

    // 초기 로드 (선택 사항: 페이지 로드 시 첫 번째 유저의 펫 로드)
    // fetchPets(userIdInput.value);
});