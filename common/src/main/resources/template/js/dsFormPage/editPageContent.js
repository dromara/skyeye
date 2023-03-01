
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

    var html = {
        'attrKeyBox': `<div class="layui-form-item layui-col-xs12">
                            <label class="layui-form-label">关联属性<i class="red">*</i></label>
                            <div class="layui-input-block">
                                <select lay-filter="attrKey" lay-search="" id="attrKey" name="attrKey" win-verify="required">
                
                                </select>
                            </div>
                        </div>`,
        'titleBox': `<div class="layui-form-item layui-col-xs12">
                        <label class="layui-form-label">标题</label>
                        <div class="layui-input-block">
                            <input type="text" id="title" name="title" placeholder="请输入标题" class="layui-input" />
                        </div>
                    </div>`,
        'placeholderBox': `<div class="layui-form-item layui-col-xs12">
                            <label class="layui-form-label">提示语</label>
                            <div class="layui-input-block winui-radio">
                                <input type="text" id="placeholder" name="placeholder" placeholder="请输入控件提示语" class="layui-input" />
                            </div>
                        </div>`,
        'requireBox': `<div class="layui-form-item layui-col-xs12">
                            <label class="layui-form-label">限制条件</label>
                            <div class="layui-input-block" id="require">
                
                            </div>
                        </div>`,
        'defaultValueBox': `<div class="layui-form-item layui-col-xs12">
                                <label class="layui-form-label">默认值</label>
                                <div class="layui-input-block">
                                    <input type="text" id="defaultValue" name="defaultValue" placeholder="请输入默认值" class="layui-input" />
                                </div>
                            </div>`,
        'widthBox': `<div class="layui-form-item layui-col-xs12">
                        <label class="layui-form-label">宽度<i class="red">*</i></label>
                        <div class="layui-input-block">
                            <select lay-filter="width" lay-search="" id="width" name="width" win-verify="required">
            
                            </select>
                        </div>
                    </div>`,
        'uploadDataTypeBox': `<div class="layui-form-item layui-col-xs12">
                            <label class="layui-form-label">文件后缀类型<i class="red">*</i></label>
                            <div class="layui-input-block" id="uploadDataType">
                
                            </div>
                        </div>`,
        'uploadTypeBox': `<div class="layui-form-item layui-col-xs12">
                            <label class="layui-form-label">文件上传类型<i class="red">*</i></label>
                            <div class="layui-input-block">
                                <select lay-filter="uploadType" lay-search="" id="uploadType" name="uploadType" win-verify="required">
                
                                </select>
                            </div>
                        </div>`,
        'uploadNumBox': `<div class="layui-form-item layui-col-xs12">
                            <label class="layui-form-label">文件数量<i class="red">*</i></label>
                            <div class="layui-input-block">
                                <input type="text" id="uploadNum" name="uploadNum" placeholder="请输入文件数量" class="layui-input" win-verify="required" />
                            </div>
                        </div>`,
        'dataShowTypeBox': `<div class="layui-form-item layui-col-xs12">
                            <label class="layui-form-label">数据展示类型<i class="red">*</i></label>
                            <div class="layui-input-block">
                                <select lay-filter="dataShowType" lay-search="" id="dataShowType" name="dataShowType" win-verify="required">
                
                                </select>
                            </div>
                        </div>`,
        'teamObjectTypeBox': `<div class="layui-form-item layui-col-xs12">
                            <label class="layui-form-label">团队适用对象<i class="red">*</i></label>
                            <div class="layui-input-block">
                                <select lay-filter="teamObjectType" lay-search="" id="teamObjectType" name="teamObjectType" win-verify="required">
                
                                </select>
                            </div>
                        </div>`,
    };

    // 加载组件关联的属性
    $.each(html, function (key, value) {
        if (data.dsFormComponent.attrKeys.indexOf(key) >= 0) {
            $("#contentBox").append(value);
        }
    });

    $("#title").val(data.title);
    $("#placeholder").val(data.placeholder);
    $("#defaultValue").val(data.defaultValue);
    $("#uploadNum").val(data.uploadNum);

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

    // 属性的限制条件
    var require = isNull(data.require) ? '' : data.require.toString();
    skyeyeClassEnumUtil.showEnumDataListByClassName("verificationParams", 'verificationSelect', "require", require, form, null, 'formerRequirement');

    // 文件后缀类型
    var uploadDataType = isNull(data.uploadDataType) ? '' : data.uploadDataType.toString();
    dataShowType.showData({rows: fileTypeList}, 'verificationSelect', "uploadDataType", uploadDataType, form, null, null, null);

    // 文件上传类型
    dataShowType.showData({rows: dataTypeList}, 'select', "uploadType", data.uploadType, form, null, null, null);

    // 数据展示类型
    dataShowType.showData({rows: dataShowType.showDataType}, 'select', "dataShowType", data.dataShowType, form, null, null, null);

    // 团队适用对象
    skyeyeClassEnumUtil.showEnumDataListByClassName("teamObjectType", 'select', "teamObjectType", data.teamObjectType, form);

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
        newParams.require = isNull($('#require').attr('value')) ? [] : JSON.parse($('#require').attr('value'));
        newParams.uploadDataType = isNull($('#uploadDataType').attr('value')) ? [] : JSON.parse($('#uploadDataType').attr('value'));
        newParams.uploadNum = $("#uploadNum").val();
        newParams.uploadType = $("#uploadType").val();
        newParams.dataShowType = $("#dataShowType").val();
        newParams.defaultValue = $("#defaultValue").val();
        newParams.width = $("#width").val();
        newParams.attrKey = $("#attrKey").val();
        newParams.teamObjectType = $("#teamObjectType").val();

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