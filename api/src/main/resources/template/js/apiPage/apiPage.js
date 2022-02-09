
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'cookie', 'orgChart'], function (exports) {
	winui.renderColor();
	layui.use(['form', 'table'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
		var $ = layui.$,
			table = layui.table,
			active = {
				parseTable: function() {
					table.init('parse-table-demo', { //转化静态表格
					});
				}
			};
		var type = 'parseTable';
		var defaultTemplate = $('#defaultTemplate').html(),
			fileListTemplate = $('#fileListTemplate').html(),
			fileMationTemplate = $('#fileMationTemplate').html(),
			menuBoxTemplate = $("#menuBoxTemplate").html(),
			modelTemplate = $("#modelTemplate").html();

		MiniSite.JsLoader.load("../../json/main.js", function(){
			$.cookie('sysMainMation', "", {path: '/' });
			setCookie('sysMainMation', JSON.stringify(sysMainMation), 'd30');
			$(document).attr("title", sysMainMation.mationTitle);
			$(".sys-logo").html(sysMainMation.mationTitle);
		});

		loadUserMation();
		//加载用户信息
		function loadUserMation(){
			if(!isNull(getCookie('userToken'))){
				AjaxPostUtil.request({url:reqBasePath + "login002", params:{}, type:'json', callback:function(json){
					if(json.returnCode == 0){
						var str = '<img alt="' + json.bean.userName + '" src="' + fileBasePath + json.bean.userPhoto + '"/>'
						+ '<font>' + json.bean.userName + '</font>'
						+ '<font id="consoleDesk">控制台</font>'
						+ '<font id="exitBtn">退出</font>';
						$("#operatorBtn").html(str);
					}else{
						location.href = "../../tpl/index/login.html?url=" + escape("../../tpl/apiPage/apiPage.html");
					}
					loadApiMicroservices();
				}});
			}else{
				location.href = "../../tpl/index/login.html?url=" + escape("../../tpl/apiPage/apiPage.html");
			}
		}

		/**
		 * 加载微服务
		 */
		function loadApiMicroservices(){
			showGrid({
				id: "apiMicroservicesId",
				url: reqBasePath + "queryApiMicroservices",
				params: {},
				pagination: false,
				template: $("#apiMicroservicesTemplate").html(),
				method: "GET",
				ajaxSendLoadBefore: function(hdb){
				},
				ajaxSendAfter:function(j){
					form.render('select');
				}
			});
		}

		form.on('select(apiMicroservicesId)', function (data) {
			var value = data.value;
			if (isNull(value)) {
				$("#modelId").html("");
				$("#appList").html("");
			} else {
				loadListMation(value);
			}
		});
		
		// 模块
		var model = new Array();
		// 分组
		var groupList = new Array();
		// 接口数据
		var jsonList;
		/**
		 * 加载接口列表
		 */
		function loadListMation(appId){
			AjaxPostUtil.request({url: reqBasePath + "queryAllSysEveReqMapping", params: {appId: appId}, type: 'json', method: "GET", callback: function(json){
				if(json.returnCode == 0){
					// 1.模块
					model = loadModel(json);
					$("#modelId").html(getDataUseHandlebars(getFileContent('tpl/template/select-option-must.tpl'), {rows: model}));
					$("#appList").html(getDataUseHandlebars(modelTemplate, {rows: model}));
					// 2.分组
					groupList = loadBox(json);
					jsonList = json.rows;
	   				// 默认加载第一个模块的接口数据
					loadPointNumApiList(model[0].id);
					matchingLanguage();
					form.render();
					loadDefaultMain();
				}else{
					winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
				}
			}});
		}

		/**
		 * 加载指定模块的接口
		 * @param modelId 模块id
		 */
		function loadPointNumApiList(modelId){
			$("#model" + modelId).html("");
			// 分组信息加入页面
			$.each(groupList, function(i, item){
				if(item.modelId === modelId){
					$("#model" + item.modelId).append(getDataUseHandlebars(menuBoxTemplate, {bean: item}));
				}
			});
			// 接口信息加入页面
			$.each(jsonList, function(i, item){
				// 获取当前接口所属模块的下标
				var thisModelIndex = getModelIndex(model, item.modelName);
				if(model[thisModelIndex].id === modelId){
					var groupName = "未分类接口";
					if(!isNull(item.groupName)){
						groupName = item.groupName;
					}
					var boxIndex = getBoxIndex(groupList, groupName, item.modelName);
					$("#child" + groupList[boxIndex].boxId).append(
						getDataUseHandlebars(fileListTemplate, {bean: item}));
				}
			});
		}
		
		form.on('select(modelId)', function(data){
			if(isNull(data.value) || data.value === '请选择'){
				$("#departmentId").html("");
				form.render('select');
			}else{
				$("#appList").find(".layui-nav-tree").hide();
		    	$("#appList").find("ul[id='model" + data.value + "']").show();
				loadPointNumApiList(data.value);
			}
		});

		// 构造模块数据
		function loadModel(json){
			var box = [];
			$.each(json.rows, function(i, item){
				if(getModelIndex(box, item.modelName) == -1){
					box.push({
						id: getRandomValueToString(),
						name: item.modelName,
						show: ((box.length == 0) ? "block" : "none")
					});
				}
			});
			return box;
		}

		// 根据模块名称获取模块下标
		function getModelIndex(box, str){
			var inIndex = -1;
			$.each(box, function(i, item){
				if(item.name == str){
					inIndex = i;
					return false;
				}
			});
			return inIndex;
		}
		
		function loadBox(json){
			var box = [];
			// 为每个工程模块都加入'未分类接口'
			$.each(model, function(i, item){
				box.push({
					boxId: getRandomValueToString(),
					modelId: model[i].id,
					boxName: '未分类接口'
				});
			});
			// 获取已分类接口
			$.each(json.rows, function(i, item){
				if(!isNull(item.groupName) && getBoxIndex(box, item.groupName, item.modelName) == -1){
					box.push({
						boxId: getRandomValueToString(),
						modelId: model[getModelIndex(model, item.modelName)].id,
						boxName: item.groupName
					});
				}
			});
			return box;
		}

		/**
		 * 获取分组中的下标
		 * @param box 分组集合
		 * @param groupName 分组名称
		 * @param modelName 模块名称
		 * @returns {number}
		 */
		function getBoxIndex(box, groupName, modelName){
			var inIndex = -1;
			var modelId = model[getModelIndex(model, modelName)].id;
			$.each(box, function(i, item){
				if(item.boxName == groupName && modelId == item.modelId){
					inIndex = i;
					return false;
				}
			});
			return inIndex;
		}
		
		//接口列表实体点击
		$("body").on("click", "#appList .api-item", function(){
			if($(this).hasClass("active")){//当前显示项为当前点击项，不做任何操作
			}else{
				$("#appList .api-item").removeClass("active");
				$(this).addClass("active");
				var params = {
					appId: $("#apiMicroservicesId").val(),
					rowId: $(this).attr("rowid")
				};
				AjaxPostUtil.request({url: reqBasePath + "queryApiDetails", params: params, type: 'json', method: "GET", callback: function(json){
					if(json.returnCode == 0){
		   				var str = getDataUseHandlebars(fileMationTemplate, json);
		   				$("#contentDesc").html(str);
						active[type] ? active[type].call(this) : '';
					}else{
						winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
					}
				}});
			}
		});
		
		//退出
		$("body").on("click", "#exitBtn", function(){
			winui.window.confirm('确认注销吗?', {id: 'exit-confim', icon: 3, title: '提示', skin: 'msg-skin-message', success: function(layero, index){
				var times = $("#exit-confim").parent().attr("times");
				var zIndex = $("#exit-confim").parent().css("z-index");
				$("#layui-layer-shade" + times).css({'z-index': zIndex});
			}}, function (index) {
				layer.close(index);
	        	AjaxPostUtil.request({url:reqBasePath + "login003", params:{}, type:'json', callback:function(json){
	        		$.cookie('userToken', "", {path: '/' });
	        		location.href = "../../tpl/index/login.html?url=" + escape("../../tpl/apiPage/apiPage.html");
	 	   		}});
	        });
		});
		
		$("body").on("click", ".menu-box-none", function(e){
	    	if($(this).parent().hasClass("layui-nav-itemed")){
	    		$(this).parent().removeClass("layui-nav-itemed");
	    	}else{
	    		$(this).parent().addClass("layui-nav-itemed");
	    	}
	    });
		
		// 控制台
		$("body").on("click", "#consoleDesk", function(){
			location.href = "../../tpl/index/index.html";
		});

		// 左上角logo点击
		$("body").on("click", ".sys-logo", function(){
			loadDefaultMain();
		});

		function loadDefaultMain(){
			AjaxPostUtil.request({url: reqBasePath + "queryLimitRestrictions", params: {}, type: 'json', method: "GET", callback: function(json){
				if(json.returnCode == 0){
					var item = {};
					item.reception = getReceptionLimitMation();
					item.backstage = json.rows;
					$("#contentDesc").html(getDataUseHandlebars(defaultTemplate, {bean: item}));
					$('#chart-container').orgchart({
						'data' : getXMLAttribute(),
						'nodeTitle': 'title',
						'nodeContent': 'name',
						'pan': true,
						'zoom': true,
						'interactive': false
					});
					form.render();
				}else{
					winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
				}
			}});
		}

		// 导出为MD文档
		$("body").on("click", "#exportMD", function(){
			var params = {
				appId: $("#apiMicroservicesId").val(),
				rowId: $(this).attr("apiId")
			};
			var fileName = $("#apiMicroservicesId").find("option:selected").text() + "-" + $(this).attr("fileName");
			AjaxPostUtil.request({url: reqBasePath + "queryApiDetails", params: params, type: 'json', method: "GET", callback: function(json){
				if(json.returnCode == 0){
					var str = getDataUseHandlebars(getFileContent('tpl/apiPage/mdModelFile.tpl'), json);
					sysFileUtil.saveAs(new Blob([str]), fileName);
				}else{
					winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
				}
			}});
		});

		// 获取前端限制条件
		function getReceptionLimitMation(){
			var list = new Array();
			$.each(winui.verify, function (key, value){
				list.push({
					key: key,
					value: value[2]
				})
			});
			return list;
		}

		function getXMLAttribute(){
			var params = {
				title: '标签controller',
				name: '一个xml中只会有一个controller',
				children: [{
					title: 'url[子集]',
					name: '一个新接口的定义',
					children: [{
						title: 'property[子集]',
						name: '接口中的入参',
						children: [{
							title: '属性id',
							name: '前台请求的入参',
						}, {
							title: '属性name',
							name: '后台接受的参数',
						}, {
							title: '属性ref',
							name: '参数的限制条件，多个用逗号隔开',
						}, {
							title: '属性var',
							name: '参数介绍',
						}]
					}, {
						title: '属性id',
						name: '前端请求的地址串',
					}, {
						title: '属性path',
						name: '后台映射的地址',
					}, {
						title: '属性val',
						name: '接口描述',
					}, {
						title: '属性allUse',
						name: '是否需要登录才能使用 1是 0否 2需要登陆才能访问，但无需授权 默认为否',
					}, {
						title: '属性method',
						name: '接口请求方式',
					}, {
						title: '属性groupName',
						name: '所属功能集',
					}]
				}, {
					title: '属性modelName',
					name: '介绍该模块是做什么功能的'
				}]
			}
			return params;
		}
		
	});
});

