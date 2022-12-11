
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
        if (showType == 'select') {
            $("#" + showBoxId).html(getDataUseHandlebars(getFileContent('tpl/template/select-option.tpl'), json));
            if (!isNull(defaultId)) {
                $("#" + showBoxId).val(defaultId);
            } else {
                $.each(json.rows, function (i, item) {
                    if (item.isDefault) {
                        $("#" + showBoxId).val(item.id);
                    }
                });
            }
            form.render('select');
        } else if (showType == 'checkbox') {
            $("#" + showBoxId).html(getDataUseHandlebars(getFileContent('tpl/template/checkbox-property.tpl'), json));
            if (!isNull(defaultId)) {
                var arr = defaultId.split(",");
                for(var i = 0; i < arr.length; i++){
                    $('input:checkbox[rowId="' + arr[i] + '"]').attr("checked", true);
                }
            } else {
                $.each(json.rows, function (i, item) {
                    if (item.isDefault) {
                        $('input:checkbox[rowId="' + item.id + '"]').attr("checked", true);
                    }
                });
            }
            form.render('checkbox');
        } else if (showType == 'radio') {
            $("#" + showBoxId).html(getDataUseHandlebars('{{#each rows}}<input type="radio" name="' + showBoxId + 'Name" value="{{id}}" title="{{name}}" />{{/each}}', json));
            if (!isNull(defaultId)) {
                $("#" + showBoxId + " input:radio[name=" + showBoxId + "Name][value=" + defaultId + "]").attr("checked", true);
            } else {
                $.each(json.rows, function (i, item) {
                    if (item.isDefault) {
                        $("#" + showBoxId + " input:radio[name=" + showBoxId + "Name][value=" + item.id + "]").attr("checked", true);
                    }
                });
            }
            form.render('radio');
        } else if (showType == 'verificationSelect') {
            var str = `<option value="">全部</option>{{#each rows}}<option value="{{formerRequirement}}">{{name}}</option>{{/each}}`;
            $("#" + showBoxId).html(getDataUseHandlebars(str, json));
            if (!isNull(defaultId)) {
                $("#" + showBoxId).val(defaultId.split(","));
            }
            form.render('select');
        }
        if (typeof (callback) == "function") {
            callback(json);
        }
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
