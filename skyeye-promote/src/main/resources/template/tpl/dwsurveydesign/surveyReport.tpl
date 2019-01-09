{{#bean}}
<div class="surveyCollectMiddle">
	<div class="surveyCollectTop">
		<div class="surveyCollectTitleDiv">
			<span class="surveyCollectTitle">{{surveyName}}</span>
		</div>
		<div class="surveyCollectInfoDiv">
			<span class="surveyCollectInfoLeft">
				状态：<span class="collectInfoSpan">收集中</span>&nbsp;&nbsp;&nbsp;&nbsp; 参与人数：
				<span class="collectInfoSpan">{{answerNum}}</span>
			</span>
			<span class="surveyCollectInfoRight">
				开始时间：<span class="collectInfoSpan">{{startTime}}</span>
			</span>
		</div>
	</div>
{{/bean}}
	<div class="surveyCollectMiddleContent">
		<div style="padding: 15px 25px;overflow: auto;">
			<div style="overflow: auto;">
				<div style="float: left;">
					<a href="${ctx }/da/survey-report!defaultReport.action?surveyId=${surveyId }" class="dw_btn025 tabpic active"><i class="fa fa-refresh"></i>&nbsp;刷新</a>
				</div>
				<div style="float: right;">
					<a href="${ctx }/da/my-survey-answer!exportXLS.action?surveyId=${surveyId }" class="dw_btn025"><i class="fa fa-download"></i>下载数据</a>
				</div>
			</div>
			<div style="padding-top:8px;">
				<div class="" style="border: 1px solid #D1D6DD;padding: 0px;">
					<table id="content-tableList" width="100%" cellpadding="0" cellspacing="0">
						{{#each rows}}
							{{#if quType}}
							{{#compare7 quType}}
								<tr id="quTr_{{id}}">
									<td colspan="3">
										<div class="surveyResultQu">
											<input type="hidden" name="quId" value="{{id}}">
											<input type="hidden" name="quType" value="{{quType}}">
											<input type="hidden" name="quAnCount" value="{{anCount}}">
											<input type="hidden" name="quTypeCnName" value="{{quTypeName}}">
											<div class="r-qu-body-title">
												<div class="quCoNum">{{showIndex quType}}、</div>
												<div class="quCoTitleText">{{quTitle}}[{{quTypeName}}]</div>
											</div>
											<div class="r-qu-body-content">
												{{#if quType}}
												{{#compare1 quType '1'}}<!-- radio -->
							    						<table class="suQuTable" border="0" cellpadding="0" cellspacing="0">
															{{#each questionRadio}}
																<tr class="quTrOptions">
																	<td width="15px">&nbsp;</td>
																	<td width="520px" class="optionName">{{optionName}}</td>
																	<td width="180px"><div id="bfbTd{{quType}}{{anAllCount}}_{{anCount}}_{{id}}" class="progressbarDiv progress{{showXhIndex @index}}"></div></td>
																	<td width="60px" align="right" id="bfbNum{{quType}}{{anAllCount}}_{{anCount}}_{{id}}" class="bfbTd">0%</td>
																	<td align="left" class="tdAnCount">&nbsp;&nbsp;{{anCount}}次</td>
																	<td width="40px">&nbsp;
																		<input type="hidden" name="quItemAnCount" value="{{anCount}}">
																	</td>
																</tr>
																<script type="text/javascript">
																	layui.define(["jquery", 'jqueryUI'], function(exports) {
																		var jQuery = layui.jquery;
																		(function($) {
																			var count = parseInt("{{anAllCount}}");
																			var anCount = parseInt("{{anCount}}");
																			if(count == 0){
																				count = 1;
																			}
																			var bfbFloat = anCount / count * 100;
																			var bfbVal = bfbFloat.toFixed(2);
																			$("#bfbNum{{quType}}{{anAllCount}}_{{anCount}}_{{id}}").html(bfbVal + "%");
																			$("#bfbTd{{quType}}{{anAllCount}}_{{anCount}}_{{id}}").progressbar({value: bfbFloat});
																		})(jQuery);
																	});
																</script>
															{{/each}}
														</table>
														<div class="reportPic">
															<div class="chartBtnEvent">
																<a href="#" class="dw_btn026 columnchart_pic"><i class="fa fa-bar-chart"></i>柱状图</a>
																<a href="#" class="dw_btn026 piechart_pic"><i class="fa fa-pie-chart"></i>饼图</a>
																<a href="#" class="dw_btn026 barchart_pic"><i class="fa fa-tasks"></i>条形图</a>
																<a href="#" class="dw_btn026 linechart_pic"><i class="fa fa-line-chart"></i>折线图</a>
															</div>
															<div style="clear: both;"></div>
															<div id="amchart_{{id}}" ></div>
														</div>
														<div style="clear:both;">
														</div>
							    					{{else}}
							    						{{#if quType}}
														{{#compare1 quType '2'}}<!-- checkbox -->
																<table class="suQuTable" border="0" cellpadding="0" cellspacing="0">
																	<c:forEach items="${en.quCheckboxs }" var="quEn" varStatus="quI">
																		<tr class="quTrOptions">
																			<td width="15px">&nbsp;</td>
																			<td width="520px" class="optionName">${quEn.optionName }</td>
																			<td width="180px"><div id="bfbTd${en.quType }${i.count }_${quI.count}" class="progressbarDiv progress${quI.index }"></div></td>
																			<td width="60px" align="right" id="bfbNum${en.quType }${i.count }_${quI.count}" class="bfbTd">0%</td>
																			<td align="left" class="tdAnCount">&nbsp;&nbsp;${quEn.anCount }次</td>
																			<td width="40px">&nbsp;
																			<input type="hidden" name="quItemAnCount" value="${quEn.anCount }">
																			</td>
																		</tr>
																		<script type="text/javascript">
																			var count=parseInt("${en.anCount }");
																			var anCount=parseInt("${quEn.anCount }");
																			var bfbFloat=anCount/count*100;
																			var bfbVal = bfbFloat.toFixed(2);
																			if(bfbVal==="NaN"){
																				bfbVal="0.00";
																			}
																			$("#bfbNum${en.quType }${i.count }_${quI.count}").html(bfbVal+"%");
																			$("#bfbTd${en.quType }${i.count }_${quI.count}").progressbar({value: bfbFloat});
																		</script>
																	</c:forEach>
																</table>
																<div class="reportPic">
																	<div class="chartBtnEvent">
																	<a href="#" class="dw_btn026 columnchart_pic"><i class="fa fa-bar-chart"></i>柱状图</a>
																	<a href="#" class="dw_btn026 piechart_pic"><i class="fa fa-pie-chart"></i>饼图</a>
																	<a href="#" class="dw_btn026 barchart_pic"><i class="fa fa-tasks"></i>条形图</a>
																	<a href="#" class="dw_btn026 linechart_pic"><i class="fa fa-line-chart"></i>折线图</a>
																	</div>
																	<div style="clear: both;"></div>
																	<div id="amchart_${en.id }" ></div>
																</div>
																<div style="clear:both;"></div>
															{{else}}
																{{#if quType}}
																{{#compare1 quType '3'}}<!-- fillblank -->
																		
																		
																		
																	{{else}}
																		{{#if quType}}
																		{{#compare1 quType '9'}}<!-- orderby -->
																				
																				
																				
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
											</div>
										</div>
									</td>
								</tr>
								{{else}}
							{{/compare7}}
							{{/if}}
						{{/each}}
					</table>
				</div>
			</div>
		</div>
	</div>
</div>