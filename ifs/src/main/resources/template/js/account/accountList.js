
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

    authBtn('1570756544366');

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: flowableBasePath + 'account001',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
            { field: 'accountName', title: '名称', align: 'left', width: 200, templet: function (d) {
                return '<a lay-event="select" class="notice-title-click">' + d.accountName + '</a>';
            }},
            { field: 'serialNo', title: '编号', align: 'left', width: 120},
            { field: 'initialAmount', title: '期初金额', align: 'left', width: 100},
            { field: 'currentAmount', title: '当前余额', align: 'left', width: 100},
            { field: 'remark', title: '备注', align: 'left', width: 200},
            { field: 'isDefault', title: '默认', align: 'center', width: 60, templet: function (d) {
                    if(d.isDefault == '1'){
                        return "<span class='state-up'>是</span>";
                    }else if(d.isDefault == '0'){
                        return "<span class='state-down'>否</span>";
                    } else {
                        return "<span class='state-error'>参数错误</span>";
                    }
                }},
            { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 300, toolbar: '#tableBar'}
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
        }else if (layEvent === 'default') { //设置默认
            defaultAccount(data);
        }else if (layEvent === 'select'){//查看详情
            selectAccount(data);
        }else if (layEvent === 'item'){
            selectItem(data);
        }
    });

    // 编辑
    function edit(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/account/accountEdit.html",
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "accountEdit",
            area: ['90vw', '90vh'],
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
            AjaxPostUtil.request({ url: flowableBasePath + "account004", params: {rowId: data.id}, type: 'json', method: "DELETE", callback: function (json) {
                winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
        });
    }

    // 设置是否默认
    function defaultAccount(data){
        layer.confirm('确认要设置该结算账户为默认状态吗？', { icon: 3, title: '设置结算账户状态' }, function (index) {
            AjaxPostUtil.request({url: flowableBasePath + "account006", params: {rowId: data.id}, type: 'json', method: "PUT", callback: function (json) {
                winui.window.msg("设置成功。", {icon: 1, time: 2000});
                loadTable();
            }});
        });
    }

    function selectAccount(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/account/accountInfo.html",
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "accountInfo",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
            }
        });
    }

    // 添加结算账户
    $("body").on("click", "#addBean", function() {
        _openNewWindows({
            url: "../../tpl/account/accountAdd.html",
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "accountAdd",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    });

    // 查看流水
    function selectItem(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/account/accountItem.html",
            title: "流水详情",
            pageId: "accountitem",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
            }
        });
    }

    form.render();
    form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
            refreshTable();
        }
        return false;
    });

    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });

    // 刷新
    function loadTable(){
        table.reload("messageTable", {where: getTableParams()});
    }

    // 搜索
    function refreshTable(){
        table.reload("messageTable", {page: {curr: 1}, where: getTableParams()})
    }

    function getTableParams(){
        return {
            accountName:$("#accountName").val(),
            serialNo: $("#serialNo").val(),
            remark: $("#remark").val()
        };
    }

    exports('accountList', {});
});
