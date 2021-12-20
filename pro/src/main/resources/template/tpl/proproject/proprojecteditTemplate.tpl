{{#bean}}
	<div class="layui-form-item layui-col-xs12">
		<span class="hr-title">项目基本信息</span><hr>
	</div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">项目名称<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="projectName" name="projectName" win-verify="required" placeholder="请输入项目名称" class="layui-input" value="{{projectName}}" maxlength="100"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">项目分类<i class="red">*</i></label>
        <div class="layui-input-block">
			<select id="typeId" name="typeId" win-verify="required" lay-filter="typeId" lay-search="">
				
			</select>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">项目编号</label>
        <div class="layui-input-block">
            <input type="text" id="projectNumber" name="projectNumber" placeholder="请输入项目编号" class="layui-input" value="{{projectNumber}}" maxlength="25"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">所属部门</label>
        <div class="layui-input-block">
			<select id="departmentId" name="departmentId" lay-filter="departmentId" lay-search="">
				
			</select>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">计划开始时间<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="startTime" name="startTime" win-verify="required" placeholder="请选择计划开始时间" class="layui-input" value="{{startTime}}" />
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">计划完成时间<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="endTime" name="endTime" win-verify="required" placeholder="请选择计划完成时间" class="layui-input" value="{{endTime}}" />
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">客户名称</label>
        <div class="layui-input-block">
			<select id="customerId" name="customerId" lay-filter="customerId" lay-search="">
				
			</select>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">关联合同</label>
        <div class="layui-input-block">
			<select id="contractId" name="contractId" lay-filter="contractId" lay-search="">
				
			</select>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">对方联系人</label>
        <div class="layui-input-block">
            <input type="text" id="contactName" name="contactName" placeholder="请输入对方联系人" class="layui-input" value="{{contactName}}" maxlength="25"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">固定电话</label>
        <div class="layui-input-block">
            <input type="text" id="telphone" name="telphone" win-verify="tel" placeholder="请输入办公电话" class="layui-input" value="{{telphone}}" maxlength="20"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">移动电话</label>
        <div class="layui-input-block">
            <input type="text" id="mobile" name="mobile" win-verify="phone" placeholder="请输入移动电话" class="layui-input" value="{{mobile}}" maxlength="11"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">邮箱</label>
        <div class="layui-input-block">
            <input type="text" id="mail" name="mail" win-verify="email" placeholder="请输入邮箱" class="layui-input" value="{{mail}}" maxlength="50"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">QQ</label>
        <div class="layui-input-block">
            <input type="text" id="qq" name="qq" placeholder="请输入QQ" class="layui-input" value="{{qq}}" maxlength="15"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">预估工作量</label>
        <div class="layui-input-block">
            <input type="number" id="estimatedWorkload" name="estimatedWorkload" placeholder="请输入预估工作量" class="layui-input" value="{{estimatedWorkload}}" maxlength="50"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">预估成本费用</label>
        <div class="layui-input-block">
            <input type="text" id="estimatedCost" name="estimatedCost" placeholder="请输入预估成本费用" class="layui-input" value="{{estimatedCost}}" win-verify="money"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
		<span class="hr-title">业务需求和目标</span><hr>
	</div>
    <div class="layui-form-item layui-col-xs10" style="margin-left: 8%;">
    	<script id="container" name="content" type="text/plain"></script>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">附件</label>
        <div class="layui-input-block" id="enclosureUpload">
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
		<div class="layui-input-block">
			<button class="winui-btn" id="cancle"><language showName="com.skyeye.cancel"></language></button>
			<button class="winui-btn typeOne layui-hide" lay-submit lay-filter="formEditBean">保存为草稿</button>
            <button class="winui-btn typeOne layui-hide" lay-submit lay-filter="formSubBean">提交审批</button>
            <button class="winui-btn typeTwo layui-hide" lay-submit lay-filter="save"><language showName="com.skyeye.save"></language></button>
		</div>
	</div>
{{/bean}}