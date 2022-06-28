
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
	 	url: reqBasePath + "sysevewintype012",
	 	params: {},
	 	pagination: false,
	 	template: getFileContent('tpl/appstore/firsttypeTemplate.tpl'),
	 	ajaxSendLoadBefore: function(hdb){
	 	},
	 	ajaxSendAfter:function (json) {
	 	}
	});
	
	initDate();
	function initDate(){
		var secondType = "";
		$.each($('#sysSecondType input:checkbox:checked'), function() {
			secondType = secondType + $(this).val() + ",";
        });
		//初始化数据
	    showGrid({
		 	id: "showForm",
		 	url: reqBasePath + "sysevewin008",
		 	params: {firstType: $("#sysFirstType").find(".active").find('a').attr('rowid'), secondType: secondType},
		 	pagination: true,
		 	pagesize: 18,
		 	template: getFileContent('tpl/appstore/storeindexTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 		
		 		hdb.registerHelper("compare1", function(v1, options){
					return fileBasePath + v1;
				});
		 		
		 		hdb.registerHelper('compare2', function(v1, v2, options) {
				 	if(isNull(v1)){
		 				return '';
		 			} else {
		 				return '<button class="layui-btn layui-btn-sm aysnMenu" rowid="' + v2 + '">下载</button>';
		 			}
			 	});
		 	},
		 	options: {},
		 	ajaxSendAfter:function (json) {
		 		$(".sysExperience").hide();
		 		$(".aysnMenu").hide();
		 		
		 		//遮罩层移入移出事件
		 	    $(".app-store-item-bottom-card").mouseover(function (e){
		 	    	$(this).parent().find(".app-store-item-bottom").addClass("app-store-item-bottom-zz");
		 	    	$(this).find(".sysExperience").show();
		 	    	$(this).find(".aysnMenu").show();
		 		}).mouseout(function (e){
		 			$(this).parent().find(".app-store-item-bottom").removeClass("app-store-item-bottom-zz");
		 			$(this).find(".sysExperience").hide();
	 				$(this).find(".aysnMenu").hide();
		 		});
		 		matchingLanguage();
		 		form.render();
		 	}
	    });
	}
    
    //一级分类点击事件
    $("#sysFirstType").on('click', "a", function (e) {
    	$("#sysFirstType").find("li").removeClass("active");
    	$(this).parent().addClass("active");
    	var rowId = $(this).attr("rowid");
    	//加载二级分类
    	showGrid({
		 	id: "sysSecondType",
		 	url: reqBasePath + "sysevewintype013",
		 	params: {rowId: rowId},
		 	pagination: false,
		 	template: getFileContent('tpl/appstore/secondtypeTemplate.tpl'),
		 	ajaxSendLoadBefore: function(hdb){
		 	},
		 	ajaxSendAfter:function (json) {
		 		form.render('checkbox');
		 		initDate();
		 	}
		});
    });
    
    //二级分类点击事件
    form.on('checkbox(checkboxProperty)', function(data){
    	if($('#sysSecondType input:checkbox:checked').length == $('#sysSecondType input:checkbox').length){
    		$("#sysSecondType").find("li").find('a[rowid="111"]').parent().addClass('active');
    	} else {
    		$("#sysSecondType").find("li").find('a[rowid="111"]').parent().removeClass('active');
    	}
		initDate();
	});
    //二级分类全选操作
    $("#sysSecondType").on('click', "a[rowid='111']", function (e) {
    	$.each($('#sysSecondType input:checkbox'), function() {
    		$(this).prop("checked", true);
    	});
    	$(this).parent().addClass('active');
    	form.render('checkbox');
    	initDate();
    });
    
    //体验
    $("body").on('click', ".sysExperience", function (e) {
    	var sysurl = $(this).attr('sysurl');
    	window.open(sysurl, '_blank', '');
    });
    
    //详情
    $("body").on('click', ".sysDetails", function (e) {
    	
    });
    
    //同步
    $("body").on('click', ".aysnMenu", function (e) {
    	var downLoadId = $(this).attr("rowid");
    	layer.prompt({
			formType: 0,
		  	value: '',
		  	title: '请输入系统所在地址'
		}, function(val, index){
			layer.close(index);
			layer.confirm('确定从该地址：[' + val + ']进行数据同步吗？', { icon: 3, title: '数据同步' }, function (index) {
				layer.close(index);
	            
				AjaxPostUtil.request({url: reqBasePath + "sysimportantsynchronization001", params:{rowId: downLoadId, url: val}, type: 'json', callback: function (json) {
	    			if (json.returnCode == 0) {
	    				winui.window.msg("同步成功", {icon: 1, time: 2000});
	    			} else {
	    				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
	    			}
	    		}});
			});
    	});
    });
	
    exports('storeindex', {});
});
