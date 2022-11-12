//表格分页复选框
layui.define(['jquery', 'table'], function (exports) {
    var $ = layui.jquery,
        table = layui.table;

    //记录选中表格记录编号
    var checkedList = {};

    var checkedDataMap = {};

    var tableCheckBoxUtil = {
        init: function (settings) {
            var param = {
                // 表格id
                gridId: '',
                // 表格lay-filter值
                filterId: '',
                // 表格主键字段名
                fieldName: '',
                // 默认回显的数据
                ids: []
            };
            $.extend(param, settings);

            // 设置当前保存数据参数
            if (checkedList[param.gridId] == null) {
                checkedList[param.gridId] = [];
            }
            if (checkedDataMap[param.gridId] == null) {
                checkedDataMap[param.gridId] = [];
            }

            if (!isNull(param.ids) && param.ids.length > 0) {
                this.setIds(settings);
            }

            // 监听选中行
            table.on('checkbox(' + param.filterId + ')', function (obj) {
                var type = obj.type;
                var checked = obj.checked;

                // 当前页数据
                var currentPageData = table.cache[param.gridId];
                // 当前选中数据
                var checkRowData = [];
                // 当前取消选中的数据
                var cacelCheckedRowData = [];

                if (checked) {
                    // 选中
                    checkRowData = table.checkStatus(param.gridId).data;
                } else {
                    // 取消选中
                    if (type == 'all') {
                        cacelCheckedRowData = currentPageData;
                    } else {
                        cacelCheckedRowData.push(obj.data);
                    }
                }
                // 清除数据
                $.each(cacelCheckedRowData, function (index, item) {
                    var itemValue = item[param.fieldName];

                    checkedList[param.gridId] = checkedList[param.gridId].filter(function (fItem, fIndex) {
                        return fItem != itemValue;
                    });
                    checkedDataMap[param.gridId] = checkedDataMap[param.gridId].filter(function (fItem, fIndex) {
                        return fItem[param.fieldName] != itemValue;
                    })
                });

                // 添加选中数据
                $.each(checkRowData, function (index, item) {
                    var itemValue = item[param.fieldName];
                    if (checkedList[param.gridId].indexOf(itemValue) < 0) {
                        checkedList[param.gridId].push(itemValue);
                        checkedDataMap[param.gridId].push(item);
                    }
                });

            });
        },
        //设置页面默认选中（在表格加载完成之后调用）
        checkedDefault: function (settings) {
            var param = {
                //表格id
                gridId: '',
                //表格主键字段名
                fieldName: ''
            };

            $.extend(param, settings);

            // 当前页数据
            var currentPageData = table.cache[param.gridId];
            if (checkedList[param.gridId] != null && checkedList[param.gridId].length > 0) {
                $.each(currentPageData, function (index, item) {
                    var itemValue = item[param.fieldName];

                    if (checkedList[param.gridId].indexOf(itemValue) >= 0) {
                        //设置选中状态
                        item.LAY_CHECKED = true;

                        var rowIndex = item['LAY_TABLE_INDEX'];
                        updateRowCheckStatus(param.gridId, 'tr[data-index=' + rowIndex + '] input[type="checkbox"]');

                        var tmpBean = getInPoingArr(checkedDataMap[param.gridId], param.fieldName, itemValue);
                        if (tmpBean == null) {
                            checkedDataMap[param.gridId].push(item);
                        }
                    }
                });
            }
            //判断当前页是否全选
            var currentPageCheckedAll = table.checkStatus(param.gridId).isAll;
            if (currentPageCheckedAll) {
                updateRowCheckStatus(param.gridId, 'thead tr input[type="checkbox"]');
            }
        },
        //获取当前获取的所有集合值
        getValue: function (settings) {
            var param = {
                //表格id
                gridId: ''
            };
            $.extend(param, settings);
            return checkedList[param.gridId];
        },
        // 获取当前选中的数据
        getValueList: function (settings) {
            var param = {
                //表格id
                gridId: ''
            };
            $.extend(param, settings);
            return checkedDataMap[param.gridId];
        },
        //设置选中的id（一般在编辑时候调用初始化选中值）
        setIds: function (settings) {
            var param = {
                gridId: '',
                //数据集合
                ids: []
            };
            $.extend(param, settings);

            checkedList[param.gridId] = [];
            $.each(param.ids, function (index, item) {
                checkedList[param.gridId].push(item);
            });
        }
    };

    var updateRowCheckStatus = function (gridId, ele) {
        var layTableView = $('.layui-table-view');
        // 一个页面多个表格，这里防止更新表格错误
        $.each(layTableView, function (index, item) {
            if ($(item).attr('lay-id') == gridId) {
                $(item).find(ele).prop('checked', true);
                $(item).find(ele).next().addClass('layui-form-checked');
            }
        });
    }
    //输出
    exports('tableCheckBoxUtil', tableCheckBoxUtil);
});