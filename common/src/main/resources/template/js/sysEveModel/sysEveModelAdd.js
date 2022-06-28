
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'fileUpload'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
		    form = layui.form;

	    var ue = ueEditorUtil.initEditor('container');

		// 模板类型  1.系统模板  2.个人模板
	    var type = GetUrlParam("type");

		// 初始化上传
		$("#logo").upload(systemCommonUtil.uploadCommon003Config('logo', 20, '', 1));

		systemModelUtil.loadSysEveModelTypeByPId("firstTypeId", "0");

		form.on('select(firstTypeId)', function(data) {
			var thisRowValue = data.value;
			systemModelUtil.loadSysEveModelTypeByPId("secondTypeId", isNull(thisRowValue) ? "-" : thisRowValue);
			form.render('select');
		});

		matchingLanguage();
		form.render();
	    form.on('submit(formAddBean)', function (data) {
	        if (winui.verifyForm(data.elem)) {
        		var params = {
    				title: $("#title").val(),
					firstTypeId: $("#firstTypeId").val(),
					secondTypeId: $("#secondTypeId").val(),
					content: encodeURIComponent(ue.getContent()),
					logo: $("#logo").find("input[type='hidden'][name='upload']").attr("oldurl"),
					type: type
        		};
 	        	if(isNull(params.logo)){
 	        		winui.window.msg('请上传LOGO', {icon: 2, time: 2000});
 	        		return false;
 	        	}
				if(isNull(params.content)){
					winui.window.msg('请填写模板内容', {icon: 2, time: 2000});
					return false;
				}
    			AjaxPostUtil.request({url: reqBasePath + "sysevemodel002", params: params, type: 'json', method: "POST", callback: function (json) {
    				if (json.returnCode == 0) {
    					parent.layer.close(index);
		 	        	parent.refreshCode = '0';
    				} else {
    					winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    				}
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