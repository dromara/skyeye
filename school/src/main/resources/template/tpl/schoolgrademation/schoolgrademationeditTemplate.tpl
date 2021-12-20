{{#bean}}
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">年级名称<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="gradeName" name="gradeName" win-verify="required" placeholder="请输入年级名称" class="layui-input" maxlength="50" value="{{gradeName}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">所属学校：</label>
        <div class="layui-input-block ver-center">
        	{{schoolName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">年级类型：</label>
        <div class="layui-input-block  ver-center">
            {{typeName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12" id="parentIdBox">
        <label class="layui-form-label">所属年级：</label>
        <div class="layui-input-block ver-center">
        	{{parentName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12" id="yearNBox">
        <label class="layui-form-label">N年后达到这个级别<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="yearN" name="yearN" win-verify="required|number" placeholder="N年后达到这个级别" class="layui-input" value="{{yearN}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle"><language showName="com.skyeye.cancel"></language></button>
            <button class="winui-btn" lay-submit lay-filter="formEditBean"><language showName="com.skyeye.save"></language></button>
        </div>
    </div>
{{/bean}}