<!-- 员工信息 -->
{{#bean}}
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">工号：</label>
        <div class="layui-input-block ver-center">
            {{jobNumber}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">姓名：</label>
        <div class="layui-input-block ver-center">
            {{userName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">所属企业：</label>
        <div class="layui-input-block ver-center">
            {{companyName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">所属部门：</label>
        <div class="layui-input-block ver-center">
            {{departmentName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">所在岗位：</label>
        <div class="layui-input-block ver-center">
            {{jobName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">岗位定级：</label>
        <div class="layui-input-block ver-center">
            {{jobScoreName}}
        </div>
    </div>
{{/bean}}