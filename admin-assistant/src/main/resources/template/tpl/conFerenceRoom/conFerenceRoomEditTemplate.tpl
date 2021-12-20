{{#bean}}
	<div class="layui-form-item">
        <label class="layui-form-label">名称<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="roomName" name="roomName" win-verify="required" placeholder="请输入会议室名称" class="layui-input" value="{{roomName}}"/>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">图片<i class="red">*</i></label>
        <div class="layui-input-block">
        	<div class="upload" id="roomImg"></div>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">容量<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="roomCapacity" name="roomCapacity" win-verify="required|number" placeholder="请输入会议室可容纳人数,该输入框只能输入数字" class="layui-input" value="{{roomCapacity}}"/>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">位置</label>
        <div class="layui-input-block">
            <input type="text" id="roomPosition" name="roomPosition" win-verify="" placeholder="请输入会议室位置" class="layui-input" value="{{roomPosition}}"/>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">内部设备</label>
        <div class="layui-input-block">
        	<input type="text" id="roomEquipment" name="roomEquipment" placeholder="请输入会议室内部设备" class="layui-input" value="{{roomEquipment}}"/>
        	<div class="layui-form-mid layui-word-aux">例如：投影仪、白板、录影设备等</div>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">管理人</label>
        <div class="layui-input-block">
        	<input type="text" id="roomAdmin" name="roomAdmin" placeholder="请选择会议室管理人" class="layui-input"/>
		    <i class="fa fa-user-plus input-icon" id="userNameSelPeople"></i>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">相关描述</label>
        <div class="layui-input-block">
        	<textarea id="roomAddDesc" name="roomAddDesc"  placeholder="请输入会议室相关描述" class="layui-textarea" style="height: 100px;" maxlength="200">{{roomAddDesc}}</textarea>
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