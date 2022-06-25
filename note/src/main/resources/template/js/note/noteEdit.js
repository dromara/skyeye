
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
    var $ = layui.$,
	    form = layui.form;
    var ue = null;
    // 定义此变量用于ueditor初始加载时也会触发contentChange方法
    var initFirst = false;
    
    matchingLanguage();
	form.render();
	var noteId = parent.noteId;
	
	// 显示编辑器内容
	AjaxPostUtil.request({url:reqBasePath + "mynote008", params: {rowId: noteId}, type: 'json', callback: function(json){
		if (json.returnCode == 0) {
			ue = ueEditorUtil.initEditor('container');

			parent.$("#noteTitle").val(json.bean.title);
			ue.addListener("ready", function () {
		    	ue.setContent(isNull(json.bean.content) ? "" : json.bean.content);
		    	var height = $(".file-console").height() - $(".edui-editor-toolbarbox").height() - $(".edui-editor-bottomContainer").height() - 15;
				$(".edui-editor-iframeholder").css({'height': height});
				// ctrl+s保存事件
				UE.dom.domUtils.on(ue.body, "keydown", function(e){
					if (e.ctrlKey && (e.which == 83)) {
						e.preventDefault();
						parent.$("#editMyNote").click();
						return false;
					}
				});
		    });
			ue.addListener('contentChange',function(editor){
				if(!initFirst){
					initFirst = true;
				} else {
					parent.$("#editMyNote").addClass('select');
				}
			});
		} else {
			winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
		}
	}});

	// 获取编辑器内容
	window.getContent = function(){
		return encodeURIComponent(ue.getContent());
	}
	
	// 获取纯文本内容
	window.getNoHtmlContent = function(){
		return ue.getContentTxt();
	}
	
});
