{{#bean}}
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">工序名称：</label>
        <div class="layui-input-block ver-center">
            {{name}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">工序编号：</label>
        <div class="layui-input-block ver-center">
            {{number}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">参考单价：</label>
        <div class="layui-input-block ver-center">
            {{unitPrice}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">部门：</label>
        <div class="layui-input-block ver-center">
            {{departmentName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">工序类别：</label>
        <div class="layui-input-block ver-center">
            {{typeName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">工序内容：</label>
        <div class="layui-input-block ver-center">
            {{content}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">工序操作员：</label>
        <div class="layui-input-block ver-center">
            {{#each operators}}
                {{name}}，
            {{/each}}
        </div>
    </div>
{{/bean}}