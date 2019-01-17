<div class="layui-form-item">
    <label class="layui-form-label">角色</label>
    <div class="layui-input-block">
		<dl class="menulist layui-form">
			{{#each rows}}
			    <dt style="width:100%;height: 20px;">
			        <input type="checkbox" class="cbxmenu" value="{{id}}" lay-skin="primary" lay-filter="cbxmenu" {{isCheck}} />
			        <font class="cbxmenu-font">{{roleName}}</font>
			        <h5 class="cbxmenu-font">（{{roleDesc}}）</h5>
			        <span class="functions"></span>
			    </dt>
			    <br/>
		    {{/each}}
		</dl>
	</div>
</div>
<div class="layui-form-item">
    <div class="layui-input-block">
        <button class="winui-btn" id="cancle">取消</button>
        <button class="winui-btn" lay-submit lay-filter="formEditBindRole">保存</button>
    </div>
</div>