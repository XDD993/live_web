package com.ddd.provider.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("qiyu.rmq.consumer")
public class RocketMQConsumerProperties {


	//	rocketmq的nameServer
	private String nameSrv;

	//	rocketmq的分组名
	private String groupName;

	public String getNameSrv() {
		return nameSrv;
	}

	public void setNameSrv(String nameSrv) {
		this.nameSrv = nameSrv;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	@Override
	public String toString() {
		return "RocketMQConsumerProperties{" +
				"nameSrv='" + nameSrv + '\'' +
				", groupName='" + groupName + '\'' +
				'}';
	}
}
