
// 目录功能的工具类
var catalogTreeUtil = {

    config: {
        boxId: '', // 展示的位置id

        objectId: '', //业务对象数据的id
        className: null, // 业务对象的className
        addOrUser: false, // 是否根据当前登录人查询

        choose: null, // 是否可以选择  null：不可以选择  radioTree：单选  checkboxTree：多选,
        defaultId: '', // 默认值
        loadType: true, // 加载的类型  true：一次性加载完  false：异步加载
        checkAuth: false, // 是否开启权限校验，true：开启；false：不开启
        auth: {}, // 权限信息  数据格式：{"add": true}，代表add操作有权限
        isRoot: 1, // 是否显示根目录，1：是  0：否
        chooseCallback: undefined, // 选中节点后加载的事件
    },

    /**
     * 初始化加载方式
     *
     * @param _config 配置信息
     */
    init: function (_config) {
        catalogTreeUtil.config = $.extend(true, catalogTreeUtil.config, _config);

        catalogTreeUtil.loadTree();
    },

    /**
     * 加载树
     */
    loadTree: function () {
        var showType = catalogTreeUtil.config.choose;
        var showBoxId = catalogTreeUtil.config.boxId;
        var isRoot = catalogTreeUtil.config.isRoot;
        var data = catalogTreeUtil.getData();
        if (showType == 'radioTree' || showType == 'checkboxTree') {
            dataShowType.showData({treeRows: data}, showType, showBoxId, catalogTreeUtil.config.defaultId, null, null, null, null, '1');
        } else {
            var _html = catalogTreeUtil.getShowTteeHtml(showBoxId, isRoot);
            var _js = `<script>
                        layui.define(["jquery", 'fsTree'], function(exports) {
                            var jQuery = layui.jquery,
                                fsTree = layui.fsTree;
                            var chooseCagelogId = "";
                            (function($) {
                                var ztree = null;
                                fsTree.render({
                                    id: "${showBoxId}Tree",
                                    simpleData: '` + JSON.stringify(data) + `',
                                    checkEnable: false,
                                    loadEnable: false,
                                    showLine: false,
                                    showIcon: false,
                                    expandSpeed: 'fast',
                                    clickCallback: onClickTree,
                                    addDiyDom: ztreeUtil.addDiyDom,
                                    onRename: onRename,
                                    beforeRename: beforeRename,
                                    onRightClick: onRightClick,
                                    onDblClick: onClickTree
                                }, function(id) {
                                    ztree = $.fn.zTree.getZTreeObj(id);
                                    fuzzySearch(id, '#${showBoxId}Name', null, true);
                                    ztreeUtil.initEventListener(id);
                                });
                                function onRightClick(event, treeId, treeNode) {
                                    chooseCagelogId = treeNode.id;
                                    // 设置右键节点选中
                                    chooseNodeSelect(chooseCagelogId);
                                    // 公共目录 不展示右键菜单
                                    if (treeNode.type == 2 || chooseCagelogId === '0') {
                                        if (chooseCagelogId === '0') {
                                            showRMenu('root', event.clientX, event.clientY);
                                        } else {
                                            showRMenu('node', event.clientX, event.clientY);
                                        }
                                    }
                                }
                                function onClickTree(event, treeId, treeNode) {
                                    var chooseId;
                                    if (treeNode == undefined || treeNode.id == 0) {
                                        chooseId = "";
                                    } else {
                                        chooseId = treeNode.id;
                                    }
                                    $('#${showBoxId}Choose').val(chooseId).change();
                                }
                                // 设置选中节点
                                function chooseNodeSelect(chooseCagelogId) {
                                    var selNode = ztree.getNodeByParam("id", chooseCagelogId, null);
                                    ztree.selectNode(selNode);
                                }
                                
                                function showRMenu(type, x, y) {
                                    $("#caralogTreeRight .is-file").show();
                                    if(type == 'root'){
                                        $("#caralogTreeRight .reName").hide();
                                        $("#caralogTreeRight .deleteFolder").hide();
                                    }
                                    $("#caralogTreeRight").show();
                                    $("#caralogTreeRight").css({top: y + "px", left: x + "px", visibility: "visible", position: "absolute"});
                                    $("body").bind("mousedown", onBodyMouseDown);
                                }
                                
                                function hideRMenu() {
                                    if ($("#caralogTreeRight")) $("#caralogTreeRight").css({"visibility": "hidden"});
                                    $("body").unbind("mousedown", onBodyMouseDown);
                                }
                                
                                function onBodyMouseDown(event){
                                    if (!($(event.target).parents(".is-file").length > 0)) {
                                        $("#caralogTreeRight").css({"visibility" : "hidden"});
                                    }
                                }
                                
                                // 节点名称修改限制
                                var oldName = '';
                                function beforeRename(treeId, treeNode, newName, isCancel) {
                                    if (isNull(oldName)) {
                                        oldName = treeNode.name;
                                    }
                                    if (newName.length < 1) {
                                        winui.window.msg("节点名称不能为空", {icon: 2, time: 2000});
                                        return false;
                                    }
                                    return true;
                                }
                                
                                // 编辑节点名称
                                function onRename(event, treeId, treeNode) {
                                    var params = {
                                        name: treeNode.name,
                                        icon: '',
                                        parentId: treeNode.parentId,
                                        objectId: '${catalogTreeUtil.config.objectId}',
                                        objectKey: '${catalogTreeUtil.config.className}',
                                        type: 2,
                                        id : treeNode.id
                                    };
                                    AjaxPostUtil.request({url: reqBasePath + "writeCatalog", params: params, type: 'json', method: 'POST', callback: function (json) {
                                        winui.window.msg("保存成功", {icon: 1, time: 2000});
                                        chooseCagelogId = treeNode.id;
                                        oldName = '';
                                    }, errorCallback: function (json) {
                                        winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                                        // 1、获取当前选中的节点
                                        var selectedNode = ztree.getSelectedNodes();
                                        // 2、恢复旧的名称
                                        if (selectedNode.length > 0) {
                                            selectedNode[0].name = oldName;
                                            ztree.editName(selectedNode[0]);
                                        }
                                        oldName = '';
                                    }});
                                }
                                
                                // 文件夹或者文件重命名
                                $("body").on("click", ".reName", function (e) {
                                    hideRMenu();
                                    var selNode = ztree.getNodeByParam("id", chooseCagelogId, null);
                                    ztree.editName(selNode);
                                });
                                
                                // 树操作--新建文件夹
                                $("body").on("click", ".createNewFolder", function (e) {
                                    var params = {
                                        name: '新建文件夹',
                                        icon: '',
                                        parentId: chooseCagelogId,
                                        objectId: '${catalogTreeUtil.config.objectId}',
                                        objectKey: '${catalogTreeUtil.config.className}',
                                        type: 2
                                    };
                                    hideRMenu();
                                    AjaxPostUtil.request({url: reqBasePath + "writeCatalog", params: params, type: 'json', method: 'POST', callback: function (json) {
                                        var newNode = json.bean;
                                        // 1、获取当前选中的节点
                                        var selectedNode = ztree.getSelectedNodes();
                                        // 2、把这个新节点添加到当前选中的节点下，作为它的子节点
                                        if (selectedNode.length > 0) {
                                            ztree.addNodes(selectedNode[0], newNode);
                                        }
                                    }});
                                });
                                
                                // 删除文件夹
                                $("body").on("click", ".deleteFolder", function (e) {
                                    hideRMenu();
                                    layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
                                        layer.close(index);
                                        deleteFile();
                                    });
                                });
                                
                                // 删除文件夹
                                function deleteFile() {
                                    AjaxPostUtil.request({url: reqBasePath + "deleteCatalogById", params: {id: chooseCagelogId}, type: 'json', method: 'DELETE', callback: function (json) {
                                        winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
                                        var selNode = ztree.getNodeByParam("id", chooseCagelogId, null);
                                        ztree.selectNode(selNode.getParentNode());// 设置选中节点
                                        // 重置 chooseCagelogId
                                        chooseCagelogId = selNode.getParentNode().id;
                                        // 移除节点
                                        ztree.removeNode(selNode);
                                    }});
                                }

                            })(jQuery);});
                       </script>`;
            _html += `<link href="../../assets/lib/winui/css/customer/ztree/common-tree.css" rel="stylesheet" />`;
            $("#" + showBoxId).append(_html + _js);
            $("#" + showBoxId + "Choose").on("change", function() {
                if (typeof (catalogTreeUtil.config.chooseCallback) == "function") {
                    catalogTreeUtil.config.chooseCallback($(this).val());
                }
            });
        }
    },

    getShowTteeHtml: function (showBoxId, isRoot) {
        var _html = `<link href="../../assets/lib/layui/lay/modules/ztree/css/zTreeStyle/zTreeStyle.css" rel="stylesheet" />
                    <link href="../../assets/lib/layui/lay/modules/contextMenu/jquery.contextMenu.min.css" rel="stylesheet" />
                    <div class="layui-inline" style="width: 100%">
                        <div class="layui-input-inline" style="width: 100%;">
                            <input type="text" id="${showBoxId}Name" name="${showBoxId}Name" placeholder="请输入要搜索的节点" class="layui-input" />
                            <input type="hidden" id="${showBoxId}Choose" name="${showBoxId}Choose" class="layui-input" />
                        </div>
                    </div>
                    <div class="layui-inline" style="width: 100%;">
                    <ul id="${showBoxId}Tree" class="ztree fsTree" method="get" isRoot="${isRoot}" isLoad="0" treeIdKey="id" inputs="parentId" treePIdKey="parentId" 
                        clickCallbackInputs="parentId:$id" treeName="name" style="overflow-y: auto; height: 100%;"></ul>
                    </div>
                    <ul class="layui-dropdown-menu" id="caralogTreeRight">
                        ` + catalogTreeUtil.getAuthAfterBtn()  + `
                    </ul>
                    <script type="text/javascript" src="../../assets/lib/layui/lay/modules/jquery-min.js"></script>
                    <script type="text/javascript" src="../../assets/lib/layui/lay/modules/contextMenu/jquery.contextMenu.min.js"></script>
                    <script type="text/javascript" src="../../assets/lib/layui/lay/modules/ztree/js/jquery.ztree.all.min.js"></script>
                    <script type="text/javascript" src="../../assets/lib/layui/lay/modules/ztree/js/jquery.ztree.exhide.min.js"></script>
                    <script type="text/javascript" src="../../assets/lib/layui/lay/modules/ztree/js/fuzzysearch.js"></script>`;
        return _html;
    },

    getAuthAfterBtn: function () {
        if (!catalogTreeUtil.config.checkAuth) {
            return `<li class="is-file createNewFolder">
                        <a href="javascript:;"><img alt="" src="../../assets/images/icon/create-folder-icon.png" /><span>新建</span></a>
                    </li>
                    <li class="is-file reName" >
                        <a href="javascript:;"><img alt="" src="../../assets/images/rename-icon.png" /><span>重命名</span></a>
                    </li>
                    <li class="is-file deleteFolder">
                        <a href="javascript:;"><img alt="" src="../../assets/images/icon/delete-icon.png" /><span>删除</span></a>
                    </li>`;
        } else {
            var str = '';
            if (catalogTreeUtil.config.auth['addCatalog']) {
                str += `<li class="is-file createNewFolder">
                            <a href="javascript:;"><img alt="" src="../../assets/images/icon/create-folder-icon.png" /><span>新建</span></a>
                        </li>`;
            }
            if (catalogTreeUtil.config.auth['renameCatalog']) {
                str += `<li class="is-file reName" >
                            <a href="javascript:;"><img alt="" src="../../assets/images/rename-icon.png" /><span>重命名</span></a>
                        </li>`;
            }
            if (catalogTreeUtil.config.auth['deleteCatalog']) {
                str += `<li class="is-file deleteFolder">
                            <a href="javascript:;"><img alt="" src="../../assets/images/icon/delete-icon.png" /><span>删除</span></a>
                        </li>`;
            }
            return str;
        }
    },

    /**
     * 一次性获取所有数据
     */
    getData: function () {
        var data = [];
        var params = {
            objectId: catalogTreeUtil.config.objectId,
            objectKey: catalogTreeUtil.config.className,
            addOrUser: catalogTreeUtil.config.addOrUser
        };
        AjaxPostUtil.request({url: reqBasePath + "queryCatalogForTree", params: params, type: 'json', method: "POST", callback: function(json) {
            data = [].concat(json.rows);
        }, async: false});
        return data;
    }

};
