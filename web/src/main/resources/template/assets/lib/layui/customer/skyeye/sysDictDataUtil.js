

var sysDictDataUtil = {

    dictDataMap: {},

    /**
     * 获取指定状态的数据字典分类
     *
     * @param enabled 状态（1 启用  2.停用）
     * @param callback 回执函数
     */
    queryDictTypeListByEnabled: function (enabled, callback) {
        var params = {
            enabled: enabled
        };
        AjaxPostUtil.request({url: reqBasePath + "queryDictTypeListByEnabled", params: params, type: 'json', method: "GET", callback: function(json) {
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
        sysDictDataUtil.queryDictDataListByDictTypeCode(dictTypeCode, function (json) {
            if (showType == 'select') {
                $("#" + showBoxId).html(getDataUseHandlebars(getFileContent('tpl/template/select-option.tpl'), json));
                if (!isNull(defaultId)) {
                    $("#" + showBoxId).val(defaultId);
                }
                form.render('select');
            } else if (showType == 'checkbox') {
                $("#" + showBoxId).html(getDataUseHandlebars(getFileContent('tpl/template/checkbox-property.tpl'), json));
                if (!isNull(defaultId)) {
                    var arr = defaultId.split(",");
                    for(var i = 0; i < arr.length; i++){
                        $('input:checkbox[rowId="' + arr[i] + '"]').attr("checked", true);
                    }
                }
                form.render('checkbox');
            }
            if (typeof (callback) == "function") {
                callback(json);
            }
        });
    },

    /**
     * 获取指定分类下的的数据字典
     *
     * @param dictTypeCode 数据字典所属分类的Code
     * @param callback 回执函数
     */
    queryDictDataListByDictTypeCode: function (dictTypeCode, callback) {
        if (isNull(sysDictDataUtil.dictDataMap[dictTypeCode])) {
            var params = {
                dictTypeCode: dictTypeCode
            };
            AjaxPostUtil.request({url: reqBasePath + "queryDictDataListByDictTypeCode", params: params, type: 'json', method: "GET", callback: function(json) {
                sysDictDataUtil.dictDataMap[dictTypeCode] = json;
            }, async: false});
        }
        if (typeof(callback) == "function") {
            callback(sysDictDataUtil.dictDataMap[dictTypeCode]);
        }
    },

    getDictDataNameByCodeAndKey: function (dictTypeCode, key) {
        var displayName = '';
        sysDictDataUtil.queryDictDataListByDictTypeCode(dictTypeCode, function (json) {
            $.each(json.rows, function (i, item) {
                if (item.id == key) {
                    displayName = item.name;
                }
            });
        });
        return displayName;
    }

};
