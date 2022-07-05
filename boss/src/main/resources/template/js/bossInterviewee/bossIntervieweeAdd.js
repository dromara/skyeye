
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'textool', 'tagEditor', 'form'], function (exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
        textool = layui.textool,
        form = layui.form;

    textool.init({eleId: 'basicResume', maxlength: 1000});

    skyeyeEnclosure.init('enclosureUpload');
    matchingLanguage();
    form.render();
    form.on('submit(formAddBean)', function (data) {
        if (winui.verifyForm(data.elem)) {
            var params = {
                name: $("#name").val(),
                sex: $("input[name='userSex']:checked").val(),
                idcard: $("#idcard").val(),
                phone: $("#phone").val(),
                fromId: bossUtil.bossIntervieweeFromChooseMation.id,
                favoriteJob: $("#favoriteJob").val(),
                basicResume: $("#basicResume").val(),
                workYears: $("#workYears").val(),
                chargePersonId: chargePerson[0].id,
                enclosureResume: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload')
            };
            AjaxPostUtil.request({url: flowableBasePath + "bossInterviewee002", params: params, type: 'json', method: "POST", callback: function(json) {
                parent.layer.close(index);
                parent.refreshCode = '0';
            }});
        }
        return false;
    });

    var chargePerson = [];
    $('#chargePersonId').tagEditor({
        initialTags: [],
        placeholder: '请选择负责人',
        editorTag: false,
        beforeTagDelete: function(field, editor, tags, val) {
            chargePerson = [].concat(arrayUtil.removeArrayPointName(chargePerson, val));
        }
    });

    // 人员选择
    $("body").on("click", "#toHandsPersonSelPeople", function (e) {
        systemCommonUtil.userReturnList = [].concat(chargePerson);
        systemCommonUtil.chooseOrNotMy = "1"; // 人员列表中是否包含自己--1.包含；其他参数不包含
        systemCommonUtil.chooseOrNotEmail = "2"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
        systemCommonUtil.checkType = "2"; // 人员选择类型，1.多选；其他。单选
        systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList){
            // 重置数据
            chargePerson = [].concat(systemCommonUtil.tagEditorResetData('chargePersonId', userReturnList));
        });
    });

    // 选择来源
    $("body").on("click", "#toChooseFromId", function (e) {
        var _this = $(this);
        bossUtil.openBossIntervieweeFromChoosePage(function (bossIntervieweeFromMation){
            _this.parent().find("input").val(bossIntervieweeFromMation.title);
        });
    });

    $("body").on("click", "#cancle", function() {
        parent.layer.close(index);
    });
});