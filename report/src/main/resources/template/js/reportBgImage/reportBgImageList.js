
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window',
}).define(['window', 'table', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form;
	
	authBtn('1625280805000');
	
	//初始化数据
    showGrid({
	 	id: "showForm",
	 	url: reportBasePath + "reportbgimage001",
	 	params: {},
	 	pagination: true,
	 	pagesize: 18,
	 	template: getFileContent('tpl/reportBgImage/bgpic-item.tpl'),
	 	ajaxSendLoadBefore: function(hdb){
	 		hdb.registerHelper("compare1", function(v1, options){
				return fileBasePath + v1;
			});
	 	},
	 	options: {'click .del':function(index, row){
				layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function(index){
					layer.close(index);
		            AjaxPostUtil.request({url:reportBasePath + "reportbgimage003", params:{rowId: row.id}, type:'json', method: "DELETE", callback:function(json){
		    			if(json.returnCode == 0){
		    				winui.window.msg("删除成功", {icon: 1,time: 2000});
		    				loadTable();
		    			}else{
		    				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
		    			}
		    		}});
				});
	 		}, 'click .sel':function(index, row){
	 			layer.open({
	        		type:1,
	        		title:false,
	        		closeBtn:0,
					offset:["5vh", "5vw"],
					area:['90vw', '90vh'],
	        		shadeClose:true,
	        		content:'<img src="' + fileBasePath + row.imagePath + '" style="max-width:100%;">',
	        		scrollbar:false
	            });
	 		}
	 	},
	 	ajaxSendAfter:function(json){
	 		authBtn('1625280815182');
			matchingLanguage();
	 	}
    });

	// 添加
	$("body").on("click", "#addBean", function(){
		_openNewWindows({
			url: "../../tpl/reportBgImage/reportBgImageAdd.html",
			title: systemLanguage["com.skyeye.addPageTitle"][languageType],
			pageId: "reportBgImageAdd",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
				if (refreshCode == '0') {
					winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1,time: 2000});
					loadTable();
				} else if (refreshCode == '-9999') {
					winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2,time: 2000});
				}
			}});
	});
	
	//刷新数据
    $("body").on("click", "#reloadTable", function(){
    	loadTable();
    });
    
    function loadTable(){
    	refreshGrid("showForm", {params:{}});
    }
    
    exports('reportBgImageList', {});
});
