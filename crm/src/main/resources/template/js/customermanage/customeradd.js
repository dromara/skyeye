
var userList = new Array();// 选择用户返回的集合或者进行回显的集合

// 新增客户
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'textool'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	textool = layui.textool;
	    var selectMust = getFileContent('tpl/template/select-option-must.tpl');

		// 获取客户类型状态为上线的所有记录
		sysCustomerUtil.queryCustomerTypeIsUpList(function (data){
			$("#typeId").html(getDataUseHandlebars(selectMust, data));
			form.render('select');
		});

		// 获取已上线的客户来源类型
		sysCustomerUtil.queryCustomerFromIsUpList(function (data){
			$("#fromId").html(getDataUseHandlebars(selectMust, data));
			form.render('select');
		});

		// 获取已上线的客户所属行业列表
		sysCustomerUtil.queryCrmCustomerIndustryIsUpList(function (data){
			$("#industryId").html(getDataUseHandlebars(selectMust, data));
			form.render('select');
			customerGroup();
		});

 		// 客户分组
 		function customerGroup(){
 			showGrid({
 			 	id: "groupId",
 			 	url: flowableBasePath + "customergroup008",
 			 	params: {},
 			 	pagination: false,
 			 	template: selectMust,
 			 	ajaxSendLoadBefore: function(hdb){
 			 	},
 			 	ajaxSendAfter:function(json){
 			 		form.render('select');
 			 	}
 			});
 		}
 		
 		textool.init({
	    	eleId: 'addDesc',
	    	maxlength: 200,
	    	tools: ['count', 'copy', 'reset']
	    });

		skyeyeEnclosure.init('enclosureUpload');
 		
	    matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	    	// 表单验证
 	        if (winui.verifyForm(data.elem)) {
 	        	var params = {
 	        		name: $("#name").val(),
 	        		combine: $("#combine").val(),
 	        		fromId: $("#fromId").val(),
 	        		industryId: $("#industryId").val(),
 	        		cusUrl: $("#cusUrl").val(),
 	        		country: $("#country").val(),
 	        		city: $("#city").val(),
 	        		detailAddress: $("#detailAddress").val(),
 	        		postalCode: $("#postalCode").val(),
 	        		fax: $("#fax").val(),
 	        		corRepresentative: $("#corRepresentative").val(),
 	        		regCapital: $("#regCapital").val(),
 	        		addDesc: $("#addDesc").val(),
 	        		bankAccount: $("#bankAccount").val(),
 	        		accountName: $("#accountName").val(),
 	        		bankName: $("#bankName").val(),
 	        		bankAddress: $("#bankAddress").val(),
 	        		dutyParagraph: $("#dutyParagraph").val(),
 	        		financePhone: $("#financePhone").val(),
 	        		mechanicName: $("#mechanicName").val(),
 	        		mechanicPhone: $("#mechanicPhone").val(),
 	        		groupId: $("#groupId").val(),
 	        		chargeUser: '',
					enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload')
 	        	};
 	        	params.typeId = data.field.typeId;
 	        	params.fromId = data.field.fromId;
 	        	params.industryId = data.field.industryId;
 	        	
        		// 负责人
        		if(userList.length > 0 ){
        			params.chargeUser = userList[0].id;
                }
        		
 	        	AjaxPostUtil.request({url: flowableBasePath + "customer002", params: params, type: 'json', callback: function(json){
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
 	    
	    // 负责人选择
		$("body").on("click", "#userNameSelPeople", function(e){
			systemCommonUtil.userReturnList = [].concat(userList);
			systemCommonUtil.chooseOrNotMy = "1"; // 人员列表中是否包含自己--1.包含；其他参数不包含
			systemCommonUtil.chooseOrNotEmail = "2"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
			systemCommonUtil.checkType = "2"; // 人员选择类型，1.多选；其他。单选
			systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList) {
				// 重置数据
				userList = [].concat(userReturnList);
				// 添加选择
				$.each(userList, function(i, item) {
					$("#relationUserId").val(item.name);
				});
			});
		});
 	    
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
});