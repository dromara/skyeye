package com.skyeye.eve.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import com.skyeye.quartz.entity.SysQuartz;
import java.util.List;

@Repository
@Mapper
public interface SysQuartzDao {
	
	public int deleteByPrimaryKey(String id);

	public int insert(SysQuartz record);

	public int insertSelective(SysQuartz record);

	public SysQuartz selectByPrimaryKey(String id);

	public int updateByPrimaryKeySelective(SysQuartz record);

	public int updateByPrimaryKey(SysQuartz record);

	@Select("select * from sys_quartz")
	public List<SysQuartz> selectAll();
	
}