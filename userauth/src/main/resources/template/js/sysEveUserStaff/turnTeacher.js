layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form', 'layedit'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
		    form = layui.form;
	    
	    showGrid({
    	 	id: "schoolId",
    	 	url: sysMainMation.schoolBasePath + "queryAllSchoolList",
			method: 'GET',
    	 	params: {},
    	 	pagination: false,
    	 	template: getFileContent('tpl/template/select-option.tpl'),
    	 	ajaxSendAfter:function (json) {
    	 		form.render('select');
    	 	}
        });
		
        matchingLanguage();
	    form.render();
	    form.on('submit(submit)', function (data) {
	        if (winui.verifyForm(data.elem)) {
        		var params = {
        			schoolId: $("#schoolId").val(),
    				staffId: parent.rowId
        		};
    			AjaxPostUtil.request({url: sysMainMation.reqBasePath + "staff007", params: params, type: 'json', method: 'POST', callback: function (json) {
					parent.layer.close(index);
					parent.refreshCode = '0';
    			}});
	        }
	        return false;
	    });
	    
	    // 取消
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});