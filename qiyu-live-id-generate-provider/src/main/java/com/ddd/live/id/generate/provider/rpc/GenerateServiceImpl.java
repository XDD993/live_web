package com.ddd.live.id.generate.provider.rpc;

import com.ddd.live.id.generate.interfaces.idGenerateRpc;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class GenerateServiceImpl implements idGenerateRpc {
	@Override
	public Long getSeqId(Integer id) {
		return null;
	}

	@Override
	public Long getUnSeqId(Integer id) {
		return null;
	}
}
