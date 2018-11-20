{{#bean}}
	<div class="layui-form-item">
        <label class="layui-form-label">主题颜色属性<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="colorClass" name="colorClass" win-verify="required" placeholder="请输入主题颜色属性" class="layui-input" value="{{colorClass}}" />
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">主题颜色效果<i class="red">*</i></label>
        <div class="layui-input-block color-choose" style="width: auto">
            <div id="modelEffect" class="{{colorClass}}"></div>
        </div>
    </div>
    <div class="layui-form-item">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle">取消</button>
            <button class="winui-btn" lay-submit lay-filter="formEditBean">保存</button>
        </div>
    </div>
{{/bean}}