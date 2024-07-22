
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
    // var selTemplate = getFileContent('tpl/template/select-option.tpl');
    let farmId = getNotUndefinedVal(GetUrlParam("id"));
    // 改
    // let assetMap = {};

    initTableChooseUtil.initTable({
        id: "arrangeList",
        cols: [
            {id: 'workshopId', title: '安排车间', formType: 'select', width: '150', verify: 'required',templet: function(d) {
                    var options = queryWorkshopList();
                    return '<select name="workshop" class="workshop-select">' + options + '</select>';
                }
            },
            {id: 'taskNumber', title: '安排任务数量', formType: 'input', width: '140', verify: 'required|number'}
        ],
        deleteRowCallback: function (trcusid) {
            delete allChooseFarm[trcusid];
        },
        addRowCallback: function (trcusid) {
            // if (!isNull(farmId)) {
            //     if (isNull(assetMap[farmId])) {
            //         AjaxPostUtil.request({url: sysMainMation.admBasePath + "queryAssetById", params: {"id": farmId}, type: 'json', method: 'GET', callback: function (json) {
            //                 assetMap[farmId] = json.bean
            //             }, async: false});
            //     }
            //     let chooseAssetMation = assetMap[farmId]
            //     // 获取表格行号
            //     var thisRowKey = trcusid.replace("tr", "");
            //     // 资产名称赋值
            //     $("#farmId" + thisRowKey).val(chooseAssetMation.name);
            //     $("#farmId" + thisRowKey).attr(initTableChooseUtil.chooseInputDataIdKey, chooseAssetMation.id);
            //     // 资产赋值
            //     allChooseFarm[trcusid] = chooseAssetMation;
            // }
        },
        form: form,
        minData: 1
    });
    //
    // var workshopId = "";
    // form.on('select(workshopId)', function(data) {
    //     console.log(555,data)
    //     // var thisRowValue = data.value;
    //     // workshopId = isNull(thisRowValue) ? "" : thisRowValue;
    //     aaa();
    // });
    // 渲染表格
//     function aaa(){
//     table.render({
//         id: 'arrangeList',//表格
//         elem: '#workshopId', //表格里的元素
//         method: 'post',
//         url: sysMainMation.erpBasePath + 'erpfarm001',
//         where: getTableParams(),
//         even: true,
//         page: true,
//         limits: getLimits(),
//         limit: getLimit(),
//         done: function(json){
//             // 表格渲染完成后的回调
//             // 在这里添加事件监听
//             $('select[name="workshop"]').on('change', function(){
//                 console.log('Workshop selected', $(this).val());
//                 // 如果需要，可以在这里打印'123'或进行其他操作
//             });
//
//             $('input[name="taskNumber"]').on('click', function(){
//                 console.log('taskNumber input clicked');
//                 // 打印'123'或其他操作
//             });
//         }
//         // done: function(json){
//         //     console.log(json)
//         //     // matchingLanguage();
//         //     // $('select[name="workshop"]').each(function(){
//         //     //     $(this).empty();
//         //     //     workshopData.forEach(function(item){
//         //     //         $(this).append('<option value="' + item.id + '">' + item.name + '</option>');
//         //     //     }, $(this));
//         //     // });
//         // }
//     });
// }

    function queryWorkshopList() {
        // 假设您有一个函数可以处理Ajax请求
        AjaxPostUtil.request({
            url: sysMainMation.erpBasePath + "erpfarm001",
            params: {page: 1, limit: 30},
            type: 'json',
            method: "POST",
            callback: function(json) {
                console.log(666, json);
            }
        });
    }

// $("body").on("click", "workshopId", function (e) {
//         // 查询车间列表
//         AjaxPostUtil.request({url: sysMainMation.erpBasePath + "erpfarm001", params: {page:1,limit:30}, type: 'json', method: "POST", callback: function(json) {
//             console.log(666,json)
//                 // $("#workshopId").html(getDataUseHandlebars(selTemplate, json));
//                 // form.render('select');
//                 // initTable();
//             }, async: false});
//     });

    // var workshopId = "";
    // form.on('select(workshopId)', function(data) {
    //     var thisRowValue = data.value;
    //     workshopId = isNull(thisRowValue) ? "" : thisRowValue;
    //     loadTable();
    // });

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
                if (parseInt(item.taskNumber) == 0) {
                    $("#taskNumber" + thisRowKey).addClass("layui-form-danger");
                    $("#taskNumber" + thisRowKey).focus();
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
        table.reloadData("arrangeList", {where: getTableParams()});
    }

    function getTableParams() {
        var params = {
            type: 'farm',
            objectId: workshopId
        }
        return $.extend(true, params, initTableSearchUtil.getSearchValue("messageTable"));
    }
});