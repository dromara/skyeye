
var rowId = "";

layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'laydate', 'fsCommon', 'fsTree'], function (exports) {
    winui.renderColor();
    var $ = layui.$,
        form = layui.form,
        laydate = layui.laydate,
        fsTree = layui.fsTree,
        fsCommon = layui.fsCommon,
        table = layui.table;
    authBtn('1571638020191');//新增
    authBtn('1572313776196');//导出

    laydate.render({
        elem: '#billTime',
        range: '~'
    });

    function initLoadTable(){
        table.render({
            id: 'messageTable',
            elem: '#messageTable',
            method: 'post',
            url: reqBasePath + 'income001',
            where: getTableParams(),
            even: true,
            page: true,
            limits: getLimits(),
            limit: getLimit(),
            cols: [[
                { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
                { field: 'billNo', title: '单据编号', align: 'left', width: 200, templet: function(d){
                        return '<a lay-event="details" class="notice-title-click">' + d.billNo + '</a>';
                    }},
                { field: 'customerName', title: '往来单位', align: 'left', width: 150},
                { field: 'totalPrice', title: '合计金额', align: 'left', width: 120},
                { field: 'hansPersonName', title: '经手人', align: 'left', width: 100},
                { field: 'billTime', title: '单据日期', align: 'center', width: 140 },
                { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 200, toolbar: '#tableBar'}
            ]],
            done: function(){
                matchingLanguage();
            }
        });
    }

    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'delete') { //删除
            deleteIncome(data);
        }else if (layEvent === 'details') { //详情
            details(data);
        }else if (layEvent === 'edit') { //编辑
            edit(data);
        }
    });

    /********* tree 处理   start *************/
    var orderType = "";
    var ztree = null;
    fsTree.render({
        id: "treeDemo",
        url: "dsFormObjectRelation007?userToken=" + getCookie('userToken') + "&loginPCIp=" + returnCitySN["cip"] + "&firstTypeCode=IFS" + "&language=" + languageType,
        clickCallback: onClickTree,
        onDblClick: onClickTree
    }, function(id){
        ztree = $.fn.zTree.getZTreeObj(id);
        initLoadTable();
    });

    // 异步加载的方法
    function onClickTree(event, treeId, treeNode) {
        if(treeNode == undefined) {
            orderType = "";
        } else {
            if(treeNode.isParent != 0){
                return;
            }
            // 订单类型
            orderType = treeNode.id;
        }
        loadTable();
    }
    /********* tree 处理   end *************/

    // 编辑
    function edit(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/income/incomeEdit.html",
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "incomeEdit",
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

    // 删除
    function deleteIncome(data){
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
            AjaxPostUtil.request({url:reqBasePath + "income005", params: {rowId: data.id}, type:'json', method: "DELETE", callback:function(json){
                if(json.returnCode == 0){
                    winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1,time: 2000});
                    loadTable();
                }else{
                    winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
                }
            }});
        });
    }

    // 详情
    function details(data){
        rowId = data.id;
        _openNewWindows({
            url: "../../tpl/income/incomeInfo.html",
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "incomeInfo",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
            }});
    }

    // 添加
    $("body").on("click", "#addBean", function(){
        _openNewWindows({
            url: "../../tpl/income/incomeAdd.html",
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "incomeAdd",
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
    
    // 导出excel
    $("body").on("click", "#downloadExcel", function () {
    	postDownLoadFile({
			url : reqBasePath + 'income007?userToken=' + getCookie('userToken') + '&loginPCIp=' + returnCitySN["cip"],
			params: getTableParams(),
			method : 'post'
		});
    });

    function getTableParams(){
        var startTime = "";
        var endTime = "";
        if(!isNull($("#billTime").val())){
            startTime = $("#billTime").val().split('~')[0].trim() + ' 00:00:00';
            endTime = $("#billTime").val().split('~')[1].trim() + ' 23:59:59';
        }
        return {
            billNo: $("#billNo").val(),
            orderType: orderType,
            startTime: startTime,
            endTime: endTime
        };
    }

    exports('incomeList', {});
});
