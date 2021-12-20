{{#bean}}
	<div class="layui-form-item">
        <label class="layui-form-label">印章名称<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="sealName" name="sealName" win-verify="required" placeholder="请输入印章名称" class="layui-input" value="{{sealName}}"/>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">启用日期<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="enableTime" name="enableTime" win-verify="required" placeholder="请选择启用日期" class="layui-input" value="{{enableTime}}"/>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">管理人<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="sealAdmin" name="sealAdmin" placeholder="请选择管理人" class="layui-input" value="{{sealAdmin}}"/>
		    <i class="fa fa-user-plus input-icon" id="userNameSelPeople"></i>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">借用人</label>
        <div class="layui-input-block">
        	<input type="text" id="borrowId" name="borrowId" placeholder="请选择借用人" class="layui-input" value="{{borrowId}}"/>
		    <i class="fa fa-user-plus input-icon" id="borrowPeople"></i>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">相关描述</label>
        <div class="layui-input-block">
        	<textarea id="roomAddDesc" name="roomAddDesc"  placeholder="请输入相关描述" class="layui-textarea" style="height: 100px;" maxlength="200">{{roomAddDesc}}</textarea>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">相关附件</label>
        <div class="layui-input-block" id="enclosureUpload">
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle"><language showName="com.skyeye.cancel"></language></button>
            <button class="winui-btn" lay-submit lay-filter="formEditBean"><language showName="com.skyeye.save"></language></button>
        </div>
    </div>
{{/bean}}