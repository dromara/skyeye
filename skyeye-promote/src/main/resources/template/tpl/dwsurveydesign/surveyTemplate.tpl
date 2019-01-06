{{#bean}}
	<input type="hidden" id="id" name="id" value="{{id}}">
	<form id="surveyForm" method="post" >
		<input type="hidden" id="surveyId" name="surveyId" value="{{id}}">
		<input type="hidden" id="sid" name="sid" value="{{sid}}">
		<div id="dw_body" style="padding-top:10px;">
			<div id="dw_body_content">
				<div id="dwSurveyHeader">
					<div id="dwSurveyTitle" class="noLogoImg">
						<div id="dwSurveyName" class="editAble dwSvyName">{{surveyName}}</div>
					</div>
					<div id="dwSurveyNote">
						<div id="dwSurveyNoteEdit" class="editAble">{{surveyNote}}</div>
					</div>
				</div>
{{/bean}}
				<div id="dwSurveyQuContent" style="min-height: 300px;">
					<div id="dwSurveyQuContentBg">
						<ul id="dwSurveyQuContentAppUl">
							{{#each rows}}
								<li class="li_surveyQuItemBody surveyQu_" style="${pageNo gt 1 ?'display: none':''}">
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
														<div class="quLogicItem quLogicItem_{{showIndex @index}}">
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
														<div class="quCoNum">{{showIndex @index}}、</div>
														<div class="editAble quCoTitleEdit" >{{quTitle}}</div>
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
																			<label class="editAble quCoOptionEdit quCoOptionPadding">{{optionName}}</label>
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
																			<label class="editAble quCoOptionEdit quCoOptionPadding">{{optionName}}</label>
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
																		<div class="quLogicItem quLogicItem_{{showIndex @index}}">
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
																		<div class="quCoNum">{{showIndex @index}}、</div>
																		<div class="editAble quCoTitleEdit" >{{quTitle}}</div>
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
																
																
																
															{{else}}
																{{#if quType}}
																{{#compare1 quType '16'}}<!-- pagetag -->
																		
																		
																		
																	{{else}}
																		{{#if quType}}
																		{{#compare1 quType '17'}}<!-- paragraph -->
																				
																				
																				
																			{{else}}
																				{{#if quType}}
																				{{#compare1 quType '4'}}<!-- multi-fillblank -->
																						
																						
																						
																					{{else}}
																						{{#if quType}}
																						{{#compare1 quType '11'}}<!-- chen-radio -->
																								
																								
																								
																							{{else}}
																								{{#if quType}}
																								{{#compare1 quType '13'}}<!-- chen-checkbox -->
																										
																										
																										
																									{{else}}
																										{{#if quType}}
																										{{#compare1 quType '12'}}<!-- chen-fbk -->
																												
																												
																												
																											{{else}}
																												{{#if quType}}
																												{{#compare1 quType '18'}}<!-- chen-score -->
																														
																														
																														
																													{{else}}
																														{{#if quType}}
																														{{#compare1 quType '8'}}<!-- score -->
																																
																																
																																
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
						</ul>
					</div>
				</div>
			</div>
		</div>
	</form>
