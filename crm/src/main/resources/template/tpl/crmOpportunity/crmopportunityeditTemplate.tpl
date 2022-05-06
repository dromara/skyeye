{{#bean}}
	<div class="layui-form-item layui-col-xs12">
		<span class="hr-title">客户信息</span><hr>
	</div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">客户名称<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="customName" name="customName" placeholder="请选择客户" class="layui-input" readonly="readonly"/>
            <i class="fa fa-plus-circle input-icon" id="customMationSel"></i>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">所属行业</label>
        <div class="layui-input-block ver-center">
			{{industryName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">所在城市</label>
        <div class="layui-input-block">
            <input type="text" id="city" name="city" placeholder="请输入所在城市" class="layui-input" value="{{city}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">详细地址</label>
        <div class="layui-input-block">
            <input type="text" id="detailAddress" name="detailAddress" placeholder="请输入详细地址" class="layui-input" value="{{detailAddress}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
		<span class="hr-title">商机信息</span><hr>
	</div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">商机名称<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="title" name="title" win-verify="required" placeholder="请输入商机名称" class="layui-input" value="{{title}}"/>
        </div>
    </div>
	<div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">商机来源<i class="red">*</i></label>
        <div class="layui-input-block">
        	<select id="fromId" name="fromId" lay-filter="fromId" win-verify="required">
        		
        	</select>
        </div>
    </div>
	<div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">预计成交金额</label>
        <div class="layui-input-block">
            <input type="text" id="estimatePrice" name="estimatePrice" placeholder="请输入预计成交金额" class="layui-input" win-verify="money" value="{{estimatePrice}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">预计结单日期</label>
        <div class="layui-input-block">
        	<input type="text" id="estimateEndTime" name="estimateEndTime" placeholder="请输入预计结单日期" class="layui-input" value="{{estimateEndTime}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">联系人<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="contacts" name="contacts" win-verify="required" placeholder="请输入联系人" class="layui-input" value="{{contacts}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">部门</label>
        <div class="layui-input-block">
            <input type="text" id="department" name="department" placeholder="请输入部门" class="layui-input" value="{{department}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">职务</label>
        <div class="layui-input-block">
            <input type="text" id="job" name="job" placeholder="请输入职务" class="layui-input" value="{{job}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">办公电话</label>
        <div class="layui-input-block">
            <input type="text" id="workPhone" name="workPhone" placeholder="请输入办公电话" win-verify="tel" class="layui-input" value="{{workPhone}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">移动电话</label>
        <div class="layui-input-block">
            <input type="text" id="mobilePhone" name="mobilePhone" placeholder="请输入移动电话" win-verify="phone" class="layui-input" value="{{mobilePhone}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">邮件</label>
        <div class="layui-input-block">
            <input type="text" id="email" name="email" placeholder="请输入邮件" win-verify="email" class="layui-input" value="{{email}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">QQ</label>
        <div class="layui-input-block">
            <input type="text" id="qq" name="qq" placeholder="请输入QQ" class="layui-input" value="{{qq}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">主要业务需求</label>
        <div class="layui-input-block">
        	<textarea id="businessNeed" name="businessNeed" placeholder="请输入主要业务需求" class="layui-textarea">{{businessNeed}}</textarea>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">附件资料</label>
        <div class="layui-input-block" id="enclosureUpload">
        </div>
    </div>
   	<div class="layui-form-item layui-col-xs12">
		<span class="hr-title">我方负责信息</span><hr>
	</div>
   	<div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">商机所属部门<i class="red">*</i></label>
        <div class="layui-input-block">
        	<select id="subDepartments" name="subDepartments" win-verify="required" lay-filter="subDepartments" >
        		
        	</select>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">商机负责人<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="responsId" name="responsId" win-verify="required" placeholder="请选择商机负责人" class="layui-input" />
        	<i class="fa fa-user-plus input-icon" id="responsIdSelPeople"></i>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">商机参与人</label>
        <div class="layui-input-block">
        	<input type="text" id="partId" name="partId" placeholder="请选择商机参与人" class="layui-input" />
        	<i class="fa fa-user-plus input-icon" id="partIdSelPeople"></i>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">商机关注人</label>
        <div class="layui-input-block">
        	<input type="text" id="followId" name="followId" placeholder="请选择商机关注人" class="layui-input" />
        	<i class="fa fa-user-plus input-icon" id="followIdSelPeople"></i>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
		<div class="layui-input-block">
			<button class="winui-btn" id="cancle"><language showName="com.skyeye.cancel"></language></button>
			<button class="winui-btn typeOne layui-hide" lay-submit lay-filter="formEditBean">保存为草稿</button>
			<button class="winui-btn typeOne layui-hide" lay-submit lay-filter="formSubBean">提交审批</button>
			<button class="winui-btn typeTwo layui-hide" lay-submit lay-filter="subBean"><language showName="com.skyeye.save"></language></button>
		</div>
	</div>
{{/bean}}