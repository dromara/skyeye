layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'jqprint'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    var beanTemplate = $("#beanTemplate").html();
	    
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "erpcommon001",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: beanTemplate,
		 	ajaxSendAfter:function(json){
		 		AjaxPostUtil.request({url: reqBasePath + "login002", params: {}, type: 'json', callback: function(data) {
					if(data.returnCode == 0) {
						$("#orderDetailTitle").html(data.bean.companyName + '调拨订单');
					} else {
						winui.window.msg(data.returnMessage, {icon: 2, time: 2000});
					}
				}});
				if(json.bean.status == 0){
		 			$("#statusName").html("<span class='state-down'>未审核</span>");
		 		}else if(json.bean.status == 1){
		 			$("#statusName").html("<span class='state-up'>审核中</span>");
		 		}else if(json.bean.status == 2){
		 			$("#statusName").html("<span class='state-new'>已调拨</span>");
		 		}else if(json.bean.status == 3){
		 			$("#statusName").html("<span class='state-down'>拒绝通过</span>");
		 		}else if(json.bean.status == 4){
		 			$("#statusName").html("<span class='state-new'>已调拨</span>");
		 		}
		 		matchingLanguage();
		 		form.render();
		 	}
		});
		
		//打印
		$("body").on("click", "#jprint", function(e){
			$("#showForm").jqprint({
				title: sysMainMation.mationTitle,
				debug: false, //如果是true则可以显示iframe查看效果（iframe默认高和宽都很小，可以再源码中调大），默认是false
				importCSS: true, //true表示引进原来的页面的css，默认是true。（如果是true，先会找$("link[media=print]")，若没有会去找$("link")中的css文件）
				printContainer: true, //表示如果原来选择的对象必须被纳入打印（注意：设置为false可能会打破你的CSS规则）。
				operaSupport: true//表示如果插件也必须支持歌opera浏览器，在这种情况下，它提供了建立一个临时的打印选项卡。默认是true
			});
		});
	});
});