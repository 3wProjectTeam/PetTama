/**
 * Pet State Visualization
 * Adds visualizations and state indicators to the pet UI
 */

document.addEventListener('DOMContentLoaded', () => {
    // Get the pet details section
    const petDetailsSection = document.querySelector('.pet-details-section');
    
    // Create a new div for the state visualization
    const stateVisDiv = document.createElement('div');
    stateVisDiv.className = 'pet-state-visualization';
    stateVisDiv.innerHTML = `
        <h3>Pet State</h3>
        <div class="state-indicator">
            <div class="state-icon">😊</div>
            <div class="state-name">HAPPY</div>
        </div>
        <div class="state-recommendation">
            Your pet is happy and healthy!
        </div>
        <div class="pet-stats-bars">
            <div class="stat-bar-container">
                <label>HP:</label>
                <div class="stat-bar">
                    <div class="stat-bar-fill hp-bar" style="width: 100%"></div>
                </div>
                <span class="stat-value">100</span>
            </div>
            <div class="stat-bar-container">
                <label>Fullness:</label>
                <div class="stat-bar">
                    <div class="stat-bar-fill fullness-bar" style="width: 100%"></div>
                </div>
                <span class="stat-value">100</span>
            </div>
            <div class="stat-bar-container">
                <label>Happiness:</label>
                <div class="stat-bar">
                    <div class="stat-bar-fill happiness-bar" style="width: 100%"></div>
                </div>
                <span class="stat-value">100</span>
            </div>
            <div class="stat-bar-container">
                <label>Tired:</label>
                <div class="stat-bar">
                    <div class="stat-bar-fill tired-bar" style="width: 0%"></div>
                </div>
                <span class="stat-value">0</span>
            </div>
            <div class="stat-bar-container">
                <label>Thirsty:</label>
                <div class="stat-bar">
                    <div class="stat-bar-fill thirsty-bar" style="width: 0%"></div>
                </div>
                <span class="stat-value">0</span>
            </div>
            <div class="stat-bar-container">
                <label>Stress:</label>
                <div class="stat-bar">
                    <div class="stat-bar-fill stress-bar" style="width: 0%"></div>
                </div>
                <span class="stat-value">0</span>
            </div>
        </div>
    `;
    
    // Add CSS styles for the visualization
    const styleElement = document.createElement('style');
    styleElement.textContent = `
        .pet-state-visualization {
            margin-top: 20px;
            padding: 15px;
            border: 1px solid #ddd;
            border-radius: 5px;
            background-color: #f9f9f9;
        }
        
        .state-indicator {
            display: flex;
            align-items: center;
            margin-bottom: 10px;
        }
        
        .state-icon {
            font-size: 32px;
            margin-right: 10px;
        }
        
        .state-name {
            font-weight: bold;
            font-size: 18px;
        }
        
        .state-recommendation {
            margin-bottom: 15px;
            padding: 10px;
            background-color: #fffde7;
            border-left: 4px solid #ffd600;
            border-radius: 2px;
        }
        
        .pet-stats-bars {
            margin-top: 15px;
        }
        
        .stat-bar-container {
            display: flex;
            align-items: center;
            margin-bottom: 8px;
        }
        
        .stat-bar-container label {
            width: 80px;
            margin-right: 10px;
        }
        
        .stat-bar {
            flex-grow: 1;
            height: 12px;
            background-color: #eee;
            border-radius: 6px;
            overflow: hidden;
            margin-right: 10px;
        }
        
        .stat-bar-fill {
            height: 100%;
            border-radius: 6px;
            transition: width 0.5s ease-in-out;
        }
        
        .hp-bar {
            background-color: #4caf50;
        }
        
        .fullness-bar {
            background-color: #ff9800;
        }
        
        .happiness-bar {
            background-color: #2196f3;
        }
        
        .tired-bar {
            background-color: #9c27b0;
        }
        
        .thirsty-bar {
            background-color: #00bcd4;
        }
        
        .stress-bar {
            background-color: #f44336;
        }
        
        .stat-value {
            min-width: 30px;
            text-align: right;
        }
        
        /* State-specific styling */
        .state-happy .state-icon { color: #4caf50; }
        .state-hungry .state-icon { color: #ff9800; }
        .state-tired .state-icon { color: #9c27b0; }
        .state-bored .state-icon { color: #2196f3; }
        .state-stressed .state-icon { color: #f44336; }
        .state-thirsty .state-icon { color: #00bcd4; }
        .state-sick .state-icon { color: #795548; }
        .state-critical .state-icon { 
            color: #f44336;
            animation: pulse 1s infinite;
        }
        
        @keyframes pulse {
            0% { transform: scale(1); }
            50% { transform: scale(1.1); }
            100% { transform: scale(1); }
        }
    `;
    document.head.appendChild(styleElement);
    
    // Insert the visualization div into the pet details section
    const petDetailsDiv = document.getElementById('petDetails');
    petDetailsSection.insertBefore(stateVisDiv, petDetailsDiv.nextSibling);
    
    // Function to update the visualization based on pet data
    window.updatePetStateVisualization = function(pet) {
        if (!pet) return;
        
        // Update stat bars
        document.querySelector('.hp-bar').style.width = `${pet.hp}%`;
        document.querySelector('.fullness-bar').style.width = `${pet.fullness}%`;
        document.querySelector('.happiness-bar').style.width = `${pet.happiness}%`;
        document.querySelector('.tired-bar').style.width = `${pet.tired}%`;
        document.querySelector('.thirsty-bar').style.width = `${pet.thirsty}%`;
        document.querySelector('.stress-bar').style.width = `${pet.stress}%`;
        
        // Update stat values
        document.querySelectorAll('.stat-value')[0].textContent = pet.hp;
        document.querySelectorAll('.stat-value')[1].textContent = pet.fullness;
        document.querySelectorAll('.stat-value')[2].textContent = pet.happiness;
        document.querySelectorAll('.stat-value')[3].textContent = pet.tired;
        document.querySelectorAll('.stat-value')[4].textContent = pet.thirsty;
        document.querySelectorAll('.stat-value')[5].textContent = pet.stress;
        
        // Update state indicator if the pet has a state property
        if (pet.state) {
            const stateIcon = document.querySelector('.state-icon');
            const stateName = document.querySelector('.state-name');
            
            // Remove all state classes
            const stateClasses = ['state-happy', 'state-hungry', 'state-tired', 
                                 'state-bored', 'state-stressed', 'state-thirsty', 
                                 'state-sick', 'state-critical'];
            stateClasses.forEach(cls => {
                document.querySelector('.state-indicator').classList.remove(cls);
            });
            
            // Add the current state class
            document.querySelector('.state-indicator').classList.add(`state-${pet.state.toLowerCase()}`);
            
            // Update icon and name based on state
            stateName.textContent = pet.state;
            
            switch(pet.state) {
                case 'HAPPY':
                    stateIcon.textContent = '😊';
                    break;
                case 'HUNGRY':
                    stateIcon.textContent = '🍽️';
                    break;
                case 'TIRED':
                    stateIcon.textContent = '😴';
                    break;
                case 'BORED':
                    stateIcon.textContent = '😐';
                    break;
                case 'STRESSED':
                    stateIcon.textContent = '😰';
                    break;
                case 'THIRSTY':
                    stateIcon.textContent = '💧';
                    break;
                case 'SICK':
                    stateIcon.textContent = '🤒';
                    break;
                case 'CRITICAL':
                    stateIcon.textContent = '⚠️';
                    break;
                default:
                    stateIcon.textContent = '😊';
            }
        }
        
        // Update recommendation if available
        if (pet.recommendation) {
            document.querySelector('.state-recommendation').textContent = pet.recommendation;
        }
    };
    
    // Modify the original fetchPetDetails function to update visualization
    const originalDisplayPetDetails = window.displayPetDetails || function() {};
    
    window.displayPetDetails = function(pet) {
        // Call the original function
        originalDisplayPetDetails(pet);
        
        // Update the visualization
        updatePetStateVisualization(pet);
    };
});
