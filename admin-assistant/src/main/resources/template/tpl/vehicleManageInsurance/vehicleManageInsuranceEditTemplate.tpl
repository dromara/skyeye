{{#bean}}
	<div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">保险标题<i class="red">*</i></label>
        <div class="layui-input-block ver-center">
        	{{insuranceTitle}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">车牌号<i class="red">*</i></label>
        <div class="layui-input-block ver-center">
	        {{licensePlate}}
	    </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">投保公司<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="insuranceCompany" name="insuranceCompany" win-verify="required" placeholder="请输入投保公司" class="layui-input" value="{{insuranceCompany}}"/>
        </div>
    </div>
    <table class="layui-table">
        <thead>
            <tr>
                <th>险种<i class="red">*</i></th>
                <th>保费<i class="red">*</i></th>
                <th>保额<i class="red">*</i></th>
                <th>备注</th>
            </tr>
        </thead>
        <tbody id="addTable" class="insurance-table">
        </tbody>
	</table>
	<div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">投保总费用<i class="red">*</i></label>
        <div class="layui-input-block ver-center">
        	<span id="insuranceAllPrice"></span>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">投保电话</label>
        <div class="layui-input-block">
        	<input type="text" id="insuredTelephone" name="insuredTelephone" placeholder="请输入投保电话" class="layui-input" value="{{insuredTelephone}}" maxlength="15"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">投保有效期<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="validityTime" name="validityTime" win-verify="required" placeholder="请选择投保有效期" class="layui-input" value="{{validityTime}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">相关描述：</label>
        <div class="layui-input-block">
        	<textarea id="roomAddDesc" name="roomAddDesc"  placeholder="请输入附加描述" class="layui-textarea" style="height: 100px;" maxlength="200">{{roomAddDesc}}</textarea>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">相关附件</label>
        <div class="layui-input-block" id="enclosureUpload">
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle"><language showName="com.skyeye.cancel"></language></button>
            <button class="winui-btn" lay-submit lay-filter="formEditBean"><language showName="com.skyeye.save"></language></button>
        </div>
    </div>
{{/bean}}
