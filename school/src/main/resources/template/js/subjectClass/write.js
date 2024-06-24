
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery'], function (exports) {
    winui.renderColor();
    layui.use(['form'], function (form) {
        var index = parent.layer.getFrameIndex(window.name);
        var $ = layui.$,
            form = layui.form;
        var objectId = GetUrlParam("objectId");
        var objectKey = GetUrlParam("objectKey");

        // 获取当前登陆用户所属的学校列表
        schoolUtil.queryMyBelongSchoolList(function (json) {
            $("#schoolId").html(getDataUseHandlebars(getFileContent('tpl/template/select-option.tpl'), json));
            form.render("select");

            skyeyeClassEnumUtil.showEnumDataListByClassName("commonEnable", 'radio', "enabled", '', form);

            // //加载学期
            initSemesterId();
        });

        // 学校监听事件
        form.on('select(schoolId)', function(data) {
            // 加载院系
            initFacultyId();
        });

        //所属院系
        function initFacultyId(){
            showGrid({
                id: "facultyId",
                url: schoolBasePath + "queryFacultyListBySchoolId",
                method: "GET",
                params: {schoolId: $("#schoolId").val()},
                pagination: false,
                template: getFileContent('tpl/template/select-option.tpl'),
                ajaxSendLoadBefore: function(hdb) {
                },
                ajaxSendAfter:function (json) {
                    form.render('select');
                }
            });
        }

        // 院系监听事件
        form.on('select(facultyId)', function(data) {
            if(isNull(data.value) || data.value === '请选择'){
                $("#majorId").html("");
                form.render('select');
            } else {
                //加载专业
                initMajorId();
            }
        });

        //所属专业
        function initMajorId(){
            showGrid({
                id: "majorId",
                url: schoolBasePath + "queryMajorListByFacultyId",
                method: "GET",
                params: {facultyId: $("#facultyId").val()},
                pagination: false,
                template: getFileContent('tpl/template/select-option.tpl'),
                ajaxSendLoadBefore: function(hdb) {
                },
                ajaxSendAfter:function (json) {
                    form.render('select');
                }
            });
        }

        // 专业监听事件
        form.on('select(majorId)', function(data) {
            if(isNull(data.value) || data.value === '请选择'){
                $("#majorId").html("");
                form.render('select');
            } else {
                //加载专业
                initClassesId();
            }
        });

        //所属班级
        function initClassesId(){
            showGrid({
                id: "classesId",
                url: schoolBasePath + "queryClassListByMajorId",
                method: "GET",
                params: {majorId: $("#majorId").val()},
                pagination: false,
                template: getFileContent('tpl/template/select-option.tpl'),
                ajaxSendLoadBefore: function(hdb) {
                },
                ajaxSendAfter:function (json) {
                    form.render('select');
                }
            });
        }

        //所属学期
        function initSemesterId(){
            showGrid({
                id: "semesterId",
                url: schoolBasePath + "queryAllSemesterList",
                method: "GET",
                pagination: false,
                template: getFileContent('tpl/template/select-option.tpl'),
                ajaxSendLoadBefore: function(hdb) {
                },
                ajaxSendAfter:function (json) {
                    form.render('select');
                }
            });
        }

        matchingLanguage();
        form.render();

        form.on('submit(formAddBean)', function (data) {
            if (winui.verifyForm(data.elem)) {
                var params = {
                    schoolId: $("#schoolId").val(),
                    facultyId: $("#facultyId").val(),
                    majorId: $("#majorId").val(),
                    classesId: $("#classesId").val(),
                    semesterId: $("#semesterId").val(),
                    enabled: dataShowType.getData('enabled'),
                    objectId : objectId,
                    objectKey :objectKey
                };
                AjaxPostUtil.request({url: schoolBasePath + "writeSubjectClasses", params: params, type: 'json', method: "POST", callback: function (json) {
                        parent.layer.close(index);
                        parent.refreshCode = '0';
                    }});
            }
            return false;
        });

        // 取消
        $("body").on("click", "#cancle", function() {
            parent.layer.close(index);
        });
    });

});