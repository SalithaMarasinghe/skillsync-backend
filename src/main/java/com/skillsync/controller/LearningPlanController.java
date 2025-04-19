package com.skillsync.controller;

import com.skillsync.dto.LearningPlanDTO;
import com.skillsync.entity.LearningPlan;
import com.skillsync.service.LearningPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/learningplans")
public class LearningPlanController {
    private final LearningPlanService learningPlanService;

    @Autowired
    public LearningPlanController(LearningPlanService learningPlanService) {
        this.learningPlanService = learningPlanService;
    }

    @PostMapping
    public ResponseEntity<LearningPlan> createLearningPlan(@RequestBody LearningPlanDTO learningPlanDTO) {
        LearningPlan learningPlan = new LearningPlan();
        learningPlan.setName(learningPlanDTO.getName());
        learningPlan.setDescription(learningPlanDTO.getDescription());
        learningPlan.setTopics(learningPlanDTO.getTopics());
        learningPlan.setResources(learningPlanDTO.getResources());

        LearningPlan created = learningPlanService.createLearningPlan(learningPlan);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LearningPlan> getLearningPlanById(@PathVariable String id) {
        LearningPlan learningPlan = learningPlanService.getLearningPlanById(id);
        if (learningPlan != null) {
            return ResponseEntity.ok(learningPlan);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by-name/{name}")
    public ResponseEntity<LearningPlan> getLearningPlanByName(@PathVariable String name) {
        LearningPlan learningPlan = learningPlanService.getLearningPlanByName(name);
        if (learningPlan != null) {
            return ResponseEntity.ok(learningPlan);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<java.util.List<LearningPlan>> getAllLearningPlans() {
        return ResponseEntity.ok(learningPlanService.getAllLearningPlans());
    }

    @PutMapping("/{id}")
    public ResponseEntity<LearningPlan> updateLearningPlan(@PathVariable String id, @RequestBody LearningPlanDTO learningPlanDTO) {
        LearningPlan updated = new LearningPlan();
        updated.setName(learningPlanDTO.getName());
        updated.setDescription(learningPlanDTO.getDescription());
        updated.setTopics(learningPlanDTO.getTopics());
        updated.setResources(learningPlanDTO.getResources());
        LearningPlan result = learningPlanService.updateLearningPlan(id, updated);
        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLearningPlan(@PathVariable String id) {
        if (learningPlanService.deleteLearningPlan(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
