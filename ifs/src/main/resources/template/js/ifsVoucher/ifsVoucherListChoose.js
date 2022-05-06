
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form;
	
    showGrid({
	 	id: "showForm",
	 	url: flowableBasePath + "ifsVoucher001",
	 	params: getTableParams(),
	 	pagination: true,
	 	pagesize: 18,
	 	template: $("#beanTemplate").html(),
	 	ajaxSendLoadBefore: function(hdb){
	 		hdb.registerHelper("compare1", function(v1, options){
	 			var fileExt = sysFileUtil.getFileExt(v1);
	 			if($.inArray(fileExt[0], imageType) >= 0){
					return fileBasePath + v1;
				}
	 			return '../../assets/images/doc.png';
			});
			hdb.registerHelper("compare2", function(v1, options){
				if(v1 == 1){
					return '<a class="layui-btn layui-btn-xs choose">选择</a>';
				}
				return '';
			});
			hdb.registerHelper("compare3", function(v1, options){
				if(v1 == 1){
					return "<span class='state-down'>未整理</span>";
				}
				return "<span class='state-up'>已整理</span>";
			});
	 	},
	 	options: {'click .sel': function(index, row){
				var fileExt = sysFileUtil.getFileExt(row.voucherPath);
				if($.inArray(fileExt[0], imageType) >= 0){
					systemCommonUtil.showPicImg(fileBasePath + row.voucherPath);
				}else{
					sysFileUtil.download(row.voucherPath, row.fileName);
				}
	 		},
			'click .choose': function(i, row){
				parent.sysIfsUtil.chooseVoucherMation = row;
				parent.refreshCode = '0';
				parent.layer.close(index);
			}
	 	},
	 	ajaxSendAfter:function(json){
	 		matchingLanguage();
	 	}
    });

	form.render();
	form.on('submit(formSearch)', function (data) {
		if (winui.verifyForm(data.elem)) {
			refreshGrid("showForm", {params: getTableParams()});
		}
		return false;
	});

	// 刷新数据
	$("body").on("click", "#reloadTable", function(){
		loadTable();
	});
    
    function loadTable(){
    	refreshGrid("showForm", {params: getTableParams()});
    }

	function getTableParams(){
		return {
			state: $("#state").val(),
		};
	}
    
    exports('ifsVoucherListChoose', {});
});
