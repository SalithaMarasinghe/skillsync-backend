package com.skillsync.controller;

import com.skillsync.assembler.LearningPlanModelAssembler;
import com.skillsync.dto.LearningPlanDTO;
import com.skillsync.entity.LearningPlan;
import com.skillsync.entity.User;
import com.skillsync.repo.UserRepository;
import com.skillsync.security.JwtService;
import com.skillsync.service.LearningPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/learningplans")
public class LearningPlanController {
    private final LearningPlanService learningPlanService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final LearningPlanModelAssembler assembler;

    @Autowired
    public LearningPlanController(LearningPlanService learningPlanService, JwtService jwtService, UserRepository userRepository, LearningPlanModelAssembler assembler) {
        this.learningPlanService = learningPlanService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.assembler = assembler;
    }

    @PostMapping
    public ResponseEntity<EntityModel<LearningPlan>> createLearningPlan(@RequestHeader("Authorization") String authHeader, @RequestBody LearningPlanDTO learningPlanDTO) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authHeader.substring(7);
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        LearningPlan learningPlan = new LearningPlan();
        learningPlan.setName(learningPlanDTO.getName());
        learningPlan.setDescription(learningPlanDTO.getDescription());
        learningPlan.setTopics(learningPlanDTO.getTopics());
        learningPlan.setResources(learningPlanDTO.getResources());
        learningPlan.setUserId(user.getId());

        LearningPlan created = learningPlanService.createLearningPlan(learningPlan);
        return new ResponseEntity<>(assembler.toModel(created), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<LearningPlan>> getLearningPlanById(@PathVariable String id) {
        LearningPlan learningPlan = learningPlanService.getLearningPlanById(id);
        if (learningPlan != null) {
            return ResponseEntity.ok(assembler.toModel(learningPlan));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by-name/{name}")
    public ResponseEntity<EntityModel<LearningPlan>> getLearningPlanByName(@PathVariable String name) {
        LearningPlan learningPlan = learningPlanService.getLearningPlanByName(name);
        if (learningPlan != null) {
            return ResponseEntity.ok(assembler.toModel(learningPlan));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<LearningPlan>>> getAllLearningPlans() {
        List<EntityModel<LearningPlan>> plans = learningPlanService.getAllLearningPlans().stream()
                .map(assembler::toModel)
                .toList();
        return ResponseEntity.ok(CollectionModel.of(plans));
    }

    @GetMapping("/my")
    public ResponseEntity<CollectionModel<EntityModel<LearningPlan>>> getMyLearningPlans(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authHeader.substring(7);
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<EntityModel<LearningPlan>> plans = learningPlanService.getLearningPlansByUserId(user.getId()).stream()
                .map(assembler::toModel)
                .toList();
        return ResponseEntity.ok(CollectionModel.of(plans));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<LearningPlan>> updateLearningPlan(@PathVariable String id, @RequestBody LearningPlanDTO learningPlanDTO) {
        LearningPlan updated = new LearningPlan();
        updated.setName(learningPlanDTO.getName());
        updated.setDescription(learningPlanDTO.getDescription());
        updated.setTopics(learningPlanDTO.getTopics());
        updated.setResources(learningPlanDTO.getResources());
        LearningPlan result = learningPlanService.updateLearningPlan(id, updated);
        if (result != null) {
            return ResponseEntity.ok(assembler.toModel(result));
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
