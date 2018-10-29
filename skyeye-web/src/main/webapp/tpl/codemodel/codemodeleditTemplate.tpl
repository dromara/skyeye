{{#bean}}
    <div class="layui-form-item">
        <label class="layui-form-label">模板别名<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="modelName" name="modelName" win-verify="required" placeholder="请输入模板别名" class="layui-input" maxlength="20" value="{{modelName}}"/>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">模板内容<i class="red">*</i></label>
        <div class="layui-input-block">
        	<textarea id="modelContent" name="modelContent" class="layui-input">{{modelContent}}</textarea>
        </div>
    </div>
    <div class="layui-form-item">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle">取消</button>
            <button class="winui-btn" lay-submit lay-filter="formEditBean">保存</button>
        </div>
    </div>
{{/bean}}