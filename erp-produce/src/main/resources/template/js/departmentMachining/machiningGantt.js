var objectKey = "";
var objectId = "";

layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form', 'laydate'], function (exports) {
    winui.renderColor();
    var $ = layui.$,
        form = layui.form,
        layer = layui.layer,
        laydate = layui.laydate;
    objectKey = GetUrlParam("objectKey");
    objectId = GetUrlParam("objectId");
    if (isNull(objectKey) || isNull(objectId)) {
        winui.window.msg("请传入适用对象信息", {icon: 2, time: 2000});
        return false;
    }

    // 根据供应商id获取所有审批通过之后的里程碑列表
    let milestoneList = [];
    AjaxPostUtil.request({url: sysMainMation.erpBasePath + "queryMachinById", params: {id: objectId}, type: 'json', method: 'GET', callback: function (json) {
            $("#milestoneId").html(getDataUseHandlebars(getFileContent('tpl/template/select-option-must.tpl'), json));
            milestoneList = json.bean.machinChildList;
            console.log(1,json)
            console.log(2,milestoneList)

            // var authPermission = teamObjectPermissionUtil.checkTeamBusinessAuthPermission(objectId, 'taskAuthEnum');
            // var btnStr = `<div style="" class="type-group" id="type">`;
            // var firstBtn = true;
            // if (authPermission['list']) {
            //     var defaultClassName = firstBtn ? 'plan-select' : '';
            //     firstBtn = false;
            //     btnStr += `<button type="button" class="layui-btn layui-btn-primary type-btn ${defaultClassName}" data-type="list" table-id="messageTable"><i class="layui-icon"></i>所有任务</button>`
            // }
            // if (authPermission['myExecute']) {
            //     var defaultClassName = firstBtn ? 'plan-select' : '';
            //     firstBtn = false;
            //     btnStr += `<button type="button" class="layui-btn layui-btn-primary type-btn ${defaultClassName}" data-type="myExecute" table-id="messageTable"><i class="layui-icon"></i>我执行的任务</button>`
            // }
            // if (authPermission['myCreate']) {
            //     var defaultClassName = firstBtn ? 'plan-select' : '';
            //     btnStr += `<button type="button" class="layui-btn layui-btn-primary type-btn ${defaultClassName}" data-type="myCreate" table-id="messageTable"><i class="layui-icon"></i>我创建的任务</button>`
            // }
            // btnStr += `</div>`;
            // $(".txtcenter").before(btnStr);

            matchingLanguage();
            form.render();
            renderPanel();
            render();
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
    function render() {
        let milestoneId = $("#milestoneId").val();
        if (isNull(milestoneId)) {
            winui.window.msg("请选择产品", {icon: 2, time: 2000});
            return false;
        }
        let params = {
            id: objectId,
            // objectKey: objectKey,
            // holderId: milestoneId,
            // type: $("#type .plan-select").attr("data-type")
        };
        var tem = getInPoingArr(milestoneList, "id", milestoneId, null);
        gantt.config.start_date = new Date(tem.startTime);
        gantt.config.end_date = new Date(tem.endTime);
        console.log(999)
        AjaxPostUtil.request({url: sysMainMation.erpBasePath + "queryMachinForGanttById", params: params, type: 'json', method: 'GET', callback: function (json) {
                console.log(88,json)
                gantt.clearAll();  //清空缓存
                let nodeList = json.bean.node;
                if (isNull(nodeList) || nodeList.length == 0) {
                    return;
                }
                $.each(nodeList, function (i, item) {
                    item.start_date = new Date(item.start_date);
                    item.end_date = new Date(item.end_date);
                });
                let linkList = json.bean.link;
                if (isNull(linkList) || linkList.length == 0) {
                    linkList = [];
                }
                // 解析
                gantt.parse({
                    data: nodeList,
                    links: linkList
                });
            }});
    }

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