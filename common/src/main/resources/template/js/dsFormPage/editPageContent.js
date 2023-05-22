
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
    var pageType = GetUrlParam("pageType");
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
                        <label class="layui-form-label">标题：</label>
                        <div class="layui-input-block">
                            <input type="text" id="title" name="title" placeholder="请输入标题" class="layui-input" />
                        </div>
                    </div>`,
        'placeholderBox': `<div class="layui-form-item layui-col-xs12">
                            <label class="layui-form-label">提示语：</label>
                            <div class="layui-input-block winui-radio">
                                <input type="text" id="placeholder" name="placeholder" placeholder="请输入控件提示语" class="layui-input" />
                            </div>
                        </div>`,
        'remarkBox': `<div class="layui-form-item layui-col-xs12">
                            <label class="layui-form-label">备注：</label>
                            <div class="layui-input-block winui-radio">
                                <input type="text" id="remark" name="remark" placeholder="请输入备注" class="layui-input" />
                            </div>
                        </div>`,
        'classNameBox': `<div class="layui-form-item layui-col-xs12">
                            <label class="layui-form-label">class属性：</label>
                            <div class="layui-input-block winui-radio">
                                <input type="text" id="className" name="className" placeholder="请输入class属性" class="layui-input" />
                            </div>
                        </div>`,
        'requireBox': `<div class="layui-form-item layui-col-xs12">
                            <label class="layui-form-label">限制条件</label>
                            <div class="layui-input-block" id="require">
                
                            </div>
                        </div>`,
        'defaultValueBox': `<div class="layui-form-item layui-col-xs12">
                                <label class="layui-form-label">默认值：</label>
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
        'preAttributeBox': `<div class="layui-form-item layui-col-xs12">
                            <label class="layui-form-label">前置属性</label>
                            <div class="layui-input-block">
                                <select lay-filter="preAttribute" lay-search="" id="preAttribute" name="preAttribute">
                
                                </select>
                            </div>
                        </div>`,
        'postAttributeBox': `<div class="layui-form-item layui-col-xs12">
                            <label class="layui-form-label">后置属性</label>
                            <div class="layui-input-block">
                                <select lay-filter="postAttribute" lay-search="" id="postAttribute" name="postAttribute">
                
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
        'isEditBox': `<div class="layui-form-item layui-col-xs12">
                            <label class="layui-form-label">是否可以编辑：</label>
                            <div class="layui-input-block" id="isEdit">
                                
                            </div>
                        </div>`,
        'dateTimeTypeBox': `<div class="layui-form-item layui-col-xs12">
                        <label class="layui-form-label">日期类型<i class="red">*</i></label>
                        <div class="layui-input-block" id="dateTimeType">
                        </div>
                    </div>`,
        'userSelBox': `<div class="layui-form-item layui-col-xs12">
                            <label class="layui-form-label">是否包含自己<i class="red">*</i></label>
                            <div class="layui-input-block">
                                <select lay-filter="chooseOrNotMy" lay-search="" id="chooseOrNotMy" name="chooseOrNotMy" win-verify="required">
                                    <option value="">请选择</option>
                                    <option value="1">包含</option>
                                    <option value="2">不包含</option>
                                </select>
                            </div>
                        </div>
                        <div class="layui-form-item layui-col-xs12">
                            <label class="layui-form-label">绑定邮箱<i class="red">*</i></label>
                            <div class="layui-input-block">
                                <select lay-filter="chooseOrNotEmail" lay-search="" id="chooseOrNotEmail" name="chooseOrNotEmail" win-verify="required">
                                    <option value="">请选择</option>
                                    <option value="1">必须</option>
                                    <option value="2">非必须</option>
                                </select>
                            </div>
                        </div>
                        <div class="layui-form-item layui-col-xs12">
                            <label class="layui-form-label">选择类型<i class="red">*</i></label>
                            <div class="layui-input-block">
                                <select lay-filter="checkType" lay-search="" id="checkType" name="checkType" win-verify="required">
                                    <option value="">请选择</option>
                                    <option value="1">多选</option>
                                    <option value="2">单选</option>
                                </select>
                            </div>
                        </div>`,
        'tableAttrBox': `<div class="layui-form-item layui-col-xs12">
                        <label class="layui-form-label">表格属性<i class="red">*</i></label>
                        <div class="layui-input-block" data="[]">
                            <button id="attrTransformTableListConfig" type="button" class="winui-toolbtn">属性配置</button>
                        </div>
                    </div>`,
        'minDataBox': `<div class="layui-form-item layui-col-xs12">
                        <label class="layui-form-label">最小数据行数：</label>
                        <div class="layui-input-block">
                            <input type="text" id="minData" name="minData" placeholder="请输入最小数据行数" class="layui-input" />
                        </div>
                    </div>`,
        'deleteRowCallbackBox': `<div class="layui-form-item layui-col-xs12">
                        <label class="layui-form-label" style="width: 70%;">删除行之后的回调函数：</label>
                        <div class="layui-input-block" script="">
                            <button id="deleteRowCallbackConfig" type="button" class="winui-toolbtn writeScript">编写脚本</button>
                        </div>
                    </div>`,
        'addRowCallbackBox': `<div class="layui-form-item layui-col-xs12">
                        <label class="layui-form-label" style="width: 70%;">新增行之后的回调函数：</label>
                        <div class="layui-input-block" script="">
                            <button id="addRowCallbackConfig" type="button" class="winui-toolbtn writeScript">编写脚本</button>
                        </div>
                    </div>`,
        'beforeScriptBox': `<div class="layui-form-item layui-col-xs12">
                        <label class="layui-form-label" style="width: 70%;">组件加载前执行的JS：</label>
                        <div class="layui-input-block" script="layui.define(['jquery'], function(exports) {\n\tvar form = layui.form;\n\tvar contentId = '{{id}}';\n\n});">
                            <button id="beforeScriptConfig" type="button" class="winui-toolbtn writeScript">编写脚本</button>
                        </div>
                    </div>`,
        'afterScriptBox': `<div class="layui-form-item layui-col-xs12">
                        <label class="layui-form-label" style="width: 70%;">组件加载完成后执行的JS：</label>
                        <div class="layui-input-block" script="layui.define(['jquery'], function(exports) {\n\tvar form = layui.form;\n\tvar contentId = '{{id}}';\n\n});">
                            <button id="afterScriptConfig" type="button" class="winui-toolbtn writeScript">编写脚本</button>
                        </div>
                    </div>`,
        'editEchoScriptBox': `<div class="layui-form-item layui-col-xs12">
                        <label class="layui-form-label" style="width: 70%; white-space: inherit;">数据编辑回显时执行的脚本，入参为value：</label>
                        <div class="layui-input-block" script="">
                            <button id="editEchoScriptConfig" type="button" class="winui-toolbtn writeScript">编写脚本</button>
                        </div>
                    </div>`,
        'dataEchoAfterScriptBox': `<div class="layui-form-item layui-col-xs12">
                        <label class="layui-form-label" style="width: 70%;">数据回显完之后执行的脚本：</label>
                        <div class="layui-input-block" script="">
                            <button id="dataEchoAfterScriptConfig" type="button" class="winui-toolbtn writeScript">编写脚本</button>
                        </div>
                    </div>`,
        'afterHtmlBox': `<div class="layui-form-item layui-col-xs12">
                        <label class="layui-form-label" style="width: 70%;">组件加载完成后执行的HTML：</label>
                        <div class="layui-input-block" script="">
                            <button id="afterHtmlConfig" type="button" class="winui-toolbtn writeScript">编写脚本</button>
                        </div>
                    </div>`,
        'dataChangeBox': `<div class="layui-form-item layui-col-xs12">
                        <label class="layui-form-label" style="width: 70%;">数据变化监听的JS：</label>
                        <div class="layui-input-block" script="// 入参为：data \n\n">
                            <button id="dataChangeConfig" type="button" class="winui-toolbtn writeScript">编写脚本</button>
                        </div>
                    </div>`,
    };
    // 详情类布局才展示的组件属性
    var detailsPageAttr = ['attrKeyBox', 'titleBox', 'widthBox', 'tableAttrBox'];

    // 加载组件关联的属性
    $.each(html, function (key, value) {
        if (pageType == 'details') {
            // 详情布局
            if (data.dsFormComponent.attrKeys.indexOf(key) >= 0 && detailsPageAttr.indexOf(key) >= 0) {
                $("#contentBox").append(value);
            }
        } else {
            // 新增和编辑布局
            if (data.dsFormComponent.attrKeys.indexOf(key) >= 0) {
                $("#contentBox").append(value);
            }
        }
    });

    $("#title").val(data.title);
    $("#placeholder").val(data.placeholder);
    $("#remark").val(data.remark);
    $("#className").val(data.className);
    $("#defaultValue").val(data.defaultValue);
    $("#uploadNum").val(data.uploadNum);

    $("#chooseOrNotMy").val(data.chooseOrNotMy);
    $("#chooseOrNotEmail").val(data.chooseOrNotEmail);
    $("#checkType").val(data.checkType);

    // 属性信息
    var attrs = [];
    var otherAttrs = [];
    $.each(parent.attrList, function (i, item) {
        // 获取已经绑定组件的属性并且适用于当前组件的属性
        if (!isNull(item.attrDefinitionCustom)) {
            var dsFormComponent = item.attrDefinitionCustom.dsFormComponent;
            if (!isNull(dsFormComponent)) {
                if (data.dsFormComponent.id == dsFormComponent.id) {
                    attrs.push({
                        id: item.attrKey,
                        name: item.attrDefinitionCustom.name
                    })
                } else {
                    otherAttrs.push({
                        id: item.attrKey,
                        name: item.attrDefinitionCustom.name
                    })
                }
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
        $("#attrTransformTableListConfig").parent().attr('data-json', JSON.stringify([]));
    });
    $("#preAttribute").html(getDataUseHandlebars(selOption, {rows: otherAttrs}));
    $("#preAttribute").val(data.preAttribute);
    $("#postAttribute").html(getDataUseHandlebars(selOption, {rows: otherAttrs}));
    $("#postAttribute").val(data.postAttribute);

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

    // 是否可以编辑
    skyeyeClassEnumUtil.showEnumDataListByClassName("whetherEnum", 'radio', "isEdit", data.isEdit, form);

    // 日期类型
    skyeyeClassEnumUtil.showEnumDataListByClassName("dateTimeType", 'select', "dateTimeType", data.dateTimeType, form);

    // 表格属性的属性配置
    var attrTransformTableList = isNull(data.attrTransformTableList) ? '[]' : JSON.stringify(data.attrTransformTableList);
    $("#attrTransformTableListConfig").parent().attr('data', attrTransformTableList);
    $("#minData").val(data.minData);
    $("#deleteRowCallbackConfig").parent().attr('script', data.deleteRowCallback);
    $("#addRowCallbackConfig").parent().attr('script', data.addRowCallback);
    if (!isNull(data.beforeScript)) {
        $("#beforeScriptConfig").parent().attr('script', data.beforeScript);
    }
    if (!isNull(data.afterScript)) {
        $("#afterScriptConfig").parent().attr('script', data.afterScript);
    }
    $("#editEchoScriptConfig").parent().attr('script', data.editEchoScript);
    $("#dataEchoAfterScriptConfig").parent().attr('script', data.dataEchoAfterScript);
    $("#afterHtmlConfig").parent().attr('script', data.afterHtml);
    $("#dataChangeConfig").parent().attr('script', data.dataChange);

    $("body").on("click", "#attrTransformTableListConfig", function() {
        parent.temData = $("#attrTransformTableListConfig").parent().attr('data');
        parent._openNewWindows({
            url: "../../tpl/dsFormPage/editPageContentIsTable.html?attrKey=" + $("#attrKey").val() + '&className=' + parent.className + '&pageType=' + pageType,
            title: '表格属性配置',
            pageId: "editPageContentIsTable",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                $("#attrTransformTableListConfig").parent().attr('data', parent.temData);
            }});
    });

    $("body").on("click", ".writeScript", function() {
        var id = $(this).attr("id");
        parent.scriptData = $(this).parent().attr('script');
        parent._openNewWindows({
            url: "../../tpl/dsFormPage/editPageContentIsScript.html",
            title: '编写脚本',
            pageId: "editPageContentIsScript",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                $(`#${id}`).parent().attr('script', parent.scriptData);
            }});
    });

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
        newParams.remark = $("#remark").val();
        newParams.className = $("#className").val();
        newParams.require = isNull($('#require').attr('value')) ? [] : JSON.parse($('#require').attr('value'));
        newParams.uploadDataType = isNull($('#uploadDataType').attr('value')) ? [] : JSON.parse($('#uploadDataType').attr('value'));
        newParams.uploadNum = $("#uploadNum").val();
        newParams.uploadType = $("#uploadType").val();
        newParams.dataShowType = $("#dataShowType").val();
        newParams.defaultValue = $("#defaultValue").val();
        newParams.width = $("#width").val();
        newParams.attrKey = $("#attrKey").val();
        newParams.preAttribute = $("#preAttribute").val();
        newParams.postAttribute = $("#postAttribute").val();
        newParams.teamObjectType = $("#teamObjectType").val();
        newParams.isEdit = dataShowType.getData("isEdit");
        newParams.dateTimeType = dataShowType.getData("dateTimeType");

        newParams.chooseOrNotMy = $("#chooseOrNotMy").val();
        newParams.chooseOrNotEmail = $("#chooseOrNotEmail").val();
        newParams.checkType = $("#checkType").val();

        newParams.attrTransformTableList = isNull($("#attrTransformTableListConfig").parent().attr('data')) ? []
            : JSON.parse($("#attrTransformTableListConfig").parent().attr('data'));
        newParams.minData = $("#minData").val();
        newParams.deleteRowCallback = $("#deleteRowCallbackConfig").parent().attr('script');
        newParams.addRowCallback = $("#addRowCallbackConfig").parent().attr('script');
        newParams.beforeScript = $("#beforeScriptConfig").parent().attr('script');
        newParams.afterScript = $("#afterScriptConfig").parent().attr('script');
        newParams.editEchoScript = $("#editEchoScriptConfig").parent().attr('script');
        newParams.dataEchoAfterScript = $("#dataEchoAfterScriptConfig").parent().attr('script');
        newParams.afterHtml = $("#afterHtmlConfig").parent().attr('script');
        newParams.dataChange = $("#dataChangeConfig").parent().attr('script');

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