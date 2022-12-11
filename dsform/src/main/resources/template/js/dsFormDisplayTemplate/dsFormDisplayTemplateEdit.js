layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['codemirror', 'xml', 'clike', 'css', 'htmlmixed', 'javascript', 'nginx', 'solr', 'sql', 'vue'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
			element = layui.element,
	    	form = layui.form;
	    
		showGrid({
		 	id: "showForm",
		 	url: flowableBasePath + "dsformdisplaytemplate004",
		 	params: {id: parent.rowId},
		 	pagination: false,
			method: 'GET',
		 	template: getFileContent('tpl/dsFormDisplayTemplate/dsFormDisplayTemplateEditTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb) {
		 	},
		 	ajaxSendAfter:function (json) {
		 		
		 		var content = CodeMirror.fromTextArea(document.getElementById("content"), {
		            mode : "xml",  // 模式
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
		 		
		        matchingLanguage();
		 		form.render();

				// 根据类型获取部分功能的使用说明
				systemCommonUtil.queryExplainMationByType(3, function (json) {
					$(".layui-colla-title").html(json.bean.title);
					$(".layui-colla-content").html(json.bean.content);
				});
				element.init();

		 		form.on('submit(formEditBean)', function (data) {
			        if (winui.verifyForm(data.elem)) {
			        	var params = {
							name: $("#name").val(),
							content: encodeURIComponent(content.getValue()),
		        			id: parent.rowId
			        	};
			        	AjaxPostUtil.request({url: flowableBasePath + "writeDsFormDisplayTemplate", params: params, type: 'json', method: 'POST', callback: function (json) {
							parent.layer.close(index);
							parent.refreshCode = '0';
			 	   		}});
			        }
			        return false;
			    });
		 		
		 	}
	    });
		
	    // 取消
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	    
	});
});