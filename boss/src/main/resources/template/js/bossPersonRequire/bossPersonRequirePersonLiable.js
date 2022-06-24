
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'tagEditor'], function (exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    layui.use(['form'], function (form) {
        var $ = layui.$;
        var personLiable = [];

        showGrid({
            id: "showForm",
            url: flowableBasePath + "queryBossPersonRequireDetailsById",
            params: {id: parent.rowId},
            pagination: false,
            method: "GET",
            template: $("#beanTemplate").html(),
            ajaxSendLoadBefore: function(hdb, json){
                json.bean.jobRequire = stringManipulation.textAreaShow(json.bean.jobRequire);
                json.bean.remark = stringManipulation.textAreaShow(json.bean.remark);
            },
            ajaxSendAfter: function(json){
                // 附件回显
                skyeyeEnclosure.showDetails({'enclosureUpload': json.bean.enclosureInfo});

                var userNames = [];
                personLiable = [].concat(json.bean.personLiable);
                $.each(personLiable, function(i, item){
                    userNames.push(item.name);
                });
                $('#personLiable').tagEditor({
                    initialTags: userNames,
                    placeholder: '请选择责任人',
                    editorTag: false,
                    beforeTagDelete: function(field, editor, tags, val) {
                        personLiable = [].concat(arrayUtil.removeArrayPointName(personLiable, val));
                    }
                });

                matchingLanguage();
                form.render();
                // 保存
                form.on('submit(formSaveBean)', function(data) {
                    if(winui.verifyForm(data.elem)) {
                        var personLiableId = "";
                        $.each(personLiable, function (i, item) {
                            personLiableId += item.id + ',';
                        });
                        var params = {
                            id: parent.rowId,
                            personLiable: personLiableId
                        };
                        AjaxPostUtil.request({url: flowableBasePath + "setUppersonLiable", params: params, type: 'json', method: "PUT", callback: function(json) {
                            if(json.returnCode == 0) {
                                parent.layer.close(index);
                                parent.refreshCode = '0';
                            } else {
                                winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                            }
                        }});
                    }
                    return false;
                });
            }
        });

        // 人员选择
        $("body").on("click", ".personLiableBtn", function(){
            systemCommonUtil.userReturnList = [].concat(personLiable);
            systemCommonUtil.chooseOrNotMy = "1"; // 人员列表中是否包含自己--1.包含；其他参数不包含
            systemCommonUtil.chooseOrNotEmail = "2"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
            systemCommonUtil.checkType = "1"; // 人员选择类型，1.多选；其他。单选
            systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList){
                // 重置数据
                personLiable = [].concat(systemCommonUtil.tagEditorResetData('personLiable', userReturnList));
            });
        });

        $("body").on("click", ".enclosureItem", function(){
            download(fileBasePath + $(this).attr("rowpath"), $(this).html());
        });

        $("body").on("click", "#cancle", function() {
            parent.layer.close(index);
        });
    });
});