{{#bean}}
	<div class="layui-form-item layui-col-xs12">
		<span class="hr-title">桌面信息</span><hr>
	</div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">中文名称<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="desktopName" name="desktopName" win-verify="required" placeholder="请输入桌面名称" class="layui-input" value="{{desktopName}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">英文名称<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="desktopCnName" name="desktopCnName" win-verify="required" placeholder="请输入英文名称" class="layui-input" value="{{desktopCnName}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle"><language showName="com.skyeye.cancel"></language></button>
            <button class="winui-btn" lay-submit lay-filter="formEditBean"><language showName="com.skyeye.save"></language></button>
        </div>
    </div>
{{/bean}}