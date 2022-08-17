layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'jquery', 'winui'], function (exports) {
	winui.renderColor();
	var index = parent.layer.getFrameIndex(window.name);
	var $ = layui.$,
		form = layui.form;

	// 表格的序号
	var rowNum = 1;
	var taxRateTemplate = $("#taxRateTemplate").html();
	var ue = null;

	showGrid({
		id: "showForm",
		url: reqBasePath + "companymation004",
		params: {rowId: parent.rowId},
		pagination: false,
		template: $("#beanTemplate").html(),
		ajaxSendLoadBefore: function(hdb){
		},
		ajaxSendAfter:function (json) {

			ue = ueEditorUtil.initEditor('container');
			ue.addListener("ready", function () {
				ue.setContent(json.bean.companyDesc);
			});

			if(json.bean.pId == '0' || isNull(json.bean.pId)){
				$("#parentIdBox").addClass("layui-hide");
				$("input:radio[name=companyType][value=1]").attr("checked", true);
			} else {
				$("input:radio[name=companyType][value=2]").attr("checked", true);
			}
			// 初始化总公司
			loadParentCompany(json.bean.pId, json.bean.id);

			//公司地址
			initArea(json.bean);//加载省级行政区划

			form.on('radio(companyType)', function (data) {
				var val = data.value;
				if(val == '1'){//总公司
					$("#parentIdBox").addClass("layui-hide");
				}else if(val == '2'){//子公司
					$("#parentIdBox").removeClass("layui-hide");
				} else {
					winui.window.msg('状态值错误', {icon: 2, time: 2000});
				}
			});

			// 设置个人所得税比例
			if (!isNull(json.bean.taxRateJson)){
				$.each(json.bean.taxRateJson, function(i, item){
					addRow();
					$("#minMoney" + (rowNum - 1)).val(item.minMoney);
					$("#maxMoney" + (rowNum - 1)).val(item.maxMoney);
					$("#janRate" + (rowNum - 1)).val(item.janRate);
					$("#febRate" + (rowNum - 1)).val(item.febRate);
					$("#marRate" + (rowNum - 1)).val(item.marRate);
					$("#aprRate" + (rowNum - 1)).val(item.aprRate);
					$("#mayRate" + (rowNum - 1)).val(item.mayRate);
					$("#junRate" + (rowNum - 1)).val(item.junRate);
					$("#julRate" + (rowNum - 1)).val(item.julRate);
					$("#augRate" + (rowNum - 1)).val(item.augRate);
					$("#septRate" + (rowNum - 1)).val(item.septRate);
					$("#octRate" + (rowNum - 1)).val(item.octRate);
					$("#novRate" + (rowNum - 1)).val(item.novRate);
					$("#decRate" + (rowNum - 1)).val(item.decRate);
				});
			}

			matchingLanguage();
			form.render();
			form.on('submit(formEditBean)', function (data) {
				if (winui.verifyForm(data.elem)) {
					var pId = '0';
					if($("input[name='companyType']:checked").val() == '2'){
						if(isNull($("#OverAllCompany").val())) {
							winui.window.msg('请选择总公司', {icon: 2, time: 2000});
							return false;
						} else {
							pId = $("#OverAllCompany").val();
						}
					}
					var provinceId = "", cityId = "", areaId = "", townshipId = "";
					if (!isNull($("#provinceId").val())) {
						provinceId = $("#provinceId").val();
					}
					if (!isNull($("#cityId").val())) {
						cityId = $("#cityId").val();
					}
					if (!isNull($("#areaId").val())) {
						areaId = $("#areaId").val();
					}
					if (!isNull($("#townshipId").val())) {
						townshipId = $("#townshipId").val();
					}
					var params = {
						companyName: $("#companyName").val(),
						companyDesc: encodeURIComponent(ue.getContent()),
						pId: pId,
						provinceId: provinceId,
						cityId: cityId,
						areaId: areaId,
						townshipId: townshipId,
						addressDetailed: $("#addressDetailed").val(),
						rowId: parent.rowId
					};

					var tableData = new Array();
					$.each($("#taxRateTable tr"), function(i, item) {
						// 获取行编号
						var rowNum = $(item).attr("trcusid").replace("tr", "");
						var row = {
							minMoney: $("#minMoney" + rowNum).val(),
							maxMoney: $("#maxMoney" + rowNum).val(),
							janRate: $("#janRate" + rowNum).val(),
							febRate: $("#febRate" + rowNum).val(),
							marRate: $("#marRate" + rowNum).val(),
							aprRate: $("#aprRate" + rowNum).val(),
							mayRate: $("#mayRate" + rowNum).val(),
							junRate: $("#junRate" + rowNum).val(),
							julRate: $("#julRate" + rowNum).val(),
							augRate: $("#augRate" + rowNum).val(),
							septRate: $("#septRate" + rowNum).val(),
							octRate: $("#octRate" + rowNum).val(),
							novRate: $("#novRate" + rowNum).val(),
							decRate: $("#decRate" + rowNum).val(),
							sortNo: (i + 1)
						};
						tableData.push(row);
					});
					params.taxRateStr = JSON.stringify(tableData);

					AjaxPostUtil.request({url: reqBasePath + "companymation005", params: params, type: 'json', callback: function (json) {
						parent.layer.close(index);
						parent.refreshCode = '0';
					}});
				}
				return false;
			});

		}
	});

	// 加载总公司
	function loadParentCompany(pId, id){
		showGrid({
			id: "OverAllCompany",
			url: reqBasePath + "companymation006",
			params: {notId: id},
			pagination: false,
			template: getFileContent('tpl/template/select-option.tpl'),
			ajaxSendLoadBefore: function(hdb){
			},
			ajaxSendAfter:function (json) {
				$("#OverAllCompany").val(pId);
				form.render('select');
			}
		});
	}

	//初始化行政区划-省
	function initArea(bean){
		AjaxPostUtil.request({url: reqBasePath + "commontarea001", params: {}, type: 'json', callback: function (json) {
			var str = '<dd class="layui-col-xs3"><select id="provinceId" win-verify="required" lay-filter="areaProvince" lay-search=""><option value="">请选择</option>';
			for(var i = 0; i < json.rows.length; i++){
				str += '<option value="' + json.rows[i].id + '">' + json.rows[i].name + '</option>';
			}
			str += '</select></dd>';
			$("#lockParentSel").append(str);
			if (!isNull(bean.provinceId)){
				$("#provinceId").val(bean.provinceId);
				initAreaCity(bean);
			}
			form.render('select');
		}});
	}

	//初始化行政区划-市
	function initAreaCity(bean){
		AjaxPostUtil.request({url: reqBasePath + "commontarea002", params: {rowId: $("#provinceId").val()}, type: 'json', callback: function (json) {
			var str = '<dd class="layui-col-xs3"><select id="cityId" lay-filter="areaCity" lay-search=""><option value="">请选择</option>';
			for(var i = 0; i < json.rows.length; i++){
				str += '<option value="' + json.rows[i].id + '">' + json.rows[i].name + '</option>';
			}
			str += '</select></dd>';
			$("#lockParentSel").append(str);
			if (!isNull(bean.cityId)){
				$("#cityId").val(bean.cityId);
				initAreaChildArea(bean);
			}
			form.render('select');
		}});
	}

	//初始化行政区划-县
	function initAreaChildArea(bean){
		AjaxPostUtil.request({url: reqBasePath + "commontarea003", params: {rowId: $("#cityId").val()}, type: 'json', callback: function (json) {
			var str = '<dd class="layui-col-xs3"><select id="areaId" lay-filter="area" lay-search=""><option value="">请选择</option>';
			for(var i = 0; i < json.rows.length; i++){
				str += '<option value="' + json.rows[i].id + '">' + json.rows[i].name + '</option>';
			}
			str += '</select></dd>';
			$("#lockParentSel").append(str);
			if (!isNull(bean.areaId)){
				$("#areaId").val(bean.areaId);
				initAreaTownShip(bean);
			}
			form.render('select');
		}});
	}

	//初始化行政区划-镇
	function initAreaTownShip(bean){
		AjaxPostUtil.request({url: reqBasePath + "commontarea004", params: {rowId: $("#areaId").val()}, type: 'json', callback: function (json) {
			var str = '<dd class="layui-col-xs3"><select id="townshipId" lay-filter="areaTownShip" lay-search=""><option value="">请选择</option>';
			for(var i = 0; i < json.rows.length; i++){
				str += '<option value="' + json.rows[i].id + '">' + json.rows[i].name + '</option>';
			}
			str += '</select></dd>';
			$("#lockParentSel").append(str);
			if (!isNull(bean.townshipId)){
				$("#townshipId").val(bean.townshipId);
			}
			form.render('select');
		}});
	}

	form.on('select(areaProvince)', function(data) {
		layui.$(data.elem).parent('dd').nextAll().remove();
		if(isNull(data.value) || data.value == '请选择'){
		} else {
			loadChildCityArea();
		}
	});
	form.on('select(areaCity)', function(data) {
		layui.$(data.elem).parent('dd').nextAll().remove();
		if(isNull(data.value) || data.value == '请选择'){
		} else {
			loadChildArea();
		}
	});
	form.on('select(area)', function(data) {
		layui.$(data.elem).parent('dd').nextAll().remove();
		if(isNull(data.value) || data.value == '请选择'){
		} else {
			loadChildAreaTownShip();
		}
	});

	//省级行政区划
	function loadChildProvinceArea(){
		AjaxPostUtil.request({url: reqBasePath + "commontarea001", params: {}, type: 'json', callback: function (json) {
			var str = '<dd class="layui-col-xs3"><select id="provinceId" win-verify="required" lay-filter="areaProvince" lay-search=""><option value="">请选择</option>';
			for(var i = 0; i < json.rows.length; i++){
				str += '<option value="' + json.rows[i].id + '">' + json.rows[i].name + '</option>';
			}
			str += '</select></dd>';
			$("#lockParentSel").append(str);
			form.render('select');
		}});
	}

	//市级行政区划
	function loadChildCityArea(){
		AjaxPostUtil.request({url: reqBasePath + "commontarea002", params: {rowId: $("#provinceId").val()}, type: 'json', callback: function (json) {
			var str = '<dd class="layui-col-xs3"><select id="cityId" lay-filter="areaCity" lay-search=""><option value="">请选择</option>';
			for(var i = 0; i < json.rows.length; i++){
				str += '<option value="' + json.rows[i].id + '">' + json.rows[i].name + '</option>';
			}
			str += '</select></dd>';
			$("#lockParentSel").append(str);
			form.render('select');
		}});
	}

	//县级行政区划
	function loadChildArea(){
		AjaxPostUtil.request({url: reqBasePath + "commontarea003", params: {rowId: $("#cityId").val()}, type: 'json', callback: function (json) {
			var str = '<dd class="layui-col-xs3"><select id="areaId" lay-filter="area" lay-search=""><option value="">请选择</option>';
			for(var i = 0; i < json.rows.length; i++){
				str += '<option value="' + json.rows[i].id + '">' + json.rows[i].name + '</option>';
			}
			str += '</select></dd>';
			$("#lockParentSel").append(str);
			form.render('select');
		}});
	}

	//镇级行政区划
	function loadChildAreaTownShip(){
		AjaxPostUtil.request({url: reqBasePath + "commontarea004", params: {rowId: $("#areaId").val()}, type: 'json', callback: function (json) {
			var str = '<dd class="layui-col-xs3"><select id="townshipId" lay-filter="areaTownShip" lay-search=""><option value="">请选择</option>';
			for(var i = 0; i < json.rows.length; i++){
				str += '<option value="' + json.rows[i].id + '">' + json.rows[i].name + '</option>';
			}
			str += '</select></dd>';
			$("#lockParentSel").append(str);
			form.render('select');
		}});
	}

	// 新增行
	$("body").on("click", "#addRow", function() {
		addRow();
	});

	// 删除行
	$("body").on("click", "#deleteRow", function() {
		deleteRow("taxRateTable");
	});

	// 新增行
	function addRow() {
		var par = {
			id: "row" + rowNum.toString(), //checkbox的id
			trId: "tr" + rowNum.toString(), //行的id
			minMoney: "minMoney" + rowNum.toString(), //最小值id
			maxMoney: "maxMoney" + rowNum.toString(), //最大值id
			janRate: "janRate" + rowNum.toString(), //一月id
			febRate: "febRate" + rowNum.toString(), //二月id
			marRate: "marRate" + rowNum.toString(), //三月id
			aprRate: "aprRate" + rowNum.toString(), //四月id
			mayRate: "mayRate" + rowNum.toString(), //五月id
			junRate: "junRate" + rowNum.toString(), //六月id
			julRate: "julRate" + rowNum.toString(), //七月id
			augRate: "augRate" + rowNum.toString(), //八月id
			septRate: "septRate" + rowNum.toString(), //九月id
			octRate: "octRate" + rowNum.toString(), //十月id
			novRate: "novRate" + rowNum.toString(), //十一月id
			decRate: "decRate" + rowNum.toString() //十二月id
		};
		$("#taxRateTable").append(getDataUseHandlebars(taxRateTemplate, par));
		form.render();
		rowNum++;
	}

	// 删除行
	function deleteRow(tableId) {
		var checkRow = $("#" + tableId + " input[type='checkbox'][name='tableCheckRow']:checked");
		if(checkRow.length > 0) {
			$.each(checkRow, function(i, item) {
				//移除界面上的信息
				$(item).parent().parent().remove();
			});
		} else {
			winui.window.msg('请选择要删除的行', {icon: 2, time: 2000});
		}
	}

	//取消
	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});