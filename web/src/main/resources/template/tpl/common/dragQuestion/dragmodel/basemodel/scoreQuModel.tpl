<li id="scoreQuModel">
	<!-- 评分题模板 -->
	<div class="dwToolbar_icon">
		<img src="../../assets/images/question/question_score_icon.png"/>
		<span>评分题</span>
	</div>
	<div class="dwQuTypeModel">
		<div class="surveyQuItemBody quDragBody">
			<div class="initLine"></div>
			<div class="quInputCase" style="display: none;">
				<input type="hidden" name="quType" value="SCORE" >
				<input type="hidden" name="quId" value="">
				<input type="hidden" name="orderById" value="0"/>
				<input type="hidden" name="saveTag" value="0">
				<input type="hidden" name="hoverTag" value="0">
				<input type="hidden" name="isRequired" value="1">
				<input type="hidden" name="hv" value="2">
				<input type="hidden" name="randOrder" value="0">
				<input type="hidden" name="cellCount" value="0">
				<input type="hidden" name="paramInt01" value="1">
				<input type="hidden" name="paramInt02" value="5">
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
					<div class="quCoItem">
						<table class="quCoItemTable" cellpadding="0" cellspacing="0">
							<tr class="quScoreOptionTr">
								<td class="quCoItemTableTd quOptionEditTd">
									<label class="editAble quCoOptionEdit">分项1</label>
									<div class="quItemInputCase"><input type="hidden" name="quItemId" value=""><input type="hidden" name="quItemSaveTag" value="0"></div>
								</td>
								<td class="quCoItemTableTd"><table class="scoreNumTable"><tr><td>1</td><td>2</td><td>3</td><td>4</td><td>5</td></tr></table></td>
								<td class="quCoItemTableTd">分</td>
							</tr>
							<tr class="quScoreOptionTr">
								<td class="quCoItemTableTd quOptionEditTd">
									<label class="editAble quCoOptionEdit">分项2</label>
									<div class="quItemInputCase"><input type="hidden" name="quItemId" value=""><input type="hidden" name="quItemSaveTag" value="0"></div>
								</td>
								<td class="quCoItemTableTd"><table class="scoreNumTable"><tr><td>1</td><td>2</td><td>3</td><td>4</td><td>5</td></tr></table></td>
								<td class="quCoItemTableTd">分</td>
							</tr>
						</table>
					</div>
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