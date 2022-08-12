
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
		
	var checkType = '1';//工序选择类型：1.单选；2.多选
	
	if(!isNull(parent.procedureCheckType)){
		checkType = parent.procedureCheckType;
	}
	
	//设置提示信息
	var s = "工序选择规则：";
	if(checkType == "1"){
		s += '1.单选，双击指定行数据即可选中；';
	} else {
		s += '1.多选；';
		//显示保存按钮
		$("#saveCheckBox").show();
	}
	s += '如没有查到要选择的工序，请检查工序信息是否满足当前规则。';
	$("#showInfo").html(s);
	
	initTable();
	function initTable(){
		if(checkType == '2'){
			//初始化值
			var ids = [];
			$.each(parent.procedureMationList, function(i, item){
				ids.push(item.procedureId);
			});
			tableCheckBoxUtil.setIds({
				gridId: 'messageTable',
				fieldName: 'procedureId',
				ids: ids
			});
			tableCheckBoxUtil.init({
				gridId: 'messageTable',
				filterId: 'messageTable',
				fieldName: 'procedureId'
			});
		}
			
		
		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: flowableBasePath + 'erpworkprocedure006',
		    where: getTableParams(),
			even: true,
		    page: true,
		    limits: [8, 16, 24, 32, 40, 48, 56],
		    limit: 8,
		    cols: [[
		    	{ type: checkType == '1' ? 'radio' : 'checkbox'},
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
		        { field: 'procedureName', title: '工序名称', align: 'left',width: 120},
	            { field: 'number', title: '工序编号', align: 'left',width: 120},
	            { field: 'unitPrice', title: '参考单价', align: 'left',width: 100},
	            { field: 'departmentName', title: '部门', align: 'right',width: 80},
	            { field: 'content', title: '工序内容', align: 'left',width: 200}
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
						parent.procedureMation = obj;
						
						parent.refreshCode = '0';
						parent.layer.close(index);
					});
					
					$('#messageTable').next().find('.layui-table-body').find("table" ).find("tbody").children("tr").on('click',function(){
						var click = $('#messageTable').next().find('.layui-table-body').find("table").find("tbody").find(".layui-table-hover");
						click.find("input[type='radio']").prop("checked", true);
						form.render();
					})
		    	} else {
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
	
	//保存按钮-多选才有
	$("body").on("click", "#saveCheckBox", function() {
		var selectedData = tableCheckBoxUtil.getValue({
			gridId: 'messageTable'
		});
		AjaxPostUtil.request({url: flowableBasePath + "erpworkprocedure007", params: {ids: selectedData.toString()}, type: 'json', callback: function (json) {
			parent.procedureMationList = [].concat(json.rows);
			parent.layer.close(index);
			parent.refreshCode = '0';
   		}});
	});
	
	
	form.render();
	form.on('submit(formSearch)', function (data) {
        
        if (winui.verifyForm(data.elem)) {
            refreshTable();
        }
        return false;
    });
	
	$("body").on("click", "#reloadTable", function() {
    	loadTable();
    });
    
    function loadTable(){
    	table.reloadData("messageTable", {where: getTableParams()});
    }
    
    function refreshTable(){
    	table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
    }

	function getTableParams(){
		return {
			name:$("#name").val(), 
    		number: $("#number").val()
		};
	}
	
    exports('erpWorkProcedureChoose', {});
});