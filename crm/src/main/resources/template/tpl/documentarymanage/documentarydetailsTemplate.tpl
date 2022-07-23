{{#bean}}
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">商机：</label>
        <div class="layui-input-block ver-center">
            {{opportunityName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">类型：</label>
        <div class="layui-input-block ver-center">
            {{typeName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">跟单时间：</label>
        <div class="layui-input-block ver-center">
            {{documentaryTime}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">详细内容：</label>
        <div class="layui-input-block ver-center">
            {{detail}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">相关附件：</label>
        <div class="layui-input-block ver-center" id="enclosureUploadBtn">
        </div>
    </div>
{{/bean}}