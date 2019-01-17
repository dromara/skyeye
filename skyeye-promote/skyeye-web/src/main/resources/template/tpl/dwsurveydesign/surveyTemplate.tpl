{{#bean}}
	<input type="hidden" id="id" name="id" value="{{id}}">
	<form id="surveyForm" method="post" >
		<input type="hidden" id="surveyId" name="surveyId" value="{{id}}">
		<input type="hidden" id="sid" name="sid" value="{{sid}}">
		<div id="dw_body" style="padding-top:10px;">
			<div id="dw_body_content">
				<div id="dwSurveyHeader">
					<div id="dwSurveyTitle" class="noLogoImg">
						<div id="dwSurveyName" class="dwSvyName">{{{surveyName}}}</div>
					</div>
					<div>
						<div id="dwSurveyNoteEdit" class="">{{{surveyNote}}}</div>
					</div>
				</div>
{{/bean}}
				<div id="dwSurveyQuContent" style="min-height: 300px;">
					<div id="dwSurveyQuContentBg">
						<ul id="dwSurveyQuContentAppUl">
							{{#each rows}}
								<li class="li_surveyQuItemBody surveyQu_{{#compare6 quType}}{{/compare6}}" style="{{#compare5 quType}}{{/compare5}}">
								{{#if quType}}
								{{#compare1 quType '1'}}<!-- radio -->
			    						<div class="surveyQuItemBody">
											<div class="initLine"></div>
											<div class="quInputCase" style="display: none;">
												<input type="hidden" class="quType" value="RADIO" >
												<input type="hidden" class="quId" value="{{id}}"  >
												<input type="hidden" class="orderById" value="${orderById }"/>
												<input type="hidden" name="isRequired" value="{{isRequired}}">
												<input type="hidden" class="answerTag" value="0" >
												<div class="quLogicInputCase">
													{{#each questionLogic}}
														<div class="quLogicItem quLogicItem_{{showXhIndex @index}}">
															<input type="hidden" class="logicId" value="{{id}}" />
															<input type="hidden" class="cgQuItemId" value="{{cgQuItemId}}" />
															<input type="hidden" class="skQuId" value="{{skQuId}}" />
															<input type="hidden" class="logicType" value="{{logicType}}">
														</div>
													{{/each}}
												</div>
											</div>
											<div class="surveyQuItem">
												<div class="surveyQuItemContent">
													<div class="quCoTitle">
														<div class="quCoNum">{{showQuestionIndex quType}}、</div>
														<div class="quCoTitleEdit" >{{quTitle}}</div>
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
																<tr>
															</table>
														{{else}}
														{{#if hv}}
														{{#compare1 hv '1'}}
															<ul class="transverse">
																{{#each questionRadio}}
																	<li class="quCoItemUlLi">
																		<div class="dwQuOptionItemContent">
																			<label class="dwRedioStyle dwQuInputLabel" ></label>
																			<input type="radio" name="qu_{{quType}}_{{quId}}" value="{{id}}" >
																			<label class="quCoOptionEdit quCoOptionPadding">{{optionName}}</label>
																			<input type='text' class='inputSytle_1' style="width:200px;padding:5px;{{#compare2 isNote 0}}{{/compare2}}" name="text_qu_{{quType}}_{{quId}}_{{id}}" />
																		</div>
																	</li>
																{{/each}}
															</ul>
														{{else}}
															<ul>
																{{#each questionRadio}}
																	<li class="quCoItemUlLi">
																		<div class="dwQuOptionItemContent">
																			<label class="dwRedioStyle dwQuInputLabel" ></label>
																			<input type="radio" name="qu_{{quType}}_{{quId}}" value="{{id}}" >
																			<label class="quCoOptionEdit quCoOptionPadding">{{optionName}}</label>
																			<input type='text' class='inputSytle_1' style="width:200px;padding:5px;{{#compare2 isNote 0}}{{/compare2}}" name="text_qu_{{quType}}_{{quId}}_{{id}}" />
																		</div>
																	</li>
																{{/each}}
															</ul>
														{{/compare1}}
														{{/if}}
													{{/compare1}}
													{{/if}}
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
														<input type="hidden" class="quType" value="CHECKBOX">
														<input type="hidden" name="quId" value="{{id}}">
														<input type="hidden" name="orderById" value="{{orderById}}" />
														<input type="hidden" name="isRequired" value="{{isRequired}}">
														<input type="hidden" class="answerTag" value="0" >
														<div class="quLogicInputCase">
															{{#each questionLogic}}
																<div class="quLogicItem quLogicItem_{{showXhIndex @index}}">
																	<input type="hidden" class="logicId" value="{{id}}" />
																	<input type="hidden" class="cgQuItemId" value="{{cgQuItemId}}" />
																	<input type="hidden" class="skQuId" value="{{skQuId}}" />
																	<input type="hidden" class="logicType" value="{{logicType}}">
																</div>
															{{/each}}
														</div>
														<input type="hidden" name="qu_{{quType}}_{{id}}" value="tag_qu_{{quType}}_{{id}}_" />
													</div>
													<div class="surveyQuItem">
														<div class="surveyQuItemContent">
															<div class="quCoTitle">
																<div class="quCoNum">{{showQuestionIndex quType}}、</div>
																<div class="quCoTitleEdit" >{{quTitle}}</div>
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
																							<div class="dwQuOptionItemContent">
																								<label class="dwCheckboxStyle dwQuInputLabel" ></label>
																								<input type="checkbox" name="tag_qu_{{quType}}_{{quId}}_{{id}}" value="{{id}}" >
																								<label class="quCoOptionEdit quCoOptionPadding">{{optionName}}</label>
																								<input type='text' class='inputSytle_1' style="width:200px;padding:5px;{{#compare2 isNote 0}}{{/compare2}}" name="text_tag_qu_{{quType}}_{{quId}}_{{id}}" />
																							</div>
																						</li>
																					{{/each}}
																				</ul>
																			{{else}}
																				<ul>
																					{{#each questionCheckBox}}
																						<li class="quCoItemUlLi">
																							<div class="dwQuOptionItemContent">
																								<label class="dwCheckboxStyle dwQuInputLabel" ></label>
																								<input type="checkbox" name="tag_qu_{{quType}}_{{quId}}_{{id}}" value="{{id}}" >
																								<label class="quCoOptionEdit quCoOptionPadding">{{optionName}}</label>
																								<input type='text' class='inputSytle_1' style="width:200px;padding:5px;{{#compare2 isNote 0}}{{/compare2}}" name="text_tag_qu_{{quType}}_{{quId}}_{{id}}" />
																							</div>
																						</li>
																					{{/each}}
																				</ul>
																		{{/compare1}}
																		{{/if}}
																{{/compare1}}
																{{/if}}
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
																<input type="hidden" class="quType" value="FILLBLANK">
																<input type="hidden" class="quId" value="{{id}}"  >
																<input type="hidden" class="orderById" value="${orderById }"/>
																<input type="hidden" name="isRequired" value="{{isRequired}}">
																<input type="hidden" class="checkType" value="{{checkType}}">
																<input type="hidden" class="answerTag" value="0" >
																<div class="quLogicInputCase">
																	{{#each questionLogic}}
																		<div class="quLogicItem quLogicItem_{{showXhIndex @index}}">
																			<input type="hidden" class="logicId" value="{{id}}" />
																			<input type="hidden" class="cgQuItemId" value="{{cgQuItemId}}" />
																			<input type="hidden" class="skQuId" value="{{skQuId}}" />
																			<input type="hidden" class="logicType" value="{{logicType}}">
																		</div>
																	{{/each}}
																</div>
															</div>
															<div class="surveyQuItem">
																<div class="surveyQuItemContent">
																	<div class="quCoTitle">
																		<div class="quCoNum">{{showQuestionIndex quType}}、</div>
																		<div class="quCoTitleEdit" >{{quTitle}}</div>
																	</div>
																	<div class="quCoItem"><ul>
																		<li class="quCoItemUlLi">
																			<div class="quFillblankItem">
																				{{#if answerInputRow}}
																					{{#compare1 checkType '8'}}
																							<input type="text" name="qu_{{quType}}_{{id}}" style="width: 300px;padding: 6px 10px 5px;border: 1px solid #83ABCB;outline: none;" class="fillblankInput Wdate"/>
																						{{else}}
																							{{#compare1 answerInputRow '1'}}
																									<input type="text" name="qu_{{quType}}_{{id}}" style="width:{{answerInputWidth}}px;" class="inputSytle_1 fillblankInput" >
																								{{else}}
																									<textarea name="qu_{{quType}}_{{id}}" rows="{{answerInputRow}}" style="width:{{answerInputWidth}}px;"class="inputSytle_2 fillblankInput" ></textarea>
																							{{/compare1}}
																					{{/compare1}}
																				{{/if}}
																				<div class="dwComEditMenuBtn" ></div>
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
																		<input type="hidden" class="quType" value="ORDERQU">
																		<input type="hidden" name="quId" value="{{id}}">
																		<input type="hidden" name="orderById" value="{{orderById}}" />
																		<input type="hidden" name="isRequired" value="{{isRequired}}">
																		<input type="hidden" class="answerTag" value="0" >
																		<div class="quLogicInputCase">
																			{{#each questionLogic}}
																				<div class="quLogicItem quLogicItem_{{showXhIndex @index}}">
																					<input type="hidden" class="cgQuItemId" value="{{cgQuItemId}}" />
																					<input type="hidden" class="skQuId" value="{{skQuId}}" />
																				</div>
																			{{/each}}
																		</div>
																		<input type="hidden" name="qu_{{quType}}_{{id}}" value="item_qu_{{quType}}_{{id}}_" />
																	</div>
																	<div class="surveyQuItem">
																		<div class="surveyQuItemContent">
																			<div class="quCoTitle">
																				<div class="quCoNum">{{showQuestionIndex quType}}、</div>
																				<div class="quCoTitleEdit">{{quTitle}}</div>
																			</div>
																			<div class="quCoItem quOrderByCoItem">
																				<div class="quOrderByRight">
																					<table class="quOrderByTable" style="padding:5px;">
																						{{#each questionOrderBy}}
																							<tr class="quOrderByTableTr">
																								<td class="quOrderyTableTd">{{showXhIndex @index}}</td>
																								<td class="quOrderTabConnect"></td>
																							</tr>
																						{{/each}}
																					</table>
																				</div>
																				<div  class="quOrderByLeft">
																					<ul class="quOrderByLeftUl">
																						{{#each questionOrderBy}}
																							<li class="quCoItemUlLi">
																								<label class="quCoOptionEdit">{{optionName}}
																									<input name="item_qu_{{quType}}_{{quId}}_{{id}}" value="1" type="hidden" class="quOrderItemHidInput">
																								</label>
																							</li>
																						{{/each}}
																					</ul>
																				</div>
																				<div style="clear: both;"></div>
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
																				<input type="hidden" class="quType" value="PAGETAG">
																				<input type="hidden" name="quId" value="{{id}}">
																				<input type="hidden" name="orderById" value="{{orderById}}"/>
																				<input type="hidden" name="isRequired" value="{{isRequired}}">
																				<div class="quLogicInputCase">
																					{{#each questionLogic}}
																						<div class="quLogicItem quLogicItem_{{showXhIndex @index}}">
																							<input type="hidden" class="cgQuItemId" value="{{cgQuItemId}}" />
																							<input type="hidden" class="skQuId" value="{{skQuId}}" />
																						</div>
																					{{/each}}
																				</div>
																			</div>
																			<div class="surveyQuItem">
																				<div class="pageBorderTop nohover"></div>
																				<div class="surveyQuItemContent" style="padding-top: 12px;height: 30px;min-height: 30px;">
																					<a href="#" class="sbtn24 sbtn24_0 nextPage_a" >下一页</a>&nbsp;&nbsp;
																					{{#if pageNo}}
																					{{#compare7 pageNo}}
																						<a href="#" class="sbtn24 sbtn24_1 prevPage_a">上一页</a>
																						{{else}}
																					{{/compare7}}
																					{{/if}}
																					<input type="hidden" name="nextPageNo" value="{{pageNo}}">
																				</div>
																			</div>
																		</div>
																	{{else}}
																		{{#if quType}}
																		{{#compare1 quType '17'}}<!-- paragraph -->
																				<div class="surveyQuItemBody">
																					<div class="initLine"></div>
																					<div class="quInputCase" style="display: none;">
																						<input type="hidden" class="quType" value="PARAGRAPH">
																						<input type="hidden" name="quId" value="{{id}}">
																						<input type="hidden" name="orderById" value="{{orderById}}"/>
																						<input type="hidden" name="isRequired" value="{{isRequired}}">
																						<div class="quLogicInputCase">
																							{{#each questionLogic}}
																								<div class="quLogicItem quLogicItem_{{showXhIndex @index}}">
																									<input type="hidden" class="cgQuItemId" value="{{cgQuItemId}}" />
																									<input type="hidden" class="skQuId" value="{{skQuId}}" />
																								</div>
																							{{/each}}
																						</div>
																					</div>
																					<div class="surveyQuItem">
																						<div class="surveyQuItemContent" style="min-height: 35px;">
																							<div class="quCoTitle" style="background: rgb(243, 247, 247);">
																								<div class="quCoTitleEdit" style="padding-left: 15px;">{{quTitle}}</div>
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
																								<input type="hidden" class="quType" value="MULTIFILLBLANK">
																								<input type="hidden" name="quId" value="{{id}}">
																								<input type="hidden" name="orderById" value="{{orderById}}"/>
																								<input type="hidden" name="isRequired" value="{{isRequired}}">
																								<div class="quLogicInputCase">
																									{{#each questionLogic}}
																										<div class="quLogicItem quLogicItem_{{showXhIndex @index}}">
																											<input type="hidden" class="logicId" value="{{id}}" />
																											<input type="hidden" class="cgQuItemId" value="{{cgQuItemId}}" />
																											<input type="hidden" class="skQuId" value="{{skQuId}}" />
																											<input type="hidden" class="logicType" value="{{logicType}}">
																										</div>
																									{{/each}}
																								</div>
																								<input type="hidden" name="qu_{{quType}}_{{id}}" value="text_qu_{{quType}}_{{id}}_" />
																							</div>
																							<div class="surveyQuItem">
																								<div class="surveyQuItemContent">
																									<div class="quCoTitle">
																										<div class="quCoNum">{{showQuestionIndex quType}}、</div>
																										<div class="quCoTitleEdit" >{{quTitle}}</div>
																									</div>
																									<div class="quCoItem">
																										<table class="mFillblankTable" cellpadding="0" cellspacing="0">
																											{{#each questionMultiFillBlank}}
																												<tr class="mFillblankTableTr">
																													<td align="right" class="mFillblankTableEditTd"><label class="quCoOptionEdit">{{optionName}}</label>
																														<input class="dwMFillblankOptionId" type="hidden" value="{{id}}" disabled="disabled">
																														<input type="hidden" name="answerTag" value="0">
																													</td>
																													<td><input name="text_qu_{{quType}}_{{quId}}_{{id}}" type="text" style="width:200px;padding:5px;" class="inputSytle_1 dwMFillblankInput"></td>
																												</tr>
																											{{/each}}
																										</table>
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
																										<input type="hidden" class="quType" value="CHENRADIO">
																										<input type="hidden" name="quId" value="{{id}}">
																										<input type="hidden" name="orderById" value="{{orderById}}"/>
																										<input type="hidden" name="isRequired" value="{{isRequired}}">
																										<div class="quLogicInputCase">
																											{{#each questionLogic}}
																												<div class="quLogicItem quLogicItem_{{showXhIndex @index}}">
																													<input type="hidden" class="logicId" value="{{id}}" />
																													<input type="hidden" class="cgQuItemId" value="{{cgQuItemId}}" />
																													<input type="hidden" class="skQuId" value="{{skQuId}}" />
																													<input type="hidden" class="logicType" value="{{logicType}}">
																												</div>
																											{{/each}}
																										</div>
																										<input type="hidden" name="qu_{{quType}}_{{id}}" value="item_qu_{{quType}}_{{id}}_" />
																									</div>
																									<div class="surveyQuItem">
																										<div class="surveyQuItemContent">
																											<div class="quCoTitle">
																												<div class="quCoNum">{{showQuestionIndex quType}}、</div>
																												<div class="quCoTitleEdit">{{quTitle}}</div>
																											</div>
																											<div class="quCoItem">
																												<div class="quCoItemLeftChenTableDiv">
																													<table class="quCoChenTable" >
																														<tr><td></td>
																															{{#each questionChenColumn}}
																																<td class="quChenColumnTd">
																																	<label class="quCoOptionEdit">{{optionName}}</label>
																																</td>
																															{{/each}}
																														</tr>
																														{{#each questionChenRow}}
																															<tr class="dwQuCoChenRowTr">
																																<td class="quChenRowTd"><label class="quCoOptionEdit">{{optionName}}</label>
																																	<input type="hidden" class="answerTag" value="0" >
																																</td>
																																{{#each questionChenColumn}}
																																	<td>
																																		<div class="dwQuOptionItemContent">
																																			<label class="dwRedioStyle dwQuInputLabel" ></label>
																																			<input type="hidden" class="dwChenInputTemp" disabled="disabled" value="{{../id}}:{{id}}">
																																			<input name="item_qu_{{quType}}_{{quId}}_{{../id}}" value="{{id}}" type="radio"> 
																																		</div>
																																	</td>
																																{{/each}}
																															</tr>
																														{{/each}}
																													</table>
																												</div>
																											</div>
																											<div style="clear: both;"></div>
																										</div>
																									</div>
																								</div>
																							{{else}}
																								{{#if quType}}
																								{{#compare1 quType '13'}}<!-- chen-checkbox -->
																										<div class="surveyQuItemBody">
																											<div class="initLine"></div>
																											<div class="quInputCase" style="display: none;">
																												<input type="hidden" class="quType" value="CHENCHECKBOX">
																												<input type="hidden" name="quId" value="{{id}}">
																												<input type="hidden" name="orderById" value="{{orderById}}"/>
																												<input type="hidden" name="isRequired" value="{{isRequired}}">
																												<div class="quLogicInputCase">
																													{{#each questionLogic}}
																														<div class="quLogicItem quLogicItem_{{showXhIndex @index}}">
																															<input type="hidden" class="logicId" value="{{id}}" />
																															<input type="hidden" class="cgQuItemId" value="{{cgQuItemId}}" />
																															<input type="hidden" class="skQuId" value="{{skQuId}}" />
																															<input type="hidden" class="logicType" value="{{logicType}}">
																														</div>
																													{{/each}}
																												</div>
																												<input type="hidden" name="qu_{{quType}}_{{id}}" value="item_qu_{{quType}}_{{id}}_" />
																											</div>
																											<div class="surveyQuItem">
																												<div class="surveyQuItemContent">
																													<div class="quCoTitle">
																														<div class="quCoNum">{{showQuestionIndex quType}}、</div>
																														<div class="quCoTitleEdit" >{{quTitle}}</div>
																													</div>
																													<div class="quCoItem">
																														<div class="quCoItemLeftChenTableDiv">
																															<table class="quCoChenTable" >
																																<tr><td></td>
																																	{{#each questionChenColumn}}
																																		<td class="quChenColumnTd">
																																			<label class="quCoOptionEdit">{{optionName}}</label>
																																		</td>
																																	{{/each}}
																																</tr>
																																{{#each questionChenRow}}
																																	<tr class="dwQuCoChenRowTr">
																																		<td class="quChenRowTd"><label class="quCoOptionEdit">{{optionName}}</label>
																																			<input type="hidden" name="item_qu_{{quType}}_{{quId}}_{{id}}" value="ck_item_qu_{{quType}}_{{quId}}_{{id}}_" />
																																			<input type="hidden" class="answerTag" value="0" >
																																		</td>
																																		{{#each questionChenColumn}}
																																			<td>
																																				<div class="dwQuOptionItemContent">
																																					<label class="dwCheckboxStyle dwQuInputLabel" ></label>
																																					<input type="hidden" class="dwChenInputTemp" disabled="disabled" value="{{../id}}:{{id}}">
																																					<input name="ck_item_qu_{{quType}}_{{quId}}_{{../id}}_{{id}}" value="{{id}}" type="checkbox"> 
																																				</div>
																																			</td>
																																		{{/each}}
																																	</tr>
																																{{/each}}
																															</table>
																														</div>
																													</div>
																													<div style="clear: both;"></div>
																												</div>
																											</div>
																										</div>
																									{{else}}
																										{{#if quType}}
																										{{#compare1 quType '12'}}<!-- chen-fbk -->
																												<div class="surveyQuItemBody">
																													<div class="initLine"></div>
																													<div class="quInputCase" style="display: none;">
																														<input type="hidden" class="quType" value="CHENFBK">
																														<input type="hidden" name="quId" value="{{id}}">
																														<input type="hidden" name="orderById" value="{{orderById}}"/>
																														<input type="hidden" name="isRequired" value="{{isRequired}}">
																														<div class="quLogicInputCase">
																															{{#each questionLogic}}
																																<div class="quLogicItem quLogicItem_{{showXhIndex @index}}">
																																	<input type="hidden" class="logicId" value="{{id}}" />
																																	<input type="hidden" class="cgQuItemId" value="{{cgQuItemId}}" />
																																	<input type="hidden" class="skQuId" value="{{skQuId}}" />
																																	<input type="hidden" class="logicType" value="{{logicType}}">
																																</div>
																															{{/each}}
																														</div>
																														<input type="hidden" name="qu_{{quType}}_{{id}}" value="item_qu_{{quType}}_{{id}}_" />
																													</div>
																													<div class="surveyQuItem">
																														<div class="surveyQuItemContent">
																															<div class="quCoTitle">
																																<div class="quCoNum">{{showQuestionIndex quType}}、</div>
																																<div class="quCoTitleEdit" >{{quTitle}}</div>
																															</div>
																															<div class="quCoItem">
																																<div class="quCoItemLeftChenTableDiv">
																																	<table class="quCoChenTable" >
																																		<tr><td></td>
																																			{{#each questionChenColumn}}
																																				<td class="quChenColumnTd"><label class="quCoOptionEdit">{{optionName}}</label>
																																				</td>
																																			{{/each}}
																																		</tr>
																																		{{#each questionChenRow}}
																																			<tr class="dwQuCoChenRowTr">
																																				<td class="quChenRowTd"><label class="quCoOptionEdit">{{optionName}}</label>
																																					<input type="hidden" name="item_qu_{{quType}}_{{quId}}_{{id}}" value="fbk_item_qu_{{quType}}_{{quId}}_{{id}}_" />
																																				</td>
																																				{{#each questionChenColumn}}
																																					<td>
																																						<div class="dwQuChenFbkOptionItemContent">
																																							<input type="hidden" class="dwChenInputTemp" disabled="disabled" value="{{../id}}:{{id}}">
																																							<input name="fbk_item_qu_{{quType}}_{{quId}}_{{../id}}_{{id}}" class="inputSytle_1 dwChenMFillblankInput" type="text"> 
																																							<input type="hidden" class="answerTag" value="0" >
																																						</div>
																																					</td>
																																				{{/each}}
																																			</tr>
																																		{{/each}}
																																	</table>
																																</div>
																															</div>
																															<div style="clear: both;"></div>
																														</div>
																													</div>
																												</div>
																											{{else}}
																												{{#if quType}}
																												{{#compare1 quType '18'}}<!-- chen-score -->
																														<div class="surveyQuItemBody">
																															<div class="initLine"></div>
																															<div class="quInputCase" style="display: none;">
																																<input type="hidden" class="quType" value="CHENSCORE">
																																<input type="hidden" name="quId" value="{{id}}">
																																<input type="hidden" name="orderById" value="{{orderById}}"/>
																																<input type="hidden" name="isRequired" value="{{isRequired}}">
																																<div class="quLogicInputCase">
																																	{{#each questionLogic}}
																																		<div class="quLogicItem quLogicItem_{{showXhIndex @index}}">
																																			<input type="hidden" class="logicId" value="{{id}}" />
																																			<input type="hidden" class="cgQuItemId" value="{{cgQuItemId}}" />
																																			<input type="hidden" class="skQuId" value="{{skQuId}}" />
																																			<input type="hidden" class="logicType" value="{{logicType}}">
																																		</div>
																																	{{/each}}
																																</div>
																																<input type="hidden" name="qu_{{quType}}_{{id}}" value="item_qu_{{quType}}_{{id}}_" />
																															</div>
																															<div class="surveyQuItem">
																																<div class="surveyQuItemContent">
																																	<div class="quCoTitle">
																																		<div class="quCoNum">{{showQuestionIndex quType}}、</div>
																																		<div class="quCoTitleEdit" >{{quTitle}}</div>
																																	</div>
																																	<div class="quCoItem">
																																		<div class="quCoItemLeftChenTableDiv">
																																			<table class="quCoChenTable" >
																																				<tr><td></td>
																																					{{#each questionChenColumn}}
																																						<td class="quChenColumnTd"><label class="quCoOptionEdit">{{optionName}}</label>
																																						</td>
																																					{{/each}}
																																				</tr>
																																				{{#each questionChenRow}}
																																					<tr class="dwQuCoChenRowTr">
																																						<td class="quChenRowTd"><label class="quCoOptionEdit">{{optionName}}</label>
																																							<input type="hidden" name="item_qu_{{quType}}_{{quId}}_{{id}}" value="cs_item_qu_{{quType}}_{{quId}}_{{id}}_" />
																																						</td>
																																						{{#each questionChenColumn}}
																																							<td>
																																								<div class="dwQuScoreOptionItemContent">
																																									<input type="hidden" class="dwChenInputTemp" disabled="disabled" value="{{../id}}:{{id}}">
																																									<select name="cs_item_qu_{{quType}}_{{quId}}_{{../id}}_{{id}}" class="quChenScoreSelect" > 
																																										<option value="0">-评分-</option>
																																										{{#scoreNum001 5}}{{/scoreNum001}}
																																									</select> 
																																									<input type="hidden" class="answerTag" value="0" >
																																								</div>
																																							</td>
																																						{{/each}}
																																					</tr>
																																				{{/each}}
																																			</table>
																																		</div>
																																	</div>
																																	<div style="clear: both;"></div>
																																</div>
																															</div>
																														</div>
																													{{else}}
																														{{#if quType}}
																														{{#compare1 quType '8'}}<!-- score -->
																																<div class="surveyQuItemBody">
																																	<div class="initLine"></div>
																																	<div class="quInputCase" style="display: none;">
																																		<input type="hidden" class="quType" value="SCORE">
																																		<input type="hidden" name="quId" value="{{id}}">
																																		<input type="hidden" name="orderById" value="{{orderById}}"/>
																																		<input type="hidden" name="isRequired" value="{{isRequired}}">
																																		<div class="quLogicInputCase">
																																			{{#each questionLogic}}
																																				<div class="quLogicItem quLogicItem_{{showXhIndex @index}}">
																																					<input type="hidden" class="logicId" value="{{id}}" />
																																					<input type="hidden" class="cgQuItemId" value="{{cgQuItemId}}" />
																																					<input type="hidden" class="skQuId" value="{{skQuId}}" />
																																					<input type="hidden" class="logicType" value="{{logicType}}">
																																					<input type="hidden" class="geLe" value="{{geLe}}">
																																					<input type="hidden" class="scoreNum" value="{{scoreNum}}">
																																				</div>
																																			{{/each}}
																																		</div>
																																		<input type="hidden" name="qu_{{quType}}_{{id}}" value="item_qu_{{quType}}_{{id}}_" />
																																	</div>
																																	<div class="surveyQuItem">
																																		<div class="surveyQuItemContent">
																																			<div class="quCoTitle">
																																				<div class="quCoNum">{{showQuestionIndex quType}}、</div>
																																				<div class="quCoTitleEdit" >{{quTitle}}</div>
																																			</div>
																																			<div class="quCoItem">
																																				<table class="quCoItemTable" cellpadding="0" cellspacing="0">
																																					{{#each quScores}}
																																						<tr class="quScoreOptionTr">
																																							<td class="quCoItemTableTd quOptionEditTd">
																																								<label class="quCoOptionEdit">{{optionName}}</label>
																																								<input class="dwScoreOptionId" value="{{id}}" disabled="disabled" type="hidden"/>
																																								<input type="hidden" class="answerTag" value="0" >
																																							</td>
																																							<td class="quCoItemTableTd">
																																								<table class="scoreNumTable" border="0" cellspacing="0" cellpadding="1" style="border-collapse: initial;">
																																									<tr>
																																										{{#showParamInt02 paramInt02}}{{/showParamInt02}}
																																									</tr>
																																								</table>
																																								<input name="item_qu_{{quType}}_{{quId}}_{{id}}" value="" type="hidden" class="scoreNumInput" >
																																							</td>
																																							<td class="quCoItemTableTd scoreNumText">分</td>
																																						</tr>
																																					{{/each}}
																																				</table>
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
								</li>
							{{/each}}
							{{#bean}}
								<li class="li_surveyQuItemBody surveyQu_{{#compare6 quType}}{{/compare6}}" style="{{#compare4 pageNo}}{{/compare4}}">
									<div class="surveyQuItemBody">
										<div class="surveyQuItem">
											<input type="hidden" class="quType" value="submitSurveyBtn">
											<div class="surveyQuItemContent" style="padding-top: 12px;height: 30px;min-height: 30px;">
												<a href="javascript:;" id="submitSurvey" class="sbtn24 sbtn24_0 submitSurvey" >提&nbsp;交</a>&nbsp;&nbsp;&nbsp;&nbsp;
												{{#if pageNo}}
												{{#compare3 pageNo 1}}
													<a href="#" class="sbtn24 sbtn24_1 prevPage_a">上一页</a>
													<input type="hidden" name="prevPageNo" value="{{pageNo}}">
													{{else}}
												{{/compare3}}
												{{/if}}
												<input type="hidden" name="nextPageNo" value="{{pageNo}}">
											</div>
										</div>
									</div>
								</li>
							{{/bean}}
						</ul>
					</div>
				</div>
				<div id="resultProgressRoot">
					<div class="progress-label">完成度：0%</div>
					<div id="resultProgress" class="progressbarDiv"></div>
				</div>
			</div>
		</div>
	</form>
