{{#bean}}
	<div class="layui-form-item layui-col-xs12">
		<span class="hr-title">基础信息</span><hr>
	</div>
	<div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">账号<i class="red">*</i></label>
        <div class="layui-input-block ver-center">
        	{{userCode}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">入职时间<i class="red">*</i></label>
        <div class="layui-input-block ver-center">
            {{entryTime}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">有效期<i class="red">*</i></label>
        <div class="layui-input-block winui-radio">
            <input type="radio" name="isTermOfValidity" value="1" title="长期有效"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">员工姓名<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="userName" name="userName" win-verify="required" placeholder="请输入真实姓名" class="layui-input" value="{{userName}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">身份证<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="userIdCard" name="userIdCard" win-verify="required|identity" placeholder="请输入身份证" class="layui-input" value="{{userIdCard}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">性别<i class="red">*</i></label>
        <div class="layui-input-block winui-radio">
            <input type="radio" name="userSex" value="0" title="保密"/>
            <input type="radio" name="userSex" value="1" title="男" />
            <input type="radio" name="userSex" value="2" title="女" />
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">头像<i class="red">*</i></label>
        <div class="layui-input-block">
        	<div class="upload" id="userPhoto"></div>
        </div>
    </div>
     <div class="layui-form-item layui-col-xs12">
		<span class="hr-title">联系方式</span><hr>
	</div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">邮箱</label>
        <div class="layui-input-block">
        	<input type="text" id="userEmail" name="userEmail" placeholder="请输入邮箱" class="layui-input" maxlength="50" value="{{userEmail}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">QQ</label>
        <div class="layui-input-block">
        	<input type="text" id="userQq" name="userQq" placeholder="请输入qq" class="layui-input" maxlength="15" value="{{userQq}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">手机号</label>
        <div class="layui-input-block">
        	<input type="text" id="userPhone" name="userPhone" placeholder="请输入手机号" class="layui-input" maxlength="11" value="{{userPhone}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">家庭电话</label>
        <div class="layui-input-block">
        	<input type="text" id="userHomePhone" name="userHomePhone" placeholder="请输入家庭号码" class="layui-input" maxlength="20" value="{{userHomePhone}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
		<span class="hr-title">组织机构</span><hr>
	</div>
	<div class="layui-form-item layui-col-xs12">
		<label class="layui-form-label">职位信息<i class="red">*</i></label>
        <div class="layui-input-block">
            <div class="layui-form-item layui-col-xs4">
            	<ul id="demoTree1" class="dtree" data-id="0" style="overflow-y: auto;height: 250px;"></ul>
            </div>
            <div class="layui-form-item layui-col-xs4">
            	<ul id="demoTree2" class="dtree" data-id="0" style="overflow-y: auto;height: 250px;"></ul>
            </div>
            <div class="layui-form-item layui-col-xs4">
            	<ul id="demoTree3" class="dtree" data-id="0" style="overflow-y: auto;height: 250px;"></ul>
            </div>
        </div>
	</div>
    <div class="layui-form-item layui-col-xs12">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle"><language showName="com.skyeye.cancel"></language></button>
            <button class="winui-btn" lay-submit lay-filter="formEditBean"><language showName="com.skyeye.save"></language></button>
        </div>
    </div>
{{/bean}}