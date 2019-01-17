{{#bean}}
    <div class="layui-form-item">
        <label class="layui-form-label">分类<i class="red">*</i></label>
        <div class="layui-input-block">
        	<select id="rmTypeId" name="rmTypeId" class="layui-input" lay-filter="selectParent" win-verify="required">
                
            </select>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">分组名称<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="rmGroupName" name="rmGroupName" win-verify="required" placeholder="请输入小程序分组名" class="layui-input" maxlength="20" value="{{rmGroupName}}"/>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">分组ICON<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="rmGroupIcon" name="rmGroupIcon" win-verify="required" placeholder="请选择小程序分组ICON" class="layui-input" maxlength="20" value="{{icon}}"/>
        </div>
    </div>
    <div class="layui-form-item">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle">取消</button>
            <button class="winui-btn" lay-submit lay-filter="formEditBean">保存</button>
        </div>
    </div>
{{/bean}}