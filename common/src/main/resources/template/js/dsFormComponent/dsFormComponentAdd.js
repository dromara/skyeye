
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui'].concat(dsFormUtil.mastHaveImport), function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		element = layui.element,
		colorpicker = layui.colorpicker,
		form = layui.form;

	// 加载图标信息
	systemCommonUtil.initIconChooseHtml('iconMation', form, colorpicker, 12);

	// 组件分类
	sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["dsFormContentType"]["key"], 'select', "dsFormContentType", '', form);
	// 组件展示类型
	skyeyeClassEnumUtil.showEnumDataListByClassName("dsFormShowType", 'select', "showType", '', form);

	// 根据类型获取部分功能的使用说明
	systemCommonUtil.queryExplainMationByType(2, function(json) {
		$(".layui-colla-title").html(json.bean.title);
		$(".layui-colla-content").html(json.bean.content);
	});
	element.init();

	// 是否关联数据
	form.on('switch(linkedData)', function (data) {
		$(data.elem).val(data.elem.checked);
	});

	var htmlEditor = CodeMirror.fromTextArea(document.getElementById("htmlContent"), codeUtil.getConfig('xml'));
	var jsEditor = CodeMirror.fromTextArea(document.getElementById("jsContent"), codeUtil.getConfig('text/javascript'));
	var jsValue = CodeMirror.fromTextArea(document.getElementById("jsValue"), codeUtil.getConfig('text/javascript'));
	var jsDisplayValue = CodeMirror.fromTextArea(document.getElementById("jsDisplayValue"), codeUtil.getConfig('text/javascript'));
	var jsFitValue = CodeMirror.fromTextArea(document.getElementById("jsFitValue"), codeUtil.getConfig('text/javascript'));

	matchingLanguage();
	form.render();
	form.on('submit(formAddBean)', function (data) {
		if (winui.verifyForm(data.elem)) {
			if (isNull(htmlEditor.getValue())) {
				winui.window.msg('请输入模板内容', {icon: 2, time: 2000});
				return false;
			}
			var params = {
				numCode: $("#numCode").val(),
				name: $("#name").val(),
				htmlContent: encodeURIComponent(htmlEditor.getValue()),
				jsContent: encodeURIComponent(jsEditor.getValue()),
				jsValue: encodeURIComponent(jsValue.getValue()),
				jsDisplayValue: encodeURIComponent(jsDisplayValue.getValue()),
				jsFitValue: encodeURIComponent(jsFitValue.getValue()),
				typeId: $("#dsFormContentType").val(),
				showType: $("#showType").val(),
				linkedData: '2'
			};
			if ($("#linkedData").val() == 'true') {
				params.linkedData = '1';
			}
			// 获取图标信息
			params = systemCommonUtil.getIconChoose(params);
			if (!params["iconChooseResult"]) {
				return false;
			}
			AjaxPostUtil.request({url: reqBasePath + "writeDsFormComponent", params: params, type: 'json', method: 'POST', callback: function(json) {
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