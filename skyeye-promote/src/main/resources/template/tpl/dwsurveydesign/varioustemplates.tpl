
<table id="setQuLogicItem" style="display: none;">
	<tr id="setQuLogicItemTrModel">
		<td class="ifSpanText1">如果本题回答</td>
		<td>
			<select name="option_id" class="logicQuOptionSel"></select>
		</td>
		<td>则&nbsp;
			<select name="option_id" class="logicType" style="width: 60px;">
				<option value="2">显示</option>
				<option value="1">跳到</option>
			</select>
		</td>
		<td>
			<select name="jump_to_qid" class="logicQuSel"></select>
		</td>
		<td>
			<div class="dialogRemoveLogic"></div>
		</td>
	</tr>
	<tr id="setQuLogicItemTrModel_score">
		<td class="ifSpanText1">如果选项</td>
		<td>
			<select name="option_id" class="logicQuOptionSel" style="width: 120px;"></select>
			评分&nbsp;
			<select name="logicScoreGtLt" class="logicScoreGtLt" style="width: 80px;">
				<option value="le">小等于</option>
				<option value="ge">大等于</option>
			</select>
			<select name="logicScoreNum" class="logicScoreNum" style="width: 40px;">
				<option>2</option>
				<option>3</option>
			</select>&nbsp;分,</td>
		<td>则&nbsp;
			<select name="logicEvent" class="logicType" style="width: 60px;">
				<option value="2">显示</option>
				<option value="1">跳到</option>
			</select>
		</td>
		<td>
			<select name="jump_to_qid" class="logicQuSel"></select>
		</td>
		<td>
			<div class="dialogRemoveLogic"></div>
		</td>
	</tr>
</table>

<div id="dialog" title="Basic dialog" style="display:none">
	<div id="editDialogCenter" class="editDialogCenter">
		<div id="dialogUeditor"></div>
	</div>
	<div id="dialogUeBottom">
		<div class="dwQuDialogBtnCon"><input type="button" value="保存" class="quDialogBtn" id="dwDialogUeOk" /></div>
	</div>
</div>
<div id="dwCommonDialog">
	<form action="#" id="dwCommonDialogForm">
		<div class="dwCommonRefIcon">
			<div class="dwCommonRefIcon1"></div>
			<div class="dwCommonRefIcon2"></div>
		</div>
		<div class="dwCommonDialogBody">
			<div class="dwCommonDialogTitle"><span id="dwComDialogTitleText">标题文本</span><span id="dwCommonDialogClose" class="closeDialog"></span></div>
			<div class="dwCommonDialogContent">
				<!-- 默认显示的LOAD -->
				<div class="dwQuDialogLoad dwQuDialogCon"><img alt="" src="../../../assets/images/load.gif"></div>
				<!-- 题目设置 -->
				<div class="dwQuSetCon dwQuFormSetDialog dwQuDialogCon">
					<ul>
						<li><label><input type="checkbox" name="setIsRequired"  >此题必答</label> </li>
						<li class="optionAutoOrder"><label><input type="checkbox" name="setRandOrder" >选择随机排列</label> </li>
						<li class="contactsAttrLi"><label><input type="checkbox" name="setAutoContacts" >关联到联系人属性</label> </li>
						<li class="contactsFieldLi"><label>用户填写的内容，会成为联系人的</label>
							<select class="contacts_range" name="setContactsField" style="width:120px;">
								<option value="1">姓名</option>
								<option value="2">手机</option>
								<option value="3">地址</option>
								<option value="4">生日</option>
								<option value="5">Email</option>
								<option value="6">性别</option>
								<option value="7">公司</option>
								<option value="8">城市</option>
								<option value="9">婚姻</option>
								<option value="10">收入</option>
							</select>
						</li>
						<li class="optionRangeHv"><label>选项排放：</label>
							<select class="option_range" name="setHv" style="width:120px;">
								<option value="2">竖排</option>
								<option value="1">横排</option>
								<option value="3">按列</option>
							</select>
							<span class="option_range_3" style="display:none;"><input type="text" name="setCellCount"  size="2" value="3" class="" >&nbsp;列</span>
						</li>
						<li class="minMaxLi">
							<span class="minSpan"><label class="lgleftLabel">&nbsp;最低分</label>&nbsp;<input class="minNum" value="1"  type="text" size="2" >&nbsp; <label class="lgRightLabel">分</label></span>&nbsp;&nbsp;
							<span class="maxSpan"><label class="lgLeftLabel">最高分</label>&nbsp;<input class="maxNum"  value="5"  type="text" size="2" >&nbsp;<label class="lgRightLabel">分</label> </span>
						</li>
						<li class="scoreMinMax">&nbsp;&nbsp;<label>最高分</label>&nbsp;
							<select class="maxScore">
								<option value="5">5分</option>
								<option value="10">10分</option>
							</select>&nbsp; </li>
					</ul>
					<div class="dwQuDialogBtnCon"><input type="button" value="保存" class="quDialogBtn" id="dwDialogQuSetSave" /></div>
				</div>
				<!-- 逻辑设置 -->
				<div class="dwQuDialogLogic dwQuDialogCon">
					<div class="dwQuDialogLogicTitle">逻辑设置</div>
					<table id="dwQuLogicTable">
					</table>
					<div class="dwQuDialogBotEvent">
						<div class="dwQuDialogAddLogic">
							<div class="dwQuIcon"></div>
						</div>
					</div>
					<div class="dwQuDialogBtnCon"><input type="button" value="保存" class="quDialogBtn" id="dwDialogSaveLogic" /></div>
				</div>
				<!-- 批量添加，单选 -->
				<div class="dwQuAddMore dwQuDialogCon">
					<div class="dwQuTextSpan">每行一个选项</div>
					<textarea id="dwQuMoreTextarea"></textarea>
					<div class="dwQuDialogBtnCon"><input type="button" value="保存" class="quDialogBtn" id="dwDialogSaveMoreItem" /></div>
				</div>
			</div>
		</div>
	</form>
</div>
