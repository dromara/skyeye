
// 车辆维修保养
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

		skyeyeEnclosure.init('enclosureUpload');
		// 获取当前登录员工信息
		systemCommonUtil.getSysCurrentLoginUserMation(function (data){
			var userName = data.bean.userName;
			$("#maintenanceTitle").html("车辆维修保养登记单-" + userName + "-" + (new Date()).getTime()) + Math.floor(Math.random()*100);
		});

		// 查询所有的车牌号用于下拉选择框
		adminAssistantUtil.queryAllVehicleList(function (data){
			$("#licensePlate").html(getDataUseHandlebars(getFileContent('tpl/template/select-option-must.tpl'), data));
			form.render('select');
		});

 		// 维修保养时间段选择
		laydate.render({
			elem: '#maintenanceTime',
			type: 'date',
			range: true,
			trigger: 'click'
		});
 		
		matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	var params = {
 	        		maintenanceTitle: $("#maintenanceTitle").html(),
 	        		maintenanceCompany: $("#maintenanceCompany").val(),
 	        		maintenancePrice: $("#maintenancePrice").val(),
 	        		startTime: $("#maintenanceTime").val().split(" - ")[0],
 	        		endTime: $("#maintenanceTime").val().split(" - ")[1],
 	        		concreteContent: $("#concreteContent").val(),
 	        		roomAddDesc: $("#roomAddDesc").val(),
					vehicleId: $("#licensePlate").val(),
 	        		maintenanceType: $("input[name='maintenanceType']:checked").val(),
					enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload')
 	        	};
 	        	AjaxPostUtil.request({url: flowableBasePath + "maintenance002", params:params, type: 'json', callback: function(json){
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