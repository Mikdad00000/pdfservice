package com.pacgem.pdfservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableJpaRepositories
@EnableConfigurationProperties
public class PdfProcessingServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(PdfProcessingServiceApplication.class, args);
	}
}
