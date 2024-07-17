
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
        tableTree = layui.tableTreeDj;
    var ztree;
    var id = GetUrlParam("id");
    var parentId = GetUrlParam("parentId");
    var depotId = GetUrlParam("id");
    var depotLevelId = GetUrlParam("depotLevelId");

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
            depotLevelId = "";
        } else {
            depotLevelId = treeNode.id;
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
                {field: 'number', title: '编号', rowspan: '2', align: 'left', width: 200},
                { field: 'name', title: '级别', width: 200, templet: function (d) {
                    return getNotUndefinedVal(d.depotLevelMation?.name);
                }},
                {field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType],rowspan: '2', align: 'left', width: 120},
                {field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], rowspan: '2', align: 'center', width: 150},
                {field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], rowspan: '2', align: 'left', width: 120},
                {field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], rowspan: '2', align: 'center', width: 150},
                { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 350, toolbar: '#tableBar' }
            ]],
            done: function (json) {
                matchingLanguage();
                initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入编号", function () {
                    tableTree.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
                });
            }
        }, {
            keyId: 'id',
            keyPid: 'parentId',
            title: 'number',
            showCache: "turnIocatorManageStatus"
        });
    }

    tableTree.getTable().on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'edit') { //编辑
            edit(data);
        } else if (layEvent === 'batchAddChildNodes') { //批量新增子节点
            batchAddChildNodes(data);
        } else if (layEvent === 'del') { //删除
            del(data);
        } else if(layEvent ==='add') { //新增子节点
            parentId = data.id;
            addChildNodes(data)
        }
    });

    // 添加
    $("body").on("click", "#addBean", function() {
        parentId = 0;
        _openNewWindows({
            url: "../../tpl/turnIocatorManage/addWarehouseLevelValue.html?depotId=" + id
                + "&parentId=" + parentId,
            title: "新增仓库级别的值",
            pageId: "warehouseLevelValueAdd",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    });

    // 编辑
    function edit(data) {
        console.log('11',data)
        _openNewWindows({
            url: "../../tpl/turnIocatorManage/addWarehouseLevelValue.html?id=" + data.id
                + "&parentId=" + data.parentId +"&depotId=" + data.depotId,
            title: "编辑仓库级别的值",
            pageId: "warehouseLevelValueEdit",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
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

    function addChildNodes(data){
        _openNewWindows({
            url: "../../tpl/turnIocatorManage/addWarehouseLevelValue.html?depotId=" + data.depotId
                + "&parentId=" + data.id,
            title: "新增子节点仓库级别的值",
            pageId: "warehouseLevelValueAdd",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    }

    //批量新增子节点
    function batchAddChildNodes(data){
        _openNewWindows({
            url: "../../tpl/turnIocatorManage/batchAdd.html?depotId=" + data.depotId + "&parentId=" + data.id,
            title: '批量新增',
            pageId: "warehouseLevelValueBatchAdd",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    }


    // 批量新增
    $("body").on("click", "#batchAdd", function() {
        parentId = 0
        _openNewWindows({
            url: "../../tpl/turnIocatorManage/batchAdd.html?depotId=" + depotId + "&parentId=" + parentId,
            title: '批量新增',
            pageId: "warehouseLevelValueBatchAdd",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                loadTable();
            }});
    });


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
        depotLevelId = nodeId;
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
        tableTree.reload("messageTable", {where: getTableParams()});
    }

    function getTableParams() {
        var params = {
            objectId: depotId,
            holderId: isNull(depotLevelId) || depotLevelId == 0 ? "" : depotLevelId
        };
        return $.extend(true, params, initTableSearchUtil.getSearchValue("messageTable"));
    }

    exports('turnIocatorManage', {});
});
