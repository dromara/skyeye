{{#bean}}
	<div class="layui-form-item">
		<label class="layui-form-label">名称：</label>
		<div class="layui-input-block ver-center">
			{{articlesName}}
		</div>
	</div>
	<div class="layui-form-item">
		<label class="layui-form-label">类别：</label>
		<div class="layui-input-block ver-center">
			{{typeName}}
		</div>
	</div>
	<div class="layui-form-item">
	    <label class="layui-form-label">编号：</label>
	    <div class="layui-input-block ver-center">
	        {{articlesNum}}
	    </div>
	</div>
	<div class="layui-form-item">
	    <label class="layui-form-label">规格：</label>
	    <div class="layui-input-block ver-center">
	        {{specifications}}
	    </div>
	</div>
	<div class="layui-form-item">
	    <label class="layui-form-label">计量单位：</label>
	    <div class="layui-input-block ver-center">
	        {{unitOfMeasurement}}
	    </div>
	</div>
	<div class="layui-form-item">
	    <label class="layui-form-label">初始化数量：</label>
	    <div class="layui-input-block ver-center">
	        {{initialNum}}
	    </div>
	</div>
	<div class="layui-form-item">
	    <label class="layui-form-label">当前数量：</label>
	    <div class="layui-input-block ver-center">
	        {{residualNum}}
	    </div>
	</div>
	<div class="layui-form-item">
	    <label class="layui-form-label">存放区域：</label>
	    <div class="layui-input-block ver-center">
	        {{storageArea}}
	    </div>
	</div>
    <div class="layui-form-item">
        <label class="layui-form-label">管理人：</label>
        <div class="layui-input-block ver-center">
        	{{assetAdmin}}
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">相关描述：</label>
        <div class="layui-input-block ver-center">
        	{{roomAddDesc}}
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">相关附件：</label>
        <div class="layui-input-block ver-center" id="enclosureUploadBtn">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">所属公司：</label>
        <div class="layui-input-block ver-center">
        	{{company}}
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">创建人：</label>
        <div class="layui-input-block ver-center">
        	{{createName}}
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">创建时间：</label>
        <div class="layui-input-block ver-center">
        	{{createTime}}
        </div>
    </div>
{{/bean}}