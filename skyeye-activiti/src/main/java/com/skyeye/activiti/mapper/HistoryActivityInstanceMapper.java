package com.skyeye.activiti.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.flowable.engine.impl.persistence.entity.HistoricActivityInstanceEntityImpl;

import java.util.List;

/**
 *
 * @ClassName: HistoryActivityInstanceMapper
 * @Description:
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/18 0:32
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Mapper
public interface HistoryActivityInstanceMapper {

    @Select("SELECT * FROM act_hi_actinst WHERE PROC_INST_ID_ = #{processInstanceId}")
    List<HistoricActivityInstanceEntityImpl> findList(@Param("processInstanceId") String processInstanceId);

    @Delete("DELETE FROM act_hi_actinst WHERE id_ = #{id}")
    int delete(@Param("id") String id);
}
