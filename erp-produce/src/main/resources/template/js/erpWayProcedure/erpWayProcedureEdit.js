
// 工序选择必备参数
var procedureCheckType = 1;//工序选择类型：1.单选procedureMation；2.多选procedureMationList
var procedureMation = {};

// 车间选择必备参数
var farmCheckType = 1;//车间选择类型：1.单选procedureMation；2.多选procedureMationList
var farmMation = {};
var procedureId = "";

layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui', 'soulTable', 'table'], function (exports) {
	winui.renderColor();
	layui.use(['form'], function (form) {
		var index = parent.layer.getFrameIndex(window.name);
	    var $ = layui.$,
		    form = layui.form,
		    table = layui.table,
		    soulTable = layui.soulTable;
		    
		var procedureList = new Array();
		var rowNum = 1;
		
		showGrid({
            id: "showForm",
            url: flowableBasePath + "erpwayprocedure003",
            params: {rowId: parent.rowId},
            pagination: false,
            template: $("#usetableTemplate").html(),
            ajaxSendLoadBefore: function(hdb){
            },
            ajaxSendAfter: function (json) {
            	$.each(json.bean.procedureList, function(j, item){
            		item.id = rowNum;
					procedureList.push(item);
					rowNum++;
				});
				
				table.render({
			        id: 'messageTable',
			        elem: '#messageTable',
			        method: 'get',
			        data: procedureList,
			        even: true,
			        page: false,
			        rowDrag: {
			        	trigger: 'row',
			        	done: function(obj) {}
			        },
			        cols: [[
			            { type: 'checkbox', align: 'center' },
			            { field: 'number', title: '工序编号', align: 'left', width: 120, templet: function (d) {
					        return '<input type="text" id="procedureId' + d.id + '" placeholder="请选择工序" class="layui-input" readonly="readonly" ' +
					        		'value="' + (isNull(d.number) ? "" : d.number) + '"/>' + 
				        			'<i class="fa fa-plus-circle input-icon chooseProcedureBtn" style="top: 8px;"></i>';
					    }},
			            { field: 'procedureName', title: '工序名称', align: 'left', width: 120},
			            { field: 'unitPrice', title: '加工单价', align: 'left', width: 120},
			            { field: 'departmentName', title: '加工部门', align: 'left', width: 120},
			            { field: 'farmId', title: '加工车间', align: 'left', width: 120, templet: function (d) {
					        return '<input type="text" id="farmId' + d.id + '" placeholder="请选择车间" class="layui-input" readonly="readonly" ' + 
					        		'value="' + (isNull(d.farmName) ? "" : d.farmName) + '"/>' + 
				        			'<i class="fa fa-plus-circle input-icon chooseFarmBtn" style="top: 8px;"></i>';
					    }}
			        ]],
			        done: function(){
			        	matchingLanguage();
				    	soulTable.render(this);
			        }
			    });
				
			    form.render();
			    form.on('submit(formEditBean)', function (data) {
			        if (winui.verifyForm(data.elem)) {
			        	if(table.cache.messageTable.length == 0){
			        		winui.window.msg('请最少选择一条工序.', {icon: 2, time: 2000});
			    			return false;
			        	}
						var params = {
		                    wayNumber: $("#wayNumber").val(),
		                    wayName: $("#wayName").val(),
		                    procedureMation: JSON.stringify(table.cache.messageTable),
		                    rowId: parent.rowId
					    };
			        	AjaxPostUtil.request({url:flowableBasePath + "erpwayprocedure004", params: params, type: 'json', callback: function (json) {
							parent.layer.close(index);
							parent.refreshCode = '0';
			 	   		}});
			        }
			        return false;
			    });
				
            }
		});
		    
		$("body").on("click", "#addRow", function() {
			addRow();
		});
		
		$("body").on("click", "#deleteRow", function() {
			deleteRow();
		});
		
		// 新增行
		function addRow() {
			procedureList = [].concat(table.cache.messageTable);
			procedureList.push({id: rowNum});
			table.reload("messageTable", {data: procedureList});
			rowNum++;
		}

		// 删除行
		function deleteRow() {
			procedureList = [].concat(table.cache.messageTable);
			var check_box = table.checkStatus('messageTable').data;
			for (var i = 0;  i < check_box.length; i++){
				var list = [];
				$.each(procedureList, function(j, item){
					if(item.id != check_box[i].id){
						list.push(item);
					}
				});
				procedureList = [].concat(list);
			}
			table.reload("messageTable", {data: procedureList});
		}
	    
	    // 工序选择
	    $("body").on("click", ".chooseProcedureBtn", function() {
	    	var trId = $(this).parent().find("input").attr("id").replace("procedureId", "");
	    	_openNewWindows({
				url: "../../tpl/erpWorkProcedure/erpWorkProcedureChoose.html", 
				title: "工序选择",
				pageId: "erpWorkProcedureChoose",
				area: ['90vw', '90vh'],
				callBack: function(refreshCode){
					loadUseTableMation(trId, procedureMation);
				}});
	    });
	    
	    function loadUseTableMation(trId, procedureMation){
	    	var inIndex = -1;
	    	$.each(procedureList, function(j, item){
				if(item.id == trId){
					inIndex = j;
					return;
				}
			});
			if(inIndex != -1){
				var procedure = $.extend(procedureList[inIndex], procedureMation);
				procedure.farmId = '';
				procedure.farmName = '';
				procedureList[inIndex] = procedure;
			}
	    	table.reload("messageTable", {data: procedureList});
	    }
	    
	    // 车间选择
	    $("body").on("click", ".chooseFarmBtn", function() {
	    	var trId = $(this).parent().find("input").attr("id").replace("farmId", "");
	    	var row = getProcedureMation(trId);
	    	if(isNull(row.procedureId)){
	    		winui.window.msg('请先选择工序', {icon: 2, time: 2000});
	    		return;
	    	}
	    	procedureId = row.procedureId;
	    	_openNewWindows({
				url: "../../tpl/erpFarm/erpFarmChoose.html", 
				title: "车间选择",
				pageId: "erpFarmChoose",
				area: ['90vw', '90vh'],
				callBack: function(refreshCode){
					loadFarmUseTableMation(trId, farmMation);
				}});
	    });
	    
	    function loadFarmUseTableMation(trId, farmMation){
	    	var inIndex = -1;
	    	$.each(procedureList, function(j, item){
				if(item.id == trId){
					inIndex = j;
					return;
				}
			});
			if(inIndex != -1){
				var farm = procedureList[inIndex];
				farm.farmId = farmMation.farmId;
				farm.farmName = farmMation.farmName;
				procedureList[inIndex] = farm;
			}
	    	table.reload("messageTable", {data: procedureList});
	    }
	    
	    function getProcedureMation(trId){
	    	var inIndex = -1;
	    	$.each(procedureList, function(j, item){
				if(item.id == trId){
					inIndex = j;
					return;
				}
			});
			if(inIndex != -1){
				return procedureList[inIndex];
			}
			return {};
	    }

	    // 取消
	    $("body").on("click", "#cancle", function() {
	    	parent.layer.close(index);
	    });
	});
	    
});