
var params = '';

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form', 'codemirror', 'xml', 'clike', 'css', 'htmlmixed', 'javascript', 'nginx',
	           'solr', 'sql', 'vue'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	form = layui.form;
	    
    	matchingLanguage();
		form.render();
		
		showDataUseHandlebars("tableColumn", getFileContent('tpl/template/table-column.tpl'), parent.tableColumn);
		form.render('select');
		
		form.on('select(tableColumn)', function(data){
			returnResult();
 		});
		
		form.on('select(showModel)', function(data){
 			AjaxPostUtil.request({url: flowableBasePath + "dsform007", params:{rowId: data.value}, type: 'json', callback: function(json){
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
    				returnResult();
    			}else{
    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
    		}});
 		});
		
		form.on('checkbox(checkboxLimit)', function(data){
			returnResult();
 		});
		
		//展现形式
		showGrid({
    	 	id: "showModel",
    	 	url: flowableBasePath + "dsform006",
    	 	params: {},
    	 	pagination: false,
    	 	template: getFileContent('tpl/template/select-option.tpl'),
    	 	ajaxSendLoadBefore: function(hdb){
    	 	},
    	 	ajaxSendAfter:function(json){
    	 		form.render('select');
    	 		//限制条件
    	 		showGrid({
	 	    	 	id: "limitRequire",
	 	    	 	url: flowableBasePath + "dsformlimitrequirement006",
	 	    	 	params: {},
	 	    	 	pagination: false,
	 	    	 	template: getFileContent('tpl/template/checkbox-limit.tpl'),
	 	    	 	ajaxSendLoadBefore: function(hdb){
	 	    	 	},
	 	    	 	ajaxSendAfter:function(json){
	 	    	 		form.render('checkbox');
	 	    	 	}
	 	        });
    	 	}
        });
		
		var htmlModelEditor = CodeMirror.fromTextArea(document.getElementById("htmlModelEditor"), {
            mode : "text/x-java",  // 模式
            theme : "eclipse",  // CSS样式选择
            indentUnit : 4,  // 缩进单位，默认2
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
            indentUnit : 4,  // 缩进单位，默认2
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
            indentUnit : 4,  // 缩进单位，默认2
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
            indentUnit : 4,  // 缩进单位，默认2
            smartIndent : true,  // 是否智能缩进
            tabSize : 4,  // Tab缩进，默认4
            readOnly : false,  // 是否只读，默认false
            showCursorWhenSelecting : true,
            lineNumbers : true,  // 是否显示行号
            styleActiveLine: true, //line选择是是否加亮
            matchBrackets: true,
        });
		
	    form.on('submit(formAddBean)', function (data) {
	        if (winui.verifyForm(data.elem)) {
	        	var result = {
        			formermation: params.require,
        			aftermation: params.aftermation,
        			htmlContent: htmlSuccessEditor.getValue(),
        			jsContent: jsSuccessEditor.getValue(),
	        	};
	        	parent.result = result;
	        	parent.layer.close(index);
				parent.refreshCode = '0';
	        }
	        return false;
	    });
	    
	    $("body").on("keyup", "#placeholderName", function(e){
	    	returnResult();
	    });
	    
	    $("body").on("keyup", "#valueName", function(e){
	    	returnResult();
	    })
	    
	    function returnResult(){
	    	var tableColumn = $("#tableColumn").val();
	    	var labelName = "", id = "", name = "", formermation = "", aftermation = "";
	    	if(!isNull(tableColumn)){
	    		labelName = tableColumn.split('--')[0];
	    		id = replaceUnderLineAndUpperCase(tableColumn.split('--')[1]);
	    		name = replaceUnderLineAndUpperCase(tableColumn.split('--')[1]);
	    	}
	    	
	    	if(!isNull(htmlModelEditor.getValue())){
	    		$.each($('input:checkbox:checked'),function(){
		    		formermation = formermation + $(this).attr("formermation") + "|";
		    		aftermation = aftermation + $(this).attr("aftermation") + ",";
	            });
	    		params = {
    				labelContent: labelName,
    				id: id,
    				name: name,
    				require: formermation,
    				aftermation: aftermation,
    				value: $("#valueName").val(),
    				placeholder: $("#placeholderName").val(),
	    		};
	    		htmlSuccessEditor.setValue(getDataUseHandlebars(htmlModelEditor.getValue(), params));
	    		if(!isNull(jsModelEditor.getValue())){
	    			jsSuccessEditor.setValue(getDataUseHandlebars(jsModelEditor.getValue(), params));
	    		}
	    	}
	    }
	    
	    //取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	    
	    /**
	     * 将字段转为Java经常使用的名字，如code_model转codeModel
	     */
	    function replaceUnderLineAndUpperCase(str){
	    	str = str.split("");
	    	var count = str.indexOf("_");
	    	while (count != 0) {
				var num = str.indexOf("_", count);
				count = num + 1;
				if (num != -1) {
					var ss = str[count];
					var ia = ss.toUpperCase();
					str.splice(count, 1, ia);
				}
			}
	    	return str.join("").replace(/[_]/g, "");
	    }
	    
	});
	    
});