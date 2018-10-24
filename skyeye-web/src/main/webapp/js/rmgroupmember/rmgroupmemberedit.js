
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
	    var $ = layui.$,
	    form = layui.form;
	    
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "rmxcx020",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/rmgroupmember/rmgroupmembereditTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 		hdb.registerHelper("compare1", function(v1, options){
					return '<img src="' + fileBasePath + v1 + '" style="width:100%;height:auto" class="cursor">';
				});
		 	},
		 	ajaxSendAfter:function(json){
		 		//搜索表单
		 		form.render();
		 		
			    form.on('submit(formEditBean)', function (data) {
			    	//表单验证
			        if (winui.verifyForm(data.elem)) {
		 	   			var params = {
		 	   				wxmlContent: encodeURI($("#wxmlContent").val()),
		 	   				wxmlJsContent: encodeURI($("#wxmlJsContent").val()),
		        			rowId: parent.rowId
			        	};
			        	
			        	AjaxPostUtil.request({url:reqBasePath + "rmxcx021", params:params, type:'json', callback:function(json){
			 	   			if(json.returnCode == 0){
				 	   			parent.layer.close(index);
				 	        	parent.refreshCode = '0';
			 	   			}else{
			 	   				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
			 	   			}
			 	   		}});
			        }
			        return false;
			    });
		 	}
	    });
	    
	    //取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	    
	    //图片点击
	    $("body").on("click", ".cursor", function(){
	    	layer.open({
        		type:1,
        		title:false,
        		closeBtn:0,
        		skin: 'demo-class',
        		shadeClose:true,
        		content:'<img src="' + $(this).attr("src") + '" style="max-height:600px;max-width:100%;">',
        		scrollbar:false
            });
	    });
	    
	});
	    
});