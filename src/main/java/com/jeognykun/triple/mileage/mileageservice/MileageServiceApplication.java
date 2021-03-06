package com.jeognykun.triple.mileage.mileageservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MileageServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MileageServiceApplication.class, args);
	}

}
