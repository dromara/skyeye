
// 车辆加油
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
		systemCommonUtil.getSysCurrentLoginUserMation(function (data) {
			var userName = data.bean.userName;
			$("#oilTitle").html("车辆加油登记单-" + userName + "-" + (new Date()).getTime()) + Math.floor(Math.random() * 100);
		});

		// 查询所有的车牌号用于下拉选择框
		adminAssistantUtil.queryAllVehicleList(function (data) {
			$("#licensePlate").html(getDataUseHandlebars(getFileContent('tpl/template/select-option-must.tpl'), data));
			form.render('select');
		});

 		// 加油日期
 		laydate.render({elem: '#oilTime', type: 'date', trigger: 'click'});
 		
 		matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	var params = {
        			oilTitle: $("#oilTitle").html(),
        			oilTime: $("#oilTime").val(),
        			oilCapacity: $("#oilCapacity").val(),
        			oilPrice: $("#oilPrice").val(),
 	        		roomAddDesc: $("#roomAddDesc").val(),
					vehicleId: $("#licensePlate").val(),
					enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload')
 	        	};
 	        	AjaxPostUtil.request({url: flowableBasePath + "oiling002", params: params, type: 'json', callback: function (json) {
					parent.layer.close(index);
					parent.refreshCode = '0';
	 	   		}});
 	        }
 	        return false;
 	    });

	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});