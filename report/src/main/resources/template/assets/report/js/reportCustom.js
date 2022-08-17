

var skyeyeReportUtil = {

    /**
     * 获取报表编辑器支持的类型
     *
     * @param showDomId 展示的bom结构id
     * @param template 模板
     * @param callBack 回调函数
     */
    getReportEditorType: function(showDomId, template, callBack){
        $.getJSON("../../assets/report/json/skyeyeEditor.json", function (data) {
            var rows = new Array();
            $.each(data, function (key, value){
                rows.push({
                    id: key,
                    name: value.name
                });
            });
            if (!isNull(showDomId)){
                $("#" + showDomId).html(getDataUseHandlebars(template, {rows: rows}));
            }
            if(typeof(callBack) == "function") {
                callBack(rows);
            }
        });
    }

};

