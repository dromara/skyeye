
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
	
	authBtn('1552959308337');
	// 公司列表
	treeGrid.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        idField: 'id',
        url: reqBasePath + 'companymation001',
        cellMinWidth: 100,
        where:{companyName: $("#companyName").val()},
        treeId: 'id',//树形id字段名称
        treeUpId: 'pId',//树形父id字段名称
        treeShowName: 'companyName',//以树形式显示的字段
        cols: [[
            { field: 'companyName', width: 300, title: '公司名称'},
            { field: 'companyDesc', width: 80, title: '公司简介', align: 'center', templet: function (d) {
	        	return '<i class="fa fa-fw fa-html5 cursor" lay-event="companyDesc"></i>';
	        }},
	        { field: 'departmentNum', title: '部门数', width: 100 },
	        { field: 'userNum', title: '员工数', width: 100 },
            { field:'id', width:400, title: '公司地址', templet: function (d) {
            	var str = d.provinceName + " ";
            	if(!isNull(d.cityName)){
            		str += d.cityName + " ";
            	}
            	if(!isNull(d.areaName)){
            		str += d.areaName + " ";
            	}
            	if(!isNull(d.townshipName)){
            		str += d.townshipName + " ";
            	}
            	if(!isNull(d.addressDetailed)){
            		str += d.addressDetailed;
            	}
	        	return str;
	        }},
	        { field:'createTime', width:150, align: 'center', title: systemLanguage["com.skyeye.entryTime"][languageType]},
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
        }else if (layEvent === 'companyDesc') { //公司简介
        	layer.open({
	            id: '公司简介',
	            type: 1,
	            title: '公司简介',
	            shade: 0.3,
	            area: ['90vw', '90vh'],
	            content: data.companyDesc
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
            AjaxPostUtil.request({url: reqBasePath + "companymation003", params:{rowId: data.id}, type: 'json', callback: function (json) {
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
			url: "../../tpl/companymation/companymationedit.html", 
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "companymationedit",
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
			url: "../../tpl/companymation/companymationadd.html", 
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "companymationadd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });
    
    function loadTable(){
    	treeGrid.query("messageTable", {where:{companyName: $("#companyName").val()}});
    }
    
    exports('companymationlist', {});
});
