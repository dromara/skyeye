layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
    var $ = layui.$;
    var noteId = parent.noteId;
    // 定义此变量用于editormd初始加载时也会触发onchange方法
    var initFirst = false;

    var layEditor = editormd("content", {
        width: ($(".file-console").width() - 5),
        height: ($(".file-console").height() - 20),
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
			AjaxPostUtil.request({url:reqBasePath + "mynote008", params: {rowId: noteId}, type:'json', callback:function(json){
				if(json.returnCode == 0){
					parent.$("#noteTitle").val(json.bean.title);
					layEditor.setMarkdown(json.bean.content);
					matchingLanguage();
				}else{
					winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
				}
			}});
        },
        onchange : function (){
            if(!initFirst){
                initFirst = true;
            }else{
                parent.$("#editMyNote").addClass('select');
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