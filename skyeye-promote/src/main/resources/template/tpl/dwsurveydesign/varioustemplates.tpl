<!-- 各种模板 -->
<!-- 单选选项模板 -->
<div id="quRadioItem" class="modelHtml">
	<input type="radio"><label class="editAble quCoOptionEdit"></label>
	<div class="quItemInputCase">
		<input type="hidden" name="quItemId" value="">
		<input type="hidden" name="quItemSaveTag" value="0">
		<input type="hidden" name="isNote" value="0">
		<input type="hidden" name="checkType" value="NO">
		<input type="hidden" name="isRequiredFill" value="0">
	</div>
</div>
<!-- 多选选项模板 -->
<div id="quCheckboxItem" class="modelHtml">
	<input type="checkbox"><label class="editAble quCoOptionEdit"></label>
	<div class="quItemInputCase">
		<input type="hidden" name="quItemId" value="">
		<input type="hidden" name="quItemSaveTag" value="0">
		<input type="hidden" name="isNote" value="0">
		<input type="hidden" name="checkType" value="NO">
		<input type="hidden" name="isRequiredFill" value="0">
	</div>
</div>
<!-- 评分题选项模板 -->
<table class="modelHtml">
	<tr id="quScoreItemModel" class="quScoreOptionTr">
		<td class="quCoItemTableTd quOptionEditTd">
			<label class="editAble quCoOptionEdit">评分项</label>
			<div class="quItemInputCase"><input type="hidden" name="quItemId" value=""><input type="hidden" name="quItemSaveTag" value="0"></div>
		</td>
		<td class="quCoItemTableTd">
			<table class="scoreNumTable">
				<tr>
					<td>1</td>
					<td>2</td>
					<td>3</td>
					<td>4</td>
					<td>5</td>
				</tr>
			</table>
		</td>
		<td class="quCoItemTableTd">分</td>
	</tr>
</table>
<!-- 排序题模板 -->
<div id="quOrderItemLeftModel" class="modelHtml">
	<label class="editAble quCoOptionEdit">&nbsp;</label>
	<div class="quItemInputCase"><input type="hidden" name="quItemId" value=""><input type="hidden" name="quItemSaveTag" value="0"></div>
</div>
<table class="modelHtml">
	<tr id="quOrderItemRightModel">
		<td class="quOrderyTableTd">1</td>
		<td></td>
	</tr>
</table>
<!--多项填空题 -->
<table class="modelHtml">
	<tr id="mFillblankTableModel">
		<td align="right" class="mFillblankTableEditTd">
			<label class="editAble quCoOptionEdit">大一</label>
			<div class="quItemInputCase"><input type="hidden" name="quItemId" value=""><input type="hidden" name="quItemSaveTag" value="0"></div>
		</td>
		<td><input type="text" style="width:200px;padding:5px;"></td>
	</tr>
</table>
<table class="modelHtml">
	<tr id="quChenColumnModel">
		<td class="quChenColumnTd">
			<label class="editAble quCoOptionEdit">新项</label>
			<div class="quItemInputCase"><input type="hidden" name="quItemId" value=""><input type="hidden" name="quItemSaveTag" value="0"></div>
		</td>
	</tr>
	<tr id="quChenRowModel">
		<td class="quChenRowTd">
			<label class="editAble quCoOptionEdit">新项</label>
			<div class="quItemInputCase"><input type="hidden" name="quItemId" value=""><input type="hidden" name="quItemSaveTag" value="0"></div>
		</td>
	</tr>
</table>
<!-- 逻辑值保存模板 -->
<div id="quLogicItemModel" class="modelHtml">
	<div class="quLogicItem">
		<input type="hidden" name="quLogicId" value="" />
		<input type="hidden" name="cgQuItemId" value="0" />
		<input type="hidden" name="skQuId" value="0" />
		<input type="hidden" name="visibility" value="0">
		<input type="hidden" name="logicSaveTag" value="0">

		<input type="hidden" name="geLe" value="le">
		<input type="hidden" name="scoreNum" value="2">
		<input type="hidden" name="logicType" value="1">
	</div>
</div>
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
<div id="dwCommonEditRoot">
	<div class="dwCommonEdit">
		<ul class="dwComEditMenuUl">
			<li>
				<a href="javascript:;" class="SeniorEdit"><i class="menu_edit2_icon"></i>高级编辑</a>
			</li>
			<li class="option_Set_Li">
				<a href="javascript:;" class="option_Set"><i class="menu_edit4_icon"></i>选项设置</a>
			</li>
		</ul>
		<ul class="dwComEditOptionUl">
			<li class="dwOptionUp">
				<div class=dwQuIcon></div>
			</li>
			<li class="dwOptionDown">
				<div class=dwQuIcon></div>
			</li>
			<li class="dwOptionDel">
				<div class=dwQuIcon></div>
			</li>
		</ul>
		<div class="dwComEditMenuBtn"></div>
		<div id="dwComEditContent" contenteditable="true">请问你的年级是？</div>
	</div>
</div>
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
<div id="modelUIDialog">
	<div id="modelUIDialogContent">
		<!-- 填空题  填空数据类型设置  -->
		<div class="dwQuFillDataTypeOption dwQuFormSetDialog dwQuDialogCon">
			<ul>
				<li><label>输入框宽：</label>
					<input type="text" name="qu_inputWidth" value="300"><span>&nbsp;字符</span>
				</li>
				<li><label>输入框高：</label>
					<input type="text" name="qu_inputRow" value="1"><span>&nbsp;行</span>
				</li>
				<li><label>数据类型：</label>
					<select class="option_range" name="quFill_checkType" style="width:120px;">
						<option value="NO">无验证</option>
						<option value="EMAIL">Email</option>
						<option value="UNSTRCN">禁止中文</option>
						<option value="STRCN">仅许中文</option>
						<option value="NUM">数值</option>
						<option value="TELENUM">电话号码</option>
						<option value="PHONENUM">手机号码</option>
						<option value="DATE">日期</option>
						<option value="IDENTCODE">身份证号</option>
						<option value="ZIPCODE">邮政编码</option>
						<option value="URL">网址</option>
					</select>
					<span class="option_range_3" style="display:none;">&nbsp;列</span>
				</li>
			</ul>
			<div class="dwQuDialogBtnCon"><input type="button" value="保存" class="quDialogBtn" id="dwDialogQuFillOptionSave" /></div>
		</div>
		<div class="dwQuRadioCheckboxOption dwQuFormSetDialog dwQuDialogCon">
			<ul>
				<li><label>选项设置</label></li>
				<li class="quOptionAddFill"><label><input type="checkbox" name="quOption_isNote" >选项后添加填空</label> </li>
				<li class="quOptionFillContentLi"><label style="padding-left:16px;">填空内容：</label>
					<select class="option_range" name="quOption_checkType" style="width:120px;">
						<option value="NO">无限制</option>
						<option value="EMAIL">Email</option>
						<option value="UNSTRCN">禁止中文</option>
						<option value="STRCN">仅许中文</option>
						<option value="NUM">数值</option>
						<option value="TELENUM">电话号码</option>
						<option value="PHONENUM">手机号码</option>
						<option value="DATE">日期</option>
						<option value="IDENTCODE">身份证号</option>
						<option value="ZIPCODE">邮政编码</option>
						<option value="URL">网址</option>
					</select>
					<span class="option_range_3" style="display:none;">&nbsp;列</span>
				</li>
				<li class="quOptionFillRequiredLi"><label style="padding-left:15px;"><input type="checkbox" name="quOption_isRequiredFill" checked="checked" >&nbsp;该空可不填</label>&nbsp;</li>
			</ul>
			<div class="dwQuDialogBtnCon"><input type="button" value="保存" class="quDialogBtn" id="dwDialogQuOptionSetSave" /></div>
		</div>
		<div class="dwSurveyAttrSetDialog dwQuFormSetDialog dwQuDialogCon">
			<div class="tabbarDialog_1">
				<div class="p_DialogContent">
					<input type="hidden" name="svyAttrSaveTag" value="1">
					<div class="p_DialogContentTitle">回答限制</div>
					<div class="p_DialogContentRoot">
						<div class="p_DialogContentItem"><label><input type="checkbox" name="effective" value="4"> 每台电脑或手机只能答一次</label></div>
						<div class="p_DialogContentItem"><label><input type="checkbox" name="effectiveIp" value="1"> 每个IP只能答一次</label></div>
						<div class="p_DialogContentItem"><label><input type="checkbox" name="rule" value="3"> 启用访问密码</label> &nbsp;&nbsp;&nbsp;设置密码：
							<input type="text" size="10" name="ruleCode" class="inputSytle_1">
						</div>
						<div class="p_DialogContentItem"><label><input type="checkbox" name="refresh" value="1"> 有重复回答启用验证码</label></div>
						<div class="p_DialogContentItem" style="display: none;"><label><input type="checkbox" name="mailOnly" value="1"> 只有邮件邀请唯一链接的受访者可回答</label></div>
					</div>
				</div>
				<div class="p_DialogContent">
					<div class="p_DialogContentTitle">何时结束</div>
					<div class="p_DialogContentRoot">
						<div class="p_DialogContentItem"><label><input type="checkbox" name="ynEndNum" value="1"> 收集到&nbsp;<input type="text" size="12"  class="inputSytle_1" name="endNum">&nbsp;份答卷时结束</label></div>
						<div class="p_DialogContentItem"><label><input type="checkbox" name="ynEndTime" value="1"> 到&nbsp;<input type="text" size="20"  class="inputSytle_1 Wdate" onfocus="WdatePicker({minDate:'%y-%M-{%d+1}',dateFmt:'yyyy-MM-dd HH:mm:ss'})" name="endTime">&nbsp;时结束 </label></div>
					</div>
				</div>
			</div>
			<div class="dwQuDialogBtnCon"><input type="button" value="保存" class="quDialogBtn" id="dwDialogSurveyAttrSave" /></div>
		</div>
	</div>
</div>