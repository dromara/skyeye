

var rowId = "";

var parentNode = null;


layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'tableTreeDj','jquery', 'winui', 'dropdown', 'fsCommon', 'fsTree', 'table', 'form'], function (exports) {
    winui.renderColor();
    var $ = layui.$,
        fsTree = layui.fsTree,
        form = layui.form,
        // table = layui.table;
        tableTree = layui.tableTreeDj;
    var ztree;
    var id = GetUrlParam("id");
    var objectKey = GetUrlParam("objectKey")
    var objectId = GetUrlParam("objectId")

    // 下拉按钮
    var dropdown = new Dropdown();
    dropdown.render();

    //获取所有仓库
    /********* tree 处理   start *************/
    fsTree.render({
        id: "treeDemo",
        url: sysMainMation.erpBasePath + "queryDepotLevelByDepotId?depotId=" + id,
        checkEnable: false,
        showLine: false,
        showIcon: true,
        addDiyDom: ztreeUtil.addDiyDom,
        clickCallback: onClickTree,
        onRightClick: onRightClick,
        onDblClick: function() {},
        onAsyncSuccess: function(id) {}
    }, function(id) {
        ztree = $.fn.zTree.getZTreeObj(id);
        fuzzySearch(id, '#name', null, true);
        initLoadTable();
        ztreeUtil.initEventListener(id);
    });

    //异步加载的方法
    function onClickTree(event, treeId, treeNode) {
        if(treeNode == undefined) {
            dictTypeId = "";
        } else {
            dictTypeId = treeNode.id;
        }
        loadTable();
    }

    function initLoadTable() {
        tableTree.render({
            id: 'messageTable',
            elem: '#messageTable',
            method: 'post',
            url: sysMainMation.erpBasePath + 'queryDepotLevelValList',
            where: getTableParams(),
            even: true,
            page: true,
            limits: getLimits(),
            limit: getLimit(),
            cols: [[
                { title: systemLanguage["com.skyeye.serialNumber"][languageType], rowspan: '2', type: 'numbers' },
                { field: 'number', title: '编号', rowspan: '2', align: 'left', width: 350 },
                { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], rowspan: '2', align: 'left', width: 120 },
                { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], rowspan: '2', align: 'center', width: 150 },
                { field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], rowspan: '2', align: 'left', width: 120 },
                { field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], rowspan: '2', align: 'center', width: 150 },
            ]],
            done: function(json) {
                matchingLanguage();
                initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入编号", function () {
                    tableTree.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
                });
            }
        }
        // , {
        //     keyId: 'id',
        //     keyPid: 'parentId',
        //     title: 'dictName',
        // }
        );

        tableTree.getTable().on('tool(messageTable)', function (obj) {
            var data = obj.data;
            var layEvent = obj.event;
            if (layEvent === 'edit') { //编辑
                edit(data);
            } else if (layEvent === 'details'){ //详情
                details(data);
            } else if (layEvent === 'del') { //删除
                del(data);
            }
        });
    }

    // 添加
    $("body").on("click", "#addBean", function() {
        _openNewWindows({
            url: "../../tpl/turnIocatorManage/addWarehouseLevelValue.html?id=" + id,
            title: "新增仓库级别的值",
            pageId: "warehouseLevelValueAdd",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});



        // _openNewWindows({
        //     url: "../../tpl/turnIocatorManage/writeWarehouseLevel.html?id=" + id,
        //     title: "新增仓库级别",
        //     pageId: "addWarehouseLevel",
        //     area: ['90vw', '90vh'],
        //     callBack: function (refreshCode) {
        //         winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
        //         // 刷新节点
        //         var nownode = ztree.getNodesByParam("id", "0", null);
        //         ztree.reAsyncChildNodes(nownode[0], "refresh");
        //     }});
    });

    // 编辑
    function edit(data) {
        _openNewWindows({
            url: systemCommonUtil.getUrl('FP2024071200008&id=' + data.id, null),
            title: systemLanguage["com.skyeye.editPageTitle"][languageType],
            pageId: "warehouseLevelValueEdit",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    }

    // 详情
    function details(data) {
        _openNewWindows({
            url: systemCommonUtil.getUrl('FP2024071200009&id=' + data.id, null),
            title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
            pageId: "warehouseLevelValueDetails",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
            }});
    }


    // 删除
    function del(data) {
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
            layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.erpBasePath + "deleteDepotLevelValById", params: {id: data.id}, type: 'json', method: "DELETE", callback: function (json) {
                winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
        });
    }


    // 树节点右键
    function onRightClick(event, treeId, treeNode) {
        folderId = treeNode.id;
        // 设置右键节点选中
        chooseNodeSelect(folderId);
        var par = {
            id: folderId,
            name: treeNode.name
        };
        $("#treeRight").html(getDataUseHandlebars($("#treeRightTemplate").html(), {bean: par}));
        showRMenu(folderId == 0 ? 'root' : '', event.clientX, event.clientY);
    }


    // 展示树节点右键菜单
    function showRMenu(type, x, y) {
        $("#treeRight .is-file").show();
        if (type == 'root') {
            $("#treeRight .treedeleteFolderAndChild").hide();
        }
        $("#treeRight").show();
        $("#treeRight").css({top: y + "px", left: x + "px", visibility: "visible", position: "absolute"});
        $("body").bind("mousedown", onBodyMouseDown);
    }

    // 隐藏树节点右键菜单
    function hideRMenu() {
        if ($("#treeRight")) $("#treeRight").css({"visibility": "hidden"});
        $("body").unbind("mousedown", onBodyMouseDown);
    }

    function onBodyMouseDown(event){
        if (!($(event.target).parents(".is-file").length>0)) {
            $("#treeRight").css({"visibility" : "hidden"});
        }
    }

    // 设置选中节点
    function chooseNodeSelect(nodeId){
        var selNode = ztree.getNodeByParam("id", nodeId, null);
        ztree.selectNode(selNode);
    }

    // 树操作--文件夹或者文件删除
    $("body").on("click", ".treedeleteFolderAndChild", function (e) {
        hideRMenu();
        layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
            layer.close(index);
            clickType = 'folder';
            deleteFileOrNote(folderId);
        });
    });

    // 删除指定文件夹或笔记
    function deleteFileOrNote(id){
        AjaxPostUtil.request({url: sysMainMation.erpBasePath + "deleteDepotLevelById", params: {id: id}, type: 'json', method: 'DELETE', callback: function (json) {
            winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                var selNode = ztree.getNodeByParam("id", id, null);
                ztree.selectNode(selNode.getParentNode());// 设置选中节点
                // 重置folderid
                folderId = selNode.getParentNode().id;
                ztree.removeNode(selNode);// 移除节点
            // showListById();// 获取文件夹和笔记列表
        }});
    }

    // 树操作--新建
    $("body").on("click", ".treecreateNewFolder", function (e) {
        hideRMenu();
        var parentId = $(this).attr("folderId");
        // 打开一个新建布局
        _openNewWindows({
            url: "../../tpl/turnIocatorManage/writeWarehouseLevel.html?depotId=" + id + "&parentId=" + parentId,
            title: "新增仓库级别",
            pageId: "writeWarehouseLevel",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                // 刷新节点
                var nownode = ztree.getNodesByParam("id", "0", null);
                ztree.reAsyncChildNodes(nownode[0], "refresh");
            }});
    });


    form.render();
    $("body").on("click", "#reloadTable", function() {
        loadTable();
    });

    function loadTable() {
        table.reload("messageTable", {where: getTableParams()});
    }

    function getTableParams() {
        return $.extend(true, {objectKey:objectKey,objectId:objectId}, initTableSearchUtil.getSearchValue("messageTable"));
    }

    exports('turnIocatorManage', {});
});
