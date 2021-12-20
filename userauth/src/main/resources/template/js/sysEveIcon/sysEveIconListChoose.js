
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form'], function (exports) {
	var index = parent.layer.getFrameIndex(window.name);
	winui.renderColor();
	var $ = layui.$;
	
    showGrid({
	 	id: "showForm",
	 	url: reqBasePath + "icon006",
	 	params: {},
	 	pagination: true,
	 	pagesize: 18,
	 	template: $("#useTemplate").html(),
	 	ajaxSendLoadBefore: function(hdb){
	 	},
	 	options: {'click .choose':function(i, row){
	 			parent.systemCommonUtil.sysIconChooseClass = row.iconClass;
	 			parent.layer.close(index);
 	        	parent.refreshCode = '0';
	 		}
	 	},
	 	ajaxSendAfter:function(json){
	 		matchingLanguage();
	 	}
    });
	
    exports('sysEveIconListChoose', {});
});
