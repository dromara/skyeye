{{#bean}}
	<div class="layui-form-item layui-col-xs12">
		<label class="layui-form-label">标题：</label>
		<div class="layui-input-block ver-center">
			{{oilTitle}}
		</div>
	</div>
	<div class="layui-form-item layui-col-xs6">
	    <label class="layui-form-label">车牌号：</label>
	    <div class="layui-input-block ver-center">
	        {{licensePlate}}
	    </div>
	</div>
	<div class="layui-form-item layui-col-xs6">
	    <label class="layui-form-label">加油日期：</label>
	    <div class="layui-input-block ver-center">
	        {{oilTime}}
	    </div>
	</div>
	<div class="layui-form-item layui-col-xs6">
	    <label class="layui-form-label">加油容量：</label>
	    <div class="layui-input-block ver-center">
	        {{oilCapacity}}
	    </div>
	</div>
	<div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">加油金额：</label>
        <div class="layui-input-block ver-center">
        	{{oilPrice}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">备注：</label>
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