
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['table', 'jquery', 'winui', 'form'], function (exports) {
	var index = parent.layer.getFrameIndex(window.name);
	winui.renderColor();
	var $ = layui.$;
	
	//初始化数据
    showGrid({
	 	id: "showForm",
	 	url: reqBasePath + "icon006",
	 	params: {},
	 	pagination: true,
	 	pagesize: 18,
	 	template: getFileContent('tpl/sysevemenu/icon-item.tpl'),
	 	ajaxSendLoadBefore: function(hdb){
	 	},
	 	options: {'click .choose':function(i, row){
	 			parent.childIcon = row.iconClass;
	 			parent.layer.close(index);
 	        	parent.refreshCode = '0';
	 		}
	 	},
	 	ajaxSendAfter:function(json){
	 	}
    });
	
	
    $("body").on("click", "#reloadTable", function(){
    	
    });
    
    exports('icon', {});
});
