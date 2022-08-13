
var groupId = "";
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'codemirror', 'xml', 'clike', 'css', 'htmlmixed', 'javascript', 'nginx',
   'solr', 'sql', 'vue'], function (exports) {
	winui.renderColor();
	//模板分组ID
	groupId = parent.rowId;
	var $ = layui.$,
		form = layui.form,
		table = layui.table;
	
	authBtn('1560217136068');
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'codemodel006',
	    where:{groupId:groupId},
	    even:true,
	    page: true,
		limits: getLimits(),
		limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'modelName', title: '模板后缀', width: 120 },
	        { field: 'modelType', title: '模板类型', width: 120 },
	        { field: 'id', title: '模板内容', width: 120, templet: function (d) {
	        	return '<i class="fa fa-fw fa-html5 cursor" lay-event="modelContent"></i>';
	        }},
	        { field: 'useNum', title: '调用次数', width: 120 },
			{ field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
			{ field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
			{ field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150},
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
        }else if (layEvent === 'modelContent') { //查看代码内容
        	var mode = returnModel(data.modelType);
        	if (!isNull(mode.length)) {
				editor.setOption('mode', mode)
			} 
        	editor.setValue(data.modelContent);
        	layer.open({
	            id: '模板内容',
	            type: 1,
	            title: '模板内容',
	            shade: 0.3,
	            area: ['90vw', '90vh'],
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
	
	//删除
	function del(data, obj){
		var msg = obj ? '确认删除模板【' + obj.data.modelName + '】吗？' : '确认删除选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '删除模板' }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "codemodel008", params:{rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	//编辑
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/codemodel/codemodeledit.html", 
			title: "编辑模板",
			pageId: "codemodeledit",
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
			url: "../../tpl/codemodel/codemodeladd.html", 
			title: "新增模板",
			pageId: "codemodeladd",
			callBack: function(refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });
    
    function loadTable(){
    	table.reloadData("messageTable", {where:{groupId:groupId}});
    }
    
    function refreshTable(){
    	table.reloadData("messageTable", {page: {curr: 1}, where:{groupId:groupId}});
    }
    
    exports('codemodellist', {});
});
