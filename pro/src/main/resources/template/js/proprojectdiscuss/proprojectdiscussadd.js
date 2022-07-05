var layedit;

layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	layui.use(['form', 'layedit'], function (form) {
	    var $ = layui.$,
	    	form = layui.form;
	    	layedit = layui.layedit;
	
	    var rowId = parent.parent.rowId;
	    showGrid({
			id: "typeId",
			url: flowableBasePath + "proprojectdiscusstype008",
			params: {},
			pagination: false,
			template: getFileContent('tpl/template/select-option.tpl'),
			ajaxSendLoadBefore: function(hdb){
			},
			ajaxSendAfter: function (json) {
				form.render('select');
			}
		});
	    
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
	    
	    matchingLanguage();
	    form.on('submit(formAddBean)', function (data) {
	    	
	        if (winui.verifyForm(data.elem)) {
        		var params = {
        			proId: rowId,
        			title: $("#title").val(),
        			typeId: $("typeId").val()
        		};
				if (isNull(layedit.getContent(completedContent))){
					winui.window.msg('请填写发帖内容', {icon: 2, time: 2000});
					return false;
				} else {
					params.content = encodeURIComponent(layedit.getContent(completedContent));
				}
    			AjaxPostUtil.request({url: flowableBasePath + "prodiscuss002", params: params, type: 'json', callback: function (json) {
					parent.layer.close(index);
					parent.refreshCode = '0';
    			}});
	        }
	        return false;
	    });

		$("body").on("click", "#cancle", function() {
			parent.layer.close(index);
		});
		
	});
});