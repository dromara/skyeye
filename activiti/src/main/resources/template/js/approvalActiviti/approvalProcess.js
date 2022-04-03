
var jsonArray = [];//表单项
var layedit, form;

// 当前审批的表单是动态表单还是静态页面
var pageTypes = "";

layui.config({
    base: basePath,
    version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'layedit', 'colorpicker', 'slider', 'fileUpload', 'codemirror', 'xml', 'clike',
			'css', 'htmlmixed', 'javascript', 'nginx', 'solr', 'sql', 'vue', 'form', "table", 'flow'], function (exports) {
    winui.renderColor();
    var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$,
    	table = layui.table,
    	flow = layui.flow;
    form = layui.form,
    layedit = layui.layedit;
    
    var taskId = parent.taskId;
    var processInstanceId = parent.processInstanceId;
    var taskType = parent.taskType;
    
    $("#activitiTitle").html(taskType);
    
    // 时间线审批历史列表模板
	var timeTreeApprovalHistory = $("#timeTreeApprovalHistory").html();
    
    var textTemplate = $("#textTemplate").html(),//文本展示
    	enclosureTemplate = $("#enclosureTemplate").html(),//附件展示
    	eichTextTemplate = $("#eichTextTemplate").html(),//富文本展示
    	picTemplate = $("#picTemplate").html(),//图片展示
    	tableTemplate = $("#tableTemplate").html(),//表格展示
		voucherTemplate = $("#voucherTemplate").html();//凭证展示

    AjaxPostUtil.request({url:reqBasePath + "activitimode016", params: {taskId: taskId, processInstanceId: processInstanceId}, type: 'json', callback: function(j){
		if(j.returnCode == 0){
			pageTypes = isNull(j.bean.pageTypes) ? '1' : j.bean.pageTypes;
			var jsonStr = "";//实体json对象
			var str = "";
			//获取该节点的id和名称
			var editableNodeId = j.bean.taskKey;
			var editableNodeName = j.bean.taskKeyName;
			$.each(j.rows, function(i, item){
				jsonStr = {
					bean: item
				};
				jsonArray.push(item);
				//判断表单项在该流程节点是否可以编辑
				if((!isNull(editableNodeId) && ("," + item.editableNodeId + ",").indexOf("," + editableNodeId + ",") > -1)
					|| (!isNull(editableNodeName) && ("," + item.editableNodeName + ",").indexOf("," + editableNodeName + ",") > -1)){
					var formItem = item.formItem;
					if(!isNull(formItem)){
						if(formItem.associatedDataTypes == 1){//json串
							var obj = formItem.aData;
							tplContent = getDataUseHandlebars(formItem.templateContent, obj);
							formItem.context = tplContent;
						}else if(formItem.associatedDataTypes == 2){//接口
							AjaxPostUtil.request({url:reqBasePath + "dsformpage011", params:{interfa: formItem.aData}, type: 'json', callback: function(j){
					   			if(j.returnCode == 0){
					   				var obj = JSON.parse(j.bean.aData);
					   				tplContent = getDataUseHandlebars(formItem.templateContent, obj);
					   				formItem.context = tplContent;
					   			}else{
					   				winui.window.msg(j.returnMessage, {icon: 2,time: 2000});
					   			}
					   		}, async: false});
						}
						//重置左侧编辑框添加冒号
						formItem.labelContent += '：';
						jsonStr = {
							bean: formItem
						};
						var htmlContent = formItem.htmlContent;//模板
						var tpl = '{{#bean}}' + htmlContent + '{{/bean}}';
						var html = getDataUseHandlebars(tpl, jsonStr);
						var jsContent = formItem.jsContent;
						var js = '{{#bean}}' + jsContent + '{{/bean}}';
						var html_js = getDataUseHandlebars(js, jsonStr);
						var jsCon = '<script>layui.define(["jquery"], function(exports) {var jQuery = layui.jquery;(function($) {' + html_js + '})(jQuery);});</script>';
						$("#showForm").append(html + jsCon);
						
						$("#" + formItem.id).val(item.text); //给能通过id赋值的控件赋值
						var _this = $("#showForm .layui-form-item").eq(i);//当前控件
						var vid = _this.attr("controlType");//控件类型
						if(vid === 'range'){//类型为滑块
							var thisId = _this.find('div[id="' + formItem.id + '"]');
							thisId.find(".layui-slider-tips").html(item.text);
							thisId.find("div .layui-slider-bar").css("width", item.text + "%");
							thisId.find("div .layui-slider-wrap").css("left", item.text + "%");
							thisId.find("div .layui-slider-wrap").data("value", item.text);
		        		}else if(vid === 'color'){//类型为颜色选择器
		        			_this.find("input").val(item.text);
		        			_this.find('div[id="' + formItem.id + '"]').find("span .layui-colorpicker-trigger-span").attr("style", "background:" + item.text);
		        		}else if(vid === 'switchedradio'){//类型为开关式单选框
		        			_this.find("input").val(item.text);
		        			if(item.text === 'true' || item.text == true){
		        				_this.find("input").prop("checked", true);
		        			}
		        			_this.find("input").attr('id', formItem.id);
		        			_this.find("input").attr('name', formItem.id);
		        			_this.find("input").attr('lay-filter', formItem.id);
		        		}else if(vid === 'radio'){//类型为单选框
		        			_this.find("input:radio").attr("name", formItem.id);
		        			_this.find("input:radio[value=" + item.text + "]").attr("checked", true);
		        		}else if(vid === 'richtextarea'){//类型为富文本框
		        			_this.find('iframe[textarea="' + formItem.id + '"]').contents().find("body").html(item.text);
		        		}else if(vid === 'checkbox'){//类型为多选框
		        			var checkArray = item.text.split(",");
		        			var checkBoxAll = _this.find("input:checkbox");
		        			checkBoxAll.attr("name", formItem.id);
		        			for(var k = 0; k < checkArray.length; k++){
		        	           $.each(checkBoxAll, function(j, formItem){
		        	        	   if(checkArray[k] == $(this).val()){
		        	        		   $(this).prop("checked", true);
		        	        	   }
		        	           });
		        	         }
		        		}
					}else{
						if(item.showType == 1){//文本展示
							str = getDataUseHandlebars(textTemplate, jsonStr);
						}else if(item.showType == 2){//附件展示
							str = getDataUseHandlebars(enclosureTemplate, jsonStr);
						}else if(item.showType == 3){//富文本展示
							str = getDataUseHandlebars(eichTextTemplate, jsonStr);
						}else if(item.showType == 4){//图片展示
							var photoValue = [];
							if(!isNull(jsonStr.bean.text)){
								photoValue = item.value.split(",");
							}
							var rows = [];
							$.each(photoValue, function(j, row){
								rows.push({photoValue: row});
							});
							jsonStr.bean.photo = rows;
							str = getDataUseHandlebars(picTemplate, jsonStr);
						}else if(item.showType == 5){//表格展示
							str = getDataUseHandlebars(tableTemplate, jsonStr);
							var tableId = "messageTable" + item.orderBy;//表格id
							var tableBoxId = "showTable" + item.orderBy;//表格外部div盒子id
							$("#showForm").append(str);
							$("#" + tableBoxId).html('<table id="' + tableId + '" lay-filter="' + tableId + '"></table>');
							table.render({
							    id: tableId,
							    elem: "#" + tableId,
							    data: $.extend(true, [], item.value),
							    page: false,
							    cols: [getHeaderTitle(item.headerTitle)]
							});
							str = "";
						}else if(item.showType == 6){//凭证展示
							str = getDataUseHandlebars(voucherTemplate, jsonStr);
							$("#showForm").append(str);
							var boxId = "showVoucher" + item.orderBy;
							// 初始化凭证
							voucherUtil.initDataDetails(boxId, item.value);
							str = "";
						}
					}
				}else{
					if(item.showType == 1){//文本展示
						str = getDataUseHandlebars(textTemplate, jsonStr);
					}else if(item.showType == 2){//附件展示
						str = getDataUseHandlebars(enclosureTemplate, jsonStr);
					}else if(item.showType == 3){//富文本展示
						str = getDataUseHandlebars(eichTextTemplate, jsonStr);
					}else if(item.showType == 4){//图片展示
						var photoValue = [];
						if(!isNull(jsonStr.bean.text)){
							photoValue = item.value.split(",");
						}
						var rows = [];
						$.each(photoValue, function(j, row){
							rows.push({photoValue: row});
						});
						jsonStr.bean.photo = rows;
						str = getDataUseHandlebars(picTemplate, jsonStr);
					}else if(item.showType == 5){//表格展示
						str = getDataUseHandlebars(tableTemplate, jsonStr);
						var tableId = "messageTable" + item.orderBy;//表格id
						var tableBoxId = "showTable" + item.orderBy;//表格外部div盒子id
						$("#showForm").append(str);
						$("#" + tableBoxId).html('<table id="' + tableId + '" lay-filter="' + tableId + '"></table>');
						table.render({
						    id: tableId,
						    elem: "#" + tableId,
						    data: $.extend(true, [], item.value),
						    page: false,
						    cols: [getHeaderTitle(item.headerTitle)]
						});
						str = "";
					}else if(item.showType == 6){//凭证展示
						str = getDataUseHandlebars(voucherTemplate, jsonStr);
						$("#showForm").append(str);
						var boxId = "showVoucher" + item.orderBy;
						// 初始化凭证
						voucherUtil.initDataDetails(boxId, item.value);
						str = "";
					}
				}
				$("#showForm").append(str);
			});
			
			// 加载流程图片
			$("#processInstanceIdImg").attr("src", fileBasePath + 'images/upload/activiti/' + processInstanceId + ".png?cdnversion=" + Math.ceil(new Date()/3600000));

			// 是否委派，如果是委派||并行会签的子实例，则不需要选择下一个节点的审批人
			if(!j.bean.delegation && !j.bean.multilnStanceExecttionChild){
				// 加载下个节点审批人选择信息
				activitiUtil.initApprovalPerson("approvalOpinionDom", processInstanceId, taskId, $("input[name='flag']:checked").val());
			}

			// 并行会签的子实例，不支持工作流的其他操作
			if(!j.bean.multilnStanceExecttionChild){
				activitiUtil.activitiMenuOperator("otherMenuOperator", j.bean, function (){
					parent.layer.close(index);
					parent.refreshCode = '0';
				});
			}else{
				$("#otherMenuOperator").parent().hide();
			}

			// 加载会签信息
			if(j.bean.isMultiInstance){
				$("#multiInstanceBox").html(getDataUseHandlebars($("#multiInstance").html(), j));
				$("#multiInstanceState").html('已开启');
				if(j.bean.nrOfInstances != 0){
					// 会签任务总数为0说明没有设置会签人，可以自行审批通过，如果不为0，说明设置了会签人，需要通过会签投票获取结果
					$("#resultTitle").html('会签结果');
					$("#multiInstanceState").html('已完成');
					if(j.bean.nrOfActiveInstances != 0){
						// 正在执行的会签总数不为0并且不是子实例，说明会签还未结束，不能提交到下一个审批节点
						if(!j.bean.multilnStanceExecttionChild){
							$("#approvalOpinionDom").hide();
							$("#subBtnBox").hide();
						}
						$("#multiInstanceState").html('进行中');
					}
					if(!isNull(j.bean.approvalResult + "")){
						// 如果已经获得会签结果，则可以进行提交到下一步
						if(j.bean.approvalResult){
							$("input:radio[name=flag][value='1']").attr("checked", true);
						}else{
							$("input:radio[name=flag][value='2']").attr("checked", true);
						}
						$("input[name='flag']").attr('disabled', true);
						$("#approvalOpinionDom").show();
						$("#subBtnBox").show();
					}
				}
			}

			// 加载审批历史
			inboxTimeTreeApprovalHistory();
			matchingLanguage();
			form.render();
		}else{
			winui.window.msg(j.returnMessage, {icon: 2,time: 2000});
		}
	}});

	function getHeaderTitle(headerTitle){
		if(typeof headerTitle == 'string'){
			return JSON.parse(headerTitle);
		}
		return headerTitle;
	}
    
    form.render();
    form.on('submit(formAddBean)', function (data) {
        if (winui.verifyForm(data.elem)) {
        	var msg = '确认提交任务吗？';
    		layer.confirm(msg, { icon: 3, title: '提交任务' }, function (i) {
    			layer.close(i);
    			var params = [];//传给服务器的数组
    			for(var i = 0; i < $("#showForm .layui-form-item").length; i++){
	        		var _this = $("#showForm .layui-form-item").eq(i);
	        		var vid = _this.attr("controlType"),//控件类型
	        			text = "",
	        			value = "";
	        		if(vid === 'input'){//类型为输入框
	        			text = _this.find("input").val();
	        			value = _this.find("input").val();
	        		}else if(vid === 'textarea'){//类型为文本框
	        			text = _this.find("textarea").val();
	        			value = _this.find("textarea").val();
	        		}else if(vid === 'select'){//类型为下拉框
	        			text = _this.find("select").find("option:selected").text();
	        			value = _this.find("select").val();
	        		}else if(vid === 'checkbox'){//类型为多选框
	        			var texts = [], values = [];
	        			var arr = _this.find("input:checkbox:checked");
	        			$.each(arr, function(i, item){
	        				texts[i] = $(this).attr("title");
	        				values[i] = $(this).attr("value");
	        			});
	        			text = texts.join(",");
	        			value = values.join(",");
	        		}else if(vid === 'radio'){//类型为单选框
	        			text = _this.find("input:radio:checked").attr("title");
	        			value = _this.find("input:radio:checked").val();
	        		}else if(vid === 'upload'){//类型为图片上传
	        			var uploadId = _this.find(".upload").attr("id");
	        			text = $("#" + uploadId).find("input[type='hidden'][name='upload']").attr("oldurl");
	        			value = $("#" + uploadId).find("input[type='hidden'][name='upload']").attr("oldurl");
	        			if(isNull(text))
	        				text = "";
	        			if(isNull(value))
	        				value = "";
	        		}else if(vid === 'color'){//类型为颜色选择器
	        			text = _this.find("input").val();
	        			value = _this.find("input").val();
	        		}else if(vid === 'range'){//类型为滑块
	        			text = _this.find(".layui-slider-tips").html();
	        			value = _this.find(".layui-slider-tips").html();
	        		}else if(vid === 'richtextarea'){//类型为富文本框
	        			var textareaId = _this.find("textarea").attr("id");
	        			var content = encodeURIComponent(_this.find('iframe[textarea="' + textareaId + '"]').contents().find("body").html());
	        			text = content;
	        			value = content;
	        		}else if(vid === 'switchedradio'){//类型为开关式单选框
	        			value = _this.find("input").val();
	        			var layText = _this.find("input").attr('lay-text');
	        			if(value == "true"){
	        				text = layText.split('|')[0];
	        			}else{
	        				text = layText.split('|')[1];
	        			}
	        		}else{
	        			continue;
	        		}
	        		var obj = {
						value: value,
						text: text,
						rowId: jsonArray[i].rowId
					};
	        		params.push(obj);
	        	}
    			var jStr = {
	    			taskId: taskId,
	    			opinion: $("#opinion").val(),
	    			flag: $("input[name='flag']:checked").val(),
	    			processInstanceId: processInstanceId,
	    			editStr: (params.length > 0) ? JSON.stringify(params) : "",
	    			pageTypes: pageTypes,
					approverId: activitiUtil.getApprovalPersonId()
	            };
	            AjaxPostUtil.request({url:reqBasePath + "activitimode005", params: jStr, type: 'json', callback: function(json){
	 	   			if(json.returnCode == 0){
                    	parent.layer.close(index);
                    	parent.refreshCode = '0';
	 	   			}else{
	 	   				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
	 	   			}
	 	   		}});
    		});
        }
        return false;
    });
    
    // 加载时间线审批历史
	function inboxTimeTreeApprovalHistory(){
		flow.load({
			elem: '#timeTreeApprovalHistoryList', //指定列表容器
			scrollElem: '#timeTreeApprovalHistoryList',
			isAuto: true,
			done: function(page, next) { //到达临界点（默认滚动触发），触发下一页
				var lis = [];
				//以jQuery的Ajax请求为例，请求下一页数据（注意：page是从2开始返回）
				AjaxPostUtil.request({url:reqBasePath + "activitimode017", params:{processInstanceId: parent.processInstanceId}, type: 'json', callback: function(json){
		   			if(json.returnCode == 0){
		   				var jsonStr = "";//实体json对象
		   				$.each(json.rows, function(index, bean) {
	   						bean.showClass = 'date02';
		   					jsonStr = {
	   							bean: bean
	   						};
							lis.push(getDataUseHandlebars(timeTreeApprovalHistory, jsonStr));
						});
		   				//执行下一页渲染，第二参数为：满足“加载更多”的条件，即后面仍有分页
						//pages为Ajax返回的总页数，只有当前页小于总页数的情况下，才会继续出现加载更多
						next(lis.join(''), (page * 1000) < json.total);
		   			}else{
		   				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
		   			}
		   		}});
			}
		});
	}
	
	$("body").on("click", ".enclosureItem", function(){
    	download(fileBasePath + $(this).attr("rowpath"), $(this).html());
    });
    
    $("body").on("click", "#processInstanceIdImg", function(){
		systemCommonUtil.showPicImg($(this).attr("src"));
    });
    
    // 取消
    $("body").on("click", "#cancle", function(){
    	parent.layer.close(index);
    });

    exports('approvalProcess', {});
});
