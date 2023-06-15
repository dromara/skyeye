
// 动态表单工具函数
var dsFormUtil = {

    // 必须包含的包
    mastHaveImport: ['laydate', 'layedit', 'colorpicker', 'slider', 'fileUpload', 'codemirror', 'xml', 'clike', 'css', 'htmlmixed', 'javascript', 'nginx', 'solr', 'sql', 'vue',
        'matchbrackets', 'closebrackets', 'showHint', 'anywordHint', 'lint', 'jsonLint', 'foldcode', 'foldgutter', 'braceFold', 'commentFold', 'form'],
    showType: {
        '1': `{{#bean}}
                <div class="layui-form-item {{width}}" controlType="{{dsFormComponent.numCode}}" contentId="{{id}}">
                    <label class="layui-form-label">{{title}}：</label>
                    <div class="layui-input-block ver-center">{{value}}</div>
                </div>
             {{/bean}}`, // 文本展示
        '2': `{{#bean}}
                <div class="layui-form-item {{width}}" controlType="{{dsFormComponent.numCode}}" contentId="{{id}}">
                    <label class="layui-form-label">{{title}}：</label>
                    <div class="layui-input-block ver-center">
                    {{#each value.enclosureInfoList}}<a rowid="{{id}}" class="enclosureItem" rowpath="{{fileAddress}}" href="javascript:;" style="color:blue;">{{name}}</a><br>{{/each}}
                    </div>
                </div>
             {{/bean}}`, // 附件展示
        '3': `{{#bean}}
                <div class="layui-form-item {{width}}" controlType="{{dsFormComponent.numCode}}" contentId="{{id}}">
                    <label class="layui-form-label">{{title}}：</label>
                    <div class="layui-input-block ver-center">{{{value}}}</div>
                </div>
             {{/bean}}`, // 富文本展示
        '4': `{{#bean}}
                <div class="layui-form-item {{width}}" controlType="{{dsFormComponent.numCode}}" contentId="{{id}}">
                    <label class="layui-form-label">{{title}}：</label>
                    <div class="layui-input-block ver-center">{{#each photo}}<img src="{{this}}" class="photo-img">{{/each}}</div>
                </div>
             {{/bean}}`, // 图片展示
        '5': `{{#bean}}
                <div class="layui-form-item {{width}}" controlType="{{dsFormComponent.numCode}}" contentId="{{id}}">
                    <label class="layui-form-label">{{title}}：</label>
                    <div class="layui-input-block ver-center" id="showTable{{orderBy}}">
                        <table id="messageTable{{orderBy}}" lay-filter="messageTable{{orderBy}}"></table>
                    </div>
                </div>
             {{/bean}}`, // 表格展示
        '6': `
            <link href="../../assets/lib/winui/css/customer/voucherUtil.css" rel="stylesheet" />
            <script src="../../assets/lib/layui/customer/voucher/spinbox.js"></script>
            <script src="../../assets/lib/layui/customer/voucher/voucherUtil.js"></script>
            {{#bean}}
                <div class="layui-form-item {{width}}" controlType="{{dsFormComponent.numCode}}" contentId="{{id}}">
                    <label class="layui-form-label">{{title}}：</label>
                    <div class="layui-input-block ver-center" id="showVoucher{{orderBy}}"></div>
                </div>
             {{/bean}}`, // 凭证展示
        '7': ``, // 脚本展示
        '8': `{{#bean}}
                <div class="layui-form-item {{width}}" controlType="{{dsFormComponent.numCode}}" contentId="{{id}}">
                    <label class="layui-form-label">{{title}}：</label>
                    <div class="layui-input-block ver-center">{{#each value}}{{name}}&nbsp;&nbsp;&nbsp;&nbsp;{{/each}}</div>
                </div>
             {{/bean}}`, // Id转Mation取值转换后的展示
        '8-1': `{{#bean}}
                <div class="layui-form-item {{width}}" controlType="{{dsFormComponent.numCode}}" contentId="{{id}}">
                    <label class="layui-form-label">{{title}}：</label>
                    <div class="layui-input-block ver-center">{{#each value}}<span class="layui-badge layui-bg-blue skyeye-badge">{{name}}</span>{{/each}}</div>
                </div>
             {{/bean}}`, // Id转Mation取值转换后的展示(用户组件)
    },

    pageMation: {},

    getBusinessData: function (businessId, serviceClassName, callback) {
        if (isNull(businessId)) {
            winui.window.msg("业务数据id为空", {icon: 2, time: 2000});
            return false;
        }
        var params = {
            objectId: businessId,
            objectKey: serviceClassName
        };
        AjaxPostUtil.request({url: reqBasePath + "queryBusinessDataByObject", params: params, type: 'json', method: 'POST', callback: function (json) {
            if(typeof(callback) == "function") {
                callback(json.bean);
            }
        }});
    },

    /**
     * 动态表单选择页面
     *
     * @param callback 回调函数
     */
    dsFormChooseMation: {}, // 动态表单选择页面类型为【单选】时返回的参数
    openDsFormPageChoosePage: function (callback) {
        _openNewWindows({
            url: "../../tpl/dsFormPage/dsFormPageListChoose.html",
            title: "表单选择",
            pageId: "dsFormPageListChoose",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                if(typeof(callback) == "function") {
                    callback(dsFormUtil.dsFormChooseMation);
                }
            }});
    },

    /**
     * 加载动态表单(编辑操作)
     *
     * @param showBoxId
     * @param pageMation
     * @param data
     */
    initEditPage: function(showBoxId, pageMation, data) {
        dsFormUtil.pageMation = pageMation;
        layui.define(["jquery", 'form'], function(exports) {
            var form = layui.form;
            $.each(dsFormUtil.pageMation.dsFormPageContents, function (i, dsFormContent) {
                var attrDefinition = dsFormContent.attrDefinition;
                dsFormContent["pageType"] = pageMation.type;
                if (!isNull(attrDefinition) && !$.isEmptyObject(attrDefinition)) {
                    var dsFormComponent = dsFormContent.dsFormComponent;
                    if (dsFormContent.isEdit == 0) {
                        // 不可以编辑
                        var value = data[attrDefinition.attrKey];
                        if (dsFormComponent.valueMergType == 'extend') {
                            value = data;
                        }
                        dsFormUtil.loadComponentValueDetails(showBoxId, dsFormContent, value, data);
                    } else {
                        // 可以编辑
                        dsFormUtil.loadComponent(showBoxId, dsFormContent);
                        var value = dsFormUtil.getValueDataFromBusinessForEdit(dsFormContent, data);
                        dsFormContent["value"] = value;
                        var jsFitValue = dsFormComponent.jsFitValue;
                        if (!isNull(jsFitValue) && jsFitValue.startsWith('layui.define')) {
                            var setValueScript = getDataUseHandlebars('{{#this}}' + dsFormComponent.jsFitValue + '{{/this}}', dsFormContent);
                            $(`#script${dsFormContent.id}`).remove();
                            $(`div[contentId="${dsFormContent.id}"]`).after(`<script id="script${dsFormContent.id}">${setValueScript}</script>`);
                        } else {
                            var setValueScript = getDataUseHandlebars('{{#this}}' + dsFormComponent.jsFitValue + '{{/this}}', dsFormContent);
                            eval(setValueScript);
                        }
                    }
                } else {
                    dsFormUtil.loadComponent(showBoxId, dsFormContent);
                }
            });
            matchingLanguage();
            form.render();

            dsFormUtil.initEvent(form);
        });
    },

    // 获取业务数据中实际的值
    getValueDataFromBusinessForEdit: function (content, data) {
        var attrDefinition = content.attrDefinition;
        var dsFormComponent = content.dsFormComponent;
        var value = data[attrDefinition.attrKey];
        if (dsFormComponent.valueMergType == 'extend') {
            value = data;
        }
        var showType = dsFormUtil.getShowType(content.attrDefinition);
        if (showType == 8 &&
            (dsFormComponent.numCode == 'userStaffChoose' || dsFormComponent.numCode == 'staffChoose')) {
            var key = attrDefinition.attrKey;
            // 例如：将key由relationUserId变为relationUserMation
            key = dsFormUtil.getKeyIdToMation(key);
            value = data[key];
            if (!isNull(value) && !$.isEmptyObject(value)) {
                // 判断值是否为对象，如果是对象，则组装成数组
                if (!$.isArray(value)) {
                    value = [].concat(value);
                }
            }
        }
        return value;
    },

    /**
     * 加载动态表单(详情操作)
     *
     * @param showBoxId
     * @param pageMation
     * @param data
     */
    initDetailsPage: function(showBoxId, pageMation, data) {
        dsFormUtil.pageMation = pageMation;
        $.each(dsFormUtil.pageMation.dsFormPageContents, function (i, content) {
            var attrDefinition = content.attrDefinition;
            if (!isNull(attrDefinition) && !$.isEmptyObject(attrDefinition)) {
                // 获取组件中设置值的脚本
                var attrDefinition = content.attrDefinition;
                var dsFormComponent = content.dsFormComponent;
                var value = data[attrDefinition.attrKey];
                if (dsFormComponent.valueMergType == 'extend') {
                    value = data;
                }
                dsFormUtil.loadComponentValueDetails(showBoxId, content, value, data);
            } else {
                dsFormUtil.loadComponentValueDetails(showBoxId, content, null, data);
            }
        });
        layui.form.render();
    },

    /**
     * 加载动态表单(新增操作)
     *
     * @param showBoxId 表单展示位置id
     * @param pageMation 页面信息
     */
    initCreatePage: function(showBoxId, pageMation) {
        dsFormUtil.pageMation = pageMation;
        layui.define(["jquery", 'form'], function(exports) {
            var form = layui.form;
            $.each(pageMation.dsFormPageContents, function(j, dsFormContent) {
                dsFormContent["pageType"] = pageMation.type;
                dsFormUtil.loadComponent(showBoxId, dsFormContent);
            });
            matchingLanguage();
            form.render();

            dsFormUtil.initEvent(form);
        });
    },

    initEvent: function (form) {
        $("body").on("click", "#cancle", function() {
            var index = parent.layer.getFrameIndex(window.name);
            parent.layer.close(index);
        });

        // 保存为草稿
        form.on('submit(formSaveDraft)', function (data) {
            if (winui.verifyForm(data.elem) && !isNull(dsFormUtil.pageMation)) {
                dsFormUtil.saveData("1", null);
            }
            return false;
        });

        // 保存/提交
        form.on('submit(formWriteBean)', function (data) {
            if (winui.verifyForm(data.elem) && !isNull(dsFormUtil.pageMation)) {
                var flowable = dsFormUtil.getFlowable(dsFormUtil.pageMation);
                if (flowable) {
                    activitiUtil.startProcess(dsFormUtil.getFlowableServiceClassName(dsFormUtil.pageMation), null, function (approvalId) {
                        dsFormUtil.saveData("2", approvalId);
                    });
                } else {
                    dsFormUtil.saveData(null, null);
                }
            }
            return false;
        });
    },

    // 判断是否开启工作流
    temPage: null,
    getFlowable: function (pageMation) {
        if (isNull(serviceClassName)) {
            var flowable = pageMation.serviceBeanCustom.serviceBean.flowable;
            // 判断业务对象是否开启了工作流
            if (flowable) {
                if ((isNull(pageMation.isFlowable) && pageMation.isFlowable + "" != "0") || pageMation.isFlowable == 1) {
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        }
        if (isNull(dsFormUtil.temPage) || $.isEmptyObject(dsFormUtil.temPage)) {
            AjaxPostUtil.request({url: reqBasePath + "queryServiceBeanCustom", params: {className: serviceClassName}, type: 'json', method: 'GET', callback: function (json) {
                dsFormUtil.temPage = json.bean;
            }, async: false});
        }
        return dsFormUtil.temPage.serviceBean.flowable;
    },

    getFlowableServiceClassName: function (pageMation) {
        if (isNull(serviceClassName)) {
            return pageMation.serviceBeanCustom.serviceBean.className;
        }
        return serviceClassName;
    },

    // 保存数据
    saveData: function (formSubType, approvalId) {
        var params = {};
        $.each(dsFormUtil.pageMation.dsFormPageContents, function (i, content) {
            if (!isNull(content.attrDefinition) && !$.isEmptyObject(content.attrDefinition)
                && (content.isEdit == 1 || (isNull(content.isEdit) && content.isEdit + '' != '0'))) {
                // 获取组件中获取值的脚本
                var dsFormComponent = content.dsFormComponent;
                var getValueScript = getDataUseHandlebars('{{#this}}' + dsFormComponent.jsValue + '{{/this}}', content);
                var value = "";
                eval('value = ' + getValueScript);
                if (dsFormComponent.valueMergType == 'extend') {
                    params = $.extend(true, params, value);
                } else {
                    params[content.attrDefinition.attrKey] = value;
                }
            }
        });
        if (dsFormUtil.pageMation.type == 'edit') {
            // 编辑布局
            params["id"] = GetUrlParam("id");
        }
        var teamAuth = dsFormUtil.pageMation.serviceBeanCustom.serviceBean.teamAuth;
        if (teamAuth) {
            // 开启团队权限
            params['objectId'] = objectId;
            params['objectKey'] = objectKey;
        }

        var flowable = dsFormUtil.getFlowable(dsFormUtil.pageMation);
        if (flowable) {
            params["formSubType"] = formSubType;
            params["approvalId"] = approvalId;
        }

        // 发送请求
        dsFormUtil.sendRequest({
            businessApi: dsFormUtil.pageMation.businessApi,
            params: params,
            loadTable: false,
            callback: function () {
                var index = parent.layer.getFrameIndex(window.name);
                parent.layer.close(index);
                parent.refreshCode = '0';
            }
        });
    },

    /**
     * 加载组件信息
     *
     * @param boxId
     * @param content
     */
    loadComponent: function (boxId, content) {
        if (!dsFormUtil.checkLoadHandlebar) {
            dsFormUtil.loadHandlebar();
        }
        var component = content.dsFormComponent;
        if (component.linkedData == 1) {
            // 关联数据
            content = dsFormUtil.getContentLinkedData(content);
        }
        content.title = dsFormUtil.getLable(content);
        if (isNull(content.attrDefinition)) {
            content.attrDefinition = {};
        }
        if (!isNull(content.dataChange)) {
            content.dataChange = getDataUseHandlebars('{{#bean}}' + content.dataChange + '{{/bean}}', {bean: content});
        }

        var jsonStr = {bean: content};
        var html = getDataUseHandlebars('{{#bean}}' + component.htmlContent + '{{/bean}}', jsonStr);
        var html_js = getDataUseHandlebars('{{#bean}}' + component.jsContent + '{{/bean}}', jsonStr);
        var jsCon = `<script id="script${content.id}">${html_js}</script>`;

        var beforeScript = getDataUseHandlebars('{{#bean}}' + content.beforeScript + '{{/bean}}', jsonStr);
        $("#" + boxId).append(`<script>${beforeScript}</script>`);
        $("#" + boxId).append(html + jsCon);
        $("#" + boxId).append(content.afterHtml);
        var afterScript = getDataUseHandlebars('{{#bean}}' + content.afterScript + '{{/bean}}', jsonStr);
        $("#" + boxId).append(`<script>${afterScript}</script>`);

        if (!isNull(content.require)) {
            dsFormUtil.setIsRequired(content);
        }
        return content;
    },

    // 设置必填标识
    setIsRequired: function (content) {
        if (content.require.indexOf('required') >= 0) {
            $(`div[contentId='${content.id}']`).find('label').append('<i class="red">*</i>');
        }
    },

    /**
     * 加载组件详情信息
     *
     * @param boxId
     * @param content
     */
    loadComponentValueDetails: function (boxId, content, value, data) {
        if (!dsFormUtil.checkLoadHandlebar) {
            dsFormUtil.loadHandlebar();
        }
        var component = content.dsFormComponent;
        if (component.linkedData == 1) {
            // 关联数据
            content = dsFormUtil.getContentLinkedData(content);
        }
        content.title = dsFormUtil.getLable(content);
        var showType = dsFormUtil.getShowType(content.attrDefinition);
        if (isNull(showType)) {
            var jsonStr = {bean: content};
            var html = getDataUseHandlebars('{{#bean}}' + component.htmlContent + '{{/bean}}', jsonStr);
            var html_js = getDataUseHandlebars('{{#bean}}' + component.jsContent + '{{/bean}}', jsonStr);
            var jsCon = `<script id="script${content.id}">${html_js}</script>`;
            $("#" + boxId).append(html + jsCon);
        } else if (showType == -1) {
            // 组件展示类型为【自定义】
            content.value = dsFormUtil.getContentLinkedDataValue(content, value, data);
            var jsonStr = {bean: content};
            var html = getDataUseHandlebars('{{#bean}}' + component.detailHtmlContent + '{{/bean}}', jsonStr);
            var html_js = getDataUseHandlebars('{{#bean}}' + component.detailJsContent + '{{/bean}}', jsonStr);
            var jsCon = `<script id="script${content.id}">${html_js}</script>`;
            $("#" + boxId).append(html + jsCon);
        } else {
            content.value = dsFormUtil.getContentLinkedDataValue(content, value, data);
            if (showType == 4) { // 图片展示
                var photoValue = [];
                if (!isNull(value)) {
                    photoValue = value.split(",");
                }
                content.photo = photoValue;
            }
            // 加载html
            var str = '';
            if (component.numCode == 'userStaffChoose' || component.numCode == 'staffChoose') {
                str = getDataUseHandlebars(dsFormUtil.showType['8-1'], {bean: content});
            } else {
                str = getDataUseHandlebars(dsFormUtil.showType[showType], {bean: content});
            }
            $("#" + boxId).append(str);

            if (showType == 5) { // 表格展示
                var result = dsFormUtil.resetTableValue(value, content.attrTransformTableList);
                dsFormTableUtil.intStaticTable("messageTable" + content.orderBy, result, content.attrTransformTableList);
            } else  if (showType == 6) { // 凭证展示
                var boxId = "showVoucher" + content.orderBy;
                // 初始化凭证
                voucherUtil.initDataDetails(boxId, value);
            }
        }

        return content;
    },

    resetTableValue: function (data, attrTransformTableList) {
        if (isNull(data)) {
            return [];
        }
        var result = [];
        $.each(data, function (i, itemVal) {
            var map = {};
            $.each(itemVal, function (key, val) {
                var attrTransformTable = getInPoingArr(attrTransformTableList, 'attrKey', key);
                if (!isNull(attrTransformTable) &&
                    (attrTransformTable.showType == 'chooseInput' || attrTransformTable.showType == 'select')) {
                    var transKey = dsFormUtil.getKeyIdToMation(key);
                    var itemData = itemVal[transKey];
                    if (!isNull(itemData)) {
                        map[key] = itemData.name;
                    } else {
                        map[key] = '';
                    }
                } else {
                    map[key] = val;
                }
            });
            result.push(map);
        });
        return result;
    },

    checkLoadHandlebar: false,
    loadHandlebar: function () {
        dsFormUtil.checkLoadHandlebar = true;
        // 加载json对象
        Handlebars.registerHelper('json', function(context, type) {
            if (!isNull(context)) {
                return JSON.stringify(context);
            }
            if (type == 'array') {
                return JSON.stringify([]);
            } else {
                return JSON.stringify({});
            }
        });
    },

    getLable: function (content) {
        var attr = content.attrDefinition;
        if (!isNull(attr) && !$.isEmptyObject(attr)) {
            if (!isNull(attr.attrDefinitionCustom)) {
                return attr.attrDefinitionCustom.name;
            } else {
                return attr.name;
            }
        }
        return content.title;
    },

    // 获取属性关联的数据值来源信息
    getContentLinkedData: function (content) {
        if (isNull(content.attrDefinition) || isNull(content.attrDefinition.attrDefinitionCustom)) {
            return content;
        }
        var customAttr = content.attrDefinition.attrDefinitionCustom;
        if (isNull(customAttr.objectId) && isNull(customAttr.defaultData) && isNull(customAttr.businessApi) && $.isEmptyObject(customAttr.businessApi)) {
            return content;
        }
        content.context = dsFormUtil.getHtmlContentByDataFrom(customAttr, content.dsFormComponent.htmlDataFrom);
        return content;
    },

    getHtmlContentByDataFrom: function (obj, htmlDataFrom) {
        var json = {};
        var dataType = obj.dataType;
        if (dataType == 1) {
            // 自定义
            var obj = isNull(obj.defaultData) ? [] : obj.defaultData;
            if (typeof obj == 'string') {
                obj = JSON.parse(obj);
            }
            json = obj;
        } else if (dataType == 2) {
            // 枚举
            json = skyeyeClassEnumUtil.getEnumDataListByClassName(obj.objectId).rows;
        } else if (dataType == 3) {
            // 数据字典
            sysDictDataUtil.queryDictDataListByDictTypeCode(obj.objectId, function (data) {
                json = data.rows;
            });
        } else if (dataType == 4) {
            // 自定义接口
            var businessApi = typeof obj.businessApi == 'string' ? JSON.parse(obj.businessApi) : obj.businessApi;
            var params = {};
            $.each(businessApi.params, function (key, value) {
                var realValue = "";
                eval('realValue = ' + value);
                params[key] = realValue;
            });
            var url = "";
            eval('url = ' + businessApi.serviceStr + ' + "' + businessApi.api + '"');
            AjaxPostUtil.request({url: url, params: params, type: 'json', method: businessApi.method, callback: function (data) {
                json = data.rows;
            }, async: false});
        }
        if (!isNull(htmlDataFrom)) {
            return getDataUseHandlebars(htmlDataFrom, json);
        }
        return '';
    },

    // 获取显示值
    getContentLinkedDataValue: function (content, value, data) {
        if (isNull(value) && value + '' != '0') {
            return null;
        }
        var attrDefinition = content.attrDefinition;
        if (isNull(attrDefinition) || isNull(attrDefinition.attrDefinitionCustom)) {
            return null;
        }
        var customAttr = attrDefinition.attrDefinitionCustom;

        if (!isNull(customAttr.objectId) || !isNull(customAttr.defaultData) || !$.isEmptyObject(customAttr.businessApi)) {
            var dataType = customAttr.dataType;
            if (dataType == 1) {
                // 自定义
                var obj = isNull(customAttr.defaultData) ? [] : customAttr.defaultData;
                if (typeof obj == 'string') {
                    obj = JSON.parse(obj);
                }
                return getInPoingArr(json, "id", value, "name");
            } else if (dataType == 2) {
                // 枚举
                return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey(customAttr.objectId, 'id', value, 'name');
            } else if (dataType == 3) {
                // 数据字典
                return sysDictDataUtil.getDictDataNameByCodeAndKey(customAttr.objectId, value);
            } else if (dataType == 4) {
                // 自定义接口
                var businessApi = customAttr.businessApi;
                var params = {};
                $.each(businessApi.params, function (key, value) {
                    var realValue = "";
                    eval('realValue = ' + value);
                    params[key] = realValue;
                });
                var url = "";
                var obj = [];
                eval('url = ' + businessApi.serviceStr + ' + "' + businessApi.api + '"');
                AjaxPostUtil.request({url: url, params: params, type: 'json', method: businessApi.method, callback: function (json) {
                    obj = json.rows;
                }, async: false});
                return getInPoingArr(obj, "id", value, "name");
            }
        }

        if (!isNull(customAttr.dsFormComponent)) {
            var showType = dsFormUtil.getShowType(attrDefinition);
            var numCode = customAttr.dsFormComponent.numCode;
            // 团队模板类型的组件，value值单独转换
            if (numCode == 'teamTemplate') {
                return teamObjectPermissionUtil.getTeamTemplate(value).name;
            }

            // Id转Mation取值转换
            if (showType == 8) {
                var key = attrDefinition.attrKey;
                // 例如：将key由relationUserId变为relationUserMation
                key = dsFormUtil.getKeyIdToMation(key);
                value = data[key];
                if (!isNull(value) && !$.isEmptyObject(value)) {
                    // 判断值是否为对象，如果是对象，则组装成数组
                    if (!$.isArray(value)) {
                        value = [].concat(value);
                    }
                }
            }
        }

        return value;
    },

    getKeyIdToMation: function (key) {
        return key.replace("Id", "") + "Mation";
    },

    // 获取属性的数据展示类型
    getShowType: function (attr) {
        if (!isNull(attr) && !isNull(attr.attrDefinitionCustom)) {
            if (!isNull(attr.attrDefinitionCustom.dsFormComponent)) {
                return attr.attrDefinitionCustom.dsFormComponent.showType;
            } else {
                return attr.attrDefinitionCustom.showType;
            }
        }
        return null;
    },

    // 执行事件
    executeEvent: function (operate, data) {
        if (operate.eventType == 'openPage') {
            // 打开新页面
            var operateOpenPage = operate.operateOpenPage;
            var url = operateOpenPage.type ? operateOpenPage.pageUrl : dsFormPageUrl + operateOpenPage.pageUrl;
            url = systemCommonUtil.getHasVersionUrl(url);
            // 构建参数
            if (!isNull(data)) {
                $.each(operateOpenPage.params, function (key, valueKey) {
                    var value = data[valueKey];
                    url += `&${key}=${value}`;
                });
            }
            var teamAuth = dsFormUtil.pageMation.serviceBeanCustom.serviceBean.teamAuth;
            if (teamAuth) {
                // 开启团队权限
                url += `&objectId=${objectId}`;
                url += `&objectKey=${objectKey}`;
            }
            _openNewWindows({
                url: url,
                title: operateOpenPage.name,
                pageId: 'page' + operate.id,
                area: ['90vw', '90vh'],
                callBack: function (refreshCode) {
                    winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                    dsFormTableUtil.loadTable();
                }});
        } else if (operate.eventType == 'ajax') {
            // 发送请求
            var businessApi = operate.businessApi;
            layer.confirm('确定执行该操作吗？', {icon: 3, title: '操作'}, function (index) {
                layer.close(index);
                // 构建参数
                var params = {};
                if (!isNull(data)) {
                    $.each(businessApi.params, function (key, valueKey) {
                        params[key] = data[valueKey]
                    });
                }
                dsFormUtil.sendRequest({
                    businessApi: businessApi,
                    params: params,
                    loadTable: true
                });
            });
        }
    },

    sendRequest: function (inputParams) {
        var businessApi = inputParams.businessApi;
        var params = inputParams.params;
        var url = "";
        eval('url = ' + businessApi.serviceStr + ' + "' + businessApi.api + '"');
        AjaxPostUtil.request({url: url, params: params, type: 'json', method: businessApi.method, callback: function (json) {
            winui.window.msg('操作成功', {icon: 1, time: 2000});
            if (inputParams.loadTable) {
                dsFormTableUtil.loadTable();
            }
            if (typeof (inputParams.callback) == "function") {
                inputParams.callback();
            }
        }});
    }

};

var dsFormTableUtil = {

    tableId: '',

    // 操作信息
    operateMap: {},

    // 初始化静态数据的表格
    intStaticTable: function (id, data, tableColumnList) {
        var table = layui.table;
        table.render({
            id: id,
            elem: `#${id}`,
            data: data,
            even: false,
            page: false,
            limit: 1000,
            cols: dsFormTableUtil.getTableHead({
                serialNumColumn: true
            }, tableColumnList)
        });
    },

    // 初始化动态表格
    initDynamicTable: function (id, pageMation) {
        dsFormUtil.pageMation = pageMation;
        var tableColumnList = pageMation.tableColumnList;
        $.each(tableColumnList, function (i, item) {
            item.label = dsFormUtil.getLable(item);
        });
        // 加载操作信息
        dsFormTableUtil.initOperate(pageMation);
        dsFormTableUtil.tableId = id;

        // 加载表格
        layui.define(["jquery", 'form', 'table', 'tableCheckBoxUtil'], function(exports) {
            var table = layui.table,
                tableCheckBoxUtil = layui.tableCheckBoxUtil;
            var form = layui.form;
            var api = pageMation.businessApi;

            if (pageMation.isDataAuth == 1) {
                // 开启数据权限
                loadAuthBtnGroup(`${id}`, pageMation.dataAuthPointNum);
            }

            if (pageMation.whetherChoose == 'checkbox') {
                // 开启了多选表格
                var chooseListMation = parent.chooseListMation;
                var ids = [];
                $.each(chooseListMation, function (i, item) {
                    ids.push(item.id);
                });
                tableCheckBoxUtil.init({
                    gridId: id,
                    filterId: id,
                    fieldName: 'id',
                    ids: ids
                });
                $("#toolBar").append(`<button class="winui-toolbtn search-table-btn-right" type="button" id="saveCheckBox"><i class="fa fa-save" aria-hidden="true"></i><language showName="com.skyeye.save"></language></button>`);
            }

            var url = "";
            eval('url = ' + api.serviceStr + ' + "' + api.api + '"');
            table.render({
                id: id,
                elem: `#${id}`,
                method: api.method,
                url: url,
                where: dsFormTableUtil.getTableParams(),
                toolbar: true,
                // 开启表格选择功能的只显示过滤字段功能
                defaultToolbar: pageMation.whetherChoose == 'close' ? ['filter', 'exports', 'print'] : ['filter'],
                even: true,
                page: pageMation.isPage == 1 ? true : false,
                overflow: {type: 'tips', header: true, total: true},
                limits: getLimits(),
                limit: getLimit(),
                cols: dsFormTableUtil.getTableHead({
                    serialNumColumn: true,
                    whetherChoose: pageMation.whetherChoose,
                    // 开启表格选择功能的不显示操作列
                    operateColumn: pageMation.whetherChoose == 'close' ? true : false
                }, tableColumnList),
                done: function(json) {
                    matchingLanguage();
                    initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, pageMation.searchTips, function () {
                        table.reloadData(id, {page: {curr: 1}, where: dsFormTableUtil.getTableParams()});
                    });
                    dsFormTableUtil.initChooseHtml(id, pageMation.whetherChoose, json, tableCheckBoxUtil, form);
                }
            });
            table.on(`tool(${id})`, function (obj) {
                var data = obj.data;
                var layEvent = obj.event;
                var operate = dsFormTableUtil.operateMap[layEvent];
                if (!isNull(operate)) {
                    dsFormUtil.executeEvent(operate, data);
                }
            });
            dsFormTableUtil.initEvent(table, form);
        });
    },

    initChooseHtml: function (id, whetherChoose, json, tableCheckBoxUtil, form) {
        if (whetherChoose == 'radio') {
            $(`#${id}`).next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('dblclick',function() {
                var dubClick = $(`#${id}`).next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
                dubClick.find("input[type='radio']").prop("checked", true);
                form.render();
                var chooseIndex = JSON.stringify(dubClick.data('index'));
                var obj = json.rows[chooseIndex];
                parent.chooseItemMation = obj;
                parent.refreshCode = '0';
                var index = parent.layer.getFrameIndex(window.name);
                parent.layer.close(index);
            });

            $(`#${id}`).next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('click',function() {
                var click = $(`#${id}`).next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
                click.find("input[type='radio']").prop("checked", true);
                form.render();
            });
        }

        if (whetherChoose == 'checkbox') {
            // 设置选中
            tableCheckBoxUtil.checkedDefault({
                gridId: id,
                fieldName: 'id'
            });

            // 保存
            $("body").on("click", "#saveCheckBox", function() {
                var selectedData = tableCheckBoxUtil.getValueList({
                    gridId: id
                });
                if (selectedData.length == 0) {
                    winui.window.msg("请选择数据", {icon: 2, time: 2000});
                    return false;
                }
                parent.chooseListMation = [].concat(selectedData);
                var index = parent.layer.getFrameIndex(window.name);
                parent.layer.close(index);
                parent.refreshCode = '0';
            });
        }
    },

    // 加载操作
    initOperate: function (pageMation) {
        var operateList = pageMation.operateList;
        if (isNull(operateList)) {
            return false;
        }
        $.each(operateList, function (i, item) {
            dsFormTableUtil.operateMap[item.id] = item;
            if (isNull(item.authPointNum) || (!isNull(item.authPointNum) && auth(item.authPointNum))) {
                // 权限校验
                if (item.position == 'toolBar') {
                    // 工具栏
                    $(`#${item.position}`).append(`<button id="${item.id}" class="winui-toolbtn search-table-btn-right item-click"><i class="fa fa-plus" aria-hidden="true"></i>${item.name}</button>`);
                } else if (item.position == 'actionBar') {
                    // 操作栏
                    $(`#${item.position}`).append(dsFormTableUtil.initActionBarOperate(item));
                } else if (item.position == 'rightMenuBar') {
                    // 右键菜单栏

                }
            }
        });
    },

    initActionBarOperate: function (item) {
        if (isNull(item.showConditionList) || item.showConditionList.length == 0) {
            // 没有设置按钮显示的条件
            return `<a class="layui-btn layui-btn-xs ${item.color}" lay-event="${item.id}">${item.name}</a>`;
        }
        var condition = [];
        $.each(item.showConditionList, function (i, bean) {
            condition.push(`d.${bean.attrKey} ${bean.symbolsMark} ${bean.value}`);
        });
        var conditionStr = condition.join('&&');
        // 没有设置按钮显示的条件
        return `{{# if (${conditionStr}) { }}<a class="layui-btn layui-btn-xs ${item.color}" lay-event="${item.id}">${item.name}</a>{{# } }}`;
    },

    getTableHead: function (column, tableColumnList) {
        var header = [];
        if (column.whetherChoose == 'radio' || column.whetherChoose == 'checkbox') {
            header.push({
                type: column.whetherChoose,
                fixed: 'left'
            });
        }
        if (!isNull(column.serialNumColumn) && column.serialNumColumn) {
            header.push({
                title: systemLanguage["com.skyeye.serialNumber"][languageType],
                type: 'numbers',
                fixed: 'left'
            });
        }
        if (isNull(tableColumnList)) {
            return [header];
        }
        $.each(tableColumnList, function (i, item) {
            var field = {
                field: item.attrKey,
                title: item.label || item.name,
                align: item.align,
                width: item.width,
                fixed: isNull(item.fixed) ? '' : item.fixed,
                hide: (!isNull(item.hide) && item.hide == 1) ? true : false,
                templet: null
            };
            var templet = dsFormTableUtil.getTemplateFun(item);
            if (!isNull(templet)) {
                field['templet'] = eval('(' + templet + ')');
            }
            header.push(field);
        });
        if (!isNull(column.operateColumn) && column.operateColumn) {
            header.push({
                title: systemLanguage["com.skyeye.operation"][languageType],
                fixed: 'right',
                width: dsFormTableUtil.calcOperateColumnWidth(),
                align: 'center',
                toolbar: '#actionBar'
            });
        }
        return [header];
    },

    getTemplateFun: function (item) {
        if (!isNull(item.templet)) {
            return item.templet;
        } else {
            if (isNull(item.attrDefinition) || isNull(item.attrDefinition.attrDefinitionCustom)) {
                return null;
            }
            var customAttr = item.attrDefinition.attrDefinitionCustom;
            var dataType = customAttr.dataType;
            if (isNull(customAttr.objectId) && isNull(customAttr.defaultData) && isNull(customAttr.businessApi) && $.isEmptyObject(customAttr.businessApi)) {
                return null;
            }
            if (dataType == 1) {
                // 自定义
                var obj = isNull(customAttr.defaultData) ? [] : customAttr.defaultData;
                if (typeof obj == 'string') {
                    obj = JSON.parse(obj);
                }
                return `function (d) {
                    var json = ${obj};
                    return getInPoingArr(json, "id", d.${item.attrKey}, "name");
                }`;
            } else if (dataType == 2) {
                // 枚举
                return `function (d) {return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey('${customAttr.objectId}', 'id', d.${item.attrKey}, 'name');}`;
            } else if (dataType == 3) {
                // 数据字典
                return `function (d) {return sysDictDataUtil.getDictDataNameByCodeAndKey('${customAttr.objectId}', d.${item.attrKey});}`;
            } else if (dataType == 4) {
                // 自定义接口
                var businessApi = customAttr.businessApi;
                var params = {};
                $.each(businessApi.params, function (key, value) {
                    var realValue = "";
                    eval('realValue = ' + value);
                    params[key] = realValue;
                });
                var url = "";
                var obj = [];
                eval('url = ' + businessApi.serviceStr + ' + "' + businessApi.api + '"');
                AjaxPostUtil.request({url: url, params: params, type: 'json', method: businessApi.method, callback: function (json) {
                    obj = json.rows;
                }, async: false});
                obj = JSON.stringify(obj);
                return `function (d) {
                    var json = JSON.parse('${obj}');
                    return getInPoingArr(json, "id", d.${item.attrKey}, "name");
                }`;
            }
        }
        return null;
    },

    initEvent: function (table, form) {
        form.render();
        $("body").on("click", "#reloadTable", function() {
            dsFormTableUtil.loadTable();
        });

        $("body").on("click", ".item-click", function() {
            var id = $(this).attr('id');
            var operate = dsFormTableUtil.operateMap[id];
            if (!isNull(operate)) {
                dsFormUtil.executeEvent(operate, null);
            }
        });

    },

    loadTable: function () {
        layui.table.reloadData(dsFormTableUtil.tableId, {where: dsFormTableUtil.getTableParams()});
    },

    getTableParams: function() {
        return $.extend(true, {}, initTableSearchUtil.getSearchValue(dsFormTableUtil.tableId));
    },

    // 计算操作列的宽度
    calcOperateColumnWidth: function () {
        var _a = $('#actionBar').find('a');
        var width = 100;
        // 获取操作列中有多少个按钮，公式：文字的数量 * 14(一个文字的宽度) + 按钮的数量 * 10(按钮的内边距) + (按钮的数量 - 1) * 10(按钮外边距) + 16(操作列的内边距)
        if (!isNull(_a) && _a.length > 0) {
            var btnName = '';
            $.each(_a, function (i, item) {
                btnName += $(item).html();
            });
            if (!isNull(btnName)) {
                var length = btnName.length;
                width = 14 * length + _a.length * 10 + (_a.length - 1) * 10 + 16;
            }
        }
        return width;
    }

};

// 字段值选择表格
var dsFormColumnUtil = {

    config: {
        id: 'skyeye', // 组件展示位置id
        title: '', // 表格的作用标题
        className: 'xxx.skyeye', // 业务对象的服务类
        attrList: [], // 业务对象的服务类对应的属性
        attrSymbols: [], // 属性与值的对比符号
    },

    // 表格序号
    rowNum: 1,
    // 缓存的表格数据
    tableDataList: new Array(),
    // layui的表格对象
    table: null,

    init: function (config, tableDataList) {
        // 设置配置信息
        dsFormColumnUtil.config = config;
        dsFormColumnUtil.tableDataList = isNull(tableDataList) ? [] : tableDataList;
        $.each(dsFormColumnUtil.tableDataList, function (i, item) {
            item.id = dsFormColumnUtil.rowNum;
            dsFormColumnUtil.rowNum++;
        });
        // 获取属性列表/属性与值的对比符号信息
        dsFormColumnUtil.getDataList();
        // 加载盒子
        dsFormColumnUtil.initHtml();
        // 加载表格
        dsFormColumnUtil.initTable();
        // 加载事件
        dsFormColumnUtil.initEvent();
    },

    getDataList: function () {
        // 获取属性列表
        AjaxPostUtil.request({url: reqBasePath + "queryAttrDefinitionList", params: {className: dsFormColumnUtil.config.className}, type: 'json', method: "POST", callback: function (data) {
            var attrList = [].concat(data.rows);
            $.each(attrList, function (i, item) {
                if (!isNull(item.attrDefinitionCustom)) {
                    item.name = item.attrDefinitionCustom.name;
                }
                item.name = item.name + '(' + item.attrKey + ')';
            });
            dsFormColumnUtil.config.attrList = [].concat(attrList);
        }, async: false});
        // 属性与值的对比符号信息
        dsFormColumnUtil.config.attrSymbols = [].concat(skyeyeClassEnumUtil.getEnumDataListByClassName("attrSymbols").rows);
    },

    initHtml: function () {
        var _html = `
            <style>
                .layui-table-cell {
                    height: auto;
                    text-overflow: inherit;
                    overflow: visible;
                    white-space: normal;
                    word-wrap: break-word;
                }
                .layui-table-cell .layui-anim {
                    height: 180px;
                }
                .place-holder {
                    height: 200px;
                }
            </style>
            <div class="layui-form-item layui-col-xs12">
                <label class="layui-form-label">${dsFormColumnUtil.config.title}<i class="red">*</i></label>
                <div class="layui-input-block">
                    <div class="winui-tip alert-info">
                        拖拽表格行可修改顺序。
                    </div>
                    <div class="winui-tool" style="text-align: left;">
                        <button id="addRow" class="winui-toolbtn" type="button"><i class="fa fa-plus" aria-hidden="true"></i>新增行</button>
                        <button id="deleteRow" class="winui-toolbtn" type="button"><i class="fa fa-trash-o" aria-hidden="true"></i>删除行</button>
                    </div>
                    <table class="layui-table" id="messageTable">

                    </table>
                </div>
            </div>`;
        $(`#${dsFormColumnUtil.config.id}`).html(_html);
    },

    initTable: function () {
        layui.define(["jquery", 'form', 'table', 'soulTable'], function(exports) {
            var form = layui.form,
                table = layui.table,
                soulTable = layui.soulTable;
            dsFormColumnUtil.table = table;
            table.render({
                id: 'messageTable',
                elem: '#messageTable',
                method: 'get',
                data: dsFormColumnUtil.tableDataList,
                even: true,
                page: false,
                limit: 100,
                rowDrag: {
                    trigger: '.drag-row',
                    done: function(obj) {}
                },
                cols: [[
                    { type: 'checkbox', align: 'center' },
                    { field: 'test', title: '', align: 'left', width: 40, templet: function (d) {return '<i class="fa fa-arrows drag-row" />';}},
                    { field: 'attrKey', title: '属性<i class="red">*</i>', align: 'left', width: 300, templet: function (d) {
                        var _html = `<select lay-filter="tableSelect" lay-search="" id="attrKey${d.id}" cus-id="${d.id}" win-verify="required"><option value="">请选择</option>`;
                        $.each(dsFormColumnUtil.config.attrList, function (i, item) {
                            if (item.attrKey == d.attrKey) {
                                _html += `<option value="${item.attrKey}" selected="selected">${item.name}</option>`;
                            } else {
                                _html += `<option value="${item.attrKey}">${item.name}</option>`;
                            }
                        });
                        _html += `</select>`;
                        return _html;
                    }},
                    { field: 'symbols', title: '比较符号<i class="red">*</i>', align: 'left', width: 140, templet: function (d) {
                        var _html = `<select lay-filter="tableSelect" lay-search="" id="symbols${d.id}" cus-id="${d.id}" win-verify="required"><option value="">请选择</option>`;
                        $.each(dsFormColumnUtil.config.attrSymbols, function (i, item) {
                            if (item.id == d.symbols) {
                                _html += `<option value="${item.id}" selected="selected">${item.name}</option>`;
                            } else {
                                _html += `<option value="${item.id}">${item.name}</option>`;
                            }
                        });
                        _html += `</select>`;
                        return _html;
                    }},
                    { field: 'value', title: '值<i class="red">*</i>', align: 'left', width: 160, templet: function (d) {
                        return `<div id="valueBox${d.id}">` + dsFormColumnUtil.getDomHtml(d) + `</div>`;
                    }},
                ]],
                done: function(json) {
                    matchingLanguage();
                    if ($(`div[lay-id='messageTable']`).find('.place-holder').length == 0) {
                        $(`div[lay-id='messageTable']`).find('.layui-table-body').append('<div class="place-holder"></div>');
                    }
                    soulTable.render(this);
                }
            });

            form.on('select(tableSelect)', function(data) {
                var id = data.elem.id;
                dsFormColumnUtil.buildData($(`#${id}`));
                if (id.startsWith('attrKey')) {
                    var idNum = id.replace('attrKey', '');
                    var item = getInPoingArr(dsFormColumnUtil.tableDataList, 'id', idNum);
                    $(`#valueBox${idNum}`).html(dsFormColumnUtil.getDomHtml(item));
                    form.render();
                }
            });
        });
    },

    getDomHtml: function (d) {
        if (isNull(d) || isNull(d.id)) {
            return '';
        }
        // 获取属性信息
        var attrKey = $(`#attrKey${d.id}`).val();
        if (isNull(attrKey) && isNull(d.attrKey)) {
            return '';
        }
        attrKey = isNull(attrKey) ? d.attrKey : attrKey;
        var displayValue = d.displayValue;
        var value = d.value;
        // 获取属性对应的组件编码---todo 后续根据编号加载对应的dom
        var numCode = dsFormColumnUtil.getAttrKeyNumCode(attrKey);
        if (isNull(numCode)) {
            return `<input type="text" id="value${d.id}" placeholder="请输入值" cus-id="${d.id}" class="layui-input tableInput" win-verify="required" ` +
                `value="` + (isNull(displayValue) ? "" : displayValue) + `"/>`
        } else {
            if (numCode == 'enumCardSolt' || numCode == 'dictDataCardSolt') {
                // 枚举卡槽/数据字典卡槽

            }
        }
        return '暂不支持';
    },

    getAttrKeyNumCode: function (attrKey) {
        var attrKeyMation = getInPoingArr(dsFormColumnUtil.config.attrList, 'attrKey', attrKey);
        if (isNull(attrKeyMation) || $.isEmptyObject(attrKeyMation)) {
            return '';
        }
        if (isNull(attrKeyMation.attrDefinitionCustom)) {
            return '';
        }
        if (isNull(attrKeyMation.attrDefinitionCustom.dsFormComponent)) {
            return '';
        }
        return attrKeyMation.attrDefinitionCustom.dsFormComponent.numCode;
    },

    initEvent: function () {
        $("body").on("input", ".tableInput", function () {
            dsFormColumnUtil.buildData($(this));
        });
        $("body").on("change", ".tableInput", function () {
            dsFormColumnUtil.buildData($(this));
        });

        $("body").on("click", "#addRow", function() {
            addRow();
        });

        $("body").on("click", "#deleteRow", function() {
            deleteRow();
        });

        // 新增行
        function addRow() {
            dsFormColumnUtil.tableDataList = [].concat(dsFormColumnUtil.table.cache.messageTable);
            dsFormColumnUtil.tableDataList.push({id: dsFormColumnUtil.rowNum});
            dsFormColumnUtil.table.reloadData("messageTable", {data: dsFormColumnUtil.tableDataList});
            dsFormColumnUtil.rowNum++;
        }

        // 删除行
        function deleteRow() {
            dsFormColumnUtil.tableDataList = [].concat(dsFormColumnUtil.table.cache.messageTable);
            var check_box = dsFormColumnUtil.table.checkStatus('messageTable').data;
            for (var i = 0;  i < check_box.length; i++){
                var list = [];
                $.each(dsFormColumnUtil.tableDataList, function(j, item) {
                    if(item.id != check_box[i].id){
                        list.push(item);
                    }
                });
                dsFormColumnUtil.tableDataList = [].concat(list);
            }
            dsFormColumnUtil.table.reloadData("messageTable", {data: dsFormColumnUtil.tableDataList});
        }
    },

    buildData: function (_this) {
        var id = _this.attr('cus-id');
        var key = _this.attr('id').replace(id, '');
        $.each(dsFormColumnUtil.tableDataList, function (j, item) {
            if (key.startsWith('value')) {
                item['value'] = _this.val();
                item['displayValue'] = _this.val();
            } else {
                if (item.id == id) {
                    item[key] = _this.val();
                }
            }
        });
    }

};
