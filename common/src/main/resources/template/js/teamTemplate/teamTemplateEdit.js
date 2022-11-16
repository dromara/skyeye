
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
	    
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "queryDictTypeMationById",
		 	params: {id: parent.rowId},
		 	pagination: false,
			method: "GET",
		 	template: $("#showTemplate").html(),
			ajaxSendLoadBefore: function(hdb, json){
				json.bean.remark = stringManipulation.textAreaShow(json.bean.remark);
			},
		 	ajaxSendAfter: function (json) {

				skyeyeClassEnumUtil.showEnumDataListByClassName("commonEnable", 'radio', "enabled", json.bean.enabled, form);

				form.on('radio(dictType)', function (data) {
					var val = data.value;
					if (val == 1) {
						$("#chooseLevel").parent().parent().remove();
					} else if (val == 2) {
						$("#dictTypeBox").after(
							`<div class="layui-form-item layui-col-xs6">
								<label class="layui-form-label">可选层级<i class="red">*</i></label>
								<div class="layui-input-block">
									<input type="text" id="chooseLevel" name="chooseLevel" win-verify="required|number" placeholder="请输入可选层级" class="layui-input"/>
									<div class="layui-form-mid layui-word-aux">例如：多级字典为三级，这里设置为2，那么只有二级和二级的所有子层级可以选择</div>
								</div>
							</div>`);
					}
				});

				if (json.bean.dictType == 2) {
					$("#dictTypeBox").after(
						`<div class="layui-form-item layui-col-xs6">
								<label class="layui-form-label">可选层级<i class="red">*</i></label>
								<div class="layui-input-block">
									<input type="text" id="chooseLevel" name="chooseLevel" win-verify="required|number" placeholder="请输入可选层级" class="layui-input" value="${json.bean.chooseLevel}"/>
									<div class="layui-form-mid layui-word-aux">例如：多级字典为三级，这里设置为2，那么只有二级和二级的所有子层级可以选择</div>
								</div>
							</div>`);
				}
				$("input:radio[name=dictType][value=" + json.bean.dictType + "]").attr("checked", true);

				textool.init({eleId: 'remark', maxlength: 200});

				matchingLanguage();
				form.render();
		 	    form.on('submit(formEditBean)', function (data) {
		 	        if (winui.verifyForm(data.elem)) {
						var dictType = $("input[name='dictType']:checked").val();
		 	        	var params = {
							id: parent.rowId,
							dictName: $("#dictName").val(),
							dictCode: $("#dictCode").val(),
							enabled: $("#enabled input:radio:checked").val(),
							dictType: dictType,
							chooseLevel: dictType == 1 ? 1 : $('#chooseLevel').val(),
							remark: $("#remark").val(),
		 	        	};
		 	        	AjaxPostUtil.request({url: reqBasePath + "writeDictTypeMation", params: params, type: 'json', method: "POST", callback: function (json) {
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