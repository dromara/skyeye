{{#bean}}
	<div class="layui-form-item layui-col-xs12">
		<span class="hr-title">基础信息</span><hr>
	</div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">真实姓名</label>
        <div class="layui-input-block ver-center">
        	{{userName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">登陆账号</label>
        <div class="layui-input-block ver-center">
        	{{userCode}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">职位</label>
        <div class="layui-input-block ver-center">
        	{{companyName}}  {{departmentName}}  {{jobName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">身份证件</label>
        <div class="layui-input-block">
        	<input type="text" id="userIdCard" name="userIdCard" placeholder="请输入身份证件" class="layui-input" maxlength="18"  value="{{userIdCard}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">性别<i class="red">*</i></label>
       	<div class="layui-input-block winui-radio">
            <input type="radio" name="userSex" value="0" title="保密"/>
            <input type="radio" name="userSex" value="1" title="男" />
            <input type="radio" name="userSex" value="2" title="女" />
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">个人头像<i class="red">*</i></label>
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
        	<input type="text" id="userEmail" name="userEmail" win-verify="email" placeholder="请输入邮箱地址" class="layui-input" maxlength="50" value="{{userEmail}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">手机号</label>
        <div class="layui-input-block">
        	<input type="text" id="userPhone" name="userPhone" win-verify="phone" placeholder="请输入手机号" class="layui-input" maxlength="11" value="{{userPhone}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">家庭电话</label>
        <div class="layui-input-block">
        	<input type="text" id="userHomePhone" name="userHomePhone" placeholder="请输入家庭号码" class="layui-input" maxlength="20" value="{{userHomePhone}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">QQ</label>
        <div class="layui-input-block">
        	<input type="text" id="userQq" name="userQq" placeholder="请输入qq" class="layui-input" maxlength="15" value="{{userQq}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
		<span class="hr-title">个性签名</span><hr>
	</div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">签名</label>
        <div class="layui-input-block">
        	<textarea id="userSign" name="userSign"  placeholder="请输入签名" class="layui-textarea" style="height: 100px;" maxlength="200">{{userSign}}</textarea>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle">关闭</button>
            <button class="winui-btn" lay-submit lay-filter="formAddBean"><language showName="com.skyeye.save"></language></button>
        </div>
    </div>
{{/bean}}