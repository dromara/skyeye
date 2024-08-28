{{#bean}}
	<!-- 背景设置 -->
    <div class="winui-tab-item layui-show">
        <h1><i class="fa fa-picture-o fa-fw"></i>背景</h1>
        <hr class="layui-bg-blue">
        <div class="background-preview">
            <div class="preview-start"></div>
            <div class="preview-window">
                <div class="preview-window-title"><p></p></div>
                <span>示例文本</span>
            </div>
            <div class="preview-taskbar"></div>
        </div>
        <h2>选择图片</h2>
        <div class="background-choose" id="background-choose" style="min-height: 90px;">
        	{{#each winBgPic}}
				<img src="{{#compare1 picUrl}}{{/compare1}}" class="bgPicItem" picUrl="{{picUrl}}"/>
			{{/each}}
        </div>
        <h2>自定义图片</h2>
        <div class="background-choose" id="cus-background-choose" style="min-height: 130px;">
        </div>
        <div class="background-choose" id="bgVagueChoose" style="min-height: 90px;">
        	<form class="layui-form" action="" id="showForm" autocomplete="off">
            	<div class="layui-form-item">
		            <label class="layui-form-label">是否雾化</label>
		            <div class="layui-input-block winui-switch">
		                <input id="winBgPicVague" name="winBgPicVague" lay-filter="winBgPicVague" type="checkbox" lay-skin="switch" lay-text="是|否" value="" />
		            </div>
		        </div>
	        </form>
        </div>
        <!-- 上传图片 -->
        <div class="background-upload" id="addBean">浏览</div>
    </div>
    <!-- 颜色设置 -->
    <div class="winui-tab-item">
        <h1><i class="fa fa-paw fa-fw"></i>主题色</h1>
        <hr class="layui-bg-blue">
        <div class="background-preview">
            <div class="preview-start"></div>
            <div class="preview-window">
                <div class="preview-window-title"><p></p></div>
                <span>示例文本</span>
            </div>
            <div class="preview-taskbar"></div>
        </div>
        <h2>主题色</h2>
        <div class="color-choose" id="color-choose">
        	{{#each winThemeColor}}
				<div class="{{colorClass}}"></div>
			{{/each}}
        </div>
    </div>
    <!-- 锁屏界面 -->
    <div class="winui-tab-item">
        <h1><i class="fa fa-lock fa-fw"></i>锁屏界面</h1>
        <hr class="layui-bg-blue">
        <div class="lockscreen-preview">
            <div class="lockscreen-preview-time"></div>
        </div>
        <h2>选择图片</h2>
        <div class="lockscreen-choose" id="lockscreen-choose" style="min-height: 90px;">
        	{{#each winLockBgPic}}
				<img src="{{#compare1 picUrl}}{{/compare1}}" class="lockBgPicItem" picUrl="{{picUrl}}"/>
			{{/each}}
        </div>
        <h2>自定义图片</h2>
        <div class="lockscreen-choose" id="cus-lockscreen-choose" style="min-height: 130px;">
        </div>
        <!-- 上传图片 -->
        <div class="lockscreen-upload" id="addBean1">浏览</div>
    </div>
    <!-- 开始 -->
    <div class="winui-tab-item">
        <h1><i class="fa fa-windows fa-fw"></i>开始</h1>
        <hr class="layui-bg-blue">
        <div class="background-preview">
            <div class="preview-start"></div>
            <div class="preview-window">
                <div class="preview-window-title"><p></p></div>
                <span>示例文本</span>
            </div>
            <div class="preview-taskbar"></div>
        </div>
        <h2>开始菜单尺寸</h2>
        <div class="layui-form winui-radio start-size">
            <input type="radio" name="startsize" value="xs" title="迷你" lay-filter="startSize">
            <input type="radio" name="startsize" value="sm" title="中等" lay-filter="startSize">
            <input type="radio" name="startsize" value="lg" title="宽敞" lay-filter="startSize">
        </div>
    </div>
    <!-- 任务栏 -->
    <div class="winui-tab-item">
        <h1><i class="fa fa-tasks fa-fw"></i>任务栏</h1>
        <hr class="layui-bg-blue">
        <div class="layui-form winui-radio taskbar-position">
            <!-- <input type="radio" name="position" value="top" title="顶部" lay-filter="taskPosition"> -->
            <input type="radio" name="position" value="bottom" title="底部" lay-filter="taskPosition">
        </div>
    </div>
    
    <!-- 菜单显示 -->
    <div class="winui-tab-item">
        <h1><i class="fa fa-tasks fa-fw"></i>菜单显示</h1>
        <hr class="layui-bg-blue">
        <div class="layui-form winui-radio bottomMenuIcon-position">
            <input type="radio" name="bottomMenuIcon" value="1" title="图标" lay-filter="winBottomMenuIcon">
            <input type="radio" name="bottomMenuIcon" value="0" title="图标+文字" lay-filter="winBottomMenuIcon">
        </div>
    </div>
{{/bean}}
