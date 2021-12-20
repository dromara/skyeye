
var staffId = "";

var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	table = layui.table;
	    
	    staffId = parent.rowId;
	    
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "staff005",
		 	params: {rowId: staffId},
		 	pagination: false,
			method: "GET",
		 	template: $("#beanTemplate").html(),
		 	ajaxSendAfter:function(json){
		 		if(json.bean.state == "在职"){
		 			$("#leaveTime").hide();
		 			$("#leaveReason").hide();
		 		}
		 		$("#userPhoto").attr("src", fileBasePath + json.bean.userPhoto);
		 		matchingLanguage();
		 		form.render();
		 	}
		});
		
		initStaffFamilyTable();
		// 初始化家庭成员
		function initStaffFamilyTable(){
			table.render({
			    id: 'sysStaffFamilyTable',
			    elem: '#sysStaffFamilyTable',
			    method: 'post',
			    url: reqBasePath + 'sysstafffamily006',
			    where: {staffId: staffId},
			    even: true,
			    page: true,
			    limits: getLimits(),
		    	limit: getLimit(),
			    cols: [[
			        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
			        { field: 'name', title: '名称', align: 'left', width: 100 },
			        { field: 'relationshipName', title: '与本人关系', width: 100},
			        { field: 'sex', title: '性别', width: 80, templet: function(d){
	                    if(d.sex == '0'){
	                        return "保密";
	                    }else if(d.sex == '1'){
	                        return "男";
	                    }else if(d.sex == '2'){
	                        return "女";
	                    }
	                }},
			        { field: 'cardTypeName', title: '证件类型', width: 100},
			        { field: 'cardNumber', title: '证件编号', width: 150},
	                { field: 'politicName', title: '政治面貌', width: 100},
	                { field: 'workUnit', title: '工作单位', width: 140},
	                { field: 'job', title: '职务', width: 120},
	                { field: 'jobNumber', title: '员工工号', align: 'left', width: 80 },
	                { field: 'userName', title: '员工姓名', align: 'left', width: 100 },
	                { field: 'state', title: '员工状态', align: 'center', width: 80, templet: function(d){
	                    if(d.state == '1'){
	                        return "在职";
	                    }else if(d.state == '2'){
	                        return "离职";
	                    }else if(d.state == '3'){
	                        return "见习";
	                    }else if(d.state == '4'){
	                        return "试用";
	                    }else if(d.state == '5'){
	                        return "退休";
	                    }
	                }},
	                { field: 'createTime', title: '录入时间', align: 'center', width: 100}
			    ]]
			});
		}
		
		initStaffEducationTable();
		// 初始化教育信息
		function initStaffEducationTable(){
			table.render({
			    id: 'sysStaffEducationTable',
			    elem: '#sysStaffEducationTable',
			    method: 'post',
			    url: reqBasePath + 'sysstaffeducation006',
			    where: {staffId: staffId},
			    even: true,
			    page: true,
			    limits: getLimits(),
		    	limit: getLimit(),
			    cols: [[
			        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
			        { field: 'graductionSchool', title: '毕业院校', align: 'left', width: 160 },
			        { field: 'admissionTime', title: '入学时间', align: 'center', width: 100},
			        { field: 'graduationTime', title: '毕业时间', align: 'center', width: 100},
	                { field: 'major', title: '专业', width: 160},
	                { field: 'educationName', title: '学历', align: 'left', width: 120 },
	                { field: 'jobNumber', title: '员工工号', align: 'left', width: 80 },
	                { field: 'userName', title: '员工姓名', align: 'left', width: 100 },
	                { field: 'state', title: '员工状态', align: 'center', width: 80, templet: function(d){
	                    if(d.state == '1'){
	                        return "在职";
	                    }else if(d.state == '2'){
	                        return "离职";
	                    }else if(d.state == '3'){
	                        return "见习";
	                    }else if(d.state == '4'){
	                        return "试用";
	                    }else if(d.state == '5'){
	                        return "退休";
	                    }
	                }},
	                { field: 'learningModalityName', title: '学习形式', align: 'left', width: 150 },
	                { field: 'schoolNatureName', title: '学校性质', align: 'left', width: 100 },
	                { field: 'createTime', title: '录入时间', align: 'center', width: 100}
			    ]]
			});
		}
		
		initStaffJobTable();
		// 初始化工作履历
		function initStaffJobTable(){
			table.render({
			    id: 'sysStaffJobTable',
			    elem: '#sysStaffJobTable',
			    method: 'post',
			    url: reqBasePath + 'sysstaffjobresume006',
			    where: {staffId: staffId},
			    even: true,
			    page: true,
			    limits: getLimits(),
		    	limit: getLimit(),
			    cols: [[
			        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
			        { field: 'workUnit', title: '任职单位', align: 'left', width: 160 },
			        { field: 'department', title: '任职部门', width: 120},
			        { field: 'job', title: '职务', width: 120},
	                { field: 'station', title: '岗位', width: 120},
	                { field: 'startTime', title: '任职开始时间', align: 'center', width: 100 },
	                { field: 'endTime', title: '任职结束时间', align: 'center', width: 100 },
	                { field: 'jobNumber', title: '员工工号', align: 'left', width: 80 },
	                { field: 'userName', title: '员工姓名', align: 'left', width: 100 },
	            	{ field: 'state', title: '员工状态', align: 'center', width: 80, templet: function(d){
	                    if(d.state == '1'){
	                        return "在职";
	                    }else if(d.state == '2'){
	                        return "离职";
	                    }else if(d.state == '3'){
	                        return "见习";
	                    }else if(d.state == '4'){
	                        return "试用";
	                    }else if(d.state == '5'){
	                        return "退休";
	                    }
	                }},
	                { field: 'createTime', title: '录入时间', align: 'center', width: 100}
			    ]]
			});
		}
		
		initStaffLanguageTable();
		// 初始化语种能力
		function initStaffLanguageTable(){
			table.render({
			    id: 'sysStaffLanguageTable',
			    elem: '#sysStaffLanguageTable',
			    method: 'post',
			    url: reqBasePath + 'sysstafflanguage006',
			    where: {staffId: staffId},
			    even: true,
			    page: true,
			    limits: getLimits(),
		    	limit: getLimit(),
			    cols: [[
			        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
			        { field: 'languageTypeName', title: '语种类型', align: 'left', width: 160 },
			        { field: 'levelName', title: '语种等级', width: 120},
			        { field: 'getTime', title: '获取时间', align: 'center', width: 100},
	                { field: 'jobNumber', title: '员工工号', align: 'left', width: 80 },
	                { field: 'userName', title: '员工姓名', align: 'left', width: 100 },
	                { field: 'state', title: '员工状态', align: 'center', width: 80, templet: function(d){
	                    if(d.state == '1'){
	                        return "在职";
	                    }else if(d.state == '2'){
	                        return "离职";
	                    }else if(d.state == '3'){
	                        return "见习";
	                    }else if(d.state == '4'){
	                        return "试用";
	                    }else if(d.state == '5'){
	                        return "退休";
	                    }
	                }},
	                { field: 'createTime', title: '录入时间', align: 'center', width: 100}
			    ]]
			});
		}
		
		initStaffCertificateTable();
		// 初始化证书信息
		function initStaffCertificateTable(){
			table.render({
			    id: 'sysStaffCertificateTable',
			    elem: '#sysStaffCertificateTable',
			    method: 'post',
			    url: reqBasePath + 'sysstaffcertificate006',
			    where: {staffId: staffId},
			    even: true,
			    page: true,
			    limits: getLimits(),
		    	limit: getLimit(),
			    cols: [[
			        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
			        { field: 'certificateNumber', title: '证书编号', align: 'left', width: 160 },
			        { field: 'certificateName', title: '证书名称', width: 120},
			        { field: 'certificateTypeName', title: '证书类型', width: 120},
	                { field: 'issueOrgan', title: '签发机构', width: 120},
	                { field: 'jobNumber', title: '员工工号', align: 'left', width: 80 },
	                { field: 'userName', title: '员工姓名', align: 'left', width: 100 },
	                { field: 'state', title: '员工状态', align: 'center', width: 80, templet: function(d){
	                    if(d.state == '1'){
	                        return "在职";
	                    }else if(d.state == '2'){
	                        return "离职";
	                    }else if(d.state == '3'){
	                        return "见习";
	                    }else if(d.state == '4'){
	                        return "试用";
	                    }else if(d.state == '5'){
	                        return "退休";
	                    }
	                }},
	                { field: 'validityType', title: '有效期类型', align: 'left', width: 100, templet: function(d){
	                    if(d.validityType == '1'){
	                        return "永久有效";
	                    }else if(d.validityType == '2'){
	                        return "时间段有效";
	                    }
	                }},
	                { field: 'issueTime', title: '签发时间', align: 'center', width: 100 },
	                { field: 'validityTime', title: '截至时间', align: 'center', width: 100 },
	                { field: 'createTime', title: '录入时间', align: 'center', width: 100}
			    ]]
			});
		}
		
		initStaffRewardTable();
		// 初始化奖惩信息
		function initStaffRewardTable(){
			table.render({
			    id: 'sysStaffRewardTable',
			    elem: '#sysStaffRewardTable',
			    method: 'post',
			    url: reqBasePath + 'sysstaffrewardpunish006',
			    where: {staffId: staffId},
			    even: true,
			    page: true,
			    limits: getLimits(),
		    	limit: getLimit(),
			    cols: [[
			        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
			        { field: 'name', title: '名称', align: 'left', width: 160 },
			        { field: 'price', title: '奖惩金额', align: 'left', width: 100},
			        { field: 'content', title: '奖惩内容', align: 'left', width: 160},
	                { field: 'rewardPunishTypeName', title: '奖惩分类', align: 'left', width: 100 },
	                { field: 'rewardPunishTime', title: '奖惩时间', align: 'center', width: 100},
	                { field: 'awardUnit', title: '授予单位', align: 'left', width: 120},
	                { field: 'desc', title: '备注', align: 'left', width: 160 },
	                { field: 'jobNumber', title: '员工工号', align: 'left', width: 80 },
	                { field: 'userName', title: '员工姓名', align: 'left', width: 100 },
	                { field: 'state', title: '员工状态', align: 'center', width: 80, templet: function(d){
	                    if(d.state == '1'){
	                        return "在职";
	                    }else if(d.state == '2'){
	                        return "离职";
	                    }else if(d.state == '3'){
	                        return "见习";
	                    }else if(d.state == '4'){
	                        return "试用";
	                    }else if(d.state == '5'){
	                        return "退休";
	                    }
	                }},
	                { field: 'createTime', title: '录入时间', align: 'center', width: 100}
			    ]]
			});
		}
		
		initStaffContractTable();
		// 初始化合同信息
		function initStaffContractTable(){
			table.render({
			    id: 'sysStaffContractTable',
			    elem: '#sysStaffContractTable',
			    method: 'post',
			    url: reqBasePath + 'sysstaffcontract006',
			    where: {staffId: staffId},
			    even: true,
			    page: true,
			    limits: getLimits(),
		    	limit: getLimit(),
			    cols: [[
			        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
			        { field: 'contractNumber', title: '合同编号', align: 'left', width: 160 },
					{ field: 'companyName', title: '签属企业', width: 180},
			        { field: 'startTime', title: '开始日期', align: 'center', width: 100},
			        { field: 'endTime', title: '结束日期', align: 'center', width: 100},
	                { field: 'typeName', title: '合同类别', width: 120},
	                { field: 'moldName', title: '合同类型', width: 120},
	                { field: 'contractState', title: '状态', align: 'left', width: 80, templet: function(d){
	                    if(d.contractState == '1'){
	                        return "待签约";
	                    }else if(d.contractState == '2'){
	                        return "执行中";
	                    }else if(d.contractState == '3'){
	                        return "过期";
	                    }
	                }},
	                { field: 'jobNumber', title: '员工工号', align: 'left', width: 80 },
	                { field: 'userName', title: '员工姓名', align: 'left', width: 100 },
	                { field: 'state', title: '员工状态', align: 'center', width: 80, templet: function(d){
	                    if(d.state == '1'){
	                        return "在职";
	                    }else if(d.state == '2'){
	                        return "离职";
	                    }else if(d.state == '3'){
	                        return "见习";
	                    }else if(d.state == '4'){
	                        return "试用";
	                    }else if(d.state == '5'){
	                        return "退休";
	                    }
	                }},
	                { field: 'createTime', title: '录入时间', align: 'center', width: 100}
			    ]]
			});
		}
		
	    $("body").on("click", "#userPhoto", function(){
	    	var src = $(this).attr("src");
	    	layer.open({
        		type: 1,
        		title: false,
        		closeBtn: 0,
        		skin: 'demo-class',
        		shadeClose: true,
        		content: '<img src="' + src + '" style="max-height:600px;max-width:100%;">',
        		scrollbar: false
            });
	    });
	    
	    $("body").on("click", ".workDay", function(){
	    	rowId = $(this).attr("rowId");
	    	_openNewWindows({
				url: "../../tpl/checkWorkTime/checkWorkTimeDetails.html", 
				title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
				pageId: "checkWorkTimeDetails",
				area: ['90vw', '90vh'],
				callBack: function(refreshCode){
				}});
	    });
	    
	});
});