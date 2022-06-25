var jsonStr = [];
var tplContentVal; //数据模板中用{{}}包裹的词

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'element'], function (exports) {
	winui.renderColor();
	layui.use(['form', 'codemirror', 'xml', 'clike', 'css', 'htmlmixed', 'javascript', 'nginx',
	           'solr', 'sql', 'vue'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
			element = layui.element,
	    	form = layui.form;

	    // 根据类型获取部分功能的使用说明
		systemCommonUtil.queryExplainMationByType(2, function(json) {
			$(".layui-colla-title").html(json.bean.title);
			$(".layui-colla-content").html(json.bean.content);
		});
		element.init();

	    //是否关联数据
 		form.on('switch(linkedData)', function (data) {
 			//关联数据值
 			$(data.elem).val(data.elem.checked);
 			if($("#linkedData").val() == 'true'){
 				$(".dataTpl").removeClass("layui-hide");
 				if(!initDatatpl){
 					initDataShowTpl();//初始化关联的数据类型
 				}
 			} else {
 				$(".dataTpl").addClass("layui-hide");
 			}
 		});
 		
 		//初始化关联的数据类型
 		var initDatatpl = false;
 		function initDataShowTpl(){
 			initDatatpl = true;
 			showGrid({
			 	id: "dataShowTpl",
			 	url: flowableBasePath + "dsformdisplaytemplate006",
			 	params: {},
			 	pagination: false,
			 	template: getFileContent('tpl/template/select-option.tpl'),
			 	ajaxSendLoadBefore: function(hdb){},
			 	ajaxSendAfter:function(json){
			 		form.render('select');
			 		jsonStr = json.rows;
			 	}
		    });
 		}
 		//数据展示模板监听事件
		form.on('select(dataShowTpl)', function(data){
			dataShowTplValue = $('#dataShowTpl').val();
			if(dataShowTplValue.length == 0){
				$("#templateContent").html("");
			} else {
				$.each(jsonStr, function(i, item){
		 			if(dataShowTplValue == item.id){
		 				var str = '<textarea class="layui-textarea" readonly>' + item.templateContent + '</textarea>';
		 				$("#templateContent").html(str);
		 				tplContentVal = strMatchAllByTwo(item.templateContent, '{{','}}');//取出数据模板中用{{}}包裹的词
		 				removeByValue(tplContentVal, "#each this");
		 				removeByValue(tplContentVal, "/each");
		 				return false;
		 			}
		 		});
			}
		});
		function removeByValue(arr, val){
			for(var i = 0; i < arr.length; i++){
				if(arr[i] == val) {
					arr.splice(i, 1);
					break;
				}
			}
		}
		
		//取出json串的键
		function getOutKey(arr){
			var jsonObj = $.parseJSON(arr);
			var a = [];
			var b = [];
			for(var i = 0; i < jsonObj.length; i++){
				for (var key in jsonObj[i])
					a.push(key); 
				b.push(a);
				a = [];
			}
			return b;
		}
		
		//B的子集是否是A的子集
		function subset(A,B){
			for(var i = 0; i < B.length; i++){
				if(!isContained(B[i], A)){
					return false;
				}
			}
			return true;
		}
		
		//b是否被a包含,是返回true,不是返回false
		isContained =(a, b)=>{
			if(!(a instanceof Array) || !(b instanceof Array)) 
		    	return false;
		    if(a.length < b.length) 
		    	return false;
		    var aStr = a.toString();
		    for(var i = 0, len = b.length; i < len; i++){
		    	if(aStr.indexOf(b[i]) == -1) 
		    		return false;
		    }
		    return true;
		}
		
	    var htmlEditor = CodeMirror.fromTextArea(document.getElementById("htmlContent"), {
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
	    
	    var jsEditor = CodeMirror.fromTextArea(document.getElementById("jsContent"), {
            mode : "text/javascript",  // 模式
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
	    form.on('submit(formAddBean)', function (data) {
	        if (winui.verifyForm(data.elem)) {
	        	if(isNull(htmlEditor.getValue())){
	        		winui.window.msg('请输入模板内容', {icon: 2, time: 2000});
	        	} else {
	        		var params = {
        				contentName: $("#contentName").val(),
        				htmlContent: encodeURIComponent(htmlEditor.getValue()),
        				htmlType: $("#htmlType").val(),
        				jsContent: encodeURIComponent(jsEditor.getValue()),
        				jsType: $("#jsType").val(),
	        		};
	        		if($("#linkedData").val() == 'true'){
	 	        		params.linkedData = '1';
	 	        		params.dataShowTpl = $("#dataShowTpl").val();
	 	        		if(isNull(params.dataShowTpl)){
	 	 	        		winui.window.msg('请选择数据展示模板', {icon: 2, time: 2000});
	 	 	        		return false;
	 	 	        	}
	 	        		var defaultDataStr = $("#defaultData").val();//默认数据值
						if (defaultDataStr.length != 0) {
							if (isJSON(defaultDataStr)) {
								var defaultKey = getOutKey(defaultDataStr);//从默认数据中取出json串的键
								if (subset(tplContentVal, defaultKey)) {
									params.defaultData = defaultDataStr;
								} else {
									winui.window.msg('默认数据内容有误，请重新填写!', {icon: 2, time: 2000});
									return false;
								}
							} else {
								winui.window.msg('默认数据格式不正确，请重新填写!', {icon: 2, time: 2000});
								return false;
							}
						} else {
							winui.window.msg('请填写默认数据', {icon: 2, time: 2000});
							return false;
						}
					} else {
						params.linkedData = '2';
						params.dataShowTpl = "";
						params.defaultData = "";
					}
	        		AjaxPostUtil.request({url: flowableBasePath + "dsform002", params: params, type: 'json', callback: function(json) {
						if (json.returnCode == 0) {
							parent.layer.close(index);
							parent.refreshCode = '0';
						} else {
							winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
						}
	        		}});
	        	}
	        }
	        return false;
	    });
	    
	    // 取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	    
	});
});