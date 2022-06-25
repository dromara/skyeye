
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

    AjaxPostUtil.request({url:flowableBasePath + "activitimode025", params: {processInstanceId: processInstanceId}, type: 'json', callback: function(j){
		if(j.returnCode == 0){
			var jsonStr = "";//实体json对象
			var str = "";
			$.each(j.rows, function(i, item){
				//如果展示文本不为空，则展示展示文本
				if(!isNull(item.text))
					item.value = item.text;
				jsonStr = {
					bean: item
				};
				if(item.showType == 1){//文本展示
					str = getDataUseHandlebars(textTemplate, jsonStr);
				}else if(item.showType == 2){//附件展示
					str = getDataUseHandlebars(enclosureTemplate, jsonStr);
				}else if(item.showType == 3){//富文本展示
					str = getDataUseHandlebars(eichTextTemplate, jsonStr);
				}else if(item.showType == 4){//图片展示
					var photoValue = [];
					if(!isNull(jsonStr.bean.value)){
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
				}else if(item.showType == 6){//凭证展示
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
		} else {
			winui.window.msg(j.returnMessage, {icon: 2, time: 2000});
		}
	}});

    function getValJson(val, startPrefix, endPrefix){
    	if(typeof val == 'string'){
    		val = startPrefix + val + endPrefix;
			return JSON.parse(val);
		}
    	return val;
	}

	//加载时间线审批历史
	function inboxTimeTreeApprovalHistory(){
		flow.load({
			elem: '#timeTreeApprovalHistoryList', //指定列表容器
			scrollElem: '#timeTreeApprovalHistoryList',
			isAuto: true,
			done: function(page, next) { //到达临界点（默认滚动触发），触发下一页
				var lis = [];
				//以jQuery的Ajax请求为例，请求下一页数据（注意：page是从2开始返回）
				AjaxPostUtil.request({url:flowableBasePath + "activitimode017", params:{processInstanceId: parent.processInstanceId}, type: 'json', callback: function(json){
		   			if (json.returnCode == 0) {
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
		   			} else {
		   				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
		   			}
		   		}});
			}
		});
	}
	
	/***
     * 图片弹出展示,默认原大小展示。图片大于浏览器时下窗口可视区域时，进行等比例缩小。
     * config.src 图片路径。必须项
     * default_config.height 图片显示高度，默认原大小展示。图片大于浏览器时下窗口可视区域时，进行等比例缩小。
     * default_config.width 图片显示宽度，默认原大小展示。图片大于浏览器时下窗口可视区域时，进行等比例缩小。
     * default_config.title 弹出框标题
     */
    function previewImg(config) {
    	if(!config.src || config.src==""){
    		layer.msg("没有发现图片！");
    		return ;
    	}
    	var default_config = {title: "流程图"};
    	var img = new Image();
        img.onload = function() {//避免图片还未加载完成无法获取到图片的大小。
        	//避免图片太大，导致弹出展示超出了网页显示访问，所以图片大于浏览器时下窗口可视区域时，进行等比例缩小。
        	var max_height = $(window).height() - 100;
        	var max_width = $(window).width();
        	//rate1，rate2，rate3 三个比例中取最小的。
        	var rate1 = max_height / img.height;
        	var rate2 = max_width / img.width;
        	var rate3 = 1;
        	var rate = Math.min(rate1, rate2, rate3); 
        	//等比例缩放
        	default_config.height = img.height * rate; //获取图片高度
            default_config.width = img.width  * rate; //获取图片宽度
            
            $.extend( default_config, config);
            var imgHtml = "<img src='" + default_config.src + "' width='" + default_config.width + "px' height='" + default_config.height + "px'/>";  
            //弹出层
            layer.open({  
                type: 1,  
                shade: 0.8,
                offset: 'auto',
                area: [(default_config.width + 100) + 'px', (default_config.height + 100) + 'px'], ////宽，高
                shadeClose:true,
                scrollbar: false,
                title: default_config.title, //不显示标题  
                content: imgHtml, //捕获的元素，注意：最好该指定的元素要存放在body最外层，否则可能被其它的相对元素所影响  
                cancel: function () {  
                },
                error: function(){
                }
            }); 
        }
        img.onerror = function() {
        	winui.window.msg("该流程图已不存在，无法进行查看。", {icon: 2, time: 2000});
        }
        img.src = config.src;
    }
	
    //附件下载
	$("body").on("click", ".enclosureItem", function(){
    	download(fileBasePath + $(this).attr("rowpath"), $(this).html());
    });
    
	//工作流图片查看
    $("body").on("click", "#processInstanceIdImg", function(){
    	previewImg({src: $(this).attr("src")});
    });
    
    //图片查看
    $("body").on("click", ".photo-img", function(){
    	var src = $(this).attr("src");
    	layer.open({
    		type:1,
    		title:false,
    		closeBtn:0,
    		skin: 'demo-class',
    		shadeClose:true,
    		content:'<img src="' + src + '" style="max-height:600px;max-width:100%;">',
    		scrollbar:false
        });
    });
    
    form.render();
    
    exports('myactivitidetailspagesub', {});
});
