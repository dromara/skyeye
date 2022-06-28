
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;

	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "wagesstaff002",
		 	params: {staffId: parent.rowId},
		 	pagination: false,
			method: "GET",
		 	template: $("#showBaseTemplate").html(),
		 	ajaxSendLoadBefore: function(hdb){
				hdb.registerHelper('compare1', function(v1, v2, options) {
					if(!isNull(v1) && !isNull(v2) && v1 != 0 && v2 != 0){
						return '建议范围：' + v1 + ' ~ ' + v2;
					}
					return '';
				});
		 	},
		 	ajaxSendAfter:function (json) {
		 		// 加载员工信息
				$("#staffMation").html(getUserStaffHtmlMationByStaffId(parent.rowId));
				matchingLanguage();
				form.render();
		 	    form.on('submit(formEditBean)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	if(json.rows.length == 0){
							winui.window.msg('请填写薪资', {icon: 2, time: 2000});
							return false;
						}
						var tableData = new Array();
		 	        	var actMoney = 0;
						$.each(json.rows, function(i, item) {
							var row = {
								fieldKey: item.fieldKey,
								amountMoney: $("#" + item.fieldKey).val(),
								staffId: parent.rowId
							};
							actMoney = sum(actMoney, $("#" + item.fieldKey).val());
							tableData.push(row);
						});
						var params = {
							fieldStr: JSON.stringify(tableData),
							staffId: parent.rowId,
							actMoney: actMoney
		 	        	};
		 	        	AjaxPostUtil.request({url: reqBasePath + "wagesstaff003", params: params, type: 'json', method: "POST", callback: function (json) {
		 	        		if (json.returnCode == 0) {
		 	        			parent.layer.close(index);
		 	        			parent.refreshCode = '0';
		 	        		} else {
		 	        			winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
		 	        		}
		 	        	}});
		 	        }
		 	        return false;
		 	    });
		 	}
		});

	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});