var rowId = "";
var forumId = "";
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
	
	laydate.render({elem: '#reportTime', type: 'datetime', range: true});
	
	laydate.render({elem: '#examineTime', type: 'datetime', range: true});
	
	laydate.render({elem: '#checkedreportTime', type: 'datetime', range: true});
	
	laydate.render({elem: '#checkedexamineTime', type: 'datetime', range: true});
	
	authBtn('1568077577288');//未审核
    authBtn('1568077598792');//已审核
	
	var showNoCheckTable = false;
	var showCheckedTable = false;
	
	showLeft();
	//初始化左侧菜单
	function showLeft(){
		if(auth('1568077577288')){
			$("#setting").find("a[rowid='nochecklist']").addClass('selected');
			$("#noCheckForm").removeClass("layui-hide");
			showNoCheckList();
		}else if(auth('1568077598792')){
			$("#setting").find("a[rowid='checkedlist']").addClass('selected');
			$("#checkedForm").removeClass("layui-hide");
			showCheckedList();
		}
	}
	
	//对左侧菜单项的点击事件
	$("body").on("click", "#setting a", function (e) {
		$(".setting a").removeClass("selected");
		$(this).addClass("selected");
		clickId = $(this).attr("rowid");
		if(clickId == "nochecklist"){
    		$("#checkedForm").addClass("layui-hide");
    		$("#noCheckForm").removeClass("layui-hide");
    		if(!showNoCheckTable){
    			showNoCheckList();
    		}
		}
		if(clickId == "checkedlist"){
			$("#noCheckForm").addClass("layui-hide");
    		$("#checkedForm").removeClass("layui-hide");
    		if(!showCheckedTable){
    			showCheckedList();
    		}
		}
	});

	// 举报类型
	sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["bbsForumReportType"]["key"], 'select', "reportType", '', form);

	//未审核
	function showNoCheckList(){
		showNoCheckTable = true;
		var reportstartTime = "";
		var reportendTime = "";
		if (!isNull($("#reportTime").val())) {
    		reportstartTime = $("#reportTime").val().split(' - ')[0].trim();
    		reportendTime = $("#reportTime").val().split(' - ')[1].trim();
    	}
		
		table.render({
		    id: 'messageNoCheckTable',
		    elem: '#messageNoCheckTable',
		    method: 'post',
		    url: sysMainMation.forumBasePath + 'forumreport002',
		    where: {title: $("#title").val(), reportTypeId: $("#reportType").val(), reportstartTime: reportstartTime, reportendTime: reportendTime},
		    even: true,
		    page: true,
		    limits: [8, 16, 24, 32, 40, 48, 56],
		    limit: 8,
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'title', title: '帖子标题', align: 'center', width: 250, templet: function (d) {
                    return '<a lay-event="forumdetails" class="notice-title-click">' + d.title + '</a>';
                }},
		        { field: 'reportType', title: '举报类型', align: 'center', width: 120 },
		        { field: 'examineState', title: '状态', width: 100, align: 'center', templet: function (d) {
		        	if(d.examineState == '1'){
		        		return "<span class='state-new'>未审核</span>";
		        	}else if(d.examineState == '2'){
		        		return "<span class='state-up'>已通过</span>";
		        	}else if(d.examineState == '3'){
		        		return "<span class='state-down'>未通过</span>";
		        	} else {
		        		return "参数错误";
		        	}
		        }},
		        { field: 'reportUser', title: '举报人', align: 'center', width: 120},
		        { field: 'reportTime', title: '举报时间', align: 'center', width: 200},
		        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 250, toolbar: '#tableBar'}
		    ]],
		    done: function(){
		    	matchingLanguage();
		    }
		});
		
		table.on('tool(messageNoCheckTable)', function (obj) {
	        var data = obj.data;
	        var layEvent = obj.event;
	        if (layEvent === 'check') { //审核
	        	check(data);
	        } else if (layEvent === 'detail') { //举报详情
	        	detail(data);
	        } else if (layEvent === 'forumdetails') { //帖子详情
                forumdetails(data);
            }
	    });
		
		form.render();
		
		$("body").on("click", "#formNoCheckSearch", function() {
			refreshTable();
		});
		
		$("body").on("click", "#reloadNoCheckTable", function() {
	    	loadTable();
	    });
	    
	    function loadTable() {
			if (!isNull($("#reportTime").val())) {
	    		reportstartTime = $("#reportTime").val().split(' - ')[0].trim();
	    		reportendTime = $("#reportTime").val().split(' - ')[1].trim();
	    	}
	    	table.reloadData("messageNoCheckTable", {where: {title: $("#title").val(), reportTypeId: $("#reportType").val(), reportstartTime: reportstartTime, reportendTime: reportendTime}});
	    }
	    
	    function refreshTable(){
			if (!isNull($("#reportTime").val())) {
	    		reportstartTime = $("#reportTime").val().split(' - ')[0].trim();
	    		reportendTime = $("#reportTime").val().split(' - ')[1].trim();
	    	}
	    	table.reloadData("messageNoCheckTable", {page: {curr: 1}, where: {title: $("#title").val(), reportTypeId: $("#reportType").val(), reportstartTime: reportstartTime, reportendTime: reportendTime}});
	    }

		//审核
		function check(data) {
			rowId = data.id;
			forumId = data.forumId;
			_openNewWindows({
				url: "../../tpl/forumreportcheck/reportcheck.html", 
				title: "审核",
				pageId: "reportcheck",
				area: ['80vw', '95vh'],
				callBack: function (refreshCode) {
					winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
					loadTable();
					loadCheckedTable();
				}});
		}
		
		//详情
		function detail(data) {
			rowId = data.id;
			_openNewWindows({
				url: "../../tpl/forumreportcheck/reportcheckdetail.html", 
				title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
				pageId: "reportcheckdetail",
				area: ['50vw', '80vh'],
				callBack: function (refreshCode) {
				}});
		};
	};
	
	//帖子详情
    function forumdetails(data) {
        forumId = data.forumId;
        _openNewWindows({
            url: "../../tpl/forumreportcheck/forumdetail.html", 
            title: "帖子详情",
            pageId: "forumdetails",
            area: ['80vw', '95vh'],
            callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
            }});
    };

	// 举报类型
	sysDictDataUtil.showDictDataListByDictTypeCode(sysDictData["bbsForumReportType"]["key"], 'select', "checkedReportType", '', form);

	//已审核
	function showCheckedList(){
		showCheckedTable = true;
		var checkedreportstartTime = "";
		var checkedreportendTime = "";
		if (!isNull($("#checkedreportTime").val())) {
			checkedreportstartTime = $("#checkedreportTime").val().split(' - ')[0].trim();
			checkedreportendTime = $("#checkedreportTime").val().split(' - ')[1].trim();
    	}
		var checkedexaminestartTime = "";
		var checkedexamineendTime = "";
		if (!isNull($("#checkedexamineTime").val())) {
			checkedexaminestartTime = $("#checkedexamineTime").val().split(' - ')[0].trim();
			checkedexamineendTime = $("#checkedexamineTime").val().split(' - ')[1].trim();
    	}
		
		
		table.render({
		    id: 'messageCheckedTable',
		    elem: '#messageCheckedTable',
		    method: 'post',
		    url: sysMainMation.forumBasePath + 'forumreport004',
		    where: {title: $("#checkedtitle").val(), reportTypeId: $("#checkedReportType").val(), reportstartTime: checkedreportstartTime, reportendTime: checkedreportendTime, examinestartTime: checkedexaminestartTime, examineendTime: checkedexamineendTime},
		    even: true,
		    page: true,
		    limits: [8, 16, 24, 32, 40, 48, 56],
		    limit: 8,
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'title', title: '帖子标题', align: 'center', width: 250, templet: function (d) {
                    return '<a lay-event="forumdetails" class="notice-title-click">' + d.title + '</a>';
                }},
		        { field: 'reportType', title: '举报类型', align: 'center', width: 120 },
		        { field: 'reportUser', title: '举报人', align: 'center', width: 120},
		        { field: 'reportTime', title: '举报时间', align: 'center', width: 200},
		        { field: 'examineState', title: '状态', width: 100, align: 'center', templet: function (d) {
		        	if(d.examineState == '1'){
		        		return "<span class='state-new'>未审核</span>";
		        	}else if(d.examineState == '2'){
		        		return "<span class='state-up'>已通过</span>";
		        	}else if(d.examineState == '3'){
		        		return "<span class='state-down'>未通过</span>";
		        	} else {
		        		return "参数错误";
		        	}
		        }},
		        { field: 'examineUser', title: '审核人', align: 'center', width: 180},
		        { field: 'examineTime', title: '审核时间', align: 'center', width: 200},
		        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 250, toolbar: '#checkedTableBar'}
		    ]],
		    done: function(){
		    	matchingLanguage();
		    }
		});
		
		table.on('tool(messageCheckedTable)', function (obj) {
	        var data = obj.data;
	        var layEvent = obj.event;
	        if (layEvent === 'detail') { //审核详情
                checkeddetail(data);
            } else if (layEvent === 'forumdetails') { //帖子详情
                forumdetails(data);
            }
	    });
		
		
		$("body").on("click", "#formCheckedSearch", function() {
			refreshCheckedTable();
		});
		
		$("body").on("click", "#reloadCheckedTable", function() {
	    	loadCheckedTable();
	    });
	    
	    function loadCheckedTable(){
			if (!isNull($("#checkedreportTime").val())) {
				checkedreportstartTime = $("#checkedreportTime").val().split(' - ')[0].trim();
				checkedreportendTime = $("#checkedreportTime").val().split(' - ')[1].trim();
	    	}
			if (!isNull($("#checkedexamineTime").val())) {
				checkedexaminestartTime = $("#checkedexamineTime").val().split(' - ')[0].trim();
				checkedexamineendTime = $("#checkedexamineTime").val().split(' - ')[1].trim();
	    	}
	    	table.reloadData("messageCheckedTable", {where: {title: $("#checkedtitle").val(), reportTypeId: $("#checkedReportType").val(), reportstartTime: checkedreportstartTime, reportendTime: checkedreportendTime, examinestartTime: checkedexaminestartTime, examineendTime: checkedexamineendTime}});
	    }
	    
	    function refreshCheckedTable(){
			if (!isNull($("#checkedreportTime").val())) {
				checkedreportstartTime = $("#checkedreportTime").val().split(' - ')[0].trim();
				checkedreportendTime = $("#checkedreportTime").val().split(' - ')[1].trim();
	    	}
			if (!isNull($("#checkedexamineTime").val())) {
				checkedexaminestartTime = $("#checkedexamineTime").val().split(' - ')[0].trim();
				checkedexamineendTime = $("#checkedexamineTime").val().split(' - ')[1].trim();
	    	}
	    	table.reloadData("messageCheckedTable", {page: {curr: 1}, where: {title: $("#checkedtitle").val(), reportTypeId: $("#checkedReportType").val(), reportstartTime: checkedreportstartTime, reportendTime: checkedreportendTime, examinestartTime: checkedexaminestartTime, examineendTime: checkedexamineendTime}});
	    }
	    
	    //详情
		function checkeddetail(data) {
			rowId = data.id;
			_openNewWindows({
				url: "../../tpl/forumreportcheck/reportcheckdetail.html", 
				title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
				pageId: "reportcheckdetail",
				area: ['50vw', '80vh'],
				callBack: function (refreshCode) {
				}});
		};
	};
	
    exports('forumreportchecklist', {});
});
