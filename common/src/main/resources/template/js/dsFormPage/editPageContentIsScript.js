
layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form'].concat(dsFormUtil.mastHaveImport), function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form;

	$("#scriptContent").val(parent.scriptData);
	var jsEditor = CodeMirror.fromTextArea(document.getElementById("scriptContent"), codeUtil.getConfig('text/javascript'));

	matchingLanguage();
	form.render();
	form.on('submit(formWriteBean)', function (data) {
		if (winui.verifyForm(data.elem)) {
			parent.scriptData = encodeURIComponent(jsEditor.getValue());
			parent.layer.close(index);
			parent.refreshCode = '0';
		}
		return false;
	});

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});