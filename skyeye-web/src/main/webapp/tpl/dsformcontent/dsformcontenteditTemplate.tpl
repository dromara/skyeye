{{#bean}}
    <div class="layui-form-item">
        <label class="layui-form-label">模板标题<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="contentName" name="contentName" win-verify="required" placeholder="请输入模板标题" class="layui-input" maxlength="20" value="{{contentName}}"/>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">HTML内容类型<i class="red">*</i></label>
        <div class="layui-input-block">
        	<select id="htmlType" name="htmlType" class="layui-input" win-verify="required" lay-filter="selectParent">
                <option value="html">html</option>
            </select>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">HTML模板内容<i class="red">*</i></label>
        <div class="layui-input-block">
        	<textarea id="htmlContent">{{htmlContent}}</textarea>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">JS内容类型</label>
        <div class="layui-input-block">
        	<select id="jsType" name="jsType" class="layui-input" win-verify="required" lay-filter="selectParent">
                <option value="javascript">javascript</option>
            </select>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">JS模板内容</label>
        <div class="layui-input-block">
        	<textarea id="jsContent">{{jsContent}}</textarea>
        </div>
    </div>
    <div class="layui-form-item">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle">取消</button>
            <button class="winui-btn" lay-submit lay-filter="formEditBean">保存</button>
        </div>
    </div>
{{/bean}}