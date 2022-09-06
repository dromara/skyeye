
// 任务执行结果
layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
	window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'fileUpload', 'laydate', 'tagEditor'], function(exports) {
	winui.renderColor();
	layui.use(['form'], function(form) {
		var index = parent.layer.getFrameIndex(window.name);
		var $ = layui.$,
			laydate = layui.laydate;
		
		showGrid({
		 	id: "showForm",
		 	url: flowableBasePath + "protask003",
		 	params: {rowId:parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/protask/protaskexecutionTemplate.tpl'),
		 	ajaxSendAfter:function (json) {
		 		//任务说明附件回显
    			var str = "暂无附件";
                if(json.bean.enclosureInfo.length != 0 && !isNull(json.bean.enclosureInfo)){
                	str = "";
                    $.each([].concat(json.bean.enclosureInfo), function(i, item) {
                        str += '<a rowid="' + item.id + '" class="enclosureItem" rowpath="' + item.fileAddress + '" href="javascript:;" style="color:blue;">' + item.name + '</a><br>';
                    });
                }
                $("#instructionsEnclosureUploadBtn").html(str);
				// 执行结果附件回显
				skyeyeEnclosure.initTypeISData({'enclosureUpload': json.bean.executionEnclosureInfo});

				content = json.bean.taskInstructions;
		 		$("#taskInstructionsShowBox").attr("src", "taskinstructionsshow.html");

				var ue = ueEditorUtil.initEditor('executionResult');
			    ue.addListener("ready", function () {
			    	if (!isNull(json.bean.executionResult))
			    		ue.setContent(json.bean.executionResult);
			    });
			    
			    matchingLanguage();
			    form.on('submit(editBean)', function(data) {
					if(winui.verifyForm(data.elem)) {
						var params = {
							rowId: parent.rowId,
							executionResult: encodeURIComponent(ue.getContent()),
							enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload')
						};
						if(isNull(ue.getContent())){
							winui.window.msg('请填写执行结果！', {icon: 2, time: 2000});
							return false;
						}
						AjaxPostUtil.request({url: flowableBasePath + "protask013", params: params, type: 'json', callback: function(j) {
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