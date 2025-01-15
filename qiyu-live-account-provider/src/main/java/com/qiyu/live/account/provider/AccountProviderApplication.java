package com.qiyu.live.account.provider;

import com.qiyu.live.account.provider.service.IAccountTokenService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.concurrent.CountDownLatch;

@SpringBootApplication
@EnableDubbo
@EnableDiscoveryClient
public class AccountProviderApplication  {

    public static void main(String[] args) throws InterruptedException {
        //若Dubbo服务会自动关闭，加上CountDownLatch
        CountDownLatch countDownLatch = new CountDownLatch(1);
        SpringApplication springApplication = new SpringApplication(AccountProviderApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
        countDownLatch.await();
    }

    @Resource
    private IAccountTokenService accountTokenService;

    /*@Override
    public void run(String... args) throws Exception {
        Long userId = 111113L;
        String token = accountTokenService.createAndSaveLoginToken(userId);
        Long userIdByToken = accountTokenService.getUserIdByToken(token);
        System.out.println("userId is " + userIdByToken);
    }*/
}