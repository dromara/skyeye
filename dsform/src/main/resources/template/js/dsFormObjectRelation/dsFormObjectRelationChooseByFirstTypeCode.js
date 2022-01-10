
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form', 'fsCommon', 'fsTree'], function (exports) {
    var index = parent.layer.getFrameIndex(window.name);
    winui.renderColor();
    var $ = layui.$,
        form = layui.form,
        fsTree = layui.fsTree,
        fsCommon = layui.fsCommon;

    var firstTypeCode = GetUrlParam("firstTypeCode");

    /********* tree 处理   start *************/
    var orderType = "";
    var ztree = null;
    fsTree.render({
        id: "treeDemo",
        url: "dsFormObjectRelation007?userToken=" + getCookie('userToken') + "&loginPCIp=" + returnCitySN["cip"] + "&firstTypeCode=" + firstTypeCode + "&language=" + languageType,
        clickCallback: onClickTree,
        onDblClick: onClickTree
    }, function(id){
        ztree = $.fn.zTree.getZTreeObj(id);
    });

    var chooseNode = null;
    // 异步加载的方法
    function onClickTree(event, treeId, treeNode) {
        if(treeNode == undefined) {
            orderType = "";
        } else {
            if(treeNode.isParent != 0){
                return;
            }
            chooseNode = treeNode;
        }
    }
    /********* tree 处理   end *************/

    matchingLanguage();
    form.render();
    form.on('submit(formChooseBean)', function(data) {
        if(winui.verifyForm(data.elem)) {
            if(isNull(chooseNode)){
                winui.window.msg("请选择单据类型.", {icon: 2, time: 2000});
                return false;
            }
            parent.dsFormUtil.dsFormObjectRelationChoose = chooseNode;
            parent.layer.close(index);
            parent.refreshCode = '0';
        }
        return false;
    });

    $("body").on("click", "#cancle", function() {
        parent.layer.close(index);
    });

    exports('dsFormObjectRelationChooseByFirstTypeCode', {});
});
