package com.skyeye.knowledge.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.skyeye.knowledge.entity.KnowledgeDoc;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface KnowledgeDocDao extends BaseMapper<KnowledgeDoc> {
}
