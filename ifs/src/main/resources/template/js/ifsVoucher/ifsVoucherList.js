
layui.config({
	base: basePath, 
	version: skyeyeVersion
}).extend({
    window: 'js/winui.window'
}).define(['window', 'table', 'jquery', 'winui', 'upload'], function (exports) {
	winui.renderColor();
	var $ = layui.$,
		form = layui.form,
		upload = layui.upload,
		table = layui.table;
	var exts = imageType.concat(officeType).join('|');
	$("#showInfo").html("仅支持以下格式的凭证文件：【" + imageType.concat(officeType).join(', ') + "】");
	
	authBtn('1641208147247');

	table.render({
		id: 'messageTable',
		elem: '#messageTable',
		method: 'post',
		url: sysMainMation.ifsBasePath + "ifsVoucher001",
		where: getTableParams(),
		even: true,
		page: true,
		limits: getLimits(),
		limit: getLimit(),
		cols: [[
			{ title: systemLanguage["com.skyeye.serialNumber"][languageType], type: 'numbers' },
			{ field: 'name', title: '名称', width: 150 },
			{ field: 'path', title: '展示', width: 80, align: 'center', templet: function (d) {
				var fileExt = sysFileUtil.getFileExt(d.path);
				if($.inArray(fileExt[0], imageType) >= 0){
					return '<img src="' + fileBasePath + d.path + '" class="photo-img" lay-event="logo">';
				}
				return '<img src="../../assets/images/cloud/doc.png" class="photo-img" lay-event="logo">';
			}},
			{ field: 'state', title: '状态', width: 80, templet: function (d) {
				return skyeyeClassEnumUtil.getEnumDataNameByCodeAndKey("voucherState", 'id', d.state, 'name');
			}},
			{ field: 'createName', title: systemLanguage["com.skyeye.createName"][languageType], width: 120 },
			{ field: 'createTime', title: systemLanguage["com.skyeye.createTime"][languageType], align: 'center', width: 150 },
			{ field: 'lastUpdateName', title: systemLanguage["com.skyeye.lastUpdateName"][languageType], align: 'left', width: 120 },
			{ field: 'lastUpdateTime', title: systemLanguage["com.skyeye.lastUpdateTime"][languageType], align: 'center', width: 150 },
			{ title: systemLanguage["com.skyeye.operation"][languageType], fixed: 'right', align: 'center', width: 100, toolbar: '#tableBar' }
		]],
		done: function(json) {
			matchingLanguage();
			initTableSearchUtil.initAdvancedSearch(this, json.searchFilter, form, "请输入名称", function () {
				table.reloadData("messageTable", {page: {curr: 1}, where: getTableParams()});
			});
		}
	});

	table.on('tool(messageTable)', function (obj) {
		var data = obj.data;
		var layEvent = obj.event;
		if (layEvent === 'del') { //删除
			del(data, obj);
		} else if (layEvent === 'logo') {
			var fileExt = sysFileUtil.getFileExt(data.path);
			if($.inArray(fileExt[0], imageType) >= 0){
				systemCommonUtil.showPicImg(fileBasePath + data.path);
			} else {
				sysFileUtil.download(data.path, data.name);
			}
		}
	});

	// 删除
	function del(data, obj) {
		layer.confirm(systemLanguage["com.skyeye.deleteOperationMsg"][languageType], {icon: 3, title: systemLanguage["com.skyeye.deleteOperation"][languageType]}, function (index) {
			layer.close(index);
			AjaxPostUtil.request({url: sysMainMation.ifsBasePath + "ifsVoucher003", params: {id: data.id}, type: 'json', method: 'DELETE', callback: function (json) {
				winui.window.msg(systemLanguage["com.skyeye.deleteOperationSuccessMsg"][languageType], {icon: 1, time: 2000});
				loadTable();
			}});
		});
	}
	
	upload.render({
		elem: '#addBean',
		url: reqBasePath + 'common003',
		data: {type: 21},
		exts: exts,
		done: function(json) {
			if (json.returnCode == 0) {
				var param = {
					type: 1, // 凭证类型  1.原始凭证  2.手工录入凭证
					path: json.bean.picUrl,
					name: json.bean.fileName
				}
				AjaxPostUtil.request({url: sysMainMation.ifsBasePath + "ifsVoucher002", params: param, type: 'json', method: "POST", callback: function (json) {
					winui.window.msg("上传成功", {icon: 1, time: 2000});
					loadTable();
	    		}});
			} else {
				winui.window.msg(json.returnMessage, {icon: 2, time: 2000});
			}
		},
		error: function(e) {
			// 请求异常回调
			console.log(e);
		}
	});

	form.render();
	$("body").on("click", "#reloadTable", function() {
		loadTable();
	});

	function loadTable() {
		table.reloadData("messageTable", {where: getTableParams()});
	}

	function getTableParams() {
		return $.extend(true, {}, initTableSearchUtil.getSearchValue("messageTable"));
	}
    
    exports('ifsVoucherList', {});
});
