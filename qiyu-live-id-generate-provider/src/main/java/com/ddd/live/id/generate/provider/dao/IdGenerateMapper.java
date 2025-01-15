package com.ddd.live.id.generate.provider.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ddd.live.id.generate.provider.dao.po.IdGeneratePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface IdGenerateMapper extends BaseMapper<IdGeneratePO> {

	@Select("select * from t_id_generate_config")
	List<IdGeneratePO> selectAll();

	//next=13000+1000 current = 12000+1000 version = 2
	@Update("update t_id_generate_config set next_threshold=next_threshold+step,current_start=current_start+step,version=version+1 where id = #{id} and version = #{version}")
	int updateNewIdCountAndVersion(@Param("id") int id, @Param("version") int version);
}
