
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

    $("#showForm").html(getDataUseHandlebars($("#controlItemEdit").html(), {bean: data}));
    if (data.dsFormComponent.linkedData == 1) {
        // 允许关联数据
        $("#isAssociated").removeClass("layui-hide");
        // 数据来源类型
        skyeyeClassEnumUtil.showEnumDataListByClassName("pageComponentDataType", 'select', "dataType", data.dataType, form);
        form.on('select(dataType)', function(data) {
            var value = $('#displayTemplateId').val();
            if (value == 1){
                $("#defaultDataBox").removeClass("layui-hide");
                initDisplayTemplate();
            } else {
                $("#defaultDataBox").addClass("layui-hide");
            }
        });
        if (data.dataType == 1) {
            // 自定义
            $(".defaultDataBox").removeClass("layui-hide");
            initDisplayTemplate();
        }
    }
    $("#width").val(data.width);

    var arr = [];
    if (!isNull(data.require)) {
        arr = data.require.split(",");
    }
    form.on('select(require)',function(data) {
        arr = data.value;
    });
    // 属性的限制条件
    skyeyeClassEnumUtil.showEnumDataListByClassName("verificationParams", 'verificationSelect', "require", data.require, form);

    matchingLanguage();
    form.render();
    form.on('submit(formAddBean)', function (data) {
        if (winui.verifyForm(data.elem)) {
            saveNodeData(data, data.id, arr);
            winui.window.msg("保存成功", {icon: 1, time: 2000});
        }
        return false;
    });

    function saveNodeData(data, contentId, arr) {
        var inDataIndex = -1;
        $.each(contentList, function(i, item) {
            if (item.id === contentId) {
                inDataIndex = i;
            }
        });
        if(inDataIndex == -1){
            return;
        }
        var newParams = contentList[inDataIndex];
        newParams.title = $("#title").val();
        newParams.placeholder = $("#placeholder").val();
        newParams.require = arr.join(",");
        newParams.defaultValue = $("#defaultValue").val();
        newParams.width = $("#width").val();
        newParams.attrKey = $("#attrKey").val();
        var linkedData; //控件关联的数据
        var defaultData; //选择事件的默认数据
        var tplContentVal; //数据展示模板的内容的值
        var templateContent; //数据展示模板的内容
        $.each(componentList, function (i, item) {
            if (item.id == newParams.formContentId) {
                linkedData = item.linkedData;
                templateContent = item.templateContent;
                if (!isNull(item.templateContent)) {
                    tplContentVal = strMatchAllByTwo(item.templateContent, '{{', '}}');//取出数据模板中用{{}}包裹的词
                    removeByValue(tplContentVal, "#each this");
                    removeByValue(tplContentVal, "/each");
                }
                if (!isNull(item.defaultData)) {
                    defaultData = item.defaultData;
                }
            }
        });
        if (newParams.linkedData == 1) {
            newParams.associatedDataTypes = data.field.associatedDataTypes;
            if (newParams.associatedDataTypes == 1) {
                var defaultDataStr = $("#jsonData").val();
                if (isNull(defaultDataStr)) {
                    winui.window.msg("请填写Json串！", {icon: 2, time: 2000});
                    return false;
                } else {
                    if (isJSON(defaultDataStr)) {
                        var defaultKey = getOutKey(defaultDataStr);//取出json串的键
                        if (subset(tplContentVal, defaultKey)) {
                            newParams.aData = defaultDataStr;
                        } else {
                            winui.window.msg('json串内容有误，请重新填写!', {icon: 2, time: 2000});
                            return false;
                        }
                    } else {
                        winui.window.msg('json串格式不正确，请重新填写!', {icon: 2, time: 2000});
                        return false;
                    }
                }
            } else if (newParams.associatedDataTypes == 2) {
                var interfa = $("#interfa").val();
                if (interfa.length == 0) {
                    winui.window.msg("请填写接口！", {icon: 2, time: 2000});
                    return false;
                }
                if (!checkURL(interfa)) {
                    winui.window.msg("接口请填写为URL类型！", {icon: 2, time: 2000});
                    return false;
                }
                newParams.aData = interfa;
            } else {
                winui.window.msg("状态值错误。", {icon: 2, time: 2000});
                return false;
            }
        } else if (newParams.linkedData == 2) {
            newParams.associatedDataTypes = "";
            newParams.aData = "";
        }
        contentList = contentList.map(t => {
            return t.id === contentId ? newParams : t;
        });
        sortDataIn();
        $(".mask-req-str").remove();
    }

    // 树据模板类型
    var displayTemplateList = [];
    function initDisplayTemplate() {
        showGrid({
            id: "displayTemplateId",
            url: reqBasePath + "dsformdisplaytemplate006",
            params: {},
            pagination: false,
            method: 'GET',
            template: getFileContent('tpl/template/select-option.tpl'),
            ajaxSendLoadBefore: function(hdb) {},
            ajaxSendAfter:function (json) {
                form.render('select');
                displayTemplateList = json.rows;
            }
        });
    }
    form.on('select(displayTemplateId)', function(data) {
        var displayTemplateValue = $('#displayTemplateId').val();
        if (displayTemplateValue.length == 0){
            $("#templateContent").html("");
        } else {
            $.each(displayTemplateList, function(i, item) {
                if (displayTemplateValue == item.id) {
                    var str = '<textarea class="layui-textarea" readonly>' + item.content + '</textarea>';
                    $("#templateContent").html(str);
                    // 取出数据模板中用{{}}包裹的词
                    tplContentVal = strMatchAllByTwo(item.content, '{{','}}');
                    removeByValue(tplContentVal, "#each this");
                    removeByValue(tplContentVal, "/each");
                    return false;
                }
            });
        }
    });

    //
    // if ($("#linkedData").val() == 'true') {
    //     params.linkedData = '1';
    //     params.dataType = $("#dataType").val();
    //     if (isNull(params.dataType)) {
    //         winui.window.msg('请选择数据来源', {icon: 2, time: 2000});
    //         return false;
    //     }
    //     params.displayTemplateId = $("#displayTemplateId").val();
    //
    //     if (isNull(params.displayTemplateId)) {
    //         winui.window.msg('请选择数据展示模板', {icon: 2, time: 2000});
    //         return false;
    //     }
    //     var defaultDataStr = $("#defaultData").val();//默认数据值
    //     if (defaultDataStr.length != 0) {
    //         if (isJSON(defaultDataStr)) {
    //             var defaultKey = getOutKey(defaultDataStr);//从默认数据中取出json串的键
    //             if (subset(tplContentVal, defaultKey)) {
    //                 params.defaultData = defaultDataStr;
    //             } else {
    //                 winui.window.msg('默认数据内容有误，请重新填写!', {icon: 2, time: 2000});
    //                 return false;
    //             }
    //         } else {
    //             winui.window.msg('默认数据格式不正确，请重新填写!', {icon: 2, time: 2000});
    //             return false;
    //         }
    //     } else {
    //         winui.window.msg('请填写默认数据', {icon: 2, time: 2000});
    //         return false;
    //     }
    // }

});