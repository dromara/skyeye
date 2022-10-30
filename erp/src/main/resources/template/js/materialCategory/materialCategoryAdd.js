layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'fsCommon', 'fsTree', 'element', 'textool'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	fsTree = layui.fsTree,
			fsCommon = layui.fsCommon,
			element = layui.element,
			textool = layui.textool;
	    
		var materialCategoryType;
		
		textool.init({eleId: 'remark', maxlength: 200});
		
	    fsTree.render({
			id: "materialCategoryType",
			url: flowableBasePath + "materialcategory008",
			checkEnable: true,
			loadEnable: false,//异步加载
			chkStyle: "radio",
			showLine: false,
			showIcon: true,
			expandSpeed: 'fast'
		}, function(id){
			materialCategoryType = $.fn.zTree.getZTreeObj(id);
			fuzzySearch(id, '#name', null, true); //初始化模糊搜索方法
		});
	    
		matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	var checkNodes = materialCategoryType.getCheckedNodes(true);
 	        	if(checkNodes.length == 0){
 	        		winui.window.msg('请选择所属类型', {icon: 2, time: 2000});
 	        		return false;
 	        	}
 	        	var params = {
        			name: $("#typeName").val(),
 	        		remark: $("#remark").val(),
 	        		parentId: checkNodes[0].id
 	        	};
 	        	AjaxPostUtil.request({url: flowableBasePath + "writeMaterialCategoryMation", params: params, type: 'json', method: "POST", callback: function (json) {
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