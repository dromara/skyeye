
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

 		// 初始化上传
 		$("#assetImg").upload({
            "action": reqBasePath + "common003",
            "data-num": "1",
            "data-type": "PNG,JPG,jpeg,gif",
            "uploadType": 6,
            "function": function (_this, data) {
                show("#assetImg", data);
            }
        });
 		
 		// 生产日期
 		laydate.render({elem: '#manufacturerTime', type: 'date', max: getYMDFormatDate(), trigger: 'click'});
 		
 		// 采购日期
 		laydate.render({elem: '#purchaseTime', type: 'date', max: getYMDFormatDate(), trigger: 'click'});
 		
 		//资产类型
 		showGrid({
		 	id: "typeId",
		 	url: flowableBasePath + "assettype006",
		 	params: {},
		 	pagination: false,
		 	template: getFileContent('tpl/template/select-option-must.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function(json){
		 		form.render('select');
		 	}
		});
 		
 		//资产来源
 		showGrid({
		 	id: "fromId",
		 	url: flowableBasePath + "assetfrom006",
		 	params: {},
		 	pagination: false,
		 	template: getFileContent('tpl/template/select-option-must.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function(json){
		 		form.render('select');
		 	}
		});

		skyeyeEnclosure.init('enclosureUpload');
		matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	var params = {
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
 	        	if(isNull(params.assetImg)){
 	        		winui.window.msg('请上传资产图片', {icon: 2, time: 2000});
 	        		return false;
 	        	}
 	        	AjaxPostUtil.request({url: flowableBasePath + "asset002", params:params, type: 'json', callback: function(json){
	 	   			if (json.returnCode == 0) {
		 	   			parent.layer.close(index);
		 	        	parent.refreshCode = '0';
	 	   			} else {
	 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	 	   			}
	 	   		}});
 	        }
 	        return false;
 	    });

 	    //资产管理人
	    $('#assetAdmin').tagEditor({
	        initialTags: [],
	        placeholder: '请选择资产管理人',
			editorTag: false,
	        beforeTagDelete: function(field, editor, tags, val) {
				userList = [].concat(arrayUtil.removeArrayPointName(userList, val));
	        }
	    });
	    //资产管理人选择
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
		 //资产领用人
	    $('#employeeId').tagEditor({
	        initialTags: [],
	        placeholder: '请选择资产领用人',
			editorTag: false,
	        beforeTagDelete: function(field, editor, tags, val) {
				employeeuserList = [].concat(arrayUtil.removeArrayPointName(employeeuserList, val));
	        }
	    });
	    //资产领用人选择
		$("body").on("click", "#employeePeople", function(e){
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