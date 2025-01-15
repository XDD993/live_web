package com.ddd.provider.config;


import ch.qos.logback.core.util.TimeUtil;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class RocketMQProducerConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(RocketMQProducerConfig.class);

	@Resource
	private RocketMQProducerProperties producerProperties;
	@Value("${spring.application.name}")
	private String applicationName;

	@Bean
	public MQProducer mqProducer(){
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(100, 150, 3, TimeUnit.MINUTES, new ArrayBlockingQueue<>(1000), new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread();
				thread.setName(applicationName+"rmq-producer" + ThreadLocalRandom.current().nextInt(1000));
				return thread;
			}
		});

		DefaultMQProducer defaultMQProducer = new DefaultMQProducer();
		try {
			defaultMQProducer.setNamesrvAddr(producerProperties.getNameSrv());
			defaultMQProducer.setProducerGroup(producerProperties.getGroupName());
			defaultMQProducer.setRetryTimesWhenSendFailed(producerProperties.getRetryTimes());
			defaultMQProducer.setRetryTimesWhenSendAsyncFailed(producerProperties.getRetryTimes());
			//开启异步发送并设置异步发送的线程池
			defaultMQProducer.setRetryAnotherBrokerWhenNotStoreOK(true);
			defaultMQProducer.setAsyncSenderExecutor(threadPoolExecutor);
			defaultMQProducer.start();
			LOGGER.info("mq生产者启动成功,nameSrv is {}",producerProperties.getNameSrv());
		} catch (MQClientException e) {
			throw new RuntimeException(e);
		}
		return defaultMQProducer;
	}
}
