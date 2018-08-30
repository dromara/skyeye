
layui.define(['layer', "fsCommon"], function(exports) {
	var layer = layui.layer,
		fsCommon = layui.fsCommon,
		FsTree = function() {
			this.config = {
				funcNo: undefined, //功能号
				url: undefined, //请求url地址
				id: "",
				isRoot: true, //是否显示根目录，默认显示
				clickCallback: undefined, //点击回调函数
				onDblClick: undefined,//双击之后的回调函数
				enable: true,//异步还是同步加载，默认同步
				checkEnable: false,//是否显示复选框,默认为否
				loadEnable: true,//数据是否异步加载,默认为是
			}
		};

	/**
	 * 渲染tree
	 */
	FsTree.prototype.render = function(options) {
		var _this = this;

		$.extend(true, _this.config, options);
		if(isNull(_this.config.id)) {
			fsCommon.warnMsg("id不能为空!");
			return;
		}
		var domTree = $("#" + _this.config.id);
		_this.config.treeName = domTree.attr("treeName");
		_this.config.treeIdKey = domTree.attr("treeIdKey");
		_this.config.treePIdKey = domTree.attr("treePIdKey");
		_this.config.checkType = domTree.attr("checkType");
		_this.config.isDrag = domTree.attr("isDrag"); //是否拖拽

		_this.queryTree();
		_this.rightMenu();
		return _this;
	};

	/**
	 * 右键菜单
	 */
	FsTree.prototype.rightMenu = function() {
		var _this = this;
		//查询菜单列表
		var treeDom = $("#" + _this.config.id);
		var fsTreeRightMenu = treeDom.next(".fsTreeRightMenu");
		if(fsTreeRightMenu.length == 0) {
			return;
		}

		var _rightMenu = {};
		$.each(fsTreeRightMenu.children(), function() {
			var thisMenu = $(this);
			var uuid = $.uuid();
			thisMenu.attr("id", uuid);
			var obj = {};
			obj["name"] = thisMenu.attr("name");
			var icon2 = thisMenu.attr("icon");
			var icon = thisMenu.attr("icon");
			if(icon == "add") {
				icon = "fa-plus";
			} else if(icon == "refresh") {
				icon = "fa-refresh";
			} else if(icon == "edit") {
				icon = "fa-edit";
			} else if(icon == "del") {
				icon = "fa-trash";
			}
			obj["icon"] = icon;
			var disabled = thisMenu.attr("disabledType"); //禁用模式
			if(!isNull(disabled)) {
				obj["disabled"] = function() {
					var disableds = disabled.split("|");
					for(var i = 0; i < disableds.length; i++) {
						if(disableds[i] == "parent") { //父栏目禁用
							var tid = $(this).parent().attr("id");
							var zTree = $.fn.zTree.getZTreeObj(_this.config.id);
							var node = zTree.getNodeByTId(tid);
							if(null != node && node["isParent"]) { //父栏目禁用
								return true;
							}
						} else if(disableds[i] == "rootNode") {
							var tid = $(this).parent().attr("id");
							var zTree = $.fn.zTree.getZTreeObj(_this.config.id);
							var node = zTree.getNodeByTId(tid);
							if(null != node && node.level === 0) { //根节点禁用
								return true;
							}
						}
					}
					return false;
				};
			}
			_rightMenu[uuid] = obj;
		});

		$.contextMenu({
			selector: "#" + _this.config.id + " a",
			callback: function(key, options) {
				var tid = $(this).parent().attr("id");
				var _thisButton = $("#" + key);
				fsCommon.buttonCallback(_thisButton, _this.config.getTree, tid);
			},
			items: _rightMenu
		});
	}

	//显示树
	FsTree.prototype.showTree = function(data) {
		var _this = this;
		var funcNo = _this.config.funcNo;
		var url = _this.config.url; //请求url
		if(isNull(funcNo) && isNull(url)) {
			fsCommon.warnMsg("功能号或请求地址为空!");
			return;
		}
		if(isNull(url)) {
			url = "/fsbus/" + funcNo;
		}
		var servletUrl = reqBasePath;
		if(!isNull(servletUrl)) {
			url = servletUrl + url;
		}
		var checkType = _this.config.checkType;
		var setting = {
			view: {
				showLine: true,
			},
			data: {
				key: {
					name: _this.config.treeName,
				},
				simpleData: {
					enable: _this.config.enable,
					idKey: _this.config.treeIdKey,
					pIdKey: _this.config.treePIdKey,
					rootPId: 0
				}
			},
			async: { //异步加载
                type: "get",
                enable: _this.config.loadEnable,
                url: reqBasePath + _this.config.url,
                autoParam : ["id=parentId"],//异步加载时需要自动提交父节点属性的参数
                dataType:"json",
                dataFilter: function(treeId, treeNode, responseData) {
                	if (responseData.rows.length > 0) {
                		$.fn.zTree.getZTreeObj(treeId).addNodes(treeNode, responseData.rows);
                	}
                },
			},
			check : {
	            enable : _this.config.checkEnable,
	            chkStyle : "checkbox",    //复选框
	            chkboxType : {
	                "Y" : "ps",
	                "N" : "ps"
	            }
	        },
			edit: edit,
			callback: {
				onClick: _this.config.clickCallback,
				onAsyncSuccess: function() {
					//判断是否选中，如果有选择的值，那么默认选中根节点
					if("checkbox" == checkType || "radio" == checkType) {
						var zTree = $.fn.zTree.getZTreeObj(_this.config.id);
						var node = zTree.getNodeByParam(_this.config.treeIdKey, 0, null);
						if(null != node) {
							var checkedNode = zTree.getCheckedNodes(true);
							if(checkedNode.length > 1 || (checkedNode.length == 1 && checkedNode[0][_this.config.treeIdKey] !== 0)) {
								node.checked = true;
							} else {
								node.checked = false;
							}
							zTree.refresh();
						}
					}
				},
				onDblClick: _this.config.onDblClick,
			}
		};

		if("checkbox" == checkType || "radio" == checkType) {
			var check = {};
			check["enable"] = true;
			check["chkStyle"] = checkType;
			setting["check"] = check;
		}

		if(_this.config.isDrag == "1") { //允许拖拽
			var edit = {};

			edit["enable"] = true;
			edit["showRemoveBtn"] = false;
			edit["showRenameBtn"] = false;
			setting["edit"] = edit;

		}
		$.fn.zTree.init($("#" + _this.config.id), setting, data);
	};

	//查询菜单树列表
	FsTree.prototype.queryTree = function() {
		var _this = this;
		var funcNo = _this.config.funcNo;
		var url = _this.config.url; //请求url

		if(isNull(funcNo) && isNull(url)) {
			fsCommon.warnMsg("功能号或请求地址为空!");
			return;
		}
		if(isNull(url)) {
			url = "/" + funcNo;
		}
		var domTree = $("#" + _this.config.id);

		domTree.empty();
		var otherParam = {}; //业务参数
		var inputs = domTree.attr("inputs");
		if(!isNull(inputs)) {
			//参数处理，如果有参数，自动带入条件
			var urlParam = fsCommon.getUrlParam();
			var paramObj = fsCommon.getParamByInputs(inputs, urlParam);
			if(!isNull(paramObj)) {
				$.extend(otherParam, paramObj);
			}
		}
		//处理查询表单
		var defaultForm = domTree.attr("defaultForm");
		if(!isNull(defaultForm)) {
			var fromData = $("#" + defaultForm).getFormData(true);
			$.extend(otherParam, fromData);
		}
		_this.config.otherParam = otherParam;

		if(domTree.attr("isLoad") === "0") {
			var array = new Array();
			if(domTree.attr("isRoot") !== "0") { //是否显示根目录
				var arr = {};
				arr["open"] = true;
				arr["isParent"] = true;
				arr["drag"] = false;
				arr[_this.config.treeName] = "全部";
				arr[_this.config.treeIdKey] = 0;
				array.push(arr);
			}
			_this.showTree(array);
		} else {
			var method = domTree.attr("method"); //请求方式
			if(!isNull(otherParam)){
				if(isNull(otherParam.parentId)){
					otherParam.parentId = "0";
				}
			}
			fsCommon.invoke(url, otherParam, function(data) {
				if(data.returnCode == 0) {
					var array = data.rows;
					if(!$.isArray(array)) {
						array = new Array();
					}
					if(domTree.attr("isRoot") !== "0") { //是否显示根目录
						var arr = {};
						arr["open"] = true;
						arr["isParent"] = true;
						arr["drag"] = false;
						arr[_this.config.treeName] = "全部";
						arr[_this.config.treeIdKey] = 0;
						array.push(arr);
					}
					_this.showTree(array);
				} else {
					//提示错误消息
					fsCommon.warnMsg(data.returnMessage, {icon: 0});
				}
			}, false, method);
		}

	}

	/**
	 * 刷新节点,tid不为空，刷新当前tid节点数据，否则刷新全部数据
	 */
	FsTree.prototype.refresh = function(tid) {
		var _this = this;
		var zTree = $.fn.zTree.getZTreeObj(_this.config.id),
			type = "refresh",
			silent = false;

		var node2 = zTree.getNodeByTId(tid);
		var treeId = null;
		if(!isNull(node2)) {
			treeId = node2[_this.config.treeIdKey];
		}

		_this.queryTree();

		var domTree = $("#" + _this.config.id);
		if(domTree.attr("isRoot") !== "0") { //是否显示根目录
			node = zTree.getNodesByFilter(function(node) {
				return node.level == 0
			}, true);
		}
		if(!isNull(tid) && !isNull(treeId)) {
			//选中刷新的菜单
			var node3 = zTree.getNodeByParam(_this.config.treeIdKey, treeId, null);
			zTree.selectNode(node3); //选中
			zTree.setting.callback.onClick(null, zTree.setting.treeId, node3); //调用事件
		}
	}

	/**
	 * 查询
	 */
	FsTree.prototype.query = function(param) {
		//处理查询表单
		var _this = this;
		var zTree = $.fn.zTree.getZTreeObj(_this.config.id);
		zTree.setting.async.otherParam = param;
		//强制加载
		$("#" + _this.config.id).attr("isLoad", "1");
		_this.refresh();
	}

	/**
	 * 获取选中的节点
	 */
	FsTree.prototype.getCheckData = function(tid) {
		var _this = this;
		var zTree = $.fn.zTree.getZTreeObj(_this.config.id);
		if(isNull(tid)) {

			//判断是否复选框选择
			var checkType = $("#" + _this.config.id).attr("checkType");
			if("checkbox" == checkType || "radio" == checkType) {
				return zTree.getCheckedNodes(true);
			} else {
				return zTree.getSelectedNodes();
			}
		}
		var arr = new Array();
		arr.push(zTree.getNodeByTId(tid));
		return arr;
	}

	var fsTree = new FsTree();
	exports("fsTree", fsTree);
});