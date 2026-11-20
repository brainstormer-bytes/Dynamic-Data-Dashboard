package com.axlero.dashboard.Criteria_API;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CriteriaApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CriteriaApiApplication.class, args);
	}
}
