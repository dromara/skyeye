
layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form', 'layedit'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	form = layui.form;
	    	layedit = layui.layedit;
	
	    var id = GetUrlParam("id");
	    var replyTemplate = $("#replyTemplate").html();

	    AjaxPostUtil.request({url: reqBasePath + "queryDiscussionById", params: {id: id}, type: 'json', method: 'GET', callback: function (json) {
			$(".disheader").html(json.bean.title);
			$(".disdesc").html('由 ' + json.bean.createName + ' 于 ' + json.bean.createTime + '发布');
			$(".discontent").html(json.bean.content);

			showGrid({
				id: "disreplay",
				url: reqBasePath + "queryDiscussionReplyList",
				params: {discussionId: id},
				pagination: false,
				template: replyTemplate,
				method: "POST",
				ajaxSendLoadBefore: function(hdb) {},
				ajaxSendAfter:function(j) {
				}
			});
			matchingLanguage();
		}});
	    
	    form.on('submit(formAddBean)', function (data) {
	        if (winui.verifyForm(data.elem)) {
        		var params = {
					discussionId: id,
					content: $("#content").val(),
					replyId: "0"
        		};
    			AjaxPostUtil.request({url: reqBasePath + "createDiscussionReply", params: params, type: 'json', method: 'POST', callback: function (json) {
					winui.window.msg("提交成功", {icon: 1, time: 2000}, function() {
						location.reload();
					});
    			}});
	        }
	        return false;
	    });

		$("body").on("click", "#cancle", function() {
			parent.layer.close(index);
		});
		
	});
});