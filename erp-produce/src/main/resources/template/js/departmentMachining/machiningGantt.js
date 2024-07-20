
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

    let noteList = [];
    // 根据id查询加工单信息
    AjaxPostUtil.request({url: sysMainMation.erpBasePath + "queryMachinForGanttById", params: {id: id}, type: 'json', method: 'GET', callback: function (json) {
        matchingLanguage();
        form.render();
        renderPanel();

        gantt.config.start_date = new Date(json.bean.mathinTime.start_time);
        gantt.config.end_date = new Date(json.bean.mathinTime.end_time);
        gantt.clearAll();  //清空缓存
        // 解析
        noteList = json.bean.node;
        gantt.parse({
            data: json.bean.node,
            links: json.bean.link
        });
    }});

    function renderPanel() {
        document.getElementById('device_load').style.cssText = 'height:' + $(window).height() + 'px';
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
        name: "types",
        label: "类型",
        width: 60,
        align: "center",
        resize: true,
        template: function (item) {
            if (item.types == "project") {
                return "<span style='color: #009688; font-weight: bold;'>产品</span>";
            }
            return "<span style='color: #FFB800;'>工序</span>";
        }
    }, {
        name: "start_date",
        label: "开始日期",
        width: 100,
        align: "center",
        resize: true
    }, {
        name: "end_date",
        label: "结束日期",
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
    // 是否可以编辑
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
    gantt.attachEvent("onTaskClick", function(id, e){
        // 这里的代码会在节点被点击时执行
        // id 参数是被点击的任务的ID
        // e 参数是点击事件的事件对象
        let item = getInPoingArr(noteList, "id", id, null);
        if (item.types != "project") {
            alert("Task with ID " + id + " was clicked");
        }
        // 返回true以允许默认行为继续，返回false可以阻止默认行为
        return true;
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
            width: 500,
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

    exports('machiningGantt', {});
});