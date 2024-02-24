{{#bean}}
	<div class="layui-form-item">
        <label class="layui-form-label">标题</label>
        <div class="layui-input-block">
        	<input type="text" id="name" name="name" placeholder="请输入标题" win-verify="required" class="layui-input" value="{{name}}"/>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">时间段</label>
        <div class="layui-input-block">
        	<input type="text" id="holidayTime" name="holidayTime" placeholder="请选择节假日时间段" win-verify="required" class="layui-input"/>
        </div>
    </div>
    <div class="layui-form-item">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle"><language showName="com.skyeye.cancel"></language></button>
            <button class="winui-btn" lay-submit lay-filter="formBean"><language showName="com.skyeye.save"></language></button>
        </div>
    </div>
{{/bean}}