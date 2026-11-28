package com.retail.ops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class RetailOperationsApplication {

	static void main(String[] args) {
		SpringApplication.run(RetailOperationsApplication.class, args);
	}

}
