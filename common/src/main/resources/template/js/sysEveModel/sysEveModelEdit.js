
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
	    
	    var type;
	    
	    AjaxPostUtil.request({url: reqBasePath + "sysevemodel004", params:{id: parent.rowId}, type: 'json', method: "GET", callback: function (json) {
			// 初始化上传
			$("#logo").upload(systemCommonUtil.uploadCommon003Config('logo', 20, json.bean.logo, 1));

			$("#title").val(json.bean.title);
			type = json.bean.type;

			systemModelUtil.loadSysEveModelTypeByPId("firstTypeId", "0");
			$("#firstTypeId").val(json.bean.firstTypeId);

			systemModelUtil.loadSysEveModelTypeByPId("secondTypeId", json.bean.firstTypeId);
			$("#secondTypeId").val(json.bean.secondTypeId);

			form.on('select(firstTypeId)', function(data) {
				var thisRowValue = data.value;
				systemModelUtil.loadSysEveModelTypeByPId("secondTypeId", isNull(thisRowValue) ? "-" : thisRowValue);
				form.render('select');
			});

			var ue = ueEditorUtil.initEditor('container');
			ue.addListener("ready", function () {
				ue.setContent(json.bean.content);
			});

			matchingLanguage();
			form.render();
			form.on('submit(formEditBean)', function (data) {
				if (winui.verifyForm(data.elem)) {
					var params = {
						title: $("#title").val(),
						firstTypeId: $("#firstTypeId").val(),
						secondTypeId: $("#secondTypeId").val(),
						content: encodeURIComponent(ue.getContent()),
						logo: $("#logo").find("input[type='hidden'][name='upload']").attr("oldurl"),
						type: type,
						id: parent.rowId
					};
					if(isNull(params.logo)){
						winui.window.msg('请上传LOGO', {icon: 2, time: 2000});
						return false;
					}
					if(isNull(params.content)){
						winui.window.msg('请填写模板内容', {icon: 2, time: 2000});
						return false;
					}
					AjaxPostUtil.request({url: reqBasePath + "sysevemodel005", params: params, type: 'json', method: "PUT", callback: function (json) {
						parent.layer.close(index);
						parent.refreshCode = '0';
					}});
				}
				return false;
			});
   		}});
	    
	    // 取消
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	    
	});
});