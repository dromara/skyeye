
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'fileUpload'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    
	    //帖子信息展示
        AjaxPostUtil.request({url:reqBasePath + "forumcontent006", params: {rowId:parent.forumId}, type: 'json', callback: function(json){
            if(json.returnCode == 0){
                $("#content").html(json.bean.content);
                $("#title").html(json.bean.title);
                $("#createTime").html(json.bean.createTime);
                $("#photo").html("<img userId=" + json.bean.userId + " alt='' src=" + json.bean.userPhoto + ">");
                $("#content").removeClass("layui-hide");
                $("#showForm").removeClass("layui-hide");
                matchingLanguage();
            }else{
                winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
            }
        }});
	    
	    //单选框变化事件
 		form.on('radio(examineState)', function (data) {
 			var val = data.value;
	    	if(val == '2'){//审核通过
	    		$("#reasonHide").addClass("layui-hide");
	    		$("#examineNopassReason").val("");
	    	}else if(val == '3'){//审核不通过
	    		$("#reasonHide").removeClass("layui-hide");
	    	}else{
	    		winui.window.msg('状态值错误', {icon: 2,time: 2000});
	    	}
        });
	    
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	var params = {
        			rowId: parent.rowId,
 	        		examineState: data.field.examineState,
        			examineNopassReason: $("#examineNopassReason").val()
 	        	};
 	        	AjaxPostUtil.request({url:reqBasePath + "forumreport003", params:params, type: 'json', callback: function(json){
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