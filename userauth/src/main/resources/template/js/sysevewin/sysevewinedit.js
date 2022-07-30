
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'fileUpload'], function (exports) {
	winui.renderColor();
	layui.use(['form', 'layedit'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
		    form = layui.form,
		    layedit = layui.layedit;
	    
	    var layContent;
	    
	    AjaxPostUtil.request({url: reqBasePath + "sysevewin003", params:{rowId: parent.rowId}, type: 'json', callback: function (json) {
			if(!isNull(json.bean)) {
				rowId = json.bean.id;
				// 初始化上传
				$("#sysPic").upload(systemCommonUtil.uploadCommon003Config('sysPic', 8, json.bean.sysPic, 1));

				$("#sysName").val(json.bean.sysName);
				$("#sysUrl").val(json.bean.sysUrl);
				$("#content").val(json.bean.sysDesc);
			}

			layedit.set({
				uploadImage: {
					url: reqBasePath + "common003", //接口url
					type: 'post', //默认post
					data: {
						type: '13'
					}
				}
			});

			layContent = layedit.build('content', {
				tool: [
				   'html'
				   ,'strong' //加粗
				   ,'italic' //斜体
				   ,'underline' //下划线
				   ,'del' //删除线
				   ,'addhr'
				   ,'|'
				   ,'removeformat'
				   ,'fontFomatt'
				   ,'fontfamily'
				   ,'fontSize'
				   ,'colorpicker'
				   ,'fontBackColor'
				   ,'face' //表情
				   ,'|' //分割线
				   ,'left' //左对齐
				   ,'center' //居中对齐
				   ,'right' //右对齐
				   ,'link' //超链接
				   ,'unlink' //清除链接
				   ,'code'
				   ,'image' //插入图片
				   ,'attachment'
				   ,'table'
				   ,'|'
				   ,'fullScreen'
				   ,'preview'
				   ,'|'
				   ,'help'
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
			});
   		}});
	    
   		matchingLanguage();
		form.render();
	    form.on('submit(formEditBean)', function (data) {
	        if (winui.verifyForm(data.elem)) {
        		var params = {
    				sysName: $("#sysName").val(),
    				sysUrl: $("#sysUrl").val(),
    				sysDesc: encodeURIComponent(layedit.getContent(layContent)),
    				rowId: parent.rowId,
					sysPic: $("#sysPic").find("input[type='hidden'][name='upload']").attr("oldurl")
        		};
				if (isNull(params.sysPic)) {
					winui.window.msg('请上传系统图片', {icon: 2, time: 2000});
					return false;
				}
    			AjaxPostUtil.request({url: reqBasePath + "sysevewin004", params: params, type: 'json', callback: function (json) {
					parent.layer.close(index);
					parent.refreshCode = '0';
    			}});
	        }
	        return false;
	    });
	    
	    //取消
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	    
	});
	    
});