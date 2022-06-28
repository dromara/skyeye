var layedit;

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
	
	    var rowId = parent.disRowId;//讨论板id
	    var leftTemplate = $("#leftTemplate").html();
	    var rightTemplate = $("#rightTemplate").html();
	    
	    AjaxPostUtil.request({url: flowableBasePath + "prodiscuss005", params: {rowId: rowId}, type: 'json', callback: function (json) {
			if (json.returnCode == 0){
				$(".disheader").html(json.bean.title);
				$(".disdesc").html('由 ' + json.bean.createName + ' 于 ' + json.bean.createTime + '发布');
				$(".discontent").html(json.bean.content);
				var jsonStr;
				var str = "";
				$.each(json.bean.replyString, function(i, item){
   					jsonStr = {
						bean: item
					};
   					if(item.createId === json.bean.createId){//左侧
   						str += getDataUseHandlebars(leftTemplate, jsonStr);
   					} else {//右侧
   						str += getDataUseHandlebars(rightTemplate, jsonStr);
   					}
   				});
   				$(".disreplay").html(str);
   				matchingLanguage();
			} else {
				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			}
		}});
	    
	    layedit.set({
	    	uploadImage: {
	    		url: reqBasePath + "common003", //接口url
    			type: 'post', //默认post
    			data: {
    				type: '13'
    			}
	    	}
	    });
	    var layEditParams = {
	    	tool: [
    	       'html',
    	        'strong', //加粗
    	        'italic', //斜体
    	        'underline', //下划线
    	        'del', //删除线
    	        'addhr',
    	        '|',
    	        'removeformat',
    	        'fontFomatt',
    	        'fontfamily',
    	        'fontSize',
    	        'colorpicker',
    	        'fontBackColor',
    	        'face', //表情
    	        '|', //分割线
    	        'left', //左对齐
    	        'center', //居中对齐
    	        'right', //右对齐
    	        'link', //超链接
    	        'unlink', //清除链接
    	        'code',
    	        'image', //插入图片
    	        'attachment',
    	        'table',
    	        '|',
    	        'fullScreen',
    	        'preview',
    	        '|',
    	        'help'
    	     ],
    	     uploadFiles: {
    	 		url: reqBasePath + "common003",
    	 		accept: 'file',
    	 		acceptMime: 'file/*',
    	 		size: '20480',
    	 		data: {
    				type: '13'
    			},
    	 		autoInsert: true, //自动插入编辑器设置
    	 		done: function(data) {
    	 		}
    	 	}
	    };
	    var completedContent = layedit.build('content', layEditParams);
	    
	    form.on('submit(formAddBean)', function (data) {
	    	
	        if (winui.verifyForm(data.elem)) {
        		var params = {
        			discussId: rowId,//讨论板id
        			proId: parent.rowId//项目id
        		};
				if (isNull(layedit.getContent(completedContent))){
					winui.window.msg('请填写回复内容', {icon: 2, time: 2000});
					return false;
				} else {
					params.content = encodeURIComponent(layedit.getContent(completedContent));
				}
    			AjaxPostUtil.request({url: flowableBasePath + "prodiscuss003", params: params, type: 'json', callback: function (json) {
    				if (json.returnCode == 0){
    					winui.window.msg("提交成功", {icon: 1, time: 2000}, function() {
    						location.reload();
    					});
    				} else {
    					winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    				}
    			}});
	        }
	        return false;
	    });

		$("body").on("click", "#cancle", function() {
			parent.layer.close(index);
		});
		
	});
});