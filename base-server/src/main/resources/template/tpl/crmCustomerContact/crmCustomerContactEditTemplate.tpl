{{#bean}}
	<div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">客户<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="customName" name="customName" placeholder="请选择客户" class="layui-input" readonly="readonly" value="{{customerName}}"/>
            <i class="fa fa-plus-circle input-icon" id="customMationSel"></i>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">联系人<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="contacts" name="contacts" win-verify="required" placeholder="请输入联系人" class="layui-input" maxlength="10" value="{{contacts}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">部门</label>
        <div class="layui-input-block">
            <input type="text" id="department" name="department" placeholder="请输入部门" class="layui-input" maxlength="25" value="{{department}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">职务</label>
        <div class="layui-input-block">
            <input type="text" id="job" name="job" placeholder="请输入职务" class="layui-input" maxlength="25" value="{{job}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">移动电话</label>
        <div class="layui-input-block">
            <input type="text" id="mobilePhone" name="mobilePhone" win-verify="phone" placeholder="请输入移动电话" class="layui-input" maxlength="20" value="{{mobilePhone}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">办公电话</label>
        <div class="layui-input-block">
            <input type="text" id="workPhone" name="workPhone" win-verify="tel" placeholder="请输入办公电话" class="layui-input" maxlength="20" value="{{workPhone}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">邮件</label>
        <div class="layui-input-block">
            <input type="text" id="email" name="email" win-verify="email" placeholder="请输入邮件" class="layui-input" maxlength="25" value="{{email}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">QQ</label>
        <div class="layui-input-block">
            <input type="text" id="qq" name="qq" placeholder="请输入QQ" class="layui-input" maxlength="15" value="{{qq}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">微信</label>
        <div class="layui-input-block">
            <input type="text" id="wechat" name="wechat" placeholder="请输入微信" class="layui-input" maxlength="50" value="{{wechat}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">是否默认<i class="red">*</i></label>
        <div class="layui-input-block winui-radio">
            <input type="radio" name="isDefault" value="1" title="是"/>
            <input type="radio" name="isDefault" value="2" title="否" />
        </div>
    </div>
	<div class="layui-form-item layui-col-xs12">
		<div class="layui-input-block">
			<button class="winui-btn" id="cancle"><language showName="com.skyeye.cancel"></language></button>
			<button class="winui-btn" lay-submit lay-filter="formEditBean"><language showName="com.skyeye.save"></language></button>
		</div>
	</div>
{{/bean}}