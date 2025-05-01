package com.example.PetTama.fsm;

import com.example.PetTama.entity.Pet;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * A Finite State Machine implementation for PetTama pets
 * This handles state transitions and pet status updates over time
 */
@Slf4j
public class PetFSM {

    // Define possible pet states
    public enum PetState {
        HAPPY,      // Pet is in good condition
        HUNGRY,     // Pet needs food
        TIRED,      // Pet needs rest
        BORED,      // Pet needs play
        STRESSED,   // Pet is stressed
        THIRSTY,    // Pet needs water
        SICK,       // Pet is unwell
        CRITICAL    // Pet is in critical condition
    }

    // Constants for status thresholds
    private static final int LOW_THRESHOLD = 20;
    private static final int CRITICAL_THRESHOLD = 10;
    private static final int HIGH_THRESHOLD = 80;
    private static final int MAX_VALUE = 100;
    
    // Time decay constants (how much stats decrease per hour)
    private static final int FULLNESS_DECAY_PER_HOUR = 5;
    private static final int HAPPINESS_DECAY_PER_HOUR = 3;
    private static final int THIRST_INCREASE_PER_HOUR = 4;
    private static final int STRESS_INCREASE_PER_HOUR = 2;
    private static final int TIRED_INCREASE_PER_HOUR = 3;
    
    // How stats affect HP
    private static final int HP_LOSS_CRITICAL = 5;
    private static final int HP_GAIN_OPTIMAL = 2;

    /**
     * Updates a pet's stats based on time passed since last update
     * @param pet The pet to update
     * @return The updated pet with new stats
     */
    public static Pet updatePetStatus(Pet pet) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastUpdated = pet.getLastUpdated();
        
        // Calculate hours passed since last update
        long hoursPassed = Duration.between(lastUpdated, now).toHours();
        
        // If less than 1 hour passed, don't update
        if (hoursPassed < 1) return pet;
        
        log.info("Updating pet {} status after {} hours", pet.getName(), hoursPassed);
        
        // Apply time decay to stats
        int newFullness = Math.max(0, pet.getFullness() - (int)(FULLNESS_DECAY_PER_HOUR * hoursPassed));
        int newHappiness = Math.max(0, pet.getHappiness() - (int)(HAPPINESS_DECAY_PER_HOUR * hoursPassed));
        int newThirsty = Math.min(MAX_VALUE, pet.getThirsty() + (int)(THIRST_INCREASE_PER_HOUR * hoursPassed));
        int newStress = Math.min(MAX_VALUE, pet.getStress() + (int)(STRESS_INCREASE_PER_HOUR * hoursPassed));
        int newTired = Math.min(MAX_VALUE, pet.getTired() + (int)(TIRED_INCREASE_PER_HOUR * hoursPassed));
        
        // Apply stat changes
        pet.setFullness(newFullness);
        pet.setHappiness(newHappiness);
        pet.setThirsty(newThirsty);
        pet.setStress(newStress);
        pet.setTired(newTired);
        
        // Update HP based on overall condition
        updateHP(pet);
        
        // Update the last updated timestamp
        pet.setLastUpdated(now);
        
        return pet;
    }
    
    /**
     * Determines the current state of the pet based on its stats
     * @param pet The pet to check
     * @return The current PetState
     */
    public static PetState getCurrentState(Pet pet) {
        // Get all current states
        List<PetState> states = getPetStates(pet);
        
        // Prioritize the most critical states
        if (states.contains(PetState.CRITICAL)) return PetState.CRITICAL;
        if (states.contains(PetState.SICK)) return PetState.SICK;
        if (states.contains(PetState.HUNGRY)) return PetState.HUNGRY;
        if (states.contains(PetState.THIRSTY)) return PetState.THIRSTY;
        if (states.contains(PetState.TIRED)) return PetState.TIRED;
        if (states.contains(PetState.STRESSED)) return PetState.STRESSED;
        if (states.contains(PetState.BORED)) return PetState.BORED;
        
        // Default state if no other conditions are met
        return PetState.HAPPY;
    }
    
    /**
     * Gets all states that apply to a pet based on its current stats
     * @param pet The pet to check
     * @return List of all applicable PetStates
     */
    private static List<PetState> getPetStates(Pet pet) {
        PetState[] states = new PetState[]{};
        
        // Check for critical condition first
        if (pet.getHp() <= CRITICAL_THRESHOLD) {
            return Arrays.asList(PetState.CRITICAL);
        }
        
        // Check for each condition and add to states
        if (pet.getFullness() <= LOW_THRESHOLD) {
            states = addState(states, PetState.HUNGRY);
        }
        
        if (pet.getThirsty() >= HIGH_THRESHOLD) {
            states = addState(states, PetState.THIRSTY);
        }
        
        if (pet.getTired() >= HIGH_THRESHOLD) {
            states = addState(states, PetState.TIRED);
        }
        
        if (pet.getStress() >= HIGH_THRESHOLD) {
            states = addState(states, PetState.STRESSED);
        }
        
        if (pet.getHappiness() <= LOW_THRESHOLD) {
            states = addState(states, PetState.BORED);
        }
        
        // Check for sickness (combination of factors)
        if ((pet.getStress() > HIGH_THRESHOLD && pet.getFullness() < LOW_THRESHOLD) ||
            (pet.getTired() > HIGH_THRESHOLD && pet.getThirsty() > HIGH_THRESHOLD)) {
            states = addState(states, PetState.SICK);
        }
        
        // Return happy if no other states
        if (states.length == 0) {
            return Arrays.asList(PetState.HAPPY);
        }
        
        return Arrays.asList(states);
    }
    
    /**
     * Helper method to add a state to a state array
     */
    private static PetState[] addState(PetState[] states, PetState newState) {
        PetState[] newStates = new PetState[states.length + 1];
        System.arraycopy(states, 0, newStates, 0, states.length);
        newStates[states.length] = newState;
        return newStates;
    }
    
    /**
     * Updates the pet's HP based on its overall condition
     * @param pet The pet to update
     */
    private static void updateHP(Pet pet) {
        int hpChange = 0;
        
        // Critical conditions decrease HP
        if (pet.getFullness() <= CRITICAL_THRESHOLD) hpChange -= HP_LOSS_CRITICAL;
        if (pet.getThirsty() >= MAX_VALUE - CRITICAL_THRESHOLD) hpChange -= HP_LOSS_CRITICAL;
        if (pet.getStress() >= MAX_VALUE - CRITICAL_THRESHOLD) hpChange -= HP_LOSS_CRITICAL;
        
        // Optimal conditions increase HP slightly
        if (pet.getFullness() >= HIGH_THRESHOLD) hpChange += HP_GAIN_OPTIMAL;
        if (pet.getHappiness() >= HIGH_THRESHOLD) hpChange += HP_GAIN_OPTIMAL;
        if (pet.getThirsty() <= LOW_THRESHOLD) hpChange += HP_GAIN_OPTIMAL;
        if (pet.getStress() <= LOW_THRESHOLD) hpChange += HP_GAIN_OPTIMAL;
        if (pet.getTired() <= LOW_THRESHOLD) hpChange += HP_GAIN_OPTIMAL;
        
        // Apply HP change with limits
        int newHp = pet.getHp() + hpChange;
        pet.setHp(Math.max(0, Math.min(MAX_VALUE, newHp)));
    }
    
    /**
     * Performs the feed action and updates pet state
     * @param pet The pet to feed
     * @return The updated pet
     */
    public static Pet feed(Pet pet) {
        // Update time-based stats first
        updatePetStatus(pet);
        
        // Apply feeding effects
        int newFullness = Math.min(MAX_VALUE, pet.getFullness() + 20);
        pet.setFullness(newFullness);
        
        // Decrease thirst slightly
        int newThirsty = Math.max(0, pet.getThirsty() - 5);
        pet.setThirsty(newThirsty);
        
        // Update last interaction time
        pet.setLastUpdated(LocalDateTime.now());
        
        return pet;
    }
    
    /**
     * Performs the play action and updates pet state
     * @param pet The pet to play with
     * @return The updated pet
     */
    public static Pet play(Pet pet) {
        // Update time-based stats first
        updatePetStatus(pet);
        
        // Apply play effects
        int newHappiness = Math.min(MAX_VALUE, pet.getHappiness() + 15);
        pet.setHappiness(newHappiness);
        
        // Playing makes pet more tired and hungry
        int newTired = Math.min(MAX_VALUE, pet.getTired() + 10);
        pet.setTired(newTired);
        
        int newFullness = Math.max(0, pet.getFullness() - 5);
        pet.setFullness(newFullness);
        
        // Decreases stress
        int newStress = Math.max(0, pet.getStress() - 10);
        pet.setStress(newStress);
        
        // Update last interaction time
        pet.setLastUpdated(LocalDateTime.now());
        
        return pet;
    }
    
    /**
     * Performs the brush action and updates pet state
     * @param pet The pet to brush
     * @return The updated pet
     */
    public static Pet brush(Pet pet) {
        // Update time-based stats first
        updatePetStatus(pet);
        
        // Apply brush effects
        int newHappiness = Math.min(MAX_VALUE, pet.getHappiness() + 15);
        pet.setHappiness(newHappiness);
        
        // Brushing relaxes the pet
        int newStress = Math.max(0, pet.getStress() - 15);
        pet.setStress(newStress);
        
        // Update last interaction time
        pet.setLastUpdated(LocalDateTime.now());
        
        return pet;
    }
    
    /**
     * Performs the sleep action and updates pet state
     * @param pet The pet to sleep
     * @return The updated pet
     */
    public static Pet sleep(Pet pet) {
        // Update time-based stats first
        updatePetStatus(pet);
        
        // Apply sleep effects
        int newTired = Math.max(0, pet.getTired() - 20);
        pet.setTired(newTired);
        
        // Sleeping makes pet slightly hungrier and thirstier
        int newFullness = Math.max(0, pet.getFullness() - 5);
        pet.setFullness(newFullness);
        
        int newThirsty = Math.min(MAX_VALUE, pet.getThirsty() + 5);
        pet.setThirsty(newThirsty);
        
        // Sleep reduces stress
        int newStress = Math.max(0, pet.getStress() - 8);
        pet.setStress(newStress);
        
        // Update last interaction time
        pet.setLastUpdated(LocalDateTime.now());
        
        return pet;
    }
    
    /**
     * Performs the give water action and updates pet state
     * @param pet The pet to give water to
     * @return The updated pet
     */
    public static Pet giveWater(Pet pet) {
        // Update time-based stats first
        updatePetStatus(pet);
        
        // Apply water effects
        int newThirsty = Math.max(0, pet.getThirsty() - 20);
        pet.setThirsty(newThirsty);
        
        // Update last interaction time
        pet.setLastUpdated(LocalDateTime.now());
        
        return pet;
    }
    
    /**
     * Performs the snack action and updates pet state
     * @param pet The pet to give a snack to
     * @return The updated pet
     */
    public static Pet giveSnack(Pet pet) {
        // Update time-based stats first
        updatePetStatus(pet);
        
        // Apply snack effects
        int newFullness = Math.min(MAX_VALUE, pet.getFullness() + 5);
        pet.setFullness(newFullness);
        
        // Snacks make pets happier
        int newHappiness = Math.min(MAX_VALUE, pet.getHappiness() + 8);
        pet.setHappiness(newHappiness);
        
        // But may increase thirst
        int newThirsty = Math.min(MAX_VALUE, pet.getThirsty() + 3);
        pet.setThirsty(newThirsty);
        
        // Update last interaction time
        pet.setLastUpdated(LocalDateTime.now());
        
        return pet;
    }
    
    /**
     * Performs the pet walking action and updates pet state
     * @param pet The pet to walk
     * @return The updated pet
     */
    public static Pet walk(Pet pet) {
        // Update time-based stats first
        updatePetStatus(pet);
        
        // Apply walking effects
        int newHappiness = Math.min(MAX_VALUE, pet.getHappiness() + 15);
        pet.setHappiness(newHappiness);
        
        // Walking makes pet tired and hungry
        int newTired = Math.min(MAX_VALUE, pet.getTired() + 15);
        pet.setTired(newTired);
        
        int newFullness = Math.max(0, pet.getFullness() - 10);
        pet.setFullness(newFullness);
        
        int newThirsty = Math.min(MAX_VALUE, pet.getThirsty() + 12);
        pet.setThirsty(newThirsty);
        
        // Walking reduces stress significantly
        int newStress = Math.max(0, pet.getStress() - 15);
        pet.setStress(newStress);
        
        // Update last interaction time
        pet.setLastUpdated(LocalDateTime.now());
        
        return pet;
    }
    
    /**
     * Gets a recommendation for the next action based on pet's current state
     * @param pet The pet to check
     * @return String recommendation
     */
    public static String getActionRecommendation(Pet pet) {
        PetState currentState = getCurrentState(pet);
        
        switch (currentState) {
            case HUNGRY:
                return "Your pet is hungry! Please feed them.";
            case TIRED:
                return "Your pet is tired! Let them sleep.";
            case THIRSTY:
                return "Your pet is thirsty! Give them water.";
            case BORED:
                return "Your pet is bored! Play with them.";
            case STRESSED:
                return "Your pet is stressed! Try brushing them or taking them for a walk.";
            case SICK:
                return "Your pet is not feeling well! Make sure all their needs are met.";
            case CRITICAL:
                return "WARNING! Your pet is in critical condition! Attend to their needs immediately!";
            case HAPPY:
                return "Your pet is happy and healthy!";
            default:
                return "Check on your pet regularly.";
        }
    }
}
