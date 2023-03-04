
// 团队成员权限工具
var teamObjectPermissionUtil = {

    getAuthCols: function (objectType) {
        if (isNull(objectType)) {
             return [];
        } else {
            var teamObjectType = skyeyeClassEnumUtil.getEnumDataListByClassName("teamObjectType").rows;
            var pageAuthList = getInPoingArr(teamObjectType, "id", objectType, "pageAuth");
            if (pageAuthList == null) {
                return [];
            }
             var result = [];
             $.each(pageAuthList, function(i, enumKey) {
                 var map = {
                     id: enumKey,
                     title: skyeyeClassEnum[enumKey].name
                 };
                 // 获取枚举中需要鉴权的内容
                 var authBtnJson = skyeyeClassEnumUtil.getEnumDataListByClassName(enumKey).rows;
                 var cols = [];
                 cols.push({
                     field: 'name',
                     title: '名称',
                     width: 160
                 });
                 $.each(authBtnJson, function(j, item) {
                     cols.push({
                         field: item.id,
                         title: item.name,
                         align: 'center',
                         width: 140,
                         templet: function(row) {
                             var roleId = row.pId == '0' ? row.id : row.pId;
                             var authGroupKey = row.authGroupKey;
                             var authKey = $(this)[0].field;
                             var userId = row.pId == '0' ? '' : row.id;
                             var checkBoxId = authGroupKey + '_' + authKey + '_' + roleId + '_' + userId;
                             var name = authGroupKey + '_' + authKey + '_' + roleId;
                             return `<input type="checkbox" id="${checkBoxId}" name="${name}" lay-filter="checkClick"/>`;
                         }
                     });
                 });
                 map['cols'] = cols;
                 result.push(map);
             });
             return result;
         }
    },

    getAuthColsDetails: function (objectType) {
        if (isNull(objectType)) {
            return [];
        } else {
            var teamObjectType = skyeyeClassEnumUtil.getEnumDataListByClassName("teamObjectType").rows;
            var pageAuthList = getInPoingArr(teamObjectType, "id", objectType, "pageAuth");
            if (pageAuthList == null) {
                return [];
            }
            var result = [];
            $.each(pageAuthList, function(i, enumKey) {
                var map = {
                    id: enumKey,
                    title: skyeyeClassEnum[enumKey].name
                };
                // 获取枚举中需要鉴权的内容
                var authBtnJson = skyeyeClassEnumUtil.getEnumDataListByClassName(enumKey).rows;
                var cols = [];
                cols.push({
                    field: 'name',
                    title: '名称',
                    width: 160
                });
                $.each(authBtnJson, function(j, item) {
                    cols.push({
                        field: item.id,
                        title: item.name,
                        align: 'center',
                        width: 140,
                        templet: function(row) {
                            var roleId = row.pId == '0' ? row.id : row.pId;
                            var authGroupKey = row.authGroupKey;
                            var authKey = $(this)[0].field;
                            var userId = row.pId == '0' ? '' : row.id;
                            var checkBoxId = authGroupKey + '_' + authKey + '_' + roleId + '_' + userId;
                            return `<div style="width: 30px; height: 30px" id="${checkBoxId}"><i class="fa fa-close" style="color: orangered"></i></div>`;
                        }
                    });
                });
                map['cols'] = cols;
                result.push(map);
            });
            return result;
        }
    },

    /**
     * 根据受用类型获取需要展示个tab页
     *
     * @param objectType
     * @returns {*[]}
     */
    getPageUrl: function (objectType) {
        if (isNull(objectType)) {
            return [];
        } else {
            var teamObjectType = skyeyeClassEnumUtil.getEnumDataListByClassName("teamObjectType").rows;
            var pageAuthList = getInPoingArr(teamObjectType, "id", objectType, "pageAuth");
            if (pageAuthList == null) {
                return [];
            }
            var result = [];
            $.each(pageAuthList, function(i, enumKey) {
                result.push({
                    title: skyeyeClassEnum[enumKey].name,
                    pageUrl: skyeyeClassEnum[enumKey].pageUrl
                });
            });
            return result;
        }
    },

    /**
     * 根据受用对象加载已启用的团队模板列表
     *
     * @param id
     * @param objectType
     * @param defaultValue
     */
    buildTeamTemplate: function (id, objectType, defaultValue) {
        if (!isNull(objectType)) {
            AjaxPostUtil.request({url: reqBasePath + "queryEnableTeamTemplateList", params: {objectType: objectType}, type: 'json', method: "GET", callback: function(json) {
                var selOption = getFileContent('tpl/template/select-option.tpl');
                $(`#${id}`).html(getDataUseHandlebars(selOption, json));
                if (!isNull(defaultValue)) {
                    $(`#${id}`).val(defaultValue);
                }
            }, async: false});
        }
    },

    // 获取团队模板信息
    getTeamTemplate: function (id) {
        var teamTemplate = {};
        if (!isNull(id)) {
            AjaxPostUtil.request({url: reqBasePath + "queryTeamTemplateById", params: {id: id}, type: 'json', method: "GET", callback: function(json) {
                teamTemplate = json.bean;
            }, async: false});
        }
        return teamTemplate;
    },

    checkTeamBusinessAuthPermission: function (objectId, enumKey) {
        var params = {
            objectId: objectId,
            enumKey: enumKey,
            enumClassName: skyeyeClassEnum[enumKey].className
        };
        var authPermission = {};
        AjaxPostUtil.request({url: reqBasePath + "checkTeamBusinessAuthPermission", params: params, type: 'json', method: "POST", callback: function(json) {
            authPermission = json.bean;
            $.each(authPermission, function (key, checkResult) {
                if (!checkResult) {
                    $('[auth="' + key + '"]').remove();
                }
            });
        }, async: false});
        return authPermission;
    }

}
