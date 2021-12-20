var parentId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window',
}).define(['window', 'jquery', 'winui', 'fsTree'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
			fsTree = layui.fsTree;

	    var parentType;
		fsTree.render({
			id: "parentType",
			url: "knowledgetype001?userToken=" + getCookie('userToken') + "&loginPCIp=",
			checkEnable: true,
			loadEnable: false,//异步加载
			chkStyle: "radio",
			showLine: false,
			showIcon: true,
			expandSpeed: 'fast'
		}, function(id){
			parentType = $.fn.zTree.getZTreeObj(id);
			fuzzySearch(id, '#name', null, true); //初始化模糊搜索方法
		});

		matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
				var checkNodes = parentType.getCheckedNodes(true);
 	        	var params = {
 	        		name: $("#typeName").val(),
					parentId: checkNodes.length == 0 ? "0" : checkNodes[0].id
 	        	};
 	        	AjaxPostUtil.request({url:reqBasePath + "knowledgetype002", params:params, type:'json', callback:function(json){
 	        		if(json.returnCode == 0){
 	        			parent.layer.close(index);
 	        			parent.refreshCode = '0';
 	        		}else{
 	        			winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
 	        		}
 	        	}});
 	        }
 	        return false;
 	    });
 	    
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
});