/**
 * 根据规格加载库存
 * @param rowNum 表格行坐标
 * @param depotId 仓库id
 */
function loadTockByDepotAndMUnit(rowNum, depotId){
    //获取当前选中的规格
    var chooseUnitId = $("#unitId" + rowNum).val();
    //当规格不为空时
    if(!isNull(chooseUnitId)){
        // 获取库存
        getStockAjaxByDepotAndNormsId(chooseUnitId, depotId, function(json){
            var currentTock = 0;
            if(!isNull(json.bean)){
                currentTock = json.bean.currentTock;
            }
            $("#currentTock" + rowNum).html(currentTock);
        });
    } else {
        //否则重置库存为空
        $("#currentTock" + rowNum).html("");
    }
}

/**
 * 根据仓库id加载表格中已经选择的商品规格的库存
 * @param depotId 仓库id
 */
function loadMaterialDepotStockByDepotId(depotId){
    var rowTr = $("#useTable tr");
    var normsIds = new Array();
    var normsIdsNum = new Array();
    $.each(rowTr, function(i, item) {
        // 获取行坐标
        var rowNum = $(item).attr("trcusid").replace("tr", "");
        var unitId = $("#unitId" + rowNum).val();
        if(!isNull(unitId)){
            normsIds.push(unitId);
            normsIdsNum.push(rowNum);
        }
    });
    if(normsIds.length == 0){
        return;
    }
    // 获取库存
    getStockAjaxByDepotAndNormsId(normsIds.join(','), depotId, function(json){
        if(normsIds.length == 1){
            $("#currentTock" + normsIdsNum[0]).html(json.bean.currentTock);
        } else {
            $.each(normsIdsNum, function(i, item) {
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
function getStockAjaxByDepotAndNormsId(normsIds, depotId, callBack){
    AjaxPostUtil.request({url: flowableBasePath + "material011", params: {depotId: depotId, normsIds: normsIds}, type: 'json', method: "POST", callback: function(json) {
        if(json.returnCode == 0) {console.log(typeof(callBack))
            if(typeof(callBack) == "function") {
                callBack(json);
            }
        } else {
            winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
        }
    }});
}