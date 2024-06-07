<li id="checkboxQuModel">
	<!-- 多选题模板 -->
	<div class="dwToolbar_icon">
		<img src="../../assets/images/question/question_checkbox_icon.png"/>
		<span>多选题</span>
	</div>
	<div class="dwQuTypeModel">
		<div class="surveyQuItemBody quDragBody">
			<div class="initLine"></div>
			<div class="quInputCase" style="display: none;">
				<input type="hidden" name="quType" value="CHECKBOX" >
				<input type="hidden" name="quId" value="">
				<input type="hidden" name="orderById" value="0"/>
				<input type="hidden" name="saveTag" value="0">
				<input type="hidden" name="hoverTag" value="0">
				<input type="hidden" name="isRequired" value="1">
				<input type="hidden" name="hv" value="2">
				<input type="hidden" name="randOrder" value="0">
				<input type="hidden" name="cellCount" value="0">
				<input type="hidden" name="contactsAttr" value="0">
				<input type="hidden" name="contactsField" value="0">
				<div class="quLogicInputCase">
				<input type="hidden" name="quLogicItemNum" value="0">
				</div>
			</div>
			<div class="surveyQuItem">
				<div class="surveyQuItemLeftTools">
					<ul class="surveyQuItemLeftToolsUl">
						<li title="移动排序" class="dwQuMove"><div class="dwQuIcon"></div></li>
						<li title="设置" class="dwQuSet"><div class=dwQuIcon></div></li>
						<li title="逻辑" class="dwQuLogic"><div class="dwQuIcon"><div class="quLogicInfo"></div></div></li>
						<li title="删除" class="dwQuDelete"><div class="dwQuIcon"></div></li>
					</ul>
				</div>
				<div class="surveyQuItemRightTools">
					<ul class="surveyQuItemRightToolsUl">
						<li class="questionUp"><div class="dwQuIcon"></div></li>
						<li class="questionDown"><div class="dwQuIcon"></div></li>
					</ul>
				</div>
				<div class="surveyQuItemContent">
					<div class="quCoTitle">
						<div class="quCoNum">1、</div>
						<div class="editAble quCoTitleEdit" >题标题？</div>
						<input type="hidden" name="quTitleSaveTag" value="0">
					</div>
					<div class="quCoItem"><ul>
						<li class="quCoItemUlLi">
							<input type="checkbox"><label class="editAble quCoOptionEdit">选项1</label>
							<div class="quItemInputCase">
								<input type="hidden" name="quItemId" value="">
								<input type="hidden" name="quItemSaveTag" value="0">
								<input type="hidden" name="isNote" value="0">
								<input type="hidden" name="checkType" value="NO">
								<input type="hidden" name="isRequiredFill" value="0">
							</div>
						</li>
						<li class="quCoItemUlLi">
							<input type="checkbox"><label class="editAble quCoOptionEdit">选项2</label>
							<div class="quItemInputCase">
								<input type="hidden" name="quItemId" value="">
								<input type="hidden" name="quItemSaveTag" value="0">
								<input type="hidden" name="isNote" value="0">
								<input type="hidden" name="checkType" value="NO">
								<input type="hidden" name="isRequiredFill" value="0">
							</div>
						</li>
						<li class="quCoItemUlLi">
							<input type="checkbox"><label class="editAble quCoOptionEdit">选项3</label>
							<div class="quItemInputCase">
								<input type="hidden" name="quItemId" value="">
								<input type="hidden" name="quItemSaveTag" value="0">
								<input type="hidden" name="isNote" value="0">
								<input type="hidden" name="checkType" value="NO">
								<input type="hidden" name="isRequiredFill" value="0">
							</div>
						</li>
					</ul></div>
					<div class="quCoBottomTools" >
						<ul class="quCoBottomToolsUl" >
							<li class="addOption" title="添加"><div class="dwQuIcon"></div></li>
							<li class="addMoreOption" title="批量添加"><div class="dwQuIcon" ></div></li>
						</ul>
					</div>
				</div>
			</div>
		</div>
	</div>
</li>