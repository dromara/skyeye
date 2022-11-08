

var sysDictDataUtil = {

    dictDataMap: {},

    /**
     * 获取指定状态的数据字典分类
     *
     * @param enabled 状态（1 启用  2.停用）
     * @param callback 回执函数
     */
    queryDictTypeListByEnabled: function (enabled, callback) {
        var params = {
            enabled: enabled
        };
        AjaxPostUtil.request({url: reqBasePath + "queryDictTypeListByEnabled", params: params, type: 'json', method: "GET", callback: function(json) {
            if (typeof(callback) == "function") {
                callback(json);
            }
        }, async: false});
    },

    /**
     * 获取指定分类下的的数据字典
     *
     * @param dictTypeCode 数据字典所属分类的Code
     * @param callback 回执函数
     */
    showDictDataListByDictTypeCode: function (dictTypeCode, showType, showBoxId, defaultId, form, callback, chooseCallback) {
        sysDictDataUtil.queryDictDataListByDictTypeCode(dictTypeCode, function (json) {
            if (showType == 'select') {
                $("#" + showBoxId).html(getDataUseHandlebars(getFileContent('tpl/template/select-option.tpl'), json));
                if (!isNull(defaultId)) {
                    $("#" + showBoxId).val(defaultId);
                } else {
                    $.each(json.rows, function (i, item) {
                        if (item.isDefault == 1) {
                            $("#" + showBoxId).val(item.id);
                        }
                    });
                }
                form.render('select');
            } else if (showType == 'checkbox') {
                $("#" + showBoxId).html(getDataUseHandlebars(getFileContent('tpl/template/checkbox-property.tpl'), json));
                if (!isNull(defaultId)) {
                    var arr = defaultId.split(",");
                    for(var i = 0; i < arr.length; i++) {
                        $('input:checkbox[rowId="' + arr[i] + '"]').attr("checked", true);
                    }
                } else {
                    $.each(json.rows, function (i, item) {
                        if (item.isDefault == 1) {
                            $('input:checkbox[rowId="' + item.id + '"]').attr("checked", true);
                        }
                    });
                }
                form.render('checkbox');
            } else if (showType == 'radioTree') {
                var _html = sysDictDataUtil.getShowTteeHtml();
                var _js = `<script>
                            layui.define(["jquery", 'fsTree'], function(exports) {
                                var jQuery = layui.jquery,
                                    fsTree = layui.fsTree;
                                (function($) {
                                    var ${showBoxId}Object;
                                    fsTree.render({
                                        id: "${showBoxId}Tree",
                                        simpleData: '` + JSON.stringify(json.treeRows) + `',
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
            } else if (showType == 'selectTree') {
                var _html = sysDictDataUtil.getShowTteeHtml();
                var _js = `<script>
                            layui.define(["jquery", 'fsTree'], function(exports) {
                                var jQuery = layui.jquery,
                                    fsTree = layui.fsTree;
                                (function($) {
                                    fsTree.render({
                                        id: "${showBoxId}Tree",
                                        simpleData: '` + JSON.stringify(json.treeRows) + `',
                                        checkEnable: true,
                                        loadEnable: false,
                                        chkStyle: "radio",
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
                                        if (typeof (chooseCallback) == "function") {
                                            chooseCallback(chooseId);
                                        }
                                    }
                                })(jQuery);});
                           </script>`;
                $("#" + showBoxId).append(_html + _js);
            }
            if (typeof (callback) == "function") {
                callback(json);
            }
        });
    },

    getShowTteeHtml: function (showBoxId) {
        var _html = `<link href="../../assets/lib/layui/lay/modules/ztree/css/zTreeStyle/zTreeStyle.css" rel="stylesheet" />
                    <link href="../../assets/lib/layui/lay/modules/contextMenu/jquery.contextMenu.min.css" rel="stylesheet" />
                    <div class="layui-inline" style="width: 100%">
                        <div class="layui-input-inline">
                            <input type="text" id="${showBoxId}Name" name="${showBoxId}Name" placeholder="请输入要搜索的节点" class="layui-input" />
                        </div>
                    </div>
                    <div class="layui-inline" style="max-height: 200px; width: 100%; overflow-y: auto;">
                    <ul id="${showBoxId}Tree" class="ztree fsTree" method="get" isRoot="0" isLoad="0" treeIdKey="id" inputs="parentId" treePIdKey="pId" 
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
     * 获取指定分类下的的数据字典
     *
     * @param dictTypeCode 数据字典所属分类的Code
     * @param callback 回执函数
     */
    queryDictDataListByDictTypeCode: function (dictTypeCode, callback) {
        if (isNull(sysDictDataUtil.dictDataMap[dictTypeCode])) {
            var params = {
                dictTypeCode: dictTypeCode
            };
            AjaxPostUtil.request({url: reqBasePath + "queryDictDataListByDictTypeCode", params: params, type: 'json', method: "GET", callback: function(json) {
                sysDictDataUtil.dictDataMap[dictTypeCode] = json;
            }, async: false});
        }
        if (typeof(callback) == "function") {
            callback(sysDictDataUtil.dictDataMap[dictTypeCode]);
        }
    },

    getDictDataNameByCodeAndKey: function (dictTypeCode, key) {
        var displayName = '';
        sysDictDataUtil.queryDictDataListByDictTypeCode(dictTypeCode, function (json) {
            $.each(json.rows, function (i, item) {
                if (item.id == key) {
                    displayName = item.name;
                }
            });
        });
        return displayName;
    }

};
