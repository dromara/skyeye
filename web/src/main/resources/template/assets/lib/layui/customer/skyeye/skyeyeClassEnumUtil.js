
// 枚举类相关的工具类
var skyeyeClassEnumUtil = {

    classEnumMap: {},

    /**
     * 展示枚举类的集合数据
     *
     * @param code 枚举类对应的前台code
     * @param showType 展示类型
     * @param showBoxId 展示位置
     * @param defaultId 默认回显值
     * @param form form对象
     * @param callback 回调函数
     */
    showEnumDataListByClassName: function (code, showType, showBoxId, defaultId, form, callback) {
        var json = skyeyeClassEnumUtil.getEnumDataListByClassName(code);
        dataShowType.showData(json, showType, showBoxId, defaultId, form, callback);
    },

    getEnumDataNameByClassName: function (code, key, value, getKey) {
        var json = skyeyeClassEnumUtil.getEnumDataListByClassName(code);
        var result = getInPoingArr(json.rows, key, value, getKey);
        return isNull(result) ? '' : result;
    },

    getEnumDataListByClassName: function (code) {
        if (isNull(skyeyeClassEnumUtil.classEnumMap[code])) {
            var params = {
                className: encodeURIComponent(skyeyeClassEnum[code]["className"])
            };
            if (!isNull(skyeyeClassEnum[code]["filterValue"])) {
                params["filterValue"] = skyeyeClassEnum[code]["filterValue"];
                params["filterKey"] = skyeyeClassEnum[code]["filterKey"];
            }
            AjaxPostUtil.request({url: reqBasePath + "getEnumDataByClassName", params: params, type: 'json', method: "POST", callback: function(json) {
                skyeyeClassEnumUtil.classEnumMap[code] = json;
            }, async: false});
        }
        return skyeyeClassEnumUtil.classEnumMap[code];
    },

    getEnumDataNameByCodeAndKey: function (code, idKey, key, displayNameKey) {
        var json = skyeyeClassEnumUtil.getEnumDataListByClassName(code);
        var displayName = '';
        $.each(json.rows, function (i, item) {
            if (item[idKey] == key) {
                displayName = item[displayNameKey];
            }
        });
        return displayName;
    }

};
