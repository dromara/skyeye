layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'element'], function (exports) {
    winui.renderColor();
    layui.use(['form', 'codemirror', 'xml', 'clike', 'css', 'htmlmixed', 'javascript', 'nginx', 'solr', 'sql', 'vue'], function (form) {
        var index = parent.layer.getFrameIndex(window.name);
        var $ = layui.$,
            element = layui.element,
            form = layui.form;
        skyeyeClassEnumUtil.showEnumDataListByClassName("commonEnable", 'radio', "enabled", '', form);
        matchingLanguage();
        form.render();
        form.on('submit(formAddBean)', function (data) {
            if (winui.verifyForm(data.elem)) {
                var params = {
                    name: $("#name").val(),
                    enabled: $("#enabled input:radio:checked").val(),
                    useCount: $("#useCount").val(),
                    takeCount: $("#takeCount").val(),
                    couponMaterialList: $("#couponMaterialList").val(),
                    discountPrice: $("#discountPrice").val(),
                    remark: $("#remark").val(),
                };
                AjaxPostUtil.request({url: shopBasePath + "writeCoupon", params: params, type: 'json', method: 'POST', callback: function (json) {
                        parent.layer.close(index);
                        parent.refreshCode = '0';
                    }});
            }
            return false;
        });

        // 取消
        $("body").on("click", "#cancle", function() {
            parent.layer.close(index);
        });





    });
});