
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
    openSysMemberChoosePage: function (callback){
        _openNewWindows({
            url: "../../tpl/member/memberSearchChoose.html",
            title: "选择会员",
            pageId: "memberSearchChoose",
            area: ['90vw', '90vh'],
            callBack: function(refreshCode){
                if (refreshCode == '0') {
                    if(typeof(callback) == "function") {
                        callback(sysMemberUtil.memberMation);
                    }
                } else if (refreshCode == '-9999') {
                    winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
                }
            }});
    },

}