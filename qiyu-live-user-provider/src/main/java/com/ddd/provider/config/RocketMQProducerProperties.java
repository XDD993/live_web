package com.ddd.provider.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "qiyu.rmq.producer")
public class RocketMQProducerProperties {

//	rocketmq的nameServer
	private String nameSrv;

//	rocketmq的分组名
	private String groupName;

//	rocketmq的的重试次数
	private int retryTimes;

//	rocketmq的超时时间
	private int sendTimeOut;

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

	public int getRetryTimes() {
		return retryTimes;
	}

	public void setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes;
	}

	public int getSendTimeOut() {
		return sendTimeOut;
	}

	public void setSendTimeOut(int sendTimeOut) {
		this.sendTimeOut = sendTimeOut;
	}

	@Override
	public String toString() {
		return "RocketMQProducerProperties{" +
				"nameSrv='" + nameSrv + '\'' +
				", groupName='" + groupName + '\'' +
				", retryTimes=" + retryTimes +
				", sendTimeOut=" + sendTimeOut +
				'}';
	}
}
