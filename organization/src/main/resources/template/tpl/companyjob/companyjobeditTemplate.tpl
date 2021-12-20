{{#bean}}
	<div class="layui-form-item">
        <label class="layui-form-label">所属公司<i class="red">*</i></label>
        <div class="layui-input-block">
        	<select lay-filter="companyId" lay-search="" win-verify="required" id="companyId">
                
            </select>
        </div>
    </div>
	<div class="layui-form-item">
        <label class="layui-form-label">所属部门<i class="red">*</i></label>
        <div class="layui-input-block">
        	<select lay-filter="departmentId" lay-search="" win-verify="required" id="departmentId">
                
            </select>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">所属父职位</label>
        <div class="layui-input-block" id="pIdBox">
        	
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">职位名称<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="jobName" name="jobName" win-verify="required" placeholder="请输入职位名称" class="layui-input" maxlength="50" value="{{jobName}}"/>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">职位简介</label>
        <div class="layui-input-block">
        	<textarea id="content" name="content" style="display: none;">{{jobDesc}}</textarea>
        </div>
    </div>
    <div class="layui-form-item">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle"><language showName="com.skyeye.cancel"></language></button>
            <button class="winui-btn" lay-submit lay-filter="formEditBean"><language showName="com.skyeye.save"></language></button>
        </div>
    </div>
{{/bean}}