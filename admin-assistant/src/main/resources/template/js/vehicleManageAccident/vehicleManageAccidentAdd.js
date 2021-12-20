
// 车辆事故
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
	    
    	AjaxPostUtil.request({url:reqBasePath + "login002", params:{}, type:'json', callback:function(json){
    		if(json.returnCode == 0) {
    			var userId = json.bean.id;
    			var userName = json.bean.userName;
    			$("#accidentTitle").html("车辆事故登记单-" + userName + "-" + (new Date()).getTime()) + Math.floor(Math.random()*100);
    		}else {
    			winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    		}
		}});
		
	    // 车牌号
 		showGrid({
		 	id: "licensePlate",
		 	url: reqBasePath + "vehicle010",
		 	params: {},
		 	pagination: false,
		 	template: getFileContent('tpl/template/select-option-must.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function(json){
		 		form.render('select');
		 	}
		});
	    
 		// 事故时间
 		laydate.render({ 
 		  	elem: '#accidentTime',
 		  	type: 'date',
 		  	max: getYMDFormatDate(),// 设置最大可选的日期
 	 	  	trigger: 'click'
 		});
 		
 		// 送修时间段选择
		laydate.render({
			elem: '#repairTime',
			type: 'date',
			range: true,
			trigger: 'click'
		});

		skyeyeEnclosure.init('enclosureUpload');
		matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
	 	    	var repairStartTime = "";
	 	    	var repairEndTime = "";
	 	    	var repairTime = $("#repairTime").val();
	 	    	if(repairTime != ""){
	 	    		repairStartTime = $("#repairTime").val().split(" - ")[0];
	    			repairEndTime = $("#repairTime").val().split(" - ")[1];
	 	    	}
 	        	var params = {
        			accidentTitle: $("#accidentTitle").html(),
        			driver: $("#driver").val(),
        			accidentTime: $("#accidentTime").val(),
        			accidentArea: $("#accidentArea").val(),
        			accidentDetail: $("#accidentDetail").val(),
        			confirmationResponsibility: $("#confirmationResponsibility").val(),
        			manufacturer: $("#manufacturer").val(),
        			repairStartTime: repairStartTime,
        			repairEndTime: repairEndTime,
        			repairPrice: $("#repairPrice").val(),
        			repairContent: $("#repairContent").val(),
        			lossFeePrice: $("#lossFeePrice").val(),
        			claimsFeePrice: $("#claimsFeePrice").val(),
        			driverBearPrice: $("#driverBearPrice").val(),
					vehicleId: $("#licensePlate").val(),
					enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload')
 	        	};
 	        	AjaxPostUtil.request({url:reqBasePath + "accident002", params:params, type:'json', callback:function(json){
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
 	    
	    $("body").on("click", ".enclosureItem", function(){
	    	download(fileBasePath + $(this).attr("rowpath"), $(this).html());
	    });
 	    
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
});