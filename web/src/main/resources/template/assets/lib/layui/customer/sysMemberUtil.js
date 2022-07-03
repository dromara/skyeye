// 会员工具类
var sysMemberUtil = {

    /**
     * 已经选的的会员信息
     */
    memberMation: {},

    /**
     * 会员选择页面
     *
     * @param callback 回调函数
     */
    openSysMemberChoosePage: function (callback) {
        _openNewWindows({
            url: "../../tpl/member/memberSearchChoose.html",
            title: "选择会员",
            pageId: "memberSearchChoose",
            area: ['90vw', '90vh'],
            callBack: function (refreshCode) {
                if (typeof (callback) == "function") {
                    callback(sysMemberUtil.memberMation);
                }
            }
        });
    },

}