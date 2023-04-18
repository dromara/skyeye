
var companyId = "";
var departmentId = "";
var jobId = "";
var jobScoreId = "";

var type = "1";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'fileUpload', 'dtree', 'laydate'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	laydate = layui.laydate,
	    	dtree = layui.dtree;
		var selTemplate = getFileContent('tpl/template/select-option.tpl');
		type = GetUrlParam("type");

		// 员工在职状态加载
		$("#state").html(getDataUseHandlebars(selTemplate, {rows: systemCommonUtil.getSysUserStaffStateList()}));
		form.on('select(state)', function (data) {
			if (data.value == '4') {
				// 试用期
				$("#trialTimeBox").show();
			} else {
				$("#trialTimeBox").hide();
			}
		});

	    // 入职时间
		laydate.render({elem: '#entryTime', range: false});

		// 参加工作时间
		laydate.render({elem: '#workTime', range: false});

		// 预计试用结束日期
		laydate.render({elem: '#trialTime', range: false});

		// 初始化上传
		$("#userPhoto").upload(systemCommonUtil.uploadCommon003Config('userPhoto', 6, '', 1));

 		// 初始化公司
 		dtree.render({
 			elem: "#demoTree1",
 			url: reqBasePath + 'queryCompanyMationListTree',
 			dataStyle: 'layuiStyle',
 			done: function(json) {
 				if($("#demoTree1 li").length > 0){
 					$("#demoTree1 li").eq(0).children('div').click();
 				}
 			}
 		});
 		
 		dtree.on("node('demoTree1')" ,function(param){
 			companyId = param.nodeId;
 			// 初始化部门
 			dtree.render({
 				elem: "#demoTree2",
 				url: reqBasePath + 'companydepartment006?companyId=' + companyId,
 				dataStyle: 'layuiStyle',
 				done: function(json) {
 					departmentId = "";
 					if($("#demoTree2 li").length > 0){
 						$("#demoTree2 li").eq(0).children('div').click();
 					} else {
 						jobId = "";
 						//初始化职位
 			 			dtree.render({
 			 				elem: "#demoTree3",  //绑定元素
 			 				url: reqBasePath + 'companyjob006?departmentId=0',
 			 				dataStyle: 'layuiStyle',
 			 				done: function(json) {
 			 				}
 			 			});
 					}
 				}
 			});
 		});
 		
 		dtree.on("node('demoTree2')" ,function(param){
 			departmentId = param.nodeId;
 			// 初始化职位
 			dtree.render({
 				elem: "#demoTree3",
 				url: reqBasePath + 'companyjob006?departmentId=' + departmentId,
 				dataStyle: 'layuiStyle',
 				done: function(json) {
 					jobId = "";
 					if($("#demoTree3 li").length > 0){
 						$("#demoTree3 li").eq(0).children('div').click();
 					}
 				}
 			});
 		});
 		
 		dtree.on("node('demoTree3')" ,function(param){
 			jobId = param.nodeId;
			// 初始化职位定级
			dtree.render({
				elem: "#demoTree4",
				url: reqBasePath + 'companyjobscore008?jobId=' + jobId,
				dataStyle: 'layuiStyle',
				method: 'GET',
				done: function(json) {
					jobScoreId = "";
					if($("#demoTree4 li").length > 0){
						$("#demoTree4 li").eq(0).children('div').click();
					}
				}
			});
 		});

		dtree.on("node('demoTree4')" ,function(param){
			jobScoreId = param.nodeId;
		});
 		
 		showGrid({
	     	id: "checkTimeBox",
	     	url: sysMainMation.checkworkBasePath + "queryEnableCheckWorkTimeList",
	     	params: {},
			method: 'GET',
	     	pagination: false,
	     	template: $("#checkTimeStrTemplate").html(),
	     	ajaxSendLoadBefore: function(hdb) {
	     	},
	     	ajaxSendAfter:function (json) {
	     		form.render('checkbox');
	     	}
	    });
 	
	    matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	if(isNull(companyId)){
 	        		winui.window.msg('请选择所属公司', {icon: 2, time: 2000});
 	        		return false;
 	        	}
 	        	if(isNull(departmentId)){
 	        		winui.window.msg('请选择所属部门', {icon: 2, time: 2000});
 	        		return false;
 	        	}
 	        	if(isNull(jobId)){
 	        		winui.window.msg('请选择职位信息', {icon: 2, time: 2000});
 	        		return false;
 	        	}
 	        	
 	        	var timeIds = "";
	        	$.each($('input:checkbox:checked'),function(){
	        		timeIds = timeIds + $(this).attr("rowId") + ",";
	            });
	            if(isNull(timeIds)){
 	        		winui.window.msg('请选择考勤段', {icon: 2, time: 2000});
 	        		return false;
 	        	}
	            var state = $("#state").val();
	            if(state == '4' && isNull($("#trialTime").val())) {
					winui.window.msg('请选择预计试用结束日期', {icon: 2, time: 2000});
					return false;
				}
 	        	var params = {
 	        		userName: $("#userName").val(),
 	        		email: $("#email").val(),
 	        		userIdCard: $("#userIdCard").val(),
 	        		userSex: $("input[name='userSex']:checked").val(),
 	        		phone: $("#phone").val(),
 	        		homePhone: $("#homePhone").val(),
 	        		qq: $("#qq").val(),
 	        		userSign: $("#userSign").val(),
					workTime: $("#workTime").val(),
 	        		entryTime: $("#entryTime").val(),
 	        		companyId: companyId,
 	        		departmentId: departmentId,
 	        		jobId: jobId,
					jobScoreId: jobScoreId,
 	        		checkTimeStr: timeIds,
					type: type,
					state: state,
					trialTime: $("#trialTime").val(),
					userPhoto: $("#userPhoto").find("input[type='hidden'][name='upload']").attr("oldurl")
 	        	};
 	        	if(isNull(params.userPhoto)){
 	        		winui.window.msg('请上传头像', {icon: 2, time: 2000});
 	        		return false;
 	        	}
 	        	
 	        	AjaxPostUtil.request({url: reqBasePath + "staff002", params: params, type: 'json', callback: function (json) {
					parent.layer.close(index);
					parent.refreshCode = '0';
	 	   		}});
 	        }
 	        return false;
 	    });
 	    
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});