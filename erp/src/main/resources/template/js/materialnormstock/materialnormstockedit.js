
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
	    
	    //获取列表中已有的仓库库存
	    var normsStock = [].concat(parent.normsStock);
	    //要编辑的仓库id
	    var depotId = parent.chooseDepotId;
	    $.each(normsStock, function(i, item) {
    		if(item.depotId === depotId){
    			$("#stock").val(item.stock);
    			$("#depotName").html(item.depotName);
    			return false;
    		}
    	});
    	
    	matchingLanguage();
 		form.render();
 	    form.on('submit(formEditBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	$.each(normsStock, function(i, item) {
 	        		if(item.depotId === depotId){
 	        			item.stock = $("#stock").val();
 	        			return false;
 	        		}
 	        	});
 	        	//赋值给列表
 	        	parent.normsStock = [].concat(normsStock);
    			parent.layer.close(index);
    			parent.refreshCode = '0';
 	        }
 	        return false;
 	    });
 	    
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});