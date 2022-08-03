
var content = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window',
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    
	    showGrid({
		 	id: "showForm",
		 	url: sysMainMation.knowlgBasePath + "knowledgecontent006",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/knowledgecontent/knowledgecontentdetailsTemplate.tpl'),
		 	ajaxSendAfter:function (json) {
		 		content = json.bean.content;
		 		$("#knowledgecontentshowBox").attr("src", "knowledgecontentshow.html");

				var lavel = isNull(json.bean.label) ? [] : json.bean.label.split(',');
				var str = "";
				$.each(lavel, function(i, item){
					str += '<span class="layui-badge layui-bg-blue">' + item + '</span>';
				});
				$('#label').html(str);

		 		matchingLanguage();
		 	}
		});
	});
});