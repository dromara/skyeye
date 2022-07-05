
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'textool', 'tagEditor'], function (exports) {
    winui.renderColor();
    layui.use(['form'], function (form) {
        var index = parent.layer.getFrameIndex(window.name);
        var $ = layui.$,
        	textool = layui.textool;

        // 工序操作员集合
        var procedureUser = new Array();

        var selOption = getFileContent('tpl/template/select-option.tpl');
        
        textool.init({eleId: 'content', maxlength: 200});

        // 获取当前登录用户所属企业的所有部门信息
        systemCommonUtil.queryDepartmentListByCurrentUserBelong(function(data){
            $("#departmentId").html(getDataUseHandlebars(selOption, data));
            form.render('select');
        });
        loadProcedureType();

        // 加载工序类别
        function loadProcedureType(){
            showGrid({
                id: "procedureType",
                url: flowableBasePath + "erpworkproceduretype008",
                params: {},
                pagination: false,
                template: selOption,
                ajaxSendLoadBefore: function(hdb){
                },
                ajaxSendAfter: function (json) {
                    form.render('select');
                }
            });
        }

        matchingLanguage();
        form.render();
        form.on('submit(formAddBean)', function (data) {
            if (winui.verifyForm(data.elem)) {
                var params = {
                    name: $("#name").val(),
                    number: $("#number").val(),
                    unitPrice: $("#unitPrice").val(),
                    departmentId: $("#departmentId").val(),
                    content: $("#content").val(),
                    procedureType: $("#procedureType").val(),
                    procedureUserId: systemCommonUtil.tagEditorGetAllData('procedureUserId', procedureUser), // 工序操作员
                };
                AjaxPostUtil.request({url: flowableBasePath + "erpworkprocedure002", params: params, type: 'json', callback: function(json) {
                    parent.layer.close(index);
                    parent.refreshCode = '0';
                }});
            }
            return false;
        });

        // 工序操作员
        $('#procedureUserId').tagEditor({
            initialTags: [],
            placeholder: '请选择工序操作员',
            editorTag: false,
            beforeTagDelete: function(field, editor, tags, val) {
                procedureUser = [].concat(arrayUtil.removeArrayPointName(procedureUser, val));
            }
        });
        // 工序操作员选择
        $("body").on("click", "#procedureUserIdSelPeople", function(e) {
            systemCommonUtil.userReturnList = [].concat(procedureUser);
            systemCommonUtil.chooseOrNotMy = "1"; // 人员列表中是否包含自己--1.包含；其他参数不包含
            systemCommonUtil.chooseOrNotEmail = "2"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
            systemCommonUtil.checkType = "1"; // 人员选择类型，1.多选；其他。单选
            systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList) {
                // 重置数据
                procedureUser = [].concat(systemCommonUtil.tagEditorResetData('procedureUserId', userReturnList));
            });
        });

        $("body").on("click", "#cancle", function() {
            parent.layer.close(index);
        });
    });
});