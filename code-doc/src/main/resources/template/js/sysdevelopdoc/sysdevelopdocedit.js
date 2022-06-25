layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
		    form = layui.form;
	    
	    AjaxPostUtil.request({url:reqBasePath + "sysdevelopdoc003", params:{rowId: parent.rowId}, type: 'json', callback: function(json){
   			if (json.returnCode == 0) {
   				$("#typeName").val(json.bean.title);

   			    showGrid({
   		    	 	id: "docFirstType",
   		    	 	url: reqBasePath + "sysdevelopdoc006",
   		    	 	params: {rowId: parent.rowId},
   		    	 	pagination: false,
   		    	 	template: getFileContent('tpl/template/select-option.tpl'),
   		    	 	ajaxSendLoadBefore: function(hdb){
   		    	 	},
   		    	 	ajaxSendAfter:function(j){
   		    	 		if(json.bean.parentId != '0'){
   		    	 			$("#docFirstType").val(json.bean.parentId);
   		    	 		}
   		    	 		form.render('select');
   		    	 	}
   		        });
   			    
   			    if(json.bean.parentId == '0' || isNull(json.bean.parentId)){
		 			$("#parentIdBox").addClass("layui-hide");
		 			$("input:radio[name=docType][value=1]").attr("checked", true);
		 		}else{
		 			$("input:radio[name=docType][value=2]").attr("checked", true);
		 		}
		 		
		 		matchingLanguage();
   			    form.render();
   			    form.on('radio(docType)', function (data) {
   		 			var val = data.value;
   			    	if(val == '1'){//一级目录
   			    		$("#parentIdBox").addClass("layui-hide");
   			    	}else if(val == '2'){//二级目录
   			    		$("#parentIdBox").removeClass("layui-hide");
   			    	}else{
   			    		winui.window.msg('状态值错误', {icon: 2, time: 2000});
   			    	}
   		        });
   				
   			    form.on('submit(formEditBean)', function (data) {
   			    	
   			        if (winui.verifyForm(data.elem)) {
   			        	var pId = '0';
   			        	if($("input[name='docType']:checked").val() == '2'){
   			        		if(isNull($("#docFirstType").val())){
   			        			winui.window.msg('请选择一级目录', {icon: 2, time: 2000});
   			        			return false;
   			        		}else{
   			        			pId = $("#docFirstType").val();
   			        		}
   			        	}
   			        	var params = {
   		        			typeName: $("#typeName").val(),
   		        			parentId: pId,
   		        			rowId: parent.rowId
   			        	};
   			        	
   			        	AjaxPostUtil.request({url:reqBasePath + "sysdevelopdoc004", params:params, type: 'json', callback: function(json){
   			 	   			if (json.returnCode == 0) {
   				 	   			parent.layer.close(index);
   				 	        	parent.refreshCode = '0';
   			 	   			}else{
   			 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
   			 	   			}
   			 	   		}});
   			        }
   			        return false;
   			    });
   			}else{
   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
   			}
   		}});
	    
	    //取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	});
	    
});