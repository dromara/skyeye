{{#bean}}
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">来源名称<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="fromName" name="fromName" win-verify="required" placeholder="请输入来源名称" class="layui-input" value="{{fromName}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle"><language showName="com.skyeye.cancel"></language></button>
            <button class="winui-btn" lay-submit lay-filter="formEditBean"><language showName="com.skyeye.save"></language></button>
        </div>
    </div>
{{/bean}}