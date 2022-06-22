
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
    var chargePerson = [];

    showGrid({
        id: "showForm",
        url: flowableBasePath + "bossInterviewee004",
        params: {id: parent.rowId},
        pagination: false,
        method: "GET",
        template: $("#beanTemplate").html(),
        ajaxSendAfter: function (json) {
            $("input:radio[name=userSex][value=" + json.bean.sex + "]").attr("checked", true);

            bossUtil.bossIntervieweeFromChooseMation = {
                id: json.bean.fromId,
                title: json.bean.fromName
            };

            var userNames = [];
            chargePerson = [].concat(json.bean.chargePerson);
            $.each(chargePerson, function(i, item){
                userNames.push(item.name);
            });
            $('#chargePersonId').tagEditor({
                initialTags: userNames,
                placeholder: '请选择负责人',
                editorTag: false,
                beforeTagDelete: function(field, editor, tags, val) {
                    var inArray = -1;
                    $.each(chargePerson, function(i, item) {
                        if(val === item.name) {
                            inArray = i;
                            return false;
                        }
                    });
                    if(inArray != -1) { //如果该元素在集合中存在
                        chargePerson.splice(inArray, 1);
                    }
                }
            });

            textool.init({
                eleId: 'basicResume',
                maxlength: 1000,
                tools: ['count', 'copy', 'reset']
            });

            skyeyeEnclosure.initTypeISData({'enclosureUpload': json.bean.enclosureInfo});
            matchingLanguage();
            form.render();
            form.on('submit(formEditBean)', function (data) {
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
                        enclosureResume: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload'),
                        id: parent.rowId
                    };
                    AjaxPostUtil.request({url: flowableBasePath + "bossInterviewee005", params: params, type: 'json', method: "PUT", callback: function(json){
                        if(json.returnCode == 0){
                            parent.layer.close(index);
                            parent.refreshCode = '0';
                        }else{
                            winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
                        }
                    }});
                }
                return false;
            });
        }
    });

    // 人员选择
    $("body").on("click", "#toHandsPersonSelPeople", function(e){
        systemCommonUtil.userReturnList = [].concat(chargePerson);
        systemCommonUtil.chooseOrNotMy = "1"; // 人员列表中是否包含自己--1.包含；其他参数不包含
        systemCommonUtil.chooseOrNotEmail = "2"; // 人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
        systemCommonUtil.checkType = "2"; // 人员选择类型，1.多选；其他。单选
        systemCommonUtil.openSysUserStaffChoosePage(function (userReturnList){
            // 移除所有tag
            var tags = $('#chargePersonId').tagEditor('getTags')[0].tags;
            for (i = 0; i < tags.length; i++) {
                $('#chargePersonId').tagEditor('removeTag', tags[i]);
            }
            chargePerson = [].concat(userReturnList);
            // 添加新的tag
            $.each(chargePerson, function(i, item){
                $('#chargePersonId').tagEditor('addTag', item.name);
            });
        });
    });

    // 选择来源
    $("body").on("click", "#toChooseFromId", function(e){
        var _this = $(this);
        bossUtil.openBossIntervieweeFromChoosePage(function (bossIntervieweeFromMation){
            _this.parent().find("input").val(bossIntervieweeFromMation.title);
        });
    });

    $("body").on("click", "#cancle", function(){
        parent.layer.close(index);
    });
});