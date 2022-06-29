
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'laydate'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		table = layui.table,
		laydate = layui.laydate;

	// 获取当前登陆用户所属的学校列表
	schoolUtil.queryMyBelongSchoolList(function (json) {
		$("#schoolId").html(getDataUseHandlebars(getFileContent('tpl/template/select-option-must.tpl'), json));
		form.render("select");
		initTable();
	});

    function initTable(){
		showGrid({
			id: "showTemplate",
			url: schoolBasePath + "schooltimesetting001",
			params: {schoolId: $("#schoolId").val()},
			pagination: false,
			template: $("#timeSettingTemplate").html(),
			ajaxSendLoadBefore: function(hdb, json) {
			},
			ajaxSendAfter: function(json) {
				$.each(json.bean.timeList, function(i, item){
					if(!isNull(item.startTime) && !isNull(item.endTime)){
						$("#" + item.gradeId + '_' + item.semesterId).val(item.startTime + ' ~ ' + item.endTime);
					}
				});
				$('.timeRange').each(function(){
					laydate.render({elem: this, format: 'yyyy年MM月dd日', range: '~'});
				});
				matchingLanguage();
			}
		});

	    form.render();
    }
    
    // 保存
    $("body").on("click", "#saveBtn", function() {
		var array = new Array();
		$('.timeRange').each(function(){
			array.push({
				schoolId: $("#schoolId").val(),
				gradeId: $(this).attr("id").split("_")[0],
				semesterId: $(this).attr("id").split("_")[1],
				startTime: $(this).val().split(' ~ ')[0],
				endTime: $(this).val().split(' ~ ')[1]
			});
		});
		var params = {
    		arrayStr: JSON.stringify(array),
    		schoolId: $("#schoolId").val()
    	};
        
        AjaxPostUtil.request({url: schoolBasePath + "schooltimesetting002", params: params, type: 'json', callback: function(json) {
			if(json.returnCode == 0) {
				winui.window.msg("修改成功", {icon: 1, time: 2000});
			} else {
				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			}
		}});
	});
	
	$("body").on("click", "#formSearch", function() {
		initTable();
	});
	
    exports('schoolTimeSetting', {});
});
