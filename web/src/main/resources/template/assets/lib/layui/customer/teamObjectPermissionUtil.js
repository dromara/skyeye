
// 团队成员权限工具
var teamObjectPermissionUtil = {

    template: `{{#json}}
                        <div class="layui-form-item layui-col-xs12">
                            <label class="layui-form-label">{{bean.name}}<i class="red">*</i></label>
                            <div class="layui-input-block">
                                <table class="layui-table">
                                    <thead>
                                        <tr>
                                            <th>成员</th>
                                            {{#each rows}}
                                                <th>{{name}}</th>
                                            {{/each}}
                                        </tr>
                                    </thead>
                                    <tbody id="{{bean.key}}" class="insurance-table">
                                        {{#each roleList}}
                                        <tr>
                                            <td>{{name}}</td>
                                            {{#each ../rows}}
                                                <th>{{name}}</th>
                                            {{/each}}
                                        </tr>
                                        {{/each}}
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    {{/json}}`;

    // 新增时的展示
    insertPageShow: function (objectType, showBoxId, form) {
        if (isNull(objectType)) {
             $('#authList').html('');
         } else {
             jsGetJsonFile("../../json/teamObjectLink.json", function(data) {
                 var authStrList = data[objectType];
                 var str = ``;
                 $.each(authStrList, function(i, item) {
                     var bean = {
                         key: item,
                         name: skyeyeClassEnum[item].name
                     };
                     var authBtnJson = skyeyeClassEnumUtil.getEnumDataListByClassName(item);
                     authBtnJson["bean"] = bean;
                     str += getDataUseHandlebars($('#authTableTemplate').html(), {json: authBtnJson});
                 });
                 $('#authList').html(str);
             });
         }
         form.render();
    }

}
