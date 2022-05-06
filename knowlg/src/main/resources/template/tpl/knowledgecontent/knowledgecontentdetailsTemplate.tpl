{{#bean}}
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">标题：</label>
        <div class="layui-input-block ver-center">
        	{{title}}
        </div>
    </div>
	<div class="layui-form-item layui-col-xs6">
	    <label class="layui-form-label">所属分类：</label>
        <div class="layui-input-block ver-center">
            {{typeName}}
        </div>
	</div>
	<div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">标签：</label>
        <div class="layui-input-block ver-center" id="label">
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">状态：</label>
        <div class="layui-input-block ver-center {{colorClass}}">
            {{state}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">创建时间：</label>
        <div class="layui-input-block ver-center">
            {{createTime}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">内容：</label>
        <div class="layui-input-block ver-center">
			<iframe id="knowledgecontentshowBox" style="width: 100%; border: 0px;" scrolling="no"></iframe>
		</div>
    </div>
{{/bean}}