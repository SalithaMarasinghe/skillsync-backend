package com.skillsync.assembler;

import com.skillsync.controller.LearningPlanController;
import com.skillsync.entity.LearningPlan;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class LearningPlanModelAssembler implements RepresentationModelAssembler<LearningPlan, EntityModel<LearningPlan>> {
    @Override
    public EntityModel<LearningPlan> toModel(LearningPlan plan) {
        return EntityModel.of(plan,
                linkTo(methodOn(LearningPlanController.class).getLearningPlanById(plan.getId())).withSelfRel(),
                linkTo(methodOn(LearningPlanController.class).getAllLearningPlans()).withRel("all-learning-plans"),
                linkTo(methodOn(LearningPlanController.class).deleteLearningPlan(plan.getId())).withRel("delete"),
                linkTo(methodOn(LearningPlanController.class).updateLearningPlan(plan.getId(), null)).withRel("update")
        );
    }
}
