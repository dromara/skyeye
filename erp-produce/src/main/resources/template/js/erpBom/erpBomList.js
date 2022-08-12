
var rowId = "";
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form'], function (exports) {
    winui.renderColor();
    
    var $ = layui.$,
        form = layui.form,
        table = layui.table;
    
    authBtn('1590074984041');
    
    
    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: flowableBasePath + 'erpbom001',
        where: getTableParams(),
        even: true,
        page: true,
        limits: [8, 16, 24, 32, 40, 48, 56],
        limit: 8,
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
            { field: 'title', title: 'bom方案名称', align: 'left',width: 150, templet: function (d) {
		        	return '<a lay-event="details" class="notice-title-click">' + d.title + '</a>';
		    }},
		    { field: 'materialName', title: '商品名称', align: 'left',width: 150},
            { field: 'materialModel', title: '商品型号', align: 'left',width: 150},
            { field: 'unitName', title: '计量单位', align: 'center',width: 100},
            { field: 'makeNum', title: '数量', align: 'center',width: 80},
            { field: 'consumablesPrice', title: '耗材总费用', align: 'right',width: 100},
            { field: 'procedurePrice', title: '工序总费用', align: 'right',width: 100},
            { field: 'wastagePrice', title: '耗损总费用', align: 'right',width: 100},
            { field: 'sealPrice', title: '产品销售价', align: 'right',width: 100},
            { field: 'remark', title: '备注说明', align: 'left',width: 200},
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 150, toolbar: '#tableBar'}
        ]],
	    done: function(){
	    	matchingLanguage();
	    }
    });

    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'edit') { //编辑
            edit(data);
        }else if (layEvent === 'delete') { //删除
            deleteAccount(data);
        }else if (layEvent === 'details') { //详情
        	details(data);
        }
    });

    
    form.render();
    form.on('submit(formSearch)', function (data) {
        
        if (winui.verifyForm(data.elem)) {
            refreshTable();
        }
        return false;
    });

    //编辑
    function edit(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/erpBom/erpBomEdit.html",
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "erpBomEdit",
            area: ['100vw', '100vh'],
            callBack: function(refreshCode){
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }
        });
    }

    // 删除
    function deleteAccount(data){
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
            layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "erpbom004", params: {rowId: data.id}, type: 'json', callback: function (json) {
                winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
        });
    }

    // 添加
    $("body").on("click", "#addBean", function() {
        _openNewWindows({
            url: "../../tpl/erpBom/erpBomAdd.html",
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "erpBomAdd",
            area: ['100vw', '100vh'],
            callBack: function(refreshCode){
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    });
    
    // 详情
	function details(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/erpBom/erpBomDetail.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "erpBomDetails",
			area: ['100vw', '100vh'],
			callBack: function(refreshCode){
			}});
	}

    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });

    //刷新
    function loadTable(){
        table.reloadData("messageTable", {where: getTableParams()});
    }

    //搜索
    function refreshTable(){
        table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()})
    }
    
    function getTableParams(){
    	return {
    		materialName:$("#materialName").val(), 
    		materialModel: $("#materialModel").val()
    	};
    }

    exports('erpBomList', {});
});
