
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
					parent.layer.close(index);
					parent.refreshCode = '0';
	 	   		}});
 	        }
 	        return false;
 	    });
 	    
 	    // 客户选择
 	    $("body").on("click", "#customMationSel", function (e) {
 	    	_openNewWindows({
 				url: "../../tpl/customerManage/customerChoose.html",
 				title: "选择客户",
 				pageId: "customerChoose",
 				area: ['90vw', '90vh'],
 				callBack: function (refreshCode) {
					$("#customName").val(customerMation.customName);
 				}});
 	    });
 	    
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});