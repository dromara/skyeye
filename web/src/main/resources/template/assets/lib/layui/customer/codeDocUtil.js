
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
            if(!isNull(codeDocUtil.tableColumnData[key])){
                str = str.replace(value, codeDocUtil.tableColumnData[key]);
            }else{
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
                }else{
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
            }else{
                javaBeanEntity.push('@ApiModelProperty(value = "' + item.columnComment + '")\n\t' +
                    'private ' + codeDocUtil.getJavaAttr(item.columnType) + ' ' + lowerColumnName + '');
            }
        });
        codeDocUtil.tableColumnData['currentTime'] = getFormatDate();
        codeDocUtil.tableColumnData['column'] = column.join(', ');
        codeDocUtil.tableColumnData['columnJavaStr'] = columnJavaStr.join(', ');
        codeDocUtil.tableColumnData['selectColumn'] = selectColumn.join(',\n\t\t\t');
        codeDocUtil.tableColumnData['updateColumn'] = updateColumn.join(',\n\t\t\t');
        codeDocUtil.tableColumnData['javaBeanEntity'] = javaBeanEntity.join(';\n\t\n\t');
    },

    getJavaAttr: function (columnType){
        if(columnType.indexOf('varchar') >= 0 || columnType.indexOf('datetime') >= 0 || columnType.indexOf('longtext') >= 0
            || columnType.indexOf('decimal') >= 0){
            return 'String';
        } else if(columnType.indexOf('int') >= 0 || columnType.indexOf('bigint') >= 0){
            return 'Integer';
        }
        return 'String';
    }

}
