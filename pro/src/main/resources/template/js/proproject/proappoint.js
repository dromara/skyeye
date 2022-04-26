
var userReturnList = new Array();//选择用户返回的集合或者进行回显的集合
var chooseOrNotMy = "1";//人员列表中是否包含自己--1.包含；其他参数不包含
var chooseOrNotEmail = "2";//人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
var checkType = "1";//人员选择类型，1.多选；其他。单选

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
	    
	    var projectEnclosureInfoList = new Array();//项目组织和分工的附件
	    var planEnclosureInfoList = new Array();//实施计划和方案的附件
	    
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

 	        	ue = UE.getEditor('container',{
			    	//初始化高度
			    	initialFrameHeight: 400,
			    	maximumWords: 100000
			    });
			    planUe =  UE.getEditor('planContainer',{
			    	//初始化高度
			    	initialFrameHeight: 400,
			    	maximumWords: 100000
			    });
			    UE.Editor.prototype._bkGetActionUrl = UE.Editor.prototype.getActionUrl;
			    UE.Editor.prototype.getActionUrl = function(action){
			        if (action == 'uploadimage' || action == 'uploadfile' || action == 'uploadvideo' || action == 'uploadimage'){//上传单个图片,上传附件,上传视频,多图上传
			            return reqBasePath + '/upload/editUploadController/uploadContentPic';
			        } else if(action == 'listimage'){
			        	return reqBasePath + '/upload/editUploadController/downloadContentPic';
			        }else{
			            return this._bkGetActionUrl.call(this, action);
			        }
			    };
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
			userReturnList = [].concat(toProjectManager);
			_openNewWindows({
				url: "../../tpl/common/sysusersel.html", 
				title: "人员选择",
				pageId: "sysuserselpage",
				area: ['80vw', '80vh'],
				callBack: function(refreshCode){
					if (refreshCode == '0') {
						//移除所有tag
						var tags = $('#projectManager').tagEditor('getTags')[0].tags;
						for (i = 0; i < tags.length; i++) { 
							$('#projectManager').tagEditor('removeTag', tags[i]);
						}
						toProjectManager = [].concat(userReturnList);
					    //添加新的tag
						$.each(toProjectManager, function(i, item){
							$('#projectManager').tagEditor('addTag', item.name);
						});
	                } else if (refreshCode == '-9999') {
	                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
	                }
				}});
		});
		
		//项目赞助人选择
		$("body").on("click", "#toProjectSponsor", function(e){
			userReturnList = [].concat(toProjectSponsor);
			_openNewWindows({
				url: "../../tpl/common/sysusersel.html", 
				title: "人员选择",
				pageId: "sysuserselpage",
				area: ['80vw', '80vh'],
				callBack: function(refreshCode){
					if (refreshCode == '0') {
						//移除所有tag
						var tags = $('#projectSponsor').tagEditor('getTags')[0].tags;
						for (i = 0; i < tags.length; i++) { 
							$('#projectSponsor').tagEditor('removeTag', tags[i]);
						}
						toProjectSponsor = [].concat(userReturnList);
					    //添加新的tag
						$.each(toProjectSponsor, function(i, item){
							$('#projectSponsor').tagEditor('addTag', item.name);
						});
	                } else if (refreshCode == '-9999') {
	                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
	                }
				}});
		});
		
		//项目组成员选择
		$("body").on("click", "#toProjectMembers", function(e){
			userReturnList = [].concat(toProjectMembers);
			_openNewWindows({
				url: "../../tpl/common/sysusersel.html", 
				title: "人员选择",
				pageId: "sysuserselpage",
				area: ['80vw', '80vh'],
				callBack: function(refreshCode){
					if (refreshCode == '0') {
						//移除所有tag
						var tags = $('#projectMembers').tagEditor('getTags')[0].tags;
						for (i = 0; i < tags.length; i++) { 
							$('#projectMembers').tagEditor('removeTag', tags[i]);
						}
						toProjectMembers = [].concat(userReturnList);
					    //添加新的tag
						$.each(toProjectMembers, function(i, item){
							$('#projectMembers').tagEditor('addTag', item.name);
						});
	                } else if (refreshCode == '-9999') {
	                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
	                }
				}});
		});
		
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
});