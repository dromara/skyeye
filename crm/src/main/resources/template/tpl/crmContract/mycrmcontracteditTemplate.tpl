{{#bean}}
	<div class="layui-form-item layui-col-xs6">
	    <label class="layui-form-label">客户<i class="red">*</i></label>
	    <div class="layui-input-block">
			<input type="text" id="customName" name="customName" placeholder="请选择客户" class="layui-input" readonly="readonly" win-verify="required" value="{{customerName}}"/>
            <i class="fa fa-plus-circle input-icon" id="customMationSel"></i>
	    </div>
	</div>
	<div class="layui-form-item layui-col-xs6">
	    <label class="layui-form-label">所在城市</label>
	    <div class="layui-input-block">
	        <input type="text" id="city" name="city" placeholder="请输入所在城市" class="layui-input" maxlength="15" value="{{city}}"/>
	    </div>
	</div>
	<div class="layui-form-item layui-col-xs12">
	    <label class="layui-form-label">详细地址</label>
	    <div class="layui-input-block">
	        <input type="text" id="detailAddress" name="detailAddress" placeholder="请输入详细地址" class="layui-input" maxlength="50" value="{{detailAddress}}"/>
	    </div>
	</div>
	<div class="layui-form-item layui-col-xs6">
	    <label class="layui-form-label">合同名称<i class="red">*</i></label>
	    <div class="layui-input-block">
	        <input type="text" id="title" name="title" win-verify="required" placeholder="请输入合同名称" class="layui-input" maxlength="50" value="{{title}}"/>
	    </div>
	</div>
	<div class="layui-form-item layui-col-xs6">
	    <label class="layui-form-label">合同编号<i class="red">*</i></label>
	    <div class="layui-input-block">
	        <input type="text" id="num" name="num" win-verify="required" placeholder="请输入合同编号" class="layui-input" maxlength="20" value="{{num}}"/>
	    </div>
	</div>
	<div class="layui-form-item layui-col-xs6">
	    <label class="layui-form-label">合同金额</label>
	    <div class="layui-input-block">
	        <input type="number" id="price" name="price" win-verify="double" placeholder="请输入合同金额" class="layui-input" maxlength="24" value="{{price}}"/>
	    </div>
	</div>
	<div class="layui-form-item layui-col-xs6">
	    <label class="layui-form-label">签约日期<i class="red">*</i></label>
	    <div class="layui-input-block">
	        <input type="text" id="signingTime" name="signingTime" win-verify="required" placeholder="请输入签约日期" class="layui-input" value="{{signingTime}}"/>
	    </div>
	</div>
	<div class="layui-form-item layui-col-xs6">
	    <label class="layui-form-label">生效日期</label>
	    <div class="layui-input-block">
	        <input type="text" id="effectTime" name="effectTime" placeholder="请输入生效日期" class="layui-input" value="{{effectTime}}"/>
	    </div>
	</div>
	<div class="layui-form-item layui-col-xs6">
	    <label class="layui-form-label">服务期至</label>
	    <div class="layui-input-block">
	        <input type="text" id="serviceEndTime" name="serviceEndTime" placeholder="请输入服务结束日期" class="layui-input" value="{{serviceEndTime}}"/>
	    </div>
	</div>
	<div class="layui-form-item layui-col-xs6">
	    <label class="layui-form-label">联系人<i class="red">*</i></label>
	    <div class="layui-input-block">
	        <input type="text" id="contacts" name="contacts" win-verify="required" placeholder="请输入联系人" class="layui-input" maxlength="10" value="{{contacts}}"/>
	    </div>
	</div>
	<div class="layui-form-item layui-col-xs6">
	    <label class="layui-form-label">固定电话</label>
	    <div class="layui-input-block">
	        <input type="text" id="workPhone" name="workPhone" win-verify="tel" placeholder="请输入办公电话" class="layui-input" maxlength="20" value="{{workPhone}}"/>
	    </div>
	</div>
	<div class="layui-form-item layui-col-xs6">
	    <label class="layui-form-label">移动电话</label>
	    <div class="layui-input-block">
	        <input type="text" id="mobilePhone" name="mobilePhone" win-verify="phone" placeholder="请输入移动电话" class="layui-input" maxlength="20" value="{{mobilePhone}}"/>
	    </div>
	</div>
	<div class="layui-form-item layui-col-xs6">
	    <label class="layui-form-label">邮箱</label>
	    <div class="layui-input-block">
	        <input type="text" id="email" name="email" win-verify="email" placeholder="请输入邮箱" class="layui-input" maxlength="25" value="{{email}}"/>
	    </div>
	</div>
	<div class="layui-form-item layui-col-xs6">
	    <label class="layui-form-label">QQ</label>
	    <div class="layui-input-block">
	        <input type="text" id="qq" name="qq" placeholder="请输入QQ" class="layui-input" maxlength="15" value="{{qq}}"/>
	    </div>
	</div>
	<div class="layui-form-item layui-col-xs12">
	    <label class="layui-form-label">主要技术条款</label>
	    <div class="layui-input-block">
	    	<textarea id="technicalTerms" name="technicalTerms"  placeholder="请输入主要技术条款" class="layui-textarea" style="height: 100px;" maxlength="200">{{technicalTerms}}</textarea>
	    </div>
	</div>
	<div class="layui-form-item layui-col-xs12">
	    <label class="layui-form-label">主要商务条款</label>
	    <div class="layui-input-block">
	    	<textarea id="businessTerms" name="businessTerms"  placeholder="请输入主要商务条款" class="layui-textarea" style="height: 100px;" maxlength="200">{{businessTerms}}</textarea>
	    </div>
	</div>
	<div class="layui-form-item layui-col-xs6">
	    <label class="layui-form-label">所属部门<i class="red">*</i></label>
	    <div class="layui-input-block">
			<select id="departmentId" name="departmentId" win-verify="required" lay-filter="departmentId" lay-search="">
				
			</select>
	    </div>
	</div>
	<div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">关联人员<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="relationUserId" name="relationUserId" win-verify="required" placeholder="请选择关联人员" class="layui-input"/>
		    <i class="fa fa-user-plus input-icon" id="userNameSelPeople"></i>
        </div>
    </div>
	<div class="layui-form-item layui-col-xs12">
	    <label class="layui-form-label">相关附件</label>
	    <div class="layui-input-block" id="enclosureUpload">
	    </div>
	</div>
    <div class="layui-form-item layui-col-xs12">
		<div class="layui-input-block">
			<button class="winui-btn" id="cancle"><language showName="com.skyeye.cancel"></language></button>
			<button class="winui-btn typeOne" lay-submit lay-filter="formEditBean">保存为草稿</button>
            <button class="winui-btn typeOne" lay-submit lay-filter="formSubBean">提交审批</button>
            <button class="winui-btn typeTwo" lay-submit lay-filter="save"><language showName="com.skyeye.save"></language></button>
		</div>
	</div>
{{/bean}}