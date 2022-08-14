// 动态表单工具函数
var dsFormUtil = {

    dsFormChooseList: [], // 动态表单选择页面类型为【多选】时返回的参数
    dsFormChooseMation: {}, // 动态表单选择页面类型为【单选】时返回的参数
    chooseType: true, // 动态表单选择页面类型，true：多选；false：单选

    dsFormDataKey: "initData",
    dsFormBtnTemplate: '<button type="button" class="layui-btn layui-btn-primary layui-btn-xs" id="{{btnId}}">表单选择</button>',
    customDsFormBox: '<div class="layui-form-item layui-col-xs12"><span class="hr-title">{{pageName}}</span><hr></div><div id="{{id}}" class="ds-form-page layui-col-xs12"></div>',
    // 必须包含的包
    mastHaveImport: ['layedit', 'colorpicker', 'slider', 'fileUpload', 'codemirror', 'xml', 'clike', 'css', 'htmlmixed', 'javascript', 'nginx', 'solr', 'sql', 'vue',
        'matchbrackets', 'closebrackets', 'showHint', 'anywordHint', 'lint', 'jsonLint', 'foldcode', 'foldgutter', 'braceFold', 'commentFold', 'form'],
    showType: {
        '1': '{{#bean}}<div class="layui-form-item {{defaultWidth}}"><label class="layui-form-label">{{labelContent}}：</label><div class="layui-input-block ver-center">{{text}}</div></div>{{/bean}}', // 文本展示
        '2': '{{#bean}}<div class="layui-form-item {{defaultWidth}}"><label class="layui-form-label">{{labelContent}}：</label><div class="layui-input-block ver-center">{{#each text}}<a rowid="{{id}}" class="enclosureItem" rowpath="{{fileAddress}}" href="javascript:;" style="color:blue;">{{name}}</a><br>{{/each}}</div></div>{{/bean}}', // 附件展示
        '3': '{{#bean}}<div class="layui-form-item {{defaultWidth}}"><label class="layui-form-label">{{labelContent}}：</label><div class="layui-input-block ver-center">{{{text}}}</div></div>{{/bean}}', // 富文本展示
        '4': '{{#bean}}<div class="layui-form-item {{defaultWidth}}"><label class="layui-form-label">{{labelContent}}：</label><div class="layui-input-block ver-center">{{#each photo}}<img src="{{photoValue}}" class="photo-img">{{/each}}</div></div>{{/bean}}', // 图片展示
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
            callBack: function(refreshCode) {
                if(typeof(callback) == "function") {
                    callback(dsFormUtil.dsFormChooseList);
                }
            }});
    },

    /**
     * 初始化表单选择按钮信息
     *
     * @param id dom对象的id
     */
    initDsFormChooseBtn: function(id, initData){
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
    initData: function(id, btnId){
        var btnHtml = getDataUseHandlebars(dsFormUtil.dsFormBtnTemplate, {btnId: btnId});
        var str = "";
        var dsFormChooseList = JSON.parse($("#" + id).attr(dsFormUtil.dsFormDataKey));
        $.each(dsFormChooseList, function(i, item){
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
    getJSONDsFormListByBoxId: function (id){
        return [].concat(JSON.parse($("#" + id).attr(dsFormUtil.dsFormDataKey)));
    },

    /**
     * 根据父id加载动态表单页面分类信息
     *
     * @param domId 要加载的dom对象的id
     * @param parentId 父id
     */
    loadDsFormPageTypeByPId: function (domId, parentId){
        showGrid({
            id: domId,
            url: flowableBasePath + "dsformpagetype006",
            params: {parentId: parentId, language: languageType},
            pagination: false,
            method: "GET",
            template: getFileContent('tpl/template/select-option.tpl'),
            ajaxSendLoadBefore: function(hdb){
            },
            ajaxSendAfter: function (json) {
            }
        });
    },

    /**
     * 加载动态表单到页面
     *
     * @param showBoxId要追加的boxid后面
     * @param code 动态表单-----业务逻辑表单关联表中的code
     */
    loadPageByCode: function(showBoxId, code, dsFormObjectRelationId){
        var params = {
            dsFormObjectRelationCode: isNull(code) ? "" : code,
            dsFormObjectRelationId: isNull(dsFormObjectRelationId) ? "" : dsFormObjectRelationId
        };
        AjaxPostUtil.request({url: flowableBasePath + "dsFormObjectRelation006", params: params, method: "GET", type: 'json', callback: function(json) {
            dsFormUtil.loadDsFormItem(showBoxId, json);
        }, async: false});
    },

    loadDsFormItem: function(showBoxId, json){
        $.each(json.rows, function(j, bean){
            var customBoxId = bean.id;
            $("#" + showBoxId).append(getDataUseHandlebars(dsFormUtil.customDsFormBox, bean));
            dsFormUtil.loadDsFormItemToEdit(customBoxId, bean.content);
        });
        form.render();
    },

    loadDsFormItemToEdit: function (customBoxId, rows){
        $.each(rows, function(i, item){
            if(item.associatedDataTypes == 1){//json串
                var obj = item.aData;
                if(typeof item.aData == 'string'){
                    obj = JSON.parse(item.aData);
                }
                item.context = getDataUseHandlebars(item.templateContent, obj);
            }else if(item.associatedDataTypes == 2){//接口
                AjaxPostUtil.request({url: flowableBasePath + "dsformpage011", params:{interfa: item.aData}, type: 'json', callback: function(j){
                    var obj = JSON.parse(j.bean.aData);
                    item.context = getDataUseHandlebars(item.templateContent, obj);
                }, async: false});
            }
            var jsonStr = {bean: item};
            var html = getDataUseHandlebars('{{#bean}}' + item.htmlContent + '{{/bean}}', jsonStr);
            var html_js = getDataUseHandlebars('{{#bean}}' + item.jsContent + '{{/bean}}', jsonStr);
            var jsCon = '<script>layui.define(["jquery"], function(exports) {var jQuery = layui.jquery;(function($) {' + html_js + '})(jQuery);});</script>';
            $("#" + customBoxId).append(html + jsCon);

            $("#" + item.id).val(item.value); //给能通过id赋值的控件赋值
            var _this = $("#" + customBoxId + " .layui-form-item").eq(i);//当前控件
            if(!isNull(item.require) && item.require.indexOf("required") >= 0){
                _this.find(".layui-form-label").append('<i class="red">*</i>');
            }
            _this.attr("controId", item.id);
            var vid = _this.attr("controlType");//控件类型
            if(vid === 'color'){//类型为颜色选择器
                _this.find("input").val(item.value);
                _this.find('div[id="' + item.id + '"]').find("span .layui-colorpicker-trigger-span").attr("style", "background:" + item.value);
            }else if(vid === 'switchedradio'){//类型为开关式单选框
                _this.find("input").val(item.value);
                if(item.value === 'true' || item.value == true){
                    _this.find("input").prop("checked", true);
                }
                _this.find("input").attr('id', item.id);
                _this.find("input").attr('name', item.id);
                _this.find("input").attr('lay-filter', item.id);
            }else if(vid === 'radio'){//类型为单选框
                _this.find("input:radio").attr("name", item.id);
                if(!isNull(item.value))
                    _this.find("input:radio[value=" + item.value + "]").attr("checked", true);
            }else if(vid === 'richtextarea'){//类型为富文本框
                _this.find('iframe[textarea="' + item.id + '"]').contents().find("body").html(item.value);
            }else if(vid === 'checkbox'){//类型为多选框
                var checkArray = item.value.split(",");
                var checkBoxAll = _this.find("input:checkbox");
                checkBoxAll.attr("name", item.id);
                for(var k = 0; k < checkArray.length; k++){
                    $.each(checkBoxAll, function(j, item){
                        if(checkArray[k] == $(this).val()){
                            $(this).prop("checked", true);
                        }
                    });
                }
            }
        });
    },

    /**
     * 获取保存的数据
     */
    savePageData: function (showBoxId, objectId){
        var result = {};
        $.each($("#" + showBoxId + " .ds-form-page"), function (i, item){
            var _item = $(item);
            result[_item.attr("id")] = dsFormUtil.getPageData(_item);
        });
        var params = {
            dataJson: JSON.stringify(result),
            objectId: objectId
        }
        AjaxPostUtil.request({url: flowableBasePath + "dsformpage014", params: params, type: 'json', method: "POST", callback: function(json) {
        }, async: false});
    },

    getPageData: function (_item){
        var list = new Array();
        for(var i = 0; i < _item.find(".layui-form-item").length; i++){
            var _this = _item.find(".layui-form-item").eq(i);
            var vid = _this.attr("controlType"),//控件类型
                showType = "", text = "", value = "";
            if(isNull(vid)){
                continue;
            }
            if(vid === 'input'){//类型为输入框
                text = _this.find("input").val();
                value = _this.find("input").val();
                showType = "1";
            }else if(vid === 'textarea'){//类型为文本框
                text = _this.find("textarea").val();
                value = _this.find("textarea").val();
                showType = "1";
            }else if(vid === 'select'){//类型为下拉框
                text = _this.find("select").find("option:selected").text();
                value = _this.find("select").val();
                showType = "1";
            }else if(vid === 'checkbox'){//类型为多选框
                var checkName = _this.find("input:first").attr("name");
                var texts = [], values = [];
                var arr = _this.find("input:checkbox[name='" + checkName + "']:checked");
                $.each(arr, function(i, item){
                    texts[i] = $(this).attr("title");
                    values[i] = $(this).attr("value");
                });
                text = texts.join(",");
                value = values.join(",");
                showType = "1";
            }else if(vid === 'radio'){//类型为单选框
                text = _this.find("input:radio:checked").attr("title");
                value = _this.find("input:radio:checked").val();
                showType = "1";
            }else if(vid === 'upload'){//类型为图片上传
                var uploadId = _this.find(".upload").attr("id");
                text = $("#" + uploadId).find("input[type='hidden'][name='upload']").attr("oldurl");
                value = $("#" + uploadId).find("input[type='hidden'][name='upload']").attr("oldurl");
                if(isNull(text))
                    text = "";
                if(isNull(value))
                    value = "";
                showType = "4";
            }else if(vid === 'color'){//类型为颜色选择器
                text = _this.find("input").val();
                value = _this.find("input").val();
                showType = "1";
            }else if(vid === 'range'){//类型为滑块
                text = _this.find(".layui-slider-tips").html();
                value = _this.find(".layui-slider-tips").html();
                showType = "1";
            }else if(vid === 'richtextarea'){//类型为富文本框
                var textareaId = _this.find("textarea").attr("id");
                var content = encodeURIComponent(_this.find('iframe[textarea="' + textareaId + '"]').contents().find("body").html());
                text = content;
                value = content;
                showType = "3";
            }else if(vid === 'switchedradio'){//类型为开关式单选框
                value = _this.find("input").val();
                var layText = _this.find("input").attr('lay-text');
                if(value == "true"){
                    text = layText.split('|')[0];
                } else {
                    text = layText.split('|')[1];
                }
                showType = "1";
            }
            list.push({
                value: value,
                text: text,
                showType: showType,
                controlType: vid,
                rowId: _this.attr("controId")
            });
        }
        return list;
    },

    /**
     * 加载动态表单详情到页面
     *
     * @param showBoxId要追加的boxid后面
     * @param objectId 绑定的objectId
     */
    loadPageShowDetailsByObjectId: function(showBoxId, objectId){
        AjaxPostUtil.request({url: flowableBasePath + "dsformpage015", params: {objectId: objectId}, method: "GET", type: 'json', callback: function(json) {
            $.each(json.rows, function(j, bean) {
                var customBoxId = bean.id;
                $("#" + showBoxId).append(getDataUseHandlebars(dsFormUtil.customDsFormBox, bean));
                dsFormUtil.initSequenceDataDetails(customBoxId, bean.content);
            });
        }, async: false});
    },

    initSequenceDataDetails: function (customBoxId, rows){
        $.each(rows, function (i, item) {
            var jsonStr = {
                bean: item
            };
            if(item.showType == 1){//文本展示
            }else if(item.showType == 2){//附件展示
            }else if(item.showType == 3){//富文本展示
            }else if(item.showType == 4){//图片展示
                var photoValue = [];
                if(!isNull(jsonStr.bean.text)){
                    photoValue = item.text.split(",");
                }
                var rows = [];
                $.each(photoValue, function(j, row){
                    rows.push({photoValue: row});
                });
                jsonStr.bean.photo = rows;
            }
            var str = getDataUseHandlebars(dsFormUtil.showType[item.showType], jsonStr);
            $("#" + customBoxId).append(str);
        });
    },

    /**
     * 加载动态表单详情到编辑
     *
     * @param showBoxId要追加的boxid后面
     * @param objectId 绑定的objectId
     */
    loadPageToEditByObjectId: function(showBoxId, objectId) {
        AjaxPostUtil.request({url: flowableBasePath + "dsformpage015", params: {objectId: objectId}, method: "GET", type: 'json', callback: function (json) {
            dsFormUtil.loadDsFormItem(showBoxId, json);
        }, async: false});
    },

    /**
     * 业务逻辑与动态表单的关联关系类型，也可叫单据类型
     *
     * @param firstTypeCode 所属一级分类的code
     * @param callback 回调函数
     */
    dsFormObjectRelationChoose: {}, // 已经选择的单据类型
    openDsFormObjectRelationChooseByFirstTypeCodeChoosePage: function (firstTypeCode, callback){
        _openNewWindows({
            url: "../../tpl/dsFormObjectRelation/dsFormObjectRelationChooseByFirstTypeCode.html?firstTypeCode=" + firstTypeCode,
            title: "单据类型",
            pageId: "dsFormObjectRelationChooseByFirstTypeCodePage",
            area: ['480px', '500px'],
            callBack: function(refreshCode) {
                if(typeof(callback) == "function") {
                    callback(dsFormUtil.dsFormObjectRelationChoose);
                }
            }});
    },

};