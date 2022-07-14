
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
	    
	    var useTemplate = $("#useTemplate").html();
	    
	    AjaxPostUtil.request({url: flowableBasePath + "asset021", params:{rowId: parent.rowId}, type: 'json', callback: function (json) {
			//状态
			if(json.bean.state == '0'){
				json.bean.stateName = "<span>" + json.bean.stateName + "</span>";
			}else if(json.bean.state == '1'){
				json.bean.stateName = "<span class='state-new'>" + json.bean.stateName + "</span>";
			}else if(json.bean.state == '2'){
				json.bean.stateName = "<span class='state-up'>" + json.bean.stateName + "</span>";
			}else if(json.bean.state == '3'){
				json.bean.stateName = "<span class='state-down'>" + json.bean.stateName + "</span>";
			}else if(json.bean.state == '4'){
				json.bean.stateName = "<span class='state-down'>" + json.bean.stateName + "</span>";
			}else if(json.bean.state == '5'){
				json.bean.stateName = "<span class='state-error'>" + json.bean.stateName + "</span>";
			}

			$("#showForm").html(getDataUseHandlebars(useTemplate, json));

			// 附件回显
			skyeyeEnclosure.showDetails({"enclosureUploadBtn": json.bean.enclosureInfo});
			matchingLanguage();
	    }});

	    //图片查看
	    $("body").on("click", ".photo-img", function() {
	    	var src = $(this).attr("src");
	    	layer.open({
	    		type:1,
	    		title:false,
	    		closeBtn:0,
	    		skin: 'demo-class',
	    		shadeClose:true,
	    		content:'<img src="' + src + '" style="max-height:600px;max-width:100%;">',
	    		scrollbar:false
	        });
	    });
	    
	});
});