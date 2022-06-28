
// 车辆年检
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

		// 获取当前登录员工信息
		systemCommonUtil.getSysCurrentLoginUserMation(function (data){
			var userName = data.bean.userName;
			$("#inspectionTitle").html("车辆年检登记单-" + userName + "-" + (new Date()).getTime()) + Math.floor(Math.random() * 100);
		});

		// 查询所有的车牌号用于下拉选择框
		adminAssistantUtil.queryAllVehicleList(function (data){
			$("#licensePlate").html(getDataUseHandlebars(getFileContent('tpl/template/select-option-must.tpl'), data));
			form.render('select');
		});

 		// 本次年检时间
 		laydate.render({ 
 			elem: '#thisInspectionTime',
 			type: 'date',
 			trigger: 'click'
 		});
 		
 		// 下次年检时间
 		laydate.render({ 
 			elem: '#nextInspectionTime',
 			type: 'date',
 			trigger: 'click'
 		});

		skyeyeEnclosure.init('enclosureUpload');
 		matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	var params = {
        			inspectionTitle: $("#inspectionTitle").html(),
        			inspectionArea: $("#inspectionArea").val(),
        			contactInformation: $("#contactInformation").val(),
        			inspectionPrice: $("#inspectionPrice").val(),
        			thisInspectionTime: $("#thisInspectionTime").val(),
        			nextInspectionTime: $("#nextInspectionTime").val(),
 	        		roomAddDesc: $("#roomAddDesc").val(),
					vehicleId: $("#licensePlate").val(),
					enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload')
 	        	};
 	        	if(params.contactInformation != ""){
 	        		var mobile = /^0?1[3|4|5|8][0-9]\d{8}$/, phone = /^0[\d]{2,3}-[\d]{7,8}$/;
 	 	        	var flag = mobile.test(params.contactInformation) || phone.test(params.contactInformation);
 	                if(!flag){
 	                	winui.window.msg('请输入正确的联系电话', {icon: 2, time: 2000});
 	 	        		return false;
 	                }
 	        	}
                if(params.inspectionPrice != ""){
 	        		var str = /^0{1}([.]\d{1,2})?$|^[1-9]\d*([.]{1}[0-9]{1,2})?$/;
 	 	        	var flag = str.test(params.inspectionPrice);
 	                if(!flag){
 	                	winui.window.msg('年检费用小数点后最多两位！', {icon: 2, time: 2000});
 	 	        		return false;
 	                }
 	        	}
 	        	AjaxPostUtil.request({url: flowableBasePath + "inspection002", params: params, type: 'json', callback: function (json) {
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

	    $("body").on("click", ".enclosureItem", function() {
	    	download(fileBasePath + $(this).attr("rowpath"), $(this).html());
	    });
 	    
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});