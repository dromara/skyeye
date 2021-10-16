package com.skyeye.activiti.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 自定义activiti mapper接口
 */
public interface ActivityMapper {

    @Update("update ACT_RE_PROCDEF set NAME_ = #{name} where ID_ = #{processDefinitionId}")
    void updateProcessDefinitionName(@Param("name") String name, @Param("processDefinitionId") String processDefinitionId);

    @Delete("delete ACT_HI_ACTINST where TASK_ID_ = #{taskId}")
    void deleteHisActivityInstanceByTaskId(@Param("taskId") String taskId);

    @Delete("delete ACT_HI_TASKINST where ID_ = #{taskId}")
    void deleteHisTaskInstanceByTaskId(@Param("taskId") String taskId);

}
