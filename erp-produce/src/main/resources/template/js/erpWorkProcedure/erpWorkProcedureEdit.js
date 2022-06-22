
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
        	
        showGrid({
            id: "showForm",
            url: flowableBasePath + "erpworkprocedure003",
            params: {rowId: parent.rowId},
            pagination: false,
            template: getFileContent('tpl/erpWorkProcedure/erpWorkProcedureEditTemplate.tpl'),
            ajaxSendLoadBefore: function(hdb){
            },
            ajaxSendAfter:function(json){
            	
            	textool.init({
			    	eleId: 'content',
			    	maxlength: 200,
			    	tools: ['count', 'copy', 'reset']
			    });

                // 获取当前登录用户所属企业的所有部门信息
                systemCommonUtil.queryDepartmentListByCurrentUserBelong(function(data){
                    $("#departmentId").html(getDataUseHandlebars(selOption, data));
                    $("#departmentId").val(json.bean.departmentId);
                    form.render('select');
                });

                // 加载工序类别
                showGrid({
                    id: "procedureType",
                    url: flowableBasePath + "erpworkproceduretype008",
                    params: {},
                    pagination: false,
                    template: selOption,
                    ajaxSendLoadBefore: function(hdb){
                    },
                    ajaxSendAfter: function(data){
                        $("#procedureType").val(json.bean.procedureType);
                        form.render('select');
                    }
                });

                var userNames = [];
                procedureUser = [].concat(json.bean.operators);
                $.each(procedureUser, function(i, item){
                    userNames.push(item.name);
                });
                // 工序操作员
                $('#procedureUserId').tagEditor({
                    initialTags: userNames,
                    placeholder: '请选择工序操作员',
                    editorTag: false,
                    beforeTagDelete: function(field, editor, tags, val) {
                        var inArray = -1;
                        $.each(procedureUser, function(i, item) {
                            if(val === item.name) {
                                inArray = i;
                                return false;
                            }
                        });
                        if(inArray != -1) { //如果该元素在集合中存在
                            procedureUser.splice(inArray, 1);
                        }
                    }
                });
				
                matchingLanguage();
                form.render();
                form.on('submit(formEditBean)', function (data) {
                    if (winui.verifyForm(data.elem)) {
                        var params = {
                            rowId: parent.rowId,
                            name: $("#name").val(),
		                    number: $("#number").val(),
		                    unitPrice: $("#unitPrice").val(),
		                    departmentId: $("#departmentId").val(),
		                    content: $("#content").val(),
                            procedureType: $("#procedureType").val(),
                            procedureUserId: ""
                        };
                        // 工序操作员
                        if(procedureUser.length > 0 && !isNull($('#procedureUserId').tagEditor('getTags')[0].tags)){
                            var procedureUserId = "";
                            $.each(procedureUser, function (i, item) {
                                procedureUserId += item.id + ',';
                            });
                            params.procedureUserId = procedureUserId;
                        }
                        AjaxPostUtil.request({url: flowableBasePath + "erpworkprocedure005", params:params, type: 'json', callback: function(json){
                            if(json.returnCode == 0){
                                parent.layer.close(index);
                                parent.refreshCode = '0';
                            }else{
                                winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
                            }
                        }});
                    }
                    return false;
                });
            }
        });

        // 工序操作员选择
        $("body").on("click", "#procedureUserIdSelPeople", function(e){
            systemCommonUtil.userReturnList = [].concat(procedureUser);
            systemCommonUtil.chooseOrNotMy = "1"; // 人员列表中是否包含自己--1.包含；其他参数不包含
            systemCommonUtil.chooseOrNotEmail = "2"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
            systemCommonUtil.checkType = "1"; // 人员选择类型，1.多选；其他。单选
            systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList) {
                // 重置数据
                procedureUser = [].concat(systemCommonUtil.tagEditorResetData('procedureUserId', userReturnList));
            });
        });

        $("body").on("click", "#cancle", function(){
            parent.layer.close(index);
        });
    });
});