{{#bean}}
	<div class="layui-form-item layui-col-xs12">
		<label class="layui-form-label">帖子标题：</label>
		<div class="layui-input-block ver-center">
			{{title}}
		</div>
	</div>
	<div class="layui-form-item layui-col-xs12">
	    <label class="layui-form-label">举报类型：</label>
	    <div class="layui-input-block ver-center">
	        {{reportType}}
	    </div>
	</div>
	<div class="reportcontent layui-form-item layui-col-xs12">
	    <label class="layui-form-label">举报内容：</label>
	    <div class="layui-input-block ver-center">
	        {{reportContent}}
	    </div>
	</div>
	<div class="layui-form-item layui-col-xs12">
	    <label class="layui-form-label">备注：</label>
	    <div class="layui-input-block ver-center">
	        {{reportDesc}}
	    </div>
	</div>
	<div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">举报人：</label>
        <div class="layui-input-block ver-center">
        	{{reportUser}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">举报时间：</label>
        <div class="layui-input-block ver-center">
        	{{reportTime}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">审核状态：</label>
        <div class="layui-input-block ver-center">
        	{{examineState}}
        </div>
    </div>
    <div class="nopass examine layui-form-item layui-col-xs12">
        <label class="layui-form-label">审核不通过原因：</label>
        <div class="layui-input-block ver-center">
        	{{examineNopassReason}}
        </div>
    </div>
    <div class="examine layui-form-item layui-col-xs12">
        <label class="layui-form-label">审核人：</label>
        <div class="layui-input-block ver-center">
        	{{examineUser}}
        </div>
    </div>
    <div class="examine layui-form-item layui-col-xs12">
        <label class="layui-form-label">审核时间：</label>
        <div class="layui-input-block ver-center">
        	{{examineTime}}
        </div>
    </div>
{{/bean}}