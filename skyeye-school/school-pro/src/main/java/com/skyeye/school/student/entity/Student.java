/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.student.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.skyeye.annotation.api.ApiModel;
import com.skyeye.annotation.api.ApiModelProperty;
import com.skyeye.annotation.api.Property;
import com.skyeye.annotation.cache.RedisCacheField;
import com.skyeye.annotation.unique.UniqueField;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.features.OperatorUserInfo;
import com.skyeye.eve.entity.School;
import com.skyeye.school.faculty.entity.Faculty;
import com.skyeye.school.grade.entity.Classes;
import com.skyeye.school.major.entity.Major;
import lombok.Data;

import java.util.List;

import com.skyeye.school.student.classenum.AdmissionMethod;
import com.skyeye.school.student.classenum.ModeOfStudy;
import com.skyeye.common.enumeration.BloodTypeEnum;
import com.skyeye.common.enumeration.HealthStatus;
import com.skyeye.common.enumeration.IDCardType;
import com.skyeye.common.enumeration.RegistrationType;
import com.skyeye.common.enumeration.SexEnum;
import com.skyeye.school.student.classenum.StudentState;
import com.skyeye.school.student.classenum.StudentType;
import com.skyeye.school.student.classenum.VaccinationStatus;
import com.skyeye.common.enumeration.WhetherEnum;

/**
 * @ClassName: Student
 * @Description: 学生实体类
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/10 16:11
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
@ExcelTarget(value = "SchoolStudentExcelModel")
@UniqueField("no")
@RedisCacheField(name = "school:student", cacheTime = RedisConstants.A_YEAR_SECONDS * 4)
@TableName(value = "school_student")
@ApiModel(value = "学生实体类")
public class Student extends OperatorUserInfo {

    /**
     * name：列名,支持name_id
     * height：列高,后期打算统一使用@ExcelTarget的height,这个会被废弃,注意
     * width：列宽
     * isImportField：校验字段,看看这个字段是不是导入的Excel中有,如果没有说明是错误的Excel,读取失败,支持name_id
     * orderNum：列的排序,支持name_id
     * type：导出类型 1 是文本 2 是图片,3 是函数,10 是数字 默认是文本
     * exportFormat：导出的时间格式,以这个是否为空来判断是否需要格式化日期
     * importFormat：导入的时间格式,以这个是否为空来判断是否需要格式化日期
     * format：时间格式,相当于同时设置了exportFormat 和 importFormat
     * databaseFormat：导出时间设置,如果字段是Date类型则不需要设置 数据库如果是string 类型,这个需要设置这个数据库格式,用以转换时间格式输出
     * mergeVertical：纵向合并内容相同的单元格
     * replace：值得替换  导出是{a_id,b_id} 导入反过来
     */

    @TableId("id")
    @ApiModelProperty("主键id。为空时新增，不为空时编辑")
    private String id;

    @TableField("name")
    @ApiModelProperty(value = "学生姓名", required = "required", fuzzyLike = true)
    @Excel(name = "学生姓名", width = 15, isImportField = "true_st", orderNum = "1")
    private String name;

    @TableField("img")
    @ApiModelProperty(value = "学生图片")
    private String img;

    @TableField("name_used")
    @ApiModelProperty(value = "曾用名")
    @Excel(name = "曾用名", width = 15, isImportField = "true_st", orderNum = "2")
    private String nameUsed;

    @TableField("no")
    @ApiModelProperty(value = "学号", required = "required", fuzzyLike = true)
    @Excel(name = "学号", width = 15, isImportField = "true_st", orderNum = "3")
    private String no;

    @TableField("sex")
    @ApiModelProperty(value = "性别", required = "required", enumClass = SexEnum.class)
    @Excel(name = "性别", width = 10, isImportField = "true_st", replace = {"保密_0", "男生_1", "女生_2"}, orderNum = "4")
    private Integer sex;

    @TableField("nation")
    @ApiModelProperty(value = "民族")
    @Excel(name = "民族", width = 20, isImportField = "true_st", orderNum = "5")
    private String nation;

    @TableField("idcard_type")
    @ApiModelProperty(value = "证件类型", required = "required", enumClass = IDCardType.class)
    @Excel(name = "证件类型", width = 30, isImportField = "true_st", orderNum = "6")
    private Integer idCardType;

    @TableField("idcard")
    @ApiModelProperty(value = "身份证", required = "required")
    @Excel(name = "身份证", width = 25, isImportField = "true_st", orderNum = "7")
    private String idCard;

    @TableField("school_id")
    @ApiModelProperty(value = "学校", required = "required")
    @Excel(name = "学校", width = 25, isImportField = "true_st", orderNum = "8")
    private String schoolId;

    @TableField(exist = false)
    @Property(value = "学校")
    private School schoolMation;

    @TableField("faculty_id")
    @ApiModelProperty(value = "院系", required = "required")
    @Excel(name = "院系", width = 15, isImportField = "true_st", orderNum = "9")
    private String facultyId;

    @TableField(exist = false)
    @Property(value = "所属院系信息")
    private Faculty facultyMation;

    @TableField("major_id")
    @ApiModelProperty(value = "专业", required = "required")
    @Excel(name = "专业", width = 15, isImportField = "true_st", orderNum = "10")
    private String majorId;

    @TableField(exist = false)
    @Property(value = "所属专业信息")
    private Major majorMation;

    @TableField("class_id")
    @ApiModelProperty(value = "班级", required = "required")
    @Excel(name = "班级", width = 15, isImportField = "true_st", orderNum = "11")
    private String classId;

    @TableField(exist = false)
    @Property(value = "所属班级信息")
    private Classes classMation;

    @TableField("residence_type")
    @ApiModelProperty(value = "户口类型", enumClass = RegistrationType.class)
    @Excel(name = "户口类型", width = 15, isImportField = "true_st", orderNum = "12")
    private Integer residenceType;

    @TableField("residence_no")
    @ApiModelProperty(value = "户口所在地编码")
    @Excel(name = "户口所在地编码", width = 15, isImportField = "true_st", orderNum = "13")
    private String residenceNo;

    @TableField("residence_police_station")
    @ApiModelProperty(value = "户口所在地派出所")
    @Excel(name = "户口所在地派出所", width = 30, isImportField = "true_st", orderNum = "14")
    private String residencePoliceStation;

    @TableField("join_time")
    @ApiModelProperty(value = "入校时间", required = "required")
    @Excel(name = "入校时间", width = 15, isImportField = "true_st", orderNum = "15")
    private String joinTime;

    @TableField("year")
    @ApiModelProperty(value = "届", required = "required")
    @Excel(name = "届", width = 10, isImportField = "true_st", orderNum = "16")
    private String year;

    @TableField("home_address")
    @ApiModelProperty(value = "家庭住址")
    @Excel(name = "家庭住址", width = 30, isImportField = "true_st", orderNum = "17")
    private String homeAddress;

    @TableField("home_postal_code")
    @ApiModelProperty(value = "家庭邮编")
    @Excel(name = "家庭邮编", width = 15, isImportField = "true_st", orderNum = "18")
    private String homePostalCode;

    @TableField("home_contact")
    @ApiModelProperty(value = "家庭联系人")
    @Excel(name = "家庭联系人", width = 15, isImportField = "true_st", orderNum = "19")
    private String homeContact;

    @TableField("home_phone")
    @ApiModelProperty(value = "家庭电话")
    @Excel(name = "家庭电话", width = 15, isImportField = "true_st", orderNum = "20")
    private String homePhone;

    @TableField("speciality")
    @ApiModelProperty(value = "学生特长")
    @Excel(name = "学生特长", width = 30, isImportField = "true_st", orderNum = "21")
    private String speciality;

    @TableField("guardian")
    @ApiModelProperty(value = "监护人姓名")
    @Excel(name = "监护人姓名", width = 15, isImportField = "true_st", orderNum = "22")
    private String guardian;

    @TableField("guardian_idcard")
    @ApiModelProperty(value = "监护人身份证号")
    @Excel(name = "监护人身份证号", width = 30, isImportField = "true_st", orderNum = "23")
    private String guardianIdcard;

    @TableField("local_contact")
    @ApiModelProperty(value = "本地联系人姓名")
    @Excel(name = "本地联系人姓名", width = 15, isImportField = "true_st", orderNum = "24")
    private String localContact;

    @TableField("contact_relationship")
    @ApiModelProperty(value = "本地联系人关系")
    @Excel(name = "本地联系人关系", width = 10, isImportField = "true_st", orderNum = "25")
    private String contactRelationship;

    @TableField("contact_phone")
    @ApiModelProperty(value = "本地联系人手机号")
    @Excel(name = "本地联系人手机号", width = 15, isImportField = "true_st", orderNum = "26")
    private String contactPhone;

    @TableField("type")
    @ApiModelProperty(value = "学生类别", required = "required", enumClass = StudentType.class)
    @Excel(name = "学生类别", width = 30, isImportField = "true_st", orderNum = "27")
    private Integer type;

    @TableField("outside_school")
    @ApiModelProperty(value = "是否学区外", enumClass = WhetherEnum.class)
    @Excel(name = "是否学区外", width = 15, isImportField = "true_st", dict = "yesORno", addressList = true, orderNum = "28")
    private Integer outsideSchool;

    @TableField("foreign_students")
    @ApiModelProperty(value = "是否外地学生", enumClass = WhetherEnum.class)
    @Excel(name = "是否外地学生", width = 15, isImportField = "true_st", orderNum = "29")
    private Integer foreignStudents;

    @TableField("behind_children")
    @ApiModelProperty(value = "是否留守儿童", enumClass = WhetherEnum.class)
    @Excel(name = "是否留守儿童", width = 15, isImportField = "true_st", orderNum = "30")
    private Integer behindChildren;

    @TableField("floating_population")
    @ApiModelProperty(value = "是否流动人口", enumClass = WhetherEnum.class)
    @Excel(name = "是否流动人口", width = 15, isImportField = "true_st", orderNum = "31")
    private Integer floatingPopulation;

    @TableField("single_parent_family")
    @ApiModelProperty(value = "是否单亲家庭", enumClass = WhetherEnum.class)
    @Excel(name = "单亲家庭", width = 15, isImportField = "true_st", replace = {"是_1", "否_2"}, orderNum = "32")
    private Integer singleParentFamily;

    @TableField("entrance_type")
    @ApiModelProperty(value = "入学方式", enumClass = AdmissionMethod.class)
    @Excel(name = "入学方式", width = 15, isImportField = "true_st", orderNum = "33")
    private Integer entranceType;

    @TableField("school_choice_students")
    @ApiModelProperty(value = "择校生(不可修改)", enumClass = WhetherEnum.class)
    @Excel(name = "择校生", width = 15, isImportField = "true_st", orderNum = "34")
    private Integer schoolChoiceStudents;

    @TableField("attend_type")
    @ApiModelProperty(value = "就读方式", enumClass = ModeOfStudy.class)
    @Excel(name = "就读方式", width = 15, isImportField = "true_st", orderNum = "35")
    private Integer attendType;

    @TableField("health_condition")
    @ApiModelProperty(value = "健康状态", enumClass = HealthStatus.class)
    @Excel(name = "健康状态", width = 15, isImportField = "true_st", orderNum = "36")
    private Integer healthCondition;

    @TableField("overseas_chinese")
    @ApiModelProperty(value = "是否港台华侨", enumClass = WhetherEnum.class)
    @Excel(name = "是否港台华侨", width = 15, isImportField = "true_st", orderNum = "37")
    private Integer overseasChinese;

    @TableField("birthday")
    @Property(value = "出生日期")
    @Excel(name = "出生日期", width = 15, isImportField = "true_st", orderNum = "38")
    private String birthday;

    @TableField("state")
    @ApiModelProperty(value = "状态", required = "required", enumClass = StudentState.class)
    @Excel(name = "状态 ", width = 15, isImportField = "true_st", replace = {"在校_1", "毕业_2", "休学_3"}, orderNum = "39")
    private String state;

    @TableField("blood_type")
    @ApiModelProperty(value = "血型", enumClass = BloodTypeEnum.class)
    @Excel(name = "血型", width = 15, isImportField = "true_st", orderNum = "40")
    private Integer bloodType;

    @TableField("preschool_education")
    @ApiModelProperty(value = "学前教育", enumClass = WhetherEnum.class)
    @Excel(name = "学前教育", width = 15, isImportField = "true_st", orderNum = "41")
    private Integer preschoolEducation;

    @TableField("preschool_school")
    @ApiModelProperty(value = "学前教育学校")
    @Excel(name = "学前教育学校", width = 15, isImportField = "true_st", orderNum = "42")
    private String preschoolSchool;

    @TableField("only_child")
    @ApiModelProperty(value = "是否独生子女", enumClass = WhetherEnum.class)
    @Excel(name = "是否独生子女", width = 15, isImportField = "true_st", orderNum = "43")
    private Integer onlyChild;

    @TableField("orphan")
    @ApiModelProperty(value = "是否孤儿", enumClass = WhetherEnum.class)
    @Excel(name = "是否孤儿", width = 15, isImportField = "true_st", orderNum = "44")
    private Integer orphan;

    @TableField("preferential")
    @ApiModelProperty(value = "是否烈士/优抚子女", enumClass = WhetherEnum.class)
    @Excel(name = "是否烈士/优抚子女", width = 15, isImportField = "true_st", orderNum = "45")
    private Integer preferential;

    @TableField("one_patch")
    @ApiModelProperty(value = "是否享受一补", enumClass = WhetherEnum.class)
    @Excel(name = "是否享受一补", width = 15, isImportField = "true_st", orderNum = "46")
    private Integer onePatch;

    @TableField("mode_of_transportation")
    @ApiModelProperty(value = "交通方式，参考数据字典")
    @Excel(name = "交通方式", width = 15, isImportField = "true_st", orderNum = "47")
    private String modeOfTransportation;

    @TableField("school_bus")
    @ApiModelProperty(value = "是否乘坐校车", enumClass = WhetherEnum.class)
    @Excel(name = "是否乘坐校车", width = 15, isImportField = "true_st", orderNum = "48")
    private Integer schoolBus;

    @TableField("vaccination")
    @ApiModelProperty(value = "疫苗接种情况", enumClass = VaccinationStatus.class)
    @Excel(name = "疫苗接种情况", width = 15, isImportField = "true_st", orderNum = "49")
    private Integer vaccination;

    @TableField("phone")
    @ApiModelProperty(value = "电话号码")
    @Excel(name = "电话号码", width = 15, isImportField = "true_st", orderNum = "50")
    private String phone;

    @TableField("email")
    @ApiModelProperty(value = "邮箱")
    @Excel(name = "邮箱", width = 15, isImportField = "true_st", orderNum = "51")
    private String email;

    @TableField(exist = false)
    @ApiModelProperty(value = "学生身心障碍", required = "json")
    private List<StudentBodyMind> studentBodyMindList;

    @TableField(exist = false)
    @ApiModelProperty(value = "学生家庭情况", required = "json")
    private List<StudentFamilySituation> studentFamilySituation;

    @TableField(exist = false)
    @ApiModelProperty(value = "学生父母情况", required = "json")
    private List<StudentParents> studentParents;

}
