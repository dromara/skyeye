
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

    $("#showForm").html(getDataUseHandlebars($("#controlItemEdit").html(), {bean: data}));

    // 属性信息
    var attrs = [];
    $.each(parent.attrList, function (i, item) {
        // 获取已经绑定组件的属性并且适用于当前组件的属性
        if (!isNull(item.attrDefinitionCustom)) {
            var dsFormComponent = item.attrDefinitionCustom.dsFormComponent;
            if (!isNull(dsFormComponent) && data.dsFormComponent.id == dsFormComponent.id) {
                attrs.push({
                    id: item.attrKey,
                    name: item.attrDefinitionCustom.name
                })
            }
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

    $("#width").val(data.width);

    // 属性的限制条件
    skyeyeClassEnumUtil.showEnumDataListByClassName("verificationParams", 'verificationSelect', "require", data.require, form, null, 'formerRequirement');

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
        newParams.title = $("#title").val();
        newParams.placeholder = $("#placeholder").val();
        newParams.require = isNull($('#require').attr('value')) ? [] : $('#require').attr('value');
        newParams.defaultValue = $("#defaultValue").val();
        newParams.width = $("#width").val();
        newParams.attrKey = $("#attrKey").val();

        if (!$("#attrKeyBox").hasClass('layui-hide') && isNull($("#attrKey").val())) {
            winui.window.msg("请选择关联属性.", {icon: 2, time: 2000});
            return false;
        }
        if (!isNull($("#attrKey").val())) {
            newParams.attrDefinition = getInPoingArr(parent.attrList, 'attrKey', $("#attrKey").val());
        }

        parent.contentList = parent.contentList.map(t => {
            return t.id === data.id ? newParams : t;
        });
        parent.sortDataIn();
        $(".mask-req-str").remove();
    }

});