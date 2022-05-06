{{#bean}}
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">公告标题：</label>
        <div class="layui-input-block ver-center">
        	{{title}}
        </div>
    </div>
	<div class="layui-form-item layui-col-xs6">
	    <label class="layui-form-label">一级分类：</label>
        <div class="layui-input-block ver-center">
            {{typeName}}
        </div>
	</div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">二级分类：</label>
        <div class="layui-input-block ver-center">
        	{{secondTypeName}}
        </div>
    </div>
	<div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">群发类型：</label>
        <div class="layui-input-block ver-center">
            {{sendType}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12" id="sendTo">
        <label class="layui-form-label">人员选择：</label>
        <div class="layui-input-block ver-center">
            {{userName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">定时通知：</label>
        <div class="layui-input-block ver-center">
            {{timeSend}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12" id="sendTime">
        <label class="layui-form-label">通知时间：</label>
        <div class="layui-input-block ver-center">
        	{{delayedTime}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">邮件通知：</label>
        <div class="layui-input-block ver-center">
            {{whetherEmail}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">当前状态：</label>
        <div class="layui-input-block ver-center {{colorClass}}">
            {{state}}
        </div>
    </div>
    <div id="stateUp">
    	<div class="layui-form-item layui-col-xs12">
	        <label class="layui-form-label">上线类型：</label>
	        <div class="layui-input-block ver-center">
	            {{realLinesType}}
	        </div>
	    </div>
	    <div class="layui-form-item layui-col-xs12" id="sendTime">
	        <label class="layui-form-label">上线时间：</label>
	        <div class="layui-input-block ver-center">
	        	{{realLinesTime}}
	        </div>
	    </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">创建人：</label>
        <div class="layui-input-block ver-center">
            {{createName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">创建时间：</label>
        <div class="layui-input-block ver-center">
            {{createTime}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">公告内容：</label>
        <div class="layui-input-block ver-center">
			{{{content}}}
		</div>
    </div>
{{/bean}}