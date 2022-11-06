
layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'eleTree'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
			eleTree = layui.eleTree;

		var url = reqBasePath + "queryDictDataListByDictTypeCodeAndNotId?dictTypeCode=" + parent.parentNode.dictCode + '&notId=' + parent.parentNode.id;
		eleTree.render({
			elem: '.ele5',
			url: url,
			defaultExpandAll: true,
			expandOnClickNode: false,
			highlightCurrent: true
		});
		$(".ele5").hide();
		$("#parentId").on("click",function (e) {
			e.stopPropagation();
			$(".ele5").toggle();
		});
		eleTree.on("nodeClick(data5)",function(d) {
			$("#parentId").val(d.data.currentData.name);
			$("#parentId").attr("parentId", d.data.currentData.id);
			$(".ele5").hide();
		})
		$(document).on("click",function() {
			$(".ele5").hide();
		})

		matchingLanguage();
		form.render();
		form.on('submit(formEditBean)', function (data) {
			if (winui.verifyForm(data.elem)) {
				var params = {
					id: parent.parentNode.id,
					parentId: $("#parentId").attr("parentId"),
				};
				AjaxPostUtil.request({url: reqBasePath + "setDictDataParent", params: params, type: 'json', method: "PUT", callback: function (json) {
					parent.layer.close(index);
					parent.refreshCode = '0';
				}});
			}
			return false;
		});

	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});