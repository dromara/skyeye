
// 员工证书
layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'textool', 'laydate'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
	    	laydate = layui.laydate,
			textool = layui.textool;

	    laydate.render({elem: '#issueTime', trigger: 'click'});

        laydate.render({elem: '#validityTime', trigger: 'click'});

        // 证书类型
        sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["employeeCertificateType"]["key"], 'select', "typeId", '', form);

		skyeyeEnclosure.init('enclosureUpload');
	    matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
				var params = {
        			certificateNumber: $("#certificateNumber").val(),
 	        		certificateName: $("#certificateName").val(),
 	        		issueOrgan: $("#issueOrgan").val(),
 	        		typeId: $("#typeId").val(),
 	        		issueTime: $("#issueTime").val(),
 	        		validityType: $("#validityType").val(),
 	        		validityTime: $("#validityTime").val(),
	 	        	staffId: parent.staffId,
					enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload')
 	        	};
 	        	AjaxPostUtil.request({url: reqBasePath + "sysstaffcertificate002", params: params, type: 'json', callback: function (json) {
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

	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });

	});
});