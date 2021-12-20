{{#bean}}
    <div class="layui-form-item">
        <label class="layui-form-label">限制标题<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="requireName" name="requireName" win-verify="required" placeholder="请输入限制标题" class="layui-input" value="{{requireName}}" maxlength="25"/>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">前端标签<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="formerRequirement" name="formerRequirement" placeholder="请输入前端限制标签" win-verify="required" class="layui-input" value="{{formerRequirement}}" maxlength="25"/>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">后端标签<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="afterRequirement" name="afterRequirement" placeholder="请输入后端限制标签" win-verify="required" class="layui-input" value="{{afterRequirement}}" maxlength="25"/>
        </div>
    </div>
    <div class="layui-form-item">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle"><language showName="com.skyeye.cancel"></language></button>
            <button class="winui-btn" lay-submit lay-filter="formEditBean"><language showName="com.skyeye.save"></language></button>
        </div>
    </div>
{{/bean}}