{{#bean}}
    <div class="layui-form-item">
        <label class="layui-form-label">类型名称<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="typeName" name="typeName" win-verify="required" placeholder="请输入类型名称" class="layui-input"  maxlength="10" value="{{typeName}}" />
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">logo类型<i class="red">*</i></label>
        <div class="layui-input-block winui-radio">
            <input type="radio" name="iconType" value="1" title="Icon" lay-filter="iconType" checked/>
            <input type="radio" name="iconType" value="2" title="图片" lay-filter="iconType" />
        </div>
    </div>
    <div class="layui-form-item menuIconTypeIsOne">
        <label class="layui-form-label">logo</label>
        <div class="layui-input-block">
            <input type="text" id="iconPath" name="iconPath" placeholder="请输入图标src或者class" class="layui-input" value="{{iconPath}}" />
        </div>
    </div>
    <div class="layui-form-item menuIconTypeIsOne">
        <label class="layui-form-label">logo预览</label>
        <div class="layui-input-block">
            <div class="layui-col-xs12">
            	<div class="layui-col-xs2">
            		<div class="winui-icon winui-icon-font" style="width: 60px; height: 60px;"><i id="iconShow" class="" style="font-size: 48px; line-height: 65px;"></i></div>
            	</div>
			</div>
        </div>
    </div>
    <div class="layui-form-item menuIconTypeIsTwo layui-hide">
        <label class="layui-form-label">logo图片</label>
        <div class="layui-input-block">
            <div class="upload" id="iconpicPath"></div>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle"><language showName="com.skyeye.cancel"></language></button>
            <button class="winui-btn" lay-submit lay-filter="formEditMenu"><language showName="com.skyeye.save"></language></button>
        </div>
    </div>
{{/bean}}