layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
    var $ = layui.$;
    var noteId = parent.noteId;
    // 因为默认加载目录会触发change函数，所以当loadChange=2时,才出发保存按钮
    var loadChange = 0;

    var layEditor = editormd("content", {
        width: ($(".manage-console").width() - 5),
        height: ($(".manage-console").height() - 20),
        path : 'editormd/lib/',
        theme : "default",
        previewTheme : "default",
        editorTheme : "default",
        codeFold : true,
        syncScrolling : "single",
        watch : true,
        saveHTMLToTextarea : true,    // 保存 HTML 到 Textarea
        searchReplace : true,
        htmlDecode : "style,script,iframe|on*",            // 开启 HTML 标签解析，为了安全性，默认不开启
        emoji : true,
        taskList : true,
        tocm : true,         // Using [TOCM]
        tex : true,                   // 开启科学公式TeX语言支持，默认关闭
        flowChart : true,             // 开启流程图支持，默认关闭
        sequenceDiagram : true,       // 开启时序/序列图支持，默认关闭,
        imageUpload : true,
        imageFormats : imageType,
        imageUploadURL : reqBasePath + "common003?type=11", // 文件上传路径，返回值为图片加载的路径
        onload : function() {
        	initPasteDragImg(this);
            // 加载后富文本编辑器成功后的回调
		    // 显示编辑器内容
			AjaxPostUtil.request({url: sysMainMation.noteBasePath + "queryNoteById", params: {id: noteId}, type: 'json', method: "GET", callback: function (json) {
                parent.$("#noteTitle").val(json.bean.name);
                layEditor.setMarkdown(json.bean.content);
                matchingLanguage();
			}});
        },
        onchange : function (){
            if(loadChange == 2){
                parent.$("#editMyNote").addClass('select');
            } else {
                loadChange++;
                if(loadChange == 1){
                    layEditor.config({
                        tocContainer : "#custom-toc-container",
                        tocDropdown  : false
                    });
                }
            }
        }
    });

    $(document).bind("keydown", function(e) {
        if (e.ctrlKey && (e.which == 83)) {
            e.preventDefault();
            parent.$("#editMyNote").click();
            return false;
        }
    });

	// 获取编辑器内容
	window.getContent = function(){
		return encodeURIComponent(layEditor.getMarkdown());
	}
	
	// 获取纯文本内容
	window.getNoHtmlContent = function(){
		var str = layEditor.getHTML().replace(/<[^>]+>/g,"");
		return str;
	}
});