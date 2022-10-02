/**
 * 根据规格加载库存
 *
 * @param rowNum 表格行坐标
 * @param depotId 仓库id
 */
function loadTockByDepotAndMUnit(rowNum, depotId) {
    // 获取当前选中的规格
    var normsId = $("#mUnitId" + rowNum).val();
    // 当规格不为空时
    if (!isNull(normsId)) {
        // 获取库存
        getStockAjaxByDepotAndNormsId(normsId, depotId, function (json) {
            $("#currentTock" + rowNum).html(json.bean[normsId]);
        });
    } else {
        // 否则重置库存为空
        $("#currentTock" + rowNum).html("");
    }
}

/**
 * 根据仓库id加载表格中已经选择的商品规格的库存
 *
 * @param depotId 仓库id
 */
function loadMaterialDepotStockByDepotId(depotId) {
    var normsIds = new Array();
    var normsIdIndex = new Array();
    $.each(initTableChooseUtil.getDataRowIndex('productList'), function (i, item) {
        // 获取行坐标
        var thisRowKey = item;
        var normsId = $("#mUnitId" + thisRowKey).val();
        if (!isNull(normsId)) {
            normsIds.push(normsId);
            normsIdIndex[thisRowKey] = normsId;
        }
    });
    if (normsIds.length == 0) {
        return;
    }
    // 获取库存
    getStockAjaxByDepotAndNormsId(normsIds.join(','), depotId, function (json) {
        var stockMation = json.bean;
        $.each(normsIdIndex, function (rowIndex, normsId) {
            $("#currentTock" + normsIdIndex[rowIndex]).html(stockMation[normsId]);
        });
    });
}

/**
 * 发送请求获取库存信息
 * @param normsIds 多规格id,逗号隔开
 * @param depotId 仓库id,可为空
 * @param callBack 回调函数
 */
function getStockAjaxByDepotAndNormsId(normsIds, depotId, callBack) {
    var params = {
        depotId: depotId,
        normsIds: normsIds
    };
    AjaxPostUtil.request({url: flowableBasePath + "material011", params: params, type: 'json', method: "POST", callback: function(json) {
        if(typeof(callBack) == "function") {
            callBack(json);
        }
    }});
}

// 判断选中的商品是否也在数组中
function inTableDataArrayByAssetarId(materialId, unitId, array) {
    var isIn = false;
    $.each(array, function(i, item) {
        if(item.mUnitId === unitId && item.materialId === materialId) {
            isIn = true;
            return false;
        }
    });
    return isIn;
}

/**
 * 商品规格加载变化事件
 *
 * @param form 表单对象
 * @param allChooseProduct 商品对象
 * @param unitPriceKey 单价显示的key，不用的单据类型展示不同的商品价格类型(零售价，最低售价，销售价等)
 * @param calcPriceCallback 计算价格回调的函数
 */
function mUnitChangeEvent(form, allChooseProduct, unitPriceKey, calcPriceCallback) {
    // 商品规格加载变化事件
    form.on('select(selectUnitProperty)', function(data) {
        var thisRowValue = data.value;
        var thisRowKey = data.elem.id.replace("mUnitId", "").toString();
        // 当前当前行选中的商品信息
        if (!isNull(thisRowValue)) {
            var product = allChooseProduct["tr" + thisRowKey];
            $.each(product.unitList, function (j, bean) {
                if (thisRowValue == bean.id) {
                    var rkNum = parseInt($("#rkNum" + thisRowKey).val());
                    // 设置单价和金额
                    var unitPrice = bean[unitPriceKey].toFixed(2);
                    $("#unitPrice" + thisRowKey).val(unitPrice);
                    $("#amountOfMoney" + thisRowKey).val((rkNum * parseFloat(unitPrice)).toFixed(2));
                    return false;
                }
            });
        } else {
            // 重置单价以及金额为空
            $("#unitPrice" + thisRowKey).val("0.00");
            $("#amountOfMoney" + thisRowKey).val("0.00");
        }
        var depotId = isNull($("#depotId").val()) ? "" : $("#depotId").val();
        // 加载库存
        loadTockByDepotAndMUnit(thisRowKey, depotId);
        if (typeof calcPriceCallback == "function") {
            calcPriceCallback();
        } else {
            // 计算价格
            calculatedTotalPrice();
        }
    });
}

/**
 * 初始化商品选择的监听事件
 *
 * @param form 表单对象
 * @param callback 回调函数
 * @param calcPriceCallback 计算价格回调的函数
 */
function initChooseProductBtnEnent (form, callback, calcPriceCallback) {
    var selOptionHtml = getFileContent('tpl/template/select-option.tpl');
    $("body").on("click", ".chooseProductBtn", function (e) {
        var trId = $(this).parent().parent().attr("trcusid");
        erpOrderUtil.openMaterialChooseChoosePage(function (chooseProductMation) {
            // 获取表格行号
            var thisRowKey = trId.replace("tr", "");
            // 表格商品名称赋值
            $("#materialId" + thisRowKey.toString()).val(chooseProductMation.materialName + "(" + chooseProductMation.materialModel + ")");
            // 表格单位赋值
            $("#mUnitId" + thisRowKey.toString()).html(getDataUseHandlebars(selOptionHtml, {rows: chooseProductMation.unitList}));
            form.render('select');
            if (typeof callback == "function") {
                callback(trId, chooseProductMation);
            }
            if (typeof calcPriceCallback == "function") {
                calcPriceCallback();
            } else {
                // 计算价格
                calculatedTotalPrice();
            }
        });
    });
}

