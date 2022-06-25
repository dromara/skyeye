
// 会签
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'table', 'form'], function (exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
        form = layui.form,
        table = layui.table;

    var chooseUserList = new Array();
    var taskId = GetUrlParam("taskId");

    var taskBean;

    // 获取会签节点信息
    AjaxPostUtil.request({url: flowableBasePath + "activitiTask006", params: {taskId: taskId}, method: "GET", type: 'json', callback: function(json) {
        if(json.returnCode == 0) {
            taskBean = json.bean;
            if(json.bean.isSequential){
                $("#jointlySignType").html("串行多实例会签");
            }else{
                $("#jointlySignType").html("并行多实例会签");
            }
            chooseUserList = [].concat(json.bean.assigneeList);
        } else {
            winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
        }
    }, async: false});

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'get',
        data: chooseUserList,
        even: true,
        page: false,
        cols: [[
            { type: 'checkbox', align: 'center' },
            { field: 'name', title: '会签人', align: 'left', width: 180, templet: function(d){
                if(d.type == 1 || d.noDelete){
                    return '<input type="text" id="approvalId' + d.LAY_TABLE_INDEX + '" placeholder="请选择审批人" class="layui-input" readonly="readonly" ' +
                        'value="' + (isNull(d.name) ? "" : d.name) + '" win-verify="required"/>';
                }else{
                    return '<input type="text" id="approvalId' + d.LAY_TABLE_INDEX + '" placeholder="请选择审批人" class="layui-input" readonly="readonly" ' +
                        'value="' + (isNull(d.name) ? "" : d.name) + '" win-verify="required"/>' +
                        '<i class="fa fa-plus-circle input-icon chooseApprovalIdBtn" style="top: 8px;"></i>';
                }
            }},
            { field: 'type', title: '角色', align: 'center', width: 100, templet: function(d){
                if(d.type == 1){
                    return "主持人";
                }else{
                    return "参与人";
                }
            }},
            { field: 'isActive', title: '状态', align: 'center', width: 100, templet: function(d){
                if(d.type != 1) {
                    if(!isNull(d.isActive + "")){
                        if (!d.isActive) {
                            return "<span class='state-up'>已评审</span>";
                        } else {
                            return "<span class='state-down'>未评审</span>";
                        }
                    }
                    return "<span class='state-down'>未评审</span>";
                }
                return "-";
            }},
            { field: 'email', title: '邮箱', align: 'left', width: 200},
            { field: 'test', title: '必选评审人', align: 'center', width: 140, templet: '#checkboxTpl', unresize: true }
        ]],
        done: function(res){
            for (var i = 0; i < res.rows.length; i++) {
                // 不允许删除的设置为不可选中
                if(res.rows[i].noDelete){
                    systemCommonUtil.disabledRow(res.rows[i].LAY_TABLE_INDEX, 'checkbox');
                }
            }
            matchingLanguage();
        }
    });

    form.on('checkbox(lockDemo)', function(obj) {
        var rowIndex = $(this).val();
        var tem = chooseUserList[rowIndex];
        delete tem["echo"];
        if(obj.elem.checked){
            tem["isMandatory"] = 1;
        }else{
            delete tem["isMandatory"];
        }
        chooseUserList[rowIndex] = tem;
        table.reload("messageTable", {data: chooseUserList});
    });

    form.render();
    form.on('submit(formAddBean)', function (data) {
        if (winui.verifyForm(data.elem)) {
            if(table.cache.messageTable.length == 1){
                winui.window.msg('请最少选择一位参与人.', {icon: 2, time: 2000});
                return false;
            }
            if(!judgeSimple()){
                return false;
            }
            var params = {
                taskId: taskId,
                chooseUserMation: JSON.stringify(table.cache.messageTable)
            };
            AjaxPostUtil.request({url: flowableBasePath + "activitiTask005", params: params, type: 'json', method: "POST", callback: function(json){
                if (json.returnCode == 0) {
                    parent.layer.close(index);
                    parent.refreshCode = '0';
                }else{
                    winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                }
            }});
        }
        return false;
    });

    /**
     * 判断是否存在相同的会签人
     *
     * @returns {boolean} true：没有重复元素，false：存在重复元素
     */
    function judgeSimple(){
        var json = table.cache.messageTable;
        var temList = [];
        for(var i = 0; i < json.length; i++){
            var item = json[i];
            if(item.type != 1){
                // 不是主持人，则需要做重复校验
                var tem = getInPoingArr(temList, "id", item.id, null);
                if(tem == null){
                    temList.push(item);
                }else{
                    winui.window.msg("存在相同的会签人，请确认", {icon: 2, time: 2000});
                    return false;
                }
            }
        }
        return true;
    }

    $("body").on("click", "#addRow", function(){
        addRow();
    });

    $("body").on("click", "#deleteRow", function(){
        deleteRow();
    });

    // 新增行
    var rowNum = 1;
    function addRow() {
        // 串行
        chooseUserList = [].concat(table.cache.messageTable);
        if(taskBean.isSequential){
            var length = chooseUserList.length;
            var tmp = chooseUserList[length - 1];
            chooseUserList[length - 1] = {id: rowNum};
            chooseUserList.push(tmp);
        }else{
            // 并行
            chooseUserList.push({id: rowNum});
        }
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
    $("body").on("click", ".chooseApprovalIdBtn", function(){
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
    $("body").on("click", "#cancle", function(){
        parent.layer.close(index);
    });

});