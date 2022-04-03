
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    
	    showGrid({
		 	id: "showForm",
		 	url: flowableBasePath + "checkwork012",
		 	params: {rowId:parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/checkWorkAbnormalAttendance/checkWorkAbnormalAttendanceDetailsTemplate.tpl'),
		 	ajaxSendAfter:function(json){
		 		if(json.bean.state == "申诉中"){
		 			$("#approvalTime").hide();
		 			$("#appealRemark").hide();
		 		}
		 		matchingLanguage();
		 		form.render();
		 	}
		});

	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
});