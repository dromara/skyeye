/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.common.constans.MqConstants;
import com.skyeye.common.constans.NoteConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.MyNoteDao;
import com.skyeye.eve.service.MyNoteService;
import com.skyeye.jedis.JedisClientService;
import com.skyeye.service.JobMateMationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 *
 * @ClassName: MyNoteServiceImpl
 * @Description: 笔记管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:56
 *
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class MyNoteServiceImpl implements MyNoteService{
	
	@Autowired
	private MyNoteDao myNoteDao;
	
	@Autowired
	public JedisClientService jedisClient;
	
	@Autowired
	private JobMateMationService jobMateMationService;
	
	/**
	 * 
	     * @Title: queryFileMyNoteByUserId
	     * @Description: 根据当前用户获取笔记文件夹
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void queryFileMyNoteByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		if(ToolUtil.isBlank(map.get("parentId").toString()) || "0".equals(map.get("parentId").toString())){
			// 加载一级文件夹
			List<Map<String, Object>> beans = new ArrayList<>(); 
			if(ToolUtil.isBlank(jedisClient.get(NoteConstants.SYS_FILE_MYNOTE_LIST_MATION))){
				beans = NoteConstants.getFileMyNoteDefaultFolder();
				jedisClient.set(NoteConstants.SYS_FILE_MYNOTE_LIST_MATION, JSONUtil.toJsonStr(beans));
			}else{
				beans = JSONUtil.toList(jedisClient.get(NoteConstants.SYS_FILE_MYNOTE_LIST_MATION), null);
			}
			outputObject.setBeans(beans);
		}else{//加载子文件夹
			Map<String, Object> user = inputObject.getLogParams();
			map.put("userId", user.get("id"));
			List<Map<String, Object>> beans = new ArrayList<>(); 
			String key = NoteConstants.getSysFileMyNoteListMation(map.get("parentId").toString(), map.get("userId").toString());
			if(ToolUtil.isBlank(jedisClient.get(key))){
				beans = myNoteDao.queryFileFolderByUserIdAndParentId(map);
				jedisClient.set(key, JSONUtil.toJsonStr(beans));
			}else{
				beans = JSONUtil.toList(jedisClient.get(key), null);
			}
			outputObject.setBeans(beans);
		}
	}
	
	/**
	 * 
	     * @Title: insertFileMyNoteByUserId
	     * @Description: 添加文件夹
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertFileMyNoteByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		String key = NoteConstants.getSysFileMyNoteListMation(map.get("parentId").toString(), user.get("id").toString());
        jedisClient.delKeys(key);//删除父文件夹的redis的key
		String parentId = setParentId(map.get("parentId").toString());
		if("0".equals(parentId)){
			outputObject.setreturnMessage("错误的文件夹编码！");
			return;
		}
		map.put("parentId", parentId);
		map.put("id", ToolUtil.getSurFaceId());
		map.put("state", 1);
		map.put("createId", user.get("id"));
		map.put("createTime", DateUtil.getTimeAndToString());
		myNoteDao.insertFileFolderByUserId(map);
		map.put("logoPath", NoteConstants.SYS_FILE_CONSOLE_IS_FOLDER_LOGO_PATH);
		outputObject.setBean(map);
	}
	
	/**
	 * 
	     * @Title: deleteFileFolderById
	     * @Description: 删除文件夹以及文件夹下的所有文件
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void deleteFileFolderById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		if("folder".equals(map.get("fileType").toString())){//操作文件夹表
			Map<String, Object> fileParent = myNoteDao.quertFolderParentById(map);
            String[] str = fileParent.get("parentId").toString().split(",");
            String key = NoteConstants.getSysFileMyNoteListMation(str[str.length - 1], user.get("id").toString());
			myNoteDao.deleteFileFolderById(map);//删除自身文件夹
			myNoteDao.deleteFolderChildByFolderId(map);//删除子文件夹
			myNoteDao.deleteFilesByFolderId(map);//删除子文件
			jedisClient.delKeys(key);//删除父文件夹的redis的key
		}else{//操作笔记内容表
			myNoteDao.deleteNoteContentById(map);//删除自身文件
		}
	}
	
	/**
	 * 
	     * @Title: editFileFolderById
	     * @Description: 编辑文件夹名称
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editFileFolderById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		if("folder".equals(map.get("fileType").toString())){//操作文件夹表
			Map<String, Object> fileParent = myNoteDao.quertFolderParentById(map);//获取文件或者文件夹父id
            String[] str = fileParent.get("parentId").toString().split(",");
            String key = NoteConstants.getSysFileMyNoteListMation(str[str.length - 1], user.get("id").toString());
            jedisClient.delKeys(key);//删除父文件夹的redis的key
			myNoteDao.editFileFolderById(map);
		}else{//操作笔记内容表
			myNoteDao.editNoteContentNameById(map);
		}
	}
	
	/**
	 * 
	     * @Title: queryMyNoteListNewByUserId
	     * @Description: 根据当前用户获取最新的笔记列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryMyNoteListNewByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
		List<Map<String, Object>> beans = myNoteDao.queryMyNoteListNewByUserId(map);
		outputObject.setBeans(beans);
		outputObject.settotal(pages.getTotal());
	}
	
	/**
	 * 
	     * @Title: insertMyNoteContentByUserId
	     * @Description: 添加笔记
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void insertMyNoteContentByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		String parentId = setParentId(map.get("parentId").toString());
		if("0".equals(parentId)){
			outputObject.setreturnMessage("错误的文件夹编码！");
			return;
		}
		map.put("parentId", parentId);
		map.put("id", ToolUtil.getSurFaceId());
		map.put("state", 1);
		map.put("createId", user.get("id"));
		map.put("createTime", DateUtil.getTimeAndToString());
		map.put("iconLogo", getIconLogoPathByType(map.get("type").toString()));
		myNoteDao.insertMyNoteContentByUserId(map);
		outputObject.setBean(map);
	}
	
	/**
	 * 
	    * @Title: getIconLogoPathByType
	    * @Description: 根据笔记类型设置展示图标
	    * @param type
	    * @param @return    参数
	    * @return String    返回类型
	    * @throws
	 */
	private String getIconLogoPathByType(String type){
		String iconLogo = "../../assets/images/%s";
		switch (type) {
		case "1":
			// 富文本编辑器
			iconLogo = String.format(Locale.ROOT, iconLogo, "note-1.png");
			break;
		case "2":
			// markdown笔记
			iconLogo = String.format(Locale.ROOT, iconLogo, "note-2.png");
			break;
		case "3":
			// word笔记
			iconLogo = String.format(Locale.ROOT, iconLogo, "note-3.png");
			break;
		case "4":
			// ecxel笔记
			iconLogo = String.format(Locale.ROOT, iconLogo, "note-4.png");
			break;
		default:
			break;
		}
		return iconLogo;
	}
	
	/**
	 * 
	     * @Title: queryFileAndContentListByFolderId
	     * @Description: 根据文件夹id获取文件夹下的文件夹和笔记列表
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryFileAndContentListByFolderId(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		List<Map<String, Object>> beans = myNoteDao.queryFileAndContentListByFolderId(map);
		outputObject.setBeans(beans);
		outputObject.settotal(beans.size());
	}
	
	/**
	 * 
	     * @Title: queryMyNoteContentMationById
	     * @Description: 编辑笔记时回显信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	public void queryMyNoteContentMationById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> bean = myNoteDao.queryMyNoteContentMationById(map);
		outputObject.setBean(bean);
	}
	
	/**
	 * 
	     * @Title: editMyNoteContentById
	     * @Description: 编辑笔记信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@Override
	@Transactional(value="transactionManager")
	public void editMyNoteContentById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String desc = map.get("desc").toString();
		if(desc.length() > 100){
			desc = desc.substring(0, 99);
        }
		map.put("desc", desc);
		Map<String, Object> user = inputObject.getLogParams();
		map.put("userId", user.get("id"));
		myNoteDao.editMyNoteContentById(map);
	}
	
	/**
	 * 
	     * @Title: editFileToDragById
	     * @Description: 保存文件夹拖拽后的信息
	     * @param inputObject
	     * @param outputObject
	     * @throws Exception    参数
	     * @return void    返回类型
	     * @throws
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(value="transactionManager")
	public void editFileToDragById(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		Map<String, Object> user = inputObject.getLogParams();
		String newParentId ="";
		String key = NoteConstants.getSysFileMyNoteListMation(map.get("targetId").toString(), user.get("id").toString());
        jedisClient.delKeys(key);//删除父文件夹的redis的key
		if(map.get("targetId").toString().equals("2")){
			newParentId = "2" + ",";
		}else{
			map.put("id", map.get("targetId"));//目标文件夹的id
			Map<String, Object> endFileParent = myNoteDao.quertFolderParentById(map);//获取目标文件夹的父id
			newParentId = endFileParent.get("parentId").toString() + map.get("targetId").toString() + ",";//拖拽文件夹新的父id
		}
		String arrId = map.get("arrId").toString();
		String[] arr = arrId.split(",");//拖拽文件夹的id数组
		List<Map<String, Object>> folderBeans = new ArrayList<>();
		Map<String, Object> bean;
		for(int i = 0; i < arr.length; i++){
			bean = new HashMap<>();
			bean.put("id", arr[i]);
			folderBeans.add(bean);
		}
		if(!folderBeans.isEmpty()){
			// 选择保存的文件夹不为空
			List<Map<String, Object>> folderNew = myNoteDao.queryFileFolderListByList(folderBeans);
			if(!folderNew.isEmpty())//删除之前的信息
				myNoteDao.deleteFileFolderListByList(folderNew);
			List<Map<String, Object>> fileNew = myNoteDao.queryFileListByList(folderNew);
			if(!fileNew.isEmpty())//删除之前的信息
				myNoteDao.deleteFileListByList(fileNew);
			for(Map<String, Object> folder: folderNew){//重置父id
				String[] str = folder.get("parentId").toString().split(",");
				String thiskey = NoteConstants.getSysFileMyNoteListMation(str[str.length - 1], user.get("id").toString());
                jedisClient.delKeys(thiskey);//删除父文件夹的redis的key
				folder.put("directParentId", str[str.length - 1]);
				folder.put("newId", ToolUtil.getSurFaceId());
			}
			//将数据转化为树的形式，方便进行父id重新赋值
			folderNew = ToolUtil.listToTree(folderNew, "id", "directParentId", "children");
			ToolUtil.FileListParentISEdit(folderNew, newParentId);//替换父id
			folderNew = ToolUtil.FileTreeTransList(folderNew);//将树转为list
			for(Map<String, Object> folder: folderNew){
				folder.put("createId", user.get("id"));
				folder.put("createTime", DateUtil.getTimeAndToString());
				folder.put("state", 1);
			}
			//为文件重置新parentId参数
			for(Map<String, Object> folder: folderNew){
				String parentId = folder.get("parentId").toString() + folder.get("id").toString() + ",";
				String nParentId = folder.get("newParentId").toString() + folder.get("newId").toString() + ",";
				// 重置文件的参数
				for(Map<String, Object> file: fileNew){
					if(file.get("parentId").toString().equals(parentId)){
						file.put("newParentId", nParentId);
						file.put("newId", ToolUtil.getSurFaceId());
						file.put("createId", user.get("id"));
						file.put("createTime", DateUtil.getTimeAndToString());
						file.put("state", 1);
					}
				}
			}
			if(!folderNew.isEmpty())
				myNoteDao.insertFileFolderListByList(folderNew);
			if(!fileNew.isEmpty())
				myNoteDao.insertFileListByList(fileNew);
		}
	}
	
	/**
	 * 
	     * @Title: setParentId
	     * @Description: 根据节点id设置ParentId
	     * @param id
	     * @throws Exception    参数
	     * @return String    返回类型
	     * @throws
	 */
	private String setParentId(String id) throws Exception {
		if ("2".equals(id)) {
			return id + ",";
		} else {
			Map<String, Object> map = new HashMap<>();
			map.put("parentId", id);
			Map<String, Object> folderParent = myNoteDao.queryFolderParentByFolderId(map);// 查询该文件夹的父id
			if (folderParent != null && !folderParent.isEmpty()) {
				return folderParent.get("parentId").toString() + id + ",";
			} else {
				return "0";
			}
		}
	}
	
	/**
     * 
         * @Title: editNoteToMoveById
         * @Description: 保存笔记移动后的信息
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @Override
    @Transactional(value="transactionManager")
    public void editNoteToMoveById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        // 要移动的笔记id
        String rowId = map.get("moveId").toString();
        // 移动后的目录id
        String toId = map.get("toId").toString();
        String parentId = setParentId(toId);
        if("0".equals(parentId)){
        	outputObject.setreturnMessage("错误的文件夹编码！");
        	return;
        }
        myNoteDao.editNoteToMoveById(rowId, parentId, inputObject.getLogParams().get("id").toString());
    }
    
    /**
     * 
         * @Title: queryTreeToMoveByUserId
         * @Description: 获取文件夹或笔记移动时的选择树
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @SuppressWarnings("unchecked")
    @Override
    public void queryTreeToMoveByUserId(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        if(ToolUtil.isBlank(map.get("parentId").toString()) || "0".equals(map.get("parentId").toString())){//加载一级文件夹
            List<Map<String, Object>> beans = new ArrayList<>(); 
            if(ToolUtil.isBlank(jedisClient.get(NoteConstants.SYS_FILE_MYNOTE_LIST_MATION))){
                beans = NoteConstants.getFileMyNoteDefaultFolder();
                jedisClient.set(NoteConstants.SYS_FILE_MYNOTE_LIST_MATION, JSONUtil.toJsonStr(beans));
            }else{
                beans = JSONUtil.toList(jedisClient.get(NoteConstants.SYS_FILE_MYNOTE_LIST_MATION), null);
            }
            outputObject.setBeans(beans);
        }else{//加载子文件夹
            Map<String, Object> user = inputObject.getLogParams();
            map.put("userId", user.get("id"));
            List<Map<String, Object>> beans = new ArrayList<>(); 
            beans = myNoteDao.queryTreeToMoveByUserId(map);
            outputObject.setBeans(beans);
        }
    }
    
    /**
     * 
         * @Title: queryShareNoteById
         * @Description: 根据id获取分享笔记的内容
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
    @Override
    public void queryShareNoteById(InputObject inputObject, OutputObject outputObject) throws Exception {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = myNoteDao.queryShareNoteById(map.get("id").toString());
        if(bean == null){
        	outputObject.setreturnMessage("该信息已被删除或不存在.");
        	return;
        }
        outputObject.setBean(bean);
    }

    /**
     * 
         * @Title: outputNoteIsZipJob
         * @Description: 根据id(文件夹或者笔记id)将笔记输出为压缩包
         * @param inputObject
         * @param outputObject
         * @throws Exception    参数
         * @return void    返回类型
         * @throws
     */
	@Override
	public void outputNoteIsZipJob(InputObject inputObject, OutputObject outputObject) throws Exception {
		Map<String, Object> map = inputObject.getParams();
		String rowId = map.get("id").toString();
		// 类型1.文件夹2.笔记
		int type = Integer.parseInt(map.get("type").toString());
		Map<String, Object> mation;
		if(type == 1){
			// 获取文件夹信息
			mation = myNoteDao.queryFolderMationById(rowId);
		}else{
			// 获取文件信息
			mation = myNoteDao.queryShareNoteById(rowId);
		}
		if(mation == null){
			outputObject.setreturnMessage("该信息已被删除或不存在.");
        	return;
		}
		String userId = inputObject.getLogParams().get("id").toString();
		Map<String, Object> json = new HashMap<>();
		json.put("title", mation.get("title").toString());
		json.put("noteType", type);
		json.put("rowId", rowId);
		json.put("userId", userId);
		json.put("type", MqConstants.JobMateMationJobType.OUTPUT_NOTES_IS_ZIP.getJobType());
		// 启动任务
		jobMateMationService.sendMQProducer(JSONUtil.toJsonStr(json), userId);
	}
	
}
