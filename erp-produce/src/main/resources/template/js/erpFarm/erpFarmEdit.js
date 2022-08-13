
// 工序选择必备参数
var procedureCheckType = 2;//工序选择类型：1.单选procedureMation；2.多选procedureMationList
var procedureMationList = new Array();

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'textool'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
		    form = layui.form,
		    textool = layui.textool;

        // 车间负责人信息
        var chargeUser = [];
	    
		var selOption = getFileContent('tpl/template/select-option.tpl');

        showGrid({
            id: "showForm",
            url: flowableBasePath + "erpfarm003",
            params: {rowId: parent.rowId},
            pagination: false,
            template: $("#beanTemplate").html(),
            ajaxSendLoadBefore: function(hdb){
            },
            ajaxSendAfter: function (json) {
                procedureMationList = [].concat(json.bean.procedureList);

                // 获取当前登录用户所属企业的所有部门信息
                systemCommonUtil.queryDepartmentListByCurrentUserBelong(function(data){
                    $("#departmentId").html(getDataUseHandlebars(selOption, data));
                    $("#departmentId").val(json.bean.departmentId);
                    form.render('select');
                });

                chargeUser.push({
                    id: json.bean.chargePerson,
                    name: json.bean.chargeName
                });
                
                textool.init({eleId: 'remark', maxlength: 200});

			    matchingLanguage();
                form.render();
                form.on('submit(formEditBean)', function (data) {
                    if (winui.verifyForm(data.elem)) {
                        if(procedureMationList.length == 0){
                            winui.window.msg('请选择工序', {icon: 2, time: 2000});
                            return false;
                        }
                        if(chargeUser.length == 0){
                            winui.window.msg('请选择车间负责人', {icon: 2, time: 2000});
                            return false;
                        }
                        var params = {
                            farmNumber: $("#farmNumber").val(),
                            farmName: $("#farmName").val(),
                            departmentId: $("#departmentId").val(),
                            desc: $("#remark").val(),
                            chargePerson: chargeUser[0].id,
                            farmProcedure: JSON.stringify(procedureMationList),
                            rowId: parent.rowId
                        };
                        AjaxPostUtil.request({url:flowableBasePath + "erpfarm004", params: params, type: 'json', callback: function (json) {
                            parent.layer.close(index);
                            parent.refreshCode = '0';
                        }});
                    }
                    return false;
                });
            }
        });
		
	    // 工序选择
	    $("body").on("click", "#procedureChoose", function() {
	    	_openNewWindows({
				url: "../../tpl/erpWorkProcedure/erpWorkProcedureChoose.html", 
				title: "工序选择",
				pageId: "erpWorkProcedureChoose",
				area: ['90vw', '90vh'],
				callBack: function(refreshCode) {
                    var str = "";
                    $.each(procedureMationList, function(i, item){
                        str += '<tr><td>' + item.number + '</td><td>' + item.procedureName + '</td><td>' + item.unitPrice + '</td><td>' + item.departmentName + '</td></tr>';
                    });
                    $("#procedureBody").html(str);
				}});
	    });

	    // 车间负责人选择
        $("body").on("click", "#chargeUserIdSelPeople", function(e) {
            systemCommonUtil.userReturnList = [].concat(chargeUser);
            systemCommonUtil.chooseOrNotMy = "1"; // 人员列表中是否包含自己--1.包含；其他参数不包含
            systemCommonUtil.chooseOrNotEmail = "2"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
            systemCommonUtil.checkType = "2"; // 人员选择类型，1.多选；其他。单选
            systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList) {
                chargeUser = [].concat(userReturnList);
                if(chargeUser.length > 0){
                    $("#chargeUserId").val(chargeUser[0].name);
                }
            });
        });
	    
	    // 取消
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
	    
});