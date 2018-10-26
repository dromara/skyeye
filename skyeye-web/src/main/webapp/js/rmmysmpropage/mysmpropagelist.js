
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['table', 'jquery', 'winui', 'form'], function (exports) {
	
	winui.renderColor();
	
	var $ = layui.$,
	form = layui.form,
	table = layui.table;
	
	$("#groupMemberTab").hide();
	
	showGrid({
	 	id: "groupMember",
	 	url: reqBasePath + "rmxcx027",
	 	params: {},
	 	pagination: false,
	 	template: getFileContent('tpl/rmmysmpropage/groupTemplate.tpl'),
	 	ajaxSendLoadBefore: function(hdb){
	 	},
	 	ajaxSendAfter:function(json){
	 		
	 	}
	});
	
	var winH = $(window).height();
    var categorySpace = 10;
    
    //二级菜单点击
    $('body').on('click', '.js_item', function(){
        var id = $(this).data('id');
        var title = $(this).data('name');
        showGrid({
    	 	id: "memberList",
    	 	url: reqBasePath + "rmxcx028",
    	 	params: {rowId: id},
    	 	pagination: false,
    	 	template: getFileContent('tpl/rmmysmpropage/groupMemberTemplate.tpl'),
    	 	ajaxSendLoadBefore: function(hdb){
    	 		hdb.registerHelper("compare1", function(v1, options){
					return fileBasePath + "assets/smpropic/" + v1;
				});
    	 	},
    	 	ajaxSendAfter:function(json){
    	 	}
    	});
        $("#groupTitle").html(title);
        $("#groupTab").animate({  
            width : "hide",  
            opacity: "0",
            paddingLeft : "hide",  
            paddingRight : "hide",  
            marginLeft : "hide",  
            marginRight : "hide"  
        }, 500);
        $("#groupMemberTab").animate({  
            width : "show",  
            opacity: "1",
            paddingLeft : "show",  
            paddingRight : "show",  
            marginLeft : "show",  
            marginRight : "show"  
        }, 500); 
    });
    
    //返回分组列表
    $('body').on('click', '#returnGroupTab', function(){
    	$("#groupMemberTab").animate({  
            width : "hide",  
            opacity: "0",
            paddingLeft : "hide",  
            paddingRight : "hide",  
            marginLeft : "hide",  
            marginRight : "hide"  
        }, 500);
        $("#groupTab").animate({  
            width : "show",  
            opacity: "1",
            paddingLeft : "show",  
            paddingRight : "show",  
            marginLeft : "show",  
            marginRight : "show"  
        }, 500); 
    });
    
    //展开一级菜单
    $('body').on('click', '.js_category', function(){
        var $this = $(this),
            $inner = $this.next('.js_categoryInner'),
            $page = $this.parents('.page'),
            $parent = $(this).parent('li');
        var innerH = $inner.data('height');
        bear = $page;

        if(!innerH){
            $inner.css('height', 'auto');
            innerH = $inner.height();
            $inner.removeAttr('style');
            $inner.data('height', innerH);
        }

        if($parent.hasClass('js_show')){
            $parent.removeClass('js_show');
        }else{
            $parent.siblings().removeClass('js_show');

            $parent.addClass('js_show');
            if(this.offsetTop + this.offsetHeight + innerH > $page.scrollTop() + winH){
                var scrollTop = this.offsetTop + this.offsetHeight + innerH - winH + categorySpace;

                if(scrollTop > this.offsetTop){
                    scrollTop = this.offsetTop - categorySpace;
                }

                $page.scrollTop(scrollTop);
            }
        }
    });
    
    //图片预览
    $('body').on('click', '.cursor', function(){
    	layer.open({
    		type:1,
    		title:false,
    		closeBtn:0,
    		skin: 'demo-class',
    		shadeClose:true,
    		content:'<img src="' + $(this).attr("src") + '" style="max-height:600px;max-width:100%;">',
    		scrollbar:false
        });
    });
    
    function loadTable(){
    	table.reload("messageTable", {where:{proName:$("#proName").val()}});
    }
    
    exports('mysmpropagelist', {});
});
