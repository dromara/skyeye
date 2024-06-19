
// 已经选择的资产集合key：表格的行trId，value：资产信息
var allChooseAsset = {};

layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form'], function (exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
        form = layui.form;
    let assetId = getNotUndefinedVal(GetUrlParam("assetId"));
    let assetMap = {};

    initTableChooseUtil.initTable({
        id: "assetList",
        cols: [
            {id: 'assetId', title: '资产', formType: 'chooseInput', width: '150', iconClassName: 'chooseAssetBtn', verify: 'required'},
            {id: 'operNumber', title: '条形码数量', formType: 'input', width: '140', verify: 'required|number', value: '1'}
        ],
        deleteRowCallback: function (trcusid) {
            delete allChooseAsset[trcusid];
        },
        addRowCallback: function (trcusid) {
            if (!isNull(assetId)) {
                if (isNull(assetMap[assetId])) {
                    AjaxPostUtil.request({url: sysMainMation.admBasePath + "queryAssetById", params: {"id": assetId}, type: 'json', method: 'GET', callback: function (json) {
                        assetMap[assetId] = json.bean
                    }, async: false});
                }
                let chooseAssetMation = assetMap[assetId]
                // 获取表格行号
                var thisRowKey = trcusid.replace("tr", "");
                // 资产名称赋值
                $("#assetId" + thisRowKey).val(chooseAssetMation.name);
                $("#assetId" + thisRowKey).attr(initTableChooseUtil.chooseInputDataIdKey, chooseAssetMation.id);
                // 资产赋值
                allChooseAsset[trcusid] = chooseAssetMation;
            }
        },
        form: form,
        minData: 1
    });

    $("body").on("click", ".chooseAssetBtn", function (e) {
        var trId = $(this).parent().parent().attr("trcusid");
        adminAssistantUtil.assetCheckType = false; // 选择类型，默认单选，true:多选，false:单选
        adminAssistantUtil.openAssetChoosePage(function (checkAssetMation){
            // 获取表格行号
            var thisRowKey = trId.replace("tr", "");
            $("#assetId" + thisRowKey.toString()).attr(initTableChooseUtil.chooseInputDataIdKey, checkAssetMation.id);
            $("#assetId" + thisRowKey.toString()).val(checkAssetMation.name);
            allChooseAsset[trId] = checkAssetMation;
        });
    });

    matchingLanguage();
    form.render();
    form.on('submit(formAddBean)', function (data) {
        if (winui.verifyForm(data.elem)) {
            var result = initTableChooseUtil.getDataList('assetList');
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
                // 资产对象
                var asset = allChooseAsset["tr" + thisRowKey];
                if (inTableDataArrayByAssetarId(asset.id, tableData)) {
                    winui.window.msg('一张单中不允许出现相同的资产信息.', {icon: 2, time: 2000});
                    noError = true;
                    return false;
                }
                item["assetId"] = asset.id;
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
    function inTableDataArrayByAssetarId(assetId, array) {
        var isIn = false;
        $.each(array, function(i, item) {
            if(item.assetId === assetId) {
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