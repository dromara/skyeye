
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form'], function (exports) {
	winui.renderColor();
	
	var $ = layui.$,
		form = layui.form;
	
	//加载一级分类
	showGrid({
	 	id: "sysFirstType",
	 	url: reqBasePath + "sysdevelopdoc020",
	 	params: {},
	 	pagination: false,
	 	template: getFileContent('tpl/sysdevelopdoc/firsttypeTemplate.tpl'),
	 	ajaxSendLoadBefore: function(hdb){
	 		hdb.registerHelper("compare1", function(index){
	 			if(index === 0){
	 				return 'active';
	 			}else{
	 				return '';
	 			}
			});
	 	},
	 	ajaxSendAfter:function(json){
	 		initSecond();
	 	}
	});
	
	//加载二级分类
	function initSecond(){
		showGrid({
		 	id: "sysSecondType",
		 	url: reqBasePath + "sysdevelopdoc021",
		 	params: {parentId: $("#sysFirstType").find("li[class='active']").find("a").attr("rowid")},
		 	pagination: false,
		 	template: getFileContent('tpl/sysdevelopdoc/secondtypeTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 		hdb.registerHelper("compare1", function(index){
		 			if(index === 0){
		 				return 'active';
		 			}else{
		 				return '';
		 			}
				});
		 	},
		 	ajaxSendAfter:function(json){
		 		initDocList();
		 	}
		});
	}
	
	//加载文档列表
	function initDocList(){
		showGrid({
		 	id: "showDocList",
		 	url: reqBasePath + "sysdevelopdoc022",
		 	params: {parentId: $("#sysSecondType").find("li[class='active']").find("a").attr("rowid")},
		 	pagination: false,
		 	template: getFileContent('tpl/sysdevelopdoc/doclist.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 		hdb.registerHelper("compare1", function(index){
		 			if(index === 0){
		 				return 'active';
		 			}else{
		 				return '';
		 			}
				});
		 	},
		 	ajaxSendAfter:function(json){
		 		initDate();
		 	}
		});
	}
	
	matchingLanguage();
	form.render();
	//初始化文档内容
	function initDate(){
		if(!isNull($("#showDocList").find("dd[class='active']").find("a").attr("rowid"))){
			AjaxPostUtil.request({url:reqBasePath + "sysdevelopdoc023", params:{rowId: $("#showDocList").find("dd[class='active']").find("a").attr("rowid")}, type:'json', callback:function(json){
				if(json.returnCode == 0){
					$('#contentDiv').empty();
					$('#contentDiv').append('<textarea id="content" style="display:none;" placeholder="markdown语言"></textarea>');
					$("#content").val(json.bean.content);
					editormd.markdownToHTML("contentDiv", {
	                    htmlDecode      : "style,script,iframe",
	                    emoji           : true,  // 解析表情
	                    taskList        : true,  // 解析列表
	                    tex             : true,  // 默认不解析
	                    flowChart       : true,  // 默认不解析
	                    sequenceDiagram : true  // 默认不解析
	                });
				}else{
					winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
				}
			}});
		}
	}
    
    //一级分类点击事件
    $("#sysFirstType").on('click', "a", function(e){
    	if(!$(this).parent().hasClass("active")){
	    	$('#contentDiv').empty();
	    	$("#sysFirstType").find("li").removeClass("active");
	    	$(this).parent().addClass("active");
	    	initSecond();
    	}
    });
    
    //二级分类点击事件
    $("#sysSecondType").on('click', "a", function(e){
    	if(!$(this).parent().hasClass("active")){
	    	$('#contentDiv').empty();
	    	$("#sysSecondType").find("li").removeClass("active");
	    	$(this).parent().addClass("active");
	    	initDocList();
    	}
    });
    
    //文档点击
    $("#showDocList").on('click', "a", function(e){
    	if(!$(this).parent().hasClass("active")){
	    	$("#showDocList").find("dd").removeClass("active");
	    	$(this).parent().addClass("active");
	    	initDate();
    	}
    });
    
    exports('sysdevelodoc', {});
});
