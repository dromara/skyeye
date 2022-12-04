
// 目录功能的工具类
var catalogTreeUtil = {

    config: {
        boxId: '', // 展示的位置id

        objectId: '', //业务对象数据的id
        className: null, // 业务对象的className
        addOrUser: false, // 是否根据当前登录人查询

        choose: null, // 是否可以选择  null：不可以选择  radio：单选  checkBox：多选
        loadType: true, // 加载的类型  true：一次性加载完  false：异步加载
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
        if (showType == 'radio' || showType == 'checkBox') {
            var _html = catalogTreeUtil.getShowTteeHtml(showBoxId, isRoot);
            var _js = `<script>
                        layui.define(["jquery", 'fsTree'], function(exports) {
                            var jQuery = layui.jquery,
                                fsTree = layui.fsTree;
                            (function($) {
                                var ${showBoxId}Object;
                                fsTree.render({
                                    id: "${showBoxId}Tree",
                                    simpleData: '` + JSON.stringify(data) + `',
                                    checkEnable: true,
                                    loadEnable: false,
                                    chkStyle: "radio",
                                    showLine: false,
                                    showIcon: true,
                                    expandSpeed: 'fast',
                                    onCheck: function (event, treeId, treeNode) {
                                        $('#${showBoxId}').attr('chooseId', treeNode.id);
                                    }
                                }, function(id) {
                                    ${showBoxId}Object = $.fn.zTree.getZTreeObj(id);
                                    fuzzySearch(id, '#${showBoxId}Name', null, true);
                                });
                                if (` + !isNull(defaultId) + `) {
                                    var zTree = ${showBoxId}Object.getCheckedNodes(false);
                                    for (var i = 0; i < zTree.length; i++) {
                                        if(zTree[i].id == '` + defaultId + `'){
                                            ${showBoxId}Object.checkNode(zTree[i], true, true);
                                            $('#${showBoxId}').attr('chooseId', zTree[i].id);
                                        }
                                    }
                                }
                            })(jQuery);});
                       </script>`;
            $("#" + showBoxId).append(_html + _js);
        } else {
            var _html = catalogTreeUtil.getShowTteeHtml(showBoxId, isRoot);
            var _js = `<script>
                        layui.define(["jquery", 'fsTree'], function(exports) {
                            var jQuery = layui.jquery,
                                fsTree = layui.fsTree;
                            (function($) {
                                fsTree.render({
                                    id: "${showBoxId}Tree",
                                    simpleData: '` + JSON.stringify(data) + `',
                                    checkEnable: false,
                                    loadEnable: false,
                                    showLine: false,
                                    showIcon: true,
                                    expandSpeed: 'fast',
                                    clickCallback: onClickTree,
                                    onDblClick: onClickTree
                                }, function(id) {
                                    fuzzySearch(id, '#${showBoxId}Name', null, true);
                                });
                                function onClickTree(event, treeId, treeNode) {
                                    var chooseId;
                                    if (treeNode == undefined || treeNode.id == 0) {
                                        chooseId = "";
                                    } else {
                                        chooseId = treeNode.id;
                                    }
                                    $('#${showBoxId}Choose').val(chooseId).change();
                                }
                            })(jQuery);});
                       </script>`;
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
                    <script type="text/javascript" src="../../assets/lib/layui/lay/modules/jquery-min.js"></script>
                    <script type="text/javascript" src="../../assets/lib/layui/lay/modules/contextMenu/jquery.contextMenu.min.js"></script>
                    <script type="text/javascript" src="../../assets/lib/layui/lay/modules/ztree/js/jquery.ztree.all.min.js"></script>
                    <script type="text/javascript" src="../../assets/lib/layui/lay/modules/ztree/js/jquery.ztree.exhide.min.js"></script>
                    <script type="text/javascript" src="../../assets/lib/layui/lay/modules/ztree/js/fuzzysearch.js"></script>`;
        return _html;
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
