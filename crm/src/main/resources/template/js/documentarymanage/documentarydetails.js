var enclosureList = new Array();//附件

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
		 	url: reqBasePath + "documentary005",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/documentarymanage/documentarydetailsTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter: function(json){
		 		//附件回显
			    if(json.bean.enclosureInfo.length != 0 && json.bean.enclosureInfo != ""){
			    	enclosureList = json.bean.enclosureInfo;
			    	var str = "";
	    			$.each([].concat(enclosureList), function(i, item){
	    				str += '<a rowid="' + item.id + '" class="enclosureItem" rowpath="' + item.fileAddress + '" href="javascript:;" style="color:blue;">' + item.name + '</a><br>';
	    			});
	    			$("#enclosureUploadBtn").html(str);
 	        	}
 	        	matchingLanguage();
		 		form.render();
		 	}
		});
	    
	    $("body").on("click", ".enclosureItem", function(){
	    	download(fileBasePath + $(this).attr("rowpath"), $(this).html());
	    });
	});
});