{{#bean}}
    <div class="layui-form-item">
        <label class="layui-form-label">分类<i class="red">*</i></label>
        <div class="layui-input-block">
        	{{typeName}}
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">分组<i class="red">*</i></label>
        <div class="layui-input-block">
        	{{groupName}}
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">HTML内容<i class="red">*</i></label>
        <div class="layui-input-block">
        	{{htmlContent}}
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">HTML-JS内容</label>
        <div class="layui-input-block">
        	{{htmlJsContent}}
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">预览图<i class="red">*</i></label>
        <div class="layui-col-xs6">
            <div class="layui-col-xs10" id="images">
                {{#compare1 printsPicUrl}}{{/compare1}}
            </div>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">WXML内容<i class="red">*</i></label>
        <div class="layui-input-block">
        	<textarea id="wxmlContent" name="wxmlContent" win-verify="required" placeholder="请填写代码块" class="layui-textarea" style="height: 100px;">{{wxmlContent}}</textarea>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">WXML-JS内容</label>
        <div class="layui-input-block">
        	<textarea id="wxmlJsContent" name="wxmlJsContent" placeholder="请填写代码块" class="layui-textarea" style="height: 100px;">{{wxmlJsContent}}</textarea>
        </div>
    </div>
    <div class="layui-form-item">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle">取消</button>
            <button class="winui-btn" lay-submit lay-filter="formEditBean">保存</button>
        </div>
    </div>
{{/bean}}