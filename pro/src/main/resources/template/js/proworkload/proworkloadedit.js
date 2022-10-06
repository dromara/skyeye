
// 任务填报
layui.config({
	base: basePath,
	version: skyeyeVersion
}).extend({
	window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'form'], function(exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form,
		laydate = layui.laydate;

	var rowNum = 1; //表格的序号
	var proList = new Array();//项目集合
	var taskListHtml = "";//任务集合展示html

	var usetableTemplate = $("#usetableTemplate").html();
	var showTemplate = $("#showTemplate").html();
	var selOption = getFileContent('tpl/template/select-option.tpl');

	AjaxPostUtil.request({url: flowableBasePath + "proworkload007", params: {rowId: parent.rowId}, type: 'json', callback: function(json) {
		$("#title").val(json.bean.title);
		$("#desc").val(json.bean.desc);
		$("#writePeople").html(json.bean.writePeople);
		$("#thisRowLoad").html(json.bean.allworkload);
		$("#projectNumber").html(json.bean.projectNumber);
		$("input:radio[name=workLoadType][value=" + json.bean.type + "]").attr("checked", true);

		if(json.bean.state == '1'){
			$(".typeTwo").removeClass("layui-hide");
		} else {
			$(".typeOne").removeClass("layui-hide");
		}

		var dateArray = new Array();
		dateArray.push(json.bean.tasks[0].monTime);
		dateArray.push(json.bean.tasks[0].tuesTime);
		dateArray.push(json.bean.tasks[0].wedTime);
		dateArray.push(json.bean.tasks[0].thurTime);
		dateArray.push(json.bean.tasks[0].friTime);
		dateArray.push(json.bean.tasks[0].satuTime);
		dateArray.push(json.bean.tasks[0].sunTime);
		//设置日期
		var cells = document.getElementById('dataTr').getElementsByTagName('th');
		for(var i = 0; i < 7; i++) {
			cells[i].innerHTML = dateArray[i];
		}

		// 附件回显
		skyeyeEnclosure.initTypeISData({'enclosureUpload': json.bean.enclosureInfo});
		// 获取我参与的项目列表
		proUtil.queryMyProjectsList(function (data) {
			$("#proId").html(getDataUseHandlebars(selOption, data));
			$("#proId").val(json.bean.proId);
			proList = data.rows;
			form.render('select');
		});
		// 获取我的任务
		AjaxPostUtil.request({url: flowableBasePath + "protask015", params: {proId: json.bean.proId}, type: 'json', callback: function(data) {
			taskListHtml = getDataUseHandlebars(selOption, data);
			$.each(json.bean.tasks, function(i, item) {
				showRow(item);
			});
		}});
		matchingLanguage();
		form.render();
	}});

	//所属项目变化事件
	form.on('select(proIdProperty)', function(data) {
		var thisRowValue = data.value;
		if (!isNull(thisRowValue) && thisRowValue != '请选择') {
			$.each(proList, function(i, item) {
				if(item.id == thisRowValue){
					$("#projectNumber").html(item.projectNumber);
					if(isNull(item.customerName)){
						$("#title").val(item.name + "-" + getYMDFormatDate());
					} else {
						$("#title").val(item.name + "-" + item.customerName + "-" + getYMDFormatDate());
					}
					//获取我的任务
					AjaxPostUtil.request({url: flowableBasePath + "protask015", params: {proId: item.id}, type: 'json', method: "POST", callback: function(json) {
						taskListHtml = getDataUseHandlebars(selOption, json);
						resetTableTask();
					}});
					return false;
				}
			});
		} else {
			taskListHtml = "";
			resetTableTask();
			$("#projectNumber").html("");
		}
	});

	function resetTableTask(){
		var rowTr = $("#useTable tr");
		$.each(rowTr, function(i, item) {
			//获取行编号
			var rowNum = $(item).attr("trcusid").replace("tr", "");
			$("#" + "content" + rowNum).html(taskListHtml);
		});
		form.render('select');
	}

	form.on('radio(workLoadType)', function(data) {
		var thisRowValue = data.value;
		if(thisRowValue == 1){//上一周
			setDate(addDate(new Date(), -7));
		} else if (thisRowValue == 2){//本周
			setDate(new Date());
		} else if (thisRowValue == 3){//下一周
			setDate(addDate(new Date(), 7));
		}
	});

	var cells = document.getElementById('dataTr').getElementsByTagName('th');
	var clen = cells.length;
	var formatDate = function(date) {
		var year = date.getFullYear() + '/';
		var month = ((date.getMonth() + 1) < 10 ? "0" + (date.getMonth() + 1) : (date.getMonth() + 1)) + '/';
		var day = date.getDate() < 10 ? "0" + date.getDate() : date.getDate();
		return year + month + day;
	};
	var addDate = function(date, n) {
		date.setDate(date.getDate() + n);
		return date;
	};
	var setDate = function(date) {
		var week = date.getDay() - 1;
		date = addDate(date, week * -1);
		for(var i = 0; i < clen; i++) {
			cells[i].innerHTML = formatDate(i == 0 ? date : addDate(date, 1));
		}
	};

	//数量变化
	$("body").on("input", ".wkLoad", function() {
		calculatedTotalPrice();
	});

	//计算总价
	function calculatedTotalPrice(){
		var rowTr = $("#useTable tr");
		var allLoad = 0;
		$.each(rowTr, function(i, item) {
			//获取行坐标
			var rowNum = $(item).attr("trcusid").replace("tr", "");
			//获取周一工作量
			var mon = parseInt(isNull($("#mon" + rowNum).val()) ? "0" : $("#mon" + rowNum).val());
			//获取周二工作量
			var tues = parseInt(isNull($("#tues" + rowNum).val()) ? "0" : $("#tues" + rowNum).val());
			//获取周三工作量
			var wed = parseInt(isNull($("#wed" + rowNum).val()) ? "0" : $("#wed" + rowNum).val());
			//获取周四工作量
			var thur = parseInt(isNull($("#thur" + rowNum).val()) ? "0" : $("#thur" + rowNum).val());
			//获取周五工作量
			var fri = parseInt(isNull($("#fri" + rowNum).val()) ? "0" : $("#fri" + rowNum).val());
			//获取周六工作量
			var satu = parseInt(isNull($("#satu" + rowNum).val()) ? "0" : $("#satu" + rowNum).val());
			//获取周日工作量
			var sun = parseInt(isNull($("#sun" + rowNum).val()) ? "0" : $("#sun" + rowNum).val());
			//计算本行的合计
			var thisRowLoad = mon + tues + wed + thur + fri + satu + sun;
			$("#allload" + rowNum).html(thisRowLoad);
			//计算所有行的合计
			allLoad += thisRowLoad;
		});
		$("#thisRowLoad").html(allLoad);
	}

	// 保存为草稿
	form.on('submit(formEditBean)', function(data) {
		if(winui.verifyForm(data.elem)) {
			saveData('1', "");
		}
		return false;
	});

	// 提交审批
	form.on('submit(formSubBean)', function(data) {
		if(winui.verifyForm(data.elem)) {
			activitiUtil.startProcess(sysActivitiModel["proworkLoad"]["key"], null, function (approvalId) {
				saveData("2", approvalId);
			});
		}
		return false;
	});

	// 工作流中保存
	form.on('submit(subBean)', function(data) {
		if(winui.verifyForm(data.elem)) {
			saveData('3', "");
		}
		return false;
	});

	function saveData(subType, approvalId) {
		var rowTr = $("#useTable tr");
		var tableData = new Array();
		$.each(rowTr, function(i, item) {
			//获取行编号
			var rowNum = $(item).attr("trcusid").replace("tr", "");
			var row = {
				taskContent: $("#content" + rowNum).val(),
				executePeople: $("#execute" + rowNum).val(),
				monAmount: isNull($("#mon" + rowNum).val()) ? "0" : $("#mon" + rowNum).val(),
				monTime: $("#monTh").html(),
				tuesAmount: isNull($("#tues" + rowNum).val()) ? "0" : $("#tues" + rowNum).val(),
				tuesTime: $("#tuesTh").html(),
				wedAmount: isNull($("#wed" + rowNum).val()) ? "0" : $("#wed" + rowNum).val(),
				wedTime: $("#wedTh").html(),
				thurAmount: isNull($("#thur" + rowNum).val()) ? "0" : $("#thur" + rowNum).val(),
				thurTime: $("#thurTh").html(),
				friAmount: isNull($("#fri" + rowNum).val()) ? "0" : $("#fri" + rowNum).val(),
				friTime: $("#friTh").html(),
				satuAmount: isNull($("#satu" + rowNum).val()) ? "0" : $("#satu" + rowNum).val(),
				satuTime: $("#satuTh").html(),
				sunAmount: isNull($("#sun" + rowNum).val()) ? "0" : $("#sun" + rowNum).val(),
				sunTime: $("#sunTh").html(),
				allWorkLoad: $("#allload" + rowNum).html()
			};
			tableData.push(row);
		});

		var params = {
			title: $("#title").val(),
			type: $("input[name='workLoadType']:checked").val(),
			proId: $("#proId").val(),
			projectNumber: $("#projectNumber").html(),
			allWorkLoad: $("#thisRowLoad").html().trim(),
			workloadTaskStr: JSON.stringify(tableData),
			desc: $("#desc").val(),
			rowId: parent.rowId,
			enclosureInfo: skyeyeEnclosure.getEnclosureIdsByBoxId('enclosureUpload'),
			subType: subType, // 1：保存为草稿  2.提交到工作流  3.在工作流中编辑
			approvalId: approvalId,
		};
		AjaxPostUtil.request({url: flowableBasePath + "proworkload008", params: params, type: 'json', callback: function(json) {
			parent.layer.close(index);
			parent.refreshCode = '0';
		}});
	}

	// 新增行
	$("body").on("click", "#addRow", function() {
		addRow();
	});

	// 删除行
	$("body").on("click", "#deleteRow", function() {
		deleteRow();
	});

	// 新增行
	function addRow() {
		var par = {
			trId: "tr" + rowNum.toString(), //行的id
			id: "row" + rowNum.toString(), //checkbox的id
			content: "content" + rowNum.toString(), //任务内容id
			execute: "execute" + rowNum.toString(), //任务执行人id
			mon: "mon" + rowNum.toString(), //周一工作量id
			tues: "tues" + rowNum.toString(), //周二工作量id
			wed: "wed" + rowNum.toString(), //周三工作量id
			thur: "thur" + rowNum.toString(), //周四工作量id
			fri: "fri" + rowNum.toString(), //周五工作量id
			satu: "satu"  + rowNum.toString(), //周六工作量id
			sun: "sun"  + rowNum.toString(), //周日工作量id
			allload: "allload" + rowNum.toString() //小计id
		};
		$("#useTable").append(getDataUseHandlebars(usetableTemplate, par));
		//赋值给任务
		$("#" + "content" + rowNum.toString()).html(taskListHtml);
		form.render('select');
		form.render('checkbox');
		rowNum++;
	}

	//回显行
	function showRow(item) {
		var par = {
			trId: "tr" + rowNum.toString(), //行的id
			id: "row" + rowNum.toString(), //checkbox的id
			content: "content" + rowNum.toString(), //任务内容id
			execute: "execute" + rowNum.toString(), //任务执行人id
			executeContent: item.executePeople,
			mon: "mon" + rowNum.toString(), //周一工作量id
			monContent: item.monAmount,
			tues: "tues" + rowNum.toString(), //周二工作量id
			tuesContent: item.tuesAmount,
			wed: "wed" + rowNum.toString(), //周三工作量id
			wedContent: item.wedAmount,
			thur: "thur" + rowNum.toString(), //周四工作量id
			thurContent: item.thurAmount,
			fri: "fri" + rowNum.toString(), //周五工作量id
			friContent: item.friAmount,
			satu: "satu"  + rowNum.toString(), //周六工作量id
			satuContent: item.satuAmount,
			sun: "sun"  + rowNum.toString(), //周日工作量id
			sunContent: item.sunAmount,
			allload: "allload" + rowNum.toString(), //小计id
			allloadContent: item.allWorkLoad
		};
		$("#useTable").append(getDataUseHandlebars(showTemplate, par));
		//赋值给任务
		$("#" + "content" + rowNum.toString()).html(taskListHtml);
		$("#" + "content" + rowNum.toString()).val(item.taskContent);
		form.render('select');
		form.render('checkbox');
		rowNum++;
	}

	//删除行
	function deleteRow() {
		var checkRow = $("#useTable input[type='checkbox'][name='tableCheckRow']:checked");
		if(checkRow.length > 0) {
			$.each(checkRow, function(i, item) {
				$(item).parent().parent().remove();
			});
			calculatedTotalPrice();
		} else {
			winui.window.msg('请选择要删除的行', {icon: 2, time: 2000});
		}
	}

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});