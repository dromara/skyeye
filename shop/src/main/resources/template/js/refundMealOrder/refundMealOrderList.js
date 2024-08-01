var rowId = "";

layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'laydate', 'soulTable'], function (exports) {
    winui.renderColor();
    var $ = layui.$,
        form = layui.form,
        table = layui.table;
    soulTable = layui.soulTable;
    var selTemplate = getFileContent('tpl/template/select-option.tpl');

    // 加载列表数据权限
    loadAuthBtnGroup('messageTable', '1647062082493');

    // 加载当前用户所属门店
    let storeHtml = '';
    AjaxPostUtil.request({url: sysMainMation.shopBasePath + "storeStaff005", params: {}, type: 'json', method: "GET", callback: function(json) {
        storeHtml = getDataUseHandlebars(selTemplate, json);
        initTable(storeHtml);
    }, async: false});

    var storeId = "";
    form.on('select(storeId)', function(data) {
        var thisRowValue = data.value;
        storeId = isNull(thisRowValue) ? "" : thisRowValue;
        loadTable();
    });

    function initTable() {
        table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: sysMainMation.shopBasePath + 'queryRefundMealOrderList',
        where: getTableParams(),
        even: true,
        page: true,
        toolbar: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], fixed: 'left', type: 'numbers' },
            { field: 'orderNum', title: '订单号', align: 'left', width: 180, fixed: 'left', templet: function (d) {
                return '<a lay-event="select" class="notice-title-click">' + d.orderNum + '</a>';
            }},
            { field: 'contacts', title: '会员名称', width: 100 },
            { field: 'phone', title: '会员手机号', width: 100, align: "center"},
            { field: 'refundCreateName', title: '服务顾问', width: 130, align: "left"},
            { field: 'createName', title: '专属顾问', width: 130, align: "left"},
            { field: 'natureName', title: '订单性质', width: 100, align: "left"},
            { field: 'modelType', title: '车型', width: 100, align: "left"},
            { field: 'plate', title: '车牌', width: 100, align: "left"},
            { field: 'vinCode', title: 'VIN码', width: 150, align: "left"},
            { field: 'state', title: '审核状态', width: 80, align: "center", templet: function (d) {
                if(d.state == 1){
                    return "待审核";
                } else if (d.state == 2){
                    return "退款驳回";
                } else if (d.state == 3){
                    return "已退款";
                }else {
                    return "取消退款";
                }
            }},
            { field: 'storeName', title: '缴费门店', width: 150, align: "left"},
            { field: 'mealTitle', title: '套餐名称', width: 150, align: "left"},
            { field: 'mealPrice', title: '应缴金额', width: 120, align: "left"},
            { field: 'payPrice', title: '实缴金额', width: 120, align: "left"},
            { field: 'mealNum', title: '总保养次数', width: 120, align: "left"},
            { field: 'remainMealNum', title: '剩余保养次数', width: 120, align: "left"},
            { field: 'mealSinglePrice', title: '单次保养金额', width: 120, align: "left"},
            { field: 'refundPrice', title: '退款金额', width: 120, align: "left"},
            { field: 'type', title: '订单来源', width: 80, align: "center", templet: function (d) {
                if(d.type == 1){
                    return "线上下单";
                } else {
                    return "线下下单";
                }
            }},
            { field: 'whetherGive', title: '是否赠送', width: 100, align: "center", templet: function (d) {
                return shopUtil.getMealOrderWhetherGiveName(d);
            }},
            { field: 'refundTime', title: '申请退款时间', align: 'center', width: 150 },
            { field: 'refundReasonName', title: '退款原因', width: 120, align: "left"},
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 200, toolbar: '#tableBar'}
        ]],
        done: function(json) {
            matchingLanguage();
            soulTable.render(this);
            initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入订单编号", function () {
                table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
            }, `<label class="layui-form-label">门店</label><div class="layui-input-inline">
						<select id="storeId" name="storeId" lay-filter="storeId" lay-search="">
						${storeHtml}
					</select></div>`);
        }
        });
    }


    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'delete') { //删除
            delet(data);
        } else if (layEvent === 'details') { //详情
            details(data);
        } else if (layEvent === 'edit') { //编辑
            edit(data);
        } else if (layEvent === 'subApproval') { //提交审核
            subApproval(data);
        }  else if (layEvent === 'processDetails') { // 工作流流程详情查看
            activitiUtil.activitiDetails(data);
        } else if (layEvent === 'revoke') { //撤销
            revoke(data);
        }
    });

    // 删除
    function delet(data) {
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
            layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.shopBasePath + "deleteMealRefundOrderById", params: {id: data.id}, type: 'json', method: "DELETE", callback: function (json) {
                    winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                    loadTable();
                }});
        });
    }
    // 详情
    function details(data) {
        _openNewWindows({
            url: systemCommonUtil.getUrl('FP2024052400003&id=' + data.id, null),
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "refundMealOrderDetails",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
            }});
    }

    // 撤销
    function revoke(data) {
        layer.confirm('确认撤销该申请吗？', { icon: 3, title: '撤销操作' }, function (index) {
            layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.shopBasePath + "revokeMealRefundOrder", params: {processInstanceId: data.processInstanceId}, type: 'json', method: "PUT", callback: function (json) {
                    winui.window.msg("提交成功", {icon: 1, time: 2000});
                    loadTable();
                }});
        });
    }

    // 提交审批
    function subApproval(data) {
        layer.confirm(systemLanguage["com.skyeye.approvalOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.approvalOperation"][languageType]}, function (index) {
            layer.close(index);
            activitiUtil.startProcess(serviceClassName, null, function (approvalId) {
                var params = {
                    id: data.id,
                    approvalId: approvalId
                };
                AjaxPostUtil.request({url: sysMainMation.shopBasePath + "submitMealRefundOrderToApproval", params: params, type: 'json', method: "POST", callback: function (json) {
                        winui.window.msg("提交成功", {icon: 1, time: 2000});
                        loadTable();
                    }});
            });
        });
    }

    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });

    // 刷新
    function loadTable() {
        table.reloadData("messageTable", {where: getTableParams()});
    }

    function getTableParams() {
        let params = {
            holderId: storeId,
        };
        return $.extend(true, params, initTableSearchUtil.getSearchValue("messageTable"));
    }

    exports('refundMealOrderList', {});
});
