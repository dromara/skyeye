{{#bean}}
<div class="surveyCollectMiddle">
	<div class="surveyCollectTop">
		<div class="surveyCollectTitleDiv">
			<span class="surveyCollectTitle">{{surveyName}}</span>
		</div>
		<div class="surveyCollectInfoDiv">
			<span class="surveyCollectInfoLeft">
				状态：<span class="collectInfoSpan" id="surveyState">收集中</span>&nbsp;&nbsp;&nbsp;&nbsp; 参与人数：
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
																	{{#each questionCheckBox}}
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
																					var bfbFloat = anCount / count * 100;
																					var bfbVal = bfbFloat.toFixed(2);
																					if(bfbVal === "NaN"){
																						bfbVal="0.00";
																					}
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
																<div style="clear:both;"></div>
															{{else}}
																{{#if quType}}
																{{#compare1 quType '3'}}<!-- fillblank -->
																		<table class="suQuTable" border="0" cellpadding="0" cellspacing="0" style="border: none! important;margin-top: 8px;">
																			<tr>
																				<td width="15px">&nbsp;</td>
																				<td class="bfbTd">回答数：{{anCount}}条&nbsp;&nbsp;</td>
																				<td colspan="4">&nbsp;</td>
																			</tr>
																		</table>
																		<div style="clear:both;"></div>
																	{{else}}
																		{{#if quType}}
																		{{#compare1 quType '9'}}<!-- orderby -->
																				<table class="suQuTable" border="0" cellpadding="0" cellspacing="0">
																					{{#each questionOrderBy}}
																					<c:forEach items="${en.quOrderbys }" var="quEn" varStatus="quI">
																						<tr  class="quTrOptions" >
																							<td width="15px">&nbsp;</td>
																							<td width="520px" class="optionName" >{{optionName}}</td>
																							<td colspan="3" align="left" class="tdAnCount">&nbsp;&nbsp;排第&nbsp;{{anOrderSum}}&nbsp;名</td>
																							<td width="40px">&nbsp;
																							</td>
																						</tr>
																					{{/each}}
																				</table>
																				<div class="reportPic">
																					<div class="chartBtnEvent">
																						<a href="#" class="dw_btn026 columnchart_pic"><i class="fa fa-bar-chart"></i>柱状图</a>
																						<a href="#" class="dw_btn026 barchart_pic"><i class="fa fa-tasks"></i>条形图</a>
																						<a href="#" class="dw_btn026 linechart_pic"><i class="fa fa-line-chart"></i>折线图</a>
																					</div>
																					<div style="clear: both;"></div>
																					<div id="amchart_{{id}}" ></div>
																				</div>
																				<div style="clear:both;"></div>
																			{{else}}
																				{{#if quType}}
																				{{#compare1 quType '4'}}<!-- multi-fillblank -->
																						<table class="suQuTable" border="0" cellpadding="0" cellspacing="0">
																							{{#each questionMultiFillBlank}}
																								<tr class="quTrOptions">
																									<td width="15px">&nbsp;</td>
																									<td width="520px">{{optionName}}</td>
																									<td class="bfbTd">回答数：{{anCount}}条</td></td>
																									<td colspan="4"></td>
																								</tr>
																							{{/each}}
																							</table>
																						<div class="reportPic"></div>
																						<div style="clear:both;"></div>
																					{{else}}
																						{{#if quType}}
																						{{#compare1 quType '11'}}<!-- chen-radio -->
																								<table class="suQuTable" border="0" cellpadding="0" cellspacing="0">
																									{{#each questionChenRow}}
																										<tr class="rowItemTr">
																											<td width="15px">&nbsp;
																												<div class="rowItemOptionName" style="display: none;">{{optionName}}</div>
																												<input type="hidden" name="rowItemAnCount" value="{{anAllCount}}">
																											</td>
																											<td class="quChenRowTd" colspan="5"><label class="editAble quCoOptionEdit" style="font-size: 14px;">{{showXhIndex @index}}、{{optionName}}</label></td>
																										</tr>
																										<tr class="columnItemTr">
																											<td colspan="6">
																												<table class="anColumnTable">
																													{{#each questionChenColumn}}
																														<tr class="columnItemTr">
																															<td width="15px">&nbsp;</td>
																															<td width="520" class="quChenRowTd" style="padding-left: 15px;"><label class="editAble quCoOptionEdit">{{optionName}}</label></td>
																															<td width="180px"><div id="bfbTd{{quType}}_{{../id}}_{{id}}" class="progressbarDiv progress{{showXhIndex @index}}"></div></td>
																															<td width="50px" align="right" id="bfbNum{{quType}}_{{../id}}_{{id}}" class="bfbTd">0%</td>
																															<td align="left" id="bfbAnCount{{quType}}_{{../id}}_{{id}}" class="tdAnCount">&nbsp;0次</td>
																															<td>
																																<div class="columnItemOptionName" style="display: none;">{{optionName}}</div>
																																<input type="hidden" name="columnItemAnCount" value="0" id="coumneItemAnCount{{quType}}_{{rowId}}_{{id}}">
																															</td>
																														</tr>
																													{{/each}}
																												</table>
																											</td>
																										</tr>
																									{{/each}}
																									{{#each anChenRadios}}
																										<script type="text/javascript">
																											layui.define(["jquery", 'jqueryUI'], function(exports) {
																												var jQuery = layui.jquery;
																												(function($) {
																													var count = parseInt("{{anAllCount}}");
																													var anCount = parseInt("{{anCount}}");
																													var bfbFloat = anCount / count * 100;
																													var bfbVal = bfbFloat.toFixed(2);
																													if(bfbVal === "NaN"){
																														bfbVal = "0.00";
																													}
																													$("#bfbNum{{quType}}_{{quRowId}}_quColId").html(bfbVal + "%");
																													$("#bfbAnCount{{quType}}_{{quRowId}}_quColId").html("&nbsp;&nbsp;" + anCount + "次");
																													$("#bfbTd{{quType}}_{{quRowId}}_quColId").progressbar({value: bfbFloat});
																													$("#coumneItemAnCount{{quType}}_{{quRowId}}_quColId").val(anCount);
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
																								<div style="clear:both;"></div>
																							{{else}}
																								{{#if quType}}
																								{{#compare1 quType '13'}}<!-- chen-checkbox -->
																										<table class="suQuTable" border="0" cellpadding="0" cellspacing="0">
																											{{#each questionChenRow}}
																												<tr class="rowItemTr">
																													<td width="15px">&nbsp;
																														<div class="rowItemOptionName" style="display: none;">{{optionName}}</div>
																														<input type="hidden" name="rowItemAnCount" value="{{anAllCount}}">
																													</td>
																													<td class="quChenRowTd" colspan="5"><label class="editAble quCoOptionEdit" style="font-size: 14px;">{{showXhIndex @index}}、{{optionName}}</label></td>
																												</tr>
																												<tr class="columnItemTr">
																													<td colspan="6">
																														<table class="anColumnTable" style="width: 100%;">
																															{{#each questionChenColumn}}
																																<tr class="columnItemTr">
																																	<td width="15px">&nbsp;</td>
																																	<td width="520" class="quChenRowTd" style="padding-left: 15px;"><label class="editAble quCoOptionEdit">{{optionName}}</label></td>
																																	<td width="180px"><div id="bfbTd{{quType}}_{{../id}}_{{id}}" class="progressbarDiv progress{{showXhIndex @index}}"></div></td>
																																	<td width="50px" align="right" id="bfbNum{{quType}}_{{../id}}_{{id}}" class="bfbTd">0%</td>
																																	<td align="left" id="bfbAnCount{{quType}}_{{../id}}_{{id}}" class="tdAnCount">&nbsp;0次</td>
																																	<td>
																																		<div class="columnItemOptionName" style="display: none;">{{optionName}}</div>
																																		<input type="hidden" name="columnItemAnCount" value="0" id="coumneItemAnCount{{quType}}_{{../id}}_{{id}}">
																																	</td>
																																</tr>
																															{{/each}}
																														</table>
																													</td>
																												</tr>
																											{{/each}}
																											{{#each anChenCheckboxs}}
																												<script type="text/javascript">
																													layui.define(["jquery", 'jqueryUI'], function(exports) {
																														var jQuery = layui.jquery;
																														(function($) {
																															var count = parseInt("{{anAllCount}}");
																															var anCount = parseInt("{{anCount}}");
																															var bfbFloat = anCount / count * 100;
																															var bfbVal = bfbFloat.toFixed(2);
																															if(bfbVal === "NaN"){
																																bfbVal = "0.00";
																															}
																															$("#bfbNum{{quType}}_{{quRowId}}_{{quColId}}").html(bfbVal + "%");
																															$("#bfbAnCount{{quType}}_{{quRowId}}_{{quColId}}").html("&nbsp;&nbsp;" + anCount + "次");
																															$("#bfbTd{{quType}}_{{quRowId}}_{{quColId}}").progressbar({value: bfbFloat});
																															$("#coumneItemAnCount{{quType}}_{{quRowId}}_{{quColId}}").val(anCount);
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
																										<div style="clear:both;"></div>
																									{{else}}
																										{{#if quType}}
																										{{#compare1 quType '12'}}<!-- chen-fbk -->
																												<table class="suQuTable" border="0" cellpadding="0" cellspacing="0" >
																													{{#each questionChenRow}}
																														<tr class="rowItemTr">
																															<td width="15px">&nbsp;</td>
																															<td class="quChenRowTd" colspan="4"><label class="editAble quCoOptionEdit" style="font-size: 14px;">{{showXhIndex @index}}、{{optionName}}</label></td>
																														</tr>
																														{{#each questionChenColumn}}
																															<tr class="columnItemTr">
																																<td width="15px">&nbsp;</td>
																																<td width="520" class="quChenRowTd" style="padding-left: 15px;"><label class="editAble quCoOptionEdit">{{optionName}}</label></td>
																																<td width="120px" align="left" id="bfbNum{{quType}}_{{../id}}_{{id}}" class="bfbTd">0%</td>
																																<td align="left" id="bfbAnCount{{quType}}_{{../id}}_{{id}}" class="tdAnCount">&nbsp;0次</td>
																																<td width="40px">&nbsp;</td>
																																<td></td>
																															</tr>
																														{{/each}}
																													{{/each}}
																													{{#each anChenFbks}}
																														<script type="text/javascript">
																															layui.define(["jquery"], function(exports) {
																																var jQuery = layui.jquery;
																																(function($) {
																																	$("#bfbNum{{quType}}_{{quRowId}}_{{quColId}}").html("回答数：{{anCount}}条");
																																})(jQuery);
																															});
																														</script>
																													{{/each}}
																												</table>
																												<div class="reportPic"></div>
																												<div style="clear:both;"></div>
																												<div class="quItemNote">{{quNote}}</div>
																											{{else}}
																												{{#if quType}}
																												{{#compare1 quType '18'}}<!-- chen-score -->
																														<table class="suQuTable" border="0" cellpadding="0" cellspacing="0">
																															{{#each questionChenRow}}
																																<tr class="rowItemTr">
																																	<td width="15px">&nbsp;
																																		<div class="rowItemOptionName" style="display: none;">{{optionName}}</div>
																																		<input type="hidden" name="rowItemAnCount" value="{{anAllCount}}">
																																	</td>
																																	<td class="quChenRowTd" colspan="5"><label class="editAble quCoOptionEdit" style="font-size: 14px;">{{showXhIndex @index}}、{{optionName}}</label></td>
																																</tr>
																																<tr class="columnItemTr">
																																	<td colspan="6">
																																		<table class="anColumnTable">
																																			{{#each questionChenColumn}}
																																				<tr class="columnItemTr">
																																					<td width="15px">&nbsp;</td>
																																					<td width="520" class="quChenRowTd" style="padding-left: 15px;"><label class="editAble quCoOptionEdit">{{optionName}}</label></td>
																																					<td width="180px"><div id="bfbTd{{quType}}{{../id}}_{{id}}" class="progressbarDiv progress{{../_index}}"></div></td>
																																					<td width="60px" align="right" id="bfbNum{{quType}}{{../id}}_{{id}}" class="bfbTd">0%</td>
																																					<td align="left" class="tdAnCount">&nbsp;&nbsp;平均</td> 
																																					<td width="40px">&nbsp;</td>
																																					<td>
																																						<div class="columnItemOptionName" style="display: none;">{{optionName}}</div>
																																						<input type="hidden" name="columnItemAnCount" value="0" id="coumneItemAnCount{{quType}}{{../id}}_{{id}}">
																																					</td>
																																				</tr>
																																			{{/each}}
																																		</table>
																																	</td>
																																</tr>
																															{{/each}}
																															{{#each anChenScores}}
																																<script type="text/javascript">
																																	layui.define(["jquery", 'jqueryUI'], function(exports) {
																																		var jQuery = layui.jquery;
																																		(function($) {
																																			var avgScore = parseFloat("{{avgScore}}");
																																			var bfbFloat = avgScore / 5 * 100;
																																			var bfbVal = bfbFloat.toFixed(2);
																																			//平均分 setAvgScore  
																																			avgScore = avgScore.toFixed(2);
																																			if(avgScore === "NaN"){
																																				avgScore = "0.00";
																																			}
																																			$("#bfbNum{{quType}}{{quRowId}}_{{quColId}}").html(avgScore + "分");
																																			$("#bfbTd{{quType}}{{quRowId}}_{{quColId}}").progressbar({value: bfbFloat});
																																			$("#coumneItemAnCount{{quType}}{{quRowId}}_{{quColId}}").val(avgScore);
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
																														<div style="clear:both;"></div>
																													{{else}}
																														{{#if quType}}
																														{{#compare1 quType '8'}}<!-- score -->
																																<input type="hidden" name="paramInt02" value="{{paramInt02}}">
																																<table class="suQuTable" border="0" cellpadding="0" cellspacing="0">
																																	{{#each quScores}}
																																		<tr class="quTrOptions">
																																			<td width="15px">&nbsp;</td>
																																			<td width="520px" class="optionName">{{optionName}}</td>
																																			<td width="180px"><div id="bfbTd{{quType}}_{{quId}}_{{id}}" class="progressbarDiv progress{{showXhIndex @index}}"></div></td>
																																			<td width="60px" align="right" id="bfbNum{{quType}}_{{quId}}_{{id}}" class="bfbTd">0%</td>
																																			<td align="left" class="tdAnCount">&nbsp;&nbsp;平均</td> 
																																			<td width="40px">&nbsp;
																																				<input type="hidden" name="quItemAnCount" value="{{anCount}}">
																																				<input type="hidden" name="quItemAvgScore" value="{{avgScore}}" >
																																			</td>
																																		</tr>
																																		<script type="text/javascript">
																																			layui.define(["jquery", 'jqueryUI'], function(exports) {
																																				var jQuery = layui.jquery;
																																				(function($) {
																																					var count = parseInt("{{anCount}}");
																																					var anCount = parseInt("{{anAllCount}}");
																																					var avgScore = parseFloat("{{avgScore}}");
																																					var bfbFloat = avgScore / {{../paramInt02}} * 100;
																																					var bfbVal = bfbFloat.toFixed(2);
																																					//平均分 setAvgScore  
																																					avgScore = avgScore.toFixed(2);
																																					if(avgScore === "NaN"){
																																						avgScore = "0.00";
																																					}
																																					$("#bfbNum{{quType}}_{{quId}}_{{id}}").html(avgScore + "分");
																																					$("#bfbTd{{quType}}_{{quId}}_{{id}}").progressbar({value: bfbFloat});
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
																																<div style="clear:both;"></div>
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