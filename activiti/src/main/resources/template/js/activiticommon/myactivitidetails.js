
layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'flow'], function (exports) {
    winui.renderColor();
    var $ = layui.$,
        form = layui.form,
        table = layui.table,
        flow = layui.flow;
    
    //流程详情
    var processInstanceId = parent.processInstanceId;
    var taskType = parent.taskType;
    
    $("#activitiTitle").html(taskType);
    
    //时间线审批历史列表模板
	var timeTreeApprovalHistory = $("#timeTreeApprovalHistory").html();
	
    var textTemplate = $("#textTemplate").html(),//文本展示
    	enclosureTemplate = $("#enclosureTemplate").html(),//附件展示
    	eichTextTemplate = $("#eichTextTemplate").html(),//富文本展示
    	picTemplate = $("#picTemplate").html(),//图片展示
    	tableTemplate = $("#tableTemplate").html(),//表格展示
		voucherTemplate = $("#voucherTemplate").html();//凭证展示

    AjaxPostUtil.request({url: flowableBasePath + "activitimode025", params: {processInstanceId: processInstanceId}, type: 'json', method: 'GET', callback: function(j){
		var jsonStr = "";//实体json对象
		var str = "";
		$.each(j.rows, function(i, item) {
			//如果展示文本不为空，则展示展示文本
			if (!isNull(item.text))
				item.value = item.text;
			jsonStr = {
				bean: item
			};
			if(item.showType == 1){//文本展示
				str = getDataUseHandlebars(textTemplate, jsonStr);
			} else if (item.showType == 2){//附件展示
				str = getDataUseHandlebars(enclosureTemplate, jsonStr);
			} else if (item.showType == 3){//富文本展示
				str = getDataUseHandlebars(eichTextTemplate, jsonStr);
			} else if (item.showType == 4){//图片展示
				var photoValue = [];
				if (!isNull(jsonStr.bean.value)){
					photoValue = item.value.split(",");
				}
				var rows = [];
				$.each(photoValue, function(j, row){
					rows.push({photoValue: row});
				});
				jsonStr.bean.photo = rows;
				str = getDataUseHandlebars(picTemplate, jsonStr);
			} else if (item.showType == 5){//表格展示
				str = getDataUseHandlebars(tableTemplate, jsonStr);
				var tableId = "messageTable" + item.orderBy;//表格id
				var tableBoxId = "showTable" + item.orderBy;//表格外部div盒子id
				$("#showForm").append(str);
				$("#" + tableBoxId).html('<table id="' + tableId + '" lay-filter="' + tableId + '"></table>');
				if(typeof item.headerTitle == 'object'){
					item.headerTitle = JSON.stringify(item.headerTitle);
				}
				table.render({
					id: tableId,
					elem: "#" + tableId,
					data: $.extend(true, [], getValJson(item.value, '', '')),
					page: false,
					cols: getValJson(item.headerTitle, '[', ']')
				});
				str = "";
			} else if (item.showType == 6){//凭证展示
				str = getDataUseHandlebars(voucherTemplate, jsonStr);
				$("#showForm").append(str);
				var boxId = "showVoucher" + item.orderBy;
				// 初始化凭证
				voucherUtil.initDataDetails(boxId, item.value);
				str = "";
			}else {
				str = "";
			}
			$("#showForm").append(str);
		});
		//加载流程图片
		$("#processInstanceIdImg").attr("src", fileBasePath + 'images/upload/activiti/' + processInstanceId + ".png?cdnversion=" + Math.ceil(new Date()/3600000));
		//加载审批历史
		inboxTimeTreeApprovalHistory();
		matchingLanguage();
	}});

    function getValJson(val, startPrefix, endPrefix){
    	if(typeof val == 'string'){
    		val = startPrefix + val + endPrefix;
			return JSON.parse(val);
		}
    	return val;
	}

	// 加载时间线审批历史
	function inboxTimeTreeApprovalHistory(){
		flow.load({
			elem: '#timeTreeApprovalHistoryList',
			scrollElem: '#timeTreeApprovalHistoryList',
			isAuto: true,
			done: function(page, next) {
				var lis = [];
				AjaxPostUtil.request({url: flowableBasePath + "activitimode017", params: {processInstanceId: parent.processInstanceId}, type: 'json', callback: function (json) {
					$.each(json.rows, function(index, bean) {
						bean.showClass = 'date02';
						lis.push(getDataUseHandlebars(timeTreeApprovalHistory, {bean: bean}));
					});
					next(lis.join(''), (page * 1000) < json.total);
		   		}});
			}
		});
	}
	
    // 附件下载
	$("body").on("click", ".enclosureItem", function() {
    	download(fileBasePath + $(this).attr("rowpath"), $(this).html());
    });
    
	// 工作流图片查看
    $("body").on("click", "#processInstanceIdImg", function() {
		systemCommonUtil.showPicImg($(this).attr("src"));
    });
    
    // 图片查看
    $("body").on("click", ".photo-img", function() {
		systemCommonUtil.showPicImg($(this).attr("src"));
    });
    
    form.render();
    
    exports('myactivitidetailspagesub', {});
});
