package com.skillsync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SkillsyncApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkillsyncApplication.class, args);
	}

}
