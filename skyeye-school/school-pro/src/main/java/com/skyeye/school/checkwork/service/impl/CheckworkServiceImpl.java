/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.checkwork.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.constans.FileConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.QRCodeLinkType;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.common.util.qrcode.QRCodeLogoUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.wall.user.service.IUserService;
import com.skyeye.school.checkwork.classenum.CheckworkSignState;
import com.skyeye.school.checkwork.classenum.CheckworkType;
import com.skyeye.school.checkwork.dao.CheckworkDao;
import com.skyeye.school.checkwork.entity.Checkwork;
import com.skyeye.school.checkwork.entity.CheckworkSign;
import com.skyeye.school.checkwork.service.CheckworkService;
import com.skyeye.school.checkwork.service.CheckworkSignService;
import com.skyeye.school.subject.entity.SubjectClasses;
import com.skyeye.school.subject.service.SubjectClassesService;
import com.skyeye.school.subject.service.SubjectClassesStuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: CheckworkServiceImpl
 * @Description: 考勤管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/24 10:46
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "考勤管理", groupName = "考勤管理")
public class CheckworkServiceImpl extends SkyeyeBusinessServiceImpl<CheckworkDao, Checkwork> implements CheckworkService {

    @Value("${IMAGES_PATH}")
    private String tPath;

    @Autowired
    private SubjectClassesService subjectClassesService;

    @Autowired
    private SubjectClassesStuService subjectClassesStuService;

    @Autowired
    private CheckworkSignService checkworkSignService;

    @Autowired
    private IUserService iUserService;

    @Override
    public QueryWrapper<Checkwork> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<Checkwork> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Checkwork::getSubClassLinkId), commonPageInfo.getHolderId())
            .orderByDesc(MybatisPlusUtil.toColumns(Checkwork::getCreateTime));
        return queryWrapper;
    }

    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        for (Map<String, Object> bean : beans) {
            String type = MybatisPlusUtil.toColumns(Checkwork::getType);
            if (bean.get(type).equals(CheckworkType.SCAN_THE_CODE.getKey())) {
                bean.put("codeNumber", StrUtil.EMPTY);
            }
            if (bean.get(type).equals(CheckworkType.DIGIT.getKey())) {
                bean.put("sourceCode", StrUtil.EMPTY);
                bean.put("qrCodeUrl", StrUtil.EMPTY);
            }
        }
        return beans;
    }

    @Override
    public void validatorEntity(Checkwork entity) {
        super.validatorEntity(entity);
        Integer maintainTime = entity.getMaintainTime();
        if (maintainTime > 30 || maintainTime <= 0) {
            throw new CustomException("持续时间必须大于0小于30分钟");
        }
        if (entity.getType() == CheckworkType.DIGIT.getKey()) {
            // 数字考勤
            if (StrUtil.isEmpty(entity.getCodeNumber())) {
                throw new CustomException("请输入考勤码.");
            }
        } else if (entity.getType() == CheckworkType.SCAN_THE_CODE.getKey()) {
            // 扫描考勤
            SubjectClasses subjectClasses = subjectClassesService.selectById(entity.getSubClassLinkId());
            if (ObjectUtil.isEmpty(subjectClasses.getObjectMation())) {
                throw new CustomException("该科目信息不存在.");
            }
            String imgPath = tPath.replace(FileConstants.FILE_BASE_FIRST_PATH, StrUtil.EMPTY) + subjectClasses.getObjectMation().getImg();
            // 生成考勤码编码
            String code = ToolUtil.getFourWord();
            entity.setSourceCode(code);
            // 生成二维码
            String content = QRCodeLinkType.getJsonStrByType(QRCodeLinkType.STUDENT_CHECKWORK.getKey(), code);
            String sourceCodeLogo = QRCodeLogoUtil.encode(content, imgPath, tPath, true, FileConstants.FileUploadPath.SCHOOL_SUBJECT.getType()[0]);
            entity.setQrCodeUrl(sourceCodeLogo);
        } else {
            throw new CustomException("考勤类型错误.");
        }
    }

    @Override
    public void createPostpose(Checkwork entity, String userId) {
        // 写入需要签到的学生信息
        List<Map<String, Object>> studentList = subjectClassesStuService.queryClassStuIds(entity.getSubClassLinkId());
        List<CheckworkSign> checkworkSignList = new ArrayList<>();
        studentList.forEach(student -> {
            CheckworkSign checkworkSign = new CheckworkSign();
            checkworkSign.setCheckworkId(entity.getId());
            checkworkSign.setUserId(student.get("id").toString());
            checkworkSign.setState(CheckworkSignState.NOT_SIGN.getKey());
            checkworkSignList.add(checkworkSign);
        });
        checkworkSignService.createEntity(checkworkSignList, StrUtil.EMPTY);
    }

    @Override
    public void deletePostpose(String id) {
        checkworkSignService.deleteCheckworkSignByCheckworkId(id);
    }

    @Override
    public Checkwork selectById(String id) {
        Checkwork checkwork = super.selectById(id);
        checkwork.setCheckworkSignList(checkworkSignService.queryCheckworkSignByCheckworkId(id));
        subjectClassesService.setDataMation(checkwork, Checkwork::getSubClassLinkId);
        iUserService.setDataMation(checkwork.getCheckworkSignList(), CheckworkSign::getUserId);
        return checkwork;
    }

    @Override
    public List<Checkwork> selectByIds(String... ids) {
        List<Checkwork> checkworkList = super.selectByIds(ids);
        // 查询学生签到信息
        Map<String, List<CheckworkSign>> listMap = checkworkSignService.queryCheckworkSignByCheckworkId(ids);
        checkworkList.forEach(checkwork -> {
            checkwork.setCheckworkSignList(listMap.get(checkwork.getId()));
        });
        subjectClassesService.setDataMation(checkworkList, Checkwork::getSubClassLinkId);
        return checkworkList;
    }

    @Override
    public void queryCheckworkBySourceCode(InputObject inputObject, OutputObject outputObject) {
        String sourceCode = inputObject.getParams().get("sourceCode").toString();
        Checkwork checkwork = queryCheckworkBySourceCode(sourceCode);
        outputObject.setBean(checkwork);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public Checkwork queryCheckworkBySourceCode(String sourceCode) {
        QueryWrapper<Checkwork> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Checkwork::getSourceCode), sourceCode);
        Checkwork checkwork = getOne(queryWrapper, false);
        if (ObjectUtil.isNotEmpty(checkwork)) {
            checkwork.setCheckworkSignList(checkworkSignService.queryCheckworkSignByCheckworkId(checkwork.getId()));
        }
        subjectClassesService.setDataMation(checkwork, Checkwork::getSubClassLinkId);
        return checkwork;
    }

    @Override
    public void queryCheckworkBySourceCodeAll(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        QueryWrapper<CheckworkSign> signedqueryWrapper = new QueryWrapper<>();
        signedqueryWrapper.eq(MybatisPlusUtil.toColumns(CheckworkSign::getCheckworkId), id);
        signedqueryWrapper.eq(MybatisPlusUtil.toColumns(CheckworkSign::getState), CheckworkSignState.SIGN.getKey());
        long signedCount = checkworkSignService.count(signedqueryWrapper);
        // 查询待考勤的数量
        QueryWrapper<CheckworkSign> unsignedqueryWrapper = new QueryWrapper<>();
        unsignedqueryWrapper.eq(MybatisPlusUtil.toColumns(CheckworkSign::getCheckworkId), id);
        unsignedqueryWrapper.eq(MybatisPlusUtil.toColumns(CheckworkSign::getState), CheckworkSignState.NOT_SIGN.getKey());
        long unsignedCount = checkworkSignService.count(unsignedqueryWrapper);
        // 查询迟到的数量
        QueryWrapper<CheckworkSign> latequeryWrapper = new QueryWrapper<>();
        latequeryWrapper.eq(MybatisPlusUtil.toColumns(CheckworkSign::getCheckworkId), id);
        latequeryWrapper.eq(MybatisPlusUtil.toColumns(CheckworkSign::getState), CheckworkSignState.LATE_SIGN.getKey());
        long lateCount = checkworkSignService.count(latequeryWrapper);
        //返回
        Map<String, Integer> result = new HashMap<>();
        result.put("signedCount", (int) signedCount);
        result.put("unsignedCount", (int) unsignedCount);
        result.put("lateCount", (int) lateCount);
        outputObject.setBean(result);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    // 科目班级下的考勤数
    @Override
    public Long queryCheckWorkNum(String id) {
        QueryWrapper<Checkwork> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Checkwork::getSubClassLinkId), id);
        return count(queryWrapper);
    }

    @Override
    public List<Checkwork> queryCheckworkList(String subjectLinkClassId) {
        QueryWrapper<Checkwork> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Checkwork::getSubClassLinkId), subjectLinkClassId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Checkwork::getCreateTime));
        return list(queryWrapper);
    }

    @Override
    public Long queryCheckWorkPersonNum(String subjectClassId) {
        List<Checkwork> checkworks = queryCheckworkList(subjectClassId);
        if (CollectionUtil.isEmpty(checkworks)) {
            return 0L;
        }
        List<String> ids = checkworks.stream().map(Checkwork::getId).collect(Collectors.toList());
        return checkworkSignService.queryCheckWorkPersonNum(ids);
    }
}
