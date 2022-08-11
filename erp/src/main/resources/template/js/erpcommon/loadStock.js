/**
 * 根据规格加载库存
 *
 * @param rowNum 表格行坐标
 * @param depotId 仓库id
 */
function loadTockByDepotAndMUnit(rowNum, depotId) {
    // 获取当前选中的规格
    var chooseUnitId = $("#mUnitId" + rowNum).val();
    // 当规格不为空时
    if (!isNull(chooseUnitId)) {
        // 获取库存
        getStockAjaxByDepotAndNormsId(chooseUnitId, depotId, function (json) {
            var currentTock = 0;
            if (!isNull(json.bean)) {
                currentTock = json.bean.currentTock;
            }
            $("#currentTock" + rowNum).html(currentTock);
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
    var rowTr = $("#useTable tr");
    var normsIds = new Array();
    var normsIdsNum = new Array();
    $.each(rowTr, function (i, item) {
        // 获取行坐标
        var rowNum = $(item).attr("trcusid").replace("tr", "");
        var unitId = $("#unitId" + rowNum).val();
        if (!isNull(unitId)) {
            normsIds.push(unitId);
            normsIdsNum.push(rowNum);
        }
    });
    if (normsIds.length == 0) {
        return;
    }
    // 获取库存
    getStockAjaxByDepotAndNormsId(normsIds.join(','), depotId, function (json) {
        if (normsIds.length == 1) {
            $("#currentTock" + normsIdsNum[0]).html(json.bean.currentTock);
        } else {
            $.each(normsIdsNum, function (i, item) {
                $("#currentTock" + normsIdsNum[i]).html(json.rows[i].currentTock);
            });
        }
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

/**
 * 商品规格加载变化事件
 *
 * @param form 表单对象
 */
function mUnitChangeEvent(form, allChooseProduct) {
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
                    $("#unitPrice" + thisRowKey).val(bean.estimatePurchasePrice.toFixed(2));
                    $("#amountOfMoney" + thisRowKey).val((rkNum * parseFloat(bean.estimatePurchasePrice)).toFixed(2));
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
        // 计算价格
        calculatedTotalPrice();
    });
}

/**
 * 初始化商品选择的监听事件
 *
 * @param form 表单对象
 * @param callback 回调函数
 */
function initChooseProductBtnEnent (form, callback) {
    var selOptionHtml = getFileContent('tpl/template/select-option.tpl');
    $("body").on("click", ".chooseProductBtn", function (e) {
        var trId = $(this).parent().parent().attr("trcusid");
        erpOrderUtil.openMaterialChooseChoosePage(function (chooseProductMation) {
            // 获取表格行号
            var thisRowKey = trId.replace("tr", "");
            // 表格商品名称赋值
            $("#materialId" + thisRowKey.toString()).val(chooseProductMation.productName + "(" + chooseProductMation.productModel + ")");
            // 表格单位赋值
            $("#mUnitId" + thisRowKey.toString()).html(getDataUseHandlebars(selOptionHtml, {rows: chooseProductMation.unitList}));
            form.render('select');
            if (typeof callback == "function") {
                callback(trId, chooseProductMation);
            }
            // 计算价格
            calculatedTotalPrice();
        });
    });
}

