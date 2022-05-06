{{#bean}}
	<div class="layui-form-item layui-col-xs12">
		<label class="layui-form-label">标题：</label>
		<div class="layui-input-block ver-center">
			{{accidentTitle}}
		</div>
	</div>
	<div class="layui-form-item layui-col-xs6">
	    <label class="layui-form-label">车牌号：</label>
	    <div class="layui-input-block ver-center">
	        {{licensePlate}}
	    </div>
	</div>
	<div class="layui-form-item layui-col-xs6">
	    <label class="layui-form-label">司机姓名：</label>
	    <div class="layui-input-block ver-center">
	        {{driver}}
	    </div>
	</div>
	<div class="layui-form-item layui-col-xs6">
	    <label class="layui-form-label">事故时间：</label>
	    <div class="layui-input-block ver-center">
	        {{accidentTime}}
	    </div>
	</div>
	<div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">事故地点：</label>
        <div class="layui-input-block ver-center">
        	{{accidentArea}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">责任认定：</label>
        <div class="layui-input-block ver-center">
        	{{confirmationResponsibility}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">维修厂家：</label>
        <div class="layui-input-block ver-center">
        	{{manufacturer}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">送修时间段：</label>
        <div class="layui-input-block ver-center">
        	{{repairTime}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">维修费用：</label>
        <div class="layui-input-block ver-center">
        	{{repairPrice}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">车损费：</label>
        <div class="layui-input-block ver-center">
        	{{lossFeePrice}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">保险理赔金额：</label>
        <div class="layui-input-block ver-center">
        	{{claimsFeePrice}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">司机承担费用：</label>
        <div class="layui-input-block ver-center">
        	{{driverBearPrice}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">事故详情：</label>
        <div class="layui-input-block ver-center">
        	{{accidentDetail}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">维修内容：</label>
        <div class="layui-input-block">
        	{{repairContent}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">相关附件：</label>
        <div class="layui-input-block ver-center" id="enclosureUploadBtn">
        </div>
    </div>
{{/bean}}