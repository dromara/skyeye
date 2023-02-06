
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form', 'soulTable', 'table'], function (exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
        form = layui.form,
        table = layui.table,
        soulTable = layui.soulTable;
    var tableDataList = new Array();
    var selOption = getFileContent('tpl/template/select-option.tpl');

    // 获取属性
    var attrList = [];
    AjaxPostUtil.request({url: reqBasePath + "queryAttrDefinitionList", params: {className: parent.objectId}, type: 'json', method: "POST", callback: function (data) {
        attrList = [].concat(data.rows);
    }, async: false});

    table.render({
        id: 'messageTable',
        elem: '#messageTable',
        method: 'get',
        data: tableDataList,
        even: true,
        page: false,
        rowDrag: {
            trigger: 'row',
            done: function(obj) {}
        },
        cols: [[
            { type: 'checkbox', align: 'center' },
            { field: 'attrKey', title: '属性<i class="red">*</i>', align: 'left', width: 150, templet: function (d) {
                var _html = `<select lay-filter="tableSelect" lay-search="" id="attrKey${d.id}" cus-id="${d.id}" win-verify="required"><option value="">全部</option>`;
                $.each(attrList, function (i, item) {
                    if (item.attrKey == d.attrKey) {
                        _html += `<option value="${item.attrKey}" selected="selected">${item.name}</option>`;
                    } else {
                        _html += `<option value="${item.attrKey}">${item.name}</option>`;
                    }
                });
                _html += `</select>`;
                return _html;
            }},
            { field: 'name', title: '名称', align: 'left', width: 120, templet: function (d) {
                return `<input type="text" id="name${d.id}" placeholder="请填写名称" cus-id="${d.id}" class="layui-input tableInput" ` +
                    `value="` + (isNull(d.name) ? "" : d.name) + `"/>`;
            }},
            { field: 'align', title: '对齐方式<i class="red">*</i>', align: 'left', width: 120, templet: function (d) {
                var _html = `<select lay-filter="tableSelect" lay-search="" id="align${d.id}" cus-id="${d.id}" win-verify="required"><option value="">全部</option>`;
                $.each(alignmentData.rows, function (i, item) {
                    if (item.id == d.align) {
                        _html += `<option value="${item.id}" selected="selected">${item.name}</option>`;
                    } else {
                        _html += `<option value="${item.id}">${item.name}</option>`;
                    }
                });
                _html += `</select>`;
                return _html;
            }},
            { field: 'width', title: '宽度<i class="red">*</i>', align: 'left', width: 120, templet: function (d) {
                return `<input type="text" id="width${d.id}" placeholder="请填写宽度" cus-id="${d.id}" class="layui-input tableInput" win-verify="required|number" ` +
                    `value="` + (isNull(d.width) ? "" : d.width) + `"/>`;
            }},
            { field: 'templet', title: '脚本', align: 'left', width: 300, templet: function (d) {
                return `<input type="text" id="templet${d.id}" placeholder="请填写脚本" cus-id="${d.id}" class="layui-input tableInput" ` +
                    `value='` + (isNull(d.templet) ? "" : d.templet) + `'/>`;
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

    matchingLanguage();
    form.render();
    form.on('submit(formWriteBean)', function (data) {
        if (winui.verifyForm(data.elem)) {
            var params = {
                id: isNull(parent.rowId) ? '' : parent.rowId,
                name: $("#name").val(),
                remark: $("#remark").val(),
                type: $("#type").val(),
                className: parent.objectId
            };
            AjaxPostUtil.request({url: reqBasePath + "writeDsFormPage", params: params, type: 'json', method: "POST", callback: function (json) {
                parent.layer.close(index);
                parent.refreshCode = '0';
            }});
        }
        return false;
    });

    $("body").on("click", "#cancle", function() {
        parent.layer.close(index);
    });
});