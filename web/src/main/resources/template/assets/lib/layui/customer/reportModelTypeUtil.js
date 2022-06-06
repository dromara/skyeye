// 模型分类工具类
var reportModelTypeUtil = {

    modelTypeChooseHtml: '<div class="layui-form-item layui-col-xs6">\n' +
        '                <label class="layui-form-label">一级分类<i class="red">*</i></label>\n' +
        '                <div class="layui-input-block">\n' +
        '                    <select id="firstTypeId" lay-filter="firstTypeId" lay-search="">\n' +
        '                        <option value="">请选择</option>\n' +
        '                    </select>\n' +
        '                </div>\n' +
        '            </div>\n' +
        '            <div class="layui-form-item layui-col-xs6">\n' +
        '                <label class="layui-form-label">二级分类<i class="red">*</i></label>\n' +
        '                <div class="layui-input-block">\n' +
        '                    <select id="secondTypeId" lay-filter="secondTypeId" lay-search="">\n' +
        '                        <option value="">请选择</option>\n' +
        '                    </select>\n' +
        '                </div>\n' +
        '            </div>',

    /**
     * 加载模型分类选择的html
     *
     * @param form form对象
     * @param showTemplate 数据的模板
     * @param boxId 加载的目标dom的id
     */
    showModelTypeOperator: function (form, showTemplate, boxId){
        $("#" + boxId).html(reportModelTypeUtil.modelTypeChooseHtml);
        
    }

};