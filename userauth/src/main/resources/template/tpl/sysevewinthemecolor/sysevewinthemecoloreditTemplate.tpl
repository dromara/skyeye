{{#bean}}
	<div class="layui-form-item">
        <label class="layui-form-label">主题属性<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="colorClass" name="colorClass" win-verify="required" placeholder="请输入主题颜色属性" class="layui-input" value="{{colorClass}}" />
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">主题效果<i class="red">*</i></label>
        <div class="layui-input-block color-choose" style="width: auto">
            <div id="modelEffect" class="{{colorClass}}"></div>
        </div>
    </div>
    <div class="layui-form-item">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle"><language showName="com.skyeye.cancel"></language></button>
            <button class="winui-btn" lay-submit lay-filter="formEditBean"><language showName="com.skyeye.save"></language></button>
        </div>
    </div>
{{/bean}}