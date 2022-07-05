
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'ClipboardJS'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		table = layui.table;
	var clipboard;//复制对象
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: reqBasePath + 'fileconsole017',
	    where:{reqBasePath: reqBasePath},
	    even:true,
	    page: true,
	    limits: [8, 16, 24, 32, 40, 48, 56],
	    limit: 8,
	    cols: [[
	        { field: 'shareName', title: '分享文件', width: 150 },
	        { field: 'shareType', title: '分享类型', width: 90 },
	        { field: 'fileType', title: '文件类型', width: 90 },
	        { field: 'stateName', title: '状态', width: 60 },
	        { field: 'createTime', title: '分享时间', width: 150 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 150, toolbar: '#tableBar'}
	    ]],
	    done: function(){
	    	matchingLanguage();
	    	//复制
	    	clipboard = new ClipboardJS('.copyUrl');
	    	clipboard.on('success', function(e) {
	    		winui.window.msg("复制成功", {icon: 1, time: 2000});
	    	});
	    	clipboard.on('error', function(e) {
	    		winui.window.msg("浏览器不支持！", {icon: 2, time: 2000});
	    	});
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'cancleShare') { //取消分享
        	cancleShare(data, obj);
        }
    });
	
	//取消分享
	function cancleShare(data, obj){
		layer.confirm("取消分享链接将失效，确定不分享了吗？", { icon: 3, title: '系统提示', btn: ['取消分享','我再想想'] }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: reqBasePath + "fileconsole018", params:{rowId: data.id}, type: 'json', callback: function (json) {
				winui.window.msg("取消外链分享成功", {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}
	
	//刷新数据
    $("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    function loadTable(){
    	table.reload("messageTable", {where:{reqBasePath: reqBasePath}});
    }
    
    exports('shareFileListPage', {});
});
