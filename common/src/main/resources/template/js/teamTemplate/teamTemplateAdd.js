
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'textool'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
			textool = layui.textool;
		textool.init({eleId: 'remark', maxlength: 200});

		skyeyeClassEnumUtil.showEnumDataListByClassName("teamObjectType", 'select', "objectType", '', form);
		skyeyeClassEnumUtil.showEnumDataListByClassName("commonEnable", 'radio', "enabled", '', form);

		loadAuthList();
		form.on('select(objectType)', function(data) {
			loadAuthList();
		});

	    matchingLanguage();
 		form.render();
 	    form.on('submit(formAddBean)', function (data) {
 	        if (winui.verifyForm(data.elem)) {
 	        	var params = {
					dictName: $("#dictName").val(),
					dictCode: $("#dictCode").val(),
					enabled: $("#enabled input:radio:checked").val(),
					remark: $("#remark").val(),
 	        	};
 	        	AjaxPostUtil.request({url: reqBasePath + "writeDictTypeMation", params: params, type: 'json', method: "POST", callback: function (json) {
					parent.layer.close(index);
					parent.refreshCode = '0';
 	        	}});
 	        }
 	        return false;
 	    });

		 function loadAuthList() {
			 var objectType = $('#objectType').val();
			 if (isNull(objectType)) {
				 $('#authList').html('');
			 } else {
				 jsGetJsonFile("../../json/teamObjectLink.json", function(data) {
					 var authStrList = data[objectType];
					 var str = ``;
					 $.each(authStrList, function(i, item) {
						 var bean = {
							 key: item,
							 name: skyeyeClassEnum[item].name
						 };
						 var authBtnJson = skyeyeClassEnumUtil.getEnumDataListByClassName(item);
						 authBtnJson["bean"] = bean;
						 str += getDataUseHandlebars($('#authTableTemplate').html(), {json: authBtnJson});
					 });
					 $('#authList').html(str);
				 });
			 }
			 form.render();
		 }
 	    
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
});