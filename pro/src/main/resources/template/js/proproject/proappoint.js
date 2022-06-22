
// 项目任命
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'tagEditor'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    
	    var ue, planUe;
	    
	    var toProjectManager = new Array();//项目经理
	    var toProjectSponsor = new Array();//项目赞助人
	    var toProjectMembers = new Array();//项目组成员
	    
	    showGrid({
		 	id: "showForm",
		 	url: flowableBasePath + "proproject013",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: $("#beanTemplate").html(),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter: function(json){
				// 附件回显
				skyeyeEnclosure.initTypeISData({
					'projectEnclosureUpload': json.bean.projectEnclosureInfoList,// 项目组织和分工的附件回显
					'planEnclosureUpload': json.bean.planEnclosureInfoList // 实施计划和方案的附件回显
				});

				// 业务需求和目标的附件回显
				var str = "";
				$.each([].concat(json.bean.businessEnclosureInfoList), function(i, item){
					str += '<a rowid="' + item.id + '" class="enclosureItem" rowpath="' + item.fileAddress + '" href="javascript:;" style="color:blue;">' + item.name + '</a><br>';
				});
				$("#businessEnclosureUpload").html(str);

				ue = ueEditorUtil.initEditor('container');
				planUe = ueEditorUtil.initEditor('planContainer');
			    ue.addListener("ready", function () {
			    	if(!isNull(json.bean.projectContent))
			    		ue.setContent(json.bean.projectContent);
			    	else
			    		ue.setContent("在此处填写您的[分工明细]");
			    });
			    
			    planUe.addListener("ready", function () {
			    	if(!isNull(json.bean.planContent))
			    		planUe.setContent(json.bean.planContent);
			    	else
			    		planUe.setContent("在此处填写您的[实施计划和方案]");
			    });
			    
			    //初始化项目经理
			    toProjectManager = [].concat(json.bean.projectManagerList);
			    var toProjectManagerName = "";
   				$.each(json.bean.projectManagerList, function(i, item){
   					toProjectManagerName += item.name + ',';
   				});
   				
   				//初始化项目赞助人
			    toProjectSponsor = [].concat(json.bean.projectSponsorList);
   				var toProjectSponsorName = "";
			    $.each(json.bean.projectSponsorList, function(i, item){
   					toProjectSponsorName += item.name + ',';
   				});
   				
   				//初始化项目组成员
   				toProjectMembers = [].concat(json.bean.projectMembersList);
   				var toProjectMembersName = "";
			    $.each(json.bean.projectMembersList, function(i, item){
   					toProjectMembersName += item.name + ',';
   				});
   				
			    $('#projectManager').tagEditor({
			        initialTags: toProjectManagerName.split(','),
			        placeholder: '请选择项目经理',
					editorTag: false,
			        beforeTagDelete: function(field, editor, tags, val) {
			        	var inArray = -1;
				    	$.each(toProjectManager, function(i, item) {
				    		if(val === item.name) {
				    			inArray = i;
				    			return false;
				    		}
				    	});
				    	if(inArray != -1) { //如果该元素在集合中存在
				    		toProjectManager.splice(inArray, 1);
				    	}
			        }
			    });
			    
			    $('#projectSponsor').tagEditor({
			        initialTags: toProjectSponsorName.split(','),
			        placeholder: '请选择项目赞助人',
					editorTag: false,
			        beforeTagDelete: function(field, editor, tags, val) {
			        	var inArray = -1;
				    	$.each(toProjectSponsor, function(i, item) {
				    		if(val === item.name) {
				    			inArray = i;
				    			return false;
				    		}
				    	});
				    	if(inArray != -1) { //如果该元素在集合中存在
				    		toProjectSponsor.splice(inArray, 1);
				    	}
			        }
			    });
			    
			    $('#projectMembers').tagEditor({
			        initialTags: toProjectMembersName.split(','),
			        placeholder: '请选择项目组成员',
					editorTag: false,
			        beforeTagDelete: function(field, editor, tags, val) {
			        	var inArray = -1;
				    	$.each(toProjectMembers, function(i, item) {
				    		if(val === item.name) {
				    			inArray = i;
				    			return false;
				    		}
				    	});
				    	if(inArray != -1) { //如果该元素在集合中存在
				    		toProjectMembers.splice(inArray, 1);
				    	}
			        }
			    });
			    
			    matchingLanguage();
		 		form.render();
		 	    form.on('submit(formAppointBean)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	var toProjectManagerIdStr = "", toProjectSponsorIdStr = "", toProjectMembersIdStr = "";
		 	        	//获取项目经理id串
	 	 	        	if(toProjectManager.length == 0 && isNull($('#projectManager').tagEditor('getTags')[0].tags)){
		 	        		winui.window.msg('请选择项目经理', {icon: 2,time: 2000});
		 	        		return false;
		 	        	}else{
		 	        		$.each(toProjectManager, function(i, item){
		 	        			toProjectManagerIdStr += item.id + ',';
			 	        	});
		 	        	}
		 	        	//获取项目赞助人id串
		 	        	$.each(toProjectSponsor, function(i, item){
	 	        			toProjectSponsorIdStr += item.id + ',';
		 	        	});
		 	        	//获取项目组成员id串
		 	        	if(toProjectMembers.length == 0 && isNull($('#projectMembers').tagEditor('getTags')[0].tags)){
		 	        		winui.window.msg('请选择项目组成员', {icon: 2,time: 2000});
		 	        		return false;
		 	        	}else{
		 	        		$.each(toProjectMembers, function(i, item){
		 	        			toProjectMembersIdStr += item.id + ',';
			 	        	});
		 	        	}
		 	        	var params = {
	 	        			rowId: parent.rowId,
	 	        			toProjectManager: toProjectManagerIdStr,
	 	        			toProjectSponsor: toProjectSponsorIdStr,
	 	        			toProjectMembers: toProjectMembersIdStr,
							projectEnclosureInfoStr: skyeyeEnclosure.getEnclosureIdsByBoxId('projectEnclosureUpload'),
							planEnclosureInfoStr: skyeyeEnclosure.getEnclosureIdsByBoxId('planEnclosureUpload')
	 	 	        	};
	 	 	        	//获取内容
		 	        	params.projectContent = encodeURIComponent(ue.getContent());
		 	        	if(isNull(params.projectContent)){
			        		winui.window.msg("请填写业务需求和目标", {icon: 2,time: 2000});
			        		return false;
			        	}
		 	        	params.planContent = encodeURIComponent(planUe.getContent());
	 	 	        	AjaxPostUtil.request({url: flowableBasePath + "proproject014", params: params, type: 'json', callback: function(json){
	 		 	   			if (json.returnCode == 0){
	 			 	   			parent.layer.close(index);
	 			 	        	parent.refreshCode = '0';
	 		 	   			} else {
	 		 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	 		 	   			}
	 		 	   		}});
		 	        }
		 	        return false;
		 	    });
		 	}
		});
		
		//项目经理选择
		$("body").on("click", "#toProjectManager", function(e){
			systemCommonUtil.userReturnList = [].concat(toProjectManager);
			systemCommonUtil.chooseOrNotMy = "1"; // 人员列表中是否包含自己--1.包含；其他参数不包含
			systemCommonUtil.chooseOrNotEmail = "2"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
			systemCommonUtil.checkType = "1"; // 人员选择类型，1.多选；其他。单选
			systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList) {
				// 重置数据
				toProjectManager = [].concat(systemCommonUtil.tagEditorResetData('projectManager', userReturnList));
			});
		});
		
		//项目赞助人选择
		$("body").on("click", "#toProjectSponsor", function(e){
			systemCommonUtil.userReturnList = [].concat(toProjectSponsor);
			systemCommonUtil.chooseOrNotMy = "1"; // 人员列表中是否包含自己--1.包含；其他参数不包含
			systemCommonUtil.chooseOrNotEmail = "2"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
			systemCommonUtil.checkType = "1"; // 人员选择类型，1.多选；其他。单选
			systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList) {
				// 重置数据
				toProjectSponsor = [].concat(systemCommonUtil.tagEditorResetData('projectSponsor', userReturnList));
			});
		});
		
		//项目组成员选择
		$("body").on("click", "#toProjectMembers", function(e){
			systemCommonUtil.userReturnList = [].concat(toProjectMembers);
			systemCommonUtil.chooseOrNotMy = "1"; // 人员列表中是否包含自己--1.包含；其他参数不包含
			systemCommonUtil.chooseOrNotEmail = "2"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
			systemCommonUtil.checkType = "1"; // 人员选择类型，1.多选；其他。单选
			systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList) {
				// 重置数据
				toProjectMembers = [].concat(systemCommonUtil.tagEditorResetData('projectMembers', userReturnList));
			});
		});
		
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
});