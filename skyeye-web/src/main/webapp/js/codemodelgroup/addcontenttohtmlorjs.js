
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
	    
		form.render();
		
		showDataUseHandlebars("tableColumn", getFileContent('tpl/template/table-column.tpl'), parent.tableColumn);
		form.render('select');
		
		//展现形式
		showGrid({
    	 	id: "showModel",
    	 	url: reqBasePath + "dsform006",
    	 	params: {},
    	 	pagination: false,
    	 	template: getFileContent('tpl/template/select-option.tpl'),
    	 	ajaxSendLoadBefore: function(hdb){
    	 	},
    	 	ajaxSendAfter:function(json){
    	 		
    	 		//限制条件
    	 		showGrid({
	 	    	 	id: "limitRequire",
	 	    	 	url: reqBasePath + "dsformlimitrequirement006",
	 	    	 	params: {},
	 	    	 	pagination: false,
	 	    	 	template: getFileContent('tpl/template/checkbox-limit.tpl'),
	 	    	 	ajaxSendLoadBefore: function(hdb){
	 	    	 	},
	 	    	 	ajaxSendAfter:function(json){
	 	    	 		form.render();
	 	    	 	}
	 	        });
    	 		
    	 		form.on('select(selectParent)', function(data){
    	 			AjaxPostUtil.request({url:reqBasePath + "dsform007", params:{rowId: data.value}, type:'json', callback:function(json){
            			if(json.returnCode == 0){
            				var mode = returnModel(json.bean.htmlType);
            				if (!isNull(mode.length)) {
            					htmlModelEditor.setOption('mode', mode);
            					htmlSuccessEditor.setOption('mode', mode);
            				} 
            				htmlModelEditor.setValue(json.bean.htmlContent);
            				
            				mode = returnModel(json.bean.jsType);
            				if (!isNull(mode.length)) {
            					jsModelEditor.setOption('mode', mode);
            					jsSuccessEditor.setOption('mode', mode);
            				} 
            				jsModelEditor.setValue(json.bean.jsContent);
            				
            			}else{
            				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
            			}
            		}});
    	 		});
    	 	}
        });
		
		var htmlModelEditor = CodeMirror.fromTextArea(document.getElementById("htmlModelEditor"), {
            mode : "text/x-java",  // 模式
            theme : "eclipse",  // CSS样式选择
            indentUnit : 2,  // 缩进单位，默认2
            smartIndent : true,  // 是否智能缩进
            tabSize : 4,  // Tab缩进，默认4
            readOnly : true,  // 是否只读，默认false
            showCursorWhenSelecting : true,
            lineNumbers : true,  // 是否显示行号
            styleActiveLine: true, //line选择是是否加亮
            matchBrackets: true,
        });
		var jsModelEditor = CodeMirror.fromTextArea(document.getElementById("jsModelEditor"), {
            mode : "text/x-java",  // 模式
            theme : "eclipse",  // CSS样式选择
            indentUnit : 2,  // 缩进单位，默认2
            smartIndent : true,  // 是否智能缩进
            tabSize : 4,  // Tab缩进，默认4
            readOnly : true,  // 是否只读，默认false
            showCursorWhenSelecting : true,
            lineNumbers : true,  // 是否显示行号
            styleActiveLine: true, //line选择是是否加亮
            matchBrackets: true,
        });
		
		var htmlSuccessEditor = CodeMirror.fromTextArea(document.getElementById("htmlSuccessEditor"), {
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
		
		var jsSuccessEditor = CodeMirror.fromTextArea(document.getElementById("jsSuccessEditor"), {
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
		
	    form.on('submit(formAddBean)', function (data) {
	    	//表单验证
	        if (winui.verifyForm(data.elem)) {
	        	
	        }
	        return false;
	    });
	    
	    //取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	    
	});
	    
});