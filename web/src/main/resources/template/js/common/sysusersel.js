
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'fsCommon', 'fsTree', 'element'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
		var $ = layui.$,
			fsTree = layui.fsTree,
			fsCommon = layui.fsCommon,
			element = layui.element;

		var userOperatorReturnList = new Array();
		
		var chooseOrNotMy = "1";//人员列表中是否包含自己--1.包含；其他参数不包含
		var chooseOrNotEmail = "1";//人员列表中是否必须绑定邮箱--1.必须；其他参数没必要
		var checkType = "1";//人员选择类型，1.多选；其他。单选
		//以下节点，父页面中可以不包含
		var ztreeCheckType = "checkbox";//树结构对应节点类型：单选还是多选
		var ztreeRadioCheck = "";//树节点类型为单选时，需要调用判断函数
		
		var urlParams = "";
		
		//人员展示模板
		var peopleTemplate = $('#peopleTemplate').html();
		//进行数据回显使用
		userOperatorReturnList = [].concat(parent.userReturnList);
		
		//遍历加载已选人员列表
		for(var j = 0; j < userOperatorReturnList.length; j++){
    		var str = getDataUseHandlebars(peopleTemplate, {bean: userOperatorReturnList[j]});
    		$("#selPeopleList").append(str);
    	}
		
		//如果父页面有这个参数，则赋值
		if(!isNull(parent.chooseOrNotMy)){
			chooseOrNotMy = parent.chooseOrNotMy;
		}
		if(!isNull(parent.chooseOrNotEmail)){
			chooseOrNotEmail = parent.chooseOrNotEmail;
		}
		if(!isNull(parent.checkType)){
			checkType = parent.checkType;
		}
		
		//设置提示信息
		var s = "人员选择规则：";
		if(chooseOrNotMy == "1"){
			s += '1.包含自己；';
		}else{
			s += '1.不包含自己；';
		}
		if(chooseOrNotEmail == "1"){
			s += '2.必须绑定邮箱；';
		}else{
			s += '2.无需绑定邮箱；';
		}
		if(checkType == "1"){
			s += '3.多选。';
		}else{
			s += '3.单选。';
		}
		s += '如没有查到要选择的人员，请检查人员信息是否满足当前规则。';
		$("#showInfo").html(s);
		
		//树节点类型赋值
		if(checkType === "1"){
			ztreeCheckType = "checkbox";
		}else{
			ztreeCheckType = "radio";
			ztreeRadioCheck = function(treeId, treeNode){
		    	if(treeNode.folderType === 'isPeople'){//单选如果选中节点是人，则允许
		    		return true;
		    	}else{
		    		winui.window.msg('此节点不是用户节点，不可选中!', {icon: 5,time: 2000});
		    		return false;
		    	}
		    };
		}
		
		//加载树时的参数传递
		urlParams = "?userToken=" + getCookie('userToken') + "&loginPCIp=" + returnCitySN["cip"] + "&chooseOrNotMy=" + chooseOrNotMy + "&chooseOrNotEmail=" + chooseOrNotEmail;

		//各模块的初始化加载情况 false为没加载，true为加载
		var allPeopleFirstLoad = false,//所有人员模块是否初始化加载
			accordingCompanyFirstLoad = false,//按公司模块是否初始化加载
			accordingDepartmentFirstLoad = false,//按部门模块是否初始化加载
			accordingJobFirstLoad = false,//按岗位模块是否初始化加载
			accordingSimpleDepartmentFirstLoad = false,//同部门人员模块是否初始化加载
			accordingGroupFirstLoad = false;//按联系组模块是否初始化加载
		
		//树对象
		var allPeopleTree = null,//所有人员模块树对象
			accordingCompanyTree = null,//按公司模块树对象
			accordingDepartmentTree = null,//按部门模块树对象
			accordingJobTree = null,//按岗位模块树对象
			accordingSimpleDepartmentTree = null,//同部门人员模块树对象
			accordingGroupTree = null;//按联系组模块树对象
		
		matchingLanguage();
		form.render();
	    form.on('submit(fileUploadStart)', function (data) {
	        if (winui.verifyForm(data.elem)) {
	        	uoloadObj.upload();
	        }
	        return false;
	    });
	    
	    //默认加载所有人员模块
	    loadAllPeopleList();
	    
	    //取消
	    $("body").on("click", "#cancle", function(){
	    	parent.layer.close(index);
	    });
	    
	    //确定
	    $("body").on("click", "#confimChoose", function(){
	    	parent.userReturnList = [].concat(userOperatorReturnList);
			parent.systemCommonUtil.staffChooseList = [].concat(userOperatorReturnList);
			parent.layer.close(index);
	    	parent.refreshCode = '0';
		});
	    
	    //已选择人员点击定位到树所在
	    $("body").on("click", "#selPeopleList .sel-people-item", function(){
	    	$("#selPeopleList .sel-people-item").removeClass("active");
	    	$(this).addClass("active");
	    	var id = $(this).attr("rowid");
	    	var _tabId = $('.layui-tab-title').find("li[class='layui-this']").attr("id");
	    	if(_tabId === 'allPeople'){//所有人员
	    		var node = allPeopleTree.getNodeByParam("id", id);
	    		if(!isNull(node)){
	    			expantNodeParentNode(allPeopleTree, node);
	    			allPeopleTree.selectNode(node);//选中指定节点
	    		}
	    	}else if(_tabId === 'accordingCompany'){//按公司
	    		var node = accordingCompanyTree.getNodeByParam("id", id);
	    		if(!isNull(node)){
	    			expantNodeParentNode(accordingCompanyTree, node);
	    			accordingCompanyTree.selectNode(node);//选中指定节点
	    		}
	    	}else if(_tabId === 'accordingDepartment'){//按部门
	    		var node = accordingDepartmentTree.getNodeByParam("id", id);
	    		if(!isNull(node)){
	    			expantNodeParentNode(accordingDepartmentTree, node);
	    			accordingDepartmentTree.selectNode(node);//选中指定节点
	    		}
	    	}else if(_tabId === 'accordingJob'){//按岗位
	    		var node = accordingJobTree.getNodeByParam("id", id);
	    		if(!isNull(node)){
	    			expantNodeParentNode(accordingJobTree, node);
	    			accordingJobTree.selectNode(node);//选中指定节点
	    		}
	    	}else if(_tabId === 'accordingSimpleDepartment'){//同部门人员
	    		var node = accordingSimpleDepartmentTree.getNodeByParam("id", id);
	    		if(!isNull(node)){
	    			expantNodeParentNode(accordingSimpleDepartmentTree, node);
	    			accordingSimpleDepartmentTree.selectNode(node);//选中指定节点
	    		}
	    	}else if(_tabId === 'accordingGroup'){//按联系组
	    		var node = accordingGroupTree.getNodeByParam("id", id);
	    		if(!isNull(node)){
	    			expantNodeParentNode(accordingGroupTree, node);
	    			accordingGroupTree.selectNode(node);//选中指定节点
	    		}
	    	}
	    });
	    
	    //展开指定节点的所有父节点
	    function expantNodeParentNode(ztr, treeObj){
	    	if(treeObj == null) return "";
	    	var pNode = treeObj.getParentNode();
	    	if(pNode != null) {
	    		expantNodeParentNode(ztr, pNode);
	    		if(!pNode.open){//如果节点不展开，则展开
	    			ztr.expandNode(pNode);
	    		}
	    	}
	    }
	    
	    //tab页切换
	    $('.layui-tab-title').on('click', function(title) {
	    	var _tabId = title.target.id;
	    	if(_tabId === 'allPeople'){//所有人员
	    		if(!allPeopleFirstLoad)
	    			loadAllPeopleList();
	    	}else if(_tabId === 'accordingCompany'){//按公司
	    		if(!accordingCompanyFirstLoad)
	    			loadPeopleListByAccordingCompany();
	    	}else if(_tabId === 'accordingDepartment'){//按部门
	    		if(!accordingDepartmentFirstLoad)
	    			loadPeopleListByAccordingDepartment();
	    	}else if(_tabId === 'accordingJob'){//按岗位
	    		if(!accordingJobFirstLoad)
	    			loadPeopleListByAccordingJob();
	    	}else if(_tabId === 'accordingSimpleDepartment'){//同部门人员
	    		if(!accordingSimpleDepartmentFirstLoad)
	    			loadPeopleListByAccordingSimpleDepartment();
	    	}else if(_tabId === 'accordingGroup'){//按联系组
	    		if(!accordingGroupFirstLoad)
	    			loadPeopleListByAccordingGroup();
	    	}
	    });
	    
	    /**
	     * 加载所有人员模块
	     */
	    function loadAllPeopleList(){
	    	allPeopleFirstLoad = true;
	    	fsTree.render({
	    		id: "allPeopleTree",
	    		url: "commonselpeople001" + urlParams,
	    		checkEnable: true,
	    		loadEnable: false,//异步加载
	    		chkStyle: ztreeCheckType,
	    		showLine: false,
	    		showIcon: true,
	    		expandSpeed: 'fast',
	    		onDblClick: function(){
	    		},
	    		onAsyncSuccess: function(id){
	    		},
	    		beforeCheck: ztreeRadioCheck,//选中之前  判断
	    		onCheck: zTreeOnCheck//选中回调函数
	    	}, function(id){
	    		allPeopleTree = $.fn.zTree.getZTreeObj(id);
	    		expancedRootNode(allPeopleTree);
	    		var zTree = allPeopleTree.getCheckedNodes(false);  
			    for (var i = 0; i < zTree.length; i++) {
			    	for(var j = 0; j < userOperatorReturnList.length; j++){
			    		if(zTree[i].id == userOperatorReturnList[j].id){
			    			allPeopleTree.checkNode(zTree[i], true, true);
			    		}
			    	}
			    }  
	    	});
	    }
	    
	    /**
	     * 加载按公司加载人员模块
	     */
	    function loadPeopleListByAccordingCompany(){
	    	accordingCompanyFirstLoad = true;
	    	fsTree.render({
	    		id: "accordingCompanyTree",
	    		url: "commonselpeople002" + urlParams,
	    		checkEnable: true,
	    		loadEnable: false,//异步加载
	    		showLine: false,
	    		showIcon: true,
	    		expandSpeed: 'fast',
	    		onDblClick: function(){
	    		},
	    		onAsyncSuccess: function(id){
	    		},
	    		beforeCheck: ztreeRadioCheck,//选中之前  判断
	    		onCheck: zTreeOnCheck//选中回调函数
	    	}, function(id){
	    		accordingCompanyTree = $.fn.zTree.getZTreeObj(id);
	    		expancedRootNode(accordingCompanyTree);
	    		var zTree = accordingCompanyTree.getCheckedNodes(false);  
			    for (var i = 0; i < zTree.length; i++) {
			    	for(var j = 0; j < userOperatorReturnList.length; j++){
			    		if(zTree[i].id == userOperatorReturnList[j].id){
			    			accordingCompanyTree.checkNode(zTree[i], true, true);
			    		}
			    	}
			    }
			    screenTree($("#companyUserName").val());
	    	});
	    }
	    
	    /**
	     * 加载按部门加载人员模块
	     */
	    function loadPeopleListByAccordingDepartment(){
	    	accordingDepartmentFirstLoad = true;
	    	fsTree.render({
	    		id: "accordingDepartmentTree",
	    		url: "commonselpeople003" + urlParams,
	    		checkEnable: true,
	    		loadEnable: false,//异步加载
	    		showLine: false,
	    		showIcon: true,
	    		expandSpeed: 'fast',
	    		onDblClick: function(){
	    		},
	    		onAsyncSuccess: function(id){
	    		},
	    		beforeCheck: ztreeRadioCheck,//选中之前  判断
	    		onCheck: zTreeOnCheck//选中回调函数
	    	}, function(id){
	    		accordingDepartmentTree = $.fn.zTree.getZTreeObj(id);
	    		expancedRootNode(accordingDepartmentTree);
	    		var zTree = accordingDepartmentTree.getCheckedNodes(false);  
			    for (var i = 0; i < zTree.length; i++) {
			    	for(var j = 0; j < userOperatorReturnList.length; j++){
			    		if(zTree[i].id == userOperatorReturnList[j].id){
			    			accordingDepartmentTree.checkNode(zTree[i], true, true);
			    		}
			    	}
			    }
			    screenTree($("#departmentUserName").val());
	    	});
	    }
	    
	    /**
	     * 加载按岗位加载人员模块
	     */
	    function loadPeopleListByAccordingJob(){
	    	accordingJobFirstLoad = true;
	    	fsTree.render({
	    		id: "accordingJobTree",
	    		url: "commonselpeople004" + urlParams,
	    		checkEnable: true,
	    		loadEnable: false,//异步加载
	    		showLine: false,
	    		showIcon: true,
	    		expandSpeed: 'fast',
	    		onDblClick: function(){
	    		},
	    		onAsyncSuccess: function(id){
	    		},
	    		beforeCheck: ztreeRadioCheck,//选中之前  判断
	    		onCheck: zTreeOnCheck//选中回调函数
	    	}, function(id){
	    		accordingJobTree = $.fn.zTree.getZTreeObj(id);
	    		expancedRootNode(accordingJobTree);
	    		var zTree = accordingJobTree.getCheckedNodes(false);  
			    for (var i = 0; i < zTree.length; i++) {
			    	for(var j = 0; j < userOperatorReturnList.length; j++){
			    		if(zTree[i].id == userOperatorReturnList[j].id){
			    			accordingJobTree.checkNode(zTree[i], true, true);
			    		}
			    	}
			    }
			    screenTree($("#jobUserName").val());
	    	});
	    }
	    
	    /**
	     * 加载按同级部门加载人员模块
	     */
	    function loadPeopleListByAccordingSimpleDepartment(){
	    	accordingSimpleDepartmentFirstLoad = true;
	    	fsTree.render({
	    		id: "accordingSimpleDepartmentTree",
	    		url: "commonselpeople005" + urlParams,
	    		checkEnable: true,
	    		loadEnable: false,//异步加载
	    		showLine: false,
	    		showIcon: true,
	    		expandSpeed: 'fast',
	    		onDblClick: function(){
	    		},
	    		onAsyncSuccess: function(id){
	    		},
	    		beforeCheck: ztreeRadioCheck,//选中之前  判断
	    		onCheck: zTreeOnCheck//选中回调函数
	    	}, function(id){
	    		accordingSimpleDepartmentTree = $.fn.zTree.getZTreeObj(id);
	    		expancedRootNode(accordingSimpleDepartmentTree);
	    		var zTree = accordingSimpleDepartmentTree.getCheckedNodes(false);  
			    for (var i = 0; i < zTree.length; i++) {
			    	for(var j = 0; j < userOperatorReturnList.length; j++){
			    		if(zTree[i].id == userOperatorReturnList[j].id){
			    			accordingSimpleDepartmentTree.checkNode(zTree[i], true, true);
			    		}
			    	}
			    }
			    screenTree($("#simpleDepartmentUserName").val());
	    	});
	    }
	    
	    /**
	     * 加载按聊天组加载人员模块
	     */
	    function loadPeopleListByAccordingGroup(){
	    	accordingGroupFirstLoad = true;
	    	fsTree.render({
	    		id: "accordingGroupTree",
	    		url: "commonselpeople006" + urlParams,
	    		checkEnable: true,
	    		loadEnable: false,//异步加载
	    		showLine: false,
	    		showIcon: true,
	    		expandSpeed: 'fast',
	    		onDblClick: function(){
	    		},
	    		onAsyncSuccess: function(id){
	    		},
	    		beforeCheck: ztreeRadioCheck,//选中之前  判断
	    		onCheck: zTreeOnCheck//选中回调函数
	    	}, function(id){
	    		accordingGroupTree = $.fn.zTree.getZTreeObj(id);
	    		expancedRootNode(accordingGroupTree);
	    		var zTree = accordingGroupTree.getCheckedNodes(false);  
			    for (var i = 0; i < zTree.length; i++) {
			    	for(var j = 0; j < userOperatorReturnList.length; j++){
			    		if(zTree[i].id == userOperatorReturnList[j].id){
			    			accordingGroupTree.checkNode(zTree[i], true, true);
			    		}
			    	}
			    }
			    screenTree($("#groupUserName").val());
	    	});
	    }
	    
	    //选中或取消选中的回调函数
	    function zTreeOnCheck(event, treeId, treeNode){
	    	if(treeId === 'allPeopleTree'){//所有人员
	    		getSelNodeByTree(allPeopleTree);
	    	}else if(treeId === 'accordingCompanyTree'){//按公司
	    		getSelNodeByTree(accordingCompanyTree);
	    	}else if(treeId === 'accordingDepartmentTree'){//按部门
	    		getSelNodeByTree(accordingDepartmentTree);
	    	}else if(treeId === 'accordingJobTree'){//按岗位
	    		getSelNodeByTree(accordingJobTree);
	    	}else if(treeId === 'accordingSimpleDepartmentTree'){//同部门人员
	    		getSelNodeByTree(accordingSimpleDepartmentTree);
	    	}else if(treeId === 'accordingGroupTree'){//按联系组
	    		getSelNodeByTree(accordingGroupTree);
	    	}
	    }
	    
	    //根据树选中节点设置选中人员
	    function getSelNodeByTree(ztr){
	    	//获取选中节点
	    	var zTree = ztr.getCheckedNodes(true);
	    	for (var i = 0; i < zTree.length; i++) {
	    		if(zTree[i].folderType === 'isPeople'){//选中节点是人
    				addToArray({
    					id: zTree[i].id,
    					name: zTree[i].name,
    					email: zTree[i].email
    				});
    				setUpTreeNodeCheckOrNot(zTree[i].id, true);
	    		}
	    	}
	    	//获取未选中节点
	    	zTree = ztr.getCheckedNodes(false);
	    	for (var i = 0; i < zTree.length; i++) {
	    		if(zTree[i].folderType === 'isPeople'){//选中节点是人
    				removeToArray(zTree[i].id);
    				setUpTreeNodeCheckOrNot(zTree[i].id, false);
	    		}
	    	}
	    }
	    
	    //设置所有的树的指定节点选中或者不选中
	    function setUpTreeNodeCheckOrNot(id, checked){
	    	if(!isNull(allPeopleTree)){
	    		var node = allPeopleTree.getNodeByParam("id", id);
	    		if(!isNull(node))
	    			allPeopleTree.checkNode(node, checked, true);
	    	}
	    	if(!isNull(accordingCompanyTree)){
	    		var node = accordingCompanyTree.getNodeByParam("id", id);
	    		if(!isNull(node))
	    			accordingCompanyTree.checkNode(node, checked, true);
	    	}
	    	if(!isNull(accordingDepartmentTree)){
	    		var node = accordingDepartmentTree.getNodeByParam("id", id);
	    		if(!isNull(node))
	    			accordingDepartmentTree.checkNode(node, checked, true);
	    	}
	    	if(!isNull(accordingJobTree)){
	    		var node = accordingJobTree.getNodeByParam("id", id);
	    		if(!isNull(node))
	    			accordingJobTree.checkNode(node, checked, true);
	    	}
	    	if(!isNull(accordingSimpleDepartmentTree)){
	    		var node = accordingSimpleDepartmentTree.getNodeByParam("id", id);
	    		if(!isNull(node))
	    			accordingSimpleDepartmentTree.checkNode(node, checked, true);
	    	}
	    	if(!isNull(accordingGroupTree)){
	    		var node = accordingGroupTree.getNodeByParam("id", id);
	    		if(!isNull(node))
	    			accordingGroupTree.checkNode(node, checked, true);
	    	}
	    }
	    
	    //向集合中添加元素
	    //参数为json
	    function addToArray(data) {
	    	//判断是多选还是单选
	    	if(checkType != 1){
	    		userOperatorReturnList = new Array();
	    	}
	    	
	    	var inArray = false;
	    	$.each(userOperatorReturnList, function(i, item) {
	    		if(item.id === data.id) {
	    			inArray = true;
	    			return false;
	    		}
	    	});
	    	
	    	//如果该元素在集合中不存在
	    	if(!inArray) {
	    		var j = {
	    			id: data.id,
	    			name: data.name,
	    			email: data.email
	    		};
	    		userOperatorReturnList.push(j);
	    		showPeople();
	    	}
	    }

	    //移除集合中的元素
	    function removeToArray(id) {
	    	var inArray = -1;
	    	$.each(userOperatorReturnList, function(i, item) {
	    		if(id === item.id) {
	    			inArray = i;
	    			return false;
	    		}
	    	});
	    	if(inArray != -1) { //如果该元素在集合中存在
	    		userOperatorReturnList.splice(inArray, 1);
	    		showPeople();
	    	}
	    }
	    
	    //展示选中的人员
	    function showPeople(){
	    	var str = "";
	    	$.each(userOperatorReturnList, function(i, item) {
	    		str += getDataUseHandlebars(peopleTemplate, {bean: item});
	    	});
	    	$("#selPeopleList").html(str);
	    }
	    
	    //所有人员监听
	    $("body").on("keyup", "#allUserName, #companyUserName, #departmentUserName, #jobUserName, #simpleDepartmentUserName, #groupUserName", function(e){
	    	var val = $(this).val();
	    	screenTree(val);
	    	changeUserNameInput(val);
	    });
	    
	    var allPeopleHiddenNodes = [],
	    	accordingCompanyHiddenNodes = [],
	    	accordingDepartmentHiddenNodes = [],
	    	accordingJobHiddenNodes = [],
	    	accordingSimpleDepartmentHiddenNodes = [],
	    	accordingGroupHiddenNodes = []; //用于存储被隐藏的结点
	    /**
	     * 筛选树对象
	     */
	    function screenTree(val){
	    	
	    	function filterFunc(node) {
	    		var keyword = val;
	    		//如果当前结点，或者其父结点可以找到，或者当前结点的子结点可以找到，则该结点不隐藏
	    		if(searchParent(keyword, node) || searchChildren(keyword, node.children)) {
	    			return false;
	    		}
	    		return true;
	    	};
	    	
	    	if(!isNull(allPeopleTree)){
	    		allPeopleTree.showNodes(allPeopleHiddenNodes);
	    		//获取不符合条件的叶子结点
	    		allPeopleHiddenNodes = allPeopleTree.getNodesByFilter(filterFunc);
	    		//隐藏不符合条件的叶子结点
	    		allPeopleTree.hideNodes(allPeopleHiddenNodes);
	    		expandNodes(allPeopleTree, allPeopleTree.getNodes());
	    	}
	    	if(!isNull(accordingCompanyTree)){
	    		accordingCompanyTree.showNodes(accordingCompanyHiddenNodes);
	    		//获取不符合条件的叶子结点
	    		accordingCompanyHiddenNodes = accordingCompanyTree.getNodesByFilter(filterFunc);
	    		//隐藏不符合条件的叶子结点
	    		accordingCompanyTree.hideNodes(accordingCompanyHiddenNodes);
	    		expandNodes(accordingCompanyTree, accordingCompanyTree.getNodes());
	    	}
	    	if(!isNull(accordingDepartmentTree)){
	    		accordingDepartmentTree.showNodes(accordingDepartmentHiddenNodes);
	    		//获取不符合条件的叶子结点
	    		accordingDepartmentHiddenNodes = accordingDepartmentTree.getNodesByFilter(filterFunc);
	    		//隐藏不符合条件的叶子结点
	    		accordingDepartmentTree.hideNodes(accordingDepartmentHiddenNodes);
	    		expandNodes(accordingDepartmentTree, accordingDepartmentTree.getNodes());
	    	}
	    	if(!isNull(accordingJobTree)){
	    		accordingJobTree.showNodes(accordingJobHiddenNodes);
	    		//获取不符合条件的叶子结点
	    		accordingJobHiddenNodes = accordingJobTree.getNodesByFilter(filterFunc);
	    		//隐藏不符合条件的叶子结点
	    		accordingJobTree.hideNodes(accordingJobHiddenNodes);
	    		expandNodes(accordingJobTree, accordingJobTree.getNodes());
	    	}
	    	if(!isNull(accordingSimpleDepartmentTree)){
	    		accordingSimpleDepartmentTree.showNodes(accordingSimpleDepartmentHiddenNodes);
	    		//获取不符合条件的叶子结点
	    		accordingSimpleDepartmentHiddenNodes = accordingSimpleDepartmentTree.getNodesByFilter(filterFunc);
	    		//隐藏不符合条件的叶子结点
	    		accordingSimpleDepartmentTree.hideNodes(accordingSimpleDepartmentHiddenNodes);
	    		expandNodes(accordingSimpleDepartmentTree, accordingSimpleDepartmentTree.getNodes());
	    	}
	    	if(!isNull(accordingGroupTree)){
	    		accordingGroupTree.showNodes(accordingGroupHiddenNodes);
	    		//获取不符合条件的叶子结点
	    		accordingGroupHiddenNodes = accordingGroupTree.getNodesByFilter(filterFunc);
	    		//隐藏不符合条件的叶子结点
	    		accordingGroupTree.hideNodes(accordingGroupHiddenNodes);
	    		expandNodes(accordingGroupTree, accordingGroupTree.getNodes());
	    	}
	    }
	    
	    /**
	     * 查找子结点，如果找到，返回true，否则返回false-----ztree查询时使用
	     */
	    function searchChildren(keyword, children) {
	    	if(children == null || children.length == 0) {
	    		return false;
	    	}
	    	for(var i = 0; i < children.length; i++) {
	    		var node = children[i];
	    		if(node.name.indexOf(keyword) != -1) {
	    			return true;
	    		}
	    		//递归查找子结点
	    		var result = searchChildren(keyword, node.children);
	    		if(result) {
	    			return true;
	    		}
	    	}
	    	return false;
	    }

	    /**
	     * 查找当前结点和父结点，如果找到，返回ture，否则返回false
	     */
	    function searchParent(keyword, node) {
	    	if(node == null) {
	    		return false;
	    	}
	    	if(node.name.indexOf(keyword) != -1) {
	    		return true;
	    	}
	    	//递归查找父结点
	    	return searchParent(keyword, node.getParentNode());
	    }
	    
	    //输入框变化赋值
	    function changeUserNameInput(val){
	    	$("#allUserName").val(val);
	    	$("#companyUserName").val(val);
	    	$("#departmentUserName").val(val);
	    	$("#jobUserName").val(val);
	    	$("#simpleDepartmentUserName").val(val);
	    	$("#groupUserName").val(val);
	    }
	    
	    /**
	     * 展开根节点
	     */
	    function expancedRootNode(treeObject){
	    	var nodeList = treeObject.getNodes();
    		if(nodeList.length > 0)
    			treeObject.expandNode(nodeList[0], true);
	    }
	    
	    /**
	     * 展开所有节点
	     */
	    function expandNodes(treeObj, nodes) {
	    	//如果nodes为null 则return
	    	if(!nodes) return;
	    	//将状态设置为expand
	    	curStatus = 'expand';
	    	//循环展开节点
	    	for(var i = 0; i < nodes.length; i++) {
	    		treeObj.expandNode(nodes[i], true, false, false);
	    		//递归 如果子节点的是父节点则进行递归操作
	    		if(nodes[i].isParent && nodes[i].zAsync) {
	    			expandNodes(treeObj, nodes[i].children);
	    		} else {
	    			goAsync = true;
	    		}
	    	}
	    }
	    
	});
});

