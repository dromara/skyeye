// var rowId = "";
//
// var memberId = "";
// var serviceClassName = "";
// layui.config({
//     base: basePath,
//     version: skyeyeVersion
// }).extend({
//     window: 'js/winui.window'
// }).define(['window', 'table', 'jquery', 'winui', 'form'], function (exports) {
//     winui.renderColor();
//     var $ = layui.$,
//         form = layui.form,
//         table = layui.table;
//     authBtn('1644234244354');
//
//     objectKey = GetUrlParam("serviceClassName");
//     objectId = GetUrlParam("id");
//     if (isNull(objectKey) || isNull(objectId)) {
//         winui.window.msg("请传入适用对象信息", {icon: 2, time: 2000});
//         return false;
//     }
//
//     table.render({
//         id: 'messageTable',
//         elem: '#messageTable',
//         method: 'post',
//         url: sysMainMation.erpBasePath + 'queryHolderNormsList',
//         where: getTableParams(),
//         even: true,
//         page: true,
//         limits: getLimits(),
//         limit: getLimit(),
//         cols: [[
//             { title: systemLanguage["com.skyeye.serialNumber"][languageType], fixed: 'left', type: 'numbers' },
//             { field: 'name', title: '产品名称', align: 'left',width: 150, templet: function (d) {return isNull(d.materialMation) ? '' : d.materialMation.name}},
//             { field: 'norms', title: '产品规格', align: 'left',width: 280, templet: function (d) {return isNull(d.normsMation) ? '' : d.normsMation.name}},
//             { field: 'allOperNumber', title: '数量', align: 'center', width: 100,templet: function (d) {
//                     var str = '<a lay-event="barcode" class="notice-title-click">' + d.allOperNumber + '</a>';
//                     return str;}},
//         ]],
// 	    done: function(json) {
// 	    	matchingLanguage();
// 	    }
//     });
//     table.on('tool(messageTable)', function (obj) {
//         var data = obj.data;
//         var layEvent = obj.event;
//         if (layEvent === 'barcode') { //条形码详情
//             barcode(data);
//         }
//         // if (layEvent === 'edit') { //编辑
//         //     editmember(data);
//         // } else if (layEvent === 'delete') { //删除
//         //     deletemember(data);
//         // }
//     });
//
//     function barcode(data) {
//         _openNewWindows({
//             // url: "../../tpl/storeInventory/storeInventoryDetails.html?materialId=" + data.materialId
//             //     + "&normsId="+data.normsId + "&storeId=" + data.storeId,
//             url:  '../../tpl/holder/holderNormsChildList.html?normsId=' + data.normsId + '&holderId=' + data.holderId + '&holderKey=' + data.holderKey,
//
//             // url: "../../tpl/storeInventory/storeInventoryDetails.html",
//             title: "详情",
//             pageId: "barcodeDetails",
//             area: ['90vw', '90vh'],
//             callBack: function (refreshCode) {
//                 winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
//                 loadTable();
//             }});
//     }
//
//     // // 编辑
//     // function editmember(data) {
//     //     rowId = data.id;
//     //     _openNewWindows({
//     //         url: "../../tpl/memberCar/memberCarEdit.html",
//     //         title: systemLanguage["com.skyeye.editPageTitle"][languageType],
//     //         pageId: "memberCarEdit",
//     //         area: ['90vw', '90vh'],
//     //         callBack: function (refreshCode) {
//     //             winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
//     //             loadTable();
//     //         }});
//     // }
//
//     // 删除
//     // function deletemember(data) {
//     //     layer.confirm("删除后关联套餐以及其他信息无法使用，确认删除吗？", {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
//     //         layer.close(index);
//     //         AjaxPostUtil.request({url: shopBasePath + "memberCar004", params: {rowId: data.id}, type: 'json', method: "POST", callback: function (json) {
//     //             winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
//     //             loadTable();
//     //         }});
//     //     });
//     // }
//
//     //
//     // // 添加
//     // $("body").on("click", "#addBean", function() {
//     //     _openNewWindows({
//     //         url: "../../tpl/memberCar/memberCarAdd.html",
//     //         title: systemLanguage["com.skyeye.addPageTitle"][languageType],
//     //         pageId: "memberCarAdd",
//     //         area: ['90vw', '90vh'],
//     //         callBack: function (refreshCode) {
//     //             winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
//     //             loadTable();
//     //         }});
//     // });
//
//     form.render();
//     form.on('submit(formSearch)', function (data) {
//         if (winui.verifyForm(data.elem)) {
//             table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()})
//         }
//         return false;
//     });
//
//     $("body").on("click", "#reloadTable", function() {
//         loadTable();
//     });
//
//     // 刷新
//     function loadTable() {
//         table.reloadData("messageTable", {where: getTableParams()});
//     }
//
//     function getTableParams() {
//         return $.extend(true, {holderKey: objectKey, holderId: objectId}, initTableSearchUtil.getSearchValue("messageTable"));
//     }
//
//     exports('memberList', {});
// });
