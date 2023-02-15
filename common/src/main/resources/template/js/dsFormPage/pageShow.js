
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

        // 加载操作
        initOperate();
        // 加载页面
        initPage();
    }

    // 加载操作信息
    var operateMap = {};
    function initOperate() {
        var operateList = pageMation.operateList;
        if (isNull(operateList)) {
            return false;
        }
        $.each(operateList, function (i, item) {
            operateMap[item.id] = item;
            if (item.position == 'toolBar') {
                // 工具栏
                $(`#${item.position}`).append(`<button id="${item.id}" class="winui-toolbtn search-table-btn-right item-click"><i class="fa fa-plus" aria-hidden="true"></i>${item.name}</button>`);
            } else if (item.position == 'actionBar') {
                // 操作栏
                $(`#${item.position}`).append(`<a class="layui-btn layui-btn-xs ${item.color}" lay-event="${item.id}">${item.name}</a>`);
            } else if (item.position == 'rightMenuBar') {
                // 右键菜单栏

            }
        });
    }

    function initPage() {
        if (pageMation.type == 'simpleTable') {
            // 基本表格
            dsFormTableUtil.initDynamicTable('messageTable', pageMation);
        }
    }

    exports('pageShow', {});
});
