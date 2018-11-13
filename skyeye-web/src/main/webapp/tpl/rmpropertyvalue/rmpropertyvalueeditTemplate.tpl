{{#bean}}
    <div class="layui-form-item">
        <label class="layui-form-label">属性值别名<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="title" name="title" placeholder="请输入属性值别名" class="layui-input" value="{{title}}"/>
        </div>
    </div>
  	<div class="layui-form-item">
        <label class="layui-form-label">属性值<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="propertyValue" name="propertyValue" win-verify="required" placeholder="请输入属性值" class="layui-input" value="{{propertyValue}}"/>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">所属标签<i class="red">*</i></label>
        <div class="layui-input-block">
            <select id="propertyId" name="propertyId" class="layui-input" win-verify="required" lay-filter="selectParent" lay-search>
                <option value="">全部</option>
            </select>
        </div>
    </div>
    <div class="layui-form-item">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle">取消</button>
            <button class="winui-btn" lay-submit lay-filter="formEditBean">保存</button>
        </div>
    </div>
{{/bean}}