
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'colorpicker', 'fileUpload'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    var colorpicker = layui.colorpicker;
	    var selOption = getFileContent('tpl/template/select-option.tpl');

	    // 加载图标信息
		systemCommonUtil.initIconChooseHtml('iconMation', form, colorpicker, 12);

        matchingLanguage();
 		form.render();
 		
 		// 桌面信息
		systemCommonUtil.getSysDesttop(function (json) {
			$("#desktop").html(getDataUseHandlebars(selOption, json));
			form.render('select');
		});

		// 所属系统
		showGrid({
			id: "sysWinId",
			url: reqBasePath + "querySysEveWinList",
			params: {},
			pagination: false,
			method: "GET",
			template: selOption,
			ajaxSendLoadBefore: function(hdb) {},
			ajaxSendAfter:function (json) {
				form.render('select');
			}
		});

 		// 菜单类型变化事件
 		form.on('radio(level)', function (data) {
 			var val = data.value;
			if (val == 0) {
				$("#lockParentSel").html("");
				$("#parentIdBox").addClass("layui-hide");
			} else if (val == 1) {
				$("#parentIdBox").removeClass("layui-hide");
				loadChildMenu();
			}
			form.render('select');
        });
 		
 	    form.on('submit(formAddMenu)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
				var level = $("input[name='level']:checked").val();
 	        	var params = {
					name: $("#name").val(),
					sysWinId: $("#sysWinId").val(),
					desktopId: $("#desktop").val(),
					pageUrl: $("#pageUrl").val(),
					type: $("input[name='type']:checked").val(),
					level: level,
					parentId: level == 0 ? "0" : $("#menuParent").val(),
					sysType: $("input[name='sysType']:checked").val(),
					isShare: $("input[name='isShare']:checked").val(),
 	        	};

				// 获取图标信息
				params = systemCommonUtil.getIconChoose(params);
				if (!params["iconChooseResult"]) {
					return false;
				}
 	        	AjaxPostUtil.request({url: reqBasePath + "writeMenu", params: params, type: 'json', method: 'POST', callback: function(json) {
					parent.layer.close(index);
					parent.refreshCode = '0';
	 	   		}});
 	        }
 	        return false;
 	    });
 	    
 	    // 加载一级菜单
 	    function loadChildMenu() {
 	    	AjaxPostUtil.request({url: reqBasePath + "sys009", params: {parentId: '0'}, type: 'json', method: 'GET', callback: function (json) {
				var str = '<select id="menuParent" lay-filter="selectParent" win-verify="required" lay-search=""><option value="">请选择</option>';
				for(var i = 0; i < json.rows.length; i++){
					str += '<option value="' + json.rows[i].id + '">' + json.rows[i].sysDesktop.name + '---------' + json.rows[i].name + '</option>';
				}
				str += '</select>';
				$("#lockParentSel").append(str);
 	   		}, async: false});
 	    }
 	    
 	    // 取消
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	    
	});
});