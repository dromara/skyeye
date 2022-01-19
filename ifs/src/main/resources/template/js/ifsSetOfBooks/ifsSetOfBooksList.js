
var rowId = "";

layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'laydate'], function (exports) {
    winui.renderColor();
    var $ = layui.$,
        form = layui.form,
        laydate = layui.laydate,
        table = layui.table;

    laydate.render({
        elem: '#startTime',
        range: '~'
    });

    laydate.render({
        elem: '#endTime',
        range: '~'
    });

    authBtn('1637982972958');

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: reqBasePath + 'ifssetofbooks001',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
            { field: 'name', title: '名称', align: 'left', width: 200, templet: function(d){
                return '<a lay-event="select" class="notice-title-click">' + d.name + '</a>';
            }},
            { field: 'startTime', title: '开始日期', align: 'center', width: 100},
            { field: 'endTime', title: '截至日期', align: 'center', width: 100},
            { field: 'state', title: '状态', align: 'center', width: 80, templet: function(d){
                if(d.state == 1){
                    return "<span class='state-up'>启用</span>";
                }else {
                    return "<span class='state-down'>停用</span>";
                }
            }},
            { field: 'remark', title: '备注', align: 'left', width: 200},
            { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
            { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
            { field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 100 },
            { field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150},
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
            delet(data);
        }else if (layEvent === 'select'){//查看详情
            selectDetails(data);
        }
    });

    // 编辑
    function edit(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/ifsSetOfBooks/ifsSetOfBooksEdit.html",
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "ifsSetOfBooksEdit",
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

    // 删除
    function delet(data){
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
            layer.close(index);
            AjaxPostUtil.request({ url: reqBasePath + "ifssetofbooks005", params: {rowId: data.id}, type: 'json', method: "DELETE", callback: function(json){
                if(json.returnCode == 0){
                    winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                    loadTable();
                }else{
                    winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                }
            }});
        });
    }

    function selectDetails(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/ifsSetOfBooks/ifsSetOfBooksDetails.html",
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "ifsSetOfBooksDetails",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
            }
        });
    }

    // 添加
    $("body").on("click", "#addBean", function(){
        _openNewWindows({
            url: "../../tpl/ifsSetOfBooks/ifsSetOfBooksAdd.html",
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "ifsSetOfBooksAdd",
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
        var startTime1 = "", startTime2 = "";
        if(!isNull($("#startTime").val())){
            startTime1 = $("#startTime").val().split('~')[0].trim();
            startTime2 = $("#startTime").val().split('~')[1].trim();
        }
        var endTime1 = "", endTime2 = "";
        if(!isNull($("#endTime").val())){
            endTime1 = $("#endTime").val().split('~')[0].trim();
            endTime2 = $("#endTime").val().split('~')[1].trim();
        }
        return {
            name: $("#name").val(),
            state: $("#state").val(),
            startTime1: startTime1,
            startTime2: startTime2,
            endTime1: endTime1,
            endTime2: endTime2
        };
    }

    exports('ifsSetOfBooksList', {});
});
