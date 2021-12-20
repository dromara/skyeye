
var content = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "knowledgepoints006",
		 	params: {rowId:parent.rowId},
		 	pagination: false,
		 	template: $("#detailTemplate").html(),
		 	ajaxSendAfter:function(json){
		 		content = json.bean.content;
		 		$("#knowledgecontentshowBox").attr("src", "schoolKnowledgePointsShow.html");
		 		matchingLanguage();
		 	}
		});
	});
});