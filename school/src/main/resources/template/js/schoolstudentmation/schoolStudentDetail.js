
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$
	    
		showGrid({
		 	id: "showForm",
		 	url: schoolBasePath + "studentmation008",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: $("#detailTemplate").html(),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function (json) {
		 		
		 		if(!isNull(json.bean.userPhoto)){
			 		$("#userPhoto").attr("src", fileBasePath + json.bean.userPhoto);
		 		} else {
		 			$("#userPhoto").attr("src", "../../assets/images/no-userphoto.png");
		 		}
		 		
		        if(json.bean.preschoolEducation != 1){
		        	$("#preschoolSchoolBox").hide();
		        }
		        matchingLanguage();
		 	}
	    });
	    
	});
	    
});