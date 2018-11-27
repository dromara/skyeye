{{#bean}}
    <div class="layui-form-item">
        <label class="layui-form-label">公司名称<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="companyName" name="companyName" win-verify="required" placeholder="请输入公司名称" class="layui-input" maxlength="50" value="{{companyName}}"/>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">公司类型<i class="red">*</i></label>
        <div class="layui-input-block winui-radio">
            <input type="radio" name="companyType" value="1" title="总公司" lay-filter="companyType"/>
            <input type="radio" name="companyType" value="2" title="子公司" lay-filter="companyType" />
        </div>
    </div>
    <div class="layui-form-item" id="parentIdBox">
        <label class="layui-form-label">总公司<i class="red">*</i></label>
        <div class="layui-input-block">
        	<select id="OverAllCompany" lay-filter="OverAllCompany" lay-search="">
        		<option value="">请选择</option>
        	</select>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">公司地址<i class="red">*</i></label>
        <div class="layui-input-block" id="lockParentSel">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">详细地址<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="addressDetailed" name="addressDetailed" win-verify="required" placeholder="请输入公司详细地址" class="layui-input" maxlength="50" value="{{addressDetailed}}"/>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">公司简介</label>
        <div class="layui-input-block">
        	<textarea id="content" name="content" style="display: none;">{{companyDesc}}</textarea>
        </div>
    </div>
    <div class="layui-form-item">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle">取消</button>
            <button class="winui-btn" lay-submit lay-filter="formEditBean">保存</button>
        </div>
    </div>
{{/bean}}