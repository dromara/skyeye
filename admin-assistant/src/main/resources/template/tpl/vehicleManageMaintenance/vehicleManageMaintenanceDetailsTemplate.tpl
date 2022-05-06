{{#bean}}
	<div class="layui-form-item layui-col-xs12">
		<label class="layui-form-label">主题：</label>
		<div class="layui-input-block ver-center">
			{{maintenanceTitle}}
		</div>
	</div>
	<div class="layui-form-item layui-col-xs6">
	    <label class="layui-form-label">车牌：</label>
	    <div class="layui-input-block ver-center">
	        {{licensePlate}}
	    </div>
	</div>
	<div class="layui-form-item layui-col-xs6">
	    <label class="layui-form-label">类型：</label>
	    <div class="layui-input-block ver-center {{colorClass}}">
	        {{maintenanceType}}
	    </div>
	</div>
	<div class="layui-form-item layui-col-xs6">
	    <label class="layui-form-label">维修保养公司：</label>
	    <div class="layui-input-block ver-center">
	        {{maintenanceCompany}}
	    </div>
	</div>
	<div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">费用：</label>
        <div class="layui-input-block ver-center">
        	{{maintenancePrice}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">时间段：</label>
        <div class="layui-input-block ver-center">
        	{{maintenanceTime}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">具体内容：</label>
        <div class="layui-input-block ver-center">
        	{{concreteContent}}
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