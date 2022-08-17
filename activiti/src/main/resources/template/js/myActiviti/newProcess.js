
var actKey = "";
var dsFormId = "";

/**
 * 新建流程
 */
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form'], function (exports) {
    winui.renderColor();
    var $ = layui.$,
        form = layui.form;
    var beanTemplate = $("#beanTemplate").html();

    showGrid({
        id: "showForm",
        url: flowableBasePath + "actmodletype019",
        params: {},
        pagination: false,
        template: beanTemplate,
		ajaxSendLoadBefore: function(hdb){
			// 展示图标
			hdb.registerHelper("compare1", function(v1, v2, v3, v4, options){
				var str = "";
				if(v1 == 1){
					// icon
					str = '<i class="fa fa-fw ' + v3 + '" style="font-size: 18px; line-height: 30px; width: 100%; color: ' + v4 + '"></i>';
				} else {
					str = '<img src="' + reqBasePath + v2 + '" class="photo-img" style="width: 25px; height: 25px">';
				}
				return str;
			});

			// 是否常用/热门的标识
			hdb.registerHelper("compare2", function(v1, options){
				var str = "";
				if(v1 == 1){
					str = '<img src="../../assets/images/hot-forum.png" style="width: 16px; height: 16px;" title="常用/热门">';
				}
				return str;
			});
		},
        ajaxSendAfter:function (json) {
        	$('.task-launch-item').hover(function() {
				var obj = $(this);
				bkIn(obj);
			}, function() {
				var obj = $(this);
				bkOut(obj);
			});
			matchingLanguage();
        }
    });

    $("body").on("click", ".launchTask", function (e) {
        var title = $(this).attr("showTitle");
        var url = $(this).attr("pageUrl");
        dsFormId = $(this).attr("dsFormId");
        actKey = $(this).attr("actid");
        _openNewWindows({
            url: url,
            title: title,
            pageId: "openLaunchTaskPage",
			area: ['100vw', '100vh'],
            callBack: function (refreshCode) {
				winui.window.msg("提交成功", {icon: 1, time: 2000});
            }});
    });
    
    //边框效果--移入
	function bkIn(obj){
		var height = $(obj).height();
		var width = $(obj).width();
	    $(obj).find('.task-launch-item-bk_1').stop(true).animate({
	        height: height + 'px'
	    },300)
	    $(obj).find('.task-launch-item-bk_2').stop(true).delay(300).animate({
	        width: width + 'px'
	    },300)
	    $(obj).find('.task-launch-item-bk_3').stop(true).animate({
	        height: height + 'px'
	    },300)
	    $(obj).find('.task-launch-item-bk_4').stop(true).delay(300).animate({
	        width: width + 'px'
	    },300)
	}
	//边框效果--移出
	function bkOut(obj){
	    $(obj).find('.task-launch-item-bk_1').stop(true).delay(100).animate({
	        height:'0px'
	    },100)
	    $(obj).find('.task-launch-item-bk_2').stop(true).animate({
	        width:'0px'
	    },100)
	    $(obj).find('.task-launch-item-bk_3').stop(true).delay(100).animate({
	        height:'0px'
	    },100)
	    $(obj).find('.task-launch-item-bk_4').stop(true).animate({
	        width:'0px'
	    },100)
	}
	
    exports('newProcess', {});
});
