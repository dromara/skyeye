
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
                { title: systemLanguage["com.skyeye.serialNumber"][languageType], rowspan: '2', type: 'numbers'},
                { field: 'billNo', title: '单据编号', align: 'left', rowspan: '2', width: 200, templet: function(d){
                    return '<a lay-event="details" class="notice-title-click">' + d.billNo + '</a>';
                }},
                { title: '审批模式', align: 'center', colspan: '2'},
                { field: 'state', title: '状态', align: 'left', rowspan: '2', width: 80, templet: function(d){
                    return activitiUtil.showStateName2(d.state, d.submitType);
                }},
                { field: 'organTypeName', title: '往来单类型', rowspan: '2', align: 'left', width: 100},
                { field: 'supplierName', title: '往来单位', rowspan: '2', align: 'left', width: 150},
                { field: 'totalPrice', title: '合计金额', rowspan: '2', align: 'left', width: 120},
                { field: 'handsPersonName', title: '经手人', rowspan: '2', align: 'left', width: 120},
                { field: 'operTime', title: '单据日期', rowspan: '2', align: 'center', width: 140 },
                { field: 'userName', title: systemLanguage["com.skyeye.createName"][languageType], rowspan: '2', width: 110 },
                { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], rowspan: '2', align: 'center', width: 150 },
                { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', rowspan: '2', align: 'center', width: 200, toolbar: '#tableBar'}
            ],[
                { field: 'submitType', title: '提交模式', align: 'left', width: 120, templet: function(d){
                    return erpOrderUtil.getSubmitTypeName(d);
                }},
                { field: 'processInstanceId', title: '流程实例id', align: 'left', width: 120, templet: function(d){
                    return erpOrderUtil.getProcessInstanceIdBySubmitType(d);
                }}
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
        }else if (layEvent === 'submitToSave') { //提交
            subExamine(data, 'submitToSave');
        }else if (layEvent === 'subExamine') { //提交审核
            subExamine(data, 'subExamine');
        }else if (layEvent === 'activitiProcessDetails') { // 工作流流程详情查看
            activitiUtil.activitiDetails(data);
        }else if (layEvent === 'revoke') { //撤销
            revorke(data);
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
            if(treeNode.id != 0 && treeNode.isParent != 0){
                return;
            }
            // 订单类型
            orderType = treeNode.id == 0 ? "" : treeNode.id;
        }
        table.reload("messageTable", {page: {curr: 1}, where: getTableParams()})
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

    // 撤销申请
    function revorke(data){
        layer.confirm('确认撤销该申请吗？', { icon: 3, title: '撤销操作' }, function (index) {
            layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "income009", params: {processInstanceId: data.processInstanceId}, type: 'json', callback: function(json){
                if(json.returnCode == 0){
                    winui.window.msg("提交成功", {icon: 1, time: 2000});
                    loadTable();
                }else{
                    winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                }
            }});
        });
    }

    // 提交数据
    function subExamine(data, type){
        layer.confirm(systemLanguage["com.skyeye.approvalOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.approvalOperation"][languageType]}, function (index) {
            layer.close(index);
            if(type == 'submitToSave'){
                // 直接提交
                var params = {
                    rowId: data.id,
                    approvalId: ""
                };
                subToData(params);
            }else{
                // 工作流提交
                activitiUtil.startProcess(sysActivitiModel["incomeOrder"]["key"], function (approvalId) {
                    var params = {
                        rowId: data.id,
                        approvalId: approvalId
                    };
                    subToData(params);
                });
            }
        });
    }
    function subToData(params){
        AjaxPostUtil.request({url: reqBasePath + "income008", params: params, type: 'json', callback: function(json){
            if(json.returnCode == 0){
                winui.window.msg("提交成功", {icon: 1, time: 2000});
                loadTable();
            }else{
                winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
            }
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
            table.reload("messageTable", {page: {curr: 1}, where: getTableParams()})
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
            type: orderType,
            startTime: startTime,
            endTime: endTime
        };
    }

    exports('incomeList', {});
});
