{{#bean}}
	<div id="dw_body_left">
		<div class="dw_body_title" style="text-align: center;">设计目录</div>
		<div id="dwBodyLeftContent">
			<h2 class=""><a href="" class="ellipsis">欢迎页</a></h2>
			<div>
				<div>
					<h2 class=""><a href="" class="ellipsis">问卷页</a></h2>
				</div>
				<div style="padding-left: 5px;">
					<h2 class=""><a href="" class="ellipsis">1、请问你的年级是？</a></h2>
					<h2 class=""><a href="" class="ellipsis">2、请问你的年级是？</a></h2>
					<h2 class=""><a href="" class="ellipsis">3、请问你的年级是？</a></h2>
					<h2 class=""><a href="" class="ellipsis">4、请问你的年级是？</a></h2>
					<h2 class=""><a href="" class="ellipsis">下一页</a></h2>
					<h2 class=""><a href="" class="ellipsis">5、请问你的年级是？</a></h2>
					<h2 class=""><a href="" class="ellipsis">6、请问你的年级是级是级是？</a></h2>
				</div>
			</div>
			<h2 class=""><a href="" class="ellipsis">结束页</a></h2>
		</div>
	</div>
	<div id="dw_body_right" style="display: none;">
		<div class="dw_body_title">题目推荐</div>
	</div>
	<div id="dw_body_content">
		<div id="dwSurveyTitle">
			<div id="dwSurveyName" class="editAble dwSvyName">{{surveyName}}</div>
		</div>
		<input type="hidden" name="svyNmSaveTag" value="1">
		<div id="dwSurveyNote">
			<div id="dwSurveyNoteTools">参考样例</div>
			<div id="dwSurveyNoteEdit" class="editAble dwSvyNoteEdit">{{surveyNote}}</div>
			<input type="hidden" name="svyNoteSaveTag" value="1">
		</div>
{{/bean}}
		<div id="dwSurveyQuContent" style="min-height: 500px;">
			<ul id="dwSurveyQuContentAppUl">
				{{#each rows}}
					{{#if quType}}
					{{#compare1 quType '1'}}<!-- radio -->
    						<div class="surveyQuItemBody">
								<div class="initLine"></div>
								<div class="quInputCase" style="display: none;">
									<input type="hidden" name="quType" value="RADIO">
									<input type="hidden" name="quId" value="{{id}}">
									<input type="hidden" name="orderById" value="{{en.orderById}}" />
									<input type="hidden" name="saveTag" value="1">
									<input type="hidden" name="hoverTag" value="0">
									<input type="hidden" name="isRequired" value="{{isRequired}}">
									<input type="hidden" name="hv" value="{{hv}}">
									<input type="hidden" name="randOrder" value="{{randOrder}}">
									<input type="hidden" name="cellCount" value="{{cellCount}}">
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
								<div class="surveyQuItem">
									<div class="surveyQuItemLeftTools">
										<ul class="surveyQuItemLeftToolsUl">
											<li title="移动排序" class="dwQuMove">
												<div class="dwQuIcon"></div>
											</li>
											<li title="设置" class="dwQuSet">
												<div class="dwQuIcon"></div>
											</li>
											<li title="逻辑" class="dwQuLogic">
												<div class="dwQuIcon">
													<div class="quLogicInfo">{{questionLogic.length}}</div>
												</div>
											</l}}
											<li title="删除" class="dwQuDelete">
												<div class="dwQuIcon"></div>
											</li>
										</ul>
									</div>
							
									<div class="surveyQuItemRightTools">
										<ul class="surveyQuItemRightToolsUl">
											<li class="questionUp">
												<div class="dwQuIcon"></div>
											</li>
											<li class="questionDown">
												<div class="dwQuIcon"></div>
											</li>
										</ul>
									</div>
									<div class="surveyQuItemContent">
										<div class="quCoTitle">
											<div class="quCoNum">{{showIndex @index}}、</div>
											<div class="editAble quCoTitleEdit">{{quTitle}}</div>
											<input type="hidden" name="quTitleSaveTag" value="1">
										</div>
										<div class="quCoItem">
											{{#if hv}}
											{{#compare1 hv '3'}}
													<table class='tableQuColItem'>
														{{#each questionRadio}}
															<tr>
																{{#cellCount001 cellCount}}{{/cellCount001}}
															</tr>
														{{/each}}
													</table>
												{{else}}
													{{#if hv}}
													{{#compare1 hv '1'}}
															<ul class="transverse">
																{{#each questionRadio}}
																	<li class="quCoItemUlLi">
																		<input type="radio"><label class="editAble quCoOptionEdit">{{optionName}}</label>
																		<input type='text' class='optionInpText' style="{{#compare2 isNote 0}}{{/compare2}}" />
																		<div class="quItemInputCase">
																			<input type="hidden" name="quItemId" value="{{id}}"><input type="hidden" name="quItemSaveTag" value="1">
																			<input type="hidden" name="isNote" value="{{isNote}}">
																			<input type="hidden" name="checkType" value="{{checkType}}">
																			<input type="hidden" name="isRequiredFill" value="{{isRequiredFill}}">
																		</div>
																	</li>
																{{/each}}
															</ul>
														{{else}}
															<ul>
																{{#each questionRadio}}
																	<li class="quCoItemUlLi">
																		<input type="radio"><label class="editAble quCoOptionEdit">{{optionName}}</label>
																		<input type='text' class='optionInpText' style="{{#compare2 isNote 0}}{{/compare2}}" />
																		<div class="quItemInputCase">
																			<input type="hidden" name="quItemId" value="{{id}}"><input type="hidden" name="quItemSaveTag" value="1">
																			<input type="hidden" name="isNote" value="{{isNote}}">
																			<input type="hidden" name="checkType" value="{{checkType}}">
																			<input type="hidden" name="isRequiredFill" value="{{isRequiredFill}}">
																		</div>
																	</li>
																{{/each}}
															</ul>
													{{/compare1}}
													{{/if}}
											{{/compare1}}
											{{/if}}
										</div>
										<div class="quCoBottomTools">
											<ul class="quCoBottomToolsUl">
												<li class="addOption" title="添加">
													<div class="dwQuIcon"></div>
												</li>
												<li class="addMoreOption" title="批量添加">
													<div class="dwQuIcon"></div>
												</li>
											</ul>
										</div>
									</div>
								</div>
							</div>
    					{{else}}
    						{{#if quType}}
							{{#compare1 quType '2'}}<!-- checkbox -->
									<div class="surveyQuItemBody">
										<div class="initLine"></div>
										<div class="quInputCase" style="display: none;">
											<input type="hidden" name="quType" value="CHECKBOX">
											<input type="hidden" name="quId" value="{{id}}">
											<input type="hidden" name="orderById" value="{{orderById}}" />
											<input type="hidden" name="saveTag" value="1">
											<input type="hidden" name="hoverTag" value="0">
											<input type="hidden" name="isRequired" value="{{isRequired}}">
											<input type="hidden" name="hv" value="{{hv}}">
											<input type="hidden" name="randOrder" value="{{randOrder}}">
											<input type="hidden" name="cellCount" value="{{cellCount}}">
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
										<div class="surveyQuItem">
											<div class="surveyQuItemLeftTools">
												<ul class="surveyQuItemLeftToolsUl">
													<li title="移动排序" class="dwQuMove">
														<div class="dwQuIcon"></div>
													</li>
													<li title="设置" class="dwQuSet">
														<div class="dwQuIcon"></div>
													</li>
													<li title="逻辑" class="dwQuLogic">
														<div class="dwQuIcon">
															<div class="quLogicInfo">{{questionLogic.length}}</div>
														</div>
													</li>
													<li title="删除" class="dwQuDelete">
														<div class="dwQuIcon"></div>
													</li>
												</ul>
											</div>
											<div class="surveyQuItemRightTools">
												<ul class="surveyQuItemRightToolsUl">
													<li class="questionUp">
														<div class="dwQuIcon"></div>
													</li>
													<li class="questionDown">
														<div class="dwQuIcon"></div>
													</li>
												</ul>
											</div>
											<div class="surveyQuItemContent">
												<div class="quCoTitle">
													<div class="quCoNum">{{showIndex @index}}、</div>
													<div class="editAble quCoTitleEdit">{{quTitle}}</div>
													<input type="hidden" name="quTitleSaveTag" value="1">
												</div>
												<div class="quCoItem">
													{{#if hv}}
													{{#compare1 hv '3'}}
															<table class='tableQuColItem'>
																{{#each questionCheckBox}}
																	<tr>
																		{{#cellCount002 cellCount}}{{/cellCount002}}
																	</tr>
																{{/each}}
															</table>
														{{else}}
															{{#if hv}}
															{{#compare1 hv '1'}}
																	<ul class="transverse">
																		{{#each questionCheckBox}}
																			<li class="quCoItemUlLi">
																				<input type="checkbox"><label class="editAble quCoOptionEdit">{{optionName}}</label>
																				<input type='text' class='optionInpText' style="{{#compare2 isNote 0}}{{/compare2}}" />
																				<div class="quItemInputCase">
																					<input type="hidden" name="quItemId" value="{{id}}"><input type="hidden" name="quItemSaveTag" value="1">
																					<input type="hidden" name="isNote" value="{{isNote}}">
																					<input type="hidden" name="checkType" value="{{checkType}}">
																					<input type="hidden" name="isRequiredFill" value="{{isRequiredFill}}">
																				</div>
																			</li>
																		{{/each}}
																	</ul>
																{{else}}
																	<ul>
																		{{#each questionCheckBox}}
																			<li class="quCoItemUlLi">
																				<input type="checkbox"><label class="editAble quCoOptionEdit">{{optionName}}</label>
																				<input type='text' class='optionInpText' style="{{#compare2 isNote 0}}{{/compare2}}" />
																				<div class="quItemInputCase">
																					<input type="hidden" name="quItemId" value="{{id}}"><input type="hidden" name="quItemSaveTag" value="1">
																					<input type="hidden" name="isNote" value="{{isNote}}">
																					<input type="hidden" name="checkType" value="{{checkType}}">
																					<input type="hidden" name="isRequiredFill" value="{{isRequiredFill}}">
																				</div>
																			</li>
																		{{/each}}
																	</ul>
															{{/compare1}}
															{{/if}}
													{{/compare1}}
													{{/if}}
												</div>
												<div class="quCoBottomTools">
													<ul class="quCoBottomToolsUl">
														<li class="addOption" title="添加">
															<div class="dwQuIcon"></div>
														</li>
														<li class="addMoreOption" title="批量添加">
															<div class="dwQuIcon"></div>
														</li>
													</ul>
												</div>
											</div>
										</div>
									</div>
								{{else}}
									{{#if quType}}
									{{#compare1 quType '3'}}<!-- fillblank -->
											<div class="surveyQuItemBody">
												<div class="initLine"></div>
												<div class="quInputCase" style="display: none;">
													<input type="hidden" name="quType" value="FILLBLANK">
													<input type="hidden" name="quId" value="{{id}}">
													<input type="hidden" name="orderById" value="{{orderById}}" />
													<input type="hidden" name="saveTag" value="1">
													<input type="hidden" name="hoverTag" value="0">
													<input type="hidden" name="isRequired" value="{{isRequired}}">
													<input type="hidden" name="hv" value="{{hv}}">
													<input type="hidden" name="randOrder" value="{{randOrder}}">
													<input type="hidden" name="cellCount" value="{{cellCount}}">
													<input type="hidden" name="checkType" value="{{checkType}}">
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
												<div class="surveyQuItem">
													<div class="surveyQuItemLeftTools">
														<ul class="surveyQuItemLeftToolsUl">
															<li title="移动排序" class="dwQuMove">
																<div class="dwQuIcon"></div>
															</li>
															<li title="设置" class="dwQuSet">
																<div class="dwQuIcon"></div>
															</li>
															<li title="逻辑" class="dwQuLogic">
																<div class="dwQuIcon">
																	<div class="quLogicInfo">{{questionLogic.length}}</div>
																</div>
															</li>
															<li title="删除" class="dwQuDelete">
																<div class="dwQuIcon"></div>
															</li>
														</ul>
													</div>
													<div class="surveyQuItemRightTools">
														<ul class="surveyQuItemRightToolsUl">
															<li class="questionUp">
																<div class="dwQuIcon"></div>
															</li>
															<li class="questionDown">
																<div class="dwQuIcon"></div>
															</li>
														</ul>
													</div>
													<div class="surveyQuItemContent">
														<div class="quCoTitle">
															<div class="quCoNum">{{showIndex @index}}、</div>
															<div class="editAble quCoTitleEdit">{{quTitle}}</div>
															<input type="hidden" name="quTitleSaveTag" value="1">
														</div>
														<div class="quCoItem">
															<ul>
																<li class="quCoItemUlLi">
																	<div class="quFillblankItem">
																		{{#if answerInputRow}}
																			{{#compare1 answerInputRow '1'}}
																					<input type="text" style="width:{{answerInputWidth}}px;padding:5px;" class="quFillblankAnswerInput">
																					<textarea rows="{{answerInputRow}}" style="width:{{answerInputWidth}}px;display: none;" class="quFillblankAnswerTextarea"></textarea>
																				{{else}}
																					<input type="text" style="width:{{answerInputWidth}}px;padding:5px;display: none;" class="quFillblankAnswerInput">
																					<textarea rows="{{answerInputRow}}" style="width:{{answerInputWidth}}px;" class="quFillblankAnswerTextarea"></textarea>
																			{{/compare1}}
																		{{/if}}
																		<div class="dwFbMenuBtn"></div>
																	</div>
																</li>
															</ul>
														</div>
													</div>
												</div>
											</div>
										{{else}}
											{{#if quType}}
											{{#compare1 quType '9'}}<!-- orderby -->
													<div class="surveyQuItemBody">
														<div class="initLine"></div>
														<div class="quInputCase" style="display: none;">
															<input type="hidden" name="quType" value="ORDERQU">
															<input type="hidden" name="quId" value="{{id}}">
															<input type="hidden" name="orderById" value="{{orderById}}" />
															<input type="hidden" name="saveTag" value="1">
															<input type="hidden" name="hoverTag" value="0">
															<input type="hidden" name="isRequired" value="{{isRequired}}">
															<input type="hidden" name="hv" value="{{hv}}">
															<input type="hidden" name="randOrder" value="{{randOrder}}">
															<input type="hidden" name="cellCount" value="{{cellCount}}">
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
														<div class="surveyQuItem">
															<div class="surveyQuItemLeftTools">
																<ul class="surveyQuItemLeftToolsUl">
																	<li title="移动排序" class="dwQuMove">
																		<div class="dwQuIcon"></div>
																	</li>
																	<li title="设置" class="dwQuSet">
																		<div class="dwQuIcon"></div>
																	</li>
																	<li title="删除" class="dwQuDelete">
																		<div class="dwQuIcon"></div>
																	</li>
																</ul>
															</div>
															<div class="surveyQuItemRightTools">
																<ul class="surveyQuItemRightToolsUl">
																	<li class="questionUp">
																		<div class="dwQuIcon"></div>
																	</li>
																	<li class="questionDown">
																		<div class="dwQuIcon"></div>
																	</li>
																</ul>
															</div>
															<div class="surveyQuItemContent">
																<div class="quCoTitle">
																	<div class="quCoNum">{{showIndex @index}}、</div>
																	<div class="editAble quCoTitleEdit">{{quTitle}}</div>
																	<input type="hidden" name="quTitleSaveTag" value="1">
																</div>
																<div class="quCoItem">
																	<div class="quOrderByLeft">
																		<ul>
																			{{#each questionOrderBy}}
																				<li class="quCoItemUlLi"><label class="editAble quCoOptionEdit">{{optionName}}</label>
																					<div class="quItemInputCase"><input type="hidden" name="quItemId" value="{{id}}"><input type="hidden" name="quItemSaveTag" value="1"></div>
																				</li>
																			{{/each}}
																		</ul>
																	</div>
																	<div class="quOrderByRight">
																		<table class="quOrderByTable">
																			{{#each questionOrderBy}}
																				<tr>
																					<td class="quOrderyTableTd">{{showIndex @index}}</td>
																					<td></td>
																				</tr>
																			{{/each}}
																		</table>
																	</div>
																	<div style="clear: both;"></div>
																</div>
																<div class="quCoBottomTools">
																	<ul class="quCoBottomToolsUl">
																		<li class="addOption" title="添加">
																			<div class="dwQuIcon"></div>
																		</li>
																		<li class="addMoreOption" title="批量添加">
																			<div class="dwQuIcon"></div>
																		</li>
																	</ul>
																</div>
															</div>
														</div>
													</div>
												{{else}}
													{{#if quType}}
													{{#compare1 quType '16'}}<!-- pagetag -->
															<div class="surveyQuItemBody">
																<div class="initLine"></div>
																<div class="quInputCase" style="display: none;">
																	<input type="hidden" name="quType" value="PAGETAG">
																	<input type="hidden" name="quId" value="{{id}}">
																	<input type="hidden" name="orderById" value="{{orderById}}" />
																	<input type="hidden" name="saveTag" value="1">
																	<input type="hidden" name="hoverTag" value="0">
																	<input type="hidden" name="isRequired" value="{{isRequired}}">
																	<input type="hidden" name="hv" value="{{hv}}">
																	<input type="hidden" name="randOrder" value="{{randOrder}}">
																	<input type="hidden" name="cellCount" value="{{cellCount}}">
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
																<div class="surveyQuItem">
																	<div class="surveyQuItemLeftTools">
																		<ul class="surveyQuItemLeftToolsUl">
																			<li title="删除" class="dwQuDelete">
																				<div class="dwQuIcon"></div>
																			</li>
																		</ul>
																	</div>
																	<div class="surveyQuItemRightTools" style="margin-top: 0px;">
																		<ul class="surveyQuItemRightToolsUl">
																			<li class="questionUp">
																				<div class="dwQuIcon"></div>
																			</li>
																			<li class="questionDown">
																				<div class="dwQuIcon"></div>
																			</li>
																		</ul>
																	</div>
																	<div class="pageBorderTop nohover"></div>
																	<div class="surveyQuItemContent" style="min-height: 10px;text-align: right;">
																		<div class="pageQuContent">下一页（1/2）</div>
																	</div>
																</div>
															</div>
														{{else}}
															{{#if quType}}
															{{#compare1 quType '17'}}<!-- paragraph -->
																	<div class="surveyQuItemBody">
																		<div class="initLine"></div>
																		<div class="quInputCase" style="display: none;">
																			<input type="hidden" name="quType" value="PARAGRAPH">
																			<input type="hidden" name="quId" value="{{id}}">
																			<input type="hidden" name="orderById" value="{{orderById}}" />
																			<input type="hidden" name="saveTag" value="1">
																			<input type="hidden" name="hoverTag" value="0">
																			<input type="hidden" name="isRequired" value="{{isRequired}}">
																			<input type="hidden" name="hv" value="{{hv}}">
																			<input type="hidden" name="randOrder" value="{{randOrder}}">
																			<input type="hidden" name="cellCount" value="{{cellCount}}">
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
																		<div class="surveyQuItem">
																			<div class="surveyQuItemLeftTools">
																				<ul class="surveyQuItemLeftToolsUl">
																					<li title="删除" class="dwQuDelete">
																						<div class="dwQuIcon"></div>
																					</li>
																				</ul>
																			</div>
																			<div class="surveyQuItemRightTools" style="margin-top: 5px;">
																				<ul class="surveyQuItemRightToolsUl">
																					<li class="questionUp">
																						<div class="dwQuIcon"></div>
																					</li>
																					<li class="questionDown">
																						<div class="dwQuIcon"></div>
																					</li>
																				</ul>
																			</div>
																			<div class="surveyQuItemContent" style="min-height: 45px;">
																				<div class="quCoTitle">
																					<div class="quCoNum" style="display: none;">{{showIndex @index}}、</div>
																					<div class="editAble quCoTitleEdit">{{quTitle}}</div>
																					<input type="hidden" name="quTitleSaveTag" value="1">
																				</div>
																			</div>
																		</div>
																	</div>
																{{else}}
																	{{#if quType}}
																	{{#compare1 quType '4'}}<!-- multi-fillblank -->
																			<div class="surveyQuItemBody">
																				<div class="initLine"></div>
																				<div class="quInputCase" style="display: none;">
																					<input type="hidden" name="quType" value="MULTIFILLBLANK">
																					<input type="hidden" name="quId" value="{{id}}">
																					<input type="hidden" name="orderById" value="{{orderById}}" />
																					<input type="hidden" name="saveTag" value="1">
																					<input type="hidden" name="hoverTag" value="0">
																					<input type="hidden" name="isRequired" value="{{isRequired}}">
																					<input type="hidden" name="hv" value="{{hv}}">
																					<input type="hidden" name="randOrder" value="{{randOrder}}">
																					<input type="hidden" name="cellCount" value="{{cellCount}}">
																					<input type="hidden" name="paramInt01" value="{{paramInt01}}">
																					<input type="hidden" name="paramInt02" value="{{paramInt02}}">
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
																				<div class="surveyQuItem">
																					<div class="surveyQuItemLeftTools">
																						<ul class="surveyQuItemLeftToolsUl">
																							<li title="移动排序" class="dwQuMove">
																								<div class="dwQuIcon"></div>
																							</li>
																							<li title="设置" class="dwQuSet">
																								<div class="dwQuIcon"></div>
																							</li>
																							<li title="逻辑" class="dwQuLogic">
																								<div class="dwQuIcon">
																									<div class="quLogicInfo">{{questionLogic.length}}</div>
																								</div>
																							</li>
																							<li title="删除" class="dwQuDelete">
																								<div class="dwQuIcon"></div>
																							</li>
																						</ul>
																					</div>
																					<div class="surveyQuItemRightTools">
																						<ul class="surveyQuItemRightToolsUl">
																							<li class="questionUp">
																								<div class=dwQuIcon></div>
																							</li>
																							<li class="questionDown">
																								<div class=dwQuIcon></div>
																							</li>
																						</ul>
																					</div>
																					<div class="surveyQuItemContent">
																						<div class="quCoTitle">
																							<div class="quCoNum">{{showIndex @index}}、</div>
																							<div class="editAble quCoTitleEdit">{{quTitle}}</div>
																							<input type="hidden" name="quTitleSaveTag" value="1">
																						</div>
																						<div class="quCoItem">
																							<table class="mFillblankTable" cellpadding="0" cellspacing="0">
																								{{#each questionMultiFillBlank}}
																									<tr class="mFillblankTableTr">
																										<td align="right" class="mFillblankTableEditTd"><label class="editAble quCoOptionEdit">{{optionName}}</label>
																											<div class="quItemInputCase"><input type="hidden" name="quItemId" value="{{id}}"><input type="hidden" name="quItemSaveTag" value="1"></div>
																										</td>
																										<td><input type="text" style="width:200px;padding:5px;"></td>
																									</tr>
																								{{/each}}
																							</table>
																						</div>
																						<div class="quCoBottomTools">
																							<ul class="quCoBottomToolsUl">
																								<li class="addOption" title="添加">
																									<div class="dwQuIcon"></div>
																								</li>
																								<li class="addMoreOption" title="批量添加">
																									<div class="dwQuIcon"></div>
																								</li>
																							</ul>
																						</div>
																					</div>
																				</div>
																			</div>
																		{{else}}
																			{{#if quType}}
																			{{#compare1 quType '11'}}<!-- chen-radio -->
																					<div class="surveyQuItemBody">
																						<div class="initLine"></div>
																						<div class="quInputCase" style="display: none;">
																							<input type="hidden" name="quType" value="CHENRADIO">
																							<input type="hidden" name="quId" value="{{id}}">
																							<input type="hidden" name="orderById" value="{{orderById}}" />
																							<input type="hidden" name="saveTag" value="1">
																							<input type="hidden" name="hoverTag" value="0">
																							<input type="hidden" name="isRequired" value="{{isRequired}}">
																							<input type="hidden" name="hv" value="{{hv}}">
																							<input type="hidden" name="randOrder" value="{{randOrder}}">
																							<input type="hidden" name="cellCount" value="{{cellCount}}">
																							<div class="quLogicInputCase">
																								<input type="hidden" name="quLogicItemNum" value="${fn:length(en.questionLogic)}}">
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
																						<div class="surveyQuItem">
																							<div class="surveyQuItemLeftTools">
																								<ul class="surveyQuItemLeftToolsUl">
																									<li title="移动排序" class="dwQuMove">
																										<div class="dwQuIcon"></div>
																									</li>
																									<li title="设置" class="dwQuSet">
																										<div class="dwQuIcon"></div>
																									</li>
																									<li title="逻辑" class="dwQuLogic">
																										<div class="dwQuIcon">
																											<div class="quLogicInfo">{{questionLogic.length}}</div>
																										</div>
																									</li>
																									<li title="删除" class="dwQuDelete">
																										<div class="dwQuIcon"></div>
																									</li>
																								</ul>
																							</div>
																							<div class="surveyQuItemRightTools">
																								<ul class="surveyQuItemRightToolsUl">
																									<li class="questionUp">
																										<div class=dwQuIcon></div>
																									</li>
																									<li class="questionDown">
																										<div class=dwQuIcon></div>
																									</li>
																								</ul>
																							</div>
																							<div class="surveyQuItemContent">
																								<div class="quCoTitle">
																									<div class="quCoNum">{{showIndex @index}}、</div>
																									<div class="editAble quCoTitleEdit">{{quTitle}}</div>
																									<input type="hidden" name="quTitleSaveTag" value="1">
																								</div>
																								<div class="quCoItem">
																									<div class="quCoItemLeftChenTableDiv">
																										<table class="quCoChenTable">
																											<tr>
																												<td></td>
																												{{#each columns}}
																													<td class="quChenColumnTd"><label class="editAble quCoOptionEdit">{{optionName}}</label>
																														<div class="quItemInputCase"><input type="hidden" name="quItemId" value="{{id}}"><input type="hidden" name="quItemSaveTag" value="1"></div>
																													</td>
																												{{/each}}
																											</tr>
																											{{#each rows1}}
																												<tr class="quChenRowTr">
																													<td class="quChenRowTd"><label class="editAble quCoOptionEdit">{{optionName}}</label>
																														<div class="quItemInputCase"><input type="hidden" name="quItemId" value="{{id}}"><input type="hidden" name="quItemSaveTag" value="1"></div>
																													</td>
																													<c:forEach items="{{columns}}" var="columnItem">
																														<td><input type="radio"> </td>
																													</c:forEach>
																												</tr>
																											{{/each}}
																										</table>
																									</div>
																									<div class="quCoRightTools">
																										<ul class="quCoBottomToolsUl">
																											<li class="addColumnOption" title="添加">
																												<div class="dwQuIcon"></div>
																											</li>
																											<li class="addMoreColumnOption" title="批量添加">
																												<div class="dwQuIcon"></div>
																											</li>
																										</ul>
																									</div>
																								</div>
																								<div style="clear: both;"></div>
																								<div class="quCoBottomTools">
																									<ul class="quCoBottomToolsUl">
																										<li class="addRowOption" title="添加">
																											<div class="dwQuIcon"></div>
																										</li>
																										<li class="addMoreRowOption" title="批量添加">
																											<div class="dwQuIcon"></div>
																										</li>
																									</ul>
																								</div>
																							</div>
																						</div>
																					</div>
																				{{else}}
																					{{#if quType}}
																					{{#compare1 quType '13'}}<!-- chen-checkbox -->
																							<div class="surveyQuItemBody">
																								<div class="initLine"></div>
																								<div class="quInputCase" style="display: none;">
																									<input type="hidden" name="quType" value="CHENCHECKBOX">
																									<input type="hidden" name="quId" value="{{id}}">
																									<input type="hidden" name="orderById" value="{{orderById}}" />
																									<input type="hidden" name="saveTag" value="1">
																									<input type="hidden" name="hoverTag" value="0">
																									<input type="hidden" name="isRequired" value="{{isRequired}}">
																									<input type="hidden" name="hv" value="{{hv}}">
																									<input type="hidden" name="randOrder" value="{{randOrder}}">
																									<input type="hidden" name="cellCount" value="{{cellCount}}">
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
																								<div class="surveyQuItem">
																									<div class="surveyQuItemLeftTools">
																										<ul class="surveyQuItemLeftToolsUl">
																											<li title="移动排序" class="dwQuMove">
																												<div class="dwQuIcon"></div>
																											</li>
																											<li title="设置" class="dwQuSet">
																												<div class="dwQuIcon"></div>
																											</li>
																											<li title="逻辑" class="dwQuLogic">
																												<div class="dwQuIcon">
																													<div class="quLogicInfo">{{questionLogic.length}}</div>
																												</div>
																											</li>
																											<li title="删除" class="dwQuDelete">
																												<div class="dwQuIcon"></div>
																											</li>
																										</ul>
																									</div>
																									<div class="surveyQuItemRightTools">
																										<ul class="surveyQuItemRightToolsUl">
																											<li class="questionUp">
																												<div class=dwQuIcon></div>
																											</li>
																											<li class="questionDown">
																												<div class=dwQuIcon></div>
																											</li>
																										</ul>
																									</div>
																									<div class="surveyQuItemContent">
																										<div class="quCoTitle">
																											<div class="quCoNum">{{showIndex @index}}、</div>
																											<div class="editAble quCoTitleEdit">{{quTitle}}</div>
																											<input type="hidden" name="quTitleSaveTag" value="1">
																										</div>
																										<div class="quCoItem">
																											<div class="quCoItemLeftChenTableDiv">
																												<table class="quCoChenTable">
																													<tr>
																														<td></td>
																														{{#each columns}}
																															<td class="quChenColumnTd"><label class="editAble quCoOptionEdit">{{optionName}}</label>
																																<div class="quItemInputCase"><input type="hidden" name="quItemId" value="{{id}}"><input type="hidden" name="quItemSaveTag" value="1"></div>
																															</td>
																														{{/each}}
																													</tr>
																													{{#each rows1}}
																														<tr class="quChenRowTr">
																															<td class="quChenRowTd"><label class="editAble quCoOptionEdit">{{optionName}}</label>
																																<div class="quItemInputCase"><input type="hidden" name="quItemId" value="{{id}}"><input type="hidden" name="quItemSaveTag" value="1"></div>
																															</td>
																															<c:forEach items="{{columns}}" var="columnItem">
																																<td><input type="checkbox"> </td>
																															</c:forEach>
																														</tr>
																													{{/each}}
																												</table>
																											</div>
																											<div class="quCoRightTools">
																												<ul class="quCoBottomToolsUl">
																													<li class="addColumnOption" title="添加">
																														<div class="dwQuIcon"></div>
																													</li>
																													<li class="addMoreColumnOption" title="批量添加">
																														<div class="dwQuIcon"></div>
																													</li>
																												</ul>
																											</div>
																										</div>
																										<div style="clear: both;"></div>
																										<div class="quCoBottomTools">
																											<ul class="quCoBottomToolsUl">
																												<li class="addRowOption" title="添加">
																													<div class="dwQuIcon"></div>
																												</li>
																												<li class="addMoreRowOption" title="批量添加">
																													<div class="dwQuIcon"></div>
																												</li>
																											</ul>
																										</div>
																									</div>
																								</div>
																							</div>
																						{{else}}
																							{{#if quType}}
																							{{#compare1 quType '12'}}<!-- chen-fbk -->
																									<div class="surveyQuItemBody">
																										<div class="initLine"></div>
																										<div class="quInputCase" style="display: none;">
																											<input type="hidden" name="quType" value="CHENFBK">
																											<input type="hidden" name="quId" value="{{id}}">
																											<input type="hidden" name="orderById" value="{{orderById}}" />
																											<input type="hidden" name="saveTag" value="1">
																											<input type="hidden" name="hoverTag" value="0">
																											<input type="hidden" name="isRequired" value="{{isRequired}}">
																											<input type="hidden" name="hv" value="{{hv}}">
																											<input type="hidden" name="randOrder" value="{{randOrder}}">
																											<input type="hidden" name="cellCount" value="{{cellCount}}">
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
																										<div class="surveyQuItem">
																											<div class="surveyQuItemLeftTools">
																												<ul class="surveyQuItemLeftToolsUl">
																													<li title="移动排序" class="dwQuMove">
																														<div class="dwQuIcon"></div>
																													</li>
																													<li title="设置" class="dwQuSet">
																														<div class="dwQuIcon"></div>
																													</li>
																													<li title="逻辑" class="dwQuLogic">
																														<div class="dwQuIcon">
																															<div class="quLogicInfo">{{questionLogic.length}}</div>
																														</div>
																													</li>
																													<li title="删除" class="dwQuDelete">
																														<div class="dwQuIcon"></div>
																													</li>
																												</ul>
																											</div>
																											<div class="surveyQuItemRightTools">
																												<ul class="surveyQuItemRightToolsUl">
																													<li class="questionUp">
																														<div class=dwQuIcon></div>
																													</li>
																													<li class="questionDown">
																														<div class=dwQuIcon></div>
																													</li>
																												</ul>
																											</div>
																											<div class="surveyQuItemContent">
																												<div class="quCoTitle">
																													<div class="quCoNum">{{showIndex @index}}、</div>
																													<div class="editAble quCoTitleEdit">{{quTitle}}</div>
																													<input type="hidden" name="quTitleSaveTag" value="1">
																												</div>
																												<div class="quCoItem">
																													<div class="quCoItemLeftChenTableDiv">
																														<table class="quCoChenTable">
																															<tr>
																																<td></td>
																																{{#each columns}}
																																	<td class="quChenColumnTd"><label class="editAble quCoOptionEdit">{{optionName}}</label>
																																		<div class="quItemInputCase"><input type="hidden" name="quItemId" value="{{id}}"><input type="hidden" name="quItemSaveTag" value="1"></div>
																																	</td>
																																{{/each}}
																															</tr>
																															{{#each rows1}}
																																<tr class="quChenRowTr">
																																	<td class="quChenRowTd"><label class="editAble quCoOptionEdit">{{optionName}}</label>
																																		<div class="quItemInputCase"><input type="hidden" name="quItemId" value="{{id}}"><input type="hidden" name="quItemSaveTag" value="1"></div>
																																	</td>
																																	<c:forEach items="{{columns}}" var="columnItem">
																																		<td><input type="text"> </td>
																																	</c:forEach>
																																</tr>
																															{{/each}}
																														</table>
																													</div>
																													<div class="quCoRightTools">
																														<ul class="quCoBottomToolsUl">
																															<li class="addColumnOption" title="添加">
																																<div class="dwQuIcon"></div>
																															</li>
																															<li class="addMoreColumnOption" title="批量添加">
																																<div class="dwQuIcon"></div>
																															</li>
																														</ul>
																													</div>
																												</div>
																												<div style="clear: both;"></div>
																												<div class="quCoBottomTools">
																													<ul class="quCoBottomToolsUl">
																														<li class="addRowOption" title="添加">
																															<div class="dwQuIcon"></div>
																														</li>
																														<li class="addMoreRowOption" title="批量添加">
																															<div class="dwQuIcon"></div>
																														</li>
																													</ul>
																												</div>
																											</div>
																										</div>
																									</div>
																								{{else}}
																									{{#if quType}}
																									{{#compare1 quType '18'}}<!-- chen-score -->
																											<div class="surveyQuItemBody">
																												<div class="initLine"></div>
																												<div class="quInputCase" style="display: none;">
																													<input type="hidden" name="quType" value="CHENSCORE">
																													<input type="hidden" name="quId" value="{{id}}">
																													<input type="hidden" name="orderById" value="{{orderById}}" />
																													<input type="hidden" name="saveTag" value="1">
																													<input type="hidden" name="hoverTag" value="0">
																													<input type="hidden" name="isRequired" value="{{isRequired}}">
																													<input type="hidden" name="hv" value="{{hv}}">
																													<input type="hidden" name="randOrder" value="{{randOrder}}">
																													<input type="hidden" name="cellCount" value="{{cellCount}}">
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
																												<div class="surveyQuItem">
																													<div class="surveyQuItemLeftTools">
																														<ul class="surveyQuItemLeftToolsUl">
																															<li title="移动排序" class="dwQuMove">
																																<div class="dwQuIcon"></div>
																															</li>
																															<li title="设置" class="dwQuSet">
																																<div class="dwQuIcon"></div>
																															</li>
																															<li title="删除" class="dwQuDelete">
																																<div class="dwQuIcon"></div>
																															</li>
																														</ul>
																													</div>
																													<div class="surveyQuItemRightTools">
																														<ul class="surveyQuItemRightToolsUl">
																															<li class="questionUp">
																																<div class=dwQuIcon></div>
																															</li>
																															<li class="questionDown">
																																<div class=dwQuIcon></div>
																															</li>
																														</ul>
																													</div>
																													<div class="surveyQuItemContent">
																														<div class="quCoTitle">
																															<div class="quCoNum">{{showIndex @index}}、</div>
																															<div class="editAble quCoTitleEdit">{{quTitle}}</div>
																															<input type="hidden" name="quTitleSaveTag" value="1">
																														</div>
																														<div class="quCoItem">
																															<div class="quCoItemLeftChenTableDiv">
																																<table class="quCoChenTable">
																																	<tr>
																																		<td></td>
																																		{{#each columns}}
																																			<td class="quChenColumnTd"><label class="editAble quCoOptionEdit">{{optionName}}</label>
																																				<div class="quItemInputCase"><input type="hidden" name="quItemId" value="{{id}}"><input type="hidden" name="quItemSaveTag" value="1"></div>
																																			</td>
																																		{{/each}}
																																	</tr>
																																	{{#each rows1}}
																																		<tr class="quChenRowTr">
																																			<td class="quChenRowTd"><label class="editAble quCoOptionEdit">{{optionName}}</label>
																																				<div class="quItemInputCase"><input type="hidden" name="quItemId" value="{{id}}"><input type="hidden" name="quItemSaveTag" value="1"></div>
																																			</td>
																																			<c:forEach items="{{columns}}" var="columnItem">
																																				<td>
																											
																																				</td>
																																			</c:forEach>
																																		</tr>
																																	{{/each}}
																																</table>
																															</div>
																															<div class="quCoRightTools">
																																<ul class="quCoBottomToolsUl">
																																	<li class="addColumnOption" title="添加">
																																		<div class="dwQuIcon"></div>
																																	</li>
																																	<li class="addMoreColumnOption" title="批量添加">
																																		<div class="dwQuIcon"></div>
																																	</li>
																																</ul>
																															</div>
																														</div>
																														<div style="clear: both;"></div>
																														<div class="quCoBottomTools">
																															<ul class="quCoBottomToolsUl">
																																<li class="addRowOption" title="添加">
																																	<div class="dwQuIcon"></div>
																																</li>
																																<li class="addMoreRowOption" title="批量添加">
																																	<div class="dwQuIcon"></div>
																																</li>
																															</ul>
																														</div>
																													</div>
																												</div>
																											</div>
																										{{else}}
																											{{#if quType}}
																											{{#compare1 quType '8'}}<!-- score -->
																													<div class="surveyQuItemBody">
																														<div class="initLine"></div>
																														<div class="quInputCase" style="display: none;">
																															<input type="hidden" name="quType" value="SCORE">
																															<input type="hidden" name="quId" value="{{id}}">
																															<input type="hidden" name="orderById" value="{{orderById}}" />
																															<input type="hidden" name="saveTag" value="1">
																															<input type="hidden" name="hoverTag" value="0">
																															<input type="hidden" name="isRequired" value="{{isRequired}}">
																															<input type="hidden" name="hv" value="{{hv}}">
																															<input type="hidden" name="randOrder" value="{{randOrder}}">
																															<input type="hidden" name="cellCount" value="{{cellCount}}">
																															<input type="hidden" name="paramInt01" value="{{paramInt01}}">
																															<input type="hidden" name="paramInt02" value="{{paramInt02}}">
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
																														<div class="surveyQuItem">
																															<div class="surveyQuItemLeftTools">
																																<ul class="surveyQuItemLeftToolsUl">
																																	<li title="移动排序" class="dwQuMove">
																																		<div class="dwQuIcon"></div>
																																	</li>
																																	<li title="设置" class="dwQuSet">
																																		<div class="dwQuIcon"></div>
																																	</li>
																																	<li title="逻辑" class="dwQuLogic">
																																		<div class="dwQuIcon">
																																			<div class="quLogicInfo">{{questionLogic.length}}</div>
																																		</div>
																																	</li>
																																	<li title="删除" class="dwQuDelete">
																																		<div class="dwQuIcon"></div>
																																	</li>
																																</ul>
																															</div>
																															<div class="surveyQuItemRightTools">
																																<ul class="surveyQuItemRightToolsUl">
																																	<li class="questionUp">
																																		<div class=dwQuIcon></div>
																																	</li>
																																	<li class="questionDown">
																																		<div class=dwQuIcon></div>
																																	</li>
																																</ul>
																															</div>
																															<div class="surveyQuItemContent">
																																<div class="quCoTitle">
																																	<div class="quCoNum">{{showIndex @index}}、</div>
																																	<div class="editAble quCoTitleEdit">{{quTitle}}</div>
																																	<input type="hidden" name="quTitleSaveTag" value="1">
																																</div>
																																<div class="quCoItem">
																																	<table class="quCoItemTable" cellpadding="0" cellspacing="0">
																																		{{#each quScores}}
																																			<tr class="quScoreOptionTr">
																																				<td class="quCoItemTableTd quOptionEditTd">
																																					<label class="editAble quCoOptionEdit">{{optionName}}</label>
																																					<div class="quItemInputCase"><input type="hidden" name="quItemId" value="{{id}}"><input type="hidden" name="quItemSaveTag" value="1"></div>
																																				</td>
																																				<td class="quCoItemTableTd">
																																					<table class="scoreNumTable">
																																						<tr>
																																							{{#showParamInt02 paramInt02}}{{/showParamInt02}}
																																						</tr>
																																					</table>
																																				</td>
																																				<td class="quCoItemTableTd">分</td>
																																			</tr>
																																		{{/each}}
																																	</table>
																																</div>
																																<div class="quCoBottomTools">
																																	<ul class="quCoBottomToolsUl">
																																		<li class="addOption" title="添加">
																																			<div class="dwQuIcon"></div>
																																		</li>
																																		<li class="addMoreOption" title="批量添加">
																																			<div class="dwQuIcon"></div>
																																		</li>
																																	</ul>
																																</div>
																															</div>
																														</div>
																													</div>
																												{{else}}
																													
																											{{/compare1}}
																											{{/if}}
																									{{/compare1}}
																									{{/if}}
																							{{/compare1}}
																							{{/if}}
																					{{/compare1}}
																					{{/if}}
																			{{/compare1}}
																			{{/if}}
																	{{/compare1}}
																	{{/if}}
															{{/compare1}}
															{{/if}}
													{{/compare1}}
													{{/if}}
											{{/compare1}}
											{{/if}}
									{{/compare1}}
									{{/if}}
							{{/compare1}}
							{{/if}}
					{{/compare1}}
					{{/if}}
				{{/each}}
			</ul>
		</div>
	</div>
