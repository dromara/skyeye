{{#bean}}
    <div class="layui-form-item">
        <label class="layui-form-label">敏感词名称<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="sensitiveWord" name="sensitiveWord" win-verify="required" placeholder="请输入论坛敏感词" class="layui-input" value="{{sensitiveWord}}"/>
        </div>
    </div>
    <div class="layui-form-item">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle"><language showName="com.skyeye.cancel"></language></button>
            <button class="winui-btn" lay-submit lay-filter="formEditBean"><language showName="com.skyeye.save"></language></button>
        </div>
    </div>
{{/bean}}