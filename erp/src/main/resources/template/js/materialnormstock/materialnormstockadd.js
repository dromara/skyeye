
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
		var selOption = getFileContent('tpl/template/select-option.tpl');
	    
	    //获取列表中已有的仓库库存
	    var normsStock = [].concat(parent.normsStock);
	    
		// 初始化仓库
		erpOrderUtil.getDepotList(function (json){
			$("#storeHouseId").html(getDataUseHandlebars(selOption, json));
			form.render();
		});

		matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	//新增的仓库库存是否在集合中
 	        	var inList = false;
 	        	$.each(normsStock, function(i, item) {
 	        		if(item.depotId === $("#storeHouseId").val()){
 	        			inList = true;
 	        			return false;
 	        		}
 	        	});
 	        	if(inList){
 	        		winui.window.msg("该仓库初始库存已存在.", {icon: 2, time: 2000});
 	        	} else {
	 	        	normsStock.push({
	 	        		depotId: $("#storeHouseId").val(),
	 	        		depotName: $("#storeHouseId").find("option:selected").text(),
	 	        		initialTock: $("#initialTock").val()
	 	        	});
	 	        	//赋值给列表
	 	        	parent.normsStock = [].concat(normsStock);
	    			parent.layer.close(index);
	    			parent.refreshCode = '0';
 	        	}
 	        }
 	        return false;
 	    });
 	    
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});