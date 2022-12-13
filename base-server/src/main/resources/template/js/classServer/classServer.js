
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
        console.log(treeNode);
        tabPageUtil.init({
            id: 'tab',
            prefixData: [{
                title: '详情',
                pageUrl: '../../tpl/customerManage/customerDetails.html'
            }],
            element: layui.element,
            objectType: "1",
            object: {
                objectId: objectId,
                objectKey: objectKey,
            }
        });
    }

    form.render();

    exports('classServer', {});
});
