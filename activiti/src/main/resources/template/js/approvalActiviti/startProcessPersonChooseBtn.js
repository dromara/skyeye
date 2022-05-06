
var pageUrl = "";

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

		pageUrl = parent.activitiUtil.pageUrl;

		$("body").on("click", ".chooseApprovalPersonBtn", function(){
			_openNewWindows({
				url: "../../tpl/approvalActiviti/startProcessPersonChoose.html",
				title: "审批人选择",
				pageId: "startProcessPersonChoose",
				area: ['90vw', '90vh'],
				callBack: function(refreshCode){
					if (refreshCode == '0') {
						$("#approvalPersonName").val(activitiUtil.chooseApprovalPersonMation.jobNumber + "_" + activitiUtil.chooseApprovalPersonMation.userName);
						$("#approvalPersonName").attr("chooseData", JSON.stringify(activitiUtil.chooseApprovalPersonMation));
					} else if (refreshCode == '-9999') {
						winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
					}
				}
			});
		});

 		matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
				parent.activitiUtil.chooseApprovalPersonMation = activitiUtil.chooseApprovalPersonMation;
				parent.refreshCode = '0';
				parent.layer.close(index);
 	        }
 	        return false;
 	    });

	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
});