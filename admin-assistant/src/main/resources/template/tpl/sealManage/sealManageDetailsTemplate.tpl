{{#bean}}
	<div class="layui-form-item">
		<label class="layui-form-label">印章名称：</label>
		<div class="layui-input-block ver-center">
			{{sealName}}
		</div>
	</div>
	<div class="layui-form-item">
	    <label class="layui-form-label">启用日期：</label>
	    <div class="layui-input-block ver-center">
	        {{enableTime}}
	    </div>
	</div>
    <div class="layui-form-item">
        <label class="layui-form-label">管理人：</label>
        <div class="layui-input-block ver-center">
        	{{sealAdmin}}
        </div>
    </div>
    <div class="layui-form-item">
	    <label class="layui-form-label">借用人：</label>
	    <div class="layui-input-block ver-center">
	        {{borrowId}}
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
{{/bean}}