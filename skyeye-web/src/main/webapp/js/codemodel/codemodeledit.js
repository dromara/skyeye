
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
	    var editor;
	    
	    AjaxPostUtil.request({url:reqBasePath + "exexplaintocodemodel004", params:{}, type:'json', callback:function(json){
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
	    
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "codemodel009",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/codemodel/codemodeleditTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function(json){
		 		
		 		editor = CodeMirror.fromTextArea(document.getElementById("modelContent"), {
		 			mode : returnModel(json.bean.modelType),  // 模式
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
		 		
		 		$("#modelType").val(json.bean.modelType);
		 		
		 		form.render();
		 		form.on('select(selectParent)', function(data){
					var mode = returnModel($("#modelType").val());
					if (!isNull(mode.length)) {
						editor.setOption('mode', mode)
					} 
		 		});
		 		
		 		form.on('submit(formEditBean)', function (data) {
			    	//表单验证
			        if (winui.verifyForm(data.elem)) {
			        	if(isNull(editor.getValue())){
			        		top.winui.window.msg('请输入模板内容', {icon: 2,time: 2000});
			        	}else{
				        	var params = {
			        			modelName: $("#modelName").val(),
			        			modelContent: encodeURI(editor.getValue()),
			        			modelText: encodeURI(editor.getValue()),
			        			groupId: parent.groupId,
			        			rowId: parent.rowId
				        	};
				        	
				        	AjaxPostUtil.request({url:reqBasePath + "codemodel010", params:params, type:'json', callback:function(json){
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
		 		
		 	}
	    });
	    
	    //取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	    
	});
	    
});