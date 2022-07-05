
// 前加签
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'soulTable', 'table', 'form'], function (exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
        form = layui.form,
        table = layui.table,
        soulTable = layui.soulTable;

    var chooseUserList = new Array();
    var taskId = GetUrlParam("taskId");

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'get',
        data: chooseUserList,
        even: true,
        page: false,
        rowDrag: {
            trigger: 'row',
            done: function(obj) {}
        },
        cols: [[
            { type: 'checkbox', align: 'center' },
            { field: 'name', title: '审批人', align: 'left', width: 180, templet: function (d) {
                return '<input type="text" id="approvalId' + d.LAY_TABLE_INDEX + '" placeholder="请选择审批人" class="layui-input" readonly="readonly" ' +
                    'value="' + (isNull(d.name) ? "" : d.name) + '" win-verify="required"/>' +
                    '<i class="fa fa-plus-circle input-icon chooseApprovalIdBtn" style="top: 8px;"></i>';
            }},
            { field: 'email', title: '邮箱', align: 'left', width: 200}
        ]],
        done: function(){
            matchingLanguage();
            soulTable.render(this);
        }
    });

    form.render();
    form.on('submit(formAddBean)', function (data) {
        if (winui.verifyForm(data.elem)) {
            if(table.cache.messageTable.length == 0){
                winui.window.msg('请最少选择一条审批节点.', {icon: 2, time: 2000});
                return false;
            }
            var params = {
                taskId: taskId,
                chooseUserMation: JSON.stringify(table.cache.messageTable)
            };
            AjaxPostUtil.request({url:flowableBasePath + "activitiTask003", params: params, type: 'json', callback: function (json) {
                parent.layer.close(index);
                parent.refreshCode = '0';
            }});
        }
        return false;
    });

    $("body").on("click", "#addRow", function() {
        addRow();
    });

    $("body").on("click", "#deleteRow", function() {
        deleteRow();
    });

    // 新增行
    var rowNum = 1;
    function addRow() {
        chooseUserList = [].concat(table.cache.messageTable);
        chooseUserList.push({id: rowNum});
        table.reload("messageTable", {data: chooseUserList});
        rowNum++;
    }

    // 删除行
    function deleteRow() {
        chooseUserList = [].concat(table.cache.messageTable);
        var check_box = table.checkStatus('messageTable').data;
        for (var i = 0;  i < check_box.length; i++){
            var list = [];
            $.each(chooseUserList, function(j, item){
                if(item.id != check_box[i].id){
                    list.push(item);
                }
            });
            chooseUserList = [].concat(list);
        }
        table.reload("messageTable", {data: chooseUserList});
    }

    // 人员选择
    $("body").on("click", ".chooseApprovalIdBtn", function() {
        var rowIndex = $(this).parent().find("input").attr("id").replace("approvalId", "");
        systemCommonUtil.userReturnList = [];
        systemCommonUtil.chooseOrNotMy = "2"; // 人员列表中是否包含自己--1.包含；其他参数不包含
        systemCommonUtil.chooseOrNotEmail = "2"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
        systemCommonUtil.checkType = "2"; // 人员选择类型，1.多选；其他。单选
        systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList){
            chooseUserList[rowIndex] = userReturnList[0];
            table.reload("messageTable", {data: chooseUserList});
        });
    });

    // 取消
    $("body").on("click", "#cancle", function() {
        parent.layer.close(index);
    });

});