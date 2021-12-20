{{#bean}}
	<div class="layui-form-item layui-col-xs12">
		<label class="layui-form-label">标题：</label>
		<div class="layui-input-block ver-center">
			{{inspectionTitle}}
		</div>
	</div>
	<div class="layui-form-item layui-col-xs6">
	    <label class="layui-form-label">车牌号：</label>
	    <div class="layui-input-block ver-center">
	        {{licensePlate}}
	    </div>
	</div>
	<div class="layui-form-item layui-col-xs6">
	    <label class="layui-form-label">年检地点：</label>
	    <div class="layui-input-block ver-center">
	        {{inspectionArea}}
	    </div>
	</div>
	<div class="layui-form-item layui-col-xs6">
	    <label class="layui-form-label">联系电话：</label>
	    <div class="layui-input-block ver-center">
	        {{contactInformation}}
	    </div>
	</div>
	<div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">年检费用：</label>
        <div class="layui-input-block ver-center">
        	{{inspectionPrice}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">本次年检时间：</label>
        <div class="layui-input-block ver-center">
        	{{thisInspectionTime}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">下次年检时间：</label>
        <div class="layui-input-block ver-center">
        	{{nextInspectionTime}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">相关描述：</label>
        <div class="layui-input-block ver-center">
        	{{roomAddDesc}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">相关附件：</label>
        <div class="layui-input-block ver-center" id="enclosureUploadBtn">
        </div>
    </div>
{{/bean}}