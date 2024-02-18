package com.noken29.svrbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories("com.noken29.svrbe.repository")
@EntityScan("com.noken29.svrbe.domain")
@SpringBootApplication(scanBasePackages = "com.noken29.svrbe")
public class SvrBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(SvrBeApplication.class, args);
	}

}
