
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    
	    var useTemplate = $("#useTemplate").html();
	    
	    AjaxPostUtil.request({url: flowableBasePath + "vehicle016", params:{rowId: parent.rowId}, type: 'json', callback: function (json) {
			// 状态
			if(json.bean.state == '0'){
				json.bean.stateName = "<span>" + json.bean.stateName + "</span>";
			}else if(json.bean.state == '1'){
				json.bean.stateName = "<span class='state-new'>" + json.bean.stateName + "</span>";
			}else if(json.bean.state == '2'){
				json.bean.stateName = "<span class='state-up'>" + json.bean.stateName + "</span>";
			}else if(json.bean.state == '3'){
				json.bean.stateName = "<span class='state-down'>" + json.bean.stateName + "</span>";
			}else if(json.bean.state == '4'){
				json.bean.stateName = "<span class='state-down'>" + json.bean.stateName + "</span>";
			}else if(json.bean.state == '5'){
				json.bean.stateName = "<span class='state-error'>" + json.bean.stateName + "</span>";
			}
			if(json.bean.vehicleState == '1'){
				json.bean.vehicleStateName = "<span class='state-up'>" + json.bean.vehicleStateName + "</span>";
			}else if(json.bean.vehicleState == '2'){
				json.bean.vehicleStateName = "<span class='state-down'>" + json.bean.vehicleStateName + "</span>";
			}

			$("#showForm").html(getDataUseHandlebars(useTemplate, json));

			// 附件回显
			skyeyeEnclosure.showDetails({"enclosureUploadBtn": json.bean.enclosureInfo});

			if(json.bean.state == '2'){
				$(".actual").removeClass("layui-hide");
				$("#vehicleImg").attr("src", fileBasePath + json.bean.vehicleImg);
			}
			matchingLanguage();
	    }});
	    
	    $("body").on("click", "#vehicleImg", function() {
			systemCommonUtil.showPicImg($(this).attr("src"));
	    });

	});
});