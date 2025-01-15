package com.ddd.live.id.generate.provider;

import com.ddd.live.id.generate.provider.service.IdGenerateService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDubbo
@EnableDiscoveryClient
public class idGenerateProvider implements CommandLineRunner {

	@Resource
	private IdGenerateService idGenerateService;

	public static void main(String[] args) {

		SpringApplication springApplication = new SpringApplication(idGenerateProvider.class);
		springApplication.setWebApplicationType(WebApplicationType.NONE);
		springApplication.run(args);
	}

	@Override
	public void run(String... args) throws Exception {
		for (int i = 0; i < 1300; i++) {
			Long seqId = idGenerateService.getSeqId(1);
			System.out.println(seqId);
		}
	}
}

