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
    	 	id: "allSchool",
    	 	url: schoolBasePath + "schoolmation007",
    	 	params: {},
    	 	pagination: false,
    	 	template: getFileContent('tpl/template/select-option.tpl'),
    	 	ajaxSendLoadBefore: function(hdb){
    	 	},
    	 	ajaxSendAfter:function(json){
    	 		form.render('select');
    	 	}
        });
		
        matchingLanguage();
	    form.render();
	    form.on('submit(submit)', function (data) {
	        if (winui.verifyForm(data.elem)) {
        		var params = {
        			schoolId: $("#allSchool").val(),
    				staffId: parent.rowId
        		};
    			AjaxPostUtil.request({url:reqBasePath + "staff007", params:params, type: 'json', callback: function(json){
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
	    
	    //取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
});