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
		<div id="dwSurveyQuContent" style="min-height: 500px;">
			<ul id="dwSurveyQuContentAppUl">
				{{#each rows}}
				{{/each}}
			</ul>
		</div>
	</div>
{{/bean}}