/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.school.entity;

import java.io.Serializable;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelTarget;
import lombok.Data;

@Data
@ExcelTarget(value = "SchoolStudentExcelModel")
public class SchoolStudentExcelModel implements Serializable {
	
	private static final long serialVersionUID = -6418517623485951191L;

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
	
	@Excel(name = "学生姓名", width = 15, isImportField = "true_st", orderNum = "1")
	private String studentName;
	
	@Excel(name = "曾用名", width = 15, isImportField = "true_st", orderNum = "2")
	private String nameUseBefore;
	
	@Excel(name = "学号", width = 15, isImportField = "true_st", orderNum = "3")
	private String studentNo;
	
	@Excel(name = "性别", width = 10, isImportField = "true_st", replace = {"保密_0", "男生_1", "女生_2"}, orderNum = "4")
	private String userSex;
	
	@Excel(name = "民族", width = 20, isImportField = "true_st", orderNum = "5")
	private String nation;
	
	@Excel(name = "证件类型", width = 30, isImportField = "true_st", orderNum = "6")
	private String idcardType;
	
	@Excel(name = "身份证", width = 25, isImportField = "true_st", orderNum = "7")
	private String idCard;
	
	@Excel(name = "学校", width = 25, isImportField = "true_st", orderNum = "8")
	private String schoolId;
	
	@Excel(name = "年级", width = 15, isImportField = "true_st", orderNum = "9")
	private String gradeId;
	
	@Excel(name = "班级", width = 15, isImportField = "true_st", orderNum = "10")
	private String classId;
	
	@Excel(name = "户口类型", width = 15, isImportField = "true_st", orderNum = "11")
	private String residenceType;
	
	@Excel(name = "户口所在地编码", width = 15, isImportField = "true_st", orderNum = "12")
	private String residenceNo;
	
	@Excel(name = "户口所在地派出所", width = 30, isImportField = "true_st", orderNum = "13")
	private String residencePoliceStation;
	
	@Excel(name = "入校时间", width = 15, isImportField = "true_st", orderNum = "14")
	private String joinTime;
	
	@Excel(name = "届", width = 10, isImportField = "true_st", orderNum = "15")
	private String sessionYear;
	
	@Excel(name = "家庭住址", width = 30, isImportField = "true_st", orderNum = "16")
	private String homeAddress;
	
	@Excel(name = "家庭邮编", width = 15, isImportField = "true_st", orderNum = "17")
	private String homePostalCode;
	
	@Excel(name = "家庭联系人", width = 15, isImportField = "true_st", orderNum = "18")
	private String homeContact;
	
	@Excel(name = "家庭电话", width = 15, isImportField = "true_st", orderNum = "19")
	private String homePhone;
	
	@Excel(name = "学生特长", width = 30, isImportField = "true_st", orderNum = "20")
	private String speciality;
	
	@Excel(name = "监护人姓名", width = 15, isImportField = "true_st", orderNum = "21")
	private String guardian;
	
	@Excel(name = "监护人身份证号", width = 30, isImportField = "true_st", orderNum = "22")
	private String guardianIdcard;
	
	@Excel(name = "本地联系人姓名", width = 15, isImportField = "true_st", orderNum = "23")
	private String localContact;
	
	@Excel(name = "本地联系人关系", width = 10, isImportField = "true_st", orderNum = "24")
	private String contactRelationship;
	
	@Excel(name = "本地联系人手机号", width = 15, isImportField = "true_st", orderNum = "25")
	private String contactPhone;
	
	@Excel(name = "学生类别", width = 30, isImportField = "true_st", orderNum = "26")
	private String stuType;
	
	@Excel(name = "是否学区外", width = 15, isImportField = "true_st", dict = "yesORno", addressList = true, orderNum = "27")
	private String outsideSchool;
	
	@Excel(name = "是否外地学生", width = 15, isImportField = "true_st", orderNum = "28")
	private String foreignStudents;
	
	@Excel(name = "是否独生子女", width = 15, isImportField = "true_st", orderNum = "29")
	private String onlyChild;
	
	@Excel(name = "是否留守儿童", width = 15, isImportField = "true_st", orderNum = "30")
	private String behindChildren;
	
	@Excel(name = "是否流动人口", width = 15, isImportField = "true_st", orderNum = "31")
	private String floatingPopulation;
	
	@Excel(name = "是否单亲家庭", width = 15, isImportField = "true_st", orderNum = "32")
	private String singleParentFamily;
	
	@Excel(name = "入学方式", width = 15, isImportField = "true_st", orderNum = "33")
	private String entranceType;
	
	@Excel(name = "择校生", width = 15, isImportField = "true_st", orderNum = "34")
	private String schoolChoiceStudents;
	
	@Excel(name = "就读方式", width = 15, isImportField = "true_st", orderNum = "35")
	private String attendType;
	
	@Excel(name = "健康状态", width = 15, isImportField = "true_st", orderNum = "36")
	private String healthCondition;
	
	@Excel(name = "是否港台华侨", width = 15, isImportField = "true_st", orderNum = "37")
	private String overseasChinese;
	
	@Excel(name = "家庭情况", width = 15, isImportField = "true_st", orderNum = "38")
	private String homeSituation;
	
	@Excel(name = "身心障碍", width = 15, isImportField = "true_st", orderNum = "39")
	private String bodyMind;
	
	@Excel(name = "血型", width = 15, isImportField = "true_st", orderNum = "40")
	private String bloodType;
	
	@Excel(name = "学前教育", width = 15, isImportField = "true_st", orderNum = "41")
	private String preschoolEducation;
	
	@Excel(name = "学前教育学校", width = 15, isImportField = "true_st", orderNum = "42")
	private String preschoolSchool;
	
	@Excel(name = "是否孤儿", width = 15, isImportField = "true_st", orderNum = "43")
	private String orphan;
	
	@Excel(name = "是否烈士/优抚子女", width = 15, isImportField = "true_st", orderNum = "44")
	private String preferential;
	
	@Excel(name = "是否享受一补", width = 15, isImportField = "true_st", orderNum = "45")
	private String onePatch;
	
	@Excel(name = "交通方式", width = 15, isImportField = "true_st", orderNum = "46")
	private String modeOfTransportation;
	
	@Excel(name = "是否乘坐校车", width = 15, isImportField = "true_st", orderNum = "47")
	private String schoolBus;
	
	@Excel(name = "疫苗接种情况", width = 15, isImportField = "true_st", orderNum = "48")
	private String vaccination;
	
}
