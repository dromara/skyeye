
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'form', 'tableCheckBoxUtil'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	
	var $ = layui.$,
		form = layui.form,
		table = layui.table,
		tableCheckBoxUtil = layui.tableCheckBoxUtil;
		
	var checkType = '1';// 车间选择类型：1.单选；2.多选
	
	if(!isNull(parent.farmCheckType)){
		checkType = parent.farmCheckType;
	}
	
	var selOption = getFileContent('tpl/template/select-option.tpl');
	
	AjaxPostUtil.request({url: flowableBasePath + "erpworkprocedure009", params: {}, type: 'json', callback: function(json) {
		if(json.returnCode == 0) {
			// 加载工序数据
			$("#procedureId").html(getDataUseHandlebars(selOption, json));
			if(!isNull(parent.procedureId)){
				$("#procedureId").val(parent.procedureId);
			}
			// 初始化表格
			initTable();
		} else {
			winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
		}
	}});
	
	//设置提示信息
	var s = "车间选择规则：";
	if(checkType == "1"){
		s += '1.单选，双击指定行数据即可选中；';
	}else{
		s += '1.多选；';
		//显示保存按钮
		$("#saveCheckBox").show();
	}
	s += '如没有查到要选择的车间，请检查车间信息是否满足当前规则。';
	$("#showInfo").html(s);
	
	function initTable(){
		if(checkType == '2'){
			//初始化值
			var ids = [];
			$.each(parent.farmMationList, function(i, item){
				ids.push(item.farmId);
			});
			tableCheckBoxUtil.setIds({
				gridId: 'messageTable',
				fieldName: 'farmId',
				ids: ids
			});
			tableCheckBoxUtil.init({
				gridId: 'messageTable',
				filterId: 'messageTable',
				fieldName: 'farmId'
			});
		}
			
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: flowableBasePath + 'erpfarm010',
		    where: getTableParams(),
			even: true,
		    page: true,
		    limits: [8, 16, 24, 32, 40, 48, 56],
		    limit: 8,
		    cols: [[
		    	{ type: checkType == '1' ? 'radio' : 'checkbox'},
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'farmNumber', title: '车间编号', align: 'left', width: 100, templet: function(d){
                    return '<a lay-event="details" class="notice-title-click">' + d.farmNumber + '</a>';
                }},
	            { field: 'farmName', title: '车间名称', align: 'left', width: 250},
	            { field: 'state', title: '状态', align: 'left', width: 80, templet: function(d){
	                if(d.state == '1'){
	                    return "<span class='state-up'>正常</span>";
	                }else if(d.state == '2'){
	                    return "<span class='state-down'>维修整改</span>";
	                }else{
	                    return "参数错误";
	                }
	            }},
	            { field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], align: 'left', width: 120 },
	            { field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 100 },
	            { field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 100 },
	            { field: 'lastUpdateTime', title: '最后修改时间', align: 'center', width: 100}
		    ]],
		    done: function(res, curr, count){
		    	matchingLanguage();
		    	if(checkType == '1'){
			    	$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('dblclick',function(){
						var dubClick = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
						dubClick.find("input[type='radio']").prop("checked", true);
						form.render();
						var chooseIndex = JSON.stringify(dubClick.data('index'));
						var obj = res.rows[chooseIndex];
						parent.farmMation = obj;
						
						parent.refreshCode = '0';
						parent.layer.close(index);
					});
					
					$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('click',function(){
						var click = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
						click.find("input[type='radio']").prop("checked", true);
						form.render();
					})
		    	}else{
		    		//多选
		    		//设置选中
		    		tableCheckBoxUtil.checkedDefault({
						gridId: 'messageTable',
						fieldName: 'procedureId'
					});
		    	}
		    }
		});
		
		form.render();
	}
	
	// 保存按钮-多选才有
	$("body").on("click", "#saveCheckBox", function(){
		var selectedData = tableCheckBoxUtil.getValue({
			gridId: 'messageTable'
		});
		AjaxPostUtil.request({url:flowableBasePath + "erpfarm011", params: {ids: selectedData.toString()}, type: 'json', callback: function(json){
   			if(json.returnCode == 0){
   				parent.procedureMationList = [].concat(json.rows);
 	   			parent.layer.close(index);
 	        	parent.refreshCode = '0';
   			}else{
   				winui.window.msg(json.returnMessage, {icon: 2,time: 2000});
   			}
   		}});
	});
	
	form.render();
	form.on('submit(formSearch)', function (data) {
        if (winui.verifyForm(data.elem)) {
            refreshTable();
        }
        return false;
    });
	
	$("body").on("click", "#reloadTable", function(){
    	loadTable();
    });
    
    function loadTable(){
    	table.reload("messageTable", {where: getTableParams()});
    }
    
    function refreshTable(){
    	table.reload("messageTable", {page: {curr: 1}, where: getTableParams()});
    }

	function getTableParams(){
		return {
			farmNumber: $("#farmNumber").val(),
            farmName: $("#farmName").val(),
            procedureId: $("#procedureId").val()
		};
	}
	
    exports('erpFarmChoose', {});
});