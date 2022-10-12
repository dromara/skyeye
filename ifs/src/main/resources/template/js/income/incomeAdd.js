
// 兼容动态表单
var layedit, form;

layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'laydate', 'tagEditor'].concat(dsFormUtil.mastHaveImport), function(exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
        laydate = layui.laydate;
    layedit = layui.layedit,
    form = layui.form;
    var serviceClassName = sysServiceMation["incomeOrder"]["key"];

    // 获取单据提交类型
    var submitType = erpOrderUtil.getSubmitTypeByKey(serviceClassName);

    var selOption = getFileContent('tpl/template/select-option.tpl');
    var handsPersonList = new Array();//经手人员

    // 单据时间
    laydate.render({elem: '#operTime', type: 'datetime', value: getFormatDate(), trigger: 'click'});

    // 初始化账户
    systemCommonUtil.getSysAccountListByType(function (json) {
        $("#accountId").html(getDataUseHandlebars(selOption, json));
    });

    // 初始化列表项选择
    voucherUtil.init('showVoucherUtilBox');

    matchingLanguage();
    form.render();
    // 保存为草稿
    form.on('submit(formAddBean)', function(data) {
        if(winui.verifyForm(data.elem)) {
            saveData("1", "");
        }
        return false;
    });

    // 走工作流的提交审批
    form.on('submit(formSubOneBean)', function(data) {
        if(winui.verifyForm(data.elem)) {
            activitiUtil.startProcess(serviceClassName, null, function (approvalId) {
                saveData("2", approvalId);
            });
        }
        return false;
    });

    // 不走工作流的提交
    form.on('submit(formSubTwoBean)', function(data) {
        if(winui.verifyForm(data.elem)) {
            saveData("2", "");
        }
        return false;
    });

    function saveData(subType, approvalId) {
        var result = voucherUtil.getData();
        if(result.length < 2){
            return false;
        }
        var params = {
            organType: $("#correspondentUnitType").val(),
            organId: getOrganId(),
            type: dsFormUtil.dsFormObjectRelationChoose.id,
            handsPersonId: handsPersonList[0].id,
            operTime: $("#operTime").val(),
            accountId: $("#accountId").val(),
            remark: $("#remark").val(),
            setOfBooksId: sysIfsUtil.ifsSetOfBooksMation.id,
            initemStr: JSON.stringify(result),
            submitType: submitType,
            subType: subType,
            approvalId: approvalId
        };
        AjaxPostUtil.request({url: flowableBasePath + "income002", params: params, type: 'json', method: "POST", callback: function(json) {
            dsFormUtil.savePageData("dsFormShow", json.bean.id);
            parent.layer.close(index);
            parent.refreshCode = '0';
        }, async: true});
    }

    function getOrganId(){
        var correspondentUnitType = $("#correspondentUnitType").val();
        if(correspondentUnitType == 1){
            // 供应商
            return sysSupplierUtil.supplierMation.id;
        } else if (correspondentUnitType == 2){
            // 客户
            return sysCustomerUtil.customerMation.id;
        }
    }

    // 往来单位变化监听
    form.on('select(correspondentUnitType)', function(data) {
        $("#customName").val("");
    });

    $('#handsPersonId').tagEditor({
        initialTags: [],
        placeholder: '请选择经手人员',
        editorTag: false,
        beforeTagDelete: function(field, editor, tags, val) {
            handsPersonList = [].concat(arrayUtil.removeArrayPointName(handsPersonList, val));
        }
    });

    // 人员选择
    $("body").on("click", "#toHandsPersonSelPeople", function (e) {
        systemCommonUtil.userReturnList = [].concat(handsPersonList);
        systemCommonUtil.chooseOrNotMy = "2"; // 人员列表中是否包含自己--1.包含；其他参数不包含
        systemCommonUtil.chooseOrNotEmail = "2"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
        systemCommonUtil.checkType = "2"; // 人员选择类型，1.多选；其他。单选
        systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList) {
            // 重置数据
            handsPersonList = [].concat(systemCommonUtil.tagEditorResetData('handsPersonId', userReturnList));
        });
    });

    // 新增行
    $("body").on("click", "#addRow", function() {
        voucherUtil.addItem();
    });

    // 往来单位选择
    $("body").on("click", "#customMationSel", function (e) {
        var correspondentUnitType = $("#correspondentUnitType").val();
        if(correspondentUnitType == 1){
            // 供应商
            sysSupplierUtil.openSysSupplierChoosePage(function (supplierMation){
                $("#customName").val(supplierMation.supplierName);
            });
        } else if (correspondentUnitType == 2){
            // 客户
            sysCustomerUtil.openSysCustomerChoosePage(function (customerMation){
                $("#customName").val(customerMation.customName);
            });
        }

    });

    // 选择账套
    $("body").on("click", "#chooseSetOfBooksBtn", function (e) {
        var _this = $(this);
        sysIfsUtil.openIfsSetOfBooksListChoosePage(function (ifsSetOfBooksMation){
            _this.parent().find("input").val(ifsSetOfBooksMation.name);
        });
    });

    // 选择单据类型
    $("body").on("click", "#chooseOrderTypeBtn", function (e) {
        var _this = $(this);
        dsFormUtil.openDsFormObjectRelationChooseByFirstTypeCodeChoosePage("IFS", function (dsFormObjectRelationChoose){
            _this.parent().find("input").val(dsFormObjectRelationChoose.name);
            $("#dsFormShow").html("");
            // 加载动态表单
            dsFormUtil.loadPageByCode("dsFormShow", null, dsFormObjectRelationChoose.id);
        });
    });

    $("body").on("click", "#cancle", function() {
        parent.layer.close(index);
    });
});