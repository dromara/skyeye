layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form'], function (exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
        form = layui.form,
        table = layui.table;

    // 设置提示信息
    $("#showInfo").html("审批人员选择规则：双击指定行即可选中。");

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'post',
        url: flowableBasePath + 'activitiProcess001',
        where: getTableParams(),
        even: true,
        page: false,
        limits: getLimits(),
        limit: getLimit(),
        cols: [[
            { type: 'radio', rowspan: '2'},
            { field: 'jobNumber', title: '工号', align: 'left', rowspan: '2', width: 120},
            { field: 'userName', title: '姓名', align: 'left', rowspan: '2', width: 120},
            { title: '公司信息', align: 'center', colspan: '3'},
            { field: 'email', title: '邮箱', align: 'left', rowspan: '2', width: 200}
        ],[
            { field: 'companyName', title: '公司', align: 'left', width: 120},
            { field: 'departmentName', title: '部门', align: 'left', width: 120},
            { field: 'jobName', title: '职位', align: 'left', width: 120}
        ]],
        done: function(res, curr, count){
            matchingLanguage();
            $('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('dblclick',function(){
                var dubClick = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
                dubClick.find("input[type='radio']").prop("checked", true);
                form.render();
                var chooseIndex = JSON.stringify(dubClick.data('index'));
                var obj = res.rows[chooseIndex];
                parent.activitiUtil.chooseApprovalPersonMation = obj;

                parent.refreshCode = '0';
                parent.layer.close(index);
            });

            $('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('click',function(){
                var click = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
                click.find("input[type='radio']").prop("checked", true);
                form.render();
            })
        }
    });

    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
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

    function loadTable() {
        table.reloadData("messageTable", {where: getTableParams()});
    }

    function refreshTable(){
        table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
    }

    function getTableParams() {
        return {
            processInstanceId: parent.activitiUtil.processInstanceId,
            taskId: parent.activitiUtil.taskId,
            flag: parent.activitiUtil.flag
        };
    }

    exports('approvalPersonChoose', {});
});