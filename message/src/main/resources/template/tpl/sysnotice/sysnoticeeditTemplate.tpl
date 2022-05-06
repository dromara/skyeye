{{#bean}}
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">公告标题<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="title" name="title" win-verify="required" placeholder="请输入公告标题" class="layui-input" value="{{title}}"/>
        </div>
    </div>
	<div class="layui-form-item layui-col-xs6">
	    <label class="layui-form-label">一级分类<i class="red">*</i></label>
        <div class="layui-input-block">
            <select lay-filter="noticeTypeId" lay-search="" win-verify="required" id="noticeTypeId">
            
            </select>
        </div>
	</div>
    <div class="layui-form-item layui-col-xs6">
        <label class="layui-form-label">二级分类<i class="red">*</i></label>
        <div class="layui-input-block">
        	<select lay-filter="secondTypeId" lay-search="" win-verify="required" id="secondTypeId">
            
            </select>
        </div>
    </div>
	<div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">群发类型<i class="red">*</i></label>
        <div class="layui-input-block winui-radio">
            <input type="radio" name="sendType" value="1" title="群发所有人" lay-filter="sendType"/>
            <input type="radio" name="sendType" value="2" title="选择性群发" lay-filter="sendType"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12" id="sendTo">
        <label class="layui-form-label">人员选择<i class="red">*</i></label>
        <div class="layui-input-block">
            <input type="text" id="userName" name="userName" placeholder="请选择公告收件人" class="layui-input"/>
            <i class="fa fa-user-plus input-icon" id="userNameSelPeople"></i>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">定时通知<i class="red">*</i></label>
        <div class="layui-input-block winui-radio">
        	<input type="radio" name="timeSend" value="2" title="是" lay-filter="timeSend"/>
            <input type="radio" name="timeSend" value="1" title="否" lay-filter="timeSend"/>
            <br><div class="layui-form-mid layui-word-aux">"已失效"和"已执行"默认为"否"</div>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12" id="sendTime">
        <label class="layui-form-label">通知时间<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="delayedTime" name="delayedTime" placeholder="请选择定时通知时间" class="layui-input" value="{{delayedTime}}"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">邮件通知<i class="red">*</i></label>
        <div class="layui-input-block winui-radio">
        	<input type="radio" name="whetherEmail" value="2" title="是" lay-filter="whetherEmail"/>
            <input type="radio" name="whetherEmail" value="1" title="否" lay-filter="whetherEmail"/>
        </div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <label class="layui-form-label">公告内容<i class="red">*</i></label>
        <div class="layui-input-block">
			<textarea id="content" name="content" placeholder="请输入公告内容" class="layui-textarea">{{content}}</textarea>
		</div>
    </div>
    <div class="layui-form-item layui-col-xs12">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle"><language showName="com.skyeye.cancel"></language></button>
            <button class="winui-btn" lay-submit lay-filter="formEditBean"><language showName="com.skyeye.save"></language></button>
        </div>
    </div>
{{/bean}}