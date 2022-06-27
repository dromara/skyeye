
//工单接收人信息
var serviceUser = {};

//已经选择的客户信息
var customerMation = {};

// 工单信息
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'laydate', 'tagEditor', 'fileUpload', 'textool'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	laydate = layui.laydate,
	    	textool = layui.textool;
	    
	    var cooperationUser = new Array();//工单协助人集合

 		//报单时间
 		laydate.render({ 
			elem: '#declarationTime',
			type: 'datetime',
			trigger: 'click'
 		});
 		
 		//指定预约时间
 		laydate.render({ 
			elem: '#pointSubscribeTime',
			type: 'datetime',
			trigger: 'click'
 		});
 		
 		textool.init({
	    	eleId: 'content',
	    	maxlength: 200,
	    	tools: ['count', 'copy', 'reset']
	    });
 		
 		//初始化工单照片上传
 		$("#sheetPicture").upload({
            "action": reqBasePath + "common003",
            "data-num": "10",
            "data-type": "PNG,JPG,jpeg,gif",
            "uploadType": 14,
            "function": function (_this, data) {
                show("#sheetPicture", data);
            }
        });
	    
 		typeId();
 		
		//服务类型
 		function typeId(){
 	 		showGrid({
 			 	id: "typeId",
 			 	url: flowableBasePath + "sealseservicetype008",
 			 	params: {},
 			 	pagination: false,
 			 	template: getFileContent('tpl/template/select-option.tpl'),
 			 	ajaxSendLoadBefore: function(hdb){
 			 	},
 			 	ajaxSendAfter:function(json){
 			 		urgencyId();
 			 	}
 			});
 		}
 		
 		//紧急程度
 		function urgencyId(){
 	 		showGrid({
 			 	id: "urgencyId",
 			 	url: flowableBasePath + "sealseserviceurgency008",
 			 	params: {},
 			 	pagination: false,
 			 	template: getFileContent('tpl/template/select-option.tpl'),
 			 	ajaxSendLoadBefore: function(hdb){
 			 	},
 			 	ajaxSendAfter:function(json){
 			 		modeId();
 			 	}
 			});
 		}
 		
		//服务处理方式
 		function modeId(){
 	 		showGrid({
 			 	id: "modeId",
 			 	url: flowableBasePath + "sealseservicemode008",
 			 	params: {},
 			 	pagination: false,
 			 	template: getFileContent('tpl/template/select-option.tpl'),
 			 	ajaxSendLoadBefore: function(hdb){
 			 	},
 			 	ajaxSendAfter:function(json){
 			 		form.render('select');
 			 		//加载行政区划-省
	    			loadChildProvinceArea();
 			 	}
 			});
 		}

		skyeyeEnclosure.init('enclosureUpload');
 		matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	var provinceId = "", cityId = "", areaId = "", townshipId = "";
	        	if(!isNull($("#provinceId").val())){
	        		provinceId = $("#provinceId").val();
	        	} else {
	        		winui.window.msg('请选择所在省.', {icon: 2, time: 2000});
	        		return false;
	        	}
	        	if(!isNull($("#cityId").val())){
	        		cityId = $("#cityId").val();
	        	}
	        	if(!isNull($("#areaId").val())){
	        		areaId = $("#areaId").val();
	        	}
	        	if(!isNull($("#townshipId").val())){
	        		townshipId = $("#townshipId").val();
	        	}
	        	//相关图片
	        	var picUrl = $("#sheetPicture").find("input[type='hidden'][name='upload']").attr("oldurl");
 	        	var params = {
        			typeId: $("#typeId").val(),//服务类型，不可为空
        			declarationTime: $("#declarationTime").val(),//报单时间，不可为空
        			customerId: customerMation.id,//客户-可为空
        			customerName: customerMation.customName,//客户名称-可为空
        			contacts: $("#contacts").val(),//联系人，不可为空
        			phone: $("#phone").val(),//联系电话，不可为空
        			email: $("#email").val(),//联系邮箱，不可为空
        			qq: $("#qq").val(),//联系QQ，不可为空
        			provinceId: provinceId,//省，不可为空
        			cityId: cityId,//市，可为空
        			areaId: areaId,//区县，可为空
        			townshipId: townshipId,//乡镇，可为空
        			addressDetailed: $("#addressDetailed").val(),//详细地址，不可为空
        			productId: isNull(erpOrderUtil.chooseProductMation.productId) ? '' : erpOrderUtil.chooseProductMation.productId,//产品id，可为空
        			productName: $("#productName").val(),//产品名称，可为空
        			productNorms: $("#productNorms").val(),//规格型号，可为空
        			productSerialNum: $("#productSerialNum").val(),//序列号，可为空
        			productWarranty: $("#productWarranty").val(),//质保类型，可为空
        			urgencyId: $("#urgencyId").val(),//紧急程度，不可为空
        			modeId: $("#modeId").val(),//处理方式，不可为空
        			content: $("#content").val(),//服务内容，不可为空
        			serviceUserId: isNull(serviceUser.userId) ? "" : serviceUser.userId,//工单接收人，可为空
        			cooperationUserId: systemCommonUtil.tagEditorGetAllData('cooperationUserId', cooperationUser),//工单协助人，可为空
        			sheetPicture: isNull(picUrl) ? "" : picUrl,//相关照片，可为空
					enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload'),//附件，可为空
        			pointSubscribeTime: $("#pointSubscribeTime").val() //指定预约时间
 	        	};
 	        	AjaxPostUtil.request({url: flowableBasePath + "sealseservice011", params: params, type: 'json', callback: function(json) {
	 	   			if (json.returnCode == 0) {
		 	   			winui.window.msg("操作成功", {icon: 1, time: 3000}, function() {
		 	   				location.reload();
		 	   			});
	 	   			} else {
	 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	 	   			}
	 	   		}});
 	        }
 	        return false;
 	    });
 	    
	    //工单接收人选择
		$("body").on("click", "#serviceUserIdSelPeople", function(e){
			_openNewWindows({
 				url: "../../tpl/serviceworker/serviceworkershowlist.html", 
 				title: "选择接收人",
 				pageId: "serviceworkershowlist",
 				area: ['90vw', '90vh'],
 				callBack: function(refreshCode){
 	                if (refreshCode == '0') {
 	                	$("#serviceUserId").val(serviceUser.userName);
 	                } else if (refreshCode == '-9999') {
 	                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
 	                }
 				}});
		});
		
		//工单协助人
	    $('#cooperationUserId').tagEditor({
	        initialTags: [],
	        placeholder: '请选择工单协助人',
            editorTag: false,
	        beforeTagDelete: function(field, editor, tags, val) {
				cooperationUser = [].concat(arrayUtil.removeArrayPointName(cooperationUser, val));
	        }
	    });
	    //工单协助人选择
		$("body").on("click", "#cooperationUserIdSelPeople", function(e){
			systemCommonUtil.userReturnList = [].concat(cooperationUser);
			systemCommonUtil.chooseOrNotMy = "1"; // 人员列表中是否包含自己--1.包含；其他参数不包含
			systemCommonUtil.chooseOrNotEmail = "2"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
			systemCommonUtil.checkType = "1"; // 人员选择类型，1.多选；其他。单选
			systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList) {
				// 重置数据
				cooperationUser = [].concat(systemCommonUtil.tagEditorResetData('cooperationUserId', userReturnList));
			});
		});
		
		form.on('select(areaProvince)', function(data){
	    	layui.$(data.elem).parent('dd').nextAll().remove();
	    	if(isNull(data.value) || data.value == '请选择'){
	    	} else {
	    		loadChildCityArea();
	    	}
 		});
	    form.on('select(areaCity)', function(data){
	    	layui.$(data.elem).parent('dd').nextAll().remove();
	    	if(isNull(data.value) || data.value == '请选择'){
	    	} else {
	    		loadChildArea();
	    	}
 		});
	    form.on('select(area)', function(data){
	    	layui.$(data.elem).parent('dd').nextAll().remove();
	    	if(isNull(data.value) || data.value == '请选择'){
	    	} else {
	    		loadChildAreaTownShip();
	    	}
 		});
	    
	    //省级行政区划
	    function loadChildProvinceArea(){
 	    	AjaxPostUtil.request({url:reqBasePath + "commontarea001", params:{}, type: 'json', callback: function(json){
 	   			if (json.returnCode == 0) {
 	   				var str = '<dd class="layui-col-xs3"><select id="provinceId" win-verify="required" lay-filter="areaProvince" lay-search=""><option value="">请选择</option>';
	 	   			for(var i = 0; i < json.rows.length; i++){
	 	   				str += '<option value="' + json.rows[i].id + '">' + json.rows[i].name + '</option>';
	 	   			}
	 	   			str += '</select></dd>';
	 	   			$("#lockParentSel").append(str);
	 	   			form.render('select');
 	   			} else {
 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
 	   			}
 	   		}});
 	    }
	    
	    //市级行政区划
	    function loadChildCityArea(){
 	    	AjaxPostUtil.request({url:reqBasePath + "commontarea002", params:{rowId: $("#provinceId").val()}, type: 'json', callback: function(json){
 	   			if (json.returnCode == 0) {
 	   				var str = '<dd class="layui-col-xs3"><select id="cityId" lay-filter="areaCity" lay-search=""><option value="">请选择</option>';
	 	   			for(var i = 0; i < json.rows.length; i++){
	 	   				str += '<option value="' + json.rows[i].id + '">' + json.rows[i].name + '</option>';
	 	   			}
	 	   			str += '</select></dd>';
	 	   			$("#lockParentSel").append(str);
	 	   			form.render('select');
 	   			} else {
 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
 	   			}
 	   		}});
 	    }
	    
	    //县级行政区划
	    function loadChildArea(){
 	    	AjaxPostUtil.request({url:reqBasePath + "commontarea003", params:{rowId: $("#cityId").val()}, type: 'json', callback: function(json){
 	   			if (json.returnCode == 0) {
 	   				var str = '<dd class="layui-col-xs3"><select id="areaId" lay-filter="area" lay-search=""><option value="">请选择</option>';
	 	   			for(var i = 0; i < json.rows.length; i++){
	 	   				str += '<option value="' + json.rows[i].id + '">' + json.rows[i].name + '</option>';
	 	   			}
	 	   			str += '</select></dd>';
	 	   			$("#lockParentSel").append(str);
	 	   			form.render('select');
 	   			} else {
 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
 	   			}
 	   		}});
 	    }
	    
	    //镇级行政区划
	    function loadChildAreaTownShip(){
 	    	AjaxPostUtil.request({url:reqBasePath + "commontarea004", params:{rowId: $("#areaId").val()}, type: 'json', callback: function(json){
 	   			if (json.returnCode == 0) {
 	   				var str = '<dd class="layui-col-xs3"><select id="townshipId" lay-filter="areaTownShip" lay-search=""><option value="">请选择</option>';
	 	   			for(var i = 0; i < json.rows.length; i++){
	 	   				str += '<option value="' + json.rows[i].id + '">' + json.rows[i].name + '</option>';
	 	   			}
	 	   			str += '</select></dd>';
	 	   			$("#lockParentSel").append(str);
	 	   			form.render('select');
 	   			} else {
 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
 	   			}
 	   		}});
 	    }
 	    
 	    //产品选择
 	    $("body").on("click", "#productNameSel", function(e){
			erpOrderUtil.openMaterialChooseChoosePage(function (chooseProductMation) {
				$("#productName").val(chooseProductMation.productName);
				$("#productNorms").val(chooseProductMation.productModel);
			});
 	    });
 	    
 	    //客户选择
 	    $("body").on("click", "#customMationSel", function(e){
 	    	_openNewWindows({
 				url: "../../tpl/customermanage/customerChoose.html", 
 				title: "选择客户",
 				pageId: "customerchooselist",
 				area: ['90vw', '90vh'],
 				callBack: function(refreshCode){
 	                if (refreshCode == '0') {
 	                	$("#customName").val(customerMation.customName);
 	                	$("#contacts").val(customerMation.contacts);
						$("#phone").val(customerMation.mobilePhone);
						$("#email").val(customerMation.email);
						$("#qq").val(customerMation.qq);
 	                } else if (refreshCode == '-9999') {
 	                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
 	                }
 				}});
 	    });
	});
});