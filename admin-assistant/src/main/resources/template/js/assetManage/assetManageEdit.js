
// 资产信息
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'fileUpload'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;

	    showGrid({
		 	id: "showForm",
		 	url: flowableBasePath + "asset004",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: $("#beanTemplate").html(),
		 	ajaxSendLoadBefore: function(hdb) {
				hdb.registerHelper("compare1", function (v1, options) {
					if (isNull(v1)) {
						return path + "assets/img/uploadPic.png";
					} else {
						return basePath + v1;
					}
				});
		 	},
		 	ajaxSendAfter: function (json) {
				// 资产类型
				sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["admAssetType"]["key"], 'select', "typeId", json.bean.typeId, form);

				// 初始化上传
				$("#assetImg").upload(systemCommonUtil.uploadCommon003Config('assetImg', 6, json.bean.assetImg, 1));

				// 附件回显
				skyeyeEnclosure.initTypeISData({'enclosureUpload': json.bean.enclosureInfo});

    			matchingLanguage();
		 		form.render();
		 	    form.on('submit(formEditBean)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
		 	        	var params = {
	 	        			id: parent.rowId,
							assetName: $("#assetName").val(),
							numberPrefix: $("#numberPrefix").val(),
							specifications: $("#specifications").val(),
							readPrice: $("#readPrice").val(),
							describe: $("#describe").val(),
							typeId: $("#typeId").val(),
							enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload'),
							assetImg: $("#assetImg").find("input[type='hidden'][name='upload']").attr("oldurl")
						};
						if (isNull(params.assetImg)) {
							winui.window.msg('请上传资产图片', {icon: 2, time: 2000});
							return false;
						}
	 	 	        	AjaxPostUtil.request({url: flowableBasePath + "writeAssetMation", params: params, type: 'json', method: "POST", callback: function (json) {
							parent.layer.close(index);
							parent.refreshCode = '0';
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