var rowId = "";

layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form'], function (exports) {
    winui.renderColor();
    authBtn('1568523956068');
    var $ = layui.$,
        form = layui.form,
        table = layui.table;
    
    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: flowableBasePath + 'storehouse001',
        where: {houseName:$("#houseName").val()},
        even: true,
        page: true,
        limits: [8, 16, 24, 32, 40, 48, 56],
        limit: 8,
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
            { field: 'houseName', title: '仓库名称', align: 'left',width: 200,templet: function(d){
                return '<a lay-event="select" class="notice-title-click">' + d.houseName + '</a>';
            }},
            { field: 'address', title: '仓库地址', align: 'left',width: 300},
            { field: 'warehousing', title: '仓储费', align: 'left',width: 100},
            { field: 'truckage', title: '搬运费', align: 'left',width: 100},
            { field: 'isDefault', title: '默认仓库', align: 'center',width: 80, templet: function(d){
                if(d.isDefault == '1'){
                    return "<span class='state-up'>是</span>";
                }else if(d.isDefault == '2'){
                    return "<span class='state-down'>否</span>";
                }else{
                    return "<span class='state-error'>参数错误</span>";
                }
            }},
            { field: 'createTime', title: systemLanguage["com.skyeye.entryTime"][languageType], align: 'center', width: 140 },
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 200, toolbar: '#tableBar'}
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
            deleteHouse(data);
        }else if (layEvent === 'default') { //设置默认
            defaultHouse(data);
        }else if (layEvent === 'select') {
            selectHouse(data);
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
            url: "../../tpl/storehouse/storehouseedit.html",
            title: "编辑仓库",
            pageId: "storehouseedit",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                if (refreshCode == '0') {
                    winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                    loadTable();
                } else if (refreshCode == '-9999') {
                    winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
            }});
    }

    //删除仓库
    function deleteHouse(data){
        layer.confirm('确认删除该仓库吗？', { icon: 3, title: '删除仓库' }, function (index) {
            var params = {
                rowId: data.id
            };
            AjaxPostUtil.request({url: flowableBasePath + "storehouse004", params: params, type: 'json', callback: function(json){
                if(json.returnCode == 0){
                    winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                    loadTable();
                }else{
                    winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                }
            }});
        });
    }

    //设置是否默认
    function defaultHouse(data){
        layer.confirm('确认要设置该仓库为默认状态吗？', { icon: 3, title: '设置仓库状态' }, function (index) {
            var params = {
                rowId: data.id
            };
            AjaxPostUtil.request({url: flowableBasePath + "storehouse006", params: params, type: 'json', callback: function(json){
                if(json.returnCode == 0){
                    winui.window.msg("设置成功。", {icon: 1, time: 2000});
                    loadTable();
                }else{
                    winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                }
            }});
        });
    }
    function selectHouse(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/storehouse/storehouseinfo.html",
            title: "查看仓库详情",
            pageId: "storehouseinfo",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                if (refreshCode == '0') {
                    winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                    loadTable();
                } else if (refreshCode == '-9999') {
                    winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
            }
        });
    }
    //添加仓库
    $("body").on("click", "#addBean", function(){
        _openNewWindows({
            url: "../../tpl/storehouse/storehouseadd.html",
            title: "新增仓库",
            pageId: "storehouseadd",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                if (refreshCode == '0') {
                    winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
                    loadTable();
                } else if (refreshCode == '-9999') {
                    winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
            }});
    });

    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });

    //刷新
    function loadTable(){
        table.reload("messageTable", {where:{houseName:$("#houseName").val()}});
    }

    //搜索
    function refreshTable(){
        table.reload("messageTable", {page: {curr: 1}, where:{houseName:$("#houseName").val()}})
    }

    exports('storehouselist', {});
});
