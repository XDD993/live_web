package com.qiyu.live.im.core.server;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.concurrent.CountDownLatch;

/**
 * netty启动类
 */

@SpringBootApplication
@EnableDiscoveryClient
public class ImCoreServerApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(ImCoreServerApplication.class);


	public static void main(String[] args) throws InterruptedException {
		CountDownLatch countDownLatch = new CountDownLatch(1);
		SpringApplication springApplication = new SpringApplication(ImCoreServerApplication.class);
		springApplication.setWebApplicationType(WebApplicationType.NONE);
		springApplication.run(args);
		countDownLatch.await();
	}
}
