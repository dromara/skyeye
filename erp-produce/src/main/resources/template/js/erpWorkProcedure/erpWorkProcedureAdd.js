
var userReturnList = new Array();//选择用户返回的集合或者进行回显的集合
var chooseOrNotMy = "1";//人员列表中是否包含自己--1.包含；其他参数不包含
var chooseOrNotEmail = "2";//人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
var checkType = "1";//人员选择类型，1.多选；其他。单选

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
        
        textool.init({
	    	eleId: 'content',
	    	maxlength: 200,
	    	tools: ['count', 'copy', 'reset', 'clear']
	    });

        // 加载部门
	    showGrid({
			id: "departmentId",
			url: reqBasePath + "mycrmcontract006",
			params: {},
			pagination: false,
			template: selOption,
			ajaxSendLoadBefore: function(hdb){
			},
			ajaxSendAfter: function(json){
                loadProcedureType();
			}
		});

        // 加载工序类别
        function loadProcedureType(){
            showGrid({
                id: "procedureType",
                url: reqBasePath + "erpworkproceduretype008",
                params: {},
                pagination: false,
                template: selOption,
                ajaxSendLoadBefore: function(hdb){
                },
                ajaxSendAfter: function(json){
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
                AjaxPostUtil.request({url:reqBasePath + "erpworkprocedure002", params:params, type:'json', callback:function(json){
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

        // 工序操作员
        $('#procedureUserId').tagEditor({
            initialTags: [],
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
        // 工序操作员选择
        $("body").on("click", "#procedureUserIdSelPeople", function(e){
            checkType = '1';
            userReturnList = [].concat(procedureUser);
            _openNewWindows({
                url: "../../tpl/common/sysusersel.html",
                title: "人员选择",
                pageId: "sysuserselpage",
                area: ['90vw', '90vh'],
                callBack: function(refreshCode){
                    if (refreshCode == '0') {
                        // 移除所有tag
                        var tags = $('#procedureUserId').tagEditor('getTags')[0].tags;
                        for (i = 0; i < tags.length; i++) {
                            $('#procedureUserId').tagEditor('removeTag', tags[i]);
                        }
                        procedureUser = [].concat(userReturnList);
                        // 添加新的tag
                        $.each(procedureUser, function(i, item){
                            $('#procedureUserId').tagEditor('addTag', item.name);
                        });
                    } else if (refreshCode == '-9999') {
                        winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                    }
                }});
        });

        $("body").on("click", "#cancle", function(){
            parent.layer.close(index);
        });
    });
});