{{#bean}}
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">班级名称<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="className" name="className" win-verify="required" placeholder="请输入年级名称" class="layui-input" maxlength="50" value="{{className}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">所属学校：</label>
        <div class="layui-input-block ver-center">
        	{{schoolName}}
        </div>
    </div>
    
    <div class="layui-form-item layui-col-xs12" id="parentIdBox">
        <label class="layui-form-label">所属年级：</label>
        <div class="layui-input-block ver-center">
        	{{gradeName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">班主任</label>
        <div class="layui-input-block">
        	<input type="text" id="masterStaffName" name="masterStaffName" placeholder="请选择班主任" class="layui-input" readonly="readonly"/>
		    <i class="fa fa-user-plus input-icon" id="masterStaffNameSel"></i>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">所在教学楼<i class="red">*</i></label>
        <div class="layui-input-block">
        	<select id="floorId" lay-filter="floorId" lay-search="">
        		<option value="">请选择</option>
        	</select>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">所属届：</label>
        <div class="layui-input-block  ver-center" id="year">
            {{year}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">限制人数<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="limitNumber" name="limitNumber" win-verify="required|number" placeholder="请输入班级限制人数" class="layui-input" value="{{limitNumber}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle"><language showName="com.skyeye.cancel"></language></button>
            <button class="winui-btn" lay-submit lay-filter="formEditBean"><language showName="com.skyeye.save"></language></button>
        </div>
    </div>
{{/bean}}