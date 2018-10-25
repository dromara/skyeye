{{#bean}}
    <div class="layui-form-item">
        <label class="layui-form-label">项目名<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="proName" name="proName" win-verify="required" placeholder="请输入项目名" class="layui-input" maxlength="20" value="{{proName}}"/>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">项目简介</label>
        <div class="layui-input-block">
        	<textarea id="proDesc" name="proDesc"  placeholder="请输入项目简介" class="layui-textarea" style="height: 100px;">{{proDesc}}</textarea>
        </div>
    </div>
    <div class="layui-form-item">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle">取消</button>
            <button class="winui-btn" lay-submit lay-filter="formEditBean">保存</button>
        </div>
    </div>
{{/bean}}