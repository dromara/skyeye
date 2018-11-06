{{#bean}}
    <div class="layui-form-item">
        <label class="layui-form-label">ICON属性<i class="red">*</i></label>
        <div class="layui-col-xs6">
            <div class="layui-col-xs10">
                <input type="text" id="iconClass" name="iconClass" win-verify="required" placeholder="请输入ICON属性" class="layui-input" value="{{iconClass}}" />
            </div>
            <div class="layui-col-xs2">
            	<a class="layui-btn layui-btn-xs yl" id="yl">预览</a>
            </div>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">ICON预览</label>
        <div class="layui-input-block">
            <div class="layui-col-xs2 item">
				<div class="layui-col-xs12 icon-item">
					<i id="iconyl" class="fa {{iconClass}} fa-fw"></i>
				</div>
			</div>
        </div>
    </div>
    <div class="layui-form-item">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle">取消</button>
            <button class="winui-btn" lay-submit lay-filter="formEditBean">保存</button>
        </div>
    </div>
{{/bean}}