// erp工具函数
var erpOrderUtil = {

    /**
     * 删除订单信息
     *
     * @param id 订单id
     * @param serviceClassName 单据类型
     */
    deleteOrderMation: function (id, serviceClassName, callback) {
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index) {
            AjaxPostUtil.request({url: sysMainMation.erpBasePath + "erpcommon005", params: {id: id, serviceClassName: serviceClassName}, method: "DELETE", type: 'json', callback: function(json) {
                winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                if (typeof (callback) == "function") {
                    callback();
                }
            }});
        });
    },

    /**
     * 提交订单信息
     *
     * @param id 订单id
     * @param serviceClassName 单据类型
     * @param submitType 单据提交类型  1.走工作流提交  2.直接提交
     * @param actKey 该地址为 sysServiceMation.json的key
     */
    submitOrderMation: function (id, serviceClassName, callback) {
        layer.confirm('确认要提交吗？', { icon: 3, title: '提交操作' }, function (index) {
            layer.close(index);
            activitiUtil.startProcess(serviceClassName, null, function (approvalId) {
                var params = {
                    id: id,
                    serviceClassName: serviceClassName,
                    approvalId: approvalId
                };
                AjaxPostUtil.request({url: sysMainMation.erpBasePath + "erpcommon006", params: params, method: "PUT", type: 'json', callback: function(json) {
                    winui.window.msg("提交成功。", {icon: 1, time: 2000});
                    if (typeof (callback) == "function") {
                        callback();
                    }
                }});
            });
        });
    },

    /**
     * 撤销订单信息
     *
     * @param processInstanceId 流程实例id
     * @param serviceClassName 单据类型
     */
    revokeOrderMation: function (processInstanceId, serviceClassName, callback) {
        layer.confirm('确认要撤销吗？', { icon: 3, title: '撤销操作' }, function (index) {
            var params = {
                processInstanceId: processInstanceId,
                serviceClassName: serviceClassName
            };
            AjaxPostUtil.request({url: sysMainMation.erpBasePath + "erpcommon003", params: params, type: 'json', method: "PUT", callback: function(json) {
                winui.window.msg("撤销成功。", {icon: 1, time: 2000});
                if (typeof(callback) == "function") {
                    callback();
                }
            }});
        });
    },

    /**
     * 获取所有仓库信息
     *
     * @param callback 回执函数
     */
    getDepotList: function (callback) {
        AjaxPostUtil.request({url: sysMainMation.erpBasePath + "queryAllStoreHouseList", params: {}, type: 'json', method: "GET", callback: function(json) {
            if (typeof(callback) == "function") {
                callback(json);
            }
        }, async: false});
    },

    /**
     * ERP商品选择对象以及工具函数
     */
    productCheckType: 1, // 商品选择类型：1.单选；2.多选
    chooseProductMation: {}, // 如果productCheckType=1，则为对象；如果productCheckType=2，则为集合
    openMaterialChooseChoosePage: function (callback) {
        _openNewWindows({
            url: "../../tpl/material/materialChoose.html",
            title: "选择商品",
            pageId: "productlist",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                if (typeof (callback) == "function") {
                    callback(erpOrderUtil.chooseProductMation);
                }
            }
        });
    },

};