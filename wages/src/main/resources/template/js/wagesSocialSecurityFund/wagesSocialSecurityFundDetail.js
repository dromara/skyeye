
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'tagEditor', 'laydate'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form;

	showGrid({
		id: "showForm",
		url: sysMainMation.wagesBasePath + "wagessocialsecurityfund008",
		params: {rowId: parent.rowId},
		pagination: false,
		method: "GET",
		template: $("#beanTemplate").html(),
		ajaxSendLoadBefore: function(hdb) {},
		ajaxSendAfter:function (json) {
			$('#company').html(getNameByList(json.bean.company).toString());
			$('#department').html(getNameByList(json.bean.departMent).toString());
			$('#userStaff').html(getNameByList(json.bean.userStaff).toString());

			// 附件回显
			skyeyeEnclosure.showDetails({"enclosureUploadBtn": json.bean.enclosureInfo});

			matchingLanguage();
			form.render();
		}
	});

	function getNameByList(array){
		var name = [];
		if(isNull(array)){
			return name;
		}
		$.each(array, function(i, item){
			name.push(item.name)
		});
		return name;
	}
});