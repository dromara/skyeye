// 根据那一列的值进行变化,默认根据数量
var showTdByEdit = 'operNumber';
var tableId = '';
// 已经选择的商品集合key：表格的行trId，value：商品信息
var allChooseProduct = {};

layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'tagEditor', 'laydate'], function (exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
        form = layui.form,
        tagEditor = layui.tagEditor,
        laydate = layui.laydate,
        table = layui.table;
    var selOption = getFileContent('tpl/template/select-option.tpl');
    var serviceClassName = sysServiceMation["purchaseRequest"]["key"];
    console.log(serviceClassName)
    // 表单模板信息
    let beanTemplate = $("#beanTemplate").html();
    var id = GetUrlParam("id");

    AjaxPostUtil.request({url: sysMainMation.erpBasePath + "queryPurchaseRequestTransferContract", params: {id: id}, type: 'json', method: "GET", callback: function(json) {
        if (!isNull(json.bean)) {
            let tabList = [];
            let tabIndex = 1;
            let supplierIds = [];
            $.each(json.bean, function (key, value) {
                if (!isNull(key)) {
                    supplierIds.push(key)
                }
                let tab = {
                    supplierId: key,
                    name: '合同-' + tabIndex,
                    class: tabIndex == 1 ? 'layui-this' : '',
                    contentClass: tabIndex == 1 ? 'layui-show' : '',
                    tabIndex: tabIndex,
                    bean: value
                };
                $.each(value, function(i, item) {
                    if (i == 0) {
                        tab.supplierMation = item.lastSupplierMation
                    }
                    item.normsId = {
                        "html": getDataUseHandlebars(selOption, {rows: item.materialMation.materialNorms}),
                        "value": item.normsId
                    };
                });
                tabList.push(tab);
                tabIndex++;
            })
            $("#titleUl").html(getDataUseHandlebars('{{#rows}}<li class="{{class}}">{{name}}</li>{{/rows}}', {rows: tabList}));
            $("#contentDiv").html(getDataUseHandlebars(beanTemplate, {rows: tabList}));

            if (supplierIds.length > 0){
                // 获取联系人
                var objectIds = JSON.stringify(supplierIds)
                AjaxPostUtil.request({url: sysMainMation.reqBasePath + "queryContactsListByObjectIds", params: {objectIds:objectIds}, type: 'json', method: "POST", callback: function(json) {
                    let contactsMap = json.bean;
                    initRender(tabIndex, tabList, contactsMap);
                }});
            } else {
                initRender(tabIndex, tabList, {});
            }
        }
    }});

    function initRender(tabIndex, tabList, contactsMap) {
        for (let i = 1; i < tabIndex; i++) {
            // 加载联系人
            $("#contacts" + i).html(getDataUseHandlebars(selOption,
                {rows: contactsMap[tabList[i - 1].supplierId]}));

            // 加载附件组件
            skyeyeEnclosure.init('enclosureUpload' + i);

            // 加载所属部门
            systemCommonUtil.queryDepartmentListByCurrentUserBelong(function(data) {
                $("#departmentId" + i).html(getDataUseHandlebars(selOption, data));
                form.render('select');
            });

            // 签约日期
            laydate.render({elem: '#signingTime' + i, type: 'date', value: getYMDFormatDate(), trigger: 'click'});
            // 生效日期
            laydate.render({elem: '#effectTime' + i, type: 'date',value: getYMDFormatDate(), trigger: 'click'});
            // 服务结束日期
            laydate.render({elem: '#serviceEndTime' + i, type: 'date',value: getYMDFormatDate(), trigger: 'click'});

            initTableChooseUtil.initTable({
                id: "supplierContractChildList" + i,
                cols: [
                    {id: 'materialId', title: '产品', formType: 'chooseInput', width: '150', className: 'materialId', verify: 'required'},
                    {id: 'normsId', title: '规格', formType: 'select', width: '50', className: 'normsId', verify: 'required', layFilter: 'selectUnitProperty'},
                    {id: 'operNumber', title: '数量', formType: 'input', width: '80', className: 'change-input rkNum', verify: 'required|number', value: '1'},
                    {id: 'unitPrice', title: '单价', formType: 'input', width: '80', className: 'change-input readOnly unitPrice', verify: 'required|money'},
                    {id: 'allPrice', title: '总金额', formType: 'input', width: '80', className: 'change-input readOnly allPrice', verify: 'required|money'},
                    {id: 'taxRate', title: '税率(%)', formType: 'input', width: '80', className: 'change-input readOnly taxRate', verify: 'required|double', value: '0.00'},
                    {id: 'taxMoney', title: '税额', formType: 'input', width: '80', className: 'change-input readOnly taxMoney', verify: 'required|money'},
                    {id: 'taxUnitPrice', title: '含税单价', formType: 'input', width: '80', className: 'change-input readOnly taxUnitPrice', verify: 'required|money'},
                    {id: 'taxLastMoney', title: '合计价税', formType: 'input', width: '80', className: 'change-input readOnly taxLastMoney', verify: 'required|money'},
                    {id: 'remark', title: '备注', formType: 'input', width: '100'}
                ],
                deleteRowCallback: function (trcusid) {
                    tableId = "supplierContractChildList" + i;
                    delete allChooseProduct[trcusid];
                    // 计算价格
                    calculatedTotalPrice();
                },
                addRowCallback: function (trcusid) {
                    // 设置根据某列变化的颜色
                    $("." + showTdByEdit).parent().css({'background-color': '#e6e6e6'});
                },
                form: form,
                minData: 1
            });
            initTableChooseUtil.deleteAllRow("supplierContractChildList" + i);
            $.each(tabList[i - 1].bean, function(j, item) {
                var trId = initTableChooseUtil.resetData("supplierContractChildList" + i, item);
                item.tableTrId = trId;
                // 将规格所属的商品信息加入到对象中存储
                allChooseProduct[trId] = item;
                $("#addRowsupplierContractChildList" + i).remove();
            });
            // 计算价格
            let totlePrice = calculatedTotalPrice();
            $("#materialTotalPrice" + i).html(totlePrice);

            $(".readOnly").attr("readonly", true);
            $(".materialId").parent().find("i").remove();
            $(".normsId").attr("disabled", true);

            // 商品规格加载变化事件
            mUnitChangeEvent(form, allChooseProduct, "estimatePurchasePrice");

            if (!isNull(tabList[i - 1].supplierMation)) {
                $("#supplierName" + i).val(tabList[i - 1].supplierMation.name);
                $("#supplierName" + i).attr("objectId", tabList[i - 1].supplierMation.id);
                $("#supplierName" + i).attr("objectKey", tabList[i - 1].supplierMation.serviceClassName);
                $("#supplierName" + i).parent().find("i").remove();
            }

            // 关联人员
            $('#relationUser' + i).tagEditor({
                initialTags: [],
                placeholder: '请选择关联人员',
                editorTag: false,
                beforeTagDelete: function(field, editor, tags, val) {
                    var listVal = $("#relationUser" + i).attr('list');
                    var list = isNull(listVal) ? [] : JSON.parse(listVal);
                    list = [].concat(arrayUtil.removeArrayPointName(list, val));
                    $("#relationUser" + i).attr('list', JSON.stringify(list));
                }
            });

            // 保存为草稿
            form.on('submit(formAddBean' + i + ')', function(data) {
                if (winui.verifyForm(data.elem)) {
                    saveData("1", "", $(this).attr("tabIndex"));
                }
                return false;
            });

            // 走工作流的提交审批
            form.on('submit(formSubOneBean' + i + ')', function(data) {
                if (winui.verifyForm(data.elem)) {
                    var tabIndex=$(this).attr("tabIndex")
                    activitiUtil.startProcess(serviceClassName, null, function (approvalId) {
                        console.log(approvalId)
                        saveData("2", approvalId, tabIndex);
                    });
                }
                return false;
            });

            initChooseProductBtnEnent(form, function(trId, chooseProductMation) {
                // 商品赋值
                allChooseProduct[trId] = chooseProductMation;
            });
        }
        matchingLanguage();
        form.render();
    }

    function saveData(formSubType, approvalId, i) {
        var result = initTableChooseUtil.getDataList('supplierContractChildList' + i);
        if (!result.checkResult) {
            return false;
        }
        var noError = false;
        var tableData = [];
        $.each(result.dataList, function(i, item) {
            //获取行编号
            var thisRowKey = item["trcusid"].replace("tr", "");
            if (parseInt(item.operNumber) == 0) {
                $("#operNumber" + thisRowKey).addClass("layui-form-danger");
                $("#operNumber" + thisRowKey).focus();
                winui.window.msg('数量不能为0', {icon: 2, time: 2000});
                noError = true;
                return false;
            }
            //商品对象
            var material = allChooseProduct["tr" + thisRowKey];
            item["materialId"] = material.materialId;
            tableData.push(item);
        });
        if (noError) {
            return false;
        }
        var listVal = $("#relationUser" + i).attr('list');
        var list = isNull(listVal) ? [] : JSON.parse(listVal);
        var params = {
            id: id,
            objectId: $("#supplierName" + i).attr("objectId"),
            objectKey: $("#supplierName" + i).attr("objectKey"),
            title: $("#title" + i).val(),
            price: $("#price" + i).val(),
            signingTime: $("#signingTime" + i).val(),
            effectTime: $("#effectTime" + i).val(),
            serviceEndTime: $("#serviceEndTime" + i).val(),
            contacts: $("#contacts" + i).val(),
            technicalTerms: $("#technicalTerms" + i).val(),
            departmentId: $("#departmentId" + i).val(),
            relationUserId: JSON.stringify(systemCommonUtil.tagEditorGetAllData('relationUser' + i, list)),
            supplierContractChildList: JSON.stringify(tableData),
            enclosureInfo: JSON.stringify({enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload' + i)}),
            formSubType: formSubType,
            approvalId: approvalId
        };
        AjaxPostUtil.request({url: sysMainMation.erpBasePath + "purchaseRequestToContract", params: params, type: 'json', method: "POST", callback: function(json) {
            parent.layer.close(index);
            parent.refreshCode = '0';
        }});
    }

    // 供应商选择
    $("body").on("click", ".supplierNameSel", function (e) {
        var inputId = "supplierName" + $(this).attr("tabIndex");
        var contactsId="contacts" + $(this).attr("tabIndex");
        sysSupplierUtil.openSysSupplierChoosePage(function (supplierMation) {
            $("#" + inputId).val(supplierMation.name);
            $("#" + inputId).attr("objectId", supplierMation.id);
            $("#" + inputId).attr("objectKey", supplierMation.serviceClassName);

            // 获取所选供应商的联系人
            AjaxPostUtil.request({url: sysMainMation.reqBasePath + "queryContactsListByObject", params: {objectId:supplierMation.id}, type: 'json', method: "GET", callback: function(json) {
                let contactsList = json.rows;
                $("#" + contactsId).html(getDataUseHandlebars(selOption, {rows: contactsList}));
                form.render();
            }});
        });
    });

    // 关联人员
    $("body").on("click", ".relationUserIdSel", function() {
        // 获取值 systemCommonUtil.tagEditorGetItemData('supplierName', list);
        var inputId = "relationUser" + $(this).attr("tabIndex");
        systemCommonUtil.userReturnList = [];
        systemCommonUtil.chooseOrNotMy = "1"; // 人员列表中是否包含自己--1.包含；其他参数不包含
        systemCommonUtil.chooseOrNotEmail = "2"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
        systemCommonUtil.checkType = "1"; // 人员选择类型，1.多选；其他。单选
        systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList){
            let list = [].concat(systemCommonUtil.tagEditorResetData(inputId, userReturnList));
            $("#" + inputId).attr('list', JSON.stringify(list));
        });
    });

});