
// 加载表格选择的表格插件
var initTableChooseUtil = {

    setting: {}, // 支出多个一个页面多个配置文件的加载

    options: {
        id: "", // 表格加载的位置
        indexRow: 0, // 表格行计数器
        cols: [], // 列属性，{id: 'materialId',
                            // title: '商品',
                            // formType: 'input', formType展示方式 select：下拉框 input：普通输入框 chooseInput：选择输入框 detail：展示,只加载<td></td>
                            // width: '150',
                            // iconClassName: 'chooseProductBtn', 当formType为chooseInput时，指定的icon图标的class属性
                            // className: '额外的class属性',
                            // verify: 'required|number',
                            // value: '默认值',
                            // valueKey: '回显时要展示数据里面的那个key',
                            // layFilter: '可以方便指定监听事件',
                            // saveKey: '保存数据时指定的那个key'}
        deleteRowCallback: function () {trId}, // 删除行之后的回调函数
        addRowCallback: function (rowIndexStr) {}, // 新增行之后的回调函数
        form: null, // form表单对象
    }, // 表格参数数据

    /**
     * 初始化表格
     *
     * @param _options 表格属性
     */
    initTable: function (_options) {
        if (isNull(_options.id)) {
            winui.window.msg('id 不能为空', {icon: 2, time: 2000});
            return false;
        }
        var newOptions = $.extend(true, initTableChooseUtil.options, _options);
        initTableChooseUtil.setting[_options.id] = newOptions;
        initTableChooseUtil.initTableHtml(newOptions);
        initTableChooseUtil.initEvent(_options.id);
        initTableChooseUtil.addRow(_options.id);
    },

    /**
     * 初始化表格
     */
    initTableHtml: function (newOptions) {
        var table = '<div class="winui-toolbar">' +
            '        <div class="winui-tool" style="text-align: left;">' +
            '            <button id="addRow' + newOptions.id + '" class="winui-toolbtn" type="button"><i class="fa fa-plus" aria-hidden="true"></i>新增行</button>' +
            '            <button id="deleteRow' + newOptions.id + '" class="winui-toolbtn" type="button"><i class="fa fa-trash-o" aria-hidden="true"></i>删除行</button>' +
            '        </div>' +
            '    </div>' +
            '    <table class="layui-table">' +
            '         <thead>' +
            '             <tr id="header' + newOptions.id + '"></tr>' +
            '         </thead>' +
            '         <tbody id="table' + newOptions.id + '" class="insurance-table">' +
            '         </tbody>' +
            '    </table>';
        $("#" + newOptions.id).html(table);
        var headerStr = '<th style="width: 30px;"></th>';
        $.each(newOptions.cols, function (i, item) {
            headerStr += '<th style="width: ' + item.width + 'px; white-space: nowrap;">' + item.title + '</th>';
        });
        $("#header" + newOptions.id).html(headerStr);
    },

    /**
     * 初始化点击事件
     *
     * @param id
     */
    initEvent: function (id) {
        // 新增行
        $("body").on("click", "#addRow" + id, function() {
            var pointBtnBoxId = $(this).attr("id").replace("addRow", "");
            initTableChooseUtil.addRow(pointBtnBoxId);
        });

        // 删除行
        $("body").on("click", "#deleteRow" + id, function() {
            var pointBtnBoxId = $(this).attr("id").replace("addRow", "");
            initTableChooseUtil.deleteRow(pointBtnBoxId);
        });
    },

    /**
     * 新增行
     *
     * @param id
     */
    addRow: function (id) {
        // 获取配置
        var options = initTableChooseUtil.setting[id];
        var rowIndexStr = id + options.indexRow.toString();
        var tbodyStr = '<tr trcusid="tr' + rowIndexStr + '"><td><input type="checkbox" rowId="row' + rowIndexStr + '" name="tableCheckRow"/></td>';
        $.each(options.cols, function (i, item) {
            var tdId = item.id + rowIndexStr;
            var value = isNull(item.value) ? "" : item.value;
            var className = isNull(item.className) ? "" : item.className;
            var verify = isNull(item.verify) ? "" : item.verify;
            if (item.formType == 'input') {
                tbodyStr += '<td><input type="text" class="layui-input ' + className + '" value="' + value + '" id="' + tdId + '" win-verify="' + verify + '"/></td>';
            } else if (item.formType == 'chooseInput') {
                tbodyStr += '<td><input type="text" class="layui-input ' + className + '" value="' + value + '" id="' + tdId + '" win-verify="' + verify + '" readonly="readonly"/>' +
                    '<i class="fa fa-plus-circle input-icon ' + item.iconClassName + '" style="top: 12px;"></i></td>';
            } else if (item.formType == 'select') {
                tbodyStr += '<td><select id="' + tdId + '" lay-filter="' + item.layFilter + '" lay-search win-verify="' + verify + '"></select></td>';
            } else if (item.formType == 'detail') {
                tbodyStr += '<td id="' + tdId + '" class="' + className + '"></td>';
            }
        });
        tbodyStr += '</tr>';
        $("#table" + id).append(tbodyStr);
        options.form.render();
        options.indexRow = options.indexRow + 1;
        initTableChooseUtil.setting[id] = options;
        if(typeof(initTableChooseUtil.addRowCallback) == "function") {
            initTableChooseUtil.addRowCallback(rowIndexStr);
        }
    },

    /**
     * 删除行
     *
     * @param id
     */
    deleteRow: function (id) {
        var checkRow = $("#table" + id + " input[type='checkbox'][name='tableCheckRow']:checked");
        if (checkRow.length > 0) {
            $.each(checkRow, function (i, item) {
                var trId = $(item).parent().parent().attr("trcusid");
                if (typeof (initTableChooseUtil.deleteRowCallback) == "function") {
                    initTableChooseUtil.deleteRowCallback(trId);
                }
                // 移除界面上的信息
                $(item).parent().parent().remove();
            });
        } else {
            winui.window.msg('请选择要删除的行', {icon: 2, time: 2000});
        }
    },

    /**
     * 获取数据
     *
     * @param id
     */
    getDataList: function (id) {
        // 获取配置
        var options = initTableChooseUtil.setting[id];
        var result = [];
        var rowTr = $("#table" + id + " tr");
        $.each(rowTr, function (i, item) {
            var trId = $(item).attr("trcusid");
            var rowIndexStr = trId.replace("tr", "");
            var row = {};
            $.each(options.cols, function (j, bean) {
                if (!isNull(bean.saveKey)) {
                    // saveKey不为空时，指定key-value
                    var tdId = bean.id + rowIndexStr;
                    var value = "";
                    if (bean.formType == 'input') {
                        value = $("#" + tdId).val();
                    } else if (bean.formType == 'select') {
                        value = $("#" + tdId).val();
                    } else if (bean.formType == 'detail') {
                        value = $("#" + tdId).html();
                    }
                    row[bean.saveKey] = value;
                }
            });
            result.push(row);
        });
        return result;
    },

    /**
     * 获取指定表格每一行的rowIndex
     *
     * @param id
     */
    getDataRowIndex: function (id) {
        // 获取配置
        var result = [];
        var rowTr = $("#table" + id + " tr");
        $.each(rowTr, function (i, item) {
            var trId = $(item).attr("trcusid");
            var rowIndexStr = trId.replace("tr", "");
            result.push(rowIndexStr);
        });
        return result;
    },

}