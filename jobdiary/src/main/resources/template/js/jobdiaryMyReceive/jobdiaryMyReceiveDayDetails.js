
var enclosureList = new Array();

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$;
	    
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "diary004",
		 	params: {rowId:parent.rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/jobdiaryMyReceive/jobdiaryMyReceiveDayDetailsTemplate.tpl'),
		 	ajaxSendAfter:function (json) {
                // 附件回显
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
	});
});