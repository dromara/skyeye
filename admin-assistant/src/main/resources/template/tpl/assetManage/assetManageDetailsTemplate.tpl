{{#bean}}
	<div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">所属公司：</label>
        <div class="layui-input-block ver-center">
            {{companyName}}
        </div>
    </div>
	<div class="layui-form-item layui-col-xs12">
		<label class="layui-form-label">资产名称：</label>
		<div class="layui-input-block ver-center">
			{{assetName}}
		</div>
	</div>
	<div class="layui-form-item layui-col-xs12">
		<label class="layui-form-label">图片：</label>
		<div class="layui-input-block ver-center">
			<img src="" class="photo-img" id="assetImg">
		</div>
	</div>
	<div class="layui-form-item layui-col-xs6">
	    <label class="layui-form-label">资产类型：</label>
	    <div class="layui-input-block ver-center">
	        {{typeId}}
	    </div>
	</div>
	<div class="layui-form-item layui-col-xs6">
	    <label class="layui-form-label">资产来源：</label>
	    <div class="layui-input-block ver-center">
	        {{fromId}}
	    </div>
	</div>
	<div class="layui-form-item layui-col-xs6">
	    <label class="layui-form-label">资产编号：</label>
	    <div class="layui-input-block ver-center">
	        {{assetNum}}
	    </div>
	</div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">资产规格：</label>
        <div class="layui-input-block ver-center">
            {{specifications}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">资产单价：</label>
        <div class="layui-input-block ver-center">
        	{{unitPrice}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">生产商：</label>
        <div class="layui-input-block ver-center">
        	{{manufacturer}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">生产日期：</label>
        <div class="layui-input-block ver-center">
        	{{manufacturerTime}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">供应商：</label>
        <div class="layui-input-block ver-center">
        	{{supplier}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">采购日期：</label>
        <div class="layui-input-block ver-center">
        	{{purchaseTime}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">存放区域：</label>
        <div class="layui-input-block ver-center">
        	{{storageArea}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">领用人：</label>
        <div class="layui-input-block ver-center">
        	{{employeeId}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">管理人：</label>
        <div class="layui-input-block ver-center">
        	{{assetAdmin}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">相关描述：</label>
        <div class="layui-input-block ver-center">
        	{{roomAddDesc}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">相关附件：</label>
        <div class="layui-input-block ver-center" id="enclosureUpload">

        </div>
    </div>
{{/bean}}