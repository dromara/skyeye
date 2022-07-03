// 财务模块工具类
var sysIfsUtil = {

    /**
     * 会计科目选择页面
     *
     * @param callback 回调函数
     */
    chooseAccountSubjectMation: {}, // 已经选择的会计科目信息
    openSysAccountSubjectChoosePage: function (callback) {
        _openNewWindows({
            url: "../../tpl/ifsAccountSubject/ifsAccountSubjectListChoose.html",
            title: "会计科目选择",
            pageId: "ifsAccountSubjectListChoose",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                if (typeof (callback) == "function") {
                    callback(sysIfsUtil.chooseAccountSubjectMation);
                }
            }
        });
    },

    /**
     * 凭证选择页面
     *
     * @param callback 回调函数
     */
    chooseVoucherMation: {}, // 已经选择的凭证信息
    openIfsVoucherChoosePage: function (callback) {
        _openNewWindows({
            url: "../../tpl/ifsVoucher/ifsVoucherListChoose.html",
            title: "凭证选择",
            pageId: "ifsVoucherListChoose",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                if (typeof (callback) == "function") {
                    callback(sysIfsUtil.chooseVoucherMation);
                }
            }
        });
    },

    /**
     * 账套选择页面
     *
     * @param callback 回调函数
     */
    ifsSetOfBooksMation: {},
    openIfsSetOfBooksListChoosePage: function (callback) {
        _openNewWindows({
            url: "../../tpl/ifsSetOfBooks/ifsSetOfBooksListChoose.html",
            title: "账套选择",
            pageId: "ifsSetOfBooksListChoose",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                if (typeof (callback) == "function") {
                    callback(sysIfsUtil.ifsSetOfBooksMation);
                }
            }
        });
    },

    // 余额方向
    amountDirection: [
        {"id": "1", "name": "借"},
        {"id": "2", "name": "贷"}
    ],
    getAmountDirectionById: function (id) {
        return getInPoingArr(sysIfsUtil.amountDirection, "id", id, "name");
    }

}