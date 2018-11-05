
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form', 'codemirror', 'xml', 'clike', 'css', 'htmlmixed', 'javascript', 'nginx',
	           'solr', 'sql', 'vue'], function (form) {
		var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
	    var $ = layui.$,
	    form = layui.form;
	    
	    AjaxPostUtil.request({url:reqBasePath + "exexplaintocodemodel002", params:{}, type:'json', callback:function(json){
   			if(json.returnCode == 0){
   				layer.open({
   					type: 1,
   					closeBtn: 0,//关闭按钮
   					resize: false,//是否允许拉伸
   					offset: 'l', // 具体配置参考：offset参数项
   					content: json.bean.content,
   					area: ['200px', '400px'],
   					shade: 0, //不显示遮罩
   					title: json.bean.title
   				});
   			}else{
   				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
   			}
   		}});

	    var editor = CodeMirror.fromTextArea(document.getElementById("modelContent"), {
            mode : "text/x-java",  // 模式
            theme : "eclipse",  // CSS样式选择
            indentUnit : 2,  // 缩进单位，默认2
            smartIndent : true,  // 是否智能缩进
            tabSize : 4,  // Tab缩进，默认4
            readOnly : false,  // 是否只读，默认false
            showCursorWhenSelecting : true,
            lineNumbers : true,  // 是否显示行号
            styleActiveLine: true, //line选择是是否加亮
            matchBrackets: true,
        });
	    
		form.render();
		form.on('select(selectParent)', function(data){
			var lang = $("#modelType").val();
			var mode = returnModel(lang);
			if (!isNull(mode.length)) {
				editor.setOption('mode', mode)
			} 
 		});
		
	    form.on('submit(formAddBean)', function (data) {
	    	//表单验证
	        if (winui.verifyForm(data.elem)) {
	        	if(isNull(editor.getValue())){
	        		top.winui.window.msg('请输入模板内容', {icon: 2,time: 2000});
	        	}else{
	        		var params = {
        				modelName: $("#modelName").val(),
        				modelContent: encodeURI(editor.getValue()),
        				modelText: encodeURI(editor.getValue()),
        				modelType: $("#modelType").val(),
        				groupId: parent.groupId,
	        		};
	        		
	        		AjaxPostUtil.request({url:reqBasePath + "codemodel007", params:params, type:'json', callback:function(json){
	        			if(json.returnCode == 0){
	        				parent.layer.close(index);
	        				parent.refreshCode = '0';
	        			}else{
	        				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
	        			}
	        		}});
	        	}
	        }
	        return false;
	    });
	    
	    //取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	    
	});
	    
});