
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
	    
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "maintenance004",
		 	params: {rowId:parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/vehicleManageMaintenance/vehicleManageMaintenanceEditTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function(json){
		 		// 维修保养时间段选择
				laydate.render({
					elem: '#maintenanceTime',
					type: 'date',
					range: true,
					trigger: 'click'
				});
				
		 		$("input:radio[name=maintenanceType][value=" + json.bean.maintenanceType + "]").attr("checked", true);
				// 附件回显
				skyeyeEnclosure.initTypeISData({'enclosureUpload': json.bean.enclosureInfo});

 	        	matchingLanguage();
		 		form.render();
		 	    form.on('submit(formEditBean)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	var params = {
	 	        			rowId: parent.rowId,
	 	 	        		maintenanceCompany: $("#maintenanceCompany").val(),
	 	 	        		maintenancePrice: $("#maintenancePrice").val(),
	 	 	        		startTime: $("#maintenanceTime").val().split(" - ")[0],
	 	 	        		endTime: $("#maintenanceTime").val().split(" - ")[1],
	 	 	        		concreteContent: $("#concreteContent").val(),
	 	 	        		roomAddDesc: $("#roomAddDesc").val(),
	 	 	        		maintenanceType: $("input[name='maintenanceType']:checked").val(),
							enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload')
	 	 	        	};
	 	 	        	AjaxPostUtil.request({url:reqBasePath + "maintenance005", params:params, type:'json', callback:function(json){
	 		 	   			if(json.returnCode == 0){
	 			 	   			parent.layer.close(index);
	 			 	        	parent.refreshCode = '0';
	 		 	   			}else{
	 		 	   				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
	 		 	   			}
	 		 	   		}});
		 	        }
		 	        return false;
		 	    });
		 	}
		});
		
	    $("body").on("click", ".enclosureItem", function(){
	    	download(fileBasePath + $(this).attr("rowpath"), $(this).html());
	    });
	    
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
});