package com.ddd.live.id.generate.provider.service.bo;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;

public class LocalUnSeqIdBO {

	private int id;
	/**
	 *在内存中记录的当前w无序id的队列
	 */
	private ConcurrentLinkedDeque<Long> idQueue;

	/**
	 *当前id的开始值
	 */
	private Long currentStart;
	/**
	 * 当前id的结束值
	 */
	private Long nextThreshold;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ConcurrentLinkedDeque<Long> getIdQueue() {
		return idQueue;
	}

	public void setIdQueue(ConcurrentLinkedDeque<Long> idQueue) {
		this.idQueue = idQueue;
	}

	public Long getCurrentStart() {
		return currentStart;
	}

	public void setCurrentStart(Long currentStart) {
		this.currentStart = currentStart;
	}

	public Long getNextThreshold() {
		return nextThreshold;
	}

	public void setNextThreshold(Long nextThreshold) {
		this.nextThreshold = nextThreshold;
	}
}
