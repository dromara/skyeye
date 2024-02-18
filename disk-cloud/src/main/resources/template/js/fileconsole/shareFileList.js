
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'ClipboardJS', 'form'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		table = layui.table;
	var clipboard;//复制对象
	
	table.render({
	    id: 'messageTable',
	    elem: '#messageTable',
	    method: 'post',
	    url: sysMainMation.diskCloudBasePath + 'queryShareFileList',
	    where: getTableParams(),
	    even: true,
	    page: true,
		limits: getLimits(),
		limit: getLimit(),
	    cols: [[
	        { field: 'shareName', title: '分享文件', width: 150 },
	        { field: 'shareTypeName', title: '分享类型', width: 90 },
	        { field: 'fileTypeName', title: '文件类型', width: 90 },
	        { field: 'stateName', title: '状态', width: 60 },
	        { field: 'createTime', title: '分享时间', width: 150 },
	        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 150, toolbar: '#tableBar'}
	    ]],
	    done: function(json) {
	    	matchingLanguage();
	    	//复制
	    	clipboard = new ClipboardJS('.copyUrl');
	    	clipboard.on('success', function(e) {
	    		winui.window.msg("复制成功", {icon: 1, time: 2000});
	    	});
	    	clipboard.on('error', function(e) {
	    		winui.window.msg("浏览器不支持！", {icon: 2, time: 2000});
	    	});

			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入文件/文件夹名称", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
	    }
	});
	
	table.on('tool(messageTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'cancleShare') { // 取消分享
        	cancleShare(data);
        }
    });
	
	// 取消分享
	function cancleShare(data){
		layer.confirm("取消分享链接将失效，确定不分享了吗？", { icon: 3, title: '系统提示', btn: ['取消分享', '我再想想'] }, function (index) {
			layer.close(index);
            AjaxPostUtil.request({url: sysMainMation.diskCloudBasePath + "deleteShareFileById", params: {id: data.id}, type: 'json', method: 'DELETE', callback: function (json) {
				winui.window.msg("取消外链分享成功", {icon: 1, time: 2000});
				loadTable();
    		}});
		});
	}

	form.render();
	$("body").on("click", "#reloadTable", function() {
		loadTable();
	});

	function loadTable() {
		table.reloadData("messageTable", {where: getTableParams()});
	}

	function getTableParams() {
		return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageTable"));
	}
    
    exports('shareFileListPage', {});
});
