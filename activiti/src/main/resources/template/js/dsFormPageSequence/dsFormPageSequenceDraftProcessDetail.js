
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form'], function (exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
        form = layui.form,
        table = layui.table;
    
    // 动态表单草稿状态下的详情
    var rowId = parent.rowId;
    AjaxPostUtil.request({url:reqBasePath + "pagesequence006", params: {rowId: rowId}, type:'json', callback:function(j){
		if(j.returnCode == 0){
			dsFormUtil.initSequenceDataDetails("showForm", j.rows);
			matchingLanguage();
		}else{
			winui.window.msg(j.returnMessage, {icon: 2,time: 2000});
		}
	}});
	
    exports('dsFormPageSequenceDraftProcessDetail', {});
});
