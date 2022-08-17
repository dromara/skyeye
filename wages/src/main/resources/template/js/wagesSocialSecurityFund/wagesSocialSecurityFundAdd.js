
// 选择企业
var companyList = [];

// 选择部门
var departmentList = [];

// 选择员工，多选
var checkStaffList = [];

// 五险一金设置
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'tagEditor', 'laydate'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form,
		laydate = layui.laydate;

	laydate.render({
		elem: '#executeMonth',
		type: 'month',
		range: '~'
	});

	skyeyeEnclosure.init('enclosureUpload');
	matchingLanguage();
	form.render();
	form.on('submit(formAddBean)', function (data) {
		if (winui.verifyForm(data.elem)) {
			var params = {
				title: $("#title").val(),
				startMonth: $("#executeMonth").val().split('~')[0].trim(),
				endMonth: $("#executeMonth").val().split('~')[1].trim(),
				desc: $("#desc").val(),
				str: "",
				insuranceEndowmentBase: $("#insuranceEndowmentBase").val(),
				insuranceEndowmentPerson: $("#insuranceEndowmentPerson").val(),
				insuranceEndowmentCompany: $("#insuranceEndowmentCompany").val(),
				insuranceUnemploymentBase: $("#insuranceUnemploymentBase").val(),
				insuranceUnemploymentPerson: $("#insuranceUnemploymentPerson").val(),
				insuranceUnemploymentCompany: $("#insuranceUnemploymentCompany").val(),
				insuranceEmploymentBase: $("#insuranceEmploymentBase").val(),
				insuranceEmploymentPerson: $("#insuranceEmploymentPerson").val(),
				insuranceEmploymentCompany: $("#insuranceEmploymentCompany").val(),
				insuranceMaternityBase: $("#insuranceMaternityBase").val(),
				insuranceMaternityPerson: $("#insuranceMaternityPerson").val(),
				insuranceMaternityCompany: $("#insuranceMaternityCompany").val(),
				insuranceMedicalBase: $("#insuranceMedicalBase").val(),
				insuranceMedicalPerson: $("#insuranceMedicalPerson").val(),
				insuranceMedicalCompany: $("#insuranceMedicalCompany").val(),
				insTotalSeriouslyIllIndividual: $("#insTotalSeriouslyIllIndividual").val(),
				insTotalPerson: $("#insTotalPerson").val(),
				insTotalCompany: $("#insTotalCompany").val(),
				accumulationBase: $("#accumulationBase").val(),
				accumulationPersonScale: $("#accumulationPersonScale").val(),
				accumulationCompanyScale: $("#accumulationCompanyScale").val(),
				accumulationPersonAmount: $("#accumulationPersonAmount").val(),
				accumulationCompanyAmount: $("#accumulationCompanyAmount").val(),
				sortNo: $("#sortNo").val(),
				enclosure: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload')
			};
			// 公积金适用对象
			var object = [];
			$.each(companyList, function(i, item){
				object.push({
					objectId: item.id,
					objectType: 3
				});
			});
			$.each(departmentList, function(i, item){
				object.push({
					objectId: item.id,
					objectType: 2
				});
			});
			$.each(checkStaffList, function(i, item){
				object.push({
					objectId: item.id,
					objectType: 1
				});
			});
			params.str = JSON.stringify(object);
			AjaxPostUtil.request({url: sysMainMation.wagesBasePath + "wagessocialsecurityfund002", params: params, type: 'json', callback: function (json) {
				parent.layer.close(index);
				parent.refreshCode = '0';
			}});
		}
		return false;
	});

	// 选择企业
	$('#company').tagEditor({
		initialTags: [],
		placeholder: '请选择企业',
		editorTag: false,
		beforeTagDelete: function(field, editor, tags, val) {
			companyList = [].concat(arrayUtil.removeArrayPointName(companyList, val));
		}
	});
	$("body").on("click", "#companySel", function() {
		_openNewWindows({
			url: "../../tpl/companymation/companyChooseList.html",
			title: "选择企业",
			pageId: "companyChooseList",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				// 重置数据
				companyList = [].concat(systemCommonUtil.tagEditorResetData('company', companyList));
			}});
	});

	// 选择部门
	$('#department').tagEditor({
		initialTags: [],
		placeholder: '请选择部门',
		editorTag: false,
		beforeTagDelete: function(field, editor, tags, val) {
			departmentList = [].concat(arrayUtil.removeArrayPointName(departmentList, val));
		}
	});
	$("body").on("click", "#departmentSel", function() {
		_openNewWindows({
			url: "../../tpl/companydepartment/companyDepartmentChooseList.html",
			title: "选择部门",
			pageId: "companyDepartmentChooseList",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				// 重置数据
				departmentList = [].concat(systemCommonUtil.tagEditorResetData('department', departmentList));
			}});
	});

	// 选择员工
	$('#userStaff').tagEditor({
		initialTags: [],
		placeholder: '请选择员工',
		editorTag: false,
		beforeTagDelete: function(field, editor, tags, val) {
			checkStaffList = [].concat(arrayUtil.removeArrayPointName(checkStaffList, val));
		}
	});
	$("body").on("click", "#userStaffSel", function() {
		systemCommonUtil.userStaffCheckType = true; // 选择类型，默认单选，true:多选，false:单选
		systemCommonUtil.checkStaffMation = [].concat(checkStaffList); // 选择时返回的对象
		systemCommonUtil.openSysAllUserStaffChoosePage(function (checkStaffMation) {
			// 重置数据
			checkStaffList = [].concat(systemCommonUtil.tagEditorResetData('userStaff', checkStaffMation));
		});
	});

	// 五险金额变化
	$("body").on("input", ".sumMoney", function() {
		calculatedTotalPrice();
	});
	$("body").on("change", ".sumMoney", function() {
		calculatedTotalPrice();
	});

	function calculatedTotalPrice(){
		// 养老保险
		var person = multiplication($("#insuranceEndowmentBase").val(), division($("#insuranceEndowmentPerson").val(), 100));
		var company = multiplication($("#insuranceEndowmentBase").val(), division($("#insuranceEndowmentCompany").val(), 100));
		// 失业保险
		person = sum(person, multiplication($("#insuranceUnemploymentBase").val(), division($("#insuranceUnemploymentPerson").val(), 100)));
		company = sum(company, multiplication($("#insuranceUnemploymentBase").val(), division($("#insuranceUnemploymentCompany").val(), 100)));
		// 工伤保险
		person = sum(person, multiplication($("#insuranceEmploymentBase").val(), division($("#insuranceEmploymentPerson").val(), 100)));
		company = sum(company, multiplication($("#insuranceEmploymentBase").val(), division($("#insuranceEmploymentCompany").val(), 100)));
		// 生育保险
		person = sum(person, multiplication($("#insuranceMaternityBase").val(), division($("#insuranceMaternityPerson").val(), 100)));
		company = sum(company, multiplication($("#insuranceMaternityBase").val(), division($("#insuranceMaternityCompany").val(), 100)));
		// 医疗保险
		person = sum(person, multiplication($("#insuranceMedicalBase").val(), division($("#insuranceMedicalPerson").val(), 100)));
		company = sum(company, multiplication($("#insuranceMedicalBase").val(), division($("#insuranceMedicalCompany").val(), 100)));
		// 大病个人
		person = sum(person, $("#insTotalSeriouslyIllIndividual").val());
		$("#insTotalPerson").val(person);
		$("#insTotalCompany").val(company);
	}

	// 一金金额变化
	$("body").on("input", ".oneGold", function() {
		calculatedOneGoldTotalPrice();
	});
	$("body").on("change", ".oneGold", function() {
		calculatedOneGoldTotalPrice();
	});

	function calculatedOneGoldTotalPrice(){
		// 一金
		var person = multiplication($("#accumulationBase").val(), division($("#accumulationPersonScale").val(), 100));
		var company = multiplication($("#accumulationBase").val(), division($("#accumulationCompanyScale").val(), 100));
		$("#accumulationPersonAmount").val(person);
		$("#accumulationCompanyAmount").val(company);
	}

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});