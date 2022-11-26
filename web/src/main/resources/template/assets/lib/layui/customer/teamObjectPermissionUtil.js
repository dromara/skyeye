
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
    }

}
