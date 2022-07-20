
var userList = new Array();//选择用户返回的集合或者进行回显的集合
var employeeuserList = new Array();//选择用户返回的集合或者进行回显的集合

// 资产信息
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'fileUpload', 'tagEditor', 'laydate'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	laydate = layui.laydate;

	    showGrid({
		 	id: "showForm",
		 	url: flowableBasePath + "asset004",
		 	params: {rowId:parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/assetManage/assetManageEditTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 		hdb.registerHelper("compare1", function(v1, options){
					if(isNull(v1)){
						return path + "assets/img/uploadPic.png";
					} else {
						return basePath + v1;
					}
				});
		 	},
		 	ajaxSendAfter:function (json) {
		 		// 生产日期
		 		laydate.render({elem: '#manufacturerTime', type: 'date', max: getFormatDate(), trigger: 'click'});
		 		
		 		// 采购日期
		 		laydate.render({elem: '#purchaseTime', type: 'date', max: getFormatDate(), trigger: 'click'});

				// 资产类型
				sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["admAssetType"]["key"], 'select', "typeId", json.bean.typeId, form);

				// 资产来源
				sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["admAssetFrom"]["key"], 'select', "fromId", json.bean.fromId, form);

				// 初始化上传
				$("#assetImg").upload(systemCommonUtil.uploadCommon003Config('assetImg', 6, json.bean.assetImg, 1));

		 		var userNames = [];
		 		userList = [].concat(json.bean.assetAdmin);
		 		$.each(json.bean.assetAdmin, function(i, item){
		 			userNames.push(item.name);
		 		});
		 		// 管理人员选择
				$('#assetAdmin').tagEditor({
			        initialTags: userNames,
			        placeholder: '请选择资产管理人',
					editorTag: false,
			        beforeTagDelete: function(field, editor, tags, val) {
						userList = [].concat(arrayUtil.removeArrayPointName(userList, val));
			        }
			    });
				
				var employeeuserNames = [];
		 		employeeuserList = [].concat(json.bean.employeeId);
		 		$.each(json.bean.employeeId, function(i, item){
		 			employeeuserNames.push(item.name);
		 		});
				// 领用人选择
				$('#employeeId').tagEditor({
			        initialTags: employeeuserNames,
			        placeholder: '请选择领用人',
					editorTag: false,
			        beforeTagDelete: function(field, editor, tags, val) {
						employeeuserList = [].concat(arrayUtil.removeArrayPointName(employeeuserList, val));
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
	 	        			assetName: $("#assetName").val(),
	 	        			assetNum: $("#assetNum").val(),
	 	        			specifications: $("#specifications").val(),
	 	        			unitPrice: $("#unitPrice").val(),
	 	        			manufacturer: $("#manufacturer").val(),
	 	        			manufacturerTime: $("#manufacturerTime").val(),
	 	        			supplier: $("#supplier").val(),
	 	        			purchaseTime: $("#purchaseTime").val(),
	 	        			storageArea: $("#storageArea").val(),
	 	        			roomAddDesc: $("#roomAddDesc").val(),
							typeId: $("#typeId").val(),
							fromId: $("#fromId").val(),
							enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload'),
							assetAdmin: systemCommonUtil.tagEditorGetItemData('assetAdmin', userList),
							employeeId: systemCommonUtil.tagEditorGetItemData('employeeId', employeeuserList),
							assetImg: $("#assetImg").find("input[type='hidden'][name='upload']").attr("oldurl")
	 	 	        	};
	 	 	        	if(isNull(params.assetImg)) {
	 	 	        		winui.window.msg('请上传资产图片', {icon: 2, time: 2000});
	 	 	        		return false;
	 	 	        	}
	 	 	        	AjaxPostUtil.request({url: flowableBasePath + "asset005", params: params, type: 'json', callback: function (json) {
							parent.layer.close(index);
							parent.refreshCode = '0';
	 		 	   		}});
		 	        }
		 	        return false;
		 	    });
		 	}
		});
	    
	    // 管理人员选择
		$("body").on("click", "#userNameSelPeople", function(e) {
			systemCommonUtil.userReturnList = [].concat(userList);
			systemCommonUtil.chooseOrNotMy = "1"; // 人员列表中是否包含自己--1.包含；其他参数不包含
			systemCommonUtil.chooseOrNotEmail = "2"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
			systemCommonUtil.checkType = "2"; // 人员选择类型，1.多选；其他。单选
			systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList) {
				// 重置数据
				userList = [].concat(systemCommonUtil.tagEditorResetData('assetAdmin', userReturnList));
			});
		});
		
		// 领用人选择
		$("body").on("click", "#employeePeople", function(e) {
			systemCommonUtil.userReturnList = [].concat(employeeuserList);
			systemCommonUtil.chooseOrNotMy = "1"; // 人员列表中是否包含自己--1.包含；其他参数不包含
			systemCommonUtil.chooseOrNotEmail = "2"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
			systemCommonUtil.checkType = "2"; // 人员选择类型，1.多选；其他。单选
			systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList) {
				// 重置数据
				employeeuserList = [].concat(systemCommonUtil.tagEditorResetData('employeeId', userReturnList));
			});
		});

	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});