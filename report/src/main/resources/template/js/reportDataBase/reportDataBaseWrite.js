
// 表格的序号
var rowNum = 1;

layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form'], function (exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
        form = layui.form;
    // 数据源信息
    var dataBaseFrom = new Array();
    // 连接池信息
    var poolList = new Array();
    var usetableTemplate = $("#usetableTemplate").html();
    var selOption = getFileContent('tpl/template/select-option.tpl');
    let id = GetUrlParam("id");

    if (isNull(id)) {
        // 加载数据源类型
        showGrid({
            id: "dataType",
            url: reportBasePath + "reportcommon006",
            params: {},
            pagination: false,
            template: selOption,
            method: "GET",
            ajaxSendAfter: function(data) {
                dataBaseFrom = [].concat(data.rows);
                form.render('select');
            }
        });

        // 加载连接池类型
        showGrid({
            id: "poolClassType",
            url: reportBasePath + "reportcommon007",
            params: {},
            pagination: false,
            template: selOption,
            method: "GET",
            ajaxSendAfter: function(data) {
                poolList = [].concat(data.rows);
                form.render('select');
            }
        });
    } else {
        AjaxPostUtil.request({url: reportBasePath + "queryDataBaseById", params: {id: id}, type: 'json', method: 'GET', callback:function(data) {
            $("#name").val(data.bean.name);
            $("#jdbcUrl").val(data.bean.jdbcUrl);
            $("#user").val(data.bean.user);
            $("#password").val(data.bean.password);
            $("#remark").val(data.bean.remark);
            // 加载数据源类型
            showGrid({
                id: "dataType",
                url: reportBasePath + "reportcommon006",
                params: {},
                pagination: false,
                template: selOption,
                method: "GET",
                ajaxSendLoadBefore: function(hdb) {
                },
                ajaxSendAfter: function(json) {
                    $("#dataType").val(data.bean.dataType);
                    dataBaseFrom = [].concat(json.rows);
                    form.render('select');
                }
            });

            // 加载连接池类型
            showGrid({
                id: "poolClassType",
                url: reportBasePath + "reportcommon007",
                params: {},
                pagination: false,
                template: selOption,
                method: "GET",
                ajaxSendLoadBefore: function(hdb) {
                },
                ajaxSendAfter: function(json) {
                    $("#poolClassType").val(data.bean.poolClassType);
                    poolList = [].concat(json.rows);
                    form.render('select');
                }
            });

            // 加载配置选项
            $.each(data.bean.options, function(i, item) {
                addRow();
                $("#configKey" + (rowNum - 1)).val(item.configKey);
                $("#configValue" + (rowNum - 1)).val(item.configValue);
                $("#remark" + (rowNum - 1)).val(item.remark);
            });
            form.render();
        }});
    }

    // 连接池数据变化
    form.on('select(poolClassType)', function(data) {
        var val = data.value;
        var options = getInPoingArr(poolList, "id", val, "options");
        options = JSON.parse(options);
        $("#useTable").html("");
        rowNum = 1;
        $.each(options, function(key, value){
            addRow();
            $("#configKey" + (rowNum - 1)).val(key);
            $("#configValue" + (rowNum - 1)).val(value);
        });
    });

    matchingLanguage();
    form.render();
    form.on('submit(formWriteBean)', function (data) {
        if (winui.verifyForm(data.elem)) {
            var tableData = new Array();
            var rowTr = $("#useTable tr");
            $.each(rowTr, function(i, item) {
                //获取行编号
                var rowNum = $(item).attr("trcusid").replace("tr", "");
                var row = {
                    configKey: $("#configKey" + rowNum).val(),
                    configValue: $("#configValue" + rowNum).val(),
                    remark: $("#remark" + rowNum).val()
                };
                tableData.push(row);
            });
            var params = {
                name: $("#name").val(),
                jdbcUrl: encodeURIComponent($("#jdbcUrl").val()),
                user: encodeURIComponent($("#user").val()),
                password: encodeURIComponent($("#password").val()),
                dataType: $("#dataType").val(),
                poolClassType: $("#poolClassType").val(),
                remark: $("#remark").val(),
                options: JSON.stringify(tableData),
                id: isNull(id) ? '' : id
            };
            AjaxPostUtil.request({url: reportBasePath + "writeDataBase", params: params, type: 'json', method: "POST", callback: function(json) {
                parent.layer.close(index);
                parent.refreshCode = '0';
            }});
        }
        return false;
    });

    // 连接测试
    form.on('submit(testConnection)', function (data) {
        if (winui.verifyForm(data.elem)) {
            var driverClass = getInPoingArr(dataBaseFrom, "id", $("#dataType").val(), "driverClass");
            var params = {
                driverClass: driverClass,
                url: encodeURIComponent($("#jdbcUrl").val()),
                user: encodeURIComponent($("#user").val()),
                pass: encodeURIComponent($("#password").val())
            };
            AjaxPostUtil.request({url: reportBasePath + "reportcommon001", params: params, type: 'json', method: "POST", callback: function(json) {
                winui.window.msg('连接成功', {icon: 1, time: 2000});
            }});
        }
        return false;
    });

    //新增行
    $("body").on("click", "#addRow", function() {
        addRow();
    });

    // 删除行
    $("body").on("click", "#deleteRow", function() {
        deleteRow();
    });

    //新增行
    function addRow() {
        var par = {
            id: "row" + rowNum.toString(), //checkbox的id
            trId: "tr" + rowNum.toString(), //行的id
            configKey: "configKey" + rowNum.toString(), // 配置项id
            configValue: "configValue" + rowNum.toString(), // 配置值id
            remark: "remark" + rowNum.toString() // 备注id
        };
        $("#useTable").append(getDataUseHandlebars(usetableTemplate, par));
        form.render();
        rowNum++;
    }

    // 删除行
    function deleteRow() {
        var checkRow = $("#useTable input[type='checkbox'][name='tableCheckRow']:checked");
        if(checkRow.length > 0) {
            $.each(checkRow, function(i, item) {
                //移除界面上的信息
                $(item).parent().parent().remove();
            });
        } else {
            winui.window.msg('请选择要删除的行', {icon: 2, time: 2000});
        }
    }

    $("body").on("click", "#cancle", function() {
        parent.layer.close(index);
    });
});