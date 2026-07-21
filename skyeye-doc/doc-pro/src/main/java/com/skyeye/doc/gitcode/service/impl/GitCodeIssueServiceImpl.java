/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.gitcode.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.doc.code.service.CodeVersionService;
import com.skyeye.doc.gitcode.dao.GitCodeIssueDao;
import com.skyeye.doc.gitcode.entity.GitCodeIssue;
import com.skyeye.doc.gitcode.service.GitCodeIssueService;
import com.skyeye.doc.gitcode.util.GitCodeApiClient;
import com.skyeye.doc.member.entity.DocMember;
import com.skyeye.doc.member.service.DocMemberService;
import com.skyeye.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: GitCodeIssueServiceImpl
 * @Description: GitCode Issue服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/1/1 12:00
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Slf4j
@Service
@SkyeyeService(name = "GitCode Issue管理", groupName = "GitCode管理", tenant = TenantEnum.PLATE)
public class GitCodeIssueServiceImpl extends SkyeyeBusinessServiceImpl<GitCodeIssueDao, GitCodeIssue> implements GitCodeIssueService {

    @Autowired
    private GitCodeApiClient gitCodeApiClient;

    @Autowired
    private DocMemberService docMemberService;

    @Autowired
    private CodeVersionService codeVersionService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        List<String> createIds = beans.stream().map(bean -> bean.get("createId").toString()).distinct().collect(Collectors.toList());
        Map<String, DocMember> memberMap = docMemberService.selectMapByIds(createIds);
        beans.forEach(bean -> {
            String createId = bean.get("createId").toString();
            DocMember docMember = memberMap.get(createId);
            if (ObjectUtil.isNull(docMember)) {
                return;
            }
            bean.put("memberName", docMember.getName().substring(0, 1).toUpperCase() + "**");
            bean.put("planetNum", docMember.getPlanetNum());
        });
        codeVersionService.setMationForMap(beans, "versionId", "versionMation");
        return beans;
    }

    @Override
    public void queryIssueListForAdmin(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        docMemberService.setMationForMap(beans, "createId", "createMation");
        codeVersionService.setMationForMap(beans, "versionId", "versionMation");
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    @Override
    protected QueryWrapper<GitCodeIssue> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<GitCodeIssue> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(GitCodeIssue::getVersionId), commonPageInfo.getObjectId());
        }
        if (WhetherEnum.ENABLE_USING.getKey().toString().equals(commonPageInfo.getType())) {
            // 是否只查询我发布的
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            queryWrapper.eq(MybatisPlusUtil.toColumns(GitCodeIssue::getCreateId), userId);
        }

        return queryWrapper;
    }

    @Override
    protected void createPrepose(GitCodeIssue entity) {
        // 调用GitCode API创建Issue
        JSONObject result = gitCodeApiClient.createIssue(entity.getTitle(), entity.getDescription(), entity.getAssigneeId(), entity.getLabels());
        String issueNumber = result.getStr("number");
        if (StrUtil.isBlank(issueNumber)) {
            String msg = StrUtil.blankToDefault(result.getStr("message"), StrUtil.blankToDefault(result.getStr("error"), "未返回 Issue 编号"));
            throw new CustomException("GitCode 创建 Issue 失败：" + msg);
        }
        entity.setRecordBug(WhetherEnum.DISABLE_USING.getKey());
        entity.setRecordRequirement(WhetherEnum.DISABLE_USING.getKey());
        entity.setBugCompleted(WhetherEnum.DISABLE_USING.getKey());
        entity.setRequirementCompleted(WhetherEnum.DISABLE_USING.getKey());
        entity.setIssueId(issueNumber);
        entity.setProjectUrl(gitCodeApiClient.projectUrl);
    }

    @Override
    protected void updatePrepose(GitCodeIssue entity) {
        GitCodeIssue oldGitCodeIssue = selectById(entity.getId());

        entity.setRecordBug(oldGitCodeIssue.getRecordBug());
        entity.setRecordRequirement(oldGitCodeIssue.getRecordRequirement());
        entity.setBugCompleted(oldGitCodeIssue.getBugCompleted());
        entity.setRequirementCompleted(oldGitCodeIssue.getRequirementCompleted());
    }

    @Override
    protected void updatePostpose(GitCodeIssue entity, String userId) {
        // 构建更新数据
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("title", entity.getTitle());
        updateData.put("description", entity.getDescription());
        updateData.put("state", entity.getState());
        updateData.put("assigneeId", entity.getAssigneeId());
        updateData.put("labels", entity.getLabels());

        GitCodeIssue gitCodeIssue = selectById(entity.getId());
        // 调用GitCode API更新Issue
        gitCodeApiClient.updateIssue(gitCodeIssue.getIssueId(), updateData);
    }

    @Override
    public GitCodeIssue selectById(String id) {
        GitCodeIssue gitCodeIssue = super.selectById(id);
        codeVersionService.setDataMation(gitCodeIssue, GitCodeIssue::getVersionId);
        DocMember docMember = docMemberService.selectById(gitCodeIssue.getCreateId());
        if (ObjectUtil.isNotEmpty(docMember) && StrUtil.isNotBlank(docMember.getId())) {
            gitCodeIssue.setMemberName(docMember.getName().substring(0, 1).toUpperCase() + "**");
            gitCodeIssue.setPlanetNum(docMember.getPlanetNum());
        }
        return gitCodeIssue;
    }

    @Override
    public void updateIssueState(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String state = params.get("state").toString();

        GitCodeIssue gitCodeIssue = selectById(id);
        if (ObjectUtil.isEmpty(gitCodeIssue) || StrUtil.isEmpty(gitCodeIssue.getId())) {
            return;
        }

        UpdateWrapper<GitCodeIssue> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(GitCodeIssue::getState), state);
        update(updateWrapper);
        refreshCache(id);

        // 调用GitCode API更新Issue状态
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("state", state);
        gitCodeApiClient.updateIssue(gitCodeIssue.getIssueId(), updateData);
    }

    @Override
    public void updateIssueRecordBug(InputObject inputObject, OutputObject outputObject) {
        updateIssueWhetherField(inputObject, "recordBug", MybatisPlusUtil.toColumns(GitCodeIssue::getRecordBug));
    }

    @Override
    public void updateIssueRecordRequirement(InputObject inputObject, OutputObject outputObject) {
        updateIssueWhetherField(inputObject, "recordRequirement", MybatisPlusUtil.toColumns(GitCodeIssue::getRecordRequirement));
    }

    @Override
    public void updateIssueBugCompleted(InputObject inputObject, OutputObject outputObject) {
        updateIssueWhetherField(inputObject, "bugCompleted", MybatisPlusUtil.toColumns(GitCodeIssue::getBugCompleted));
    }

    @Override
    public void updateIssueRequirementCompleted(InputObject inputObject, OutputObject outputObject) {
        updateIssueWhetherField(inputObject, "requirementCompleted", MybatisPlusUtil.toColumns(GitCodeIssue::getRequirementCompleted));
    }

    private void updateIssueWhetherField(InputObject inputObject, String fieldKey, String column) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        Integer fieldValue = Integer.parseInt(params.get(fieldKey).toString());
        if (!WhetherEnum.ENABLE_USING.getKey().equals(fieldValue)
            && !WhetherEnum.DISABLE_USING.getKey().equals(fieldValue)) {
            throw new CustomException("参数值无效，仅支持0或1");
        }

        GitCodeIssue gitCodeIssue = selectById(id);
        if (ObjectUtil.isEmpty(gitCodeIssue) || StrUtil.isEmpty(gitCodeIssue.getId())) {
            throw new CustomException("Issue不存在");
        }

        UpdateWrapper<GitCodeIssue> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(column, fieldValue);
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    public void insertUploadImageToIssue(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String images = params.get("images").toString();
        String fileName = params.get("fileName").toString();
        try {
            // 调用GitCode API上传图片（使用base64编码）
            JSONObject result = gitCodeApiClient.uploadImage(images, fileName);

            if (result != null) {
                // 构建返回结果
                Map<String, Object> uploadResult = new HashMap<>();
                uploadResult.put("url", result.getStr("url"));
                uploadResult.put("alt", result.getStr("alt"));
                uploadResult.put("markdown", result.getStr("markdown"));
                uploadResult.put("fileName", fileName);

                outputObject.setBean(uploadResult);
                outputObject.settotal(1);
            } else {
                outputObject.setreturnMessage("图片上传失败");
            }

        } catch (Exception e) {
            log.error("上传图片到Issue失败", e);
            outputObject.setreturnMessage("上传图片失败: " + e.getMessage());
        }
    }

    @Override
    public void updateIssueCommentCount(String id, boolean isAdd) {
        GitCodeIssue gitCodeIssue = selectById(id);
        if (ObjectUtil.isEmpty(gitCodeIssue) || StrUtil.isEmpty(gitCodeIssue.getId())) {
            return;
        }
        UpdateWrapper<GitCodeIssue> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        if (isAdd) {
            updateWrapper.set(MybatisPlusUtil.toColumns(GitCodeIssue::getCommentCount), gitCodeIssue.getCommentCount() + 1);
        } else {
            updateWrapper.set(MybatisPlusUtil.toColumns(GitCodeIssue::getCommentCount), gitCodeIssue.getCommentCount() - 1);
        }
        update(updateWrapper);
        refreshCache(id);
    }


}
