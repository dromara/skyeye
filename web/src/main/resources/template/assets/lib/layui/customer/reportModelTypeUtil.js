// 模型分类工具类
var reportModelTypeUtil = {

    modelTypeChooseHtml: '<div class="layui-form-item layui-col-xs6">\n' +
        '                <label class="layui-form-label">一级分类<i class="red">*</i></label>\n' +
        '                <div class="layui-input-block">\n' +
        '                    <select id="firstTypeId" lay-filter="firstTypeId" lay-search="" win-verify="required">\n' +
        '                        <option value="">请选择</option>\n' +
        '                    </select>\n' +
        '                </div>\n' +
        '            </div>\n' +
        '            <div class="layui-form-item layui-col-xs6">\n' +
        '                <label class="layui-form-label">二级分类<i class="red">*</i></label>\n' +
        '                <div class="layui-input-block">\n' +
        '                    <select id="secondTypeId" lay-filter="secondTypeId" lay-search="" win-verify="required">\n' +
        '                        <option value="">请选择</option>\n' +
        '                    </select>\n' +
        '                </div>\n' +
        '            </div>',

    /**
     *
     * 加载模型分类选择的html
     *
     * @param form form对象
     * @param boxId 加载的目标dom的id
     * @param firstTypeId 一级分类id
     * @param secondTypeId 二级分类id
     */
    showModelTypeOperator: function (form, boxId, firstTypeId, secondTypeId) {
        $("#" + boxId).html(reportModelTypeUtil.modelTypeChooseHtml);
        var selOption = getFileContent('tpl/template/select-option.tpl');
        // 加载一级模型分类
        reportModelTypeUtil.loadModelType("firstTypeId", selOption, "0", form);
        if (!isNull(firstTypeId)) {
            $("#firstTypeId").val(firstTypeId);
            // 加载二级模型分类
            reportModelTypeUtil.loadModelType("secondTypeId", selOption, firstTypeId, form);
        }
        if (!isNull(secondTypeId)) {
            $("#secondTypeId").val(secondTypeId);
        }
        form.render('select');
        form.on('select(firstTypeId)', function (data) {
            var value = data.value;
            // 加载二级模型分类
            reportModelTypeUtil.loadModelType("secondTypeId", selOption, value, form);
        });
    },

    /**
     * 获取模型分类信息
     *
     * @param id 展示对象id
     * @param template 模板
     * @param parentId 父id
     * @param form form对象
     */
    loadModelType: function (id, template, parentId, form) {
        showGrid({
            id: id,
            url: reportBasePath + "reportmodeltype006",
            params: {"parentId": parentId},
            pagination: false,
            method: "GET",
            template: template,
            ajaxSendLoadBefore: function (hdb) {
            },
            ajaxSendAfter: function (json) {
                form.render('select');
            }
        });
    }

};