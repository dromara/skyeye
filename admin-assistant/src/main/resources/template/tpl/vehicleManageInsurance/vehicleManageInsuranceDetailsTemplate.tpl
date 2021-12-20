{{#bean}}
	<div class="layui-form-item layui-col-xs12">
		<label class="layui-form-label">标题：</label>
		<div class="layui-input-block ver-center">
			{{insuranceTitle}}
		</div>
	</div>
	<div class="layui-form-item layui-col-xs6">
	    <label class="layui-form-label">车牌号：</label>
	    <div class="layui-input-block ver-center">
	        {{licensePlate}}
	    </div>
	</div>
	<div class="layui-form-item layui-col-xs6">
	    <label class="layui-form-label">投保公司：</label>
	    <div class="layui-input-block ver-center">
	        {{insuranceCompany}}
	    </div>
	</div>
	<table class="layui-table">
        <thead>
            <tr>
                <th>险种</th>
                <th>保费</th>
                <th>保额</th>
                <th>备注</th>
            </tr>
        </thead>
        <tbody id="addTable" class="insurance-table">
        </tbody>
	</table>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">投保总费用：</label>
        <div class="layui-input-block ver-center">
        	{{insuranceAllPrice}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
	    <label class="layui-form-label">投保电话：</label>
	    <div class="layui-input-block ver-center">
	        {{insuredTelephone}}
	    </div>
	</div>
	<div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">投保有效期：</label>
        <div class="layui-input-block ver-center">
        	{{validityTime}}
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
        <div class="layui-input-block ver-center" id="enclosureUploadBtn">
        </div>
    </div>
{{/bean}}