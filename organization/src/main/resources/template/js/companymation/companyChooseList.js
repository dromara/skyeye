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
	
	// 选择的公司信息
	var companyList = parent.companyList;
	
	// 设置提示信息
	var s = "企业选择规则：1.多选；如没有查到要选择的企业，请检查企业信息是否满足当前规则。";
	$("#showInfo").html(s);

	initTable();
	function initTable(){
		// 初始化值
		var ids = [];
		$.each(companyList, function(i, item){
			ids.push(item.id);
		});
		tableCheckBoxUtil.setIds({
			gridId: 'messageTable',
			fieldName: 'id',
			ids: ids
		});
		tableCheckBoxUtil.init({
			gridId: 'messageTable',
			filterId: 'messageTable',
			fieldName: 'id'
		});

		table.render({
		    id: 'messageTable',
		    elem: '#messageTable',
		    method: 'post',
		    url: reqBasePath + 'companymation010',
		    where: getTableParams(),
			even: true,
		    page: true,
		    limits: [8, 16, 24, 32, 40, 48, 56],
		    limit: 8,
		    cols: [[
		    	{ type: 'checkbox'},
		        { title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers'},
				{ field: 'companyName', width: 300, title: '公司名称'},
				{ field: 'companyDesc', width: 80, title: '公司简介', align: 'center', templet: function(d){
						return '<i class="fa fa-fw fa-html5 cursor" lay-event="companyDesc"></i>';
					}},
				{ field: 'departmentNum', title: '部门数', width: 100 },
				{ field: 'userNum', title: '员工数', width: 100 },
				{ field:'id', width:400, title: '公司地址', templet: function(d){
					var str = d.provinceName + " ";
					if(!isNull(d.cityName)){
						str += d.cityName + " ";
					}
					if(!isNull(d.areaName)){
						str += d.areaName + " ";
					}
					if(!isNull(d.townshipName)){
						str += d.townshipName + " ";
					}
					if(!isNull(d.addressDetailed)){
						str += d.addressDetailed;
					}
					return str;
				}}
		    ]],
		    done: function(res, curr, count){
		    	matchingLanguage();
	    		//设置选中
	    		tableCheckBoxUtil.checkedDefault({
					gridId: 'messageTable',
					fieldName: 'id'
				});
		    }
		});
		
		table.on('tool(messageTable)', function (obj) {
	        var data = obj.data;
	        var layEvent = obj.event;
			if (layEvent === 'companyDesc') { //公司简介
				layer.open({
					id: '公司简介',
					type: 1,
					title: '公司简介',
					shade: 0.3,
					area: ['90vw', '90vh'],
					content: data.companyDesc
				});
			}
	    });
		form.render();
	}

	// 保存
	$("body").on("click", "#saveCheckBox", function(){
		var selectedData = tableCheckBoxUtil.getValue({
			gridId: 'messageTable'
		});
		if(selectedData.length == 0){
			winui.window.msg("请选择企业", {icon: 2,time: 2000});
			return false;
		}
		AjaxPostUtil.request({url:reqBasePath + "companymation011", params: {ids: selectedData.toString()}, type: 'json', callback: function(json){
   			if(json.returnCode == 0){
				parent.companyList = [].concat(json.rows);
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
			companyName: $("companyName").val()
		};
	}
	
    exports('companyChooseList', {});
});