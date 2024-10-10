
// 代码生成器相关工具类
var codeDocUtil = {

    // 替换代码生成器模板对应关系
    replaceModelRelationship: {
        'ControllerPackageName': /[$]{{controllerPackage}}/g,
        'ServicePackageName': /[$]{{servicePackage}}/g,
        'ServiceImplPackageName': /[$]{{serviceImplPackage}}/g,
        'DaoPackageName': /[$]{{daoPackage}}/g,
        'tableZhName': /[$]{{tableName}}/g,
        'tableFirstISlowerName': /[$]{{objectName}}/g,
        'tableISlowerName': /[$]{{urlName}}/g,
        'tableBzName': /[$]{{notesName}}/g,

        // 以下数据从tableColumnData对象中获取
        'currentTime': /[$]{{currentTime}}/g,
        'table': /[$]{{table}}/g,
        'column': /[$]{{column}}/g,
        'columnJavaStr': /[$]{{columnJavaStr}}/g,
        'selectColumn': /[$]{{selectColumn}}/g,
        'updateColumn': /[$]{{updateColumn}}/g,
        'javaBeanEntity': /[$]{{javaBeanEntity}}/g,
        'addFormItem': /[$]{{addFormItem}}/g,
        'editFormItem': /[$]{{editFormItem}}/g,
        'detailsFormItem': /[$]{{detailsFormItem}}/g,
        'submitFormData': /[$]{{submitFormData}}/g,
        'getOrSerMethod': /[$]{{getOrSerMethod}}/g,

        sqlPlaceholder: /[$]{{sqlPlaceholder}}/g,
        updateSqlParams: /[$]{{updateSqlParams}}/g,
        updateSqlParamsLength: /[$]{{updateSqlParamsLength}}/g,

        // JSP相关的
        'jspJavaBeanEntity': /[$]{{jspJavaBeanEntity}}/g,
        'jspGetParamFromRequest': /[$]{{jspGetParamFromRequest}}/g,
        'jspGetParamFromResultSet': /[$]{{jspGetParamFromResultSet}}/g,
        'jspSetParamForResultSet': /[$]{{jspSetParamForResultSet}}/g,
        'jspSetForEntity': /[$]{{jspSetForEntity}}/g,

        'jspAddForm1': /[$]{{jspAddForm1}}/g,
        'jspEditForm1': /[$]{{jspEditForm1}}/g,
        'jspDetailForm1': /[$]{{jspDetailForm1}}/g,
        'jspTableForm1': /[$]{{jspTableForm1}}/g,
        'jspTableValue1': /[$]{{jspTableValue1}}/g,
    },

    // 表参数数据
    tableColumnData: {

    },

    /**
     * 替换代码生成器模板内容
     *
     * @param str
     */
    replaceModelContent: function (str){
        $.each(codeDocUtil.replaceModelRelationship, function (key, value){
            if (!isNull(codeDocUtil.tableColumnData[key])){
                str = str.replace(value, codeDocUtil.tableColumnData[key]);
            } else {
                str = str.replace(value, $("#" + key).val());
            }
        });
        return str;
    },

    /**
     * 设置表参数数据
     *
     * @param columns
     */
    setTableColumnData: function (columns, tableMap){
        var column = []; // 表字段
        var columnJavaStr = []; // 表字段对应的Java属性
        var selectColumn = []; // 查询时的表参数信息
        var updateColumn = []; // 编辑时的表参数信息
        var javaBeanEntity = []; // Java对象属性
        var addFormItem = []; // 新增页面表单项
        var editFormItem = []; // 编辑页面表单项
        var detailsFormItem = []; // 详情页面表单项
        var submitFormData = []; // 提交表单时的数据
        let getOrSerMethod = []; // 获取get/set方法

        let sqlPlaceholder = []; // SQL占位符
        let updateSqlParams = []; // 修改sql语句中的参数

        let jspJavaBeanEntity = []; // JSP页面Java对象属性
        let jspGetParamFromRequest = []; // JSP页面获取请求参数
        let jspGetParamFromResultSet = []; // JSP页面获取数据库结果集
        let jspSetParamForResultSet = []; // JSP页面设置数据库结果集
        let jspSetForEntity = []; // JSP页面设置Java对象属性

        let jspAddForm1 = []; // JSP页面新增表单1
        let jspEditForm1 = []; // JSP页面编辑表单1
        let jspDetailForm1 = []; // JSP页面详情表单1
        let jspTableForm1 = []; // JSP页面获取表格表单1
        let jspTableValue1 = []; // JSP页面获取表格值1

        $.each(columns, function (i, item) {
            codeDocUtil.tableColumnData['table'] = item.tableName;
            var columnName = item.columnName;
            var lowerColumnName = item.lowerColumnName;
            item.upperColumnName = firstLetterUpper(lowerColumnName);
            column.push(columnName);
            columnJavaStr.push('#{' + lowerColumnName + '}');

            if(lowerColumnName == 'create_time' || lowerColumnName == 'last_update_time'){
                selectColumn.push('CONVERT(a.' + columnName + ', char) ' + lowerColumnName);
            } else {
                if(columnName == lowerColumnName){
                    selectColumn.push('a.' + columnName);
                } else {
                    selectColumn.push('a.' + columnName + ' ' + lowerColumnName);
                }
            }

            if(lowerColumnName != 'id') {
                // 编辑的时候，id不可以修改
                updateColumn.push(columnName + ' = #{' + lowerColumnName + '}');
            }

            if('NO' == item.isNullable){
                javaBeanEntity.push('@ApiModelProperty(value = "' + item.columnComment + '", required = "required")\n\t' + codeDocUtil.getPrivateAttr(item));
            } else {
                javaBeanEntity.push('@ApiModelProperty(value = "' + item.columnComment + '")\n\t' + codeDocUtil.getPrivateAttr(item));
            }

            getOrSerMethod.push(codeDocUtil.getOrSetMethod(item));
            jspJavaBeanEntity.push(codeDocUtil.getPrivateAttr(item));

            let sqlPlaceholderStr = codeDocUtil.getSQLPlaceholder(item);
            if (!isNull(sqlPlaceholderStr)) {
                sqlPlaceholder.push(sqlPlaceholderStr);
            }
            updateSqlParams.push(columnName + ' = ?');

            let jspGetParamFromRequestStr = codeDocUtil.getParamsFromRequest(item);
            if (!isNull(jspGetParamFromRequestStr)) {
                jspGetParamFromRequest.push(jspGetParamFromRequestStr);
            }
            let jspGetParamFromResultSetStr = codeDocUtil.getParamsFromResultSet(item, tableMap);
            if (!isNull(jspGetParamFromResultSetStr)) {
                jspGetParamFromResultSet.push(jspGetParamFromResultSetStr);
            }
            let jspSetParamForResultSetStr = codeDocUtil.setParamsFroResultSet(item, i, tableMap);
            if (!isNull(jspSetParamForResultSetStr)) {
                jspSetParamForResultSet.push(jspSetParamForResultSetStr);
            }
            let jspSetForEntityStr = codeDocUtil.getJspSetForEntity(item, tableMap);
            if (!isNull(jspSetForEntityStr)) {
                jspSetForEntity.push(jspSetForEntityStr);
            }

            // 表单
            let jspAddForm1Str = codeDocUtil.getJSPAddForm1(item);
            if (!isNull(jspAddForm1Str)) {
                jspAddForm1.push(jspAddForm1Str);
            }
            let jspEditForm1Str = codeDocUtil.getJSPEditForm1(item, tableMap);
            if (!isNull(jspEditForm1Str)) {
                jspEditForm1.push(jspEditForm1Str);
            }
            let jspDetailForm1Str = codeDocUtil.getJSPDetailForm1(item, tableMap);
            if (!isNull(jspDetailForm1Str)) {
                jspDetailForm1.push(jspDetailForm1Str);
            }
            let jspTableForm1Str = codeDocUtil.getJSPTableForm1(item, tableMap);
            if (!isNull(jspTableForm1Str)) {
                jspTableForm1.push(jspTableForm1Str);
            }
            let jspTableValue1Str = codeDocUtil.getJSPTableValue1(item, tableMap);
            if (!isNull(jspTableValue1Str)) {
                jspTableValue1.push(jspTableValue1Str);
            }

            if(lowerColumnName != 'id' && lowerColumnName != 'createTime' && lowerColumnName != 'createId'
                && lowerColumnName != 'lastUpdateId' && lowerColumnName != 'lastUpdateTime') {
                addFormItem.push(codeDocUtil.getAddFormItem(item.columnComment, lowerColumnName, item.isNullable));
                editFormItem.push(codeDocUtil.getEditFormItem(item.columnComment, lowerColumnName, item.isNullable));
                detailsFormItem.push(codeDocUtil.getDetailsFormItem(item.columnComment, lowerColumnName, item.isNullable));
                submitFormData.push(lowerColumnName + ': $("#' + lowerColumnName + '").val()');
            }
        });
        codeDocUtil.tableColumnData['currentTime'] = getFormatDate();
        codeDocUtil.tableColumnData['column'] = column.join(', ');
        codeDocUtil.tableColumnData['columnJavaStr'] = columnJavaStr.join(', ');
        codeDocUtil.tableColumnData['selectColumn'] = selectColumn.join(',\n\t\t\t');
        codeDocUtil.tableColumnData['updateColumn'] = updateColumn.join(',\n\t\t\t');
        codeDocUtil.tableColumnData['javaBeanEntity'] = javaBeanEntity.join(';\n\t\n\t') + ';';
        codeDocUtil.tableColumnData['addFormItem'] = addFormItem.join('');
        codeDocUtil.tableColumnData['editFormItem'] = editFormItem.join('');
        codeDocUtil.tableColumnData['detailsFormItem'] = detailsFormItem.join('');
        codeDocUtil.tableColumnData['submitFormData'] = submitFormData.join(',\n\t\t\t') + ',';

        codeDocUtil.tableColumnData['sqlPlaceholder'] = sqlPlaceholder.join(', ');
        codeDocUtil.tableColumnData['updateSqlParams'] = updateSqlParams.join(', ');
        codeDocUtil.tableColumnData['updateSqlParamsLength'] = updateSqlParams.length;

        codeDocUtil.tableColumnData['getOrSerMethod'] = getOrSerMethod.join('');
        codeDocUtil.tableColumnData['jspJavaBeanEntity'] = jspJavaBeanEntity.join(';\n\t\n\t') + ';';
        codeDocUtil.tableColumnData['jspGetParamFromRequest'] = jspGetParamFromRequest.join('\n\t\t');
        codeDocUtil.tableColumnData['jspGetParamFromResultSet'] = jspGetParamFromResultSet.join('\n\t\t\t\t');
        codeDocUtil.tableColumnData['jspSetParamForResultSet'] = jspSetParamForResultSet.join('\n\t\t\t');
        codeDocUtil.tableColumnData['jspSetForEntity'] = jspSetForEntity.join('\n\t\t');

        codeDocUtil.tableColumnData['jspAddForm1'] = jspAddForm1.join('\n\t\t\t');
        codeDocUtil.tableColumnData['jspEditForm1'] = jspEditForm1.join('\n\t\t\t');
        codeDocUtil.tableColumnData['jspDetailForm1'] = jspDetailForm1.join('\n\t\t\t');
        codeDocUtil.tableColumnData['jspTableForm1'] = jspTableForm1.join('\n\t\t\t');
        codeDocUtil.tableColumnData['jspTableValue1'] = jspTableValue1.join('\n\t\t\t');
    },

    getJavaAttr: function (columnType){
        if(columnType.indexOf('varchar') >= 0 || columnType.indexOf('datetime') >= 0 || columnType.indexOf('longtext') >= 0
            || columnType.indexOf('decimal') >= 0) {
            return 'String';
        } else if(columnType.indexOf('int') >= 0 || columnType.indexOf('bigint') >= 0){
            return 'Integer';
        }
        return 'String';
    },

    getAddFormItem: function (title, name, isNullable){
        if('NO' == isNullable){
            return '<div class="layui-form-item layui-col-xs12">\n' +
                '                <label class="layui-form-label">' + title + '<i class="red">*</i></label>\n' +
                '                <div class="layui-input-block">\n' +
                '                    <input type="text" id="' + name + '" name="' + name + '" win-verify="required" placeholder="请输入' + title + '" class="layui-input"/>\n' +
                '                </div>\n' +
                '            </div>\n\t\t\t';
        } else{
            return '<div class="layui-form-item layui-col-xs12">\n' +
                '                <label class="layui-form-label">' + title + '</label>\n' +
                '                <div class="layui-input-block">\n' +
                '                    <input type="text" id="' + name + '" name="' + name + '" placeholder="请输入' + title + '" class="layui-input"/>\n' +
                '                </div>\n' +
                '            </div>\n\t\t\t';
        }
    },

    getEditFormItem: function (title, name, isNullable){
        if('NO' == isNullable){
            return '<div class="layui-form-item layui-col-xs12">\n' +
                '                <label class="layui-form-label">' + title + '<i class="red">*</i></label>\n' +
                '                <div class="layui-input-block">\n' +
                '                    <input type="text" id="' + name + '" name="' + name + '" win-verify="required" placeholder="请输入' + title + '" class="layui-input" value="{{' + name + '}}"/>\n' +
                '                </div>\n' +
                '            </div>\n\t\t\t';
        } else{
            return '<div class="layui-form-item layui-col-xs12">\n' +
                '                <label class="layui-form-label">' + title + '</label>\n' +
                '                <div class="layui-input-block">\n' +
                '                    <input type="text" id="' + name + '" name="' + name + '" placeholder="请输入' + title + '" class="layui-input" value="{{' + name + '}}"/>\n' +
                '                </div>\n' +
                '            </div>\n\t\t\t';
        }
    },

    getPrivateAttr: function (item){
        return 'private ' + codeDocUtil.getJavaAttr(item.columnType) + ' ' + item.lowerColumnName;
    },

    getOrSetMethod: function (item){
        return 'public ' + codeDocUtil.getJavaAttr(item.columnType) + ' get' + item.upperColumnName + '() {\n' +
            '\t\treturn this.' + item.lowerColumnName + ';\n' +
            '\t}\n\t\n\t' +
            'public void set' + item.upperColumnName + '(' + codeDocUtil.getJavaAttr(item.columnType) + ' ' + item.lowerColumnName + ') {\n' +
            '\t\tthis.' + item.lowerColumnName + ' = ' + item.lowerColumnName + ';\n' +
            '\t}\n\t\n\t';
    },

    getDetailsFormItem: function (title, name){
        return '<div class="layui-form-item layui-col-xs12">\n' +
            '                <label class="layui-form-label">' + title + '：</label>\n' +
            '                <div class="layui-input-block">\n' +
            '                    {{' + name + '}}\n' +
            '                </div>\n' +
            '            </div>\n\t\t\t';
    },

    getParamsFromRequest: function (item) {
        let noNeed = ['id', 'create_time', 'last_update_time', 'create_id', 'last_update_id'];
        if (noNeed.indexOf(item.columnName) >= 0) {
            return '';
        }
        let javaType = codeDocUtil.getJavaAttr(item.columnType);
        if (javaType == 'String') {
            return `${javaType} ${item.lowerColumnName} = req.getParameter("${item.lowerColumnName}");`;
        } else if (javaType == 'Integer') {
            return `${javaType} ${item.lowerColumnName} = Intege.parseInt(req.getParameter("${item.lowerColumnName}"));`;
        }
        return '';
    },

    getParamsFromResultSet: function (item, tableMap) {
        let javaType = codeDocUtil.getJavaAttr(item.columnType);
        if (javaType == 'String') {
            return `${tableMap.tableFirstISlowerName}.set${item.upperColumnName}(rs.getString("${item.columnName}"));`;
        } else if (javaType == 'Integer') {
            return `${tableMap.tableFirstISlowerName}.set${item.upperColumnName}(rs.getInt("${item.columnName}"));`;
        }
        return '';
    },

    setParamsFroResultSet: function (item, i, tableMap) {
        let javaType = codeDocUtil.getJavaAttr(item.columnType);
        if (javaType == 'String') {
            return `ps.setString(${i + 1}, ${tableMap.tableFirstISlowerName}.get${item.upperColumnName}());`;
        } else if (javaType == 'Integer') {
            return `ps.setInt(${i + 1}, ${tableMap.tableFirstISlowerName}.get${item.upperColumnName}());`;
        }
        return '';
    },

    getJspSetForEntity: function (item, tableMap) {
        let noNeed = ['id', 'create_time', 'last_update_time', 'create_id', 'last_update_id'];
        if (noNeed.indexOf(item.columnName) >= 0) {
            return '';
        }
        return `${tableMap.tableFirstISlowerName}.set${item.upperColumnName}(${item.lowerColumnName})`;
    },

    getSQLPlaceholder: function (item) {
        return `?`;
    },

    getJSPAddForm1: function (item) {
        let noNeed = ['id', 'create_time', 'last_update_time', 'create_id', 'last_update_id'];
        if (noNeed.indexOf(item.columnName) >= 0) {
            return '';
        }
        return `<tr>
                <td width="92">${item.columnComment}：</td>
                <td width="386">
                    <input type="text" name="${item.lowerColumnName}" id="${item.lowerColumnName}" />
                </td>
            </tr>`;
    },
    getJSPEditForm1: function (item, tableMap) {
        let noNeed = ['id', 'create_time', 'last_update_time', 'create_id', 'last_update_id'];
        if (noNeed.indexOf(item.columnName) >= 0) {
            return '';
        }
        return `<tr>
                <td width="92">${item.columnComment}：</td>
                <td width="386">
                    <input type="text" name="${item.lowerColumnName}" id="${item.lowerColumnName}" value="${tableMap.tableFirstISlowerName}.${item.lowerColumnName}"/>
                </td>
            </tr>`;
    },
    getJSPDetailForm1: function (item, tableMap) {
        let noNeed = ['id', 'create_time', 'last_update_time', 'create_id', 'last_update_id'];
        if (noNeed.indexOf(item.columnName) >= 0) {
            return '';
        }
        return `<tr>
                <td width="92">${item.columnComment}：</td>
                <td width="386">
                    <%=${tableMap.tableFirstISlowerName}.get${item.upperColumnName}() %>
                </td>
            </tr>`;
    },
    getJSPTableForm1: function (item, tableMap) {
        let noNeed = ['id', 'create_time', 'last_update_time', 'create_id', 'last_update_id'];
        if (noNeed.indexOf(item.columnName) >= 0) {
            return '';
        }
        return `<td align="center">${item.columnComment}</td>`;
    },
    getJSPTableValue1: function (item, tableMap) {
        let noNeed = ['id', 'create_time', 'last_update_time', 'create_id', 'last_update_id'];
        if (noNeed.indexOf(item.columnName) >= 0) {
            return '';
        }
        return `<td align="center"><%=${tableMap.tableFirstISlowerName}.get${item.upperColumnName}() %></td>`;
    },

}
