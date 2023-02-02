
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
	
	var _html = {
		'color': `<div class="layui-form-item layui-col-xs6">
					<label class="layui-form-label">按钮颜色<i class="red">*</i></label>
					<div class="layui-input-block">
						<select id="color" name="color" lay-filter="color" win-verify="required"></select>
					</div>
				</div>`,
		'businessApi': `<div class="layui-form-item layui-col-xs6">
							<label class="layui-form-label">所属服务<i class="red">*</i></label>
							<div class="layui-input-block">
								<select id="serviceStr" name="serviceStr" lay-filter="serviceStr" win-verify="required"></select>
							</div>
						</div>
						<div class="layui-form-item layui-col-xs6">
							<label class="layui-form-label">接口地址<i class="red">*</i></label>
							<div class="layui-input-block">
								<input type="text" id="api" name="api" placeholder="请输入接口地址" win-verify="required" class="layui-input" maxlength="200"/>
							</div>
						</div>
						<div class="layui-form-item layui-col-xs6">
							<label class="layui-form-label">请求方式<i class="red">*</i></label>
							<div class="layui-input-block">
								<select id="method" name="method" lay-filter="method" win-verify="required"></select>
							</div>
						</div>
						<div class="layui-form-item layui-col-xs6">
							<label class="layui-form-label">接口入参</label>
							<div class="layui-input-block" id="pageParams">
								
							</div>
						</div>`,
		'operateOpenPage': `<div class="layui-form-item layui-col-xs6">
								<label class="layui-form-label">新开页面名称<i class="red">*</i></label>
								<div class="layui-input-block">
									<input type="text" id="openPageName" name="openPageName" win-verify="required" placeholder="请输入新开页面名称" class="layui-input" maxlength="200"/>
								</div>
							</div>
							<div class="layui-form-item layui-col-xs12">
								<label class="layui-form-label">页面类型<i class="red">*</i></label>
								<div class="layui-input-block winui-radio">
									<input type="radio" name="type" value="1" title="自定义页面" lay-filter="type" checked/>
									<input type="radio" name="type" value="2" title="表单布局" lay-filter="type" />
								</div>
							</div>
							<div id="typeChangeBox">
							</div>
							<div class="layui-form-item layui-col-xs6">
								<label class="layui-form-label">页面入参</label>
								<div class="layui-input-block" id="pageParams">
									
								</div>
							</div>`,
		'customPageUrl': `<div class="layui-form-item layui-col-xs6">
							<label class="layui-form-label">页面地址<i class="red">*</i></label>
							<div class="layui-input-block">
								<input type="text" id="pageUrl" name="pageUrl" placeholder="请输入接口地址" win-verify="required" class="layui-input" maxlength="200"/>
							</div>
						</div>`,
		'dsFormPage': `<div class="layui-form-item layui-col-xs6">
							<label class="layui-form-label">表单布局<i class="red">*</i></label>
							<div class="layui-input-block">
								<input type="text" id="pageUrl" name="pageUrl" placeholder="请选择表单布局" win-verify="required" class="layui-input"/>
							</div>
						</div>`,
	};

	matchingLanguage();
	form.render();
	form.on('submit(formAddBean)', function (data) {
		if (winui.verifyForm(data.elem)) {
			var params = {
				objectId: parent.objectId,
				objectKey: parent.objectKey,
				name: $("#name").val(),
				department: $("#department").val(),
				job: $("#job").val(),
				workPhone: $("#workPhone").val(),
				mobilePhone: $("#mobilePhone").val(),
				email: $("#email").val(),
				qq: $("#qq").val(),
				wechat: $("#wechat").val(),
				isDefault: $("input[name='isDefault']:checked").val()
			};
			AjaxPostUtil.request({url: reqBasePath + "writeContactsMation", params: params, type: 'json', method: 'POST', callback: function (json) {
				parent.layer.close(index);
				parent.refreshCode = '0';
			}});
		}
		return false;
	});

	$("body").on("click", "#cancle", function() {
		parent.layer.close(index);
	});
});