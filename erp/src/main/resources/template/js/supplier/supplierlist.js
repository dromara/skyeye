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

    authBtn('1569132999722');

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: flowableBasePath + 'supplier001',
        where: {supplierName:$("#supplierName").val(),
            telephone: $("#telephone").val(),
            email: $("#email").val(),
            fax: $("#fax").val(),
            enabled: $("#enabled").val()},
        even: true,
        page: true,
        limits: [8, 16, 24, 32, 40, 48, 56],
        limit: 8,
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
            { field: 'supplierName', title: '供应商名称', align: 'left', width: 140,templet: function (d) {
                return '<a lay-event="select" class="notice-title-click">' + d.supplierName + '</a>';
            }},
            { field: 'contacts', title: '联系人', align: 'left', width: 100},
            { field: 'phonenum', title: '联系电话', align: 'center', width: 100},
            { field: 'email', title: '电子邮箱', align: 'left', width: 120},
            { field: 'telephone', title: '手机号码', align: 'center', width: 100},
            { field: 'fax', title: '传真', align: 'left', width: 100},
            { field: 'advanceIn', title: '预收款', align: 'left',width: 100},
            { field: 'beginNeedGet', title: '期初应收', align: 'left', width: 100},
            { field: 'beginNeedPay', title: '期初应付', align: 'left', width: 100},
            { field: 'allNeedGet', title: '累计应收', align: 'left', width: 100},
            { field: 'allNeedPay', title: '累计应付', align: 'left', width: 100},
            { field: 'taxRate', title: '税率(%)',  align: 'left',width: 100},
            { field: 'enabled', title: '状态', align: 'center',width: 80, templet: function (d) {
                    if(d.enabled == '1'){
                        return "<span class='state-up'>启用</span>";
                    }else if(d.enabled == '2'){
                        return "<span class='state-down'>禁用</span>";
                    } else {
                        return "<span class='state-error'>参数错误</span>";
                    }
                }},
            { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 140 },
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
            editSupplier(data);
        }else if (layEvent === 'delete') { //删除
            deleteSupplier(data);
        }else if (layEvent === 'enabled') { //启用
            editEnabled(data);
        }else if(layEvent == 'unenabled'){ //禁用
            editNotEnabled(data)
        }else if(layEvent == 'select'){ //详情
            selectSupplier(data)
        }
    });

    form.render();
    form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
            loadTable();
        }
        return false;
    });

    //编辑
    function editSupplier(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/supplier/supplieredit.html",
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "supplieredit",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    }

    //删除供应商
    function deleteSupplier(data){
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
            layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "supplier004", params: {rowId: data.id}, type: 'json', callback: function (json) {
                winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
        });
    }

    //设置启用状态
    function editEnabled(data){
        layer.confirm('确认要更改该供应商为启用状态吗？', { icon: 3, title: '状态变更' }, function (index) {
            AjaxPostUtil.request({url: flowableBasePath + "supplier006", params: {rowId: data.id}, type: 'json', callback: function (json) {
                winui.window.msg("设置成功。", {icon: 1, time: 2000});
                loadTable();
            }});
        });
    }
    
    //设置禁用状态
    function editNotEnabled(data){
        layer.confirm('确认要更改该供应商为禁用状态吗？', { icon: 3, title: '状态变更' }, function (index) {
            AjaxPostUtil.request({url: flowableBasePath + "supplier007", params: {rowId: data.id}, type: 'json', callback: function (json) {
                winui.window.msg("设置成功。", {icon: 1, time: 2000});
                loadTable();
            }});
        });
    }

    //详情
    function selectSupplier(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/supplier/supplierinfo.html",
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "supplierinfo",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
            }
        });
    }

    //添加供应商
    $("body").on("click", "#addBean", function() {
        _openNewWindows({
            url: "../../tpl/supplier/supplieradd.html",
            title: "新增供应商",
            pageId: "supplieradd",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    });

    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });

    $("body").on("click", "#formSearch", function () {
        refreshTable();
    })
    //刷新
    function loadTable(){
        table.reload("messageTable", {where:{supplierName:$("#supplierName").val(),
                telephone: $("#telephone").val(),
                email: $("#email").val(),
                fax: $("#fax").val(),
                enabled: $("#enabled").val()}});
    }

    //搜索
    function refreshTable(){
        table.reload("messageTable", {page: {curr: 1}, where:{supplierName:$("#supplierName").val(),
                telephone: $("#telephone").val(),
                email: $("#email").val(),
                fax: $("#fax").val(),
                enabled: $("#enabled").val()}})
    }

    exports('supplierlist', {});
});
