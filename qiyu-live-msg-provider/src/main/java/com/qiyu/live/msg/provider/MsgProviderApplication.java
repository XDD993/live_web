package com.qiyu.live.msg.provider;

import com.ddd.qiyu.live.msg.dto.MsgCheckDTO;
import com.ddd.qiyu.live.msg.enums.MsgSendResultEnum;
import com.qiyu.live.msg.provider.service.ISmsService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.Scanner;

@SpringBootApplication
@EnableDiscoveryClient
@EnableDubbo
//public class MsgProviderApplication implements CommandLineRunner {
public class MsgProviderApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(MsgProviderApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }
    
    @Resource
    private ISmsService smsService;

    /*@Override
    public void run(String... args) throws Exception {
        String phone = "17341741178";
        MsgSendResultEnum msgSendResultEnum = smsService.sendLoginCode(phone);
        System.out.println(msgSendResultEnum);
        while (true) {
            System.out.println("输入验证码：");
            Scanner scanner = new Scanner(System.in);
            int code = scanner.nextInt();
            MsgCheckDTO msgCheckDTO = smsService.checkLoginCode(phone, code);
            System.out.println(msgCheckDTO);
        }*/
    }