
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    
	    var beanTemplate = $("#beanTemplate").html();
	    
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "sealseservice028",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: beanTemplate,
		 	ajaxSendAfter:function(json){
		 		//相关附件回显
			    if(!isNull(json.bean.enclosureInfo) && json.bean.enclosureInfo.length > 0){
			    	var str = "";
	    			$.each(json.bean.enclosureInfo, function(i, item){
	    				str += '<a rowid="' + item.id + '" class="enclosureItem" rowpath="' + item.fileAddress + '" href="javascript:;" style="color:blue;">' + item.name + '</a><br>';
	    			});
	    			$("#enclosureUpload").html(str);
 	        	}
 	        	
 	        	matchingLanguage();
 	        	form.on('submit(formSubBean)', function (data) {
			        if (winui.verifyForm(data.elem)) {
			        	var msg = '确认提交审核吗？';
			    		layer.confirm(msg, { icon: 3, title: '审核操作' }, function (i) {
			    			layer.close(i);
			    			var jStr = {
				    			opinion: $("#opinion").val(),
				    			isAgree: $("input[name='flag']:checked").val(),
				    			rowId: parent.rowId
				            };
				            AjaxPostUtil.request({url:reqBasePath + "sealseservice030", params: jStr, type: 'json', callback: function(json){
				 	   			if(json.returnCode == 0){
			                    	parent.layer.close(index);
			                    	parent.refreshCode = '0';
				 	   			}else{
				 	   				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
				 	   			}
				 	   		}});
			    		});
			        }
			        return false;
			    });
 	        	
		 		form.render();
		 	}
		});
		
		//附件下载
	    $("body").on("click", ".enclosureItem", function(){
	    	download(fileBasePath + $(this).attr("rowpath"), $(this).html());
	    });
	    
	    $("body").on("click", "#cancle", function() {
			parent.layer.close(index);
		});
	    
	});
});