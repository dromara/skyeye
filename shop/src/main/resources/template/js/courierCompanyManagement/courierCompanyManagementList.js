layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form'], function (exports) {
    winui.renderColor();
    var $ = layui.$,
        form = layui.form,
        table = layui.table;
    var selTemplate = getFileContent('tpl/template/select-option-must.tpl');

    authBtn('1728025733469');//新增

    var storeId = "";
    // 加载
    let storeHtml = '';
    AjaxPostUtil.request({url: sysMainMation.shopBasePath + "storeStaff005", params: {}, type: 'json', method: "Get", callback: function (json) {
            storeId = json.rows.length > 0 ? json.rows[0].id : '-';
            storeHtml = getDataUseHandlebars(selTemplate, json);
            initTable();
        }});

    form.on('select(storeId)', function (data) {
        var thisRowValue = data.value;
        storeId = isNull(thisRowValue) ? "" : thisRowValue;
        loadTable();
    });

    function initTable() {
        table.render({
            id: 'messageTable',
            elem: '#messageTable',
            method: 'POST',
            url: sysMainMation.shopBasePath + 'queryDeliveryList',
            where: getTableParams(),
            page: true,
            limits: getLimits(),
            limit: getLimit(),
            cols: [[
                {title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
                {
                    field: 'name', title: '快递公司名称', width: 150, align: 'center', templet: function (d) {
                        return '<a lay-event="details" class="notice-title-click">' + d.name + '</a>';
                    }
                }, {
                    field: 'logo', title: 'logo', width: 120,templet: function (d) {
                        if (isNull(d.logo)) {
                            return '<img src="../../assets/images/os_windows.png" class="photo-img">';
                        } else {
                            return '<img src="' + fileBasePath + d.logo + '" class="photo-img" lay-event="logo">';
                        }
                    }
                },   {
                    field: 'orderBy',
                    title: '排序',
                    width: 120
                },{
                    field: 'codeNum',
                    title: '快递公司 code',
                    width: 120
                },
                {
                    field: 'enabled', title: '状态', width: 100, templet: function (d) {
                        return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("commonEnable", 'id', d.enabled, 'name');
                    }
                },


                {
                    field: 'createName',
                    title: systemLanguage["com.skyeye.createName"][languageType],
                    width: 150
                },
                {
                    field: 'createTime',
                    title: systemLanguage["com.skyeye.createTime"][languageType],
                    align: 'center',
                    width: 150
                },
                {
                    field: 'lastUpdateName',
                    title: systemLanguage["com.skyeye.lastUpdateName"][languageType],
                    align: 'left',
                    width: 150
                },
                {
                    field: 'lastUpdateTime',
                    title: systemLanguage["com.skyeye.lastUpdateTime"][languageType],
                    align: 'center',
                    width: 150
                },
                {
                    title: systemLanguage["com.skyeye.operation"][languageType],
                    fixed: 'right',
                    align: 'center',
                    width: 200,
                    toolbar: '#tableBar'
                }

            ]],
            done: function (json) {
                matchingLanguage();
                initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入快递公司名称", function () {
                    table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
                }, `<label class="layui-form-label">门店</label><div class="layui-input-inline">
						<select id="storeId" name="storeId" lay-filter="storeId" lay-search="">
						${storeHtml}
					</select></div>`);
            }
        });
    }
    table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'del') {
            del(data);
        } else if (layEvent === 'edit') {
            edit(data);
        }
    });

    // 新增
    $("body").on("click", "#addBean", function () {
        _openNewWindows({
            url: "../../tpl/courierCompanyManagement/courierCompanyManagementWrite.html?storeId=" + storeId,
            title: systemLanguage["com.skyeye.addPageTitle"][languageType],
            pageId: "storeTypeServiceAdd",
            area: ['90vw', '90vh'],//宽度和高度
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }
        });
    });
    // 编辑
    function edit(data) {
        _openNewWindows({
            url: "../../tpl/courierCompanyManagement/courierCompanyManagementWrite.html?id=" + data.id,
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "storeTypeServiceEdit",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }
        });
    }
    // 删除
    function del(data, obj) {
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
            layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.shopBasePath + "deleteDeliveryById", params: {ids: data.id}, type: 'json', method: 'DELETE', callback: function (json) {
                    winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                    loadTable();
                }});
        });
    }
    form.render();
    $("body").on("click", "#reloadTable", function () {
        loadTable();
    });
    function loadTable() {
        table.reloadData("messageTable", {where: getTableParams()});
    }
    function getTableParams() {
        let params = {
            objectId: storeId,
        };
        return $.extend(true, params, initTableSearchUtil.getSearchValue("messageTable"));
    }

});