{{#bean}}
    <div class="layui-form-item">
        <label class="layui-form-label">分组名称<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="groupName" name="groupName" win-verify="required" placeholder="请输入模块分组名" class="layui-input" maxlength="20" value="{{groupName}}"/>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">分组描述</label>
        <div class="layui-input-block">
        	<textarea id="groupDesc" name="groupDesc" style="display: none;">{{groupDesc}}</textarea>
        </div>
    </div>
    <div class="layui-form-item">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle">取消</button>
            <button class="winui-btn" lay-submit lay-filter="formEditBean">保存</button>
        </div>
    </div>
{{/bean}}