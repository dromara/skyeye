var receiveId = "";
var chatType = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	form = layui.form;
	    
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "companytalkgroup008",
		 	params: {receiveId: receiveId, chatType: chatType},
		 	pagination: true,
		 	pagesize: 50,
		 	template: getFileContent('tpl/chat/chatLog.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 		hdb.registerHelper('compare1', function(v1, v2, options) {
		 			if(v1 === v2){
		 				return options.inverse(this);
		 			} else {
		 				return options.fn(this);
		 			}
		 		});
		 	},
		 	options: {},
		 	ajaxSendAfter:function(json){
		 		matchingLanguage();
		 		form.render();
		 	}
	    });
	    
	});
	    
});

function dataBase(id, type){
	receiveId = id;
	chatType = type;
	console.log(id);
	console.log(type);
}