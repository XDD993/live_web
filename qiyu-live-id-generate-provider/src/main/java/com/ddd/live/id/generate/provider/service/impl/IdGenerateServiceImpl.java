package com.ddd.live.id.generate.provider.service.impl;

import com.ddd.live.id.generate.provider.dao.IdGenerateMapper;
import com.ddd.live.id.generate.provider.dao.po.IdGeneratePO;
import com.ddd.live.id.generate.provider.service.IdGenerateService;
import com.ddd.live.id.generate.provider.service.bo.LocalSeqIdBO;
import com.ddd.live.id.generate.provider.service.bo.LocalUnSeqIdBO;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class IdGenerateServiceImpl implements IdGenerateService, InitializingBean {

	@Resource
	private IdGenerateMapper generateMapper;

	//日志打印
	private static final Logger LOGGER = LoggerFactory.getLogger(IdGenerateServiceImpl.class);
	//将id存储在内存当中
	private static Map<Integer, LocalSeqIdBO> localSeqIdBOMap = new ConcurrentHashMap<>();
	private static Map<Integer, LocalUnSeqIdBO> localUnSeqIdBOMap = new ConcurrentHashMap<>();
	//用于信息同步的锁
	private static Map<Integer, Semaphore> semaphoreMap = new ConcurrentHashMap();
	//异步线程池
	private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(8, 16, 3, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), new ThreadFactory() {
		@Override
		public Thread newThread(Runnable r) {
			Thread thread = new Thread();
			thread.setName("id-generate-thread-"+ThreadLocalRandom.current().nextInt(1000));
			return thread;
		}
	});

	//获取有序id
	@Override
	public Long getSeqId(Integer id) {
		if (id ==null){
			LOGGER.error("[getSeqId] id is error,id is {}",id);
			return null;
		}
		LocalSeqIdBO localSeqIdBO = localSeqIdBOMap.get(id);
		if (localSeqIdBO == null){
			LOGGER.error("[getSeqId] id is null,id is {}",id);
		}
		this.refreshLocalSeqId(localSeqIdBO);
		if (localSeqIdBO.getCurrentNum().get()>localSeqIdBO.getNextThreshold()){
			LOGGER.error("[getSeqId] id is over limit,id is {}",id);
			return null;
		}
		long returnId = localSeqIdBO.getCurrentNum().getAndIncrement();
		return returnId;
	}

	//获取无序id
	@Override
	public Long getUnSeqId(Integer id) {
		if (id ==null){
			LOGGER.error("[getUnSeqId] id is error,id is {}",id);
			return null;
		}
		LocalUnSeqIdBO localUnSeqIdBO = localUnSeqIdBOMap.get(id);
		if (localUnSeqIdBO == null){
			LOGGER.error("[getUnSeqId] id is null,id is {}",id);
			return null;
		}
		Long returnId = localUnSeqIdBO.getIdQueue().poll();
		if (returnId == null){
			LOGGER.error("[getUnSeqId] id is null,id is {}",id);
			return null;
		}
		this.refreshLocalUnSeqId(localUnSeqIdBO);
		return returnId;
	}

	/**
	 * 刷新本地无序id
	 * @param localUnSeqIdBO
	 */
	private void refreshLocalUnSeqId(LocalUnSeqIdBO localUnSeqIdBO) {
		long begin = localUnSeqIdBO.getCurrentStart();
		long end = localUnSeqIdBO.getNextThreshold();
		long size = localUnSeqIdBO.getIdQueue().size();
		if ((end-begin)*0.25>size){
			IdGeneratePO idGeneratePO = generateMapper.selectById(localUnSeqIdBO.getId());
			tryUpdateMySqlRecord(idGeneratePO);
			LOGGER.info("本地Id同步完成");
		}
	}

	/**
	 * 刷新本地有序id
	 * @param localSeqIdBO
	 */
	private void refreshLocalSeqId(LocalSeqIdBO localSeqIdBO) {
		long step = localSeqIdBO.getNextThreshold() - localSeqIdBO.getCurrentStart();
		if (localSeqIdBO.getCurrentNum().get() - localSeqIdBO.getCurrentStart()>step*0.75){
			Semaphore semaphore = semaphoreMap.get(localSeqIdBO.getId());
			if (semaphore == null){
				LOGGER.error("[getSeqId] semaphore is null,id is {}",localSeqIdBO.getId());
				return;
			}
			boolean acquire = semaphore.tryAcquire();
			if (acquire){
				LOGGER.info("尝试开始同步");
				//异步进行同步id段操作
				threadPoolExecutor.execute(new Runnable() {
					@Override
					public void run() {
						IdGeneratePO idGeneratePO = generateMapper.selectById(localSeqIdBO.getId());
						tryUpdateMySqlRecord(idGeneratePO);
						//释放map
						semaphoreMap.get(localSeqIdBO.getId()).release();
						LOGGER.info("无序Id同步完成,id is {}",idGeneratePO.getId());
					}
				});
			}
			};


	}

	//初始化bean的时候就把这个有序id的值设置到内存里,这个只会执行一次
	@Override
	public void afterPropertiesSet() throws Exception {
		List<IdGeneratePO> idGeneratePOList = generateMapper.selectAll();
		for (IdGeneratePO idGeneratePO : idGeneratePOList) {
			tryUpdateMySqlRecord(idGeneratePO);
			semaphoreMap.put(idGeneratePO.getId(),new Semaphore(1));
		}
	}

	private void tryUpdateMySqlRecord(IdGeneratePO idGeneratePO) {
		//当前的开始值 12000

		int updateResult = generateMapper.updateNewIdCountAndVersion(idGeneratePO.getId(), idGeneratePO.getVersion());
		if (updateResult>0){
			localIdHandler(idGeneratePO);
			return;
		}
		//假如更新失败就使用重试机制
		for (int i = 0; i <3; i++) {
			idGeneratePO = generateMapper.selectById(idGeneratePO.getId());
			updateResult = generateMapper.updateNewIdCountAndVersion(idGeneratePO.getId(), idGeneratePO.getVersion());
			if (updateResult>0){
				localIdHandler(idGeneratePO);
				return;
			}
		}
	}

	//抽取一个公用的更新Id方法
	private void localIdHandler(IdGeneratePO idGeneratePO){
		long currentStart = idGeneratePO.getCurrentStart();
		//当前阶段的阈值 13000
		long nextThreshold = idGeneratePO.getNextThreshold();
		//当前id的值
		long currentNum = currentStart;
		if (idGeneratePO.getIsSqp() == 1){
			LocalSeqIdBO localSeqIdBO = new LocalSeqIdBO();
			localSeqIdBO.setId(idGeneratePO.getId());
			AtomicLong atomicLong = new AtomicLong(currentNum);
			localSeqIdBO.setCurrentNum(atomicLong);
			localSeqIdBO.setCurrentStart(currentStart);
			localSeqIdBO.setNextThreshold(nextThreshold);
			localSeqIdBOMap.put(idGeneratePO.getId(),localSeqIdBO);
		}else{
			LocalUnSeqIdBO localUnSeqIdBO = new LocalUnSeqIdBO();
			localUnSeqIdBO.setId(idGeneratePO.getId());
			localUnSeqIdBO.setId(idGeneratePO.getId());
			localUnSeqIdBO.setCurrentStart(currentStart);
			localUnSeqIdBO.setNextThreshold(nextThreshold);
			long begin = idGeneratePO.getCurrentStart();
			long end = idGeneratePO.getNextThreshold();
			List<Long> list = new ArrayList<>();
			for (long i = begin; i <end ; i++) {
				list.add(i);
			}
			//将List集合里面的数据打乱
			Collections.shuffle(list);
			ConcurrentLinkedDeque<Long> idQue = new ConcurrentLinkedDeque();
			idQue.addAll(list);
			localUnSeqIdBO.setIdQueue(idQue);
			localUnSeqIdBOMap.put(idGeneratePO.getId(),localUnSeqIdBO);
		}
	}
}
