package com.ddd.live.id.generate.provider.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("t_id_generate_config")
public class IdGeneratePO {

	@TableId(type = IdType.AUTO)
	/**
	 * 主键id
	 */
	private Integer id;

	/**
	 * 描述
	 */
	private String remark;

	/**
	 * 当前ID所在阶段得阈值
	 */
	private Long nextThreshold;

	/**
	 * 初始化值
	 */
	private String initNum;

	/**
	 * 当前ID所在阶段得开始值
	 */
	private Long currentStart;

	/**
	 * id递增区间
	 */
	private Integer step;

	/**
	 * 是否有序（0无序，1有序）
	 */
	private Long isSqp;

	/**
	 * 业务前缀码，如果没有则返回时不携带
	 */
	private String idProfix;

	/**
	 * 乐观锁版本号
	 */
	private Integer version;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Long getNextThreshold() {
		return nextThreshold;
	}

	public void setNextThreshold(Long nextThreshold) {
		this.nextThreshold = nextThreshold;
	}

	public String getInitNum() {
		return initNum;
	}

	public void setInitNum(String initNum) {
		this.initNum = initNum;
	}

	public Long getCurrentStart() {
		return currentStart;
	}

	public void setCurrentStart(Long currentStart) {
		this.currentStart = currentStart;
	}

	public Integer getStep() {
		return step;
	}

	public void setStep(Integer step) {
		this.step = step;
	}

	public Long getIsSqp() {
		return isSqp;
	}

	public void setIsSqp(Long isSqp) {
		this.isSqp = isSqp;
	}

	public String getIdProfix() {
		return idProfix;
	}

	public void setIdProfix(String idProfix) {
		this.idProfix = idProfix;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
}
