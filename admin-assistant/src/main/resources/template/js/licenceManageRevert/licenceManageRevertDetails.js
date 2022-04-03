
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
	    var revertTemplate = $("#revertTemplate").html();
	    
	    AjaxPostUtil.request({url:flowableBasePath + "licencerevert003", params:{rowId: parent.rowId}, type:'json', callback:function(json){
    		if(json.returnCode == 0) {
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
    			//附件回显
    			var str = "";
                if(json.bean.enclosureInfo.length != 0 && !isNull(json.bean.enclosureInfo)){
                    $.each([].concat(json.bean.enclosureInfo), function(i, item){
                        str += '<a rowid="' + item.id + '" class="enclosureItem" rowpath="' + item.fileAddress + '" href="javascript:;" style="color:blue;">' + item.name + '</a><br>';
                    });
                }
    			var _html = getDataUseHandlebars(revertTemplate, json);//加载数据
    			$("#showForm").html(_html);
    			$("#enclosureUploadBtn").html(str);
    			matchingLanguage();
    		}else {
    			winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    		}
	    }});
	    
	    $("body").on("click", ".enclosureItem", function(){
	    	download(fileBasePath + $(this).attr("rowpath"), $(this).html());
	    });
	    
	});
});