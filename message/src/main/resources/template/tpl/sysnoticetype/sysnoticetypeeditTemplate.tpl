{{#bean}}
    <div class="layui-form-item">
        <label class="layui-form-label">类型名称<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="typeName" name="typeName" win-verify="required" placeholder="请输入公告类型名称" class="layui-input" value="{{typeName}}"/>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">类型级别<i class="red">*</i></label>
        <div class="layui-input-block ver-center">
            {{pId}}
        </div>
    </div>
    <div class="layui-form-item layui-hide" id="parentIdBox">
        <label class="layui-form-label">上级类型<i class="red">*</i></label>
        <div class="layui-input-block ver-center">
        	{{pName}}
        </div>
    </div>
    <div class="layui-form-item">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle"><language showName="com.skyeye.cancel"></language></button>
            <button class="winui-btn" lay-submit lay-filter="formEditBean"><language showName="com.skyeye.save"></language></button>
        </div>
    </div>
{{/bean}}