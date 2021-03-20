package com.supplyfy.core;

import com.supplyfy.core.config.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableConfigurationProperties(ApplicationProperties.class)
@EnableJpaRepositories
@SpringBootApplication
public class SupplyfyCoreApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SupplyfyCoreApiApplication.class, args);
	}

}
