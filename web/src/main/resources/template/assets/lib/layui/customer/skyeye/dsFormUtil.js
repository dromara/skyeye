
// 动态表单工具函数
var dsFormUtil = {

    dsFormChooseList: [], // 动态表单选择页面类型为【多选】时返回的参数
    dsFormChooseMation: {}, // 动态表单选择页面类型为【单选】时返回的参数
    chooseType: true, // 动态表单选择页面类型，true：多选；false：单选

    dsFormDataKey: "initData",
    dsFormBtnTemplate: '<button type="button" class="layui-btn layui-btn-primary layui-btn-xs" id="{{btnId}}">表单选择</button>',
    customDsFormBox: '<div class="layui-form-item layui-col-xs12"><span class="hr-title">{{dsFormPage.name}}</span><hr></div><div id="{{dsFormPage.id}}" class="ds-form-page layui-col-xs12"></div>',
    customWriteDsFormBox: '<div class="layui-form-item layui-col-xs12"><span class="hr-title">{{name}}</span><hr></div><div id="{{id}}" class="ds-form-page layui-col-xs12"></div>',
    // 必须包含的包
    mastHaveImport: ['laydate', 'layedit', 'colorpicker', 'slider', 'fileUpload', 'codemirror', 'xml', 'clike', 'css', 'htmlmixed', 'javascript', 'nginx', 'solr', 'sql', 'vue',
        'matchbrackets', 'closebrackets', 'showHint', 'anywordHint', 'lint', 'jsonLint', 'foldcode', 'foldgutter', 'braceFold', 'commentFold', 'form'],
    showType: {
        '1': '{{#bean}}<div class="layui-form-item {{proportion}}"><label class="layui-form-label">{{label}}：</label><div class="layui-input-block ver-center">{{displayValue}}</div></div>{{/bean}}', // 文本展示
        '2': '{{#bean}}<div class="layui-form-item {{proportion}}"><label class="layui-form-label">{{label}}：</label><div class="layui-input-block ver-center">{{#each displayValue.enclosureInfoList}}<a rowid="{{id}}" class="enclosureItem" rowpath="{{fileAddress}}" href="javascript:;" style="color:blue;">{{name}}</a><br>{{/each}}</div></div>{{/bean}}', // 附件展示
        '3': '{{#bean}}<div class="layui-form-item {{proportion}}"><label class="layui-form-label">{{label}}：</label><div class="layui-input-block ver-center">{{{displayValue}}}</div></div>{{/bean}}', // 富文本展示
        '4': '{{#bean}}<div class="layui-form-item {{proportion}}"><label class="layui-form-label">{{label}}：</label><div class="layui-input-block ver-center">{{#each photo}}<img src="{{photoValue}}" class="photo-img">{{/each}}</div></div>{{/bean}}', // 图片展示
        '5': '{{#bean}}<div class="layui-form-item {{proportion}}"><label class="layui-form-label">{{label}}：</label><div class="layui-input-block ver-center" id="showTable{{orderBy}}">' +
            '<table id="messageTable{{orderBy}}" lay-filter="messageTable{{orderBy}}"></table></div></div>{{/bean}}', // 表格展示
        '6': '{{#bean}}<div class="layui-form-item {{proportion}}"><label class="layui-form-label">{{label}}：</label><div class="layui-input-block ver-center" id="showVoucher{{orderBy}}"></div></div>{{/bean}}', // 凭证展示
    },

    /**
     * 动态表单选择页面
     *
     * @param callback 回调函数
     */
    openDsFormPageChoosePage: function (callback) {
        _openNewWindows({
            url: "../../tpl/dsFormPage/dsFormPageListChoose.html",
            title: "表单选择",
            pageId: "dsFormPageListChoose",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                if(typeof(callback) == "function") {
                    callback(dsFormUtil.dsFormChooseList);
                }
            }});
    },

    /**
     * 初始化表单选择按钮信息 todo 目前没有用到
     *
     * @param id dom对象的id
     */
    initDsFormChooseBtn: function(id, initData) {
        var btnId = id + 'Btn';
        $("#" + id).attr(dsFormUtil.dsFormDataKey, JSON.stringify(initData));
        dsFormUtil.initData(id, btnId);
        $("body").on("click", "#" + btnId, function (e) {
            dsFormUtil.dsFormChooseList = [].concat(JSON.parse($("#" + id).attr("initData")));
            dsFormUtil.openDsFormPageChoosePage(function (){
                $("#" + id).attr(dsFormUtil.dsFormDataKey, JSON.stringify(dsFormUtil.dsFormChooseList));
                dsFormUtil.initData(id, btnId);
            });
        });
    },

    /**
     * 加载数据
     *
     * @param id box的id
     * @param btnId 按钮id
     */
    initData: function(id, btnId) {
        var btnHtml = getDataUseHandlebars(dsFormUtil.dsFormBtnTemplate, {btnId: btnId});
        var str = "";
        var dsFormChooseList = JSON.parse($("#" + id).attr(dsFormUtil.dsFormDataKey));
        $.each(dsFormChooseList, function(i, item) {
            str += '<br><a rowid="' + item.id + '" class="dsFormItem" href="javascript:;" style="color:blue;">' + item.pageName + '</a>';
        });
        str = btnHtml + str;
        $("#" + id).html(str);
    },

    /**
     * 获取数据
     *
     * @param id box的id
     * @returns {*[]}
     */
    getJSONDsFormListByBoxId: function (id) {
        return [].concat(JSON.parse($("#" + id).attr(dsFormUtil.dsFormDataKey)));
    },

    /**
     * 加载动态表单(新增操作)
     *
     * @param showBoxId 要追加的boxid后面
     * @param code 动态表单-----业务逻辑表单关联表中的code
     */
    loadPageByCode: function(showBoxId, code, dsFormObjectRelationId) {
        var params = {
            dsFormObjectRelationCode: isNull(code) ? "" : code,
            dsFormObjectRelationId: isNull(dsFormObjectRelationId) ? "" : dsFormObjectRelationId
        };
        AjaxPostUtil.request({url: reqBasePath + "dsFormObjectRelation006", params: params, method: "GET", type: 'json', callback: function(json) {
            dsFormUtil.loadAddDsFormItem(showBoxId, json);
        }, async: false});
    },

    loadAddDsFormItem: function(showBoxId, json) {
        $.each(json.rows, function(j, bean) {
            var customBoxId = bean.id;
            $("#" + showBoxId).append(getDataUseHandlebars(dsFormUtil.customWriteDsFormBox, bean));
            dsFormUtil.loadDsFormItemToAdd(customBoxId, bean.content);
        });
        form.render();
    },

    loadDsFormItemToAdd: function (customBoxId, rows) {
        $.each(rows, function(i, item) {
            item.value = item.defaultValue;
            dsFormUtil.setValue(customBoxId, item, i);
        });
    },

    loadEditDsFormItem: function(showBoxId, json) {
        $.each(json.rows, function(j, bean) {
            var customBoxId = bean.pageId;
            $("#" + showBoxId).append(getDataUseHandlebars(dsFormUtil.customWriteDsFormBox, bean.dsFormPage));
            dsFormUtil.loadDsFormItemToEdit(customBoxId, bean.dsFormPageDataList);
        });
        form.render();
    },

    loadDsFormItemToEdit: function (customBoxId, rows) {
        $.each(rows, function(i, item) {
            var pageComponent = item.dsFormPageContent;
            pageComponent.value = item.value;
            dsFormUtil.setValue(customBoxId, pageComponent, i);
        });
    },

    /**
     * 加载组件信息
     *
     * @param boxId
     * @param content
     */
    loadComponent: function (boxId, content) {
        var component = content.dsFormComponent;
        if (component.linkedData == 1) {
            // 关联数据
            content = dsFormUtil.getContentLinkedData(content);
        }
        if (!isNull(content.attrDefinition)) {
            if (!isNull(content.attrDefinition.attrDefinitionCustom)) {
                content.title = content.attrDefinition.attrDefinitionCustom.name;
            } else {
                content.title = content.attrDefinition.name;
            }
        }

        var jsonStr = {bean: content};
        var html = getDataUseHandlebars('{{#bean}}' + component.htmlContent + '{{/bean}}', jsonStr);
        var html_js = getDataUseHandlebars('{{#bean}}' + component.jsContent + '{{/bean}}', jsonStr);
        var jsCon = '<script>layui.define(["jquery"], function(exports) {var jQuery = layui.jquery;(function($) {' + html_js + '})(jQuery);});</script>';
        $("#" + boxId).append(html + jsCon);
        return content;
    },

    getContentLinkedData: function (content) {
        var json = {};
        if (isNull(content.attrDefinition) || isNull(content.attrDefinition.attrDefinitionCustom)) {
            return content;
        }
        var customAttr = content.attrDefinition.attrDefinitionCustom;
        var dataType = customAttr.dataType;
        if (isNull(customAttr.objectId) && isNull(customAttr.defaultData)) {
            return content;
        }
        if (dataType == 1) {
            // 自定义
            var obj = isNull(customAttr.defaultData) ? [] : customAttr.defaultData;
            if(typeof obj == 'string'){
                obj = JSON.parse(obj);
            }
            json = obj;
        } else if (dataType == 2) {
            // 枚举
            json = skyeyeClassEnumUtil.getEnumDataListByClassName(customAttr.objectId).rows;
        } else if (dataType == 3) {
            // 数据字典
            sysDictDataUtil.queryDictDataListByDictTypeCode(customAttr.objectId, function (data) {
                json = data.rows;
            });
        }
        if (!isNull(content.dsFormComponent.htmlDataFrom)) {
            content.context = getDataUseHandlebars(content.dsFormComponent.htmlDataFrom, json);
        }
        return content;
    },

    setValue: function (customBoxId, item, i) {
        // 加载组件
        dsFormUtil.loadComponent(customBoxId, item);

        // 给能通过id赋值的控件赋值
        $("#" + item.id).val(item.value);
        var _this = $("#" + customBoxId + " .layui-form-item").eq(i);
        if (!isNull(item.require) && item.require.indexOf("required") >= 0){
            _this.find(".layui-form-label").append('<i class="red">*</i>');
        }
        _this.attr("controId", item.id);
        var vid = _this.attr("controlType");//控件类型
        if (vid === 'color') {//类型为颜色选择器
            _this.find("input").val(item.value);
            _this.find('div[id="' + item.id + '"]').find("span .layui-colorpicker-trigger-span").attr("style", "background:" + item.value);
        } else if (vid === 'switchedradio') {//类型为开关式单选框
            _this.find("input").val(item.value);
            if (item.value === 'true' || item.value == true) {
                _this.find("input").prop("checked", true);
            }
            _this.find("input").attr('id', item.id);
            _this.find("input").attr('name', item.id);
            _this.find("input").attr('lay-filter', item.id);
        } else if (vid === 'radio') {//类型为单选框
            _this.find("input:radio").attr("name", item.id);
            if (!isNull(item.value))
                _this.find("input:radio[value=" + item.value + "]").attr("checked", true);
        } else if (vid === 'richtextarea') {//类型为富文本框
            _this.find('iframe[textarea="' + item.id + '"]').contents().find("body").html(item.value);
        } else if (vid === 'checkbox') {//类型为多选框
            var checkArray = item.value.split(",");
            var checkBoxAll = _this.find("input:checkbox");
            checkBoxAll.attr("name", item.id);
            for (var k = 0; k < checkArray.length; k++) {
                $.each(checkBoxAll, function (j, item) {
                    if (checkArray[k] == $(this).val()) {
                        $(this).prop("checked", true);
                    }
                });
            }
        }
    },

    /**
     * 获取保存的数据
     */
    savePageData: function (showBoxId, objectId) {
        var result = [];
        $.each($("#" + showBoxId + " .ds-form-page"), function (i, item) {
            var _item = $(item);
            result.push({
                pageId: _item.attr("id"),
                dsFormPageDataList: dsFormUtil.getPageData(_item)
            });
        });
        var params = {
            dsFormPageSequenceList: JSON.stringify(result),
            objectId: objectId
        }
        AjaxPostUtil.request({url: flowableBasePath + "dsformpage014", params: params, type: 'json', method: "POST", callback: function(json) {
        }, async: false});
    },

    getPageData: function (_item) {
        var list = new Array();
        for(var i = 0; i < _item.find(".layui-form-item").length; i++){
            var _this = _item.find(".layui-form-item").eq(i);
            var vid = _this.attr("controlType"),//控件类型
                text = "", value = "";
            if (isNull(vid)) {
                continue;
            }
            if (vid === 'textarea') {//类型为文本框
                text = _this.find("textarea").val();
                value = _this.find("textarea").val();
            } else if (vid === 'select') {//类型为下拉框
                text = _this.find("select").find("option:selected").text();
                value = _this.find("select").val();
            } else if (vid === 'checkbox') {//类型为多选框
                var checkName = _this.find("input:first").attr("name");
                var texts = [], values = [];
                var arr = _this.find("input:checkbox[name='" + checkName + "']:checked");
                $.each(arr, function (i, item) {
                    texts[i] = $(this).attr("title");
                    values[i] = $(this).attr("value");
                });
                text = texts.join(",");
                value = values.join(",");
            } else if (vid === 'radio') {//类型为单选框
                text = _this.find("input:radio:checked").attr("title");
                value = _this.find("input:radio:checked").val();
            } else if (vid === 'upload') {//类型为图片上传
                var uploadId = _this.find(".upload").attr("id");
                text = $("#" + uploadId).find("input[type='hidden'][name='upload']").attr("oldurl");
                value = $("#" + uploadId).find("input[type='hidden'][name='upload']").attr("oldurl");
                if (isNull(text))
                    text = "";
                if (isNull(value))
                    value = "";
            } else if (vid === 'range') {//类型为滑块
                text = _this.find(".layui-slider-tips").html();
                value = _this.find(".layui-slider-tips").html();
            } else if (vid === 'richtextarea') {//类型为富文本框
                var textareaId = _this.find("textarea").attr("id");
                var content = encodeURIComponent(_this.find('iframe[textarea="' + textareaId + '"]').contents().find("body").html());
                text = content;
                value = content;
            } else if (vid === 'switchedradio') {//类型为开关式单选框
                value = _this.find("input").val();
                var layText = _this.find("input").attr('lay-text');
                if (value == "true") {
                    text = layText.split('|')[0];
                } else {
                    text = layText.split('|')[1];
                }
            } else {
                text = _this.find("input").val();
                value = _this.find("input").val();
            }
            list.push({
                value: value,
                displayValue: text,
                contentId: _this.attr("controId")
            });
        }
        return list;
    },

    /**
     * 加载动态表单详情
     *
     * @param showBoxId要追加的boxid后面
     * @param objectId 绑定的objectId
     */
    loadPageShowDetailsByObjectId: function(showBoxId, objectId) {
        AjaxPostUtil.request({url: flowableBasePath + "dsformpage015", params: {objectId: objectId}, method: "GET", type: 'json', callback: function(json) {
            $.each(json.rows, function(j, bean) {
                var customBoxId = bean.dsFormPage.id;
                $("#" + showBoxId).append(getDataUseHandlebars(dsFormUtil.customDsFormBox, bean));
                dsFormUtil.initSequencePageDataDetails(customBoxId, bean.dsFormPageDataList);
            });
        }, async: false});
    },

    /**
     * 表单布局的详情展示
     *
     * @param customBoxId
     * @param rows
     */
    initSequencePageDataDetails: function (customBoxId, rows) {
        $.each(rows, function (i, item) {
            item.label = item.dsFormPageContent.title;
            item.proportion = item.dsFormPageContent.width;
            var jsonStr = {
                bean: item
            };
            var showType = item.dsFormPageContent.dsFormComponent.showType;
            if (showType == 4) { // 图片展示
                var photoValue = [];
                if (!isNull(jsonStr.bean.displayValue)) {
                    photoValue = item.displayValue.split(",");
                }
                var rows = [];
                $.each(photoValue, function(j, row){
                    rows.push({photoValue: row});
                });
                jsonStr.bean.photo = rows;
            }

            // 加载html
            var str = getDataUseHandlebars(dsFormUtil.showType[showType], jsonStr);
            $("#" + customBoxId).append(str);

            if (showType == 5) { // 表格展示
                var table = layui.table;
                table.render({
                    id: "messageTable" + item.orderBy,
                    elem: "#messageTable" + item.orderBy,
                    data: item.displayValue,
                    page: false,
                    cols: dsFormUtil.getTableHead(item.attrTransformTableList)
                });
            } else  if (showType == 6) { // 凭证展示
                var boxId = "showVoucher" + item.orderBy;
                // 初始化凭证
                voucherUtil.initDataDetails(boxId, item.value);
            }
        });
    },

    /**
     * 目前用于工作流详情展示
     *
     * @param customBoxId
     * @param rows
     */
    initSequenceDataDetails: function (customBoxId, rows) {
        $.each(rows, function (i, item) {
            var jsonStr = {
                bean: item
            };
            if (item.showType == 4) { // 图片展示
                var photoValue = [];
                if (!isNull(jsonStr.bean.displayValue)) {
                    photoValue = item.displayValue.split(",");
                }
                var rows = [];
                $.each(photoValue, function(j, row){
                    rows.push({photoValue: row});
                });
                jsonStr.bean.photo = rows;
            }

            // 加载html
            var str = getDataUseHandlebars(dsFormUtil.showType[item.showType], jsonStr);
            $("#" + customBoxId).append(str);

            if (item.showType == 5) { // 表格展示
                var table = layui.table;
                table.render({
                    id: "messageTable" + item.orderBy,
                    elem: "#messageTable" + item.orderBy,
                    data: item.displayValue,
                    page: false,
                    cols: dsFormUtil.getTableHead(item.attrTransformTableList)
                });
            } else  if (item.showType == 6) { // 凭证展示
                var boxId = "showVoucher" + item.orderBy;
                // 初始化凭证
                voucherUtil.initDataDetails(boxId, item.value);
            }
        });
    },

    getTableHead: function (attrTransformTableList) {
        var header = [];
        $.each(attrTransformTableList, function (i, item) {
            var field = {
                field: item.attrKey,
                title: item.label,
                align: item.align,
                width: item.width,
                templet: null
            };
            if (!isNull(item.templet)) {
                field['templet'] = eval('(' + item.templet + ')');
            }
            header.push(field);
        });
        return [header];
    },

    /**
     * 加载动态表单(编辑操作)
     *
     * @param showBoxId要追加的boxid后面
     * @param objectId 绑定的objectId
     */
    loadPageToEditByObjectId: function(showBoxId, objectId) {
        AjaxPostUtil.request({url: flowableBasePath + "dsformpage015", params: {objectId: objectId}, method: "GET", type: 'json', callback: function (json) {
            dsFormUtil.loadEditDsFormItem(showBoxId, json);
        }, async: false});
    },

};