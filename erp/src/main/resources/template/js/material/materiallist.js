
var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'eleTree'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		table = layui.table,
		eleTree = layui.eleTree;
	
	authBtn('1570697180609');
	
	table.render({
	    id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: flowableBasePath + 'material001',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
	    limit: getLimit(),
	    cols: [[
	        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
	        { field: 'name', title: '商品名称', align: 'left', width: 150, templet: function (d) {
		        	return '<a lay-event="details" class="notice-title-click">' + d.name + '</a>';
		    }},
	        { field: 'model', title: '型号', align: 'left', width: 150 },
	        { field: 'categoryName', title: '所属类型', align: 'center', width: 100 },
	        { field: 'typeName', title: '商品来源', align: 'left', width: 100 },
	        { field: 'enabled', title: '状态', align: 'center', width: 60, templet: function (d) {
	        	if(d.enabled == '0'){
	        		return "<span class='state-down'>禁用</span>";
	        	}else if(d.enabled == '1'){
	        		return "<span class='state-up'>启用</span>";
	        	}
	        }},
	        { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 260, toolbar: '#tableBar'}
	    ]],
	    done: function(){
	    	matchingLanguage();
	    	if(!loadFirstType){
	    		initFirstType();
	    	}
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'edit') { //编辑
        	edit(data);
        }else if (layEvent === 'delet') { //删除
        	delet(data);
        }else if (layEvent === 'up') { //启用
        	up(data);
        }else if (layEvent === 'down') { //禁用
        	down(data);
        }else if (layEvent === 'details') { //详情
        	details(data);
        }
    });
    
    var loadFirstType = false;
	//初始化商品类型
	function initFirstType(){
		loadFirstType = true;
		var el5;
		el5 = eleTree.render({
            elem: '.ele5',
            url: flowableBasePath + "materialcategory009",
            defaultExpandAll: true,
            expandOnClickNode: false,
            highlightCurrent: true
        });
        $(".ele5").hide();
		$("#categoryId").on("click",function (e) {
		    e.stopPropagation();
		    $(".ele5").toggle();
		});
		eleTree.on("nodeClick(data5)",function(d) {
		    $("#categoryId").val(d.data.currentData.name);
		    $("#categoryId").attr("categoryId", d.data.currentData.id);
		    $(".ele5").hide();
		}) 
		$(document).on("click",function() {
		    $(".ele5").hide();
		})
	}
	
	form.render();
	form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
        	refreshloadTable();
        }
        return false;
    });
    
    //详情
	function details(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/material/materialdetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "materialdetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode) {
			}});
	}
	
	//添加
	$("body").on("click", "#addBean", function() {
    	_openNewWindows({
			url: "../../tpl/material/materialadd.html", 
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "materialadd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
    });
	
	//删除
	function delet(data){
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "material006", params: {rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	//禁用
	function down(data){
		layer.confirm(systemLanguage["com.skyeye.disableOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.disableOperation"][languageType]}, function(index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "material004", params: {rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.disableOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	//启用
	function up(data){
		layer.confirm(systemLanguage["com.skyeye.enableOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.enableOperation"][languageType]}, function(index) {
			layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "material005", params: {rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.enableOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	//编辑
	function edit(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/material/materialedit.html", 
			title: systemLanguage["com.skyeye.editPageTitle"][languageType],
			pageId: "materialedit",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
			}
		});
	}
	
	// 刷新数据
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    function loadTable(){
    	table.reloadData("messageTable", {where: getTableParams()});
    }
    
    function refreshloadTable(){
    	table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
    }
    
    function getTableParams(){
    	return {
    		materialName: $("#materialName").val(), 
    		model: $("#model").val(), 
    		categoryId: isNull($("#categoryId").val()) ? "" : $("#categoryId").attr("categoryId"), 
    		typeNum: $("#typeNum").val(), 
    		enabled: $("#enabled").val()
    	};
    }
    
    exports('materiallist', {});
});
