
// 菜单按钮相关工具类
var indexMenu = {


    getUrlPath: function (){

    },

    /**
     * 传统模式界面下加载菜单信息
     *
     * @param dataMenu 菜单对象
     * @param icon 菜单图标
     */
    loadTraditionPage: function (dataMenu, icon){
        if(isNull(icon)){
            icon = "";
        }
        // 这时会判断右侧#LAY_app_tabsheader属性下的有lay-id属性的li的数目，即已经打开的tab项数目
        if($("#LAY_app_tabsheader li[lay-id]").length <= 0) {
            // 如果比零小，则直接打开新的tab项
            active.tabAdd(dataMenu.attr("data-url"), dataMenu.attr("data-id"), icon + dataMenu.attr("data-title"));
        } else {
            // 否则判断该tab项是否以及存在
            var isData = false; // 初始化一个标志，为false说明未打开该tab项 为true则说明已有
            $.each($("#LAY_app_tabsheader li[lay-id]"), function() {
                // 如果点击左侧菜单栏所传入的id 在右侧tab项中的lay-id属性可以找到，则说明该tab项已经打开
                if($(this).attr("lay-id") === dataMenu.attr("data-id")) {
                    isData = true;
                }
            })
            if(isData == false) {
                // 标志为false 新增一个tab项
                active.tabAdd(dataMenu.attr("data-url"), dataMenu.attr("data-id"), icon + dataMenu.attr("data-title"));
            }
        }
        // 最后不管是否新增tab，最后都转到要打开的选项页面上
        active.tabChange(dataMenu.attr("data-id"));
    }

}

