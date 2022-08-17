var layedit, form;

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'].concat(dsFormUtil.mastHaveImport), function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$;
	layedit = layui.layedit,
	form = layui.form;
	
	// 获取动态表单内容用于编辑申请类型实体
	AjaxPostUtil.request({url:flowableBasePath + "activitimode023", params: {rowId: parent.sequenceId}, type: 'json', callback: function (json) {
		dsFormUtil.loadDsFormItemToEdit("showForm", json.rows);
		$("#showForm").append('<div class="layui-form-item layui-col-xs12"><div class="layui-input-block">' +
				'<button class="winui-btn" id="cancle">' + systemLanguage["com.skyeye.cancel"][languageType] + '</button>' +
				'<button class="winui-btn" lay-submit="" lay-filter="formAddBean">' + systemLanguage["com.skyeye.save"][languageType] + '</button>' +
				'</div></div>');
		form.render();
		matchingLanguage();
 	}});
	form.on('submit(formAddBean)', function (data) {
        if (winui.verifyForm(data.elem)) {
			var jStr = {
				jsonStr: JSON.stringify(dsFormUtil.getPageData($("#showForm"))),
				taskId: parent.taskId,
				processInstanceId: parent.processInstanceId
			};
        	AjaxPostUtil.request({url:flowableBasePath + "activitimode024", params: jStr, type: 'json', callback: function (json) {
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