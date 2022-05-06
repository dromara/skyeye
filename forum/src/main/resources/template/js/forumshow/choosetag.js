
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form', 'tagEditor'], function (exports) {
	var index = parent.layer.getFrameIndex(window.name);
	winui.renderColor();
	var $ = layui.$,
		form = layui.form;
	
	var tagReturnList = new Array();
	
	$("body").on("click", "#cancle", function(){
		parent.layer.close(index);
    });
	
	var num = 0;
	form.on('checkbox(checkboxProperty)', function(data){
		if (data.elem.checked == true){
			if(num < 3){
				num++;
			}else{
				winui.window.msg("最多选三个标签！", {icon: 2,time: 2000});
				$('input:checkbox[rowId="' + $(this).attr("rowId") + '"]').attr("checked", false);
				form.render('checkbox');
			}
		}else{
			num--;
		}
	});
	
	//确定
    $("body").on("click", "#confimChoose", function(){
    	tagReturnList = new Array();
    	$.each($('input:checkbox:checked'),function(){
    		id= $(this).attr("rowId");
			name= $(this).attr("name");
			var j = {
	    			id: $(this).attr("rowId"),
	    			name: $(this).attr("title")
	    		};
			tagReturnList.push(j);
    	});
		parent.tagReturnList = [].concat(tagReturnList);
    	parent.layer.close(index);
    	parent.refreshCode = '0';
    });
	
	//标签
	showGrid({
	 	id: "choosetag",
	 	url: reqBasePath + "forumtag010",
	 	params: {},
	 	pagination: false,
	 	template: getFileContent('tpl/template/checkbox-property.tpl'),
	 	ajaxSendLoadBefore: function(hdb){
	 	},
	 	ajaxSendAfter:function(j){
	 		if(parent.tagReturnList.length > 0){
	 			tagReturnList = [].concat(parent.tagReturnList);
	 			num = tagReturnList.length;
	 			for(var j = 0; j < tagReturnList.length; j++){
	 				$('input:checkbox[rowId="' + tagReturnList[j].id + '"]').attr("checked", true);
	 	    	}
	 		}
	 		form.render('checkbox');
	 		matchingLanguage();
	 	}
	});
	
    exports('choosetag', {});
});
