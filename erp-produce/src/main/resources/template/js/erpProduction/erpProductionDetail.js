
// 加工单id
var rowId = "";

layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'jqprint'], function (exports) {
    winui.renderColor();
    layui.use(['form'], function (form) {
        var index = parent.layer.getFrameIndex(window.name);
        var $ = layui.$;
        
        AjaxPostUtil.request({url: flowableBasePath + "erpproduction006", params: {orderId: parent.rowId}, type: 'json', callback: function (json) {
            if (json.returnCode == 0) {
            	json.bean.stateName = getStateName(json.bean);
            	$.each(json.bean.procedureMationList, function(i, item){
            		if(item.state == 1){
            			item.stateName = "<span class='state-down'>待下达</span>";
            		}else if(item.state == 2){
            			item.stateName = "<span class='state-up'>已下达</span>"
            		}
            	});
            	
            	$.each(json.bean.childList, function(i, item){
            		item.needsNum = parseInt(item.needNum) * parseInt(json.bean.number);
            	});
                $("#showForm").html(getDataUseHandlebars($("#mainHtml").html(), json));

				$("#contentIframe").attr("src", "../../tpl/erpcommon/erpOrderFlowLine.html?rowId=" + json.bean.id + "&type=2");
                matchingLanguage();
				form.render();
            } else {
                winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
            }
        }});
        
        function getStateName(d){
        	if(d.state == 1){
        		return "<span class='state-new'>" + d.stateName + "</span>";
        	}else if(d.state == 2){
        		return "<span>" + d.stateName + "</span>";
        	}else if(d.state == 3){
        		return "<span class='state-up'>" + d.stateName + "</span>";
        	}else if(d.state == 4){
        		return "<span class='state-down'>" + d.stateName + "</span>";
        	}else if(d.state == 5){
        		return "<span class='state-up'>" + d.stateName + "</span>";
        	} else {
        		return "参数错误";
        	}
        }
        
        // 打印
		$("body").on("click", "#jprint", function (e) {
			$("#showForm").jqprint({
				title: sysMainMation.mationTitle,
				debug: false, // 如果是true则可以显示iframe查看效果（iframe默认高和宽都很小，可以再源码中调大），默认是false
				importCSS: true, // true表示引进原来的页面的css，默认是true。（如果是true，先会找$("link[media=print]")，若没有会去找$("link")中的css文件）
				printContainer: true, // 表示如果原来选择的对象必须被纳入打印（注意：设置为false可能会打破你的CSS规则）。
				operaSupport: true // 表示如果插件也必须支持歌opera浏览器，在这种情况下，它提供了建立一个临时的打印选项卡。默认是true
			});
		});
		
		// 详情
		$("body").on("click", ".machinDetails", function (e) {
			rowId = $(this).attr("rowId");
			_openNewWindows({
				url: "../../tpl/erpMachin/erpMachinDetails.html", 
				title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
				pageId: "erpMachinDetails",
				area: ['90vw', '90vh'],
				callBack: function(refreshCode){
				}});
		});
        
    });
});