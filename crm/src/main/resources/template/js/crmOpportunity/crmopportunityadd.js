
var responsIdList = new Array();// 商机负责人返回的集合或者进行回显的集合
var partIdList = new Array();// 商机参与人返回的集合或者进行回显的集合
var followIdList = new Array();// 商机关注人返回的集合或者进行回显的集合

var userReturnList = new Array();// 选择用户返回的集合或者进行回显的集合
var chooseOrNotMy = "1";// 人员列表中是否包含自己--1.包含；其他参数不包含
var chooseOrNotEmail = "2";// 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
var checkType = "1";// 人员选择类型，1.多选；其他。单选

// 已经选择的客户信息
var customerMation = {};

// 客户商机
layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'laydate', 'tagEditor', 'textool'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
    	laydate = layui.laydate,
    	form = layui.form,
    	textool = layui.textool;
    var selOption = getFileContent('tpl/template/select-option.tpl');
    
    textool.init({
    	eleId: 'businessNeed',
    	maxlength: 1000,
    	tools: ['count', 'copy', 'reset']
    });

	// 选择入职时间
	laydate.render({
		elem: '#estimateEndTime',
		range: false
	});

    // 获取已经上线的商机来源信息
    sysCustomerUtil.queryCustomerOpportunityFromIsUpList(function(data){
        $("#fromId").html(getDataUseHandlebars(selOption, data));
        form.render('select');
    });

    // 获取当前登录用户所属企业的所有部门信息
    systemCommonUtil.queryDepartmentListByCurrentUserBelong(function(data){
        $("#subDepartments").html(getDataUseHandlebars(selOption, data));
        form.render('select');
    });

	skyeyeEnclosure.init('enclosureUpload');
	matchingLanguage();
	form.render();
    // 保存为草稿
    form.on('submit(formAddBean)', function(data) {
        if(winui.verifyForm(data.elem)) {
            saveData("1", "");
        }
        return false;
    });

    // 提交审批
    form.on('submit(formSubBean)', function(data) {
        if(winui.verifyForm(data.elem)) {
            activitiUtil.startProcess(sysActivitiModel["crmOpportUnity"]["key"], function (approvalId) {
                saveData("2", approvalId);
            });
        }
        return false;
    });

    function saveData(subType, approvalId){
        if(isNull(customerMation.id)){
            winui.window.msg('请选择客户.', {icon: 2, time: 2000});
            return false;
        }
        var params = {
            title: $("#title").val(),
            city: $("#city").val(),
            detailAddress: $("#detailAddress").val(),
            estimatePrice: $("#estimatePrice").val(),
            estimateEndTime: $("#estimateEndTime").val(),
            contacts: $("#contacts").val(),
            department: $("#department").val(),
            job: $("#job").val(),
            workPhone: $("#workPhone").val(),
            mobilePhone: $("#mobilePhone").val(),
            email: $("#email").val(),
            qq: $("#qq").val(),
            businessNeed: $("#businessNeed").val(),
            customerId: customerMation.id,
            fromId: $("#fromId").val(),
            subDepartments: $("#subDepartments").val(),
            enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload'),
            subType: subType, // 表单类型 1.保存草稿  2.提交审批
            approvalId: approvalId
        };
        // 如果商机负责人为空
        if(responsIdList.length == 0 || isNull($('#responsId').tagEditor('getTags')[0].tags)){
            winui.window.msg('请选择商机负责人', {icon: 2,time: 2000});
            return false;
        }else{
            $.each(responsIdList, function (i, item) {
                params.responsId = item.id;
            });
        }
        // 如果商机参与人为空
        if(partIdList.length == 0 || isNull($('#partId').tagEditor('getTags')[0].tags)){
            params.partId = "";
        }else{
            var partId = "";
            $.each(partIdList, function (i, item) {
                partId += item.id + ',';
            });
            params.partId = partId;
        }
        // 如果商机关注人为空
        if(followIdList.length == 0 || isNull($('#followId').tagEditor('getTags')[0].tags)){
            params.followId = "";
        }else{
            var followId = "";
            $.each(followIdList, function (i, item) {
                followId += item.id + ',';
            });
            params.followId = followId;
        }
        AjaxPostUtil.request({url: flowableBasePath + "opportunity011", params: params, type: 'json', callback: function(json){
            if (json.returnCode == 0){
                parent.layer.close(index);
                parent.refreshCode = '0';
            } else {
                winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
            }
        }});
    }
	
	// 商机负责人
    $('#responsId').tagEditor({
        initialTags: [],
        placeholder: '请选择商机负责人',
        editorTag: false,
        beforeTagDelete: function(field, editor, tags, val) {
            var inArray = -1;
            $.each(responsIdList, function(i, item) {
                if(val === item.name) {
                    inArray = i;
                    return false;
                }
            });
            if(inArray != -1) { //如果该元素在集合中存在
                responsIdList.splice(inArray, 1);
            }
        }
    });
    // 商机负责人选择
    $("body").on("click", "#responsIdSelPeople", function(e){
        userReturnList = [].concat(responsIdList);
        checkType = "2";//人员选择类型，1.多选；其他。单选
        _openNewWindows({
            url: "../../tpl/common/sysusersel.html", 
            title: "人员选择",
            pageId: "sysuserselpage",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                if (refreshCode == '0') {
                    //移除所有tag
                    var tags = $('#responsId').tagEditor('getTags')[0].tags;
                    for (i = 0; i < tags.length; i++) { 
                        $('#responsId').tagEditor('removeTag', tags[i]);
                    }
                    responsIdList = [].concat(userReturnList);
                    //添加新的tag
                    $.each(responsIdList, function(i, item){
                        $('#responsId').tagEditor('addTag', item.name);
                    });
                } else if (refreshCode == '-9999') {
                    winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
            }});
    });
    
	// 商机参与人
    $('#partId').tagEditor({
        initialTags: [],
        placeholder: '请选择商机参与人',
        editorTag: false,
        beforeTagDelete: function(field, editor, tags, val) {
            var inArray = -1;
            $.each(partIdList, function(i, item) {
                if(val === item.name) {
                    inArray = i;
                    return false;
                }
            });
            if(inArray != -1) { //如果该元素在集合中存在
                partIdList.splice(inArray, 1);
            }
        }
    });
    // 商机参与人选择
    $("body").on("click", "#partIdSelPeople", function(e){
        userReturnList = [].concat(partIdList);
        checkType = "1";//人员选择类型，1.多选；其他。单选
        _openNewWindows({
            url: "../../tpl/common/sysusersel.html", 
            title: "人员选择",
            pageId: "sysuserselpage",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                if (refreshCode == '0') {
                    //移除所有tag
                    var tags = $('#partId').tagEditor('getTags')[0].tags;
                    for (i = 0; i < tags.length; i++) { 
                        $('#partId').tagEditor('removeTag', tags[i]);
                    }
                    partIdList = [].concat(userReturnList);
                    //添加新的tag
                    $.each(partIdList, function(i, item){
                        $('#partId').tagEditor('addTag', item.name);
                    });
                } else if (refreshCode == '-9999') {
                    winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
            }});
    });
    
	// 商机关注人
    $('#followId').tagEditor({
        initialTags: [],
        placeholder: '请选择商机关注人',
        editorTag: false,
        beforeTagDelete: function(field, editor, tags, val) {
            var inArray = -1;
            $.each(followIdList, function(i, item) {
                if(val === item.name) {
                    inArray = i;
                    return false;
                }
            });
            if(inArray != -1) { //如果该元素在集合中存在
                followIdList.splice(inArray, 1);
            }
        }
    });
    // 商机关注人选择
    $("body").on("click", "#followIdSelPeople", function(e){
    	checkType = "1";//人员选择类型，1.多选；其他。单选
        userReturnList = [].concat(followIdList);
        _openNewWindows({
            url: "../../tpl/common/sysusersel.html", 
            title: "人员选择",
            pageId: "sysuserselpage",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                if (refreshCode == '0') {
                    //移除所有tag
                    var tags = $('#followId').tagEditor('getTags')[0].tags;
                    for (i = 0; i < tags.length; i++) { 
                        $('#followId').tagEditor('removeTag', tags[i]);
                    }
                    followIdList = [].concat(userReturnList);
                    //添加新的tag
                    $.each(followIdList, function(i, item){
                        $('#followId').tagEditor('addTag', item.name);
                    });
                } else if (refreshCode == '-9999') {
                    winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
            }});
    });
    
    // 客户选择
    $("body").on("click", "#customMationSel", function(e){
    	_openNewWindows({
			url: "../../tpl/customermanage/customerChoose.html", 
			title: "选择客户",
			pageId: "customerchooselist",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	$("#customName").val(customerMation.customName);
                	$("#industryName").html(customerMation.industryName);
					$("#city").val(customerMation.city);
					$("#detailAddress").val(customerMation.detailAddress);
					$("#contacts").val(customerMation.contacts);
					$("#department").val(customerMation.department);
					$("#job").val(customerMation.job);
					$("#workPhone").val(customerMation.workPhone);
					$("#mobilePhone").val(customerMation.mobilePhone);
					$("#email").val(customerMation.email);
					$("#qq").val(customerMation.qq);
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
			}});
    });
    
	$("body").on("click", "#cancle", function(){
		parent.layer.close(index);
	});
});