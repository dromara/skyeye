{{#bean}}
	<div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">所属公司<i class="red">*</i></label>
        <div class="layui-input-block">
        	<select lay-filter="companyId" lay-search="" win-verify="required" id="companyId">
            </select>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">部门名称<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="departmentName" name="departmentName" win-verify="required" placeholder="请输入部门名称" class="layui-input" maxlength="50" value="{{departmentName}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">加班结算方式<i class="red">*</i></label>
        <div class="layui-input-block">
            <select lay-filter="overtimeSettlementType" lay-search="" win-verify="required" id="overtimeSettlementType">
                <option value="">请选择</option>
                <option value="1">单倍薪资结算</option>
                <option value="2">1.5倍薪资结算</option>
                <option value="3">双倍薪资结算</option>
                <option value="6">补休结算</option>
            </select>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">部门简介</label>
        <div class="layui-input-block">
        	<textarea id="content" name="content" style="display: none;">{{departmentDesc}}</textarea>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle"><language showName="com.skyeye.cancel"></language></button>
            <button class="winui-btn" lay-submit lay-filter="formEditBean"><language showName="com.skyeye.save"></language></button>
        </div>
    </div>
{{/bean}}