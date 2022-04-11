
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
    },

    /**
     * 替换代码生成器模板内容
     *
     * @param str
     */
    replaceModelContent: function (str){
        $.each(codeDocUtil.replaceModelRelationship, function (key, value){
            str = str.replace(value, $("#" + key).val());
        });
        return str;
    }

}
