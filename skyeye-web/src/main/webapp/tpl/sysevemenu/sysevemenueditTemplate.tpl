{{#bean}}
    <div class="layui-form-item">
        <label class="layui-form-label">菜单名称</label>
        <div class="layui-input-block">
            <input type="text" id="menuName" name="menuName" win-verify="required" placeholder="请输入菜单名称" class="layui-input" value="{{menuName}}" />
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">窗口标题</label>
        <div class="layui-input-block">
            <input type="text" id="menuTitle" name="menuTitle" win-verify="required" placeholder="请输入窗口标题" class="layui-input" value="{{titleName}}" />
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">菜单图标</label>
        <div class="layui-input-block">
            <input type="text" id="menuIcon" name="menuIcon" win-verify="required" placeholder="请输入图标src或者class" class="layui-input" value="{{menuIcon}}" />
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">菜单地址</label>
        <div class="layui-input-block">
            <input type="text" id="menuUrl" name="menuUrl" win-verify="required" placeholder="请输入菜单地址" class="layui-input" value="{{menuUrl}}" />
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">菜单级别</label>
        <div class="layui-input-block winui-radio">
            <input type="radio" name="menuLevel" value="1" title="创世菜单" lay-filter="menuLevel"/>
            <input type="radio" name="menuLevel" value="2" title="子菜单" lay-filter="menuLevel"/>
        </div>
    </div>
    <div class="layui-form-item" id="parentIdBox">
        <label class="layui-form-label">上级菜单</label>
        <div class="layui-input-block" id="lockParentSel">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">菜单类型</label>
        <div class="layui-input-block winui-radio">
            <input type="radio" name="menuType" value="html" title="HTML" lay-filter="menuType"/>
            <input type="radio" name="menuType" value="iframe" title="Iframe" lay-filter="menuType"/>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">系统菜单</label>
        <div class="layui-input-block winui-switch">
            <input id="menuSysType" name="menuSysType" lay-filter="isNecessary" type="checkbox" lay-skin="switch" lay-text="是|否" {{#compare2 menuSysType}}{{/compare2}} value="{{#compare3 menuSysType}}{{/compare3}}" />
        </div>
    </div>
    <div class="layui-form-item">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle">取消</button>
            <button class="winui-btn" lay-submit lay-filter="formEditMenu">保存</button>
        </div>
    </div>
{{/bean}}