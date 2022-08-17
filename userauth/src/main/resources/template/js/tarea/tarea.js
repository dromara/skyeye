
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'tableTreeDj', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
        tableTree = layui.tableTreeDj;
	
	form.render();
	form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
        	loadTable();
        }
        return false;
	});

    tableTree.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: reqBasePath + 'systarea001',
        where: {addressName: $("#addressName").val()},
        cols: [[
            {field:'name', width:300, title: '区域名称'},
            {field:'id', width:100, title: 'id'},
            {field:'pId', title: 'pId'},
        ]],
        isPage:false,
	    done: function(){
	    	matchingLanguage();
	    }
    }, {
        keyId: 'id',
        keyPid: 'pId',
        title: 'name',
    });
	
	// 刷新数据
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
	
	function loadTable() {
        tableTree.reload("messageTable", {where:{addressName: $("#addressName").val()}});
    }
    
    exports('tarea', {});
});
