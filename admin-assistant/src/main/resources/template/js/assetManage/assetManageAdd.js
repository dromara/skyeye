
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

		// 初始化上传
		$("#assetImg").upload(systemCommonUtil.uploadCommon003Config('assetImg', 27, '', 1));

		// 资产类型
		sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["admAssetType"]["key"], 'select', "typeId", '', form);

		skyeyeEnclosure.init('enclosureUpload');
		matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	var params = {
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

	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});