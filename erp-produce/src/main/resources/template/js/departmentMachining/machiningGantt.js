
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form', 'laydate'], function (exports) {
    winui.renderColor();
    var $ = layui.$,
        form = layui.form;
    var id = GetUrlParam("id");

    // 根据id查询加工单信息
    AjaxPostUtil.request({url: sysMainMation.erpBasePath + "queryMachinForGanttById", params: {id: id}, type: 'json', method: 'GET', callback: function (json) {
        matchingLanguage();
        form.render();
        renderPanel();

        gantt.clearAll();  //清空缓存
        // 解析
        gantt.parse({
            data: json.bean.node,
            links: json.bean.link
        });
    }});

    function renderPanel() {
        document.getElementById('device_load').style.cssText = 'height:' + ($(window).height() - 140) + 'px';
    }
    $(window).resize(function () {
        renderPanel();
    });

    // 时间格式
    gantt.config.date_format = "%Y-%m-%d";
    gantt.config.scales = [
        {unit: "year", step: 1, format: "%Y"},
        {unit: "day", step: 1, date: "%m-%d"}
    ];
    gantt.config.reorder_grid_columns = true;
    gantt.config.columns = [{
        name: "text",
        label: "产品",
        width: 200,
        align: "center",
        tree: true,
        resize: true
    }, {
        name: "start_date",
        label: "开始日期",
        width: 100,
        align: "center",
        resize: true
    }, {
        name: "duration",
        label: "持续时间",
        width: 100,
        align: "center",
        resize: true
    }];
    gantt.config.readonly = true;
    gantt.config.row_height = 40;
    gantt.config.scale_height = 50;
    gantt.config.drag_move = false;
    gantt.config.drag_resize = false;
    gantt.config.sort = true;
    gantt.config.show_quick_info = false;
    //  关闭点击事件
    gantt.attachEvent("onTaskDblClick", function (id, e) {
        return false;
    });
    gantt.config.show_tasks_outside_timescale = true;
    gantt.plugins({
        auto_scheduling: true,  //自动排程
        tooltip: true     //提示信息
    });
    // 样式
    gantt.config.layout = {
        css: "gantt_container",
        cols: [{
            width: 400,
            min_width: 300,
            rows: [
                {
                    view: "grid",
                    scrollX: "gridScroll",
                    scrollable: true,
                    scrollY: "scrollVer"
                },
                {view: "scrollbar", id: "gridScroll", group: "horizontal"}
            ]
        },
            {resizer: true, width: 1},
            {
                rows: [
                    {
                        view: "timeline",
                        scrollX: "scrollHor",
                        scrollY: "scrollVer"
                    },
                    {
                        view: "scrollbar",
                        id: "scrollHor",
                        group: "horizontal"
                    }
                ]
            },
            {view: "scrollbar", id: "scrollVer"}]
    };

    gantt.init("device_load");
    gantt.i18n.setLocale("cn");  //使用中文

    $("body").on("click", ".type-btn", function (e) {
        $(this).parent().find('.type-btn').removeClass("plan-select");
        $(this).addClass("plan-select");
        render();
    });

    form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
            render();
        }
        return false;
    });

    exports('machiningGantt', {});
});