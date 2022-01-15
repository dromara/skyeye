
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

    var selOption = getFileContent('tpl/template/select-option.tpl');
    var handsPersonList = new Array();//经手人员

    //单据时间
    laydate.render({
        elem: '#operTime',
        type: 'datetime',
        value: getFormatDate(),
        trigger: 'click'
    });

    // 初始化账户
    systemCommonUtil.getSysAccountListByType(function(json){
        // 加载账户数据
        $("#accountId").html(getDataUseHandlebars(selOption, json));
    });

    // 初始化列表项选择
    voucherUtil.init('showVoucherUtilBox');

    matchingLanguage();
    form.render();
    form.on('submit(formAddBean)', function(data) {
        if(winui.verifyForm(data.elem)) {
            var result = voucherUtil.getData();
            console.log(result);
            if(result.length < 2){
                return false;
            }
            var params = {
                organId: sysCustomerUtil.customerMation.id,
                handsPersonId: handsPersonList[0].id,
                operTime: $("#operTime").val(),
                accountId: $("#accountId").val(),
                remark: $("#remark").val(),
                changeAmount: $("#changeAmount").val(),
                initemStr: JSON.stringify(result)
            };
            AjaxPostUtil.request({url: reqBasePath + "income002", params: params, type: 'json', method: "POST", callback: function(json) {
                if(json.returnCode == 0) {
                    dsFormUtil.savePageData("dsFormShow", json.bean.id);
                    parent.layer.close(index);
                    parent.refreshCode = '0';
                } else {
                    winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                }
            }});
        }
        return false;
    });

    $('#handsPersonId').tagEditor({
        initialTags: [],
        placeholder: '请选择经手人员',
        editorTag: false,
        beforeTagDelete: function(field, editor, tags, val) {
            var inArray = -1;
            $.each(handsPersonList, function(i, item) {
                if(val === item.name) {
                    inArray = i;
                    return false;
                }
            });
            if(inArray != -1) { //如果该元素在集合中存在
                handsPersonList.splice(inArray, 1);
            }
        }
    });

    // 人员选择
    $("body").on("click", "#toHandsPersonSelPeople", function(e){
        systemCommonUtil.userReturnList = [].concat(handsPersonList);
        systemCommonUtil.chooseOrNotMy = "2"; // 人员列表中是否包含自己--1.包含；其他参数不包含
        systemCommonUtil.chooseOrNotEmail = "2"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
        systemCommonUtil.checkType = "2"; // 人员选择类型，1.多选；其他。单选
        systemCommonUtil.openSysUserStaffChoosePage(function (staffChooseList){
            // 移除所有tag
            var tags = $('#handsPersonId').tagEditor('getTags')[0].tags;
            for (i = 0; i < tags.length; i++) {
                $('#handsPersonId').tagEditor('removeTag', tags[i]);
            }
            handsPersonList = [].concat(staffChooseList);
            // 添加新的tag
            $.each(handsPersonList, function(i, item){
                $('#handsPersonId').tagEditor('addTag', item.name);
            });
        });
    });

    // 新增行
    $("body").on("click", "#addRow", function() {
        voucherUtil.addItem();
    });

    // 客户选择
    $("body").on("click", "#customMationSel", function(e){
        sysCustomerUtil.openSysCustomerChoosePage(function (customerMation){
            $("#customName").val(customerMation.customName);
        });
    });

    // 选择账套
    $("body").on("click", "#chooseSetOfBooksBtn", function(e){
        var _this = $(this);
        sysIfsUtil.openIfsSetOfBooksListChoosePage(function (ifsSetOfBooksMation){
            _this.parent().find("input").val(ifsSetOfBooksMation.name);
        });
    });

    // 选择单据类型
    $("body").on("click", "#chooseOrderTypeBtn", function(e){
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