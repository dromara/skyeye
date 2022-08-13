
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
	
	authBtn('1560822829559');
	
	var editor = CodeMirror.fromTextArea(document.getElementById("modelContent"), {
        mode : "xml",  // 模式
        theme : "eclipse",  // CSS样式选择
        indentUnit : 4,  // 缩进单位，默认2
        smartIndent : true,  // 是否智能缩进
        tabSize : 4,  // Tab缩进，默认4
        readOnly : true,  // 是否只读，默认false
        showCursorWhenSelecting : true,
        lineNumbers : true,  // 是否显示行号
        styleActiveLine: true, //line选择是是否加亮
        matchBrackets: true
    });
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: flowableBasePath + 'dsformdisplaytemplate001',
	    where:{templateName: $("#templateName").val()},
	    even:true,
	    page: true,
		limits: getLimits(),
		limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'templateName', title: '模板标题', width: 180 },
	        { field: 'id', title: '模板内容', align: 'center', width: 80, templet: function (d) {
	        	if(!isNull(d.templateContent)){
	        		return '<i class="fa fa-fw fa-html5 cursor" lay-event="templateContent"></i>';
	        	} else {
	        		return '无';
	        	}
	        }},
	        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 240, toolbar: '#tableBar'}
	    ]],
	    done: function(){
	    	matchingLanguage();
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'del') { //删除
        	del(data, obj);
        }else if (layEvent === 'edit') { //编辑
        	edit(data);
        }else if (layEvent === 'templateContent') { //模板内容
        	editor.setValue(data.templateContent);
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
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "dsformdisplaytemplate003", params:{rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	//编辑分类
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/dsformdisplaytemplate/dsformdisplaytemplateedit.html", 
			title: "编辑动态表单数据展示模板",
			pageId: "dsformdisplaytemplateedit",
			area: ['90vw', '90vh'],
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
			url: "../../tpl/dsformdisplaytemplate/dsformdisplaytemplateadd.html", 
			title: "新增动态表单数据展示模板",
			pageId: "dsformdisplaytemplateadd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });
    
    function loadTable(){
    	table.reloadData("messageTable", {where:{templateName: $("#templateName").val()}});
    }
    
    function refreshTable(){
    	table.reloadData("messageTable", {page: {curr: 1}, where:{templateName: $("#templateName").val()}});
    }
    
    exports('dsformdisplaytemplatelist', {});
});
