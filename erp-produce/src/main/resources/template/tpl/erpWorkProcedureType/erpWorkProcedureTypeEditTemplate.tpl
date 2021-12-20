{{#bean}}
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">名称<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="typeName" name="typeName" win-verify="required" placeholder="请输入名称" class="layui-input" value="{{name}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle"><language showName="com.skyeye.cancel"></language></button>
            <button class="winui-btn" lay-submit lay-filter="formEditBean"><language showName="com.skyeye.save"></language></button>
        </div>
    </div>
{{/bean}}