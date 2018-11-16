{{#bean}}
    <div class="layui-form-item">
        <label class="layui-form-label">模板标题<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="templateName" name="templateName" win-verify="required" placeholder="请输入模板标题" class="layui-input" maxlength="20" value="{{templateName}}"/>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">模板内容<i class="red">*</i></label>
        <div class="layui-input-block">
        	<textarea id="templateContent" name="templateContent">{{templateContent}}</textarea>
        </div>
    </div>
    <div class="layui-form-item">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle">取消</button>
            <button class="winui-btn" lay-submit lay-filter="formEditBean">保存</button>
        </div>
    </div>
{{/bean}}