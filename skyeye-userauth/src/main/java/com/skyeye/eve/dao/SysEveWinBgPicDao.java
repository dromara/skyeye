/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.dao;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: SysEveWinBgPicDao
 * @Description: win系统桌面图片管理数据层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 22:55
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SysEveWinBgPicDao {

	public List<Map<String, Object>> querySysEveWinBgPicList(Map<String, Object> map) throws Exception;

	public int insertSysEveWinBgPicMation(Map<String, Object> map) throws Exception;

	public int deleteSysEveWinBgPicMationById(Map<String, Object> map) throws Exception;

	public Map<String, Object> querySysEveMationById(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySysEveWinBgPicListToShow(Map<String, Object> map) throws Exception;

	public int insertSysEveWinBgPicMationByCustom(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> querySysEveWinBgPicCustomList(Map<String, Object> map) throws Exception;

}
