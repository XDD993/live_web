package com.ddd.provider.config;

import jakarta.annotation.Resource;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RocketMQConsumerConfig implements InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(RocketMQConsumerConfig.class);

	@Resource
	private RocketMQConsumerProperties consumerProperties;

	@Override
	public void afterPropertiesSet() throws Exception {
		initConsumer();
	}

	/**
	 * 初始化消费者
	 */
	public void initConsumer(){
		try {
			DefaultMQPushConsumer pushConsumer = new DefaultMQPushConsumer();
			pushConsumer.setVipChannelEnabled(false);
			pushConsumer.setNamesrvAddr(consumerProperties.getNameSrv());
			pushConsumer.setConsumerGroup(consumerProperties.getGroupName());
//			一次最多消费一条信息
			pushConsumer.setConsumeMessageBatchMaxSize(1);
			pushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
			//要设置订阅的哪个topic
			pushConsumer.subscribe("user-update-cache","*");
			pushConsumer.setMessageListener(new MessageListenerConcurrently() {
				@Override
				public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
					System.out.println(list.get(0).getBody());
					return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
				}
			});
			pushConsumer.start();
			LOGGER.info("mq消费者启动成功,nameSrv is {}",consumerProperties.getNameSrv());
		} catch (MQClientException e) {
			throw new RuntimeException(e);
		}

	}



}
