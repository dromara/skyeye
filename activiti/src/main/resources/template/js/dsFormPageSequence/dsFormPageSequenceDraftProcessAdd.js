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
	
	var actFlowId = parent.actFlowId;
	
	// 加载动态表单页
	AjaxPostUtil.request({url: flowableBasePath + "dsformpage004", params: {pageId: parent.dsFormId}, type: 'json', method: 'GET', callback: function (json) {
		dsFormUtil.loadDsFormItemToEdit("showForm", json.rows);
		$("#showForm").append('<div class="layui-form-item layui-col-xs12"><div class="layui-input-block">' +
				'<button class="winui-btn" id="cancle">' + systemLanguage["com.skyeye.cancel"][languageType] + '</button>' +
				'<button class="winui-btn" lay-submit="" lay-filter="formAddBean">提交审批</button>' +
				'</div></div>');
		matchingLanguage();
		form.render();
 	}});
 	
	form.on('submit(formAddBean)', function (data) {
        if (winui.verifyForm(data.elem)) {
			activitiUtil.startProcess(actFlowId, null, function (approvalId) {
				if(isNull(actFlowId)){
					winui.window.msg('流程对象为空，无法启动.', {icon: 2, time: 2000});
					return false;
				}
				var jStr = {
					actFlowId: actFlowId,
					jsonStr: JSON.stringify(dsFormUtil.getPageData($("#showForm"))),
					pageId: parent.dsFormId,
					approvalId: approvalId
				};
				AjaxPostUtil.request({url: flowableBasePath + "activitimode022", params: jStr, type: 'json', callback: function (json) {
					winui.window.msg("申请提交成功，等待审核...", {icon: 1, time: 2000}, function() {
						parent.layer.close(index);
						parent.refreshCode = '0';
					});
				}});
			});
        }
        return false;
    });
	
	// 取消
    $("body").on("click", "#cancle", function() {
    	parent.layer.close(index);
    });
});