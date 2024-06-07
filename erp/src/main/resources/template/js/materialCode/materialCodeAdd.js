
// 已经选择的商品集合key：表格的行trId，value：商品信息
var allChooseProduct = {};

layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form', 'eleTree'], function (exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
        form = layui.form;

    initTableChooseUtil.initTable({
        id: "materialNormsList",
        cols: [
            {id: 'materialId', title: '产品', formType: 'chooseInput', width: '150', iconClassName: 'chooseProductBtn', verify: 'required'},
            {id: 'normsId', title: '规格', formType: 'select', width: '180', verify: 'required'},
            {id: 'operNumber', title: '条形码数量', formType: 'input', width: '140', verify: 'required|number', value: '1'}
        ],
        deleteRowCallback: function (trcusid) {
            delete allChooseProduct[trcusid];
        },
        addRowCallback: function (trcusid) {
        },
        form: form,
        minData: 1
    });

    var selOptionHtml = getFileContent('tpl/template/select-option.tpl');
    $("body").on("click", ".chooseProductBtn", function (e) {
        var trId = $(this).parent().parent().attr("trcusid");
        erpOrderUtil.openMaterialChooseChoosePage(function (chooseProductMation) {
            // 获取表格行号
            var thisRowKey = trId.replace("tr", "");
            // 产品名称赋值
            $("#materialId" + thisRowKey).val(chooseProductMation.name);
            $("#materialId" + thisRowKey).attr(initTableChooseUtil.chooseInputDataIdKey, chooseProductMation.id);
            // 规格赋值
            $("#normsId" + thisRowKey).html(getDataUseHandlebars(selOptionHtml, {rows: chooseProductMation.materialNorms}));
            form.render('select');
            // 商品赋值
            allChooseProduct[trId] = chooseProductMation;
        });
    });

    matchingLanguage();
    form.render();
    form.on('submit(formAddBean)', function (data) {
        if (winui.verifyForm(data.elem)) {
            var result = initTableChooseUtil.getDataList('materialNormsList');
            if (!result.checkResult) {
                return false;
            }
            var noError = false;
            var tableData = [];
            $.each(result.dataList, function(i, item) {
                // 获取行编号
                var thisRowKey = item["trcusid"].replace("tr", "");
                if (parseInt(item.operNumber) == 0) {
                    $("#operNumber" + thisRowKey).addClass("layui-form-danger");
                    $("#operNumber" + thisRowKey).focus();
                    winui.window.msg('数量不能为0', {icon: 2, time: 2000});
                    noError = true;
                    return false;
                }
                // 商品对象
                var material = allChooseProduct["tr" + thisRowKey];
                if (inTableDataArrayByAssetarId(material.materialId, item.normsId, tableData)) {
                    winui.window.msg('一张单中不允许出现相同单位的商品信息.', {icon: 2, time: 2000});
                    noError = true;
                    return false;
                }
                item["materialId"] = material.id;
                tableData.push(item);
            });
            if (noError) {
                return false;
            }

            var params = {
                list: JSON.stringify(tableData),
            };
            AjaxPostUtil.request({url: sysMainMation.erpBasePath + "insertMaterialNormsCode", params: params, type: 'json', method: 'POST', callback: function (json) {
                parent.layer.close(index);
                parent.refreshCode = '0';
            }});
        }
        return false;
    });

    // 判断选中的商品是否也在数组中
    function inTableDataArrayByAssetarId(materialId, normsId, array) {
        var isIn = false;
        $.each(array, function(i, item) {
            if(item.normsId === normsId && item.materialId === materialId) {
                isIn = true;
                return false;
            }
        });
        return isIn;
    }

    $("body").on("click", "#cancle", function() {
        parent.layer.close(index);
    });
});