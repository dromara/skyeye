package com.skyeye.knowledge.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.skyeye.knowledge.entity.Knowledge;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface KnowledgeDao extends BaseMapper<Knowledge> {
}
