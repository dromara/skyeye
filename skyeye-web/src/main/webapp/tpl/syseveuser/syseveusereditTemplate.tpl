{{#bean}}
	<div class="layui-form-item">
        <label class="layui-form-label">账号<i class="red">*</i></label>
        <div class="layui-input-block ver-center">
        	{{userCode}}
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">用户名</label>
        <div class="layui-input-block">
            <input type="text" id="userName" name="userName" win-verify="required" placeholder="请输入用户名" class="layui-input" value="{{userName}}"/>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">身份证</label>
        <div class="layui-input-block">
            <input type="text" id="userIdCard" name="userIdCard" placeholder="请输入身份证" class="layui-input" value="{{userIdCard}}"/>
        </div>
    </div>
    <div class="layui-form-item" pane>
        <label class="layui-form-label">性别</label>
        <div class="layui-input-block winui-radio">
            <input type="radio" name="userSex" value="0" title="保密"/>
            <input type="radio" name="userSex" value="1" title="男" />
            <input type="radio" name="userSex" value="2" title="女" />
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">头像</label>
        <div class="layui-input-block">
        	<div class="upload" id="userPhoto"></div>
        </div>
    </div>
    <div class="layui-form-item">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle">取消</button>
            <button class="winui-btn" lay-submit lay-filter="formEditBean">保存</button>
        </div>
    </div>
{{/bean}}