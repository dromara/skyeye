/*!
 * BaseDataSource.
 */
(function($, window, document, undefined) {
    'use strict';
    var BaseDataSource = function() {

    };

    BaseDataSource.prototype.getDictList = function(code) {
        var dict = null;
        var obj = {
            "code" : code
        };
        $.ajax({
            type : "post",
            async : false,
            url : basePath+"/dict/getDictsByCode",
            data : obj,
            dataType : "json",
            success : function(data) {
                dict = data;
            },
            error : function() {
                modals.error("字典" + code + "获取异常,请检查");
            }
        });
        return dict;
    };

    /**
     * dictCode 字典编码 callback 回调函数
     */
    BaseDataSource.prototype.getDict = function(dictCode, callback) {
        if (!dictCode)
            return null;
        var dict = this.getDictList(dictCode);
        if (dict && dict.length > 0&&callback) {
            callback(dict);
        }
    };

    BaseDataSource.prototype.getDataByUrl = function(url, callback) {
        if (!url)
            return null;
        var data = this.getData(url);
        if (data&&data.length>0&&callback) {
            callback(data);
        } 
    };
   

    BaseDataSource.prototype.getData = function(url) {
        if(!url.startWith(basePath))
            url=basePath+url;
        var dict = null;
        $.ajax({
            type : "post",
            async : false,
            url : url,
            data : null,
            dataType : "json",
            success : function(data) {
                dict = data;
            },
            error : function() {
                modals.error("获取数据异常，请检查"+url+"方法");
            }
        });
        return dict;
    };

    window.$dataSource = new BaseDataSource();

})(jQuery, window, document);
