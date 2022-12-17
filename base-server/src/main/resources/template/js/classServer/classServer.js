
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
        tabPageUtil.init({
            id: 'tab',
            prefixData: [{
                title: '详情',
                pageUrl: '../../tpl/classServer/classServerDetails.html'
            }],
            suffixData: null,
            element: layui.element,
            object: {
                objectId: treeNode.id
            }
        });
    }

    form.render();

    exports('classServer', {});
});
