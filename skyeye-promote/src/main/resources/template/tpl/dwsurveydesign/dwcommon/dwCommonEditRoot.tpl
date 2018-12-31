

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