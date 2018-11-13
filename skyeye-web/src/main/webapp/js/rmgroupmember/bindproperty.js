
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
	    var $ = layui.$,
	    form = layui.form;
	    
	    //标签属性
	    showGrid({
	     	id: "propertyList",
	     	url: reqBasePath + "rmproperty006",
	     	params: {},
	     	pagination: false,
	     	template: getFileContent('tpl/template/checkbox-property.tpl'),
	     	ajaxSendLoadBefore: function(hdb){
	     	},
	     	ajaxSendAfter:function(json){
	     		form.render('checkbox');
	     	}
	    });
		 		
 		//搜索表单
 		form.render();
 		
	    form.on('submit(formAddBean)', function (data) {
	    	//表单验证
	        if (winui.verifyForm(data.elem)) {
	        	var propertyIds = "";
	        	$.each($('input:checkbox:checked'),function(){
	        		propertyIds = propertyIds + $(this).attr("rowId") + ",";
	            });
	        	if(isNull(propertyIds)){
	        		top.winui.window.msg('请先选择标签属性。', {icon: 2,time: 2000});
	        	}else{
	        		var params = {
	        			rowId: parent.rowId,
	        			propertyIds: propertyIds
	        		};
	        		AjaxPostUtil.request({url:reqBasePath + "rmxcx038", params:params, type:'json', callback:function(json){
						if(json.returnCode == 0){
							parent.layer.close(index);
							parent.refreshCode = '0';
						}else{
							top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
						}
					}});
	        	}
	        }
	        return false;
	    });
	    
	    //取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	    
	});
	    
});