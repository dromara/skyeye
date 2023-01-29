
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
    winui.renderColor();
    var $ = layui.$,
        form = layui.form;
    var data = parent.contentData;
    if (data == null) {
        $("#showForm").html('');
        return false;
    }
    var selOption = getFileContent('tpl/template/select-option.tpl');

    // 不同的数据来源对应不同的html
    var dataTypeObject = {
        "1": `<div class="layui-form-item layui-col-xs12">
                <label class="layui-form-label">默认数据<i class="red">*</i></label>
                <div class="layui-input-block">
                    <textarea id="defaultData" name="defaultData" win-verify="required" class="layui-textarea"></textarea>
                    <div class="layui-form-mid layui-word-aux">数据样式为：[{"id":"1","name":"男",...},{"id":"2","name":"女",...}]</div>
                </div>
            </div>`,
        "2": `<div class="layui-form-item layui-col-xs12">
                <label class="layui-form-label">枚举数据<i class="red">*</i></label>
                <div class="layui-input-block">
                    <select id="objectId" name="objectId" lay-search="" win-verify="required" lay-filter="objectId"></select>
                </div>
            </div>`,
        "3": `<div class="layui-form-item layui-col-xs12">
                <label class="layui-form-label">数据字典<i class="red">*</i></label>
                <div class="layui-input-block">
                    <select id="objectId" name="objectId" lay-search="" win-verify="required" lay-filter="objectId"></select>
                </div>
            </div>`
    };

    $("#showForm").html(getDataUseHandlebars($("#controlItemEdit").html(), {bean: data}));

    // 属性信息
    var attrs = [];
    $.each(parent.attrList, function (i, item) {
        // 获取已经绑定组件的属性并且适用于当前组件的属性
        if (!isNull(item.dsFormComponent) && data.dsFormComponent.id == item.dsFormComponent.id) {
            attrs.push({
                id: item.attrKey,
                name: item.name
            })
        }
    });
    $("#attrKey").html(getDataUseHandlebars(selOption, {rows: attrs}));
    $("#attrKey").val(data.attrKey);
    form.on('select(attrKey)', function(data) {
        if (!isNull(data.value)) {
            // 判断该属性是否已经存在
            var temp = getInPoingArr(parent.contentList, 'attrKey', data.value);
            if (!isNull(temp)) {
                winui.window.msg('该属性已存在于布局，请重新选择.', {icon: 2, time: 2000});
                $("#attrKey").val('');
            }
        }
    });

    // 宽度
    skyeyeClassEnumUtil.showEnumDataListByClassName("widthScale", 'select', "width", data.width, form);

    // 加载组件关联的属性
    $.each(data.dsFormComponent.attrKeys, function (i, item) {
        $("#" + item).removeClass("layui-hide");
    });

    if (data.dsFormComponent.linkedData == 1) {
        // 允许关联数据
        $("#linkDataBox").removeClass("layui-hide");
        // 数据来源类型
        skyeyeClassEnumUtil.showEnumDataListByClassName("pageComponentDataType", 'select', "dataType", data.dataType, form);
        form.on('select(dataType)', function(data) {
            loadDataMation($('#dataType').val());
        });
        loadDataMation(data.dataType)
    }
    $("#width").val(data.width);

    // 属性的限制条件
    skyeyeClassEnumUtil.showEnumDataListByClassName("verificationParams", 'verificationSelect', "require", data.require, form, 'formerRequirement');

    matchingLanguage();
    form.render();
    form.on('submit(formAddBean)', function (data) {
        if (winui.verifyForm(data.elem)) {
            saveNodeData();
        }
        return false;
    });

    function saveNodeData() {
        var inDataIndex = -1;
        $.each(parent.contentList, function (i, item) {
            if (item.id === data.id) {
                inDataIndex = i;
            }
        });
        var newParams = parent.contentList[inDataIndex];
        newParams.placeholder = $("#placeholder").val();
        newParams.require = isNull($('#require').attr('value')) ? [] : $('#require').attr('value');
        newParams.defaultValue = $("#defaultValue").val();
        newParams.width = $("#width").val();
        newParams.attrKey = $("#attrKey").val();
        newParams.attrDefinition = getInPoingArr(parent.attrList, 'attrKey', $("#attrKey").val());

        if (newParams.dsFormComponent.linkedData == 1) {
            newParams.dataType = $("#dataType").val();
            if (newParams.dataType == 1) {
                // 自定义
                var defaultDataStr = $("#defaultData").val();
                if (isNull(defaultDataStr)) {
                    winui.window.msg("请填写Json串！", {icon: 2, time: 2000});
                    return false;
                } else {
                    if (isJSON(defaultDataStr)) {
                        var defaultKey = getOutKey(defaultDataStr);//取出json串的键
                        // 获取数据展示模板
                        var tplContentVal = strMatchAllByTwo(data.dsFormComponent.htmlDataFrom, '{{', '}}');//取出数据模板中用{{}}包裹的词
                        removeByValue(tplContentVal, "#each this");
                        removeByValue(tplContentVal, "/each");
                        if (subset(tplContentVal, defaultKey)) {
                            newParams.defaultData = defaultDataStr;
                        } else {
                            winui.window.msg('json串内容有误，请重新填写!', {icon: 2, time: 2000});
                            return false;
                        }
                    } else {
                        winui.window.msg('json串格式不正确，请重新填写!', {icon: 2, time: 2000});
                        return false;
                    }
                }
            } else {
                newParams.objectId = $("#objectId").val();
            }
        }
        parent.contentList = parent.contentList.map(t => {
            return t.id === data.id ? newParams : t;
        });
        parent.sortDataIn();
        $(".mask-req-str").remove();
    }

    function loadDataMation(value) {
        $("#dataTypeObjectBox").html(dataTypeObject[value]);
        if (value == 1) {
            // 自定义
        } else if (value == 2) {
            // 枚举
            initEnumData();
        } else if (value == 3) {
            // 数据字典
            initDictData();
        }
    }

    /**
     * 加载枚举类可选列表
     */
    function initEnumData() {
        var arr = [];
        $.each(skyeyeClassEnum, function (key, value) {
            arr.push({
                id: key,
                name: value.name
            })
        });
        $("#objectId").html(getDataUseHandlebars(selOption, {rows: arr}));
        form.render('select');
    }

    /**
     * 加载数据字典可选列表
     */
    function initDictData() {
        AjaxPostUtil.request({url: reqBasePath + "queryDictTypeListByEnabled", params: {enabled: 1}, type: 'json', method: 'GET', callback: function (json) {
            $("#objectId").html(getDataUseHandlebars(selOption, json));
            form.render('select');
        }});
    }

});