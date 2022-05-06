{{#bean}}
	<div class="layui-form-item">
        <label class="layui-form-label">所属公司：</label>
        <div class="layui-input-block ver-center">
            {{companyName}}
        </div>
    </div>
	<div class="layui-form-item">
		<label class="layui-form-label">证照名称：</label>
		<div class="layui-input-block ver-center">
			{{licenceName}}
		</div>
	</div>
	<div class="layui-form-item">
	    <label class="layui-form-label">证照编号：</label>
	    <div class="layui-input-block ver-center">
	        {{licenceNum}}
	    </div>
	</div>
    <div class="layui-form-item">
        <label class="layui-form-label">签发单位：</label>
        <div class="layui-input-block ver-center">
        	{{issuingOrganization}}
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">签发时间：</label>
        <div class="layui-input-block ver-center">
        	{{issueTime}}
        </div>
    </div>
    <div class="layui-form-item">
	    <label class="layui-form-label">是否年审：</label>
	    <div class="layui-input-block ver-center">
	        {{annualReview}}
	    </div>
	</div>
	<div class="layui-form-item" id="nextTime">
	    <label class="layui-form-label">下次年审时间：</label>
	    <div class="layui-input-block ver-center">
	        {{nextAnnualReview}}
	    </div>
	</div>
	<div class="layui-form-item">
	    <label class="layui-form-label">有效期：</label>
	    <div class="layui-input-block ver-center">
	        {{termOfValidity}}
	    </div>
	</div>
	<div class="layui-form-item" id="termTime">
	    <label class="layui-form-label">有效期至：</label>
	    <div class="layui-input-block ver-center">
	        {{termOfValidityTime}}
	    </div>
	</div>
	<div class="layui-form-item">
	    <label class="layui-form-label">管理人：</label>
	    <div class="layui-input-block ver-center">
	        {{licenceAdmin}}
	    </div>
	</div>
	<div class="layui-form-item">
	    <label class="layui-form-label">借用人：</label>
	    <div class="layui-input-block ver-center">
	        {{borrowName}}
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