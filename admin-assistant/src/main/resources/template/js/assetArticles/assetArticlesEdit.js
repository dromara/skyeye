
var userList = new Array();//选择用户返回的集合或者进行回显的集合

// 用品信息
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'fileUpload', 'tagEditor'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;

	    showGrid({
		 	id: "showForm",
		 	url: flowableBasePath + "assetarticles015",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/assetArticles/assetArticlesEditTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 		hdb.registerHelper("compare1", function(v1, options){
					if(isNull(v1)){
						return path + "assets/img/uploadPic.png";
					}else{
						return basePath + v1;
					}
				});
		 	},
		 	ajaxSendAfter:function(json) {
				// 获取已经上线的用品类别列表
				adminAssistantUtil.queryAssetArticlesTypeUpStateList(function (data){
					$("#typeId").html(getDataUseHandlebars(getFileContent('tpl/template/select-option.tpl'), data));
					$("#typeId").val(json.bean.typeId);
					form.render('select');
				});
		 		var userNames = [];
		 		userList = [].concat(json.bean.assetAdmin);
		 		$.each(json.bean.assetAdmin, function(i, item){
		 			userNames.push(item.name);
		 		});
		 		// 管理人员选择
				$('#assetAdmin').tagEditor({
			        initialTags: userNames,
			        placeholder: '请选择用品管理人',
			        editorTag: false,
			        beforeTagDelete: function(field, editor, tags, val) {
						userList = [].concat(arrayUtil.removeArrayPointName(userList, val));
			        }
			    });
				// 附件回显
				skyeyeEnclosure.initTypeISData({'enclosureUpload': json.bean.enclosureInfo});

    			matchingLanguage();
		 		form.render();
		 	    form.on('submit(formEditBean)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	var params = {
	 	        			rowId: parent.rowId,
	 	        			articlesName: $("#articlesName").val(),
	 	        			typeId: $("#typeId").val(),
	 	        			specifications: $("#specifications").val(),
	 	        			unitOfMeasurement: $("#unitOfMeasurement").val(),
	 	        			initialNum: $("#initialNum").val(),
	 	        			residualNum: $("#residualNum").val(),
	 	        			storageArea: $("#storageArea").val(),
	 	        			roomAddDesc: $("#roomAddDesc").val(),
							enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload')
	 	 	        	};
	 	 	        	if(userList.length == 0 || isNull($('#assetAdmin').tagEditor('getTags')[0].tags)){
	 	 	        		params.assetAdmin = "";
	 	 	        	}else{
	 	        			params.assetAdmin = userList[0].id;
	 	        		}
	 	 	        	AjaxPostUtil.request({url: flowableBasePath + "assetarticles016", params: params, type: 'json', callback: function(json){
	 		 	   			if(json.returnCode == 0){
	 			 	   			parent.layer.close(index);
	 			 	        	parent.refreshCode = '0';
	 		 	   			}else{
	 		 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	 		 	   			}
	 		 	   		}});
		 	        }
		 	        return false;
		 	    });
		 	}
		});
 		
 		// 管理人员选择
		$("body").on("click", "#userNameSelPeople", function(e){
			systemCommonUtil.userReturnList = [].concat(userList);
			systemCommonUtil.chooseOrNotMy = "1"; // 人员列表中是否包含自己--1.包含；其他参数不包含
			systemCommonUtil.chooseOrNotEmail = "2"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
			systemCommonUtil.checkType = "2"; // 人员选择类型，1.多选；其他。单选
			systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList) {
				// 重置数据
				userList = [].concat(systemCommonUtil.tagEditorResetData('assetAdmin', userReturnList));
			});
		});
	    
	    $("body").on("click", ".enclosureItem", function(){
	    	download(fileBasePath + $(this).attr("rowpath"), $(this).html());
	    });
	    
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
});