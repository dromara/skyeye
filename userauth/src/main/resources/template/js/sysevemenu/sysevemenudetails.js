
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window',
}).define(['window', 'table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "sys040",
		 	params: {rowId:parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/sysevemenu/sysevemenudetailsTemplate.tpl'),
		 	ajaxSendAfter:function(json){
		 		var str = '';
	        	if(json.bean.menuIconType == '1'){
		        	if(isNull(json.bean.menuIconBg)){
		        		str += '<div class="winui-icon winui-icon-font" style="width: 20px; height: 20px;">';
		        	} else {
		        		str += '<div class="winui-icon winui-icon-font" style="text-align: center; padding: 7px; width: 20px; height: 20px; background-color:' + json.bean.menuIconBg + '">';
		        	}
		        	if(isNull(json.bean.menuIconColor)){
		        		str += '<i class="fa fa-fw ' + json.bean.menuIcon + '" style="color: white"></i>';
		        	} else {
		        		str += '<i class="fa fa-fw ' + json.bean.menuIcon + '" style="color: ' + json.bean.menuIconColor + '"></i>';
		        	}
		        	str += '</div>';
	        	}else if(json.bean.menuIconType == '2'){
	        		str = '<img src="' + fileBasePath + json.bean.menuIconPic + '" class="photo-img" id="menuIconPic">';
	        	}
	        	$("#icon").html(str);
	        	
	        	if(json.bean.parentId == '0'){
	        		$("#menuLevel").html("创世菜单");
	        	} else {
	        		$("#menuLevel").html( "子菜单-->" + json.bean.menuLevel + "级子菜单");
	        	}
	        	matchingLanguage();
		 		form.render();
		 		$("body").on("click","#menuIconPic", function(){
		 			layer.open({
		        		type:1,
		        		title:false,
		        		closeBtn:0,
		        		skin: 'demo-class',
		        		shadeClose:true,
		        		content:'<img src="' + fileBasePath + json.bean.menuIconPic + '" style="max-height:400px;max-width:100%;">',
		        		scrollbar:false
		            });
		 		}) 
		 	}
		});
	    
	});
});