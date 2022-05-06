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
									<div class="surveyQuItemBody">
										<div class="initLine"></div>
										<div class="quInputCase" style="display: none;">
											<input type="hidden" class="quType" value="{{quTypeName}}" />
											<input type="hidden" class="quId" value="{{id}}" />
											<input type="hidden" class="orderById" value="{{orderById}}" />
											<input type="hidden" name="isRequired" value="{{isRequired}}" />
											<input type="hidden" class="answerTag" value="0" />
											<!-- 填空题 -->
											{{#compare8 quType checkType}}{{/compare8}}
											<div class="quLogicInputCase">
												{{#each questionLogic}}
													<div class="quLogicItem quLogicItem_{{showIndex @index}}">
														<input type="hidden" class="logicId" value="{{id}}" />
														<input type="hidden" class="cgQuItemId" value="{{cgQuItemId}}" />
														<input type="hidden" class="skQuId" value="{{skQuId}}" />
														<input type="hidden" class="logicType" value="{{logicType}}" />
														<!-- 评分题 -->
														{{#compare8 quType geLe scoreNum}}{{/compare8}}
													</div>
												{{/each}}
												<!-- 多选题 -->
												{{#compare10 quType id}}{{/compare10}}
												<!-- 排序题，矩阵单选题，矩阵多选题，矩阵填空题，矩阵评分题，评分题 -->
												<!-- 多项填空题 -->
											</div>
										</div>
										<!-- 题目基础信息 -->
										{{#showQuestion quType @index}}{{/showQuestion}}
									</div>
								</li>
							{{/each}}
							{{#bean}}
								<li class="li_surveyQuItemBody surveyQu_{{#compare6 quType}}{{/compare6}}" style="{{#compare4 pageNo}}{{/compare4}}">
									<div class="surveyQuItemBody">
										<div class="surveyQuItem">
											<div class="surveyQuItemContent" style="padding-top: 12px;height: 30px;min-height: 30px;">
												<a href="#" id="submitSurvey" class="sbtn24 sbtn24_0 submitSurvey" >提&nbsp;交</a>&nbsp;&nbsp;&nbsp;&nbsp;
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
