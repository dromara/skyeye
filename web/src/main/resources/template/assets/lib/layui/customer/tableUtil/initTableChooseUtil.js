
// 加载表格选择的表格插件
var initTableChooseUtil = {

    setting: {}, // 支出多个一个页面多个配置文件的加载

    chooseInputDataIdKey: "data-id",

    options: {
        id: "", // 表格加载的位置
        indexRow: 0, // 表格行计数器
        placeholder: '', // 描述
        cols: [], // 列属性，{id: 'materialId',
                            // title: '商品',
                            // formType: 'input', formType展示方式 select：下拉框 input：普通输入框 chooseInput：选择输入框 detail：展示,只加载<td></td>
                            // width: '150',
                            // iconClassName: 'chooseProductBtn', 当formType为chooseInput时，指定的icon图标的class属性
                            // className: '额外的class属性',
                            // verify: 'required|number',
                            // value: '默认值',
                            // valueKey: '回显时要展示数据里面的那个key',
                            // modelHtml: '当formType=select时，可以设定select的内容',
                            // colHeaderId: '可以指定所在列的header的id'
                            // layFilter: '可以方便指定监听事件'}
        deleteRowCallback: function () {trcusid}, // 删除行之后的回调函数
        addRowCallback: function (trcusid) {}, // 新增行之后的回调函数
        form: null, // form表单对象
        minData: 0, // 允许的最小数据数
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
        var newOptions = $.extend(true, {}, initTableChooseUtil.options, _options);
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
            '    <div class="layui-form-mid layui-word-aux" style="width: 100%">' + newOptions.placeholder + '</div>' +
            '    <div style="width: 100%; overflow-x: auto;">' +
            '    <table class="layui-table" style="width: auto">' +
            '         <thead>' +
            '             <tr id="header' + newOptions.id + '"></tr>' +
            '         </thead>' +
            '         <tbody id="table' + newOptions.id + '" class="insurance-table">' +
            '         </tbody>' +
            '    </table>' +
            '    <div class="place-holder" style="height: 200px"></div>' +
            '    </div>';
        $("#" + newOptions.id).html(table);
        var headerStr = '<th style="width: 30px;"></th>';
        var fontSize = 14;
        $.each(newOptions.cols, function (i, item) {
            var colHeaderId = isNull(item.colHeaderId) ? "" : ("id = " + item.colHeaderId);
            var bs = '';
            if (!isNull(item.verify) && item.verify.indexOf('required') >= 0) {
                bs = '<i class="red">*</i>';
            }
            // 计算需要添加几个空格
            // 1. 获取文字的宽度
            let titleWidth = fontSize * item.title.length;
            // 2. 获取空格宽度
            let spaceWidth = (item.width - titleWidth) / 2;
            let spaceStr = '';
            if (spaceWidth > 0) {
                // 每个空格的宽度默认是2px
                for (var i = 0 ; i < spaceWidth / 4; i++) {
                    spaceStr += '&nbsp;';
                }
            }
            headerStr += '<th style="width: ' + item.width + 'px; white-space: nowrap;" ' + colHeaderId + '>' + spaceStr +  item.title + bs + spaceStr + '</th>';
        });
        $("#header" + newOptions.id).html(headerStr);
    },

    /**
     * 初始化点击事件
     *
     * @param tableDivId 表格外层div的id
     */
    initEvent: function (tableDivId) {
        // 新增行
        $("body").on("click", "#addRow" + tableDivId, function() {
            var tableDivId = $(this).attr("id").replace("addRow", "");
            initTableChooseUtil.addRow(tableDivId);
        });

        // 删除行
        $("body").on("click", "#deleteRow" + tableDivId, function() {
            var tableDivId = $(this).attr("id").replace("deleteRow", "");
            initTableChooseUtil.deleteRow(tableDivId);
        });
    },

    /**
     * 新增行
     *
     * @param tableDivId 表格外层div的id
     */
    addRow: function (tableDivId) {
        // 获取配置
        var options = initTableChooseUtil.setting[tableDivId];
        var rowIndexStr = tableDivId + options.indexRow.toString();
        var trcusid = 'tr' + rowIndexStr;
        var tbodyStr = '<tr trcusid="' + trcusid + '"><td><input type="checkbox" rowId="row' + rowIndexStr + '" name="tableCheckRow"/></td>';
        $.each(options.cols, function (i, item) {
            var tdId = item.id + rowIndexStr;
            var value = isNull(item.value) ? "" : item.value;
            var className = isNull(item.className) ? "" : item.className;
            var verify = isNull(item.verify) ? "" : item.verify;
            if (item.formType == 'input') {
                tbodyStr += '<td><input type="text" class="layui-input ' + className + '" value="' + value + '" id="' + tdId + '" win-verify="' + verify + '"/></td>';
            } else if (item.formType == 'textarea') {
                tbodyStr += '<td><textarea style="height: 100px;" class="layui-textarea ' + className + '" value="' + value + '" id="' + tdId + '" win-verify="' + verify + '"></textarea></td>';
            } else if (item.formType == 'chooseInput') {
                tbodyStr += '<td class="input-add-icon"><input type="text" class="layui-input ' + className + '" value="' + value + '" id="' + tdId + '" win-verify="' + verify + '" readonly="readonly"/>' +
                    '<i class="fa fa-plus-circle input-icon add-icon ' + item.iconClassName + '"></i></td>';
            } else if (item.formType == 'select') {
                var modelHtml = isNull(item.modelHtml) ? "" : item.modelHtml;
                tbodyStr += '<td><select id="' + tdId + '" lay-filter="' + item.layFilter + '" lay-search win-verify="' + verify + '" class="' + className + '">' + modelHtml + '</select></td>';
            } else if (item.formType == 'detail') {
                tbodyStr += '<td id="' + tdId + '" class="' + className + '">' + value + '</td>';
            }
        });
        tbodyStr += '</tr>';
        $("#table" + tableDivId).append(tbodyStr);
        options.form.render();
        options.indexRow = options.indexRow + 1;
        initTableChooseUtil.setting[tableDivId] = options;
        if(typeof(options.addRowCallback) == "function") {
            options.addRowCallback(trcusid);
        }
        return trcusid;
    },

    /**
     * 删除行
     *
     * @param tableDivId 表格外层div的id
     */
    deleteRow: function (tableDivId) {
        var checkRow = $("#table" + tableDivId + " input[type='checkbox'][name='tableCheckRow']:checked");
        if (checkRow.length > 0) {
            var options = initTableChooseUtil.setting[tableDivId];
            $.each(checkRow, function (i, item) {
                var trcusid = $(item).parent().parent().attr("trcusid");
                if (typeof (options.deleteRowCallback) == "function") {
                    options.deleteRowCallback(trcusid);
                }
                // 移除界面上的信息
                $(item).parent().parent().remove();
            });
        } else {
            winui.window.msg('请选择要删除的行', {icon: 2, time: 2000});
        }
    },

    /**
     * 删除所有行
     *
     * @param tableDivId 表格外层div的id
     */
    deleteAllRow: function (tableDivId) {
        var trRow = $("#table" + tableDivId + " tr");
        var options = initTableChooseUtil.setting[tableDivId];
        $.each(trRow, function (i, item) {
            var trId = $(item).attr("trcusid");
            if (typeof (options.deleteRowCallback) == "function") {
                options.deleteRowCallback(trId);
            }
            // 移除界面上的信息
            $(item).remove();
        });
    },

    /**
     * 获取数据
     *
     * @param tableDivId 表格外层div的id
     */
    getDataList: function (tableDivId) {
        // 获取配置
        var options = initTableChooseUtil.setting[tableDivId];
        var dataList = [];
        var rowTr = $("#table" + tableDivId + " tr");
        $.each(rowTr, function (i, item) {
            var trcusid = $(item).attr("trcusid");
            var rowIndexStr = trcusid.replace("tr", "");
            var row = {
                "trcusid": trcusid
            };
            $.each(options.cols, function (j, bean) {
                var tdId = bean.id + rowIndexStr;
                var value = "";
                if (bean.formType == 'input') {
                    value = $("#" + tdId).val();
                } if (bean.formType == 'textarea') {
                    value = $("#" + tdId).val();
                } else if (bean.formType == 'select') {
                    value = $("#" + tdId).val();
                } else if (bean.formType == 'detail') {
                    value = $("#" + tdId).html();
                } else if (bean.formType == 'chooseInput') {
                    value = $("#" + tdId).attr(initTableChooseUtil.chooseInputDataIdKey);
                }
                row[bean.id] = value;
            });
            row["sortNo"] = i;
            row["orderBy"] = i;
            dataList.push(row);
        });
        var checkResult = true;
        if (dataList.length < options.minData) {
            checkResult = false;
            winui.window.msg('请最少填写' + options.minData + '条信息', {icon: 2, time: 2000});
        }

        return {
            checkResult: checkResult,
            dataList: dataList
        };
    },

    /**
     * 获取指定表格每一行的rowIndex
     *
     * @param tableDivId 表格外层div的id
     */
    getDataRowIndex: function (tableDivId) {
        // 获取配置
        var result = [];
        var rowTr = $("#table" + tableDivId + " tr");
        $.each(rowTr, function (i, item) {
            var trcusid = $(item).attr("trcusid");
            var thisRowKey = trcusid.replace("tr", "");
            result.push(thisRowKey);
        });
        return result;
    },

    /**
     * 重置数据
     *
     * @param tableDivId 表格外层div的id
     * @param data 数据集合[]
     */
    resetDataList: function (tableDivId, data) {
        // 清空数据
        initTableChooseUtil.deleteAllRow(tableDivId);
        // 获取配置
        var options = initTableChooseUtil.setting[tableDivId];
        $.each(data, function (i, item) {
            var trcusid = initTableChooseUtil.addRow(tableDivId);
            initTableChooseUtil.setCols(options.cols, item, trcusid);
        });
    },

    /**
     * 重置单条数据
     *
     * @param tableDivId 表格外层div的id
     * @param data 数据对象{}
     */
    resetData: function (tableDivId, data) {
        // 获取配置
        var options = initTableChooseUtil.setting[tableDivId];
        var trcusid = initTableChooseUtil.addRow(tableDivId);
        initTableChooseUtil.setCols(options.cols, data, trcusid);
        if (typeof (options.addRowCallback) == "function") {
            options.addRowCallback(trcusid);
        }
        return trcusid;
    },

    /**
     * 设置每一列的值
     *
     * @param cols 列集合
     * @param data 数据对象{}
     * @param trcusid 行id
     */
    setCols: function (cols, data, trcusid) {
        var rowIndexStr = trcusid.replace("tr", "");
        $.each(cols, function (j, bean) {
            var value = data[bean.id];
            var tdId = bean.id + rowIndexStr;
            var formType = bean.formType;
            if (formType == 'input') {
                $("#" + tdId).val(value);
            } else if (formType == 'textarea') {
                $("#" + tdId).val(value);
            } else if (formType == 'chooseInput') {
                $("#" + tdId).attr(initTableChooseUtil.chooseInputDataIdKey, value);
                var key = dsFormUtil.getKeyIdToMation(bean.id);
                value = data[key];
                if (!isNull(value)) {
                    $("#" + tdId).val(value.name || value.title);
                }
            } else if (formType == 'select') {
                if (!isNull(value)) {
                    if (!isNull(value["html"])) {
                        $("#" + tdId).html(value["html"]);
                        value = data[bean.id]['value'];
                    }
                    $("#" + tdId).val(value);
                }
            } else if (formType == 'detail') {
                $("#" + tdId).html(value);
            }
        });
    }

}