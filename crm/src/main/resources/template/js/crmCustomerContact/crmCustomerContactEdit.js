
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
	    
	    showGrid({
		 	id: "showForm",
		 	url: flowableBasePath + "customercontact003",
		 	params: {contactId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/crmCustomerContact/crmCustomerContactEditTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter: function (json) {
		 		// 客户信息赋值
		 		customerMation = {
		 			id: json.bean.customerId,
		 			customName: json.bean.customerName
		 		}
		 		$("input:radio[name=isDefault][value=" + json.bean.isDefault + "]").attr("checked", true);
		 		
		 		matchingLanguage();
		 		form.render();
		 	    form.on('submit(formEditBean)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	if(isNull(customerMation.id)){
							winui.window.msg('请选择客户.', {icon: 2, time: 2000});
							return false;
						}
		 	        	var params = {
	 	        			contactId: parent.rowId,
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
	 	 	        	AjaxPostUtil.request({url: flowableBasePath + "customercontact004", params: params, type: 'json', callback: function (json) {
							parent.layer.close(index);
							parent.refreshCode = '0';
	 		 	   		}});
		 	        }
		 	        return false;
		 	    });
		 	}
		});
		
		// 客户选择
 	    $("body").on("click", "#customMationSel", function (e) {
 	    	_openNewWindows({
 				url: "../../tpl/customermanage/customerChoose.html", 
 				title: "选择客户",
 				pageId: "customerchooselist",
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