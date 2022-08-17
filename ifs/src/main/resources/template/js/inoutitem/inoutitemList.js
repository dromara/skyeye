
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

    authBtn('1570755543012');

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: flowableBasePath + 'inoutitem001',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
	    limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
            { field: 'inoutitemName', title: '名称', align: 'left', width: 200,templet: function (d) {
                return '<a lay-event="select" class="notice-title-click">' + d.inoutitemName + '</a>';
            }},
            { field: 'inoutitemType', title: '类型', align: 'center', width: 100, templet: function (d) {
                if(d.inoutitemType == '1'){
                    return "<span class='state-up'>收入</span>";
                }else if(d.inoutitemType == '2'){
                    return "<span class='state-down'>支出</span>";
                } else {
                    return "<span class='state-error'>参数错误</span>";
                }
            }},
            { field: 'remark', title: '备注', align: 'left', width: 400},
            { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 180 },
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 140, toolbar: '#tableBar'}
        ]],
	    done: function(){
	    	matchingLanguage();
	    }
    });

    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'edit') { // 编辑
            editInoutitem(data);
        } else if (layEvent === 'delete') { // 删除
            deleteInoutitem(data);
        } else if (layEvent === 'select') {
            selectInoutitem(data);
        }
    });

    // 编辑
    function editInoutitem(data) {
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/inoutitem/inoutitemEdit.html",
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "inoutitemEdit",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    }

    // 删除收支项目
    function deleteInoutitem(data) {
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
            layer.close(index);
            AjaxPostUtil.request({url: flowableBasePath + "inoutitem004", params: {rowId: data.id}, type: 'json', method: "DELETE", callback: function (json) {
                winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
        });
    }

    function selectInoutitem(data) {
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/inoutitem/inoutitemInfo.html",
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "inoutitemInfo",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }
        });
    }

    // 添加
    $("body").on("click", "#addBean", function() {
        _openNewWindows({
            url: "../../tpl/inoutitem/inoutitemAdd.html",
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "inoutitemAdd",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    });

    form.render();
    form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
            loadTable();
        }
        return false;
    });

    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });

    // 刷新
    function loadTable() {
        table.reloadData("messageTable", {where: getTableParams()});
    }

    // 搜索
    function refreshTable(){
        table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()})
    }

    function getTableParams() {
        return {
            inoutitemName: $("#inoutitemName").val(),
            inoutitemType: $("#inoutitemType").val(),
            remark: $("#remark").val()
        };
    }

    exports('inoutitemList', {});
});
