
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
		 	ajaxSendLoadBefore: function(hdb) {
		 	},
		 	ajaxSendAfter: function (json) {
				// 附件回显
				skyeyeEnclosure.initTypeISData({
					'projectEnclosureUpload': json.bean.projectEnclosureInfoList,// 项目组织和分工的附件回显
					'planEnclosureUpload': json.bean.planEnclosureInfoList // 实施计划和方案的附件回显
				});

				// 业务需求和目标的附件回显
				skyeyeEnclosure.showDetails({"businessEnclosureUpload": json.bean.businessEnclosureInfoList});

				ue = ueEditorUtil.initEditor('container');
			    ue.addListener("ready", function () {
			    	if (!isNull(json.bean.projectContent))
			    		ue.setContent(json.bean.projectContent);
			    	else
			    		ue.setContent("在此处填写您的[分工明细]");
			    });

				planUe = ueEditorUtil.initEditor('planContainer');
			    planUe.addListener("ready", function () {
			    	if (!isNull(json.bean.planContent))
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
						toProjectManager = [].concat(arrayUtil.removeArrayPointName(toProjectManager, val));
			        }
			    });
			    
			    $('#projectSponsor').tagEditor({
			        initialTags: toProjectSponsorName.split(','),
			        placeholder: '请选择项目赞助人',
					editorTag: false,
			        beforeTagDelete: function(field, editor, tags, val) {
						toProjectSponsor = [].concat(arrayUtil.removeArrayPointName(toProjectSponsor, val));
			        }
			    });
			    
			    $('#projectMembers').tagEditor({
			        initialTags: toProjectMembersName.split(','),
			        placeholder: '请选择项目组成员',
					editorTag: false,
			        beforeTagDelete: function(field, editor, tags, val) {
						toProjectMembers = [].concat(arrayUtil.removeArrayPointName(toProjectMembers, val));
			        }
			    });
			    
			    matchingLanguage();
		 		form.render();
				form.on('submit(formAppointBean)', function (data) {
					if (winui.verifyForm(data.elem)) {
						var params = {
							rowId: parent.rowId,
							toProjectManager: systemCommonUtil.tagEditorGetAllData('projectManager', toProjectManager), // 项目经理
							toProjectSponsor: systemCommonUtil.tagEditorGetAllData('projectSponsor', toProjectSponsor), // 项目赞助人
							toProjectMembers: systemCommonUtil.tagEditorGetAllData('projectMembers', toProjectMembers), // 项目组成员
							projectEnclosureInfoStr: skyeyeEnclosure.getEnclosureIdsByBoxId('projectEnclosureUpload'),
							planEnclosureInfoStr: skyeyeEnclosure.getEnclosureIdsByBoxId('planEnclosureUpload'),
							projectContent: encodeURIComponent(ue.getContent()),
							planContent: encodeURIComponent(planUe.getContent())
						};
						if (isNull(params.toProjectManager)) {
							winui.window.msg("请选择项目经理", {icon: 2, time: 2000});
							return false;
						}
						if (isNull(params.toProjectMembers)) {
							winui.window.msg("请选择项目成员", {icon: 2, time: 2000});
							return false;
						}
						if (isNull(params.projectContent)) {
							winui.window.msg("请填写业务需求和目标", {icon: 2, time: 2000});
							return false;
						}
						AjaxPostUtil.request({url: flowableBasePath + "proproject014", params: params, type: 'json', callback: function (json) {
							parent.layer.close(index);
							parent.refreshCode = '0';
						}});
					}
					return false;
				});
		 	}
		});
		
		//项目经理选择
		$("body").on("click", "#toProjectManager", function (e) {
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
		$("body").on("click", "#toProjectSponsor", function (e) {
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
		$("body").on("click", "#toProjectMembers", function (e) {
			systemCommonUtil.userReturnList = [].concat(toProjectMembers);
			systemCommonUtil.chooseOrNotMy = "1"; // 人员列表中是否包含自己--1.包含；其他参数不包含
			systemCommonUtil.chooseOrNotEmail = "2"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
			systemCommonUtil.checkType = "1"; // 人员选择类型，1.多选；其他。单选
			systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList) {
				// 重置数据
				toProjectMembers = [].concat(systemCommonUtil.tagEditorResetData('projectMembers', userReturnList));
			});
		});
		
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});