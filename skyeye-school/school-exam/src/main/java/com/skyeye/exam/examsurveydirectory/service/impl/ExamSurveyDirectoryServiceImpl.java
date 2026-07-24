package com.skyeye.exam.examsurveydirectory.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.constans.QuartzConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.common.util.question.QuType;
import com.skyeye.eve.entity.School;
import com.skyeye.eve.rest.quartz.SysQuartzMation;
import com.skyeye.eve.service.IQuartzService;
import com.skyeye.eve.service.SchoolService;
import com.skyeye.exam.examquchckbox.entity.ExamQuCheckbox;
import com.skyeye.exam.examquchckbox.service.ExamQuCheckboxService;
import com.skyeye.exam.examquchencolumn.entity.ExamQuChenColumn;
import com.skyeye.exam.examquchencolumn.service.ExamQuChenColumnService;
import com.skyeye.exam.examquchenrow.entity.ExamQuChenRow;
import com.skyeye.exam.examquchenrow.service.ExamQuChenRowService;
import com.skyeye.exam.examquestion.entity.Question;
import com.skyeye.exam.examquestion.service.QuestionService;
import com.skyeye.exam.examquestionlogic.entity.ExamQuestionLogic;
import com.skyeye.exam.examquestionlogic.service.ExamQuestionLogicService;
import com.skyeye.exam.examqumultfillblank.entity.ExamQuMultiFillblank;
import com.skyeye.exam.examqumultfillblank.service.ExamQuMultiFillblankService;
import com.skyeye.exam.examquorderby.entity.ExamQuOrderby;
import com.skyeye.exam.examquorderby.service.ExamQuOrderbyService;
import com.skyeye.exam.examquradio.entity.ExamQuRadio;
import com.skyeye.exam.examquradio.service.ExamQuRadioService;
import com.skyeye.exam.examquscore.entity.ExamQuScore;
import com.skyeye.exam.examquscore.service.ExamQuScoreService;
import com.skyeye.exam.examsurveyanswer.entity.ExamSurveyAnswer;
import com.skyeye.exam.examsurveyanswer.service.ExamSurveyAnswerService;
import com.skyeye.exam.examsurveyclass.service.ExamSurveyClassService;
import com.skyeye.exam.examsurveydirectory.dao.ExamSurveyDirectoryDao;
import com.skyeye.exam.examsurveydirectory.entity.ExamSurveyDirectory;
import com.skyeye.exam.examsurveydirectory.service.ExamSurveyDirectoryService;
import com.skyeye.exam.examsurveymarkexam.entity.ExamSurveyMarkExam;
import com.skyeye.exam.examsurveymarkexam.service.ExamSurveyMarkExamService;
import com.skyeye.exam.examsurveyquanswer.entity.ExamSurveyQuAnswer;
import com.skyeye.exam.examsurveyquanswer.service.ExamSurveyQuAnswerService;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.wall.user.service.IUserService;
import com.skyeye.school.common.entity.UserOrStudent;
import com.skyeye.school.common.service.SchoolCommonService;
import com.skyeye.school.exam.service.ExamService;
import com.skyeye.school.faculty.entity.Faculty;
import com.skyeye.school.faculty.service.FacultyService;
import com.skyeye.school.grade.entity.Classes;
import com.skyeye.school.grade.service.ClassesService;
import com.skyeye.school.major.entity.Major;
import com.skyeye.school.major.service.MajorService;
import com.skyeye.school.score.classenum.NumberCodeEnum;
import com.skyeye.school.score.entity.ScoreTypeChild;
import com.skyeye.school.score.service.ScoreTypeChildService;
import com.skyeye.school.semester.service.SemesterService;
import com.skyeye.school.subject.entity.Subject;
import com.skyeye.school.subject.entity.SubjectClasses;
import com.skyeye.school.subject.entity.SubjectClassesStu;
import com.skyeye.school.subject.service.SubjectClassesService;
import com.skyeye.school.subject.service.SubjectClassesStuService;
import com.skyeye.school.subject.service.SubjectService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: ExamSurveyDirectoryServiceImpl
 * @Description: 试卷管理服务层
 * @author: skyeye云系列--lyj
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "试卷管理", groupName = "试卷管理", allowDynamicAttrKey = false)
public class ExamSurveyDirectoryServiceImpl extends SkyeyeBusinessServiceImpl<ExamSurveyDirectoryDao, ExamSurveyDirectory> implements ExamSurveyDirectoryService, ExamService {

    @Autowired
    private ExamSurveyClassService examSurveyClassService;

    @Autowired
    private ExamSurveyMarkExamService examSurveyMarkExamService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ExamQuRadioService examQuRadioService;

    @Autowired
    private ExamQuScoreService examquScoreService;

    @Autowired
    private ExamQuCheckboxService examQuCheckboxService;

    @Autowired
    private ExamQuMultiFillblankService examQuMultiFillblankService;

    @Autowired
    private ExamQuOrderbyService examQuOrderbyService;

    @Autowired
    private ExamQuChenColumnService examQuChenColumnService;

    @Autowired
    private ExamQuChenRowService examQuChenRowService;

    @Autowired
    private ExamQuestionLogicService examQuestionLogicService;

    @Autowired
    private ExamSurveyAnswerService examSurveyAnswerService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private ClassesService classesService;

    @Autowired
    private SchoolService schoolService;

    @Autowired
    private SemesterService semesterService;

    @Autowired
    private FacultyService facultyService;

    @Autowired
    private MajorService majorService;

    @Autowired
    private SubjectClassesService subjectClassesService;

    @Autowired
    private SchoolCommonService schoolCommonService;

    @Autowired
    private SubjectClassesStuService subjectClassesStuService;

    @Autowired
    private IQuartzService iQuartzService;

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ScoreTypeChildService scoreTypeChildService;

    /**
     * 发布试卷
     *
     * @param inputObject  输入对象，包含请求参数
     * @param outputObject 输出对象，用于返回响应数据
     */
    @Override
    public void setUpExamDirectory(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        ExamSurveyDirectory examSurveyDirectory = selectById(id);
        if (ObjUtil.isEmpty(examSurveyDirectory)) {
            throw new CustomException("该试卷信息不存在。");
        }
        Integer surveyState = examSurveyDirectory.getSurveyState();
        if (surveyState == null || !surveyState.equals(CommonNumConstants.NUM_ZERO)) {
            throw new CustomException("该试卷已发布，请刷新数据。");
        }
        List<Question> questions = questionService.QueryQuestionByBelongId(id);
        if (CollectionUtil.isEmpty(questions)) {
            throw new CustomException("该试卷没有题目，请添加题目。");
        }
        Integer fraction = 0;
        int questionNum = 0;
        for (Question question : questions) {
            int questionType = question.getQuType();
            if (questionType != QuType.PAGETAG.getIndex() && questionType != QuType.PARAGRAPH.getIndex()) {
                fraction += question.getFraction();
                questionNum++;
            }
        }
        if (fraction == null || fraction == 0) {
            throw new CustomException("该试卷没有题目，请添加题目!");
        }
        UpdateWrapper<ExamSurveyDirectory> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getFraction), fraction);
        updateWrapper.set(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getSurveyQuNum), questionNum);
        updateWrapper.set(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getSurveyState), CommonNumConstants.NUM_ONE);
        update(updateWrapper);
        if (examSurveyDirectory.getEndType() != null) {
            if (examSurveyDirectory.getEndType().equals(CommonNumConstants.NUM_TWO)) {
                startUpTaskQuartz(examSurveyDirectory.getId(), examSurveyDirectory.getSurveyName(), examSurveyDirectory.getRealEndTime());
            }
        }
    }

    /**
     * 启动定时任务
     *
     * @param name    定时任务名称
     * @param title   定时任务标题
     * @param endTime 定时任务开始时间
     */
    private void startUpTaskQuartz(String name, String title, String endTime) {
        // 正式准备启动定时任务
        SysQuartzMation sysQuartzMation = new SysQuartzMation();
        sysQuartzMation.setName(name);
        sysQuartzMation.setTitle(title);
        sysQuartzMation.setDelayedTime(endTime);
        sysQuartzMation.setGroupId(QuartzConstants.QuartzMateMationJobType.CREATE_EXAM.getTaskType());
        iQuartzService.startUpTaskQuartz(sysQuartzMation);
    }

    @NotNull
    private Integer getFractionNumber(String belongId) {
        List<Question> questions = questionService.QueryQuestionByBelongId(belongId);
        int fraction = 0;
        if (CollectionUtil.isNotEmpty(questions)) {
            for (Question question : questions) {
                int questionType = question.getQuType();
                if (questionType != QuType.PAGETAG.getIndex() && questionType != QuType.PARAGRAPH.getIndex()) {
                    fraction += question.getFraction();
                }
            }
        }
        return fraction;
    }

    /**
     * 参加考试的方法
     *
     * @param inputObject  输入对象，包含请求参数
     * @param outputObject 输出对象，用于返回响应数据
     * @return 允许参加考试时返回考试目录信息
     */
    @Override
    public ExamSurveyDirectory takeExam(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        // 是否可以参加考试，true：可以；false：不可以
        boolean yesOrNo = false;
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        String id = map.get("id").toString();
        ExamSurveyDirectory examSurveyDirectory = selectById(id);
        if (StrUtil.isEmpty(examSurveyDirectory.getId())) {
            throw new CustomException("该试卷信息不存在。");
        }
        // 判断试卷是否存在
        if (ObjUtil.isEmpty(examSurveyDirectory)) {
            throw new CustomException("该试卷不存在");
        }
        // 判断试卷是否发布
        if (examSurveyDirectory.getSurveyState().equals(CommonNumConstants.NUM_ONE)) {
            ExamSurveyAnswer examSurveyAnswer = examSurveyAnswerService.queryWhetherExamIngByStuId(userId, id); // 查询用户是否已经参加过该考试
            if (ObjUtil.isNotEmpty(examSurveyAnswer)) {
                throw new CustomException("您已参加过该考试");
            } else {
                yesOrNo = true;
            }
        } else {
            throw new CustomException("该试卷未发布");
        }
        if (yesOrNo) {
            return examSurveyDirectory;
        } else {
            throw new CustomException("您不具备该考试权限");
        }
    }

    /**
     * 复制考试目录的方法
     *
     * @param inputObject  输入对象，包含请求参数
     * @param outputObject 输出对象，用于返回响应数据
     */
    @Override
    public void copyExamDirectory(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String examDirectoryId = map.get("id").toString();
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        String surveyName = map.get("surveyName").toString();
        ExamSurveyDirectory examSurveyDirectory = selectById(examDirectoryId);
        List<ExamSurveyMarkExam> examSurveyMarkExamList = examSurveyMarkExamService.getExamSurveyMarkExamList(examDirectoryId);
        String userIdJoin = Joiner.on(CommonCharConstants.COMMA_MARK).join(examSurveyMarkExamList.stream().map(ExamSurveyMarkExam::getUserId).collect(Collectors.toList()));
        examSurveyDirectory.setSurveyModel(CommonNumConstants.NUM_ONE);
        examSurveyDirectory.setSurveyState(CommonNumConstants.NUM_ZERO);
        examSurveyDirectory.setCreateId(userId);
        examSurveyDirectory.setCreateTime(DateUtil.getTimeAndToString());
        examSurveyDirectory.setReaderList(userIdJoin);
        examSurveyDirectory.setRealEndTime(null);
        examSurveyDirectory.setId(ToolUtil.randomStr(6, 12));
        examSurveyDirectory.setQuestionMation(new ArrayList<>());
        if (StrUtil.isEmpty(surveyName)) {
            examSurveyDirectory.setSurveyName(examSurveyDirectory.getSurveyName() + "_副本");
        } else {
            examSurveyDirectory.setSurveyName(surveyName);
        }
        createEntity(examSurveyDirectory, userId);
        List<Question> questionList = questionService.QueryQuestionByBelongId(examDirectoryId);
        if (CollectionUtil.isNotEmpty(questionList)) {
            List<String> questionIdList = questionList.stream().map(Question::getId).collect(Collectors.toList());
            Map<String, List<ExamQuestionLogic>> stringListMap = examQuestionLogicService.selectByQuestionIds(questionIdList);
            Map<String, List<ExamQuRadio>> stringListMap1 = examQuRadioService.selectByQuestionIds(questionIdList);
            Map<String, List<ExamQuScore>> stringListMap2 = examquScoreService.selectByQuestionIds(questionIdList);
            Map<String, List<ExamQuCheckbox>> stringListMap3 = examQuCheckboxService.selectByQuestionIds(questionIdList);
            Map<String, List<ExamQuMultiFillblank>> stringListMap4 = examQuMultiFillblankService.selectByQuestionIds(questionIdList);
            Map<String, List<ExamQuOrderby>> stringListMap5 = examQuOrderbyService.selectByQuestionIds(questionIdList);
            Map<String, List<ExamQuChenColumn>> stringListMap6 = examQuChenColumnService.selectByQuestionIds(questionIdList);
            Map<String, List<ExamQuChenRow>> stringListMap7 = examQuChenRowService.selectByQuestionIds(questionIdList);
            for (Question question : questionList) {
                String id = question.getId();
                question.setCopyFromId(id);
                List<ExamQuestionLogic> examQuestionLogics = stringListMap.get(id);
                question.setQuestionLogic(examQuestionLogics);
                List<ExamQuRadio> examQuRadioList = stringListMap1.get(id);
                question.setRadioTd(examQuRadioList);
                List<ExamQuScore> examQuScoreList = stringListMap2.get(id);
                question.setScoreTd(examQuScoreList);
                List<ExamQuCheckbox> examQuCheckboxList = stringListMap3.get(id);
                question.setCheckboxTd(examQuCheckboxList);
                question.setCheckboxTd(examQuCheckboxList);
                List<ExamQuMultiFillblank> multiFillblanks = stringListMap4.get(id);
                question.setMultifillblankTd(multiFillblanks);
                List<ExamQuOrderby> examQuOrderbyList = stringListMap5.get(id);
                question.setOrderByTd(examQuOrderbyList);
                List<ExamQuChenColumn> examQuChenColumnList = stringListMap6.get(id);
                question.setColumnTd(examQuChenColumnList);
                List<ExamQuChenRow> examQuChenRows = stringListMap7.get(id);
                question.setRowTd(examQuChenRows);
                question.setBelongId(examSurveyDirectory.getId());
            }
            questionService.createEntity(questionList, userId);
            examSurveyDirectory.setQuestionMation(questionList);
        }
        outputObject.setBean(examSurveyDirectory);
        outputObject.settotal(1);
    }

    @Override
    public void validatorEntity(ExamSurveyDirectory examSurveyDirectory) {
        super.validatorEntity(examSurveyDirectory);
        String realStartTime = examSurveyDirectory.getRealStartTime();
        String realEndTime = examSurveyDirectory.getRealEndTime();
        if (StrUtil.isNotEmpty(realStartTime) && StrUtil.isNotEmpty(realEndTime)) {
            boolean compare = DateUtil.compare(realStartTime, realEndTime);
            if (!compare) {
                throw new CustomException("开始时间不能大于结束时间");
            }
        }
    }

    @Override
    protected void createPrepose(ExamSurveyDirectory entity) {
        //班级Id
        String classId = entity.getClassId();
        //科目Id
        String subjectId = entity.getSubjectId();
        List<String> classIds = Arrays.asList(classId.split(","));
        //总人数
        int num = subjectClassesService.queryNumBySubAndClassIds(subjectId, classIds);
        entity.setAllNumber(num);
        entity.setIsMarkState(CommonNumConstants.NUM_ZERO);
        entity.setReadNum(CommonNumConstants.NUM_ZERO);
    }

    @Override
    public void createPostpose(ExamSurveyDirectory entity, String userId) {
        String id = entity.getId();
        String reader = entity.getReaderList();
        List<String> readerIds = Arrays.asList(reader.split(CommonCharConstants.COMMA_MARK));
        String classId = entity.getClassId();
        List<String> classIds = Arrays.asList(classId.split(CommonCharConstants.COMMA_MARK));
        examSurveyClassService.createExamSurveyClass(id, classIds, userId);
        examSurveyMarkExamService.createExamSurveyMarkExam(id, readerIds, userId);
        List<Question> questionList = entity.getQuestionMation();
        if (CollectionUtil.isNotEmpty(questionList)) {
            for (Question question : questionList) {
                question.setBelongId(id);
            }
            questionService.createEntity(questionList, userId);
        }

        // 新增试卷时，创建空白成绩记录
        List<SubjectClasses> subjectClassesList = subjectClassesService.getSubjectClassesByObjectIdAndClassesIds(entity.getSubjectId(), classIds);
        List<String> subjectClassesIds = subjectClassesList.stream().map(SubjectClasses::getId).collect(Collectors.toList());
        List<ScoreTypeChild> scoreTypeChildren = scoreTypeChildService.selectIds(entity.getSubjectId(), subjectClassesIds, NumberCodeEnum.TEST.getKey());
        if (CollectionUtil.isEmpty(scoreTypeChildren)) {
            throw new CustomException("没有科目对应的班级");
        }
        Map<String, List<ScoreTypeChild>> collect = scoreTypeChildren.stream().collect(Collectors.groupingBy(ScoreTypeChild::getSubClassLinkId));
        List<ScoreTypeChild> list = new ArrayList<>();
        for (String subjectClassesId : subjectClassesIds) {
            ScoreTypeChild scoreTypeChild = new ScoreTypeChild();
            scoreTypeChild.setSubjectId(entity.getSubjectId());
            scoreTypeChild.setSubClassLinkId(subjectClassesId);
            if (CollectionUtil.isEmpty(collect.get(subjectClassesId)) || ObjectUtil.isEmpty(collect.get(subjectClassesId).get(CommonNumConstants.NUM_ZERO))) {
                throw new CustomException("没有科目对应的班级");
            }
            scoreTypeChild.setParentId(collect.get(subjectClassesId).get(CommonNumConstants.NUM_ZERO).getId());
            scoreTypeChild.setName(entity.getSurveyName());
            scoreTypeChild.setNameLinkId(entity.getId());
            scoreTypeChild.setNameLinkKey(getServiceClassName());
            scoreTypeChild.setProportion(CommonNumConstants.NUM_ZERO.toString());
            list.add(scoreTypeChild);
        }
        scoreTypeChildService.createEntity(list, userId);
    }

    @Override
    public void updatePostpose(ExamSurveyDirectory entity, String userId) {
        String surveId = entity.getId();
        QueryWrapper<ExamSurveyMarkExam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyMarkExam::getSurveyId), surveId);
        examSurveyMarkExamService.remove(queryWrapper);
        String reader = entity.getReaderList();
        if (StrUtil.isNotEmpty(reader)) {
            List<String> readerIds = Arrays.asList(reader.split(","));
            examSurveyMarkExamService.createExamSurveyMarkExam(surveId, readerIds, userId);
        }
        List<Question> questionList = entity.getQuestionMation();
        List<Question> existingQuestions = questionService.QueryQuestionByBelongId(surveId);
        List<String> existingIds = existingQuestions.stream().map(Question::getId)
            .collect(Collectors.toList());
        Map<Boolean, List<Question>> partitionedQuestions = questionList.stream()
            .collect(Collectors.partitioningBy(question -> StrUtil.isNotEmpty(question.getId())));
        List<Question> questionsWithId = partitionedQuestions.get(true);
        List<Question> questionsWithoutId = partitionedQuestions.get(false);
        List<String> submittedIds = questionsWithId.stream().map(Question::getId)
            .collect(Collectors.toList());
        Set<String> submittedIdSet = new HashSet<>(submittedIds);
        List<String> idsToDelete = existingIds.stream()
            .filter(id -> !submittedIdSet.contains(id))
            .collect(Collectors.toList());
        questionService.deleteById(idsToDelete);
        if (CollectionUtil.isNotEmpty(questionsWithId)) {
            List<Question> createQuestion = new ArrayList<>();
            //纯题目
            List<Question> collect = questionsWithId.stream()
                .filter(question -> StrUtil.isNotEmpty(question.getId()) &&
                    StrUtil.isEmpty(question.getBelongId())).collect(Collectors.toList());
            createQuestion.addAll(collect);
            for (Question question : createQuestion) {
                question.setBelongId(surveId);
            }
            for (Question question : collect) {
                question.setId(StrUtil.EMPTY);
            }
            questionService.updateEntity(createQuestion, userId);
            questionService.createEntity(collect, userId);
            questionService.updateEntity(questionsWithId, userId);
        }
        questionService.createEntity(questionsWithoutId, userId);

        // 修改试卷时，修改成绩子类型名称
        List<String> classIds = Arrays.asList(entity.getClassId().split(CommonCharConstants.COMMA_MARK));
        List<SubjectClasses> subjectClassesList = subjectClassesService.getSubjectClassesByObjectIdAndClassesIds(entity.getSubjectId(), classIds);
        List<String> subjectClassesIds = subjectClassesList.stream().map(SubjectClasses::getId).collect(Collectors.toList());
        scoreTypeChildService.editNames(entity.getSubjectId(), subjectClassesIds, entity.getId(), entity.getSurveyName());
    }

    /**
     * 切换是否删除考试目录的方法
     *
     * @param inputObject  输入对象，包含请求参数
     * @param outputObject 输出对象，用于返回响应数据
     */
    @Override
    public void changeWhetherDeleteById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        UpdateWrapper<ExamSurveyDirectory> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getWhetherDelete), CommonNumConstants.NUM_TWO);
        updateWrapper.set(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getSurveyState), CommonNumConstants.NUM_TWO);
        update(updateWrapper);
    }

    @Override
    protected void deletePostpose(ExamSurveyDirectory entity) {
        String id = entity.getId();
        // 删除试卷下的题目
        questionService.deleteBySurveyDirectoryId(id);

        examSurveyClassService.deleteSurveyClassBySurveyId(id);
        examSurveyMarkExamService.deleteSurveyMarkExamBySurveyId(id);

        // 删除成绩子类型
        List<String> classIds = Arrays.asList(entity.getClassId().split(CommonCharConstants.COMMA_MARK));
        List<SubjectClasses> subjectClassesList = subjectClassesService.getSubjectClassesByObjectIdAndClassesIds(entity.getSubjectId(), classIds);
        List<String> subjectClassesIds = subjectClassesList.stream().map(SubjectClasses::getId).collect(Collectors.toList());
        scoreTypeChildService.deletes(entity.getSubjectId(), subjectClassesIds, entity.getId());
    }

    /**
     * 更新考试状态结束信息的方法
     *
     * @param inputObject  输入对象，包含请求参数
     * @param outputObject 输出对象，用于返回响应数据
     */
    @Override
    public void updateExamMationEndById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String examSurveyDirectoryId = map.get("id").toString();
        QueryWrapper<ExamSurveyDirectory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, examSurveyDirectoryId);
        ExamSurveyDirectory examSurveyDirectory = getOne(queryWrapper);
        // 判断考试目录对象是否存在
        if (ObjUtil.isNotEmpty(examSurveyDirectory)) {
            // 判断考试目录状态是否为进行中（NUM_ONE）
            if (examSurveyDirectory.getSurveyState().equals(CommonNumConstants.NUM_ONE)) {
                // 获取当前时间作为实际结束时间
                String realEndTime = DateUtil.getTimeAndToString();
                UpdateWrapper<ExamSurveyDirectory> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq(CommonConstants.ID, examSurveyDirectoryId);
                // 设置实际结束时间为当前时间
                updateWrapper.set(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getRealEndTime), realEndTime);
                // 设置考试目录状态为已结束（NUM_TWO）
                updateWrapper.set(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getSurveyState), CommonNumConstants.NUM_TWO);
                // 设置结束类型为自动结束（NUM_ONE）
                updateWrapper.set(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getEndType), CommonNumConstants.NUM_ONE);
                // 执行更新操作
                update(updateWrapper);
                // 创建没有提交考试的答案的学生，创建0分答案
                createNotSubStudent(examSurveyDirectoryId, inputObject.getLogParams().get("id").toString());
            }
        } else {
            throw new CustomException("该试卷信息不存在!");
        }
    }

    @Override
    public void queryMyExamList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String userId = inputObject.getLogParams().get("id").toString();
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<ExamSurveyDirectory> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getCreateId), userId);
        extracted(commonPageInfo, queryWrapper);
        outputResult(outputObject, page, queryWrapper);
    }

    @Override
    public void queryFilterExamLists(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<ExamSurveyDirectory> queryWrapper = super.getQueryWrapper(commonPageInfo);
        extracted(commonPageInfo, queryWrapper);
        outputResult(outputObject, page, queryWrapper);
    }

    private static void extracted(CommonPageInfo commonPageInfo, QueryWrapper<ExamSurveyDirectory> queryWrapper) {
        // 学校
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderKey())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getSchoolId), commonPageInfo.getHolderKey());
        }
        // 院校
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getFacultyId), commonPageInfo.getHolderId());
        }
        // 专业
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectKey())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getMajorId), commonPageInfo.getObjectKey());
        }
        // 科目
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getSubjectId), commonPageInfo.getObjectId());
        }
        // 试卷名称
        if (StrUtil.isNotEmpty(commonPageInfo.getKeyword())) {
            queryWrapper.like(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getSurveyName), commonPageInfo.getKeyword());
        }
        // 状态
        if (StrUtil.isNotEmpty(commonPageInfo.getState())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getSurveyState), commonPageInfo.getState());
        }
    }

    @Override
    public void querySurveyListBySubjectLinkId(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<ExamSurveyDirectory> queryWrapper = new QueryWrapper<>();
        // 班级
        String holderId = commonPageInfo.getHolderId();
        // 科目
        String objectId = commonPageInfo.getObjectId();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getSubjectId), objectId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getSurveyState), CommonNumConstants.NUM_ONE);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getClassId), holderId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getCreateTime));
        List<ExamSurveyDirectory> examSurveyDirectoryList = list(queryWrapper);
        // 总人数
        SubjectClasses subjectClasses = subjectClassesService.queryStuNumBySubjectId(objectId, holderId);
        Integer stuNum = subjectClasses.getPeopleNum();
        if (CollectionUtil.isEmpty(examSurveyDirectoryList)) {
            return;
        }
        List<String> directoryIds = examSurveyDirectoryList.stream().map(ExamSurveyDirectory::getId).collect(Collectors.toList());
        // 获取已回答的人数
        Map<String, Integer> answerNumMap = examSurveyAnswerService.queryAnswerNum(directoryIds, subjectClasses.getCreateId(), holderId, objectId, CommonNumConstants.NUM_ONE);
        // 获取已批阅的人数
        Map<String, Integer> alreadyAnswerNum = examSurveyAnswerService.queryAlreadyAnswerNum(directoryIds);
        List<String> surveyList = examSurveyDirectoryList.stream().map(ExamSurveyDirectory::getId).collect(Collectors.toList());
        Map<String, List<Question>> queryQuestionListBySurveyIdList = questionService.queryQuestionListBySurveyIdList(surveyList);
        for (ExamSurveyDirectory examSurveyDirectory : examSurveyDirectoryList) {
            examSurveyDirectory.setQuestionMation(queryQuestionListBySurveyIdList.getOrDefault(examSurveyDirectory.getId(), Collections.emptyList()));
            // 获取已批阅
            int readNum = alreadyAnswerNum.get(examSurveyDirectory.getId()) == null ? CommonNumConstants.NUM_ZERO : alreadyAnswerNum.get(examSurveyDirectory.getId());
            examSurveyDirectory.setReadNum(readNum);
            int answerNum = answerNumMap.get(examSurveyDirectory.getId()) == null ? CommonNumConstants.NUM_ZERO : answerNumMap.get(examSurveyDirectory.getId());
            // 获取未交的人数
            int unSubmitNum = stuNum - answerNum;
            unSubmitNum = unSubmitNum == -1 ? CommonNumConstants.NUM_ONE : unSubmitNum;
            examSurveyDirectory.setUnSubmitNum(unSubmitNum);
            // 未批阅
            int unReadNum = answerNum - readNum;
            examSurveyDirectory.setUnreadNum(unReadNum);
        }
        outputObject.settotal(page.getTotal());
        outputObject.setBeans(examSurveyDirectoryList);
    }

    @Override
    public Map<String, List<ExamSurveyDirectory>> querySurveyListByIds(List<String> surveyIds, String createId) {
        QueryWrapper<ExamSurveyDirectory> queryWrapper = new QueryWrapper<>();
        Map<String, List<ExamSurveyDirectory>> listMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(surveyIds)) {
            queryWrapper.in(CommonConstants.ID, surveyIds);
            List<ExamSurveyDirectory> examSurveyDirectoryList = list(queryWrapper);
            //试卷Id和对应的创建者题目
            Map<String, List<Question>> stringListMap = questionService.queryQuestionListBySurveyIds(surveyIds, createId);
            for (ExamSurveyDirectory examSurveyDirectory : examSurveyDirectoryList) {
                examSurveyDirectory.setQuestionMation(stringListMap.get(examSurveyDirectory.getId()));
            }
            listMap = examSurveyDirectoryList.stream().collect(Collectors.groupingBy(ExamSurveyDirectory::getId));
        }
        return listMap;
    }

    @Override
    public ExamSurveyDirectory selectBySurAndStuId(String surveyId, String studentId) {
        ExamSurveyDirectory bean = super.selectById(surveyId);
        List<Question> questionList = questionService.QueryQuestionByBelongIdAndStuId(surveyId, studentId);
        bean.setQuestionMation(questionList);
        return bean;
    }

    @Autowired
    private ExamSurveyQuAnswerService examSurveyQuAnswerService;

    @Override
    public ExamSurveyDirectory selectBySurAndStuIds(String surveyId, String studentId, String id) {
        ExamSurveyDirectory bean = super.selectById(surveyId);
        List<Question> questionList = questionService.QueryQuestionByBelongIdAndStuId(surveyId, studentId);
        Map<String, List<Question>> collect = questionList.stream()
            .collect(Collectors.groupingBy(Question::getId));
        Map<String, List<ExamSurveyQuAnswer>> stringFloatMap = examSurveyQuAnswerService.selectFacByIdAndSurveyId(id, surveyId);
        // 遍历分组后的 Map
        for (Map.Entry<String, List<Question>> entry : collect.entrySet()) {
            String key = entry.getKey();
            List<Question> questions = entry.getValue();
            List<ExamSurveyQuAnswer> examSurveyQuAnswers = stringFloatMap.get(key);
            if (CollectionUtil.isNotEmpty(examSurveyQuAnswers)) {
                for (Question question : questions) {
                    question.setExamSurveyQuAnswerFraction(examSurveyQuAnswers.get(CommonNumConstants.NUM_ZERO));
                }
            }
        }
        bean.setQuestionMation(questionList);
        return bean;
    }

    @Override
    public Map<String, ExamSurveyDirectory> selectMapBysurveyIds(List<String> surveyIds) {
        if (CollectionUtil.isEmpty(surveyIds)) {
            return new HashMap<>();
        }
        QueryWrapper<ExamSurveyDirectory> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, surveyIds)
            .and(wrapper -> wrapper
                .eq(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getSurveyState), CommonNumConstants.NUM_ONE)
                .or()
                .eq(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getSurveyState), CommonNumConstants.NUM_TWO)
            );
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getCreateTime));
        List<ExamSurveyDirectory> examSurveyDirectoryList = list(queryWrapper);
        if (CollectionUtil.isEmpty(examSurveyDirectoryList)) {
            return new HashMap<>();
        }
        List<String> subjecyIds = examSurveyDirectoryList.stream().map(ExamSurveyDirectory::getSubjectId).collect(Collectors.toList());
        List<String> classIds = examSurveyDirectoryList.stream().map(ExamSurveyDirectory::getClassId).collect(Collectors.toList());
        List<String> schoolIds = examSurveyDirectoryList.stream().map(ExamSurveyDirectory::getSchoolId).collect(Collectors.toList());
        List<String> facultyIds = examSurveyDirectoryList.stream().map(ExamSurveyDirectory::getFacultyId).collect(Collectors.toList());
        List<String> majorIds = examSurveyDirectoryList.stream().map(ExamSurveyDirectory::getMajorId).collect(Collectors.toList());

        Map<String, List<Subject>> subjectMap = subjecyIds.isEmpty() ? new HashMap<>() : subjectService.selectByIdList(subjecyIds);
        Map<String, List<Classes>> classMap = classIds.isEmpty() ? new HashMap<>() : classesService.selectByIdList(classIds);
        Map<String, List<School>> schoolMapList = schoolIds.isEmpty() ? new HashMap<>() : schoolService.selectByIdList(schoolIds);
        Map<String, List<Faculty>> facultyMapList = facultyIds.isEmpty() ? new HashMap<>() : facultyService.selectByIdList(facultyIds);
        Map<String, List<Major>> majorMapList = majorIds.isEmpty() ? new HashMap<>() : majorService.selectByIdList(majorIds);

        for (ExamSurveyDirectory examSurveyDirectory : examSurveyDirectoryList) {
            List<Subject> subjects = subjectMap.getOrDefault(examSurveyDirectory.getSubjectId(), Collections.emptyList());
            examSurveyDirectory.setSubjectListMation(subjects.isEmpty() ? null : subjects);
            List<Classes> classes = classMap.getOrDefault(examSurveyDirectory.getClassId(), Collections.emptyList());
            examSurveyDirectory.setClassesMation(classes.isEmpty() ? null : classes);
            List<School> schools = schoolMapList.getOrDefault(examSurveyDirectory.getSchoolId(), Collections.emptyList());
            examSurveyDirectory.setSchoolMation(schools.isEmpty() ? null : schools.get(CommonNumConstants.NUM_ZERO));
            List<Faculty> faculties = facultyMapList.getOrDefault(examSurveyDirectory.getFacultyId(), Collections.emptyList());
            examSurveyDirectory.setFacultyMation(faculties.isEmpty() ? null : faculties.get(CommonNumConstants.NUM_ZERO));
            List<Major> majors = majorMapList.getOrDefault(examSurveyDirectory.getMajorId(), Collections.emptyList());
            examSurveyDirectory.setMajorMation(majors.isEmpty() ? null : majors.get(CommonNumConstants.NUM_ZERO));
        }
        return examSurveyDirectoryList.stream().collect(Collectors.toMap(ExamSurveyDirectory::getId, examSurveyDirectory -> examSurveyDirectory));
    }

    @Override
    public Long queryClassExamSurveyDirectoryNum(String classId, String subjectId) {
        QueryWrapper<ExamSurveyDirectory> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getClassId), classId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getSubjectId), subjectId);
        queryWrapper.ne(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getSurveyState), CommonNumConstants.NUM_ZERO);
        return count(queryWrapper);
    }

    @Override
    public List<Map<String, Object>> queryListBySubjectIdAndState(String subjectId, Integer state) {
        if (StrUtil.isEmpty(subjectId)) {
            return Collections.emptyList();
        }
        QueryWrapper<ExamSurveyDirectory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getSubjectId), subjectId);
        if (ObjectUtil.isNotEmpty(state)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getSurveyState), state);
        }
        return listMaps(queryWrapper);
    }

    @Override
    public Double queryClassExamSurveyTotalAvgScore(String classesId, String subjectId) {
        QueryWrapper<ExamSurveyDirectory> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getClassId), classesId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getSubjectId), subjectId);
        queryWrapper.ne(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getSurveyState), CommonNumConstants.NUM_ZERO);
        List<ExamSurveyDirectory> list = list(queryWrapper);
        if (CollectionUtil.isEmpty(list)) {
            return 0.0;
        }
        // 求所有试卷的总分的平均分
        double totalScore = list.stream().mapToDouble(ExamSurveyDirectory::getFraction).sum();
        return totalScore / list.size();
    }

    @Override
    public List<String> queryDirectoryIdsByClassId(String classId, String subjectId) {
        QueryWrapper<ExamSurveyDirectory> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getClassId), classId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getSubjectId), subjectId);
        return list(queryWrapper).stream().map(ExamSurveyDirectory::getId).collect(Collectors.toList());
    }

    @Override
    public void queryMyDoSurvey(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        // 学生老师的userId
        String userId = inputObject.getLogParams().get("id").toString();
        //班级Id
        String holderId = commonPageInfo.getHolderId();
        //科目Id
        String objectId = commonPageInfo.getObjectId();
        UserOrStudent userOrStudent = schoolCommonService.queryUserOrStudent(userId);
        if (userOrStudent.getUserOrStudent().equals(true)) {
            // 学生
            Map<String, Object> dataMation = userOrStudent.getDataMation();
            String studentNumber = dataMation.get("studentNumber").toString();
            if (StrUtil.isEmpty(studentNumber)) {
                return;
            }
            List<SubjectClassesStu> subjectClassesStuList = subjectClassesStuService.querySubClassLinkIdByStuNumberNo(studentNumber);
            if (CollectionUtil.isEmpty(subjectClassesStuList)) {
                return;
            }
            List<String> subClassLinkIds = subjectClassesStuList.stream().map(SubjectClassesStu::getSubClassLinkId).collect(Collectors.toList());
            List<SubjectClasses> subjectClassesList = subjectClassesService.queryClassBySubClassLinkId(subClassLinkIds);
            //学生所在班级对应的科目id
            List<String> objectIds = subjectClassesList.stream().map(SubjectClasses::getObjectId).collect(Collectors.toList());
            //科目对应的所有试卷
            Map<String, List<ExamSurveyDirectory>> objectIdMapList = queryDirectoryIdsByClassIds(objectIds, holderId);
            if (CollectionUtils.isEmpty(objectIdMapList)) {
                return;
            }
            // 学生回答并上交的试卷
            List<ExamSurveyAnswer> examSurveyAnswerList = examSurveyAnswerService.selectSurveyIdByUserId(userId);
            List<String> yesDoSurveyList = examSurveyAnswerList.stream().map(ExamSurveyAnswer::getSurveyId).collect(Collectors.toList());
            // 过滤掉学生做过的试卷
            objectIdMapList.replaceAll((subjectId, directories) ->
                directories.stream()
                    .filter(dir -> !yesDoSurveyList.contains(dir.getId()))
                    .collect(Collectors.toList())
            );
            // 获取目标科目下的试卷列表
            List<ExamSurveyDirectory> filteredList = Optional.ofNullable(objectIdMapList.get(objectId))
                .orElseGet(Collections::emptyList);
            int pageSize = Math.max(1, commonPageInfo.getLimit());
            int pageNum = Math.max(1, commonPageInfo.getPage());
            // 计算分页范围
            int total = filteredList.size();
            int fromIndex = (pageNum - 1) * pageSize;
            // 分页结果处理
            List<ExamSurveyDirectory> pagedList;
            if (fromIndex >= total) {
                pagedList = Collections.emptyList();
            } else {
                int toIndex = Math.min(fromIndex + pageSize, total);
                pagedList = filteredList.subList(fromIndex, toIndex);
            }

            outputObject.settotal(filteredList.size());
            outputObject.setBeans(pagedList);
        } else {
            String[] holderIdArray = holderId.split(",");
            QueryWrapper<ExamSurveyDirectory> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getSubjectId), objectId);
            queryWrapper.and(wrapper -> {
                for (String holderId1 : holderIdArray) {
                    wrapper.or(qw -> qw.apply("FIND_IN_SET({0}, class_id)", holderId1));
                }
            });
            queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getSurveyState), CommonNumConstants.NUM_ONE);
            queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getCreateTime));
            //这个科目班级的试卷
            List<ExamSurveyDirectory> allResults = list(queryWrapper);
            //老师回答过的答卷
            List<ExamSurveyAnswer> examSurveyAnswerList = examSurveyAnswerService.selectSurveyIdByteacherId(userId);
            if (CollectionUtil.isNotEmpty(examSurveyAnswerList)) {
                Set<String> yesDoSurveyIds = examSurveyAnswerList.stream().map(ExamSurveyAnswer::getSurveyId)
                    .collect(Collectors.toSet());
                allResults.forEach(
                    survey -> {
                        if (yesDoSurveyIds.contains(survey.getId())) {
                            survey.setIsAnswered(true);
                        } else {
                            survey.setIsAnswered(false);
                        }
                    }
                );
            }
            SubjectClasses idAndClassesId = subjectClassesService.getSubjectClassesByObjectIdAndClassesId(objectId, holderId);
            //老师Id
            String createId = idAndClassesId.getCreateId();
            int size;
            if (idAndClassesId != null) {
                List<SubjectClassesStu> subjectClassesStuList = Optional.ofNullable(
                        subjectClassesStuService.selectNumBySubClassLinkId(idAndClassesId.getId()))
                    .orElseGet(Collections::emptyList);
                size = subjectClassesStuList.size();
            } else {
                size = 0;
            }
            //试卷所有Id
            List<String> collect = allResults.stream()
                .map(ExamSurveyDirectory::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
            //已交未批试卷
            Map<String, Integer> stringIntegerMap = CollectionUtil.isNotEmpty(collect)
                ? examSurveyAnswerService.queryAnswerNum(collect, createId, holderId, objectId, CommonNumConstants.NUM_ONE)
                : Collections.emptyMap();
            // 已交已批试卷
            Map<String, Integer> stringIntegerMap1 = CollectionUtil.isNotEmpty(collect)
                ? examSurveyAnswerService.queryAnswerNum(collect, createId, holderId, objectId, CommonNumConstants.NUM_TWO)
                : Collections.emptyMap();
            Map<String, Integer> finalStringIntegerMap = stringIntegerMap;
            Map<String, Integer> finalStringIntegerMap1 = stringIntegerMap1;
            allResults.forEach(survey -> {
                String surveyId = survey.getId();
                //已交未批人数
                Integer num = finalStringIntegerMap.getOrDefault(surveyId, 0);
                //已交已批人数
                Integer num1 = finalStringIntegerMap1.getOrDefault(surveyId, 0);
                int unSubmitNum = Math.max(0, size - num - num1);
                survey.setUnSubmitNum(unSubmitNum);
                // 已批阅数量
                survey.setReadNum(Math.max(0, num1));
                // 未批阅数量
                survey.setUnreadNum(Math.max(0, num));
            });
            int pageSize = Math.max(1, commonPageInfo.getLimit());
            int pageNum = Math.max(1, commonPageInfo.getPage());
            long offset = (long) (pageNum - 1) * pageSize;
            // 分页处理
            List<ExamSurveyDirectory> pagedList = allResults.stream()
                .skip(offset)
                .limit(pageSize)
                .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(pagedList)) {
                outputObject.settotal(CommonNumConstants.NUM_ZERO);
                outputObject.setBeans(Collections.emptyList());
            } else {
                outputObject.settotal(allResults.size());
                outputObject.setBeans(pagedList);
            }
        }
    }

    @Override
    public List<ExamSurveyDirectory> queryCreatedSurveyListByUserId(String userId) {
        QueryWrapper<ExamSurveyDirectory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getCreateId), userId);
        list(queryWrapper).forEach(examSurveyDirectory -> examSurveyDirectory.setIsCreated(true));
        return list(queryWrapper);
    }

    private Map<String, List<ExamSurveyDirectory>> queryDirectoryIdsByClassIds(List<String> objectIds, String holderId) {
        if (CollectionUtil.isEmpty(objectIds)) {
            return new HashMap<>();
        }
        QueryWrapper<ExamSurveyDirectory> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getSubjectId), objectIds);
        queryWrapper.like(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getClassId), holderId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getSurveyState), CommonNumConstants.NUM_ONE);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getCreateTime));
        List<ExamSurveyDirectory> examSurveyDirectoryList = list(queryWrapper);
        return examSurveyDirectoryList.stream().collect(Collectors.groupingBy(ExamSurveyDirectory::getSubjectId));
    }

    private void outputResult(OutputObject outputObject, Page page, QueryWrapper<ExamSurveyDirectory> queryWrapper) {
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getWhetherDelete), CommonNumConstants.NUM_ONE);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getCreateTime));
        List<ExamSurveyDirectory> beans = list(queryWrapper);

        // 设置科目信息
        subjectService.setDataMation(beans, ExamSurveyDirectory::getSubjectId);
        // 设置学校信息
        schoolService.setDataMation(beans, ExamSurveyDirectory::getSchoolId);
        // 设置学院信息
        facultyService.setDataMation(beans, ExamSurveyDirectory::getFacultyId);
        // 设置专业信息
        majorService.setDataMation(beans, ExamSurveyDirectory::getMajorId);
        // 设置学期信息
        semesterService.setDataMation(beans, ExamSurveyDirectory::getSemesterId);

        iAuthUserService.setName(beans, "createId", "createName");
        iAuthUserService.setName(beans, "lastUpdateId", "lastUpdateName");
        outputObject.setBeans(beans);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public ExamSurveyDirectory selectById(String id) {
        ExamSurveyDirectory bean = getExamSurveyDirectory(id);
        Integer fractionNumber = getFractionNumber(id);
        List<Question> questionList = questionService.QueryQuestionByBelongId(bean.getId());
        if (CollectionUtil.isEmpty(questionList)) {
            return bean;
        }
        bean.setQuestionMation(questionList);
        bean.setFraction(fractionNumber);
        return bean;
    }

    @Override
    public void selectById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        ExamSurveyDirectory bean = getExamSurveyDirectory(id);
        Integer fractionNumber = getFractionNumber(id);
        bean.setFraction(fractionNumber);
        List<Question> questionList = questionService.QueryQuestionByBelongId(bean.getId());
        if (CollectionUtil.isEmpty(questionList)) {
            outputObject.setBean(bean);
        }
        outputObject.setBean(bean);
        outputObject.setBeans(questionList);
    }

    @NotNull
    private ExamSurveyDirectory getExamSurveyDirectory(String id) {
        ExamSurveyDirectory bean = super.selectById(id);
        bean.setSubjectMation(subjectService.selectById(bean.getSubjectId()));
        bean.setSchoolMation(schoolService.selectById(bean.getSchoolId()));
        String classId = bean.getClassId();
        if (StrUtil.isNotEmpty(classId)) {
            String[] split = classId.split(",");
            List<String> stringList = Arrays.asList(split);
            List<Classes> classesList = classesService.selectClssByIds(stringList);
            bean.setClassesMation(classesList);
        }
        bean.setFacultyMation(facultyService.selectById(bean.getFacultyId()));
        bean.setMajorMation(majorService.selectById(bean.getMajorId()));
        bean.setSemesterMation(semesterService.selectById(bean.getSemesterId()));
        List<ExamSurveyMarkExam> examSurveyMarkExamList = examSurveyMarkExamService.selectBySurveyId(bean.getId());
        if (CollectionUtil.isNotEmpty(examSurveyMarkExamList)) {
            List<String> markIds = examSurveyMarkExamList.stream().map(ExamSurveyMarkExam::getUserId).collect(Collectors.toList());
            String markIdsString = String.join(",", markIds);
            List<Map<String, Object>> userMationList = iAuthUserService.queryDataMationByIds(markIdsString);
            bean.setReaderMationList(userMationList);
        }
        return bean;
    }

    @Override
    public void queryFilterNoSurveys(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String state = commonPageInfo.getState();
        Integer stateInt = Integer.valueOf(state);
        String userId = inputObject.getLogParams().get("id").toString();
        List<ExamSurveyMarkExam> examSurveyMarkExams = examSurveyMarkExamService.selectByUserId(userId);
        List<String> surveyIds = examSurveyMarkExams.stream().map(ExamSurveyMarkExam::getSurveyId).collect(Collectors.toList());
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        Map<String, ExamSurveyDirectory> stringExamSurveyDirectoryMap = selectMapBysurveyIds(surveyIds);
        //作为批阅人需要批阅的试卷
        List<ExamSurveyDirectory> examSurveyDirectoryList = stringExamSurveyDirectoryMap.values().stream().collect(Collectors.toList());
        List<String> collect = examSurveyDirectoryList.stream().map(ExamSurveyDirectory::getId).collect(Collectors.toList());

        for (ExamSurveyDirectory examSurveyDirectory : examSurveyDirectoryList) {
            if (examSurveyDirectory.getCreateId().equals(userId)) {
                examSurveyDirectory.setIsCreated(true);
            }
        }
        List<ExamSurveyDirectory> filteredExamSurveyDirectoryList = examSurveyDirectoryList.stream()
            .filter(examSurveyDirectory -> examSurveyDirectory.getIsMarkState() != null
                && examSurveyDirectory.getIsMarkState().equals(stateInt))
            .collect(Collectors.toList());
        outputObject.setBeans(filteredExamSurveyDirectoryList);
        outputObject.settotal(page.getTotal());
    }

    /**
     * 根据试卷id和当前用户id，创建没有提交考试的答案的学生，创建0分答案
     *
     * @param id
     * @param userId
     */
    @Override
    public void createNotSubStudent(String id, String userId) {
        ExamSurveyDirectory examSurveyDirectory = super.selectById(id);
        String subjectId = examSurveyDirectory.getSubjectId();
        // 查询班级信息
        List<SubjectClasses> subjectClassesList = subjectClassesService.querySubjectClassesByObjectId(subjectId);
        if (CollectionUtil.isEmpty(subjectClassesList)) {
            return;
        }
        List<String> subjectClassesIdList = subjectClassesList.stream().map(SubjectClasses::getId).collect(Collectors.toList());
        // 查询班级的所有学生
        List<SubjectClassesStu> subjectClassesStuList = subjectClassesStuService.queryListBySubClassLinkIds(subjectClassesIdList);
        List<String> allStuNo = subjectClassesStuList.stream().map(SubjectClassesStu::getStuNo).collect(Collectors.toList());
        // 查询学生的答题信息
        List<ExamSurveyAnswer> examSurveyAnswerList = examSurveyAnswerService.queryListByStuNoListAndExamId(allStuNo, id);
        List<String> haveStuNoList = examSurveyAnswerList.stream().map(ExamSurveyAnswer::getStudentNumber).collect(Collectors.toList());
        // 找出没有答题的学生学号
        List<String> notHaveStuNoList = allStuNo.stream().filter(stuNo -> !haveStuNoList.contains(stuNo)).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(notHaveStuNoList)) {
            // 根据学号获取学生的用户信息
            List<Map<String, Object>> userList = iUserService.queryListBuStudentNumberList(Joiner.on(CommonCharConstants.COMMA_MARK).join(notHaveStuNoList));
            // 过滤出学号和id
            Map<String, String> stuNoIdMap = userList.stream().collect(Collectors.toMap(user -> user.get("studentNumber").toString(), user -> user.get("id").toString()));
            // 创造零分答题信息
            List<ExamSurveyAnswer> examSurveyAnswerList1 = new ArrayList<>();
            for (String s : notHaveStuNoList) {
                ExamSurveyAnswer examSurveyAnswer = new ExamSurveyAnswer();
                examSurveyAnswer.setSurveyId(id);
                examSurveyAnswer.setCompleteNum(CommonNumConstants.NUM_ZERO);
                examSurveyAnswer.setCompleteItemNum(CommonNumConstants.NUM_ZERO);
                examSurveyAnswer.setDataSource(CommonNumConstants.NUM_ZERO);
                examSurveyAnswer.setHandleState(CommonNumConstants.NUM_ONE);
                examSurveyAnswer.setIsComplete(CommonNumConstants.NUM_ZERO);
                examSurveyAnswer.setQuNum(CommonNumConstants.NUM_ZERO);
                examSurveyAnswer.setCreateId(stuNoIdMap.getOrDefault(s, "not exits stuNo duty data"));
                examSurveyAnswer.setState(CommonNumConstants.NUM_ONE);
                examSurveyAnswer.setMarkFraction(CommonNumConstants.NUM_ZERO.floatValue());
                examSurveyAnswer.setStudentNumber(s);
                examSurveyAnswerList1.add(examSurveyAnswer);
            }
            examSurveyAnswerService.createEntity(examSurveyAnswerList1, userId);
        }
    }

    @Override
    public void updateSurveyAnswerStatus(String id) {
        ExamSurveyDirectory examSurveyDirectory = selectById(id);
        //已经批阅的学生人数加一
        if (examSurveyDirectory.getReadNum() == null) {
            examSurveyDirectory.setReadNum(CommonNumConstants.NUM_ONE);
        }
        UpdateWrapper<ExamSurveyDirectory> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getReadNum), examSurveyDirectory.getReadNum() + CommonNumConstants.NUM_ONE);
        update(updateWrapper);
        if (examSurveyDirectory.getAllNumber().equals(examSurveyDirectory.getReadNum() + CommonNumConstants.NUM_ONE)) {
            UpdateWrapper<ExamSurveyDirectory> updateWrapper1 = new UpdateWrapper<>();
            updateWrapper1.eq(CommonConstants.ID, id);
            updateWrapper1.set(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getIsMarkState), CommonNumConstants.NUM_ONE);
            update(updateWrapper1);
        }
    }

    /**
     * 自动组卷
     *
     * @param inputObject  输入对象，包含试卷ID和组卷规则列表
     * @param outputObject 输出对象
    */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void autoGeneratePaper(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String surveyId = params.get("id").toString();
        String userId = InputObject.getLogParamsStatic().get("id").toString();

        // 获取试卷信息
        ExamSurveyDirectory examSurveyDirectory = selectById(surveyId);
        if (ObjectUtil.isEmpty(examSurveyDirectory)) {
            throw new CustomException("试卷不存在");
        }

        // 获取组卷规则列表
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> ruleList = JSONUtil.toList(params.get("ruleList").toString(), null);
        if (CollectionUtil.isEmpty(ruleList)) {
            throw new CustomException("组卷规则不能为空");
        }

        // 验证规则
        validateRules(ruleList);

        // 收集所有需要组卷的题目
        List<Question> selectedQuestions = new ArrayList<>();

        // 遍历每个规则，按规则选择题目
        for (Map<String, Object> ruleMap : ruleList) {
            Integer quType = Integer.valueOf(ruleMap.get("quType").toString());
            String quTypeName = QuType.getCName(quType);
            Integer totalScore = Integer.valueOf(ruleMap.get("totalScore").toString());
            Integer questionCount = Integer.valueOf(ruleMap.get("questionCount").toString());

            // 从题库中查询符合条件的题目
            List<Question> candidateQuestions = queryQuestionsFromBank(quType, examSurveyDirectory.getSubjectId());

            if (CollectionUtil.isEmpty(candidateQuestions)) {
                throw new CustomException(String.format("题目类型 %s 的题目数量不足", quTypeName));
            }

            if (candidateQuestions.size() < questionCount) {
                throw new CustomException(String.format("题目类型 %s 的可用题目数量 %d 少于所需数量 %d",
                    quTypeName, candidateQuestions.size(), questionCount));
            }

            // 计算每道题的分值（简单平均分配）
            int scorePerQuestion = totalScore / questionCount;
            int remainingScore = totalScore % questionCount;

            // 随机选择题目
            List<Question> selected = randomlySelectQuestions(candidateQuestions, questionCount);

            // 批量查询题目的选项信息
            List<String> questionIds = selected.stream().map(Question::getId).collect(Collectors.toList());

            // 查询题目的选项信息（通过selectByIds会自动加载选项）
            List<Question> questionsWithOptions = questionService.selectByIds(questionIds.toArray(new String[0]));

            // 设置题目分值和属性
            for (int i = 0; i < questionsWithOptions.size(); i++) {
                Question question = questionsWithOptions.get(i);
                // 复制题目
                Question newQuestion = copyQuestion(question);
                // 设置分值和属性
                int questionScore = scorePerQuestion + (i < remainingScore ? 1 : 0);
                newQuestion.setFraction(questionScore);
                newQuestion.setBelongId(surveyId);
                newQuestion.setTag(CommonNumConstants.NUM_TWO); // 标记为试卷中的题
                newQuestion.setOrderById(selectedQuestions.size() + i + 1); // 设置排序
                selectedQuestions.add(newQuestion);
            }
        }

        // 自动组卷应替换原有题目，避免重复执行时将新题目追加到试卷中
        questionService.deleteBySurveyDirectoryId(surveyId);

        // 创建题目
        if (CollectionUtil.isNotEmpty(selectedQuestions)) {
            questionService.createEntity(selectedQuestions, userId);
            examSurveyDirectory.setQuestionMation(selectedQuestions);
        }

        // 更新试卷的题目数量和总分
        int totalQuestionCount = selectedQuestions.size();
        int totalFraction = selectedQuestions.stream().mapToInt(Question::getFraction).sum();

        UpdateWrapper<ExamSurveyDirectory> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, surveyId);
        updateWrapper.set(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getSurveyQuNum), totalQuestionCount);
        updateWrapper.set(MybatisPlusUtil.toColumns(ExamSurveyDirectory::getFraction), totalFraction);
        update(updateWrapper);

        outputObject.setBean(examSurveyDirectory);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 验证组卷规则
     *
     * @param ruleList 规则列表
     */
    private void validateRules(List<Map<String, Object>> ruleList) {
        for (Map<String, Object> ruleMap : ruleList) {
            Integer quType = Integer.valueOf(ruleMap.get("quType").toString());
            Integer questionCount = Integer.valueOf(ruleMap.get("questionCount").toString());
            Integer totalScore = Integer.valueOf(ruleMap.get("totalScore").toString());

            if (quType == null || quType <= 0) {
                throw new CustomException("题目类型不能为空或小于等于0");
            }

            if (questionCount == null || questionCount <= 0) {
                throw new CustomException("题目数量不能为空或小于等于0");
            }

            if (totalScore == null || totalScore <= 0) {
                throw new CustomException("总分值不能为空或小于等于0");
            }
        }
    }

    /**
     * 从题库中查询符合条件的题目
     *
     * @param quType    题目类型
     * @param subjectId 科目ID
     * @return 题目列表
     */
    private List<Question> queryQuestionsFromBank(Integer quType, String subjectId) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();

        // 只查询题库中的题目（tag=1）
        queryWrapper.eq(MybatisPlusUtil.toColumns(Question::getTag), CommonNumConstants.NUM_ONE);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Question::getQuType), quType);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Question::getIsDelete), CommonNumConstants.NUM_ONE);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Question::getVisibility), CommonNumConstants.NUM_ONE);
        // 筛选该科目的题目
        queryWrapper.eq(MybatisPlusUtil.toColumns(Question::getSubjectId), subjectId);

        return questionService.list(queryWrapper);
    }

    /**
     * 随机选择指定数量的题目
     *
     * @param candidateQuestions 候选题目列表
     * @param count              需要选择的数量
     * @return 选中的题目列表
     */
    private List<Question> randomlySelectQuestions(List<Question> candidateQuestions, Integer count) {
        if (candidateQuestions.size() <= count) {
            return new ArrayList<>(candidateQuestions);
        }

        // 打乱顺序
        List<Question> shuffled = new ArrayList<>(candidateQuestions);
        Collections.shuffle(shuffled);

        // 选择前count个
        return shuffled.subList(0, count);
    }

    /**
     * 复制题目
     *
     * @param source 源题目
     * @return 新题目
     */
    private Question copyQuestion(Question source) {
        Question newQuestion = new Question();
        newQuestion.setQuTitle(source.getQuTitle());
        newQuestion.setQuType(source.getQuType());
        newQuestion.setQuTag(source.getQuTag());
        newQuestion.setIsPublic(source.getIsPublic());
        newQuestion.setAnswerInputRow(source.getAnswerInputRow());
        newQuestion.setAnswerInputWidth(source.getAnswerInputWidth());
        newQuestion.setCellCount(source.getCellCount());
        newQuestion.setCheckType(source.getCheckType());
        newQuestion.setContactsAttr(source.getContactsAttr());
        newQuestion.setContactsField(source.getContactsField());
        newQuestion.setCopyFromId(source.getId());
        newQuestion.setHv(source.getHv());
        newQuestion.setRandOrder(source.getRandOrder());
        newQuestion.setVisibility(source.getVisibility());
        newQuestion.setYesnoOption(source.getYesnoOption());
        newQuestion.setKnowledgeIds(source.getKnowledgeIds());
        newQuestion.setFileUrl(source.getFileUrl());
        newQuestion.setFileType(source.getFileType());
        newQuestion.setWhetherUpload(source.getWhetherUpload());
        newQuestion.setIsDefaultAnswer(source.getIsDefaultAnswer());
        newQuestion.setParamInt01(source.getParamInt01());
        newQuestion.setParamInt02(source.getParamInt02());
        newQuestion.setSchoolId(source.getSchoolId());
        newQuestion.setFacultyId(source.getFacultyId());
        newQuestion.setMajorId(source.getMajorId());
        newQuestion.setSubjectId(source.getSubjectId());

        // 复制题目选项等信息（需要在创建后通过关联表复制）
        newQuestion.setRadioTd(source.getRadioTd());
        newQuestion.setCheckboxTd(source.getCheckboxTd());
        newQuestion.setScoreTd(source.getScoreTd());
        newQuestion.setMultifillblankTd(source.getMultifillblankTd());
        newQuestion.setOrderByTd(source.getOrderByTd());
        newQuestion.setColumnTd(source.getColumnTd());
        newQuestion.setRowTd(source.getRowTd());
        newQuestion.setQuestionLogic(source.getQuestionLogic());

        return newQuestion;
    }

}
