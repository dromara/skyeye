
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'laydate', 'form', 'table'], function (exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
        form = layui.form,
        table = layui.table,
        laydate = layui.laydate;
    var subjectList = [];

    // 哪一届
    laydate.render({elem: '#year', type: 'year', max: 'date'});

    // 获取学校列表
    AjaxPostUtil.request({url: sysMainMation.schoolBasePath + "queryAllSchoolList", params: {}, type: 'json', method: 'GET', callback: function (json) {
        $("#schoolId").html(getDataUseHandlebars(getFileContent('tpl/template/select-option.tpl'), json));
        form.render("select");
    }});

    form.on('select(schoolId)', function (data) {
        var value = data.value;
        if (isNull(value)) {
            $("#facultyId").html("");
            $("#majorId").html("");
        } else {
            var params = {
                schoolId: value
            };
            AjaxPostUtil.request({url: sysMainMation.schoolBasePath + "queryFacultyListBySchoolId", params: params, type: 'json', method: 'GET', callback: function (json) {
                $("#facultyId").html(getDataUseHandlebars(getFileContent('tpl/template/select-option.tpl'), json));
                $("#majorId").html("");
                form.render("select");
            }});
        }
    });

    form.on('select(facultyId)', function (data) {
        var value = data.value;
        if (isNull(value)) {
            $("#majorId").html("");
        } else {
            var params = {
                facultyId: value
            };
            AjaxPostUtil.request({url: sysMainMation.schoolBasePath + "queryMajorListByFacultyId", params: params, type: 'json', method: 'GET', callback: function (json) {
                $("#majorId").html(getDataUseHandlebars(getFileContent('tpl/template/select-option.tpl'), json));
                form.render("select");
            }});
        }
    });

    matchingLanguage();
    form.render();
    var tempParams = {};
    form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
            console.log(111)
            tempParams = {
                schoolId: $("#schoolId").val(),
                majorId: $("#majorId").val(),
                year: $("#year").val()
            }
            // 获取科目列表 TODO 待删除
            AjaxPostUtil.request({url: sysMainMation.schoolBasePath + "querySubjectListBySchoolId", params: {schoolId: tempParams.schoolId}, type: 'json', method: 'GET', callback: function (json) {
                subjectList = json.rows;
            }, async: false});

            // 获取该学年该专业的学期列表
            var params = {
                majorId: tempParams.majorId,
                year: tempParams.year
            }
            var tableDataList = [];
            AjaxPostUtil.request({url: sysMainMation.schoolBasePath + "", params: params, type: 'json', method: 'GET', callback: function (json) {

            }, async: false});
            var rowNum = 1;
            $.each(tableDataList, function (i, item) {
                item["id"] = rowNum;
                rowNum++;
            });

            table.render({
                id: 'messageTable',
                elem: '#messageTable',
                method: 'get',
                data: tableDataList,
                even: true,
                page: false,
                limit: 100,
                cols: [[
                    { field: 'value', title: '学期', align: 'left', width: 120, templet: function (d) {
                        return d.name;
                    }},
                    { field: 'subject', title: '科目', align: 'left', width: 200, templet: function (d) {
                        return `<div id="subject${d.id}" cus-id="${d.id}"></div>`;
                    }},
                ]],
                done: function(json) {
                    matchingLanguage();
                    $.each(tableDataList, function (i, item) {
                        // 限制条件
                        var subjectIds = isNull(item.subjectIds) ? '' : ($.isArray(item.subjectIds) ? item.subjectIds.toString() : JSON.parse(item.subjectIds).toString());
                        dataShowType.showData(subjectList, 'verificationSelect', "subject" + item.id, subjectIds, form, null, null, 'formerRequirement');
                    });
                }
            });
        }
        return false;
    });

    form.on('submit(formAddBean)', function (data) {
        if (winui.verifyForm(data.elem)) {
            var params = {
                majorId: tempParams.majorId,
                year: tempParams.year,
            };

            var tableDataList = [].concat(table.cache.messageTable);
            $.each(tableDataList, function (i, item) {
                item.subjectIds = dataShowType.getData(`subject${item.id}`);
            });


            AjaxPostUtil.request({url: sysMainMation.schoolBasePath + "", params: params, type: 'json', callback: function (json) {
                winui.window.msg("录入成功", {icon: 1, time: 3000});
            }});
        }
        return false;
    });

});