// 通讯录工具函数
var mailUtil = {

    mailChooseList: [],

    /**
     * 通讯录选择页面
     *
     * @param callback 回调函数
     */
    openMailChoosePage: function (callback){
        _openNewWindows({
            url: "../../tpl/mail/mailListChoose.html",
            title: "通讯录选择",
            pageId: "mailListChoose",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                if (refreshCode == '0') {
                    if(typeof(callback) == "function") {
                        callback(mailUtil.mailChooseList);
                    }
                } else if (refreshCode == '-9999') {
                    winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
            }});
    }

};