
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
		 	url: flowableBasePath + "licence006",
		 	params: {rowId:parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/licenceManage/licenceManageDetailsTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function(json){
		 		if(json.bean.annualReview == "否"){
		 			$("#nextTime").addClass('layui-hide');
		 		}
		 		if(json.bean.termOfValidity == "永久"){
		 			$("#termTime").addClass('layui-hide');
		 		}
		 		// 附件回显
			    if(json.bean.enclosureInfo.length != 0 && json.bean.enclosureInfo != ""){
			    	var str = "";
	    			$.each([].concat(json.bean.enclosureInfo), function(i, item){
	    				str += '<a rowid="' + item.id + '" class="enclosureItem" rowpath="' + item.fileAddress + '" href="javascript:;" style="color:blue;">' + item.name + '</a><br>';
	    			});
	    			$("#enclosureUploadBtn").html(str);
		        }
		        matchingLanguage();
		 	}
		});
	    
	    $("body").on("click", ".enclosureItem", function() {
	    	download(fileBasePath + $(this).attr("rowpath"), $(this).html());
	    });
	});
});