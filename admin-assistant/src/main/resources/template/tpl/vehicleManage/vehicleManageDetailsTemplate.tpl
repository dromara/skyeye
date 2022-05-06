{{#bean}}
	<div class="layui-form-item layui-col-xs12">
		<label class="layui-form-label">所属公司：</label>
		<div class="layui-input-block ver-center">
			{{vehicleCompany}}
		</div>
	</div>
	<div class="layui-form-item layui-col-xs12">
		<label class="layui-form-label">名称：</label>
		<div class="layui-input-block ver-center">
			{{vehicleName}}
		</div>
	</div>
	<div class="layui-form-item layui-col-xs6">
	    <label class="layui-form-label">状态：</label>
	    <div class="layui-input-block ver-center {{colorClass}}">
	        {{state}}
	    </div>
	</div>
	<div class="layui-form-item layui-col-xs12">
		<label class="layui-form-label">图片：</label>
		<div class="layui-input-block ver-center">
			<img src="" class="photo-img" id="vehicleImg">
		</div>
	</div>
	<div class="layui-form-item layui-col-xs6">
	    <label class="layui-form-label">车牌：</label>
	    <div class="layui-input-block ver-center">
	        {{licensePlate}}
	    </div>
	</div>
	<div class="layui-form-item layui-col-xs6">
	    <label class="layui-form-label">车辆规格：</label>
	    <div class="layui-input-block ver-center">
	        {{specifications}}
	    </div>
	</div>
	<div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">油耗：</label>
        <div class="layui-input-block ver-center">
        	{{oilConsumption}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">单价：</label>
        <div class="layui-input-block ver-center">
        	{{unitPrice}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">车辆颜色：</label>
        <div class="layui-input-block ver-center">
        	{{vehicleColor}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">生产商：</label>
        <div class="layui-input-block ver-center">
        	{{manufacturer}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">供应商：</label>
        <div class="layui-input-block ver-center">
        	{{supplier}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">生产日期：</label>
        <div class="layui-input-block ver-center">
        	{{manufactureTime}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">采购日期：</label>
        <div class="layui-input-block ver-center">
        	{{purchaseTime}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">发动机号：</label>
        <div class="layui-input-block ver-center">
        	{{engineNumber}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">车架号：</label>
        <div class="layui-input-block ver-center">
        	{{frameNumber}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">存放区域：</label>
        <div class="layui-input-block ver-center">
            {{storageArea}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">管理人：</label>
        <div class="layui-input-block ver-center">
        	{{vehicleAdmin}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">下次年检时间：</label>
        <div class="layui-input-block ver-center">
        	{{nextInspectionTime}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">保险截止期限：</label>
        <div class="layui-input-block ver-center">
        	{{insuranceDeadline}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">上次保养日期：</label>
        <div class="layui-input-block ver-center">
        	{{prevMaintainTime}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">附加描述：</label>
        <div class="layui-input-block ver-center">
        	{{roomAddDesc}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">相关附件：</label>
        <div class="layui-input-block ver-center" id="enclosureUpload">

        </div>
    </div>
{{/bean}}