<li id="cityQuModel">
	<!--城市模板 -->
	<div class="dwToolbar_icon">
		<img src="../../assets/images/question/question_city_icon.png"/>
		<span>城市</span>
	</div>
	<div class="dwQuTypeModel">
		<div class="surveyQuItemBody quDragBody">
			<div class="initLine"></div>
			<div class="quInputCase" style="display: none;">
				<input type="hidden" name="quType" value="FILLBLANK" >
				<input type="hidden" name="quId" value="">
				<input type="hidden" name="orderById" value="0"/>
				<input type="hidden" name="saveTag" value="0">
				<input type="hidden" name="hoverTag" value="0">
				<input type="hidden" name="isRequired" value="1">
				<input type="hidden" name="hv" value="2">
				<input type="hidden" name="randOrder" value="0">
				<input type="hidden" name="cellCount" value="0">
				<input type="hidden" name="checkType" value="NO">
				<input type="hidden" name="answerInputWidth" value="300">
				<input type="hidden" name="answerInputRow" value="1">
				<input type="hidden" name="contactsAttr" value="1">
				<input type="hidden" name="contactsField" value="8">
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
						<div class="editAble quCoTitleEdit" >您所在的城市？</div>
						<input type="hidden" name="quTitleSaveTag" value="0">
					</div>
					<div class="quCoItem">
						<ul>
							<li class="quCoItemUlLi">
								<!-- <input type="text" style="width:200px;padding:5px;"> -->
								<!-- <div class="quFillblankItem">
									<input type="text" style="width:200px;padding:5px;">
									<div class="dwComEditMenuBtn" ></div>
								</div> -->
								<select style="padding: 5px;" class="hat_province"><option>--选择省--</option> </select>
								<select style="padding: 5px;" class="hat_city"><option>--选择市--</option> </select>
								<select style="padding: 5px;" class="hat_area"><option>--选择区--</option> </select>
							</li>
						</ul>
					</div>
				</div>
			</div>
		</div>
	</div>
</li>