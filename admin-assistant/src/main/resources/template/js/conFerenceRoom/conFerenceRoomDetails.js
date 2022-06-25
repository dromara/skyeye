var enclosureList = new Array();

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
		 	url: flowableBasePath + "conferenceroom007",
		 	params: {rowId: parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/conFerenceRoom/conFerenceRoomDetailsTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function(json){
		 		$("#roomImg").attr("src", fileBasePath + json.bean.roomImg);
		 		//附件回显
			    if(json.bean.enclosureInfo.length != 0 && json.bean.enclosureInfo != ""){
			    	enclosureList = json.bean.enclosureInfo;
			    	var str = "";
	    			$.each([].concat(enclosureList), function(i, item){
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
	    
	    $("body").on("click", "#roomImg", function() {
	    	var src = $(this).attr("src");
	    	layer.open({
        		type:1,
        		title:false,
        		closeBtn:0,
        		skin: 'demo-class',
        		shadeClose:true,
        		content:'<img src="' + src + '" style="max-height:600px;max-width:100%;">',
        		scrollbar:false
            });
	    });
	});
});