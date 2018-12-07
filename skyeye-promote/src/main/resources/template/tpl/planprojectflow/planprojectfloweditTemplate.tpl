{{#bean}}
    <div class="layui-form-item">
        <label class="layui-form-label">所属项目</label>
        <div class="layui-input-block ver-center" id="projectName">
        	
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">名称<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="title" name="title" win-verify="required" placeholder="请输入名称" class="layui-input" maxlength="50" value="{{title}}"/>
        	<div class="layui-form-mid layui-word-aux">该名称为目录名称或者流程图名称</div>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">是否共享<i class="red">*</i></label>
        <div class="layui-input-block winui-switch">
            <input id="isShare" name="isShare" lay-filter="isShare" type="checkbox" lay-skin="switch" lay-text="是|否" {{#compare2 isShare}}{{/compare2}} value="{{#compare3 isShare}}{{/compare3}}" />
        </div>
    </div>
    <div class="layui-form-item">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle">取消</button>
            <button class="winui-btn" lay-submit lay-filter="formEditBean">保存</button>
        </div>
    </div>
{{/bean}}