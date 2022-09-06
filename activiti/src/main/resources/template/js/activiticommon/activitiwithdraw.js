
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
    	form = layui.form;
    
    var processInstanceId = parent.processInstanceId,
    	hisTaskId = parent.hisTaskId;
    
    matchingLanguage();
    form.render();
    form.on('submit(formAddBean)', function (data) {
        if (winui.verifyForm(data.elem)) {
        	var msg = '确认撤回任务吗？';
    		layer.confirm(msg, { icon: 3, title: '撤回操作' }, function (i) {
    			layer.close(i);
    			var jStr = {
	    			opinion: $("#opinion").val(),
	    			processInstanceId: processInstanceId,
	    			hisTaskId: hisTaskId
	            };
	            AjaxPostUtil.request({url:flowableBasePath + "activitimode026", params: jStr, type: 'json', callback: function (json) {
					parent.layer.close(index);
					parent.refreshCode = '0';
	 	   		}});
    		});
        }
        return false;
    });
    
    // 取消
    $("body").on("click", "#cancle", function() {
    	parent.layer.close(index);
    });
    exports('activitiwithdraw', {});
});
