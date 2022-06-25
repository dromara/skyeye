
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window',
}).define(['window', 'jquery', 'winui', 'fileUpload'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    
	    showGrid({
		 	id: "parentId",
		 	url: reqBasePath + "appworkpage001",
		 	params: {},
		 	pagination: false,
		 	template: getFileContent('tpl/template/select-option.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function(json){
		 		form.render('select');
		 	}
		});
	    
	    //初始化上传
	    $("#logo").upload({
           "action": reqBasePath + "common003",
           "data-num": "1",
           "data-type": "PNG,JPG,jpeg,gif",
           "uploadType": 6,
           "function": function (_this, data) {
               show("#logo", data);
           }
	    });
	    
	    matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	var params = {
        			title: $("#title").val(),
        			url: $("#url").val(),
					parentId: $("#parentId").val(),
					urlType: data.field.urlType
 	        	};
 	        	params.logo = $("#logo").find("input[name='upload']").attr("oldurl");
 	        	AjaxPostUtil.request({url:reqBasePath + "appworkpage004", params:params, type: 'json', callback: function(json){
	 	   			if (json.returnCode == 0) {
		 	   			parent.layer.close(index);
		 	        	parent.refreshCode = '0';
		 	        	parent.chooseId = params.parentId;//将选中的目录id传给父页面
	 	   			}else{
	 	   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
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