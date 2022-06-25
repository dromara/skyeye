
var rowId = "";

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
	    
	    rowId = parent.rowId;
	    var url = erpOrderUtil.getErpDetailUrl({subType: parent.subType});
	    $("#showBean").attr("src", url);
	    
	    //为了iframe高度自适应
	    var loadNum = 0;
	    var interval = setInterval(iFrameHeight, 300);
	    function iFrameHeight() {
	        var ifm = document.getElementById("showBean");
	        var subWeb = document.frames ? document.frames["showBean"].document : ifm.contentDocument;
            if(ifm != null && subWeb != null) {
            	ifm.height = subWeb.body.scrollHeight;
            	loadNum++;
            	if(loadNum > 10){
	            	clearInterval(interval);//停止
            	}
            }
	    }
	    
	    matchingLanguage();
 		form.render();
 		form.on('submit(formAddBean)', function (data) {
	        if (winui.verifyForm(data.elem)) {
	        	var msg = '确认提交吗？';
	    		layer.confirm(msg, { icon: 3, title: '提交审批' }, function (i) {
	    			layer.close(i);
	    			var jStr = {
		    			content: $("#opinion").val(),
		    			status: $("input[name='flag']:checked").val(),
		    			rowId: parent.rowId
		            };
		            AjaxPostUtil.request({url: flowableBasePath + "storehouseapproval006", params: jStr, type: 'json', callback: function(json){
		 	   			if (json.returnCode == 0) {
	                    	parent.layer.close(index);
	                    	parent.refreshCode = '0';
		 	   			} else {
		 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
		 	   			}
		 	   		}});
	    		});
	        }
	        return false;
	    });
		
		//取消
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	    
	});
});