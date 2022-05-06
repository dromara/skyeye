{{#bean}}
	<div id="dw_body_left">
		<div class="dw_body_title" style="text-align: center;">设计目录</div>
		<div id="dwBodyLeftContent">
			{{#each questionLeftList}}
				{{#if quType}}
				{{#compareShowLeft quType}}
					<h2 class="">
						<a href="#{{id}}" class="ellipsis" toid="{{id}}">{{showQuestionLeftIndex quType}}、{{quTitle}}</a>
						（<input class="exam-fraction" value="{{fraction}}" toid="{{id}}"/>分）
					</h2>
					{{else}}
				{{/compareShowLeft}}
				{{/if}}
			{{/each}}
		</div>
	</div>
	<div id="dw_body_content">
		<div id="dwSurveyTitle">
			<div id="dwSurveyName" class="editAble dwSvyName">{{surveyName}}</div>
		</div>
		<input type="hidden" name="svyNmSaveTag" value="1">
		<div id="dwSurveyNote">
			<div id="dwSurveyNoteEdit" class="editAble dwSvyNoteEdit">{{surveyNote}}</div>
			<input type="hidden" name="svyNoteSaveTag" value="1">
		</div>
{{/bean}}
		<div id="dwSurveyQuContent" style="min-height: 500px;">
			<ul id="dwSurveyQuContentAppUl">
				{{#each rows}}
					<li class="li_surveyQuItemBody">
						<div class="surveyQuItemBody" checkNameIn="{{id}}">
							<div class="initLine"></div>
							<!-- 题目基础信息 -->
							<div class="quInputCase" style="display: none;">
								<input type="hidden" name="quType" value="{{quTypeName}}">
								<input type="hidden" name="quId" value="{{id}}">
								<input type="hidden" name="orderById" value="{{orderById}}" />
								<input type="hidden" name="saveTag" value="{{saveTag}}">
								<input type="hidden" name="fraction" value="{{fraction}}">
								<input type="hidden" name="hoverTag" value="0">
								<input type="hidden" name="hv" value="{{hv}}">
								<input type="hidden" name="randOrder" value="{{randOrder}}">
								<input type="hidden" name="cellCount" value="{{cellCount}}">
								<!-- 单选，多选 -->
								<input type="hidden" name="contactsAttr" value="{{contactsAttr}}">
								<input type="hidden" name="contactsField" value="{{contactsField}}">
								<!-- 多项填空题，评分题 -->
								<input type="hidden" name="paramInt01" value="{{paramInt01}}">
								<input type="hidden" name="paramInt02" value="{{paramInt02}}">
								<!-- 填空题 -->
								<input type="hidden" name="answerInputWidth" value="{{answerInputWidth}}">
								<input type="hidden" name="answerInputRow" value="{{answerInputRow}}">
								<input type="hidden" name="contactsAttr" value="{{contactsAttr}}">
								<input type="hidden" name="contactsField" value="{{contactsField}}">
								<div class="quLogicInputCase">
									<input type="hidden" name="quLogicItemNum" value="{{questionLogic.length}}"> 
									{{#each questionLogic}}
									<div class="quLogicItem quLogicItem_{{showIndex @index}}">
										<input type="hidden" name="quLogicId" value="{{id}}" />
										<input type="hidden" name="cgQuItemId" value="{{cgQuItemId}}" />
										<input type="hidden" name="skQuId" value="{{skQuId}}" />
										<input type="hidden" name="visibility" value="1">
										<input type="hidden" name="logicSaveTag" value="1">
										<input type="hidden" name="geLe" value="{{geLe}}">
										<input type="hidden" name="scoreNum" value="{{scoreNum}}">
										<input type="hidden" name="logicType" value="{{logicType}}">
									</div>
									{{/each}}
								</div>
							</div>
							{{#showQuestion quType @index}}{{/showQuestion}}
						</div>
					</li>
				{{/each}}
			</ul>
		</div>
	</div>
