
var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'treeGrid', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	
	var $ = layui.$,
		form = layui.form,
		treeGrid = layui.treeGrid;
	
	authBtn('1585824465120');
	
	
	treeGrid.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        idField: 'id',
        url: schoolBasePath + 'schoolmation001',
        cellMinWidth: 100,
        where:{schoolName: $("#schoolName").val()},
        treeId: 'id',//树形id字段名称
        treeUpId: 'pId',//树形父id字段名称
        treeShowName: 'schoolName',//以树形式显示的字段
        cols: [[
            { field: 'schoolName', width: 300, title: '学校名称'},
            { field: 'schoolDesc', width: 80, title: '学校简介', align: 'center', templet: function (d) {
	        	return '<i class="fa fa-fw fa-html5 cursor" lay-event="schoolDesc"></i>';
	        }},
            { field:'addressDetailed', width: 400, title: '学校地址'},
	        { field:'createTime', width: 150, align: 'center', title: systemLanguage["com.skyeye.entryTime"][languageType]},
	        { field:'powerName', width: 120, align: 'center', title: '数据权限'},
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 240, toolbar: '#tableBar'}
        ]],
        isPage:false,
	    done: function(){
	    	matchingLanguage();
	    }
    });
	
	treeGrid.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'del') { //删除
        	del(data, obj);
        }else if (layEvent === 'edit') { //编辑
        	edit(data);
        }else if (layEvent === 'schoolDesc') { //学校简介
        	layer.open({
	            id: '学校简介',
	            type: 1,
	            title: '学校简介',
	            shade: 0.3,
	            area: ['90vw', '90vh'],
	            content: data.schoolDesc
	        });
        }
    });
	
	form.render();
	form.on('submit(formSearch)', function (data) {
    	
        if (winui.verifyForm(data.elem)) {
        	loadTable();
        }
        return false;
	});
	
	//删除
	function del(data, obj){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url:schoolBasePath + "schoolmation003", params:{rowId: data.id}, type: 'json', callback: function (json) {
    			if (json.returnCode == 0) {
    				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
    				loadTable();
    			} else {
    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
    			}
    		}});
		});
	}
	
	//编辑
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/schoolmation/schoolmationedit.html", 
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "schoolmationedit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
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
			url: "../../tpl/schoolmation/schoolmationadd.html", 
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "schoolmationadd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });
    
    function loadTable(){
    	treeGrid.query("messageTable", {where:{schoolName: $("#schoolName").val()}});
    }
    
    exports('schoolmationlist', {});
});
