
var rowId = "";
var payMonth = "";

layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'laydate'], function (exports) {
    winui.renderColor();
    var $ = layui.$,
        form = layui.form,
        table = layui.table,
        laydate = layui.laydate;

    laydate.render({elem: '#payMonth', type: 'month'});

    // 获取所有待发放薪资列表
    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: sysMainMation.wagesBasePath + 'wagespaymenthistory003',
        where: getTableParams(),
        even: true,
        page: true,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
            { field: 'companyName', title: '公司', align: 'left', width: 150 },
            { field: 'departmentName', title: '部门', align: 'left', width: 150 },
            { field: 'jobName', title: '职位', align: 'left', width: 120 },
            { field: 'userName', title: '员工', align: 'left', width: 120},
            { field: 'payMonth', title: '月份', align: 'center', width: 80, templet: function (d) {
                return '<a  lay-event="details" class="notice-title-click">' + d.payMonth + '</p>';
            }},
            { field: 'state', title: '状态', align: 'center', width: 80, templet: function (d) {
                    return "<span class='state-down'>待发放</span>";
            }},
            { field: 'actWages', title: '实发工资', align: 'left', width: 100 },
            { field: 'type', title: '核算方式', align: 'center', width: 80, templet: function (d) {
                if(d.type == '1'){
                    return "人工核算";
                }else if(d.type == '2'){
                    return "系统核算";
                }
            }},
            { field: 'createTime', title: '核算日期', align: 'center', width: 150 },
            { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 100, toolbar: '#tableBar'}
        ]],
        done: function(json) {
            matchingLanguage();
            initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入员工姓名、员工工号", function () {
                table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
            });
        }
    });

    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'details') { //详情
            details(data);
        } else if (layEvent === 'grant') { //发放
            grant(data);
        }
    });

    // 详情
    function details(data) {
        rowId = data.staffId;
        payMonth = data.payMonth;
        _openNewWindows({
            url: "../../tpl/wagesPaymentHistory/wagesStaffPaymentDetail.html",
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "wagesStaffPaymentDetail",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
            }
        });
    }

    // 发放
    function grant(data) {

    }

    form.render();

    // 刷新数据
    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });

    function loadTable() {
        table.reloadData("messageTable", {where: getTableParams()});
    }

    function getTableParams() {
        return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageTable"));
    }

    exports('wagesAllNotGrantPaymentHistoryList', {});
});
