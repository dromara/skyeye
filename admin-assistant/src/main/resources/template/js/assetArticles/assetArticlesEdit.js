
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
		 	url: flowableBasePath + "assetarticles013",
		 	params: {id: parent.rowId},
		 	pagination: false,
			method: 'GET',
		 	template: $("#beanTemplate").html(),
		 	ajaxSendLoadBefore: function(hdb) {
		 		hdb.registerHelper("compare1", function(v1, options) {
					if (isNull(v1)) {
						return path + "assets/img/uploadPic.png";
					} else {
						return basePath + v1;
					}
				});
		 	},
		 	ajaxSendAfter:function(json) {
				// 用品类别
				sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["admAssetArticlesType"]["key"], 'select', "typeId", json.bean.typeId, form);

		 		var userNames = [];
		 		userList = [].concat(json.bean.assetAdminMation);
		 		$.each(json.bean.assetAdminMation, function(i, item) {
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
	 	        			id: parent.rowId,
							name: $("#name").val(),
	 	        			typeId: $("#typeId").val(),
	 	        			specifications: $("#specifications").val(),
	 	        			unitOfMeasurement: $("#unitOfMeasurement").val(),
	 	        			initialNum: json.bean.initialNum,
	 	        			residualNum: $("#residualNum").val(),
	 	        			storageArea: $("#storageArea").val(),
							remark: $("#remark").val(),
							enclosureInfo: JSON.stringify({enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload')}),
							assetAdmin: systemCommonUtil.tagEditorGetItemData('assetAdmin', userList)
	 	 	        	};
	 	 	        	AjaxPostUtil.request({url: flowableBasePath + "writeAssetArticlesMation", params: params, type: 'json', method: "POST", callback: function (json) {
							parent.layer.close(index);
							parent.refreshCode = '0';
	 		 	   		}});
		 	        }
		 	        return false;
		 	    });
		 	}
		});
 		
 		// 管理人员选择
		$("body").on("click", "#userNameSelPeople", function (e) {
			systemCommonUtil.userReturnList = [].concat(userList);
			systemCommonUtil.chooseOrNotMy = "1"; // 人员列表中是否包含自己--1.包含；其他参数不包含
			systemCommonUtil.chooseOrNotEmail = "2"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
			systemCommonUtil.checkType = "2"; // 人员选择类型，1.多选；其他。单选
			systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList) {
				// 重置数据
				userList = [].concat(systemCommonUtil.tagEditorResetData('assetAdmin', userReturnList));
			});
		});
	    
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});