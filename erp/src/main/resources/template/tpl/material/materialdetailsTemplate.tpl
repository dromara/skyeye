{{#bean}}
	<div class="layui-form-item layui-col-xs12">
		<span class="hr-title">基础信息</span><hr>
	</div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">商品名称：</label>
        <div class="layui-input-block ver-center">
        	{{materialName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">型号：</label>
        <div class="layui-input-block ver-center">
            {{model}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">所属类型：</label>
        <div class="layui-input-block ver-center">
            {{categoryName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">商品来源：</label>
        <div class="layui-input-block ver-center">
            {{typeName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">工序资料：</label>
        <div class="layui-input-block ver-center" id="procedureShowBox">
        	{{#each procedureMationList}}
        		<span class="layui-badge layui-bg-blue" style="height: 25px !important; line-height: 25px !important; margin: 5px 0px;">{{procedureName}}
					<span class="layui-badge layui-bg-gray">{{number}}</span>
				</span>
				<br>
        	{{/each}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">备注：</label>
        <div class="layui-input-block ver-center">
        	{{remark}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">附件资料：</label>
        <div class="layui-input-block ver-center" id="enclosureUploadBtn">
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
		<span class="hr-title">价格信息</span><hr>
	</div>
	<div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">单位类型：</label>
        <div class="layui-input-block ver-center">
            {{unitType}}
        </div>
    </div>
{{/bean}}