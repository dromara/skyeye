{{#bean}}
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">菜单名称：</label>
        <div class="layui-input-block ver-center">
        	{{menuName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">英文名称：</label>
        <div class="layui-input-block ver-center">
            {{menuNameEn}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">图标：</label>
       	<div class="layui-input-block">
        	<div class="layui-col-xs2" id="icon">
        	
        	</div>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">所属桌面：</label>
        <div class="layui-input-block ver-center">
        	{{desktopName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">菜单级别：</label>
        <div class="layui-input-block ver-center" id="menuLevel">
        
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">菜单地址：</label>
        <div class="layui-input-block ver-center">
        	{{menuUrl}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">角色使用量：</label>
        <div class="layui-input-block ver-center">
        	{{roleNum}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">权限点数量：</label>
        <div class="layui-input-block ver-center">
        	{{authpointNum}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">上级菜单：</label>
        <div class="layui-input-block ver-center">
        	{{menuParentName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">菜单类型：</label>
        <div class="layui-input-block ver-center">
        	{{menuType}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">系统菜单：</label>
        <div class="layui-input-block ver-center">
        	{{menuSysType}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">同步共享：</label>
        <div class="layui-input-block ver-center">
        	{{isShare}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">创建人：</label>
        <div class="layui-input-block ver-center">
        	{{userName}}
        </div>
    </div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">创建时间：</label>
        <div class="layui-input-block ver-center">
        	{{createTime}}
        </div>
    </div>
{{/bean}}