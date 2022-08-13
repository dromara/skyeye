
var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'codemirror', 'xml', 'clike', 'css', 'htmlmixed', 'javascript', 'nginx', 'solr', 'sql', 'vue'], function (exports) {
	winui.renderColor();
	
	var $ = layui.$,
		form = layui.form,
		table = layui.table;
	
	authBtn('1560828282644');
	
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: sysMainMation.rmprogramBasePath + 'rmproperty001',
	    where: {title: $("#title").val(), propertyTag: $("#propertyTag").val(), dsFormContentId: $("#dsFormContentId").val()},
	    even: true,
	    page: true,
	    limits: [8, 16, 24, 32, 40, 48, 56],
	    limit: 8,
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'title', title: '属性别名', width: 180 },
	        { field: 'propertyTag', title: '属性标签', width: 100 },
	        { field: 'propertyUnit', title: '属性单位', width: 100 },
	        { field: 'selChildData', title: '子查询', width: 100 },
	        { field: 'templateName', title: '子模板', width: 150 },
	        { field: 'propertyOut', title: '外部属性', width: 100 },
	        { field: 'contentName', title: '展现形式', width: 120 },
	        { field: 'propertyValueNum', title: '属性值数量', width: 100 },
	        { field: 'useNum', title: '使用数量', width: 100 },
	        { field: 'id', title: 'HTML内容', width: 100, templet: function (d) {
	        	if(!isNull(d.htmlContent)){
	        		return '<i class="fa fa-fw fa-html5 cursor" lay-event="htmlContent"></i>';
	        	} else {
	        		return '无';
	        	}
	        }},
	        { field: 'id', title: 'JS内容', width: 80, templet: function (d) {
	        	if(!isNull(d.jsContent)){
	        		return '<i class="fa fa-fw fa-html5 cursor" lay-event="jsContent"></i>';
	        	} else {
	        		return '无';
	        	}
	        }},
	        { field: 'id', title: 'JS依赖文件', width: 120, templet: function (d) {
	        	if(!isNull(d.jsRelyOn)){
	        		return '<i class="fa fa-fw fa-html5 cursor" lay-event="jsRelyOn"></i>';
	        	} else {
	        		return '无';
	        	}
	        }},
	        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], width: 180 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 240, toolbar: '#tableBar'}
	    ]],
	    done: function(){
	    	matchingLanguage();
	    }
	});
	
	var editor = CodeMirror.fromTextArea(document.getElementById("modelContent"), {
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
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'del') { //删除
        	del(data, obj);
        }else if (layEvent === 'edit') { //编辑
        	edit(data);
        }else if (layEvent === 'htmlContent') { //HTML内容
        	var mode = returnModel('html');
        	if (!isNull(mode.length)) {
				editor.setOption('mode', mode)
			} 
        	editor.setValue(data.htmlContent);
        	layer.open({
	            id: 'HTML内容',
	            type: 1,
	            title: 'HTML内容',
	            shade: 0.3,
	            area: ['700px', '90vh'],
	            content: $("#modelContentDiv").html(),
	        });
        }else if (layEvent === 'jsContent') { //JS内容
        	var mode = returnModel('javascript');
        	if (!isNull(mode.length)) {
				editor.setOption('mode', mode)
			} 
        	editor.setValue(data.jsContent);
        	layer.open({
	            id: 'HTML-JS内容',
	            type: 1,
	            title: 'HTML-JS内容',
	            shade: 0.3,
	            area: ['700px', '90vh'],
	            content: $("#modelContentDiv").html(),
	        });
        }else if (layEvent === 'jsRelyOn') { //JS依赖文件
        	var mode = returnModel('javascript');
        	if (!isNull(mode.length)) {
				editor.setOption('mode', mode)
			} 
        	editor.setValue(data.jsRelyOn);
        	layer.open({
	            id: 'HTML-JS内容',
	            type: 1,
	            title: 'HTML-JS内容',
	            shade: 0.3,
	            area: ['700px', '90vh'],
	            content: $("#modelContentDiv").html(),
	        });
        }
    });
	
	
	form.render();
	form.on('submit(formSearch)', function (data) {
    	
        if (winui.verifyForm(data.elem)) {
        	refreshTable();
        }
        return false;
	});
	
	//展现形式
  	showGrid({
	 	id: "dsFormContentId",
	 	url: flowableBasePath + "dsform006",
	 	params: {},
	 	pagination: false,
	 	template: getFileContent('tpl/template/select-option.tpl'),
	 	ajaxSendLoadBefore: function(hdb){
	 	},
	 	ajaxSendAfter:function (json) {
	 		form.render('select');
	 	}
    });
	
	//删除
	function del(data, obj){
		var msg = obj ? '确认删除样式属性【' + obj.data.title + '】吗？' : '确认删除选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '删除样式属性' }, function (index) {
			layer.close(index);
            
            AjaxPostUtil.request({url: sysMainMation.rmprogramBasePath + "rmproperty003", params: {rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	//编辑分类
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/rmproperty/rmpropertyedit.html", 
			title: "编辑小程序样式属性",
			pageId: "rmpropertyedit",
			area: ['700px', '90vh'],
			callBack: function(refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
	}
	
	//刷新数据
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    //新增
    $("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: "../../tpl/rmproperty/rmpropertyadd.html", 
			title: "新增小程序样式属性",
			pageId: "rmpropertyadd",
			area: ['700px', '90vh'],
			callBack: function(refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });
    
    function loadTable(){
    	table.reloadData("messageTable", {where:{title: $("#title").val(), propertyTag: $("#propertyTag").val(), dsFormContentId: $("#dsFormContentId").val()}});
    }
    
    function refreshTable(){
    	table.reloadData("messageTable", {page: {curr: 1}, where:{title: $("#title").val(), propertyTag: $("#propertyTag").val(), dsFormContentId: $("#dsFormContentId").val()}});
    }
    
    exports('rmpropertylist', {});
});
