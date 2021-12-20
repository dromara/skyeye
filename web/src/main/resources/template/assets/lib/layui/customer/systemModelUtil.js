// 系统编辑器模板工具函数
var systemModelUtil = {

    // 已经选择的编辑器模板
    chooseSysModel: {},

    /**
     * 根据父id加载模板分类信息
     *
     * @param domId 要加载的dom对象的id
     * @param parentId 父id
     */
    loadSysEveModelTypeByPId: function (domId, parentId){
        showGrid({
            id: domId,
            url: reqBasePath + "sysevemodeltype006",
            params: {parentId: parentId},
            pagination: false,
            method: "GET",
            async: false,
            template: getFileContent('tpl/template/select-option.tpl'),
            ajaxSendLoadBefore: function(hdb){
            },
            ajaxSendAfter: function(json){
            }
        });
    },

    /**
     * 根据父id加载模板分类信息
     *
     * @param domId 要加载的dom对象的id
     * @param parentId 父id
     */
    getSysEveModelTypeDataByPId: function (parentId, callback){
        AjaxPostUtil.request({url: reqBasePath + "sysevemodeltype006", params: {parentId: parentId}, type:'json', method: "GET", callback:function(json){
            if(json.returnCode == 0){
                if(typeof(callback) == "function") {
                    callback(json);
                }
            }else{
                winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
            }
        }, async: false});
    },

    /**
     * 动态表单选择页面
     *
     * @param callback 回调函数
     */
    openSystemModelPageChoosePage: function (callback){
        _openNewWindows({
            url: "../../tpl/sysEveModel/sysEveModelChoose.html",
            title: "编辑器模板选择",
            pageId: "sysEveModelChoose",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                if (refreshCode == '0') {
                    if(typeof(callback) == "function") {
                        callback(systemModelUtil.chooseSysModel);
                    }
                } else if (refreshCode == '-9999') {
                    winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
            }});
    },

};