
var chooseTreeNode;

layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form', 'fsTree'], function (exports) {
    winui.renderColor();
    var $ = layui.$,
        form = layui.form,
        fsTree = layui.fsTree;

    fsTree.render({
        id: "treeDemo",
        url: reqBasePath + "queryServiceClassForTree",
        checkEnable: false,
        loadEnable: false,
        showLine: false,
        showIcon: true,
        addDiyDom: ztreeUtil.addDiyDom,
        clickCallback: onClickTree,
        onDblClick: onClickTree
    }, function(id) {
        fuzzySearch(id, '#name', null, true);
        ztreeUtil.initEventListener(id);
    });

    // 异步加载的方法
    function onClickTree(event, treeId, treeNode) {
        if (treeNode.level != 3) {
            return false;
        }
        chooseTreeNode = treeNode;
        let appId = chooseTreeNode.classMation.appId;
        var defaultList = [{
            title: '详情',
            pageUrl: '../../tpl/classServer/classServerDetails.html?appId=' + appId
        }, {
            title: '属性信息',
            pageUrl: '../../tpl/attr/attrList.html?appId=' + appId
        }, {
            title: '表单布局',
            pageUrl: '../../tpl/dsFormPage/pageList.html?appId=' + appId
        }, {
            title: '操作按钮',
            pageUrl: '../../tpl/operate/operateList.html?appId=' + appId
        }];

        if (chooseTreeNode.classMation.flowable) {
            defaultList.push({
                title: '流程信息',
                pageUrl: '../../tpl/classServer/classServerProcessList.html?appId=' + appId
            });
        }

        tabPageUtil.init({
            id: 'tab',
            prefixData: [],
            suffixData: defaultList,
            element: layui.element,
            object: {
                objectId: treeNode.id.replace(appId, ''),
            }
        });
    }

    form.render();

    exports('classServer', {});
});
