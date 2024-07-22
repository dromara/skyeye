
// 已经选择的资产集合key：表格的行trId，value：资产信息
// 改
var allChooseFarm = {};

layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table','jquery', 'winui', 'form'], function (exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
        form = layui.form,
        table = layui.table;
    var selTemplate = getFileContent('tpl/template/select-option.tpl');
    let farmId = getNotUndefinedVal(GetUrlParam("id"));
    // 改
    let assetMap = {};

    initTableChooseUtil.initTable({
        id: "arrangeList",
        cols: [
            {id: 'workshopId', title: '安排车间', formType: 'input', width: '150', verify: 'required'},
            {id: 'operNumber', title: '安排任务数量', formType: 'input', width: '140', verify: 'required|number'}
        ],
        deleteRowCallback: function (trcusid) {
            delete allChooseFarm[trcusid];
        },
        addRowCallback: function (trcusid) {
            if (!isNull(farmId)) {
                if (isNull(assetMap[farmId])) {
                    AjaxPostUtil.request({url: sysMainMation.admBasePath + "queryAssetById", params: {"id": farmId}, type: 'json', method: 'GET', callback: function (json) {
                            assetMap[farmId] = json.bean
                        }, async: false});
                }
                let chooseAssetMation = assetMap[farmId]
                // 获取表格行号
                var thisRowKey = trcusid.replace("tr", "");
                // 资产名称赋值
                $("#farmId" + thisRowKey).val(chooseAssetMation.name);
                $("#farmId" + thisRowKey).attr(initTableChooseUtil.chooseInputDataIdKey, chooseAssetMation.id);
                // 资产赋值
                // 车间赋值
                allChooseFarm[trcusid] = chooseAssetMation;
            }
        },
        form: form,
        minData: 1
    });

    $("body").on("click", "farmId", function (e) {
        // 查询车间列表
        AjaxPostUtil.request({url: sysMainMation.erpBasePath + "erpfarm001", params: {page:1,limit:30}, type: 'json', method: "POST", callback: function(json) {
            console.log(666,json)
                // $("#workshopId").html(getDataUseHandlebars(selTemplate, json));
                // form.render('select');
                // initTable();
            }, async: false});
    });

    var workshopId = "";
    form.on('select(workshopId)', function(data) {
        var thisRowValue = data.value;
        workshopId = isNull(thisRowValue) ? "" : thisRowValue;
        loadTable();
    });

    // AjaxPostUtil.request({url: sysMainMation.erpBasePath + "erpfarm001",  params: {page: page, limit: 15}, type: 'json', method: 'POST', callback: function (json) {
    //         parent.layer.close(index);
    //         parent.refreshCode = '0';
    //     }});

    matchingLanguage();
    form.render();
    form.on('submit(formAddBean)', function (data) {
        if (winui.verifyForm(data.elem)) {
            var result = initTableChooseUtil.getDataList('arrangeList');
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
                // 车间对象
                // 更换下面两个
                // assert
                // allChooseAsset
                // inTableDataArrayByAssetarId
                var farm = allChooseFarm["tr" + thisRowKey];
                if (inTableDataArrayByAssetarId(farm.id, tableData)) {
                    winui.window.msg('一张单中不允许出现相同的资产信息.', {icon: 2, time: 2000});
                    noError = true;
                    return false;
                }
                item["farmId"] = farm.id;
                tableData.push(item);
            });
            if (noError) {
                return false;
            }

            var params = {
                list: JSON.stringify(tableData),
            };
            AjaxPostUtil.request({url: sysMainMation.admBasePath + "insertAssetReport", params: params, type: 'json', method: 'POST', callback: function (json) {
                    parent.layer.close(index);
                    parent.refreshCode = '0';
                }});
        }
        return false;
    });

    // 判断选中的资产是否也在数组中
    function inTableDataArrayByAssetarId(farmId, array) {
        var isIn = false;
        $.each(array, function(i, item) {
            if(item.farmId === farmId) {
                isIn = true;
                return false;
            }
        });
        return isIn;
    }

    $("body").on("click", "#cancle", function() {
        parent.layer.close(index);
    });

    function loadTable() {
        table.reloadData("messageTable", {where: getTableParams()});
    }

    function getTableParams() {
        var params = {
            type: 'farm',
            objectId: workshopId
        }
        return $.extend(true, params, initTableSearchUtil.getSearchValue("messageTable"));
    }
});