
// 已经选择的客户信息
var customerMation = {};

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
	    
	    matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	if(isNull(customerMation.id)){
					winui.window.msg('请选择客户.', {icon: 2, time: 2000});
					return false;
				}
 	        	var params = {
 	        		customerId: customerMation.id,
 	        		contacts: $("#contacts").val(),
 	        		department: $("#department").val(),
 	        		job: $("#job").val(),
 	        		workPhone: $("#workPhone").val(),
 	        		mobilePhone: $("#mobilePhone").val(),
 	        		email: $("#email").val(),
 	        		qq: $("#qq").val(),
 	        		wechat: $("#wechat").val(),
 	        		isDefault: $("input[name='isDefault']:checked").val()
 	        	};
 	        	AjaxPostUtil.request({url: flowableBasePath + "customercontact002", params: params, type: 'json', callback: function (json) {
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
 	    
 	    // 客户选择
 	    $("body").on("click", "#customMationSel", function (e) {
 	    	_openNewWindows({
 				url: "../../tpl/customermanage/customerChoose.html", 
 				title: "选择客户",
 				pageId: "customerchooselist",
 				area: ['90vw', '90vh'],
 				callBack: function(refreshCode){
 	                if (refreshCode == '0') {
 	                	$("#customName").val(customerMation.customName);
 	                } else if (refreshCode == '-9999') {
 	                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
 	                }
 				}});
 	    });
 	    
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});