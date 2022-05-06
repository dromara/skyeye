{{#bean}}
    <div class="layui-form-item">
        <label class="layui-form-label">菜单名称<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="menuName" name="menuName" win-verify="required" placeholder="请输入菜单名称" class="layui-input" value="{{menuName}}" />
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">英文名称<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="menuNameEn" name="menuNameEn" win-verify="required" placeholder="请输入英文名称" class="layui-input" value="{{menuNameEn}}" />
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">图标类型<i class="red">*</i></label>
        <div class="layui-input-block winui-radio">
            <input type="radio" name="menuIconType" value="1" title="Icon" lay-filter="menuIconType" checked/>
            <input type="radio" name="menuIconType" value="2" title="图片" lay-filter="menuIconType" />
        </div>
    </div>
    <div class="layui-form-item menuIconTypeIsOne">
        <label class="layui-form-label">菜单图标<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="menuIcon" name="menuIcon" placeholder="请输入图标src或者class" class="layui-input" value="{{menuIcon}}" />
        </div>
    </div>
    <div class="layui-form-item menuIconTypeIsOne">
        <label class="layui-form-label">图标预览</label>
        <div class="layui-input-block">
            <div class="layui-col-xs12">
            	<div class="layui-col-xs2">
            		<div class="winui-icon winui-icon-font" style="width: 60px; height: 60px;"><i id="iconShow" class="" style="font-size: 48px; line-height: 65px;"></i></div>
            	</div>
            	<div class="layui-col-xs5">
            		<div class="layui-input-inline" style="width: 120px;">
			            <input type="text" value="" class="layui-input" placeholder="请选择图标颜色" id="menuIconColorinput" />
			        </div>
			        <div id="menuIconColor"></div>
            	</div>
            	<div class="layui-col-xs5">
            		<div class="layui-input-inline" style="width: 120px;">
			            <input type="text" value="" class="layui-input" placeholder="请选择背景颜色" id="menuIconBginput" />
			        </div>
			        <div id="menuIconBg"></div>
            	</div>
			</div>
        </div>
    </div>
    <div class="layui-form-item menuIconTypeIsTwo">
        <label class="layui-form-label">菜单图片<i class="red">*</i></label>
        <div class="layui-input-block">
            <div class="upload" id="menuIconPic"></div>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">所属桌面</label>
        <div class="layui-input-block">
            <select id="desktop" name="desktop" lay-filter="desktop" lay-search="">
				
            </select>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">菜单地址<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="menuUrl" name="menuUrl" win-verify="required" placeholder="请输入菜单地址" class="layui-input" value="{{menuUrl}}" />
            <div class="layui-form-mid layui-word-aux">如果是一级菜单,格式为：--<br>如果是子菜单,格式为：../../tpl/model/modellist.html</div>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">所属系统<i class="red">*</i></label>
        <div class="layui-input-block">
			<select id="menuSysWinId" name="menuSysWinId" class="menuSysWinId" lay-filter="selectMenuSysWinId" lay-search="">
				
			</select>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">菜单级别<i class="red">*</i></label>
        <div class="layui-input-block winui-radio">
            <input type="radio" name="menuLevel" value="1" title="创世菜单" lay-filter="menuLevel"/>
            <input type="radio" name="menuLevel" value="2" title="子菜单" lay-filter="menuLevel"/>
        </div>
    </div>
    <div class="layui-form-item" id="parentIdBox">
        <label class="layui-form-label">上级菜单<i class="red">*</i></label>
        <div class="layui-input-block" id="lockParentSel">
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">菜单类型<i class="red">*</i></label>
        <div class="layui-input-block winui-radio">
            <input type="radio" name="menuType" value="html" title="当前页面打开" lay-filter="menuType"/>
            <input type="radio" name="menuType" value="win" title="新窗口打开" lay-filter="menuType"/>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">系统菜单<i class="red">*</i></label>
        <div class="layui-input-block winui-switch">
            <input id="menuSysType" name="menuSysType" lay-filter="isNecessary" type="checkbox" lay-skin="switch" lay-text="是|否" {{#compare2 menuSysType}}{{/compare2}} value="{{#compare3 menuSysType}}{{/compare3}}" />
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">同步共享<i class="red">*</i></label>
        <div class="layui-input-block winui-switch">
            <input id="isShare" name="isShare" lay-filter="isShare" type="checkbox" lay-skin="switch" lay-text="是|否" {{#compare4 isShare}}{{/compare4}} value="{{#compare5 isShare}}{{/compare5}}" />
        </div>
    </div>
    <div class="layui-form-item">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle"><language showName="com.skyeye.cancel"></language></button>
            <button class="winui-btn" lay-submit lay-filter="formEditMenu"><language showName="com.skyeye.save"></language></button>
        </div>
    </div>
{{/bean}}