var rowId = parent.rowId;//页面Id
var jsonArray = [];//从数据库获取的控件数组
var layedit, form;

// 表单控件集合
var formPageControl = [];

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'jqueryUI', 'tagEditor'].concat(dsFormUtil.mastHaveImport), function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
    var $ = layui.$;
		layedit = layui.layedit,
		form = layui.form;
	
	authBtn('1567732055673');//保存控件
	
	AjaxPostUtil.request({url: flowableBasePath + "dsformpage004", params: {pageId: parent.rowId}, type: 'json', method: 'GET', callback: function (json) {
		// 加载表单控件
		loadLeftBoxItem();
		// 加载拖拽
		setup_draggable();
		// 加载页面内容
		loadPageMation(json);
 	}});
 	
 	// 加载新增加的控件信息
    function loadNewControl(item) {
    	if(item.associatedDataTypes == 1){//json串
			var obj = item.aData;
			if(typeof item.aData == 'string'){
				obj = JSON.parse(item.aData);
			}
			item.context = getDataUseHandlebars(item.templateContent, obj);
		} else if (item.associatedDataTypes == 2){//接口
			AjaxPostUtil.request({url: flowableBasePath + "dsformpage011", params: {interfa: item.aData}, type: 'json', callback: function(j){
				var obj = JSON.parse(j.bean.aData);
				item.context = getDataUseHandlebars(item.templateContent, obj);
	   		}, async: false});
		}
		var jsonStr = {bean: item};
		var html = getDataUseHandlebars('{{#bean}}' + item.htmlContent + '{{/bean}}', jsonStr);
		var html_js = getDataUseHandlebars('{{#bean}}' + item.jsContent + '{{/bean}}', jsonStr);
		var jsCon = '<script>layui.define(["jquery"], function(exports) {var jQuery = layui.jquery;(function($) {' + html_js + '})(jQuery);});</script>';
		$(html).appendTo($("#showForm").get(0)).attr("rowid", item.id);
		$("#showForm").append(jsCon);
		jsonArray.push(item);
		form.render();
    }

	function loadPageMation(json){
		$.each(json.rows, function(i, item) {
			if(parseInt(item.state) == 1){
				// 加载非删除状态的数据
				loadNewControl(item);
			} else {
				jsonArray.push(item);
			}
		});
		loadFormItemDrop();
		matchingLanguage();
	}

 	function loadFormItemDrop(){
 		$.each($("#showForm").find(".layui-form-item"), function(i, item) {
 			var _this = $(item);
 			if(!_this.hasClass('form-group')){
 				_this.addClass('form-group');
 			}
 			if(!_this.hasClass('draggable')){
 				_this.addClass('draggable');
 			}
 			if(!_this.hasClass('ui-draggable')){
 				_this.addClass('ui-draggable');
 			}
 			if(!_this.hasClass('dropped')){
 				_this.addClass('dropped');
 			}
 		});
 	}
 	
 	// 加载表单控件
 	function loadLeftBoxItem(){
 		showGrid({
		 	id: "btnBox",
		 	url: flowableBasePath + "queryDsFormComponentMationToShow",
		 	params: {},
		 	pagination: false,
			method: 'GET',
		 	template: $("#leftBoxItem").html(),
			ajaxSendLoadBefore: function (hdb, json) {
				$.each(json.bean, function (key, value) {
					$.each(value, function (j, bean) {
						bean.logo = systemCommonUtil.initIconShow(bean);
					});
				});
			},
		 	ajaxSendAfter:function (json) {
		 		formPageControl = [].concat(json.rows);
		 		form.render();
		 	}
		});
 	}
 	
 	var setup_draggable = function() {
	    $(".draggable").draggable({
	        appendTo: "body",
	        helper: "clone",
			drag: function (event, ui) {
	        	
			},
			stop: function () {

			}
	    });
	    $(".droppable").droppable({
	        accept: ".draggable",
	        helper: "clone",
	        hoverClass: "droppable-active",
			drop: function(event, ui) {
	        	$(".empty-form").remove();
	        	var _this = $(ui.draggable);
	            if (!_this.hasClass("dropped")) {
	            	getFormPageControlContent(_this.attr("rowid"));
	            } else {
	                if ($(this)[0] != _this.parent()[0]) {
	                    var $el = _this.clone().appendTo(this);
	                    _this.remove()
	                }
	            }
	        }
	    }).sortable();
	};
	
	function getFormPageControlContent(id) {
		var linkedData; //控件关联的数据
		var defaultData; //选择事件的默认数据
		var tplContentVal; //数据展示模板的内容的值
		var templateContent; //数据展示模板的内容
		$.each(formPageControl, function(i, item) {
			if(item.id == id){
				linkedData = item.linkedData;
				templateContent = item.templateContent;
				if (!isNull(item.templateContent)) {
					tplContentVal = strMatchAllByTwo(item.templateContent, '{{','}}');//取出数据模板中用{{}}包裹的词
	 				removeByValue(tplContentVal, "#each this");
	 				removeByValue(tplContentVal, "/each");
				}
				if (!isNull(item.defaultData)){
					defaultData = item.defaultData;
				}
			}
		});
		var params  = {
    		pageId: rowId,
			defaultWidth: 'layui-col-xs12',
			title: '标题',
			linkedData: linkedData,
			require: '',
			placeholder: '',
			defaultValue: '',
			formContentId: id,
			editableNodeId: '',
			editableNodeName: '',
			keyId: ''
			
       	};
		if(linkedData == 1){
			// 自定义数据类型
    		params.associatedDataTypes = 1;
    		var defaultKey = getOutKey(defaultData);//取出json串的键
			if(subset(tplContentVal, defaultKey)){
				params.aData = defaultData;
			} else {
				winui.window.msg('json串内容有误，请重新填写!', {icon: 2, time: 2000});
        		return false;
			}
    	} else if (linkedData == 2){
    		params.associatedDataTypes = "";
    		params.aData = "";
    	}
       	params = getDataScript(params);
       	// 保存控件
       	reqSaveData(params, templateContent);
	}
	
	// 获取该控件的脚本信息
	function getDataScript(params){
		AjaxPostUtil.request({url: flowableBasePath + "queryDsFormContentMationById", params: {id: params.formContentId}, type: 'json', method: 'GET', callback: function (json) {
			params.htmlContent = encodeURIComponent(json.bean.htmlContent);
			params.jsContent = encodeURIComponent(json.bean.jsContent);
    	}, async: false});
    	return params;
	}
	
	// 保存“新增控件”
    function reqSaveData(params, templateContent){
    	AjaxPostUtil.request({url: flowableBasePath + "dsformpage003", params: params, type: 'json', callback: function (json) {
			winui.window.msg(systemLanguage["com.skyeye.successfulOperation"][languageType], {icon: 1, time: 2000});
			var templateJson = json.bean;
			templateJson.biaoJi = "1";
			templateJson.templateContent = templateContent;
			loadNewControl(templateJson);
			loadFormItemDrop();
		}, async: false});
    }
    
    // 页面控件点击事件
    $("body").on("click", ".form-group", function() {
    	$(".form-group").removeClass('ui-sortable-placeholder-choose');
    	$(this).addClass("ui-sortable-placeholder-choose");
    	var rowid = $(this).attr("rowid");
    	var arr = [];
    	$.each(jsonArray, function(i, item) {
			if(item.id === rowid){
				$("#btnBoxDesignForm").html(getDataUseHandlebars($("#controlItemEdit").html(), {bean: item}));
				$("#deleteBtn").attr("rowid", rowid);
				if (item.linkedData == 1) {
					// 允许关联数据
					var associatedDataTypes = item.associatedDataTypes;
					$("#isAssociated").removeClass("layui-hide");
					associatedDataTypesChange(associatedDataTypes);
					if (associatedDataTypes == "1") {
						var obj = item.aData;
						if (typeof item.aData != 'string') {
							obj = JSON.stringify(item.aData);
						}
						// json串
						$("#JsonData").val(obj);
					} else if (associatedDataTypes == "2") {
						// 接口
						$("#nterfac").val(item.aData);
					}
					$("input:radio[name=associatedDataTypes][value=" + associatedDataTypes + "]").attr("checked", true);
				}
				$("#defaultWidth").val(item.defaultWidth);
				form.render();
				form.on('submit(formAddBean)', function (data) {
					if (winui.verifyForm(data.elem)) {
						saveNodeData(data, rowid, arr);
						winui.window.msg("保存成功", {icon: 1, time: 2000});
					}
					return false;
				});
				
				// 对限制条件的监听
		 		form.on('select(require)',function(data) {
		 			arr = data.value;
		 		});
				
				// 可编辑节点Id
			    $('#editableNodeId').tagEditor({
			        placeholder: '请输入可编辑节点Id'
			    });
			    
			    // 可编辑节点名称
			    $('#editableNodeName').tagEditor({
			        placeholder: '请输入可编辑节点名称'
			    });
				initRequire(item);
				matchingLanguage();
				return;
			}
		});
    });
    
    // 删除操作
    $("body").on("click", "#deleteBtn", function (e) {
		var rowid = $(this).attr("rowid");
		$("#showForm div[rowid='" + rowid + "']").remove();
		$.each(jsonArray, function(i, item) {
			if(item.id === rowid){
				jsonArray[i].state = 0;
			}
		});
		$("#btnBoxDesignForm").empty();
	});
    
    function saveNodeData(data, rowid, arr){
    	var inDataIndex = -1;
    	$.each(jsonArray, function(i, item) {
			if(item.id === rowid){
				inDataIndex = i;
			}
    	});
    	if(inDataIndex == -1){
    		return;
    	}
		var newParams = jsonArray[inDataIndex];
		newParams.labelContent = $("#title").val();
		newParams.placeholder = $("#placeholder").val();
		newParams.requireId = arr.join(",");
		newParams.value = $("#defaultValue").val();
		newParams.defaultWidth = $("#defaultWidth").val();
		newParams.keyId = $("#keyId").val();
		newParams.editableNodeId = data.field.editableNodeId;
		newParams.editableNodeName = data.field.editableNodeName;
		var linkedData; //控件关联的数据
		var defaultData; //选择事件的默认数据
		var tplContentVal; //数据展示模板的内容的值
		var templateContent; //数据展示模板的内容
		$.each(formPageControl, function (i, item) {
			if (item.id == newParams.formContentId) {
				linkedData = item.linkedData;
				templateContent = item.templateContent;
				if (!isNull(item.templateContent)) {
					tplContentVal = strMatchAllByTwo(item.templateContent, '{{', '}}');//取出数据模板中用{{}}包裹的词
					removeByValue(tplContentVal, "#each this");
					removeByValue(tplContentVal, "/each");
				}
				if (!isNull(item.defaultData)) {
					defaultData = item.defaultData;
				}
			}
		});
		if (newParams.linkedData == 1) {
			newParams.associatedDataTypes = data.field.associatedDataTypes;
			if (newParams.associatedDataTypes == 1) {
				var defaultDataStr = $("#JsonData").val();
				if (isNull(defaultDataStr)) {
					winui.window.msg("请填写Json串！", {icon: 2, time: 2000});
					return false;
				} else {
					if (isJSON(defaultDataStr)) {
						var defaultKey = getOutKey(defaultDataStr);//取出json串的键
						if (subset(tplContentVal, defaultKey)) {
							newParams.aData = defaultDataStr;
						} else {
							winui.window.msg('json串内容有误，请重新填写!', {icon: 2, time: 2000});
							return false;
						}
					} else {
						winui.window.msg('json串格式不正确，请重新填写!', {icon: 2, time: 2000});
						return false;
					}
				}
			} else if (newParams.associatedDataTypes == 2) {
				var interfa = $("#nterfac").val();
				if (interfa.length == 0) {
					winui.window.msg("请填写接口！", {icon: 2, time: 2000});
					return false;
				}
				if (!checkURL(interfa)) {
					winui.window.msg("接口请填写为URL类型！", {icon: 2, time: 2000});
					return false;
				}
				newParams.aData = interfa;
			} else {
				winui.window.msg("状态值错误。", {icon: 2, time: 2000});
				return false;
			}
		} else if (newParams.linkedData == 2) {
			newParams.associatedDataTypes = "";
			newParams.aData = "";
		}
    	jsonArray = jsonArray.map(t => {
    		return t.id === rowid ? newParams : t;
    	});
    	sortDataIn();
    	$(".mask-req-str").remove();
    }
    
    function sortDataIn(){
    	// 对控件进行排序
    	sortNodeData();
    	$("#showForm").empty();
    	jsonArray.sort(getSortFun('asc','orderBy'));
    	var newJson = [].concat(jsonArray);
    	jsonArray = [];
    	loadPageMation({rows: newJson});
    }
    
    function sortNodeData(){
    	$.each(jsonArray, function(i, item) {
    		if(parseInt(item.state) == 1){
	    		var inIndex = $("#showForm div[rowid='" + item.id + "']").index();
	    		jsonArray[i].orderBy = inIndex / 2 + 1;
    		}
    	});
    }
    
    // 关联数据类型变化事件
	form.on('radio(associatedDataTypes)', function (data) {
    	associatedDataTypesChange(data.value);
    });
    
    // 关联数据类型变化
    function associatedDataTypesChange(val){
    	if(val == '1'){//Json串
    		$(".TypeIsTwo").addClass("layui-hide");
    		$(".TypeIsOne").removeClass("layui-hide");
    	} else if (val == '2'){//接口
    		$(".TypeIsTwo").removeClass("layui-hide");
    		$(".TypeIsOne").addClass("layui-hide");
    	} else {
    		winui.window.msg('状态值错误', {icon: 2, time: 2000});
    	}
    }
    
    // 初始化限制条件
	function initRequire(item) {
		AjaxPostUtil.request({url: flowableBasePath + "dsformlimitrequirement006", params: {}, type: 'json', callback: function (json) {
			var jsonStr = getDataUseHandlebars(getFileContent('tpl/template/select-option.tpl'), json);//模板和数据结合
			$("#require").html(jsonStr);
			if (!isNull(item.requireId)){
				$("#require").val(item.requireId.split(","));//给这个元素赋值
			}
			form.render();
   		}});
	}
	
	$("body").on("keyup", ".keyIdName", function() {
		$(this).val($(this).val().replace(/[^\w\.\/]/ig,''));
	});
    
	//保存
	$("body").on("click", "#save", function() {
		sortDataIn();
		if(jsonArray.length == 0){
			winui.window.msg('保存页面不能为空！', {icon: 2, time: 2000});
			return;
		}
		AjaxPostUtil.request({url: flowableBasePath + "dsformpage009", params: {formedit: JSON.stringify(jsonArray)}, type: 'json', callback: function (json) {
			winui.window.msg("保存成功", {icon: 1, time: 2000});
			parent.refreshCode = '0';
   		}});
    });
});