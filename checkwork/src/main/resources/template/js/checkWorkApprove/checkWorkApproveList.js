
var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'laydate', 'form', 'tableSelect'], function (exports) {
	winui.renderColor();
	
	var $ = layui.$,
		table = layui.table,
		form = layui.form,
		laydate = layui.laydate,
		tableSelect = layui.tableSelect;
	
	// 审批日期选取时间段表格
	laydate.render({
		elem: '#checkDate',
		range: '~'
	});
	
	// 初始化申诉原因类型
	showGrid({
	 	id: "appealReasonId",
	 	url: flowableBasePath + "checkworkreason010",
	 	params: {},
	 	pagination: false,
	 	template: getFileContent('tpl/template/select-option.tpl'),
	 	ajaxSendLoadBefore: function(hdb){},
	 	ajaxSendAfter:function(json){
	 		form.render('select');
	 	}
    });
	
	var createid = "";
	tableSelect.render({
    	elem: '#createId',	//定义输入框input对象
    	checkedKey: 'id', //表格的唯一键值，非常重要，影响到选中状态 必填
    	searchKey: 'userName',	//搜索输入框的name值 默认keyword
    	searchPlaceholder: '审批人搜索',	//搜索输入框的提示文字 默认关键词搜索
    	table: {	//定义表格参数，与LAYUI的TABLE模块一致，只是无需再定义表格elem
    		url: flowableBasePath + 'checkwork008',
    		where: {userName: $("#userName").val()},
    		method: 'post',
    		page: true,
    	    limits: [8, 16, 24, 32, 40, 48, 56],
    	    limit: 8,
    		cols: [[
    		    { type: 'radio'},
				{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
				{ field: 'userName', title: '申诉人', width: 100 },
				{ field: 'userSex', title: '性别', width: 60, templet: function(d){
		        	if(d.userSex == '0'){
		        		return "保密";
		        	}else if(d.userSex == '1'){
		        		return "男";
		        	}else if(d.userSex == '2'){
		        		return "女";
		        	} else {
		        		return "参数错误";
		        	}
		        }},
		        { field: 'departmentName', title: '所在部门', width: 100 }
			]]
    	},
    	done: function (elem, data) {
    		var newJson = data.data[0].userName;
    		createid = data.data[0].id;
    		elem.val(newJson);
    		elem.attr('ts-selected', createid);
    	}
    })
    
    initMyGetTable();
	// 员工考勤申诉审批列表
	function initMyGetTable(){
		table.render({
		    id: 'myGetTable',
		    elem: '#myGetTable',
		    method: 'post',
		    url: flowableBasePath + 'checkwork007',
		    where: getTablePatams(),
		    even: true,
		    page: true,
		    limits: getLimits(),
	    	limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'checkDate', title: '申诉内容', align: 'left', width: 160, templet: function(d){
		        	var text = d.checkDate;
		        	return text += d.situation;
		        }},
		        { field: 'appealName', title: '申诉原因', align: 'center', width: 100 },
		        { field: 'appealReason', title: '申诉具体原因', align: 'left', width: 170 },
		        { field: 'createTime', title: '申诉时间', align: 'center', width: 150 },
		        { field: 'state', title: '申诉状态', align: 'left', width: 100, templet: function(d){
		        	if(d.state == '0'){
		        		return "<span class='state-new'>申诉中</span>";
		        	}else if(d.state == '1'){
		        		return "<span class='state-up'>申诉通过</span>";
		        	}else if(d.state == '2'){
		        		return "<span class='state-down'>申诉不通过</span>";
		        	} else {
		        		return "参数错误";
		        	}
		        }},
		        { field: 'userName', title: '申诉人', align: 'left', width: 80},
		        { field: 'approvalTime', title: '审批时间', align: 'center', width: 150},
		        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 140, toolbar: '#tableBar2'}
		    ]],
		    done: function(){
		    	matchingLanguage();
		    }
		});
		table.on('tool(myGetTable)', function (obj) {
			var data = obj.data;
			var layEvent = obj.event;
			if (layEvent === 'approve') { //审批员工的申诉申请
				approve(data);
			}else if(layEvent === 'appealdetails'){ //员工的申诉申请详情
				appealdetails(data);
			}
		});
	}
	
	form.render();
	
	// 审批员工的申诉申请
	function approve(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/checkWorkApprove/checkWorkApprove.html", 
			title: "审批申诉申请",
			pageId: "checkWorkApprove",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
                if (refreshCode == '0') {
                	winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
                	reloadMyGetTable();
                } else if (refreshCode == '-9999') {
                	winui.window.msg(systemLanguage["com.skyeye.operationFailed"][languageType], {icon: 2, time: 2000});
                }
			}});
	}
	
	// 员 工的申诉申请详情
	function appealdetails(data){
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/checkWorkAbnormalAttendance/checkWorkAbnormalAttendanceDetails.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "checkWorkAbnormalAttendanceDetails",
			area: ['90vw', '90vh'],
			callBack: function(refreshCode){
			}});
	}
	
	// 搜索员工考勤申诉审批
	$("body").on("click", "#searchForm2", function() {
    	table.reload("myGetTable", {page: {curr: 1}, where: getTablePatams()});
	});
    
    // 刷新员工考勤申诉审批
    $("body").on("click", "#reloadGetTable", function() {
    	reloadMyGetTable();
    });
    
    function reloadMyGetTable(){
    	table.reload("myGetTable", {where: getTablePatams()});
    }
    
    function getTablePatams(){
    	if(isNull($("#createId").val())){
    		createid = "";
    	}
    	var startTime = "", endTime = "";
    	if(!isNull($("#checkDate").val())){
    		startTime = $("#checkDate").val().split('~')[0].trim() + ' 00:00:00';
    		endTime = $("#checkDate").val().split('~')[1].trim() + ' 23:59:59';
    	}
    	return {
    		firstTime: startTime,
    		lastTime: endTime,
    		createId: createid,
    		state: $("#state").val(),
    		appealReasonId: $("#appealReasonId").val()
    	};
    }
    
    exports('checkWorkApproveList', {});
});
