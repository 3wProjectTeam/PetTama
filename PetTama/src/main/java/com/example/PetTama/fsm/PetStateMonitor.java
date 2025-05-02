package com.example.PetTama.fsm;

import com.example.PetTama.entity.Pet;
import com.example.PetTama.fsm.PetFSM.PetState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Monitors pet states and provides insights and alerts
 * This component runs scheduled tasks to check pet health
 */
@Slf4j
@Component
public class PetStateMonitor {

    /**
     * Inner class to store state history for a pet
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    static class PetStateHistory {
        private Long petId;
        private Map<PetState, Integer> stateOccurrences = new HashMap<>();
        private PetState previousState;
        private LocalDateTime lastStateChange;
        private LocalDateTime lastInteraction;
        private int criticalStateCount = 0;
        
        // Add a state occurrence
        public void addStateOccurrence(PetState state) {
            int count = stateOccurrences.getOrDefault(state, 0);
            stateOccurrences.put(state, count + 1);
            
            // If state changed, update the change time
            if (previousState != state) {
                previousState = state;
                lastStateChange = LocalDateTime.now();
                
                // Track critical state occurrences
                if (state == PetState.CRITICAL) {
                    criticalStateCount++;
                }
            }
        }
        
        // Record an interaction
        public void recordInteraction() {
            lastInteraction = LocalDateTime.now();
        }
        
        // Check if pet needs attention (no interaction for a long time)
        public boolean needsAttention() {
            if (lastInteraction == null) return true;
            
            Duration sinceLastInteraction = Duration.between(lastInteraction, LocalDateTime.now());
            return sinceLastInteraction.toHours() >= 24; // No interaction for 24+ hours
        }
        
        // Get the pet's most common state
        public PetState getMostCommonState() {
            PetState mostCommon = PetState.HAPPY;
            int maxCount = 0;
            
            for (Map.Entry<PetState, Integer> entry : stateOccurrences.entrySet()) {
                if (entry.getValue() > maxCount) {
                    maxCount = entry.getValue();
                    mostCommon = entry.getKey();
                }
            }
            
            return mostCommon;
        }
    }
    
    // Map to store state history for all pets
    private final Map<Long, PetStateHistory> petStateHistories = new HashMap<>();
    
    /**
     * Record a pet state
     * @param pet The pet
     * @param state The current state
     * @param wasInteraction Whether this was triggered by user interaction
     */
    public void recordPetState(Pet pet, PetState state, boolean wasInteraction) {
        Long petId = pet.getId();
        
        // Get or create pet history
        PetStateHistory history = petStateHistories.getOrDefault(
            petId, 
            new PetStateHistory(petId, new HashMap<>(), null, null, null, 0)
        );
        
        // Add state occurrence
        history.addStateOccurrence(state);
        
        // If this was a user interaction, record it
        if (wasInteraction) {
            history.recordInteraction();
        }
        
        // Update the history map
        petStateHistories.put(petId, history);
    }
    
    /**
     * Check if a pet needs attention
     * @param petId The pet ID
     * @return true if pet needs attention
     */
    public boolean petNeedsAttention(Long petId) {
        PetStateHistory history = petStateHistories.get(petId);
        return history != null && history.needsAttention();
    }
    
    /**
     * Get insights about a pet's state history
     * @param petId The pet ID
     * @return String with insights
     */
    public String getPetInsights(Long petId) {
        PetStateHistory history = petStateHistories.get(petId);
        if (history == null) {
            return "No history available for this pet yet.";
        }
        
        StringBuilder insights = new StringBuilder();
        
        // Most common state
        PetState mostCommonState = history.getMostCommonState();
        insights.append("Most common state: ").append(mostCommonState).append("\n");
        
        // Critical state count
        insights.append("Times in critical condition: ").append(history.getCriticalStateCount()).append("\n");
        
        // Current state duration
        if (history.getLastStateChange() != null) {
            Duration inCurrentState = Duration.between(history.getLastStateChange(), LocalDateTime.now());
            insights.append("In current state for: ")
                   .append(inCurrentState.toHours())
                   .append(" hours, ")
                   .append(inCurrentState.toMinutesPart())
                   .append(" minutes\n");
        }
        
        // Time since last interaction
        if (history.getLastInteraction() != null) {
            Duration sinceLastInteraction = Duration.between(history.getLastInteraction(), LocalDateTime.now());
            insights.append("Last interaction: ")
                   .append(sinceLastInteraction.toDays())
                   .append(" days, ")
                   .append(sinceLastInteraction.toHoursPart())
                   .append(" hours ago\n");
        }
        
        return insights.toString();
    }
    
    /**
     * Scheduled task to check all pets for attention needed
     * Runs every hour
     */
    @Scheduled(fixedRate = 3600000) // Every hour
    public void checkPetsForAttention() {
        log.info("Checking pets for attention...");
        for (Map.Entry<Long, PetStateHistory> entry : petStateHistories.entrySet()) {
            Long petId = entry.getKey();
            PetStateHistory history = entry.getValue();
            
            if (history.needsAttention()) {
                log.warn("Pet {} needs attention! No interaction for 24+ hours.", petId);
                // Could trigger notifications here
            }
        }
    }
}
