

var sysDictDataUtil = {

    /**
     * 获取指定状态的数据字典分类
     *
     * @param status 状态（0正常 1停用）
     * @param callback 回执函数
     */
    queryDictTypeListByStatus: function (status, callback) {
        var params = {
            status: status
        };
        AjaxPostUtil.request({url: reqBasePath + "queryDictTypeListByStatus", params: params, type: 'json', method: "GET", callback: function(json) {
            if (typeof(callback) == "function") {
                callback(json);
            }
        }, async: false});
    },

    /**
     * 获取指定分类下的的数据字典
     *
     * @param dictTypeCode 数据字典所属分类的Code
     * @param callback 回执函数
     */
    showDictDataListByDictTypeCode: function (dictTypeCode, showType, showBoxId, defaultId, form, callback) {
        var params = {
            dictTypeCode: dictTypeCode
        };
        AjaxPostUtil.request({url: reqBasePath + "queryDictDataListByDictTypeCode", params: params, type: 'json', method: "GET", callback: function(json) {
            if (showType == 'select') {
                $("#" + showBoxId).html(getDataUseHandlebars(getFileContent('tpl/template/select-option.tpl'), json));
                if (!isNull(defaultId)) {
                    $("#" + showBoxId).val(defaultId);
                }
                form.render('select');
            }
            if (typeof (callback) == "function") {
                callback(json);
            }
        }, async: false});
    },

    /**
     * 获取指定分类下的的数据字典
     *
     * @param dictTypeCode 数据字典所属分类的Code
     * @param callback 回执函数
     */
    queryDictDataListByDictTypeCode: function (dictTypeCode, callback) {
        var params = {
            dictTypeCode: dictTypeCode
        };
        AjaxPostUtil.request({url: reqBasePath + "queryDictDataListByDictTypeCode", params: params, type: 'json', method: "GET", callback: function(json) {
            if (typeof(callback) == "function") {
                callback(json);
            }
        }, async: false});
    },

};
