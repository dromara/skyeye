
// 动态表单工具函数
var dsFormUtil = {

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

    pageMation: {},

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
        dsFormUtil.initCreatePage(showBoxId, pageMation);
        $.each(dsFormUtil.pageMation.dsFormPageContents, function (i, content) {
            var attrDefinition = content.attrDefinition;
            if (!isNull(attrDefinition) && !$.isEmptyObject(attrDefinition)) {
                // 获取组件中设置值的脚本
                var dsFormComponent = content.dsFormComponent;
                var value = data[attrDefinition.attrKey];
                if (dsFormComponent.valueMergType == 'extend') {
                    value = data;
                }
                content["value"] = value;
                content["pageType"] = 'edit';
                var jsFitValue = dsFormComponent.jsFitValue;
                if (!isNull(jsFitValue) && jsFitValue.startsWith('layui.define')) {
                    var setValueScript = getDataUseHandlebars('{{#this}}' + dsFormComponent.jsFitValue + '{{/this}}', content);
                    $(`#script${content.id}`).remove();
                    $(`div[contentId="${content.id}"]`).after(`<script id="script${content.id}">${setValueScript}</script>`);
                } else {
                    var setValueScript = getDataUseHandlebars('{{#this}}' + dsFormComponent.jsFitValue + '{{/this}}', content);
                    eval(setValueScript);
                }
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

        form.on('submit(formWriteBean)', function (data) {
            if (winui.verifyForm(data.elem) && !isNull(dsFormUtil.pageMation)) {
                var params = {};
                $.each(dsFormUtil.pageMation.dsFormPageContents, function (i, content) {
                    if (!isNull(content.attrDefinition) && !$.isEmptyObject(content.attrDefinition)) {
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
            }
            return false;
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

        var jsonStr = {bean: content};
        var html = getDataUseHandlebars('{{#bean}}' + component.htmlContent + '{{/bean}}', jsonStr);
        var html_js = getDataUseHandlebars('{{#bean}}' + component.jsContent + '{{/bean}}', jsonStr);
        var jsCon = `<script id="script${content.id}">${html_js}</script>`;
        $("#" + boxId).append(html + jsCon);

        if (!isNull(content.require) && content.require.indexOf('required') >= 0) {
            $(`div[contentId='${content.id}']`).find('label').append('<i class="red">*</i>');
        }

        return content;
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

    /**
     * 目前用于工作流详情展示
     *
     * @param customBoxId
     * @param rows
     */
    initSequenceDataDetails: function (customBoxId, rows) {
        $.each(rows, function (i, item) {
            item.label = dsFormUtil.getLable(item);
            var jsonStr = {
                bean: item
            };
            var showType = dsFormUtil.getShowType(item.attrDefinition);
            if (showType == 4) { // 图片展示
                var photoValue = [];
                if (!isNull(jsonStr.bean.displayValue)) {
                    photoValue = item.displayValue.split(",");
                }
                var rows = [];
                $.each(photoValue, function(j, row) {
                    rows.push({photoValue: row});
                });
                jsonStr.bean.photo = rows;
            }

            // 加载html
            var str = getDataUseHandlebars(dsFormUtil.showType[showType], jsonStr);
            $("#" + customBoxId).append(str);

            if (showType == 5) { // 表格展示
                dsFormTableUtil.intStaticTable("messageTable" + item.orderBy, item.displayValue, item.attrTransformTableList);
            } else  if (showType == 6) { // 凭证展示
                var boxId = "showVoucher" + item.orderBy;
                // 初始化凭证
                voucherUtil.initDataDetails(boxId, item.value);
            }
        });
    },

    // 获取属性的数据展示类型
    getShowType: function (attr) {
        if (!isNull(attr.attrDefinitionCustom)) {
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
            elem: id,
            data: data,
            page: false,
            cols: dsFormUtil.getTableHead({}, tableColumnList)
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
        layui.define(["jquery", 'form', 'table'], function(exports) {
            var table = layui.table;
            var form = layui.form;
            var api = pageMation.businessApi;

            if (pageMation.isDataAuth == 1) {
                // 开启数据权限
                loadAuthBtnGroup(`${id}`, pageMation.dataAuthPointNum);
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
                even: true,
                page: pageMation.isPage == 1 ? true : false,
                overflow: {type: 'tips', header: true, total: true},
                limits: getLimits(),
                limit: getLimit(),
                cols: dsFormTableUtil.getTableHead({
                    serialNumColumn: true,
                    operateColumn: true
                }, tableColumnList),
                done: function(json) {
                    matchingLanguage();
                    initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, pageMation.searchTips, function () {
                        table.reloadData(id, {page: {curr: 1}, where: dsFormTableUtil.getTableParams()});
                    });
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
                    $(`#${item.position}`).append(`<a class="layui-btn layui-btn-xs ${item.color}" lay-event="${item.id}">${item.name}</a>`);
                } else if (item.position == 'rightMenuBar') {
                    // 右键菜单栏

                }
            }
        });
    },

    getTableHead: function (column, tableColumnList) {
        var header = [];
        if (!isNull(column.serialNumColumn) && column.serialNumColumn) {
            header.push({
                title: systemLanguage["com.skyeye.serialNumber"][languageType],
                type: 'numbers',
                fixed: 'left'
            });
        }
        $.each(tableColumnList, function (i, item) {
            var field = {
                field: item.attrKey,
                title: item.label,
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
            if (isNull(customAttr.objectId) && isNull(customAttr.defaultData)) {
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