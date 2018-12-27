{{#bean}}
	<div class="layui-form-item">
        <div class="p_DialogContent">
			<input type="hidden" name="svyAttrSaveTag" value="1">
			<div class="p_DialogContentTitle">回答限制</div>
			<div class="p_DialogContentRoot">
				<div class="p_DialogContentItem">
					<input type="checkbox" name="effective" {{#compare1 effective '4'}}{{/compare1}} value="4" lay-filter="effective" />每台电脑或手机只能答一次
				</div>
				<div class="p_DialogContentItem">
					<input type="checkbox" name="effectiveIp" {{#compare1 effectiveIp '1'}}{{/compare1}} value="1" lay-filter="effectiveIp" />每个IP只能答一次
				</div>
				<div class="p_DialogContentItem">
					<input type="checkbox" name="rule" {{#compare1 rule '3'}}{{/compare1}} value="3" lay-filter="rule" />启用访问密码&nbsp;&nbsp;&nbsp;设置密码：
					<input type="text" size="10" id="ruleCode" name="ruleCode" class="inputSytle_1" value="{{ruleCode}}" {{#compare2 rule '3'}}{{/compare2}}>
				</div>
				<div class="p_DialogContentItem">
					<input type="checkbox" name="refresh" {{#compare1 refresh '1'}}{{/compare1}} value="1" lay-filter="refresh" />有重复回答启用验证码
				</div>
			</div>
		</div>
		<div class="p_DialogContent">
			<div class="p_DialogContentTitle">结束限制</div>
			<div class="p_DialogContentRoot">
				<div class="p_DialogContentItem">
					<input type="checkbox" name="ynEndNum" {{#compare1 ynEndNum '1'}}{{/compare1}} value="1" lay-filter="ynEndNum" /> 收集到&nbsp;
					<input type="text" size="12" class="inputSytle_1" id="endNum" name="endNum" value="{{endNum}}" {{#compare2 ynEndNum '1'}}{{/compare2}}>&nbsp;份答卷时结束
				</div>
				<div class="p_DialogContentItem">
					<input type="checkbox" name="ynEndTime" {{#compare1 ynEndTime '1'}}{{/compare1}} value="1" lay-filter="ynEndTime" /> 到&nbsp;
					<input type="text" size="20" class="inputSytle_1" id="endTime" name="endTime" value="{{endTime}}" {{#compare2 ynEndTime '1'}}{{/compare2}}>&nbsp;时结束
				</div>
			</div>
		</div>
    </div>
    <div class="layui-form-item">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle">取消</button>
            <button class="winui-btn" lay-submit lay-filter="formAddBean">保存</button>
        </div>
    </div>
{{/bean}}