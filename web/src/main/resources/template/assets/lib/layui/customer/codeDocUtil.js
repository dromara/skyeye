
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
    setTableColumnData: function (columns){
        var column = []; // 表字段
        var columnJavaStr = []; // 表字段对应的Java属性
        var selectColumn = []; // 查询时的表参数信息
        var updateColumn = []; // 编辑时的表参数信息
        var javaBeanEntity = []; // Java对象属性
        var addFormItem = []; // 新增页面表单项
        var editFormItem = []; // 编辑页面表单项
        var detailsFormItem = []; // 详情页面表单项
        var submitFormData = []; // 提交表单时的数据
        $.each(columns, function (i, item) {
            codeDocUtil.tableColumnData['table'] = item.tableName;
            var columnName = item.columnName;
            var lowerColumnName = item.lowerColumnName;
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
                javaBeanEntity.push('@ApiModelProperty(value = "' + item.columnComment + '", required = "required")\n\t' +
                    'private ' + codeDocUtil.getJavaAttr(item.columnType) + ' ' + lowerColumnName + '');
            } else {
                javaBeanEntity.push('@ApiModelProperty(value = "' + item.columnComment + '")\n\t' +
                    'private ' + codeDocUtil.getJavaAttr(item.columnType) + ' ' + lowerColumnName + '');
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

    getDetailsFormItem: function (title, name){
        return '<div class="layui-form-item layui-col-xs12">\n' +
            '                <label class="layui-form-label">' + title + '：</label>\n' +
            '                <div class="layui-input-block">\n' +
            '                    {{' + name + '}}\n' +
            '                </div>\n' +
            '            </div>\n\t\t\t';
    }

}
