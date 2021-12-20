{{#bean}}
	<div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">标题<i class="red">*</i></label>
        <div class="layui-input-block ver-center">
        	{{inspectionTitle}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">车牌号<i class="red">*</i></label>
        <div class="layui-input-block ver-center">
	        {{licensePlate}}
	    </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">年检地点</label>
        <div class="layui-input-block">
            <input type="text" id="inspectionArea" name="inspectionArea" placeholder="请输入年检地点" class="layui-input" value="{{inspectionArea}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">联系电话</label>
        <div class="layui-input-block">
        	<input type="text" id="contactInformation" name="contactInformation" placeholder="请输入联系电话" class="layui-input" value="{{contactInformation}}" maxlength="15"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">年检费用</label>
        <div class="layui-input-block">
        	<input type="number" id="inspectionPrice" name="inspectionPrice"  placeholder="请输入年检费用（元）" class="layui-input" value="{{inspectionPrice}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">本次年检时间<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="thisInspectionTime" name="thisInspectionTime" win-verify="required" placeholder="请输入本次年检时间" class="layui-input" value="{{thisInspectionTime}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">下次年检时间<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="nextInspectionTime" name="nextInspectionTime" win-verify="required" placeholder="请输入下次年检时间" class="layui-input" value="{{nextInspectionTime}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">相关描述：</label>
        <div class="layui-input-block">
        	<textarea id="roomAddDesc" name="roomAddDesc"  placeholder="请输入相关描述" class="layui-textarea" style="height: 100px;" maxlength="200">{{roomAddDesc}}</textarea>
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