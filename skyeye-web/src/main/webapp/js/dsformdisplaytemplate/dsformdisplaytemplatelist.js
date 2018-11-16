
var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['table', 'jquery', 'winui', 'form', 'codemirror', 'xml', 'clike', 'css', 'htmlmixed', 'javascript', 'nginx',
           'solr', 'sql', 'vue'], function (exports) {
	
	winui.renderColor();
	
	var $ = layui.$,
	form = layui.form,
	table = layui.table;
	
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
        matchBrackets: true,
    });
	
	//表格渲染
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'dsformdisplaytemplate001',
	    where:{templateName: $("#templateName").val()},
	    even:true,  //隔行变色
	    page: true,
	    limits: [8, 16, 24, 32, 40, 48, 56],
	    limit: 8,
	    cols: [[
	        { title: '序号', type: 'numbers'},
	        { field: 'templateName', title: '模板标题', width: 180 },
	        { field: 'id', title: '模板内容', width: 120, templet: function(d){
	        	if(!isNull(d.templateContent)){
	        		return '<i class="fa fa-fw fa-html5 cursor" lay-event="templateContent"></i>';
	        	}else{
	        		return '无';
	        	}
	        }},
	        { field: 'createTime', title: '创建时间', width: 180 },
	        { title: '操作', fixed: 'right', align: 'center', width: 240, toolbar: '#tableBar'}
	    ]]
	});
	
	table.on('tool(messageTable)', function (obj) { //注：tool是工具条事件名，test是table原始容器的属性 lay-filter="对应的值"
        var data = obj.data; //获得当前行数据
        var layEvent = obj.event; //获得 lay-event 对应的值
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
	            area: ['1200px', '600px'],
	            content: $("#modelContentDiv").html(),
	        });
        }
    });
	
	//搜索表单
	form.render();
	form.on('submit(formSearch)', function (data) {
    	//表单验证
        if (winui.verifyForm(data.elem)) {
        	loadTable();
        }
        return false;
	});
	
	//删除
	function del(data, obj){
		var msg = obj ? '确认删除模板【' + obj.data.templateName + '】吗？' : '确认删除选中数据吗？';
		layer.confirm(msg, { icon: 3, title: '删除动态表单数据展示模板' }, function (index) {
			layer.close(index);
            //向服务端发送删除指令
            AjaxPostUtil.request({url:reqBasePath + "dsformdisplaytemplate003", params:{rowId: data.id}, type:'json', callback:function(json){
    			if(json.returnCode == 0){
    				top.winui.window.msg("删除成功", {icon: 1,time: 2000});
    				loadTable();
    			}else{
    				top.winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
    			}
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
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	top.winui.window.msg("操作成功", {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	top.winui.window.msg("操作失败", {icon: 2,time: 2000});
                }
			}});
	}
	
	//刷新数据
    $("body").on("click", "#reloadTable", function(){
    	loadTable();
    });
    
    //新增
    $("body").on("click", "#addBean", function(){
    	_openNewWindows({
			url: "../../tpl/dsformdisplaytemplate/dsformdisplaytemplateadd.html", 
			title: "新增动态表单数据展示模板",
			pageId: "dsformdisplaytemplateadd",
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	top.winui.window.msg("操作成功", {icon: 1,time: 2000});
                	loadTable();
                } else if (refreshCode == '-9999') {
                	top.winui.window.msg("操作失败", {icon: 2,time: 2000});
                }
			}});
    });
    
    function loadTable(){
    	table.reload("messageTable", {where:{templateName: $("#templateName").val()}});
    }
    
    exports('dsformdisplaytemplatelist', {});
});
