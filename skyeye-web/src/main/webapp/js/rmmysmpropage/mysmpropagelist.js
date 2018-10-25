
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).define(['table', 'jquery', 'winui', 'form'], function (exports) {
	
	winui.renderColor();
	
	var $ = layui.$,
	form = layui.form,
	table = layui.table;
	
	showGrid({
	 	id: "groupMember",
	 	url: reqBasePath + "rmxcx027",
	 	params: {},
	 	pagination: false,
	 	template: getFileContent('tpl/rmmysmpropage/groupMemberTemplate.tpl'),
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
        window.pageManager.go(id);
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
    
    function loadTable(){
    	table.reload("messageTable", {where:{proName:$("#proName").val()}});
    }
    
    exports('mysmpropagelist', {});
});
