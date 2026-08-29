/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.subject.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.constans.FileConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.QRCodeLinkType;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.FileUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.common.util.qrcode.QRCodeLogoUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.jedis.util.RedisLock;
import com.skyeye.school.announcement.service.AnnouncementService;
import com.skyeye.school.assignment.service.AssignmentService;
import com.skyeye.school.assignment.service.AssignmentSubService;
import com.skyeye.school.checkwork.classenum.CheckworkSignState;
import com.skyeye.school.checkwork.service.CheckworkService;
import com.skyeye.school.checkwork.service.CheckworkSignService;
import com.skyeye.school.courseware.service.CoursewareService;
import com.skyeye.school.courseware.service.CoursewareStudyService;
import com.skyeye.school.datum.service.DatumService;
import com.skyeye.school.exam.service.ExamDirectoryAnService;
import com.skyeye.school.exam.service.ExamService;
import com.skyeye.school.grade.entity.Classes;
import com.skyeye.school.grade.service.ClassesService;
import com.skyeye.school.score.service.ScoreTypeChildService;
import com.skyeye.school.semester.service.SemesterService;
import com.skyeye.school.subject.dao.SubjectClassesDao;
import com.skyeye.school.subject.entity.Subject;
import com.skyeye.school.subject.entity.SubjectClasses;
import com.skyeye.school.subject.entity.SubjectClassesStu;
import com.skyeye.school.subject.service.SubjectClassesService;
import com.skyeye.school.subject.service.SubjectClassesStuService;
import com.skyeye.school.subject.service.SubjectClassesTopService;
import com.skyeye.school.subject.service.SubjectService;
import com.skyeye.school.topic.service.TopicService;
import com.skyeye.school.topiccomment.service.TopicCommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: SubjectClassesServiceImpl
 * @Description: 科目表与班级表的关系服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/10 14:52
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
@Service
@SkyeyeService(name = "科目表与班级表的关系管理", groupName = "科目管理")
public class SubjectClassesServiceImpl extends SkyeyeBusinessServiceImpl<SubjectClassesDao, SubjectClasses> implements SubjectClassesService {

    @Value("${IMAGES_PATH}")
    private String tPath;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private ClassesService classesService;

    @Autowired
    private SemesterService semesterService;

    @Autowired
    private SubjectClassesStuService subjectClassesStuService;

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private TopicCommentService topicCommentService;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private AssignmentSubService assignmentSubService;

    @Autowired
    private CoursewareService coursewareService;

    @Autowired
    private CheckworkService checkworkService;

    @Autowired
    private SubjectClassesTopService subjectClassesTopService;

    @Autowired
    private DatumService datumService;

    @Autowired
    private ExamService examService;

    @Autowired
    private ExamDirectoryAnService examDirectoryAnService;

    @Autowired
    private ScoreTypeChildService scoreTypeChildService;

    @Autowired
    private CoursewareStudyService coursewareStudyService;

    @Autowired
    private CheckworkSignService checkworkSignService;


    @Override
    public QueryWrapper<SubjectClasses> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<SubjectClasses> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClasses::getObjectId), commonPageInfo.getObjectId());
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        classesService.setMationForMap(beans, "classesId", "classesMation");
        return beans;
    }

    @Override
    public void queryNoPageSubjectClassesList(InputObject inputObject, OutputObject outputObject) {
        String subjectId = inputObject.getParams().get("objectId").toString();
        if (StrUtil.isBlank(subjectId)) {
            return;
        }
        QueryWrapper<SubjectClasses> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClasses::getObjectId), subjectId);
        List<SubjectClasses> list = list(queryWrapper);
        classesService.setDataMation(list, SubjectClasses::getClassesId);
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }

    @Override
    public void createPrepose(SubjectClasses entity) {
        Subject subject = subjectService.selectById(entity.getObjectId());
        log.info("subject is {}", JSONUtil.toJsonStr(subject));
        String imgPath = tPath.replace(FileConstants.FILE_BASE_FIRST_PATH, StrUtil.EMPTY) + subject.getImg();
        log.info("imgPath is {}", imgPath);
        // 生成加课码编码
        String code = ToolUtil.getFourWord();
        log.info("code is {}", code);
        entity.setSourceCode(code);
        // 生成二维码
        String content = QRCodeLinkType.getJsonStrByType(QRCodeLinkType.SUBJECT_CLASSES.getKey(), code);
        log.info("content is {}", content);
        String sourceCodeLogo = QRCodeLogoUtil.encode(content, imgPath, tPath, true, FileConstants.FileUploadPath.SCHOOL_SUBJECT.getType()[0]);
        entity.setSourceCodeLogo(sourceCodeLogo);
        entity.setPeopleNum(CommonNumConstants.NUM_ZERO);
    }

    @Override
    protected void createPostpose(SubjectClasses entity, String userId) {
        scoreTypeChildService.initScoreTypeChild(entity.getObjectId(), entity.getId());
    }

    @Override
    public void deletePostpose(SubjectClasses entity) {
        FileUtil.deleteFile(tPath.replace(FileConstants.FILE_BASE_FIRST_PATH, StrUtil.EMPTY) + entity.getSourceCode());
        // 删除班级学生关联表
        subjectClassesStuService.deleteBySubClassLinkId(Arrays.asList(entity.getId()));
        // 删除班级学生置顶课程
        subjectClassesTopService.deleteSubjectClassesTopBySubClassLinkId(entity.getId());
        // 删除成绩信息
        scoreTypeChildService.deleteBySubjectIdAndSubjectClassId(entity.getObjectId(), entity.getId());
    }

    @Override
    public SubjectClasses selectById(String id) {
        SubjectClasses subjectClasses = super.selectById(id);
        classesService.setDataMation(subjectClasses, SubjectClasses::getClassesId);
        subjectService.setDataMation(subjectClasses, SubjectClasses::getObjectId);
        semesterService.setDataMation(subjectClasses, SubjectClasses::getSemesterId);
        return subjectClasses;
    }

    @Override
    public List<SubjectClasses> selectByIds(String... ids) {
        List<SubjectClasses> subjectClassesList = super.selectByIds(ids);
        classesService.setDataMation(subjectClassesList, SubjectClasses::getClassesId);
        subjectService.setDataMation(subjectClassesList, SubjectClasses::getObjectId);
        semesterService.setDataMation(subjectClassesList, SubjectClasses::getSemesterId);
        return subjectClassesList;
    }

    @Override
    public void querySubjectClassesBySourceCode(InputObject inputObject, OutputObject outputObject) {
        String sourceCode = inputObject.getParams().get("sourceCode").toString();
        QueryWrapper<SubjectClasses> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClasses::getSourceCode), sourceCode);
        SubjectClasses subjectClasses = getOne(queryWrapper, false);
        if (ObjectUtil.isEmpty(subjectClasses)) {
            throw new CustomException("该加课码对应的课程信息不存在");
        }
        subjectClasses = selectById(subjectClasses.getId());
        outputObject.setBean(subjectClasses);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public List<SubjectClasses> querySubjectClassesByObjectId(String... objectId) {
        List<String> objectIdList = Arrays.asList(objectId);
        if (CollectionUtil.isEmpty(objectIdList)) {
            return CollectionUtil.newArrayList();
        }
        QueryWrapper<SubjectClasses> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(SubjectClasses::getObjectId), objectIdList);
        List<SubjectClasses> subjectClassesList = list(queryWrapper);
        List<String> ids = subjectClassesList.stream().map(SubjectClasses::getId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(ids)) {
            return CollectionUtil.newArrayList();
        }
        return selectByIds(ids.toArray(new String[ids.size()]));
    }

    @Override
    public void editSubjectClassesPeopleNum(String id, Boolean isAdd) {
        String lockKey = String.format("editSubjectClassesPeopleNum_%s", id);
        RedisLock lock = new RedisLock(lockKey);
        try {
            if (!lock.lock()) {
                // 加锁失败
                throw new CustomException("增减人员失败，当前并发量较大，请稍后再次尝试.");
            }
            log.info("get lock success, lockKey is {}.", lockKey);
            SubjectClasses subjectClasses = selectById(id);
            if (isAdd) {
                // 新增
                UpdateWrapper<SubjectClasses> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq(CommonConstants.ID, id);
                updateWrapper.set(MybatisPlusUtil.toColumns(SubjectClasses::getPeopleNum), subjectClasses.getPeopleNum() + CommonNumConstants.NUM_ONE);
                update(updateWrapper);
            } else {
                // 减少
                UpdateWrapper<SubjectClasses> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq(CommonConstants.ID, id);
                updateWrapper.set(MybatisPlusUtil.toColumns(SubjectClasses::getPeopleNum), subjectClasses.getPeopleNum() - CommonNumConstants.NUM_ONE);
                update(updateWrapper);
            }
            refreshCache(id);
            log.info("editSubjectClassesPeopleNum is success.");
        } catch (Exception ee) {
            log.warn("editSubjectClassesPeopleNum error, because {}", ee);
            if (ee instanceof CustomException) {
                throw new CustomException(ee.getMessage());
            }
            throw new RuntimeException(ee.getMessage());
        } finally {
            lock.unlock();
        }
    }

    public void changeEnabled(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String enabled = map.get("enabled").toString();
        String subjectClassesId = map.get("id").toString();

        UpdateWrapper<SubjectClasses> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, subjectClassesId);
        updateWrapper.set(MybatisPlusUtil.toColumns(SubjectClasses::getEnabled), enabled);
        update(updateWrapper);
        refreshCache(subjectClassesId);
    }

    public void changeQuit(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String quit = map.get("quit").toString();
        String subjectClassesId = map.get("id").toString();

        UpdateWrapper<SubjectClasses> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, subjectClassesId);
        updateWrapper.set(MybatisPlusUtil.toColumns(SubjectClasses::getQuit), quit);
        update(updateWrapper);
        refreshCache(subjectClassesId);
    }

    @Override
    public void updatePeopleNum(String subClassLinkId, Integer count) {
        UpdateWrapper<SubjectClasses> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, subClassLinkId);
        updateWrapper.set(MybatisPlusUtil.toColumns(SubjectClasses::getPeopleNum), count);
        update(updateWrapper);
    }

    @Override
    public void queryTeacherMessage(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String subClassLinkId = map.get("subClassLinkId").toString();
        QueryWrapper<SubjectClasses> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, subClassLinkId);
        List<SubjectClasses> subjectClassesList = list(queryWrapper);
        iAuthUserService.setDataMation(subjectClassesList, SubjectClasses::getCreateId);
        outputObject.setBean(subjectClassesList);
        outputObject.settotal(subjectClassesList.size());
    }

    @Override
    public SubjectClasses getSubjectClassesByObjectIdAndClassesId(String objectId, String classesId) {
        QueryWrapper<SubjectClasses> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClasses::getObjectId), objectId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClasses::getClassesId), classesId);
        return getOne(queryWrapper);
    }

    @Override
    public void querySubjectClassesInfo(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString(); // 科目与班级的关系id
        String subjectId = selectById(id).getObjectId();
        String classId = selectById(id).getClassesId();

        Map<String, Object> resultMap = new HashMap<>();
        // 获取加课人数
        Long joinNum = subjectClassesStuService.queryClassStuNum(id);
        resultMap.put("joinNum", joinNum);
        // 资料个数
        Long dataNum = datumService.queryClassDataNum(subjectId);
        resultMap.put("dataNum", dataNum);
        // 公告数
        Long announcementNum = announcementService.queryClassNoticeNum(id);
        resultMap.put("announcementNum", announcementNum);
        // 话题发帖数
        Long topicNum = topicService.queryClassTopicNum(id);
        resultMap.put("topicNum", topicNum);
        // 话题参与人数
        Long topicJoinNum = topicCommentService.queryClassTopicJoinNum(id);
        resultMap.put("topicJoinNum", topicJoinNum);
        // 话题参与人次--评论总数
        Long topicJoinPersonNum = topicCommentService.queryClassTopicJoinPersonNum(id, null);
        resultMap.put("topicJoinPersonNum", topicJoinPersonNum);
        // 作业数
        Long assignmentNum = assignmentService.queryClassAssignmentNum(id);
        resultMap.put("assignmentNum", assignmentNum);
        // 作业参数人数
        Long assignmentJoinNum = assignmentSubService.queryClassAssignmentJoinNum(id);
        resultMap.put("assignmentJoinNum", assignmentJoinNum);
        // 测试数量
        Long testNum = examService.queryClassExamSurveyDirectoryNum(classId, subjectId);
        resultMap.put("testNum", testNum);
        // 测试参与人次
        Long testJoinNum = examDirectoryAnService.queryClassExamSurveyAnswerNum(classId, null, subjectId);
        resultMap.put("testJoinNum", testJoinNum);
        outputObject.setBean(resultMap);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryStudentAnalysis(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString(); // 科目与班级的关系id
        SubjectClasses subjectClasses = selectById(id);
        String subjectId = subjectClasses.getObjectId();
        String classesId = subjectClasses.getClassesId();
        // 查询班级学生信息
        List<Map<String, Object>> studentList = subjectClassesStuService.queryClassStuIds(id);
        if (CollectionUtil.isEmpty(studentList)) {
            return;
        }
        List<Map<String, Object>> bean = new ArrayList<>();
        // 获取科目班级下的话题数量
        Long topicNum = topicService.queryClassTopicNum(id);
        // 获取考勤数量
        Long checkWorkNum = checkworkService.queryCheckWorkNum(id);
        // 获取测试数量--按班级查
        Long testNum = examService.queryClassExamSurveyDirectoryNum(classesId, subjectId);
        // 获取资料数--按科目查
        Long dataNum = datumService.queryClassDataNum(subjectId);
        // 获取互动课件数 --按科目查
        Long coursewareNum = coursewareService.queryClassCoursewareNum(subjectId);
        // 获取作业数
        Long assignmentNum = assignmentService.queryClassAssignmentNum(id);


        // 与学生有关的数据
        List<String> stuIds = new ArrayList<>();
        List<String> stuNumbers = new ArrayList<>();
        for (Map<String, Object> student : studentList) {
            stuIds.add(student.get("id").toString());
            stuNumbers.add(student.get("studentNumber").toString());
        }
        // 1.获取学生上传的资料数--按stuId分组
        Map<String, Long> stuDataNumMap = datumService.queryDatumBySubjectIdAndStuIds(subjectId, stuIds);
        // 2.获取学生学习的互动课件数--按stuId分组
        Map<String, Long> stuCoursewareNumMap = coursewareStudyService.queryStuCourBySubjectIdsAndStuIds(subjectId, stuIds);
        // 3.获取学生提交的作业数--按stuId分组
        Map<String, Long> stuAssignmentNumMap = assignmentSubService.queryStuAssignNumBySubClassesId(id, stuIds);
        // 4.获取学生考勤次数---按stuId分组
        Map<String, Long> stuCheckWorkNumMap = checkworkSignService.queryStuCheckWorkSignNums(id, stuIds);
        // 5.获取学生的奖励星星数
        Map<String, String> stuStarNumMap = subjectClassesStuService.queryStuStarNumBySubClassesId(id, stuNumbers);
        // 6.获取学生的弹幕数量
        Map<String, Long> stuTopicCommentNumMap = topicCommentService.queryCommentNumByTopicIdsAndStuIds(id, stuIds);
        // 7.获取学生参与的测试数量
        Map<String, Long> stuTestNumMap = examDirectoryAnService.queryClassExamSurveyAnswerNumByStuIds(classesId, stuIds, subjectId);
        for (Map<String, Object> student : studentList) {
            String stuId = student.get("id").toString();
            String studentNumber = student.get("studentNumber").toString();
            // 互动课件
            student.put("coursewareNum", coursewareNum);
            student.put("stuCoursewareNum", stuCoursewareNumMap.getOrDefault(stuId, 0L));
            double courseRate = getRate(stuCoursewareNumMap.getOrDefault(stuId, 0L), coursewareNum);
            // 资料
            student.put("dataNum", dataNum);
            student.put("stuDataNum", stuDataNumMap.getOrDefault(stuId, 0L));
            double dataRate = getRate(stuDataNumMap.getOrDefault(stuId, 0L), dataNum);
            // 作业
            student.put("assignmentNum", assignmentNum);
            student.put("stuAssignmentNum", stuAssignmentNumMap.getOrDefault(stuId, 0L));
            double assignmentRate = getRate(stuAssignmentNumMap.getOrDefault(stuId, 0L), assignmentNum);
            // 测试
            student.put("testNum", testNum);
            student.put("stuTestNum", stuTestNumMap.getOrDefault(stuId, 0L));
            double testRate = getRate(stuTestNumMap.getOrDefault(stuId, 0L), testNum);
            // 考勤
            student.put("checkWorkNum", checkWorkNum);
            student.put("stuCheckWorkNum", stuCheckWorkNumMap.getOrDefault(stuId, 0L));
            double checkWorkRate = getRate(stuCheckWorkNumMap.getOrDefault(stuId, 0L), checkWorkNum);
            // 话题数
            student.put("topicNum", topicNum);
            // 表现奖励
            student.put("rewardNum", stuStarNumMap.getOrDefault(studentNumber, "0"));
            // 发弹幕数
            student.put("stuTopicCommentNum", stuTopicCommentNumMap.getOrDefault(stuId, 0L));
            // 整体完成率
            double rate = (courseRate + assignmentRate + testRate + checkWorkRate + dataRate) / 5;
            // 保留三位
            student.put("rate", String.format("%.2f", rate) + '%');
            bean.add(student);
        }
        outputObject.setBeans(bean);
        outputObject.settotal(bean.size());
    }

    private double getRate(Long num, Long totalNum) {
        if (totalNum == 0) {
            return 0.0;
        }
        return (double) num / totalNum * 100;
    }

    @Override
    public void deleteBySubjectId(String subjectId) {
        QueryWrapper<SubjectClasses> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClasses::getObjectId), subjectId);
        List<SubjectClasses> subjectClassesList = list(queryWrapper);
        if (CollectionUtil.isEmpty(subjectClassesList)) {
            return;
        }
        List<String> ids = subjectClassesList.stream().map(SubjectClasses::getId).collect(Collectors.toList());
        // 删除班级学生关联表
        subjectClassesStuService.deleteBySubClassLinkId(ids);
        // 删除班级科目关联表
        remove(queryWrapper);
    }

    @Override
    public SubjectClasses queryStuNumBySubjectId(String subjectId, String classId) {
        QueryWrapper<SubjectClasses> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClasses::getObjectId), subjectId)
            .eq(MybatisPlusUtil.toColumns(SubjectClasses::getClassesId), classId);
        SubjectClasses one = getOne(queryWrapper);
        if (ObjectUtil.isEmpty(one)) {
            return new SubjectClasses();
        }
        return one;
    }

    @Override
    public List<SubjectClasses> getSubjectClassesByObjectIdAndClassesIds(String subjectId, List<String> classIds) {
        if (CollectionUtil.isEmpty(classIds)) {
            return new ArrayList<>();
        }
        QueryWrapper<SubjectClasses> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClasses::getObjectId), subjectId)
            .in(MybatisPlusUtil.toColumns(SubjectClasses::getClassesId), classIds);
        List<SubjectClasses> subjectClassesList = list(queryWrapper);
        return subjectClassesList;
    }

    @Override
    public void queryOverallAnalysis(InputObject inputObject, OutputObject outputObject) {
        String subjectClassId = inputObject.getParams().get("id").toString();
        SubjectClasses subjectClasses = selectById(subjectClassId);
        String classesId = subjectClasses.getClassesId();
        String subjectId = subjectClasses.getObjectId();
        Map<String, Object> resultMap = new HashMap<>();
        // 作业发布次数
        Long assignmentNum = assignmentService.queryClassAssignmentNum(subjectClassId);
        resultMap.put("assignmentNum", assignmentNum);
        // 话题数
        Long topicNum = topicService.queryClassTopicNum(subjectClassId);
        resultMap.put("topicNum", topicNum);
        // 资料数
        Long datumNum = datumService.queryClassDataNum(subjectId);
        // 互动课件数
        Long coursewareNum = coursewareService.queryClassCoursewareNum(subjectId);
        resultMap.put("dataAndCoursewareNum", datumNum + coursewareNum);
        // 测试数量
        Long examNum = examService.queryClassExamSurveyDirectoryNum(classesId, subjectId);
        resultMap.put("examNum", examNum);
        // 课程人数
        Long joinNum = subjectClassesStuService.queryClassStuNum(subjectClassId);
        if (ObjectUtil.isEmpty(joinNum) || joinNum == (long) CommonNumConstants.NUM_ZERO) {
            resultMap.put("joinNum", CommonNumConstants.NUM_ZERO);
            resultMap.put("assignmentSubNum", CommonNumConstants.NUM_ZERO);
            resultMap.put("assignmentAvg", "0.00%");
            resultMap.put("assignmentSubRate", "0.00%");
            resultMap.put("topicCommentNum", CommonNumConstants.NUM_ZERO);
            resultMap.put("topicJoinNum", CommonNumConstants.NUM_ZERO);
            resultMap.put("joinRate", "0.00%");
            resultMap.put("checkWorkRate", "0.00%");
            resultMap.put("testJoinNum", CommonNumConstants.NUM_ZERO);
            resultMap.put("testAvgScore", CommonNumConstants.NUM_ZERO);
            resultMap.put("testSubRate", "0.00%");
            resultMap.put("rewordList", new ArrayList<>());
            outputObject.setBean(resultMap);
            return;
        }
        resultMap.put("joinNum", joinNum);
        // 作业提交数----作业人数
        Long assignmentSubNum = assignmentSubService.queryClassAssignmentJoinNum(subjectClassId);
        resultMap.put("assignmentSubNum", assignmentSubNum);
        // 作业平均分
        Double assignmentAvg = assignmentSubService.queryClassAssignmentAvg(subjectClassId);
        resultMap.put("assignmentAvg", String.format("%.2f", assignmentAvg));
        // 作业提交率
        Double assignmentSubRate = getRate(assignmentSubNum, joinNum * assignmentNum);
        resultMap.put("assignmentSubRate", String.format("%.2f", assignmentSubRate) + '%');
        // 话题的评论数--弹幕条数--话题回复数
        Long topicCommentNum = topicCommentService.queryClassTopicJoinPersonNum(subjectClassId, null);
        resultMap.put("topicCommentNum", topicCommentNum);
        // 话题参与人数
        Long topicJoinNum = topicCommentService.queryClassTopicJoinNum(subjectClassId);
        resultMap.put("topicJoinNum", topicJoinNum);
        // 成员参与率
        Double joinRate = getRate(topicJoinNum, (joinNum + 1) * topicNum);
        resultMap.put("joinRate", String.format("%.2f", joinRate) + '%');
        // 考勤数量
        Long checkWorkNum = checkworkService.queryCheckWorkNum(subjectClassId);
        // 考勤人次
        Long checkWorkPersonNum = checkworkService.queryCheckWorkPersonNum(subjectClassId);
        // 出勤率
        Double checkWorkRate = getRate(checkWorkPersonNum, joinNum * checkWorkNum);
        resultMap.put("checkWorkRate", String.format("%.2f", checkWorkRate) + '%');
        // 测试参与人数---累计参与人数
        Long testJoinNum = examDirectoryAnService.queryClassExamSurveyAnswerNum(classesId, null, subjectId);
        resultMap.put("testJoinNum", testJoinNum);
        // 测试平均分
        Double testAvgScore = examDirectoryAnService.queryClassExamSurveyAvgScore(classesId, null, subjectId);
        resultMap.put("testAvgScore", testAvgScore);
        // 测试提交率
        Double testSubRate = getRate(testJoinNum, joinNum * examNum);
        resultMap.put("testSubRate", String.format("%.2f", testSubRate) + '%');
        // 表现榜
        List<Map<String, Object>> rewordList = subjectClassesStuService.queryStuRewordList(subjectClassId);
        resultMap.put("rewordList", rewordList);
        outputObject.setBean(resultMap);
    }

    @Override
    public List<SubjectClasses> queryClassBySubClassLinkId(List<String> subClassLinkId) {
        QueryWrapper<SubjectClasses> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, subClassLinkId);
        List<SubjectClasses> subjectClassesList = list(queryWrapper);
        return subjectClassesList;
    }

    public SubjectClasses queryClassBySubClassLinkId(String subClassLinkId) {
        QueryWrapper<SubjectClasses> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, subClassLinkId);
        SubjectClasses subjectClasses = getOne(queryWrapper);
        return subjectClasses;
    }

    @Override
    public void queryOneStudentAnalysis(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString(); // 科目与班级的关系id
        String stuId = map.get("stuId").toString(); // 学生id
        SubjectClasses subjectClasses = selectById(id);
        String subjectId = subjectClasses.getObjectId();
        String classesId = subjectClasses.getClassesId();

        // 查询班级学生信息
        List<Map<String, Object>> studentList = subjectClassesStuService.queryClassStuIds(id);
        Map<String, Object> stuMap = Optional.ofNullable(studentList)
            .orElse(Collections.emptyList())
            .stream()
            .filter(m -> CollectionUtil.isNotEmpty(m) && ObjectUtil.isNotEmpty(m.get("id")) && m.get("id").equals(stuId))
            .findFirst()
            .orElse(null);
        if (ObjectUtil.isEmpty(stuMap)) {
            throw new CustomException("查询不到该学生信息");
        }
        String studentNumber = stuMap.get("studentNumber").toString();
        //1. 考勤情况
        Map<String, Object> checkworkMap = checkworkSignService.queryStuCheckworkSignByStuId(stuId, id);
        stuMap.put("checkWorkList", checkworkMap);
        //2. 表现星星
        String reword = subjectClassesStuService.queryRewordNumByStuNoAndSubjectClassId(id, studentNumber);
        stuMap.put("reword", reword);
        //3. 考勤次数
        Long checkWorkNum = checkworkService.queryCheckWorkNum(id);
        //4. 自己的考勤记录
        Long stuCheckWorkNum = Long.parseLong(checkworkMap.get(CheckworkSignState.SIGN.name()).toString());
        // 5. 考勤率
        double checkWorkRate = getRate(stuCheckWorkNum, checkWorkNum);
        stuMap.put("checkWorkRate", String.format("%.2f", checkWorkRate) + "%");
        // 6. 作业数
        Long assignmentNum = assignmentService.queryClassAssignmentNum(id);
        stuMap.put("assignmentNum", assignmentNum);
        // 7. 学生提交作业数
        Long stuAssignmentNum = assignmentSubService.queryStuAssignNumByStuId(id, stuId);
        stuMap.put("stuAssignmentNum", stuAssignmentNum);
        // 8. 作业完成率
        double assignmentRate = getRate(stuAssignmentNum, assignmentNum);
        stuMap.put("assignmentRate", String.format("%.2f", assignmentRate) + "%");
        // 9. 测试
        Long testNum = examService.queryClassExamSurveyDirectoryNum(classesId, subjectId);
        stuMap.put("testNum", testNum);
        // 10.学生完成的测试数
        Long stuTestNum = examDirectoryAnService.queryClassExamSurveyAnswerNum(classesId, stuId, subjectId);
        stuMap.put("stuTestNum", stuTestNum);
        // 11. 测试平均数
        Double testAvgScore = examDirectoryAnService.queryClassExamSurveyAvgScore(classesId, stuId, subjectId);
        stuMap.put("testAvgScore", String.format("%.2f", testAvgScore));
        // 测总分平均分
        Double testTotalAvgScore = examService.queryClassExamSurveyTotalAvgScore(classesId, subjectId);
        stuMap.put("testTotalScore", String.format("%.2f", testTotalAvgScore));
        // 12. 互动课件数
        Long coursewareNum = coursewareService.queryClassCoursewareNum(subjectId);
        stuMap.put("coursewareNum", coursewareNum);
        // 13. 学生观看课件数
        Long stuCoursewareNum = coursewareStudyService.queryStuStudyCoursewareNum(subjectId, stuId);
        stuMap.put("stuCoursewareNum", stuCoursewareNum);
        // 14. 学生发的弹幕数
        Long stuTopicCommentNum = topicCommentService.queryClassTopicJoinPersonNum(id, stuId);
        stuMap.put("stuTopicCommentNum", stuTopicCommentNum);
        // 15. 话题数
        Long topicNum = topicService.queryClassTopicNum(id);
        stuMap.put("topicNum", topicNum);
        outputObject.setBean(stuMap);
    }

    @Override
    public int queryNumBySubAndClassIds(String subjectId, List<String> classIds) {
        QueryWrapper<SubjectClasses> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClasses::getObjectId), subjectId)
            .in(MybatisPlusUtil.toColumns(SubjectClasses::getClassesId), classIds);
        List<SubjectClasses> list = list(queryWrapper);
        List<String> collect = list.stream().map(SubjectClasses::getId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(collect)) {
            throw new CustomException("查询不到班级信息");
        }
        List<SubjectClassesStu> subjectClassesStuList = subjectClassesStuService.queryListBySubClassLinkIds(collect);
        return subjectClassesStuList.size();
    }

    @Override
    public void queryClassBySemesterAndSubjectId(InputObject inputObject, OutputObject outputObject) {
        String semesterId = inputObject.getParams().get("semesterId").toString();
        String subjectId = inputObject.getParams().get("subjectId").toString();
        QueryWrapper<SubjectClasses> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClasses::getObjectId), subjectId)
            .eq(MybatisPlusUtil.toColumns(SubjectClasses::getSemesterId), semesterId);
        List<SubjectClasses> list = list(queryWrapper);
        List<String> classIdList = list.stream().map(SubjectClasses::getClassesId).collect(Collectors.toList());
        List<Classes> classesList = classesService.selectClssByIds(classIdList);
        outputObject.setBeans(classesList);
        outputObject.settotal(classesList.size());
    }
}