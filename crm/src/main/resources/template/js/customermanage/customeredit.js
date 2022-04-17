
var userList = new Array();// 选择用户返回的集合或者进行回显的集合
var userReturnList = new Array();// 选择用户返回的集合或者进行回显的集合
var chooseOrNotMy = "1";// 人员列表中是否包含自己--1.包含；其他参数不包含
var chooseOrNotEmail = "2";// 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
var checkType = "2";// 人员选择类型，1.多选；其他。单选

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
	    
	    showGrid({
		 	id: "showForm",
		 	url: flowableBasePath + "customer003",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/customermanage/customereditTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter: function(json){
		 		// 获取客户类型状态为上线的所有记录
				sysCustomerUtil.queryCustomerTypeIsUpList(function (data){
					$("#typeId").html(getDataUseHandlebars(selectMust, data));
					$("#typeId").val(json.bean.typeId);
					form.render('select');
				});

				// 获取已上线的客户来源类型
				sysCustomerUtil.queryCustomerFromIsUpList(function (data){
					$("#fromId").html(getDataUseHandlebars(selectMust, data));
					$("#fromId").val(json.bean.fromId);
					form.render('select');
					customerIndustry();
				});

		 		// 行业
		 		function customerIndustry(){
		 			showGrid({
		 			 	id: "industryId",
		 			 	url: flowableBasePath + "crmcustomerindustry008",
		 			 	params: {},
		 			 	pagination: false,
		 			 	template: selectMust,
		 			 	ajaxSendLoadBefore: function(hdb){
		 			 	},
		 			 	ajaxSendAfter:function(j){
		 			 		$("#industryId").val(json.bean.industryId);
		 			 		form.render('select');
		 			 		customerGroup();
		 			 	}
		 			});
		 		}
		 		
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
		 			 	ajaxSendAfter:function(data){
		 			 		$("#groupId").val(json.bean.groupId);
		 			 		form.render('select');
		 			 	}
		 			});
		 		}
		 		
		 		textool.init({
			    	eleId: 'addDesc',
			    	maxlength: 200,
			    	tools: ['count', 'copy', 'reset']
			    });
		 		
		 		var userNames = [];
		 		userList = [].concat(json.bean.chargeUser);
		 		$.each(json.bean.chargeUser, function(i, item){
		 			$("#relationUserId").val(item.name);
		 		});
		 		
		 		// 附件回显
				skyeyeEnclosure.initTypeISData({'enclosureUpload': json.bean.enclosureInfo});

 	        	matchingLanguage();
		 		form.render();
		 	    form.on('submit(formEditBean)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	var params = {
	 	        			rowId: parent.rowId,
	 	        			name: $("#name").val(),
	 	 	        		combine: $("#combine").val(),
	 	 	        		typeId: $("#typeId").val(),
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
	 	        		
	 	 	        	AjaxPostUtil.request({url: flowableBasePath + "customer004", params: params, type: 'json', callback: function(json){
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
		
		// 负责人选择
		$("body").on("click", "#userNameSelPeople", function(e){
			userReturnList = [].concat(userList);
			_openNewWindows({
				url: "../../tpl/common/sysusersel.html", 
				title: "人员选择",
				pageId: "sysuserselpage",
				area: ['90vw', '90vh'],
				callBack: function(refreshCode){
					if (refreshCode == '0') {
						userList = [].concat(userReturnList);
					    //添加选择
						$.each(userList, function(i, item){
							$("#relationUserId").val(item.name);
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