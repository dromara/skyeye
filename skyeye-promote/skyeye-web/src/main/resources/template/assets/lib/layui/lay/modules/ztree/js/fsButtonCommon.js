

layui.define([], function(exports) {

	var FsButtonCommon = function() {

		};

	FsButtonCommon.prototype.test = function(elem, data, datagrid) {
		alert("测试自定义按钮" + JSON.stringify(data));
	}

	var fsButtonCommon = new FsButtonCommon();
	exports('fsButtonCommon', fsButtonCommon);
});