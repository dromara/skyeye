

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
            dataShowType.showData(json, showType, showBoxId, defaultId, form, callback, chooseCallback);
        });
    },

    getShowTteeHtml: function (showBoxId, isRoot) {
        var _html = `<link href="../../assets/lib/layui/lay/modules/ztree/css/zTreeStyle/zTreeStyle.css" rel="stylesheet" />
                    <link href="../../assets/lib/layui/lay/modules/contextMenu/jquery.contextMenu.min.css" rel="stylesheet" />
                    <div class="layui-inline" style="width: 100%">
                        <div class="layui-input-inline" style="width: 100%">
                            <input type="text" id="${showBoxId}Name" name="${showBoxId}Name" placeholder="请输入要搜索的节点" class="layui-input" />
                            <input type="hidden" id="${showBoxId}Choose" name="${showBoxId}Choose" class="layui-input" />
                        </div>
                    </div>
                    <div class="layui-inline" style="width: 100%;">
                    <ul id="${showBoxId}Tree" class="ztree fsTree" method="get" isRoot="${isRoot}" isLoad="0" treeIdKey="id" inputs="parentId" treePIdKey="parentId" 
                        clickCallbackInputs="parentId:$id" treeName="name" style="overflow-y: auto; height: 100%;"></ul>
                    </div>
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
