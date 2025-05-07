package com.skillsync.service;

import com.skillsync.entity.LearningPlan;
import com.skillsync.repo.LearningPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LearningPlanService {
    private final LearningPlanRepository learningPlanRepository;

    @Autowired
    public LearningPlanService(LearningPlanRepository learningPlanRepository) {
        this.learningPlanRepository = learningPlanRepository;
    }

    @CacheEvict(value = "learningPlans", allEntries = true)
    public LearningPlan createLearningPlan(LearningPlan learningPlan) {
        return learningPlanRepository.save(learningPlan);
    }

    @Cacheable(value = "learningPlans", key = "#id")
    public LearningPlan getLearningPlanById(String id) {
        return learningPlanRepository.findById(id)
                .orElse(null);
    }

    @Cacheable(value = "learningPlans", key = "#name")
    public LearningPlan getLearningPlanByName(String name) {
        return learningPlanRepository.findByName(name)
                .orElse(null);
    }

    @CacheEvict(value = "learningPlans", allEntries = true)
    public LearningPlan updateLearningPlan(String id, LearningPlan updatedPlan) {
        return learningPlanRepository.findById(id)
                .map(existingPlan -> {
                    existingPlan.setName(updatedPlan.getName());
                    existingPlan.setDescription(updatedPlan.getDescription());
                    existingPlan.setTopics(updatedPlan.getTopics());
                    existingPlan.setResources(updatedPlan.getResources());
                    return learningPlanRepository.save(existingPlan);
                })
                .orElse(null);
    }

    @CacheEvict(value = "learningPlans", allEntries = true)
    public boolean deleteLearningPlan(String id) {
        if (learningPlanRepository.existsById(id)) {
            learningPlanRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Cacheable(value = "learningPlans", key = "'all'", unless = "#result == null || #result.isEmpty()")
    public List<LearningPlan> getAllLearningPlans() {
        return learningPlanRepository.findAll();
    }

    @Cacheable(value = "learningPlans", key = "'user_' + #userId", unless = "#result == null || #result.isEmpty()")
    public List<LearningPlan> getLearningPlansByUserId(String userId) {
        return learningPlanRepository.findByUserId(userId);
    }
}
