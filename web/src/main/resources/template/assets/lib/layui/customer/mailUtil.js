// 通讯录工具函数
var mailUtil = {

    mailChooseList: [],

    /**
     * 通讯录选择页面
     *
     * @param callback 回调函数
     */
    openMailChoosePage: function (callback) {
        _openNewWindows({
            url: "../../tpl/mail/mailListChoose.html",
            title: "通讯录选择",
            pageId: "mailListChoose",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                if(typeof(callback) == "function") {
                    callback(mailUtil.mailChooseList);
                }
            }});
    }

};