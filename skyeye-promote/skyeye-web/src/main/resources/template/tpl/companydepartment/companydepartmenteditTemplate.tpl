{{#bean}}
    <div class="layui-form-item">
        <label class="layui-form-label">部门名称<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="departmentName" name="departmentName" win-verify="required" placeholder="请输入部门名称" class="layui-input" maxlength="50" value="{{departmentName}}"/>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">部门简介</label>
        <div class="layui-input-block">
        	<textarea id="content" name="content" style="display: none;">{{departmentDesc}}</textarea>
        </div>
    </div>
    <div class="layui-form-item">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle">取消</button>
            <button class="winui-btn" lay-submit lay-filter="formEditBean">保存</button>
        </div>
    </div>
{{/bean}}