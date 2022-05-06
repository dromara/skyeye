{{#bean}}
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">主题：</label>
        <div class="layui-input-block ver-center">
        	{{title}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">类别：</label>
        <div class="layui-input-block ver-center">
        	{{typeName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12" id="sendTime">
        <label class="layui-form-label">发送时间：</label>
        <div class="layui-input-block ver-center">
        	{{linesTime}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">发送人：</label>
        <div class="layui-input-block ver-center">
            {{createName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">内容：</label>
        <div class="layui-input-block ver-center">
			{{{content}}}
		</div>
    </div>
{{/bean}}