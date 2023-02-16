
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery'], function (exports) {
    winui.renderColor();
    var $ = layui.$;
    var pageId = GetUrlParam("pageId");
    if (isNull(pageId)) {
        winui.window.msg("请传入布局id", {icon: 2, time: 2000});
        return false;
    }

    // 获取布局信息
    var pageMation = null;
    AjaxPostUtil.request({url: reqBasePath + "dsformpage006", params: {id: pageId}, type: 'json', method: 'GET', callback: function (json) {
        pageMation = json.bean;
        if (isNull(pageMation)) {
            winui.window.msg("该布局信息不存在", {icon: 2, time: 2000});
            return false;
        } else {
            init();
        }
    }});

    var pageHtml = {
        'create': `<div style="margin:0 auto;padding:20px;">
                        <form class="layui-form" action="" id="showForm" autocomplete="off">
                            <div id="content"></div>
                            <div class="layui-form-item layui-col-xs12">
                                <div class="layui-input-block">
                                    <button class="winui-btn" type="button" id="cancle"><language showName="com.skyeye.cancel"></language></button>
                                    <button class="winui-btn" lay-submit lay-filter="formWriteBean"><language showName="com.skyeye.save"></language></button>
                                </div>
                            </div>
                        </form>
                    </div>`,

        'simpleTable': `<div class="winui-toolbar">
                            <div class="winui-tool" id="toolBar">
                                <button id="reloadTable" class="winui-toolbtn search-table-btn-right"><i class="fa fa-refresh" aria-hidden="true"></i><language showName="com.skyeye.refreshDataBtn"></language></button>
                            </div>
                        </div>
                        <div style="margin:auto 10px;">
                            <table id="messageTable" lay-filter="messageTable"></table>
                            <script type="text/html" id="actionBar">
                                
                            </script>
                        </div>`

    };

    // 初始化加载
    function init() {
        console.log(pageMation);
        var html = pageHtml[pageMation.type];
        $("body").append(html);

        // 加载页面
        initPage();
    }

    function initPage() {
        if (pageMation.type == 'create') {
            // 创建布局
            dsFormUtil.initCreatePage('content', pageMation);
        } else if (pageMation.type == 'simpleTable') {
            // 基本表格
            dsFormTableUtil.initDynamicTable('messageTable', pageMation);
        }
    }

    exports('pageShow', {});
});
