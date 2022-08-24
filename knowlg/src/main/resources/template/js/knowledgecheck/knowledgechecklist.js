var rowId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'laydate', 'soulTable', 'eleTree'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		table = layui.table,
		soulTable = layui.soulTable,
		laydate = layui.laydate,
		eleTree = layui.eleTree;
	
	// 未审核表的提交时间
	laydate.render({elem: '#createTime', range: '~'});
	
	// 已审核表的提交时间
	laydate.render({elem: '#checkedCreateTime', range: '~'});
	
	//已审核表的审核时间
	laydate.render({elem: '#checkedExamineTime', range: '~'});
	
	authBtn('1568973352669');//未审核
	authBtn('1568973377977');//已审核
	
	var showNoCheckTable = false;
	var showCheckedTable = false;
	
	showLeft();
	//初始化左侧菜单
	function showLeft(){
		if(auth('1568973352669')){//未审核
			$("#setting").find("a[rowid='nochecklist']").addClass('selected');
			$("#noCheckForm").removeClass("layui-hide");
			showNoCheckList();
		}else if(auth('1568973377977')){//已审核
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
	//未审核
	function showNoCheckList(){
		showNoCheckTable = true;
		table.render({
		    id: 'messageNoCheckTable',
		    elem: '#messageNoCheckTable',
		    method: 'post',
		    url: sysMainMation.knowlgBasePath + 'knowledgecontent010',
		    where: getNoCheckTableParams(),
		    even: true,
		    page: true,
			limits: getLimits(),
			limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
		        { field: 'title', title: '标题', width: 250, templet: function (d) {
		        	return '<a lay-event="detail" class="notice-title-click">' + d.title + '</a>';
		    	}},
		        { field: 'typeName', title: '所属分类', width: 120 },
		        { field: 'createUser', title: '提交人', width: 120},
		        { field: 'createTime', title: '最后编辑时间', align: 'center', width: 200},
		        { title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 100, toolbar: '#tableBar'}
		    ]],
		    done: function(json) {
		    	matchingLanguage();
		    	soulTable.render(this);
		    	if(!loadType){
					initType();
		    	}
		    }
		});
	};
	
	table.on('tool(messageNoCheckTable)', function (obj) {
		var data = obj.data;
		var layEvent = obj.event;
		if (layEvent === 'check') { //审核
			check(data);
		} else if (layEvent === 'detail') { //详情
			detail(data);
		}
	});
	
	var loadType = false;
	//初始化类型
	function initType(){
		loadType = true;
		var el5;
		el5 = eleTree.render({
			elem: '.ele5',
			url: sysMainMation.knowlgBasePath + "knowledgetype008",
			defaultExpandAll: true,
			expandOnClickNode: false,
			highlightCurrent: true
		});
		$(".ele5").hide();
		$("#typeId").on("click",function (e) {
			e.stopPropagation();
			$(".ele5").toggle();
		});
		eleTree.on("nodeClick(data5)",function(d) {
			$("#typeId").val(d.data.currentData.name);
			$("#typeId").attr("typeId", d.data.currentData.id);
			$(".ele5").hide();
		})
		$(document).on("click",function() {
			$(".ele5").hide();
		})
	}

	form.render();
	
	//搜索未审核的表单
	$("body").on("click", "#formNoCheckSearch", function() {
		refreshTable();
	});
	
	$("body").on("click", "#reloadNoCheckTable", function() {
		loadTable();
	});
	
	function loadTable() {
		table.reloadData("messageNoCheckTable", {where: getNoCheckTableParams()});
	};
	
	function refreshTable(){
		table.reloadData("messageNoCheckTable", {page: {curr: 1}, where: getNoCheckTableParams()});
	};

	function getNoCheckTableParams(){
		var startTime = "", endTime = "";
		if (!isNull($("#createTime").val())) {
			startTime = $("#createTime").val().split('~')[0].trim() + ' 00:00:00';
			endTime = $("#createTime").val().split('~')[1].trim() + ' 23:59:59';
		}else {
			startTime = "";
			endTime = "";
		}
		return {
			title:$("#title").val(),
			typeId: isNull($("#typeId").val()) ? "" : $("#typeId").attr("typeId"),
			createUser: $("#createUser").val(),
			startTime: startTime,
			endTime: endTime
		};
	}
	
	// 审核
	function check(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/knowledgecheck/knowledgecheck.html", 
			title: "审核",
			pageId: "knowledgecheck",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
				winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
				loadTable();
				loadCheckedTable();
			}});
	};
	
	// 未审核详情
	function detail(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/knowledgecheck/knowledgeuncheckdetail.html", 
			title: systemLanguage["com.skyeye.detailsPageTitle"][languageType],
			pageId: "knowledgeuncheckdetail",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}});
	};
	
	// 已审核
	function showCheckedList(){
		showCheckedTable = true;
		table.render({
		    id: 'messageCheckedTable',
		    elem: '#messageCheckedTable',
		    method: 'post',
		    url: sysMainMation.knowlgBasePath + 'knowledgecontent013',
		    where: getCheckTableParams(),
		    even: true,
		    page: true,
			limits: getLimits(),
			limit: getLimit(),
		    cols: [[
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
		        { field: 'title', title: '标题', width: 250, templet: function (d) {
		        	return '<a lay-event="checkeddetail" class="notice-title-click">' + d.title + '</a>';
		    	}},
				{ field: 'typeName', title: '所属分类', width: 120 },
		        { field: 'state', title: '状态', width: 80, templet: function (d) {
		        	if(d.state == '2'){
		        		return "<span class='state-up'>已通过</span>";
		        	}else if(d.state == '3'){
		        		return "<span class='state-down'>未通过</span>";
		        	} else {
		        		return "参数错误";
		        	}
		        }},
		        { field: 'createUser', title: '提交人', width: 80},
		        { field: 'createTime', title: '最后编辑时间', align: 'center', width: 130},
		        { field: 'examineUser', title: '审核人', width: 80},
		        { field: 'examineTime', title: '审核时间', align: 'center', width: 130}
		    ]],
		    done: function(json) {
		    	soulTable.render(this);
		    	if(!loadCheckedType){
		    		initCheckedType();
		    	}
		    }
		});
	};
		
	table.on('tool(messageCheckedTable)', function (obj) {
        var data = obj.data;
        var layEvent = obj.event;
        if (layEvent === 'checkeddetail') { //审核详情
        	checkeddetail(data);
        }
    });
	
	var loadCheckedType = false;
	// 初始化类型
	function initCheckedType(){
		loadCheckedType = true;
		var el2;
		el2 = eleTree.render({
			elem: '.ele2',
			url: sysMainMation.knowlgBasePath + "knowledgetype008",
			defaultExpandAll: true,
			expandOnClickNode: false,
			highlightCurrent: true
		});
		$(".ele2").hide();
		$("#typeId2").on("click",function (e) {
			e.stopPropagation();
			$(".ele2").toggle();
		});
		eleTree.on("nodeClick(data2)",function(d) {
			$("#typeId2").val(d.data.currentData.name);
			$("#typeId2").attr("typeId", d.data.currentData.id);
			$(".ele2").hide();
		})
		$(document).on("click",function() {
			$(".ele2").hide();
		})
	}
	
	// 已审核详情
	function checkeddetail(data) {
		rowId = data.id;
		_openNewWindows({
			url: "../../tpl/knowledgecheck/knowledgecheckeddetail.html", 
			title: "详情",
			pageId: "knowledgecheckeddetail",
			area: ['90vw', '90vh'],
			callBack: function (refreshCode) {
			}});
	};
    
	// 搜索表单
	$("body").on("click", "#formCheckedSearch", function() {
		refreshCheckedTable();
	});
	
	$("body").on("click", "#reloadCheckedTable", function() {
    	loadCheckedTable();
    });
    
    function loadCheckedTable(){
    	table.reloadData("messageCheckedTable", {where: getCheckTableParams()});
    };
    
    function refreshCheckedTable(){
    	table.reloadData("messageCheckedTable", {page: {curr: 1}, where: getCheckTableParams()});
    };

    function getCheckTableParams(){
		var firstTime = "", lastTime = "";
		var theFirstTime = "", theLastTime = "";
		if (!isNull($("#checkedCreateTime").val())) {
			firstTime = $("#checkedCreateTime").val().split('~')[0].trim() + ' 00:00:00';
			lastTime = $("#checkedCreateTime").val().split('~')[1].trim() + ' 23:59:59';
		}else {
			firstTime = "";
			lastTime = "";
		}
		if (!isNull($("#checkedExamineTime").val())) {
			theFirstTime = $("#checkedExamineTime").val().split('~')[0].trim() + ' 00:00:00';
			theLastTime = $("#checkedExamineTime").val().split('~')[1].trim() + ' 23:59:59';
		}else {
			theFirstTime = "";
			theLastTime = "";
		}
    	return {
    		title:$("#checkedtitle").val(),
			state:$("#state").val(),
			typeId: isNull($("#typeId2").val()) ? "" : $("#typeId2").attr("typeId"),
			createUser: $("#createName").val(),
			startTime: firstTime,
			endTime: lastTime,
			examineStartTime: theFirstTime,
			examineEndTime: theLastTime
    	};
	}
	
    exports('knowledgechecklist', {});
});
