var ruleCode_setting = [];
layui.define(["jquery", "dragula", 'winui', 'form', 'element'], function(exports) {
	"use strict";
	var $ = layui.jquery;
	var form = layui.form,
		element = layui.element;
	
	var utilList = [{
        text: "纯文本",
        code: "code_manage_pure_text",
        bgColor: "#FFA39E",
        helper: "<p data-lang='code_manage_any_character'>任意字符,不包括双引号(&quot;);并且不能有汉字</p>",
        html: '<input type="text" class="layui-input code_manage_pure_text" placeholder="纯文本">',
        eg: function(value) {
            return value.replaceAll('"', "")
        }
    }, {
        text: "日期",
        code: "code_manage_date",
        html: '<select class="layui-input code_manage_date" lay-filter="code_manage_date"><option value="yyyy" data-lang="code_manage_y">年</option><option value="yyyyMM" data-lang="code_manage_y_m">年月</option><option value="yyyyMMdd" data-lang="code_manage_y_m_d">年月日</option><option value="yyyyMMddhh" data-lang="code_manage_y_m_d_h">年月日时</option></select>',
        helper: '<p data-lang="code_manage_y" class="ellipsis">年</p><p> yyyy</p><p data-lang="code_manage_y_m" class="ellipsis">年月</p><p> yyyyMM</p><p data-lang="code_manage_y_m_d" class="ellipsis">年月日</p><p> yyyyMMdd</p><p data-lang="code_manage_y_m_d_h" class="ellipsis">年月日时</p><p> yyyyMMddhh</p>',
        bgColor: "#FFBB96",
        eg: function(value) {
            return new Date().format(value);
        }
    }, {
        text: "变量",
        code: "code_manage_variable",
        html: '<input type="text" class="layui-input code_manage_variable" placeholder="变量">',
        helper: '<p data-lang="code_manage_can_only_be_any_combination">只能是 字母、数字或下划线的任意组合</p><p data-lang="code_manage_first_character_cannot_be_num">第一个字符不能是数字,不能包含汉字</p>',
        bgColor: "#ADF1C7",
        eg: function(value) {
            return value.replaceAll(/{|}/g, "")
        },
    }, {
        text: "流水码",
        code: "code_manage_pipeline_code",
        html: '<input type="text" class="layui-input code_manage_pipeline_code" placeholder="流水码">',
        helper: '<p>n: 0-9</p><p data-lang="code_manage_example">例子: nnn: 001-999</p>',
        water: !0,
        bgColor: "#F1F1F1",
        eg: function(value) {
            return value && value.replaceAll(/(n+)/g, (function(e, a, t, n) {
                return e = e.replaceAll("n", 0).substr(0, e.length - 1) + "1"
            }
            ))
        }
    }];
	
	var n = "// value不可变 \nif(value == '1'){\n    return '苹果';\n}else if(value == '2'){\n    return '梨';\n}";
	var n1 = "1|苹果\n2|梨";
	
	// 变量规则中间的存储值
	var variableMap = {};

	// 编码规则构造函数
	var RuleCodeUtil = function(options) {
		this.options = options;
	};

	// 渲染
	RuleCodeUtil.prototype.render = function(options) {
		this.renderHtml(options);
		
		this.renderDrag(options);
		
		this.renderEvent(options);
		
		this.addPipelineCode(options);
		
		this.resetData(options);
	};
	
	// 渲染HTML
	RuleCodeUtil.prototype.renderHtml = function(options) {
		var _box = $("#" + options.id);
		var html = "<div class='rule-pattern-wrapper'>" + 
						"<div class='rule-pattern-wrapper-left'>" + 
							"<div class='pattern-rules-title'>规则</div>" + 
							"<div class='pattern-rules-body ruleList' id='ruleList" + options.id + "'></div>" + 
						"</div>" + 
						"<div class='rule-pattern-wrapper-right-config'>" +
							"<div class='pattern-rules-title'>" + 
								"配置<span class='tip'>拖动左侧规则到当前区域</span>" + 
								"<button type='button' class='layui-btn layui-btn-primary layui-btn-xs variableConfig' style='float: right; margin: 0px 10px;'>变量配置</button>" +
								"<button type='button' class='layui-btn layui-btn-primary layui-btn-xs regulation' style='float: right'>规则</button>" +
							"</div>" + 
							"<div class='pattern-rules-body ruleConfig' id='ruleConfig" + options.id + "'></div>" + 
						"</div>" + 
						"<div class='rule-pattern-wrapper-right-show'>" +
							"<div class='pattern-rules-title'>预览</div>" + 
							"<div class='pattern-rules-body ruleShow' id='ruleShow" + options.id + "'></div>" + 
						"</div>" + 
					"</div>";
		_box.html(html);
		// 加载可拖拽的规则
		$.each(utilList, function(i, item) {
			if (!item.water) {
				_box.find('#ruleList' + options.id).append('<div class="config-item" data-code="' + item.code + '" style="background-color: ' + item.bgColor + '">' +
					 '<div class="rule-pattern-text">' + item.text + '</div>' + 
					 '</div>');
			}
		});
	};
	
	// 加载拖拽事件
	RuleCodeUtil.prototype.renderDrag = function(options) {
		var _this = this;
		dragula([document.getElementById('ruleList' + options.id), document.getElementById('ruleConfig' + options.id)], {
			copy: function (el, source) {//复制
				el.className = 'config-item';
				return source === document.getElementById('ruleList' + options.id);
			},
			accepts: function (el, target) {//移动
				return target !== document.getElementById('ruleList' + options.id);
			}
		}).on('drop', function (el, container) {//放置
			if ($(container).attr("id") == 'ruleConfig' + options.id) {
				var code = $(el).attr("data-code");
				var codeRuleMation = _this.getPointUtilByCode(code);
				// 放置在配置里面
				el.className = 'config-item';
				var content = codeRuleMation.html;
				var operationContent = '<div class="btn-base">' +
					'<button type="button" class="btn btn-danger removeThis" title="删除"><i class="fa fa-trash"></i></button>' + 
				'</div>';
				$(el).html('<div>' + content + operationContent + '</div>');
				$(el).find(".check-item-operation").hide();
				// 加载预览
				_this.loadShow(options.id);
				form.render('select');
			}
		});
	};
	
	// 加载监听事件
	RuleCodeUtil.prototype.renderEvent = function(options) {
		var _this = this;
		
		// 删除配置中的规则对象
		$("body").on("click", ".removeThis", function() {
			$(this).parent().parent().parent().remove();
			// 加载预览
			_this.loadShow(options.id);
		});
		
		// 规则
		$("body").on("click", ".regulation", function() {
			var _cardHtml = '<div class="layui-collapse" lay-filter="regulationFilter">';
			$.each(utilList, function(i, item) {
				_cardHtml += '<div class="layui-colla-item">' +
									'<h2 class="layui-colla-title" style="border-bottom: 1px solid white; background-color: ' + item.bgColor + '">' + item.text + '</h2>' +
									'<div class="layui-colla-content">' +
										item.helper +
									'</div>' +
								'</div>';
			});
			_cardHtml += '</div>'
			layer.open({
				id: '规则',
				type: 1,
				title: '规则',
				shade: 0.3,
				area: ['350px', '400px'],
				content: _cardHtml
			});
			element.init();
		});
		
		// 选中配置中的规则对象
		$("body").on("click", ".config-item", function() {
			$('#ruleConfig' + options.id).find('.config-item').removeClass('active');
			$(this).addClass("active");
		});
		
		// 变化监听
		form.on('select(code_manage_date)', function(data) {
			_this.loadShow(options.id);
		});
		$("body").on("input", ".code_manage_pure_text, .code_manage_variable, .code_manage_pipeline_code", function(e) {
			var code = $(this).parent().parent().data("code");
			var value = $(this).val();
			var checkResult = _this.checkValue(code, value);
			if (checkResult) {
				_this.loadShow(options.id);
			} else {
				$(this).val($(this).attr("old-value"));
			}
		});
		
		// 变量配置
		$("body").on("click", ".variableConfig", function(e) {
			var _config = $('#ruleConfig' + options.id);
			var variableList = _config.find('div[data-code="code_manage_variable"]');
			if (variableList.length == 0) {
				layer.msg("没有可以转换的变量", {time: 3000, tips: 3});
				return;
			}

			var variableSelectStr = [];
			var variableSelectHtml = '<select class="layui-input" lay-filter="variableSelect" id="variableSelect">';
			var firstValue = '';
			for (var i = 0; i < variableList.length; i++) {
				var value = $(variableList.eq(i)).find(".layui-input").val();
				if (isNull(value)) {
					layer.msg("变量不能为空", {time: 3000, tips: 3});
					return;
				}
				if (variableSelectStr.indexOf(value) >= 0) {
					layer.msg("不允许存在相同的变量", {time: 3000, tips: 3});
					return;
				}
				if (i == 0) {
					firstValue = value;
				}
				variableSelectStr.push(value);
				variableSelectHtml += '<option value="' + value + '">' + value + '</option>';
			}
			variableSelectHtml += '</select>';
			
			var _form = '<form class="layui-form" action="" id="variableForm" autocomplete="off" style="margin: 5px;">' +
							'<div class="layui-form-item layui-col-xs12">' +
								'<label class="layui-form-label">可选变量<i class="red">*</i></label>' +
								'<div class="layui-input-block">' +
									variableSelectHtml +
								'</div>' +
							'</div>' +
							'<div class="layui-form-item layui-col-xs12">' +
								'<label class="layui-form-label">映射管理</label>' +
								'<div class="layui-input-block winui-radio">' +
									'<input type="radio" name="variableType" value="map" title="简单映射" lay-filter="variableType" />' +
									'<input type="radio" name="variableType" value="javascript" title="脚本(javascript)" lay-filter="variableType" />' +
								'</div>' +
							'</div>' +
							'<div class="layui-form-item layui-col-xs12">' +
								'<label class="layui-form-label">脚本</label>' +
								'<div class="layui-input-block">' +
									'<textarea id="featureScriptContent" name="featureScriptContent" class="layui-textarea"></textarea>' +
									'<div class="layui-form-mid layui-word-aux">点击查看<a href="javascript:;" style="color: blue" class="variableHelp">帮助</a></div>' +
								'</div>' +
							'</div>' +
							'<div class="layui-form-item layui-col-xs12">' +
								'<div class="layui-input-block">' +
									'<button class="winui-btn" type="button" lay-submit lay-filter="formAddBean">保存</button>' +
								'</div>' +
							'</div>' +
						'</form>';
			layer.open({
				id: '变量转换',
				type: 1,
				title: '变量转换',
				shade: 0.3,
				area: ['600px', '400px'],
				content: _form
			});
			
			_this.variableSelectChange(firstValue);
			form.render();
			form.on('radio(variableType)', function (data) {
				var val = data.value;
				if (val == 'map') {
					$("#featureScriptContent").attr("placeholder", n1);
				} else if (val == 'javascript') {
					$("#featureScriptContent").attr("placeholder", n);
				}
				$("#featureScriptContent").val('');
			});
			form.on('select(variableSelect)', function (data) {
				var val = data.value;
				_this.variableSelectChange(val);
			});
			form.on('submit(formAddBean)', function (data) {
				var type = $("input[name='variableType']:checked").val();
				var content = $("#featureScriptContent").val();
				var params = {
					type: type,
					content: content
				};
				variableMap[$("#variableSelect").val()] = params;
				layer.msg("保存成功", {time: 3000, tips: 3});
				return false;
			});
		});
		
		// 帮助
		$("body").on("click", ".variableHelp", function(e) {
			var _html = '<div style="margin: 5px">' +
							'<p><b>场景</b><p>' + 
							'<div>如果调用方的数据为编码，生成编码的时候，想转为对应的中文或者任何对应的值</div>'+
							'<p><b>简单映射</b><p>' + 
							'<pre class="layui-code" lay-options="{title: \'Custom Title\', skin: \'dark\'}">' + n1 + '</pre>' +
							'<p><b>脚本(javascript)</b><p>' +
							'<pre class="layui-code" lay-options="{title: \'Custom Title\', skin: \'dark\'}">' + n + '</pre>' +
						'</div>';
			layer.open({
				id: '变量转换show',
				type: 1,
				title: '变量转换',
				shade: 0.3,
				area: ['300px', '400px'],
				content: _html
			});
		});
		
	};
	
	RuleCodeUtil.prototype.variableSelectChange = function (value) {
		if (isNull(variableMap[value])) {
			$("#variableForm input:radio[name=variableType][value=map]").get(0).checked = true;
			$("#featureScriptContent").attr("placeholder", n1);
			$("#featureScriptContent").val('');
		} else {
			var variableMapVal = variableMap[value];
			$("#featureScriptContent").val(variableMapVal.content);
			$("#variableForm input:radio[name=variableType][value=" + variableMapVal.type + "]").get(0).checked = true;
			if (variableMapVal.type == 'javascript') {
				$("#featureScriptContent").attr("placeholder", n);
			} else {
				$("#featureScriptContent").attr("placeholder", n1);
			}
		}
		form.render('radio');
	};
	
	// 校验
	RuleCodeUtil.prototype.checkValue = function (code, value) {
		if (code == 'code_manage_pure_text') {
			var flag = new RegExp("[`~!@#$^&*()=|{}':;',\\[\\].<>《》/?~！@#￥……&*（）——|{}【】‘；：”“\"'。，、？ ]");
			if (flag.test(value)) {
				layer.msg("错误的纯文本", {time: 3000, tips: 3});
				return false;
			}
		}
		if (code == 'code_manage_variable') {
			var flag = /^\w+$/;
			if (!flag.test(value)) {
				layer.msg("错误的变量", {time: 3000, tips: 3});
				return false;
			}
		}
		if (code == 'code_manage_pipeline_code') {
			if (value.replaceAll(/(n+)/g, '').length != 0) {
				layer.msg("错误的流水码", {time: 3000, tips: 3});
				return false;
			}
		}
		return true;
	};
	
	// 加载流水码
	RuleCodeUtil.prototype.addPipelineCode = function(options) {
		var _config = $('#ruleConfig' + options.id);
		var pipelineCodeMation = getInPoingArr(utilList, "code", "code_manage_pipeline_code");
		
		var _html = '<div class="config-item" data-code="' + pipelineCodeMation.code + '" style="background-color: ' + pipelineCodeMation.bgColor + '">' + 
						'<div>' + pipelineCodeMation.html + 
						'</div>' + 
					'</div>';
		_config.html(_html);
		// 加载预览
		this.loadShow(options.id);
	};
	
	// 回显数据
	RuleCodeUtil.prototype.resetData = function(options) {
		var data = options["data"];
		if (isNull(data)) {
			return;
		}
		var _config = $('#ruleConfig' + options.id);
		var patternList = isNull(data["patternList"]) ? [] : JSON.parse(data["patternList"]);
		
		_config.html('');
		$.each(patternList, function(i, item) {
			var codeMation = getInPoingArr(utilList, "code", item.code);
			var _html = '';
			if (item.code == 'code_manage_pipeline_code') {
				_html = '<div class="config-item" data-code="' + codeMation.code + '" style="background-color: ' + codeMation.bgColor + '">' +
							'<div>' + codeMation.html + 
							'</div>' + 
						'</div>';
			} else {
				_html = '<div class="config-item" data-code="' + codeMation.code + '" style="background-color: ' + codeMation.bgColor + '">' +
							'<div>' + codeMation.html + 
								'<div class="btn-base">' +
									'<button type="button" class="btn btn-danger removeThis" title="删除"><i class="fa fa-trash"></i></button>' + 
								'</div>'
							'</div>' + 
						'</div>';
			}
			_config.append(_html);
			_config.children(":last-child").find(".layui-input").val(item.value);
			
			// 变量规则
			if (item.code == 'code_manage_variable') {
				var featureScript = isNull(data["featureScript"]) ? [] : JSON.parse(data["featureScript"]);
				$.each(featureScript, function (key, value) {
					var type = value.type;
					var content = value.content;
					if (type == 'map') {
						var str = '';
						$.each(content, function (key1, value1) {
							str += key1 + '|' + value1 + '\n';
						});
						content = str;
					}
					variableMap[key] = {
						type: type,
						content: content
					}
				});
			}
		});
		
		this.loadShow(options.id);
	};
	
	// 根据配置加载预览
	RuleCodeUtil.prototype.loadShow = function(id) {
		var _config = $('#ruleConfig' + id);
		var _show = $('#ruleShow' + id);
		var _this = this;
		
		var codeRuleList = _config.find(".config-item");
		var showHtml = '';
		var configHtml = '';
		$.each(codeRuleList, function(i, item) {
			var code = $(item).data("code");
			var value = $(item).find(".layui-input").val();
			$(item).find(".layui-input").attr("old-value", value);
			configHtml += _this.getConfigValue(code, value);
			
			var ruleMation = getInPoingArr(utilList, "code", code);
			value = '<span style="padding: 5px; background-color: ' + ruleMation.bgColor + '">' + ruleMation.eg(value) + '</span>';
			showHtml += value;
		});
		var html = '<p>' + configHtml + '</p><p>' + showHtml + '</p>';
		
		_show.html(html);
		return configHtml;
	};
	
	RuleCodeUtil.prototype.getConfigValue = function(code, value) {
		if (code == 'code_manage_pure_text') {
			value = '"' + value + '"';
		}
		if (code == 'code_manage_variable') {
			value = '{' + value + '}';
		}
		return value;
	};
	
	// 获取数据
	RuleCodeUtil.prototype.getDataById = function (id) {
		var _config = $('#ruleConfig' + id);
		var codeRuleList = _config.find(".config-item");
		var _this = this;
		
		var configHtml = '';
		var featureScript = {};
		var patternList = [];
		for (var i = 0; i < codeRuleList.length; i++) {
			var code = $(codeRuleList.eq(i)).data("code");
			var value = $(codeRuleList.eq(i)).find(".layui-input").val();
			patternList.push({
				code: code,
				value: value,
				order: i
			});
			configHtml += _this.getConfigValue(code, value);
			if (code == 'code_manage_variable') {
				var params;
				// 变量规则
				if (isNull(variableMap[value])) {
					params = {
						type: 'map',
						content: {}
					};
				} else {
					var content = variableMap[value].content;
					if (variableMap[value].type == 'map') {
						if (!isNull(variableMap[value].content)) {
							var contentMap = {};
							content = content.split(/\n/);
							$.each(content, function (i, item) {
								contentMap[item.split("|")[0]] = item.split("|")[1];
							});
							content = contentMap;
						} else {
							content = {};
						}
					}
					params = {
						type: variableMap[value].type,
						content: content
					};
				}
				featureScript[value] = params;
			}
		}
		var result = {
			pattern: configHtml,
			featureScript: JSON.stringify(featureScript),
			patternList: JSON.stringify(patternList)
		};
		return result;
	};
	
	// 获取数据前需要校验，自行调用
	RuleCodeUtil.prototype.checkDataById = function (id) {
		var _config = $('#ruleConfig' + id);
		var codeRuleList = _config.find(".config-item");
		var _this = this;
		
		for (var i = 0; i < codeRuleList.length; i++) {
			var code = $(codeRuleList.eq(i)).data("code");
			var value = $(codeRuleList.eq(i)).find(".layui-input").val();
			if (isNull(value)) {
				layer.msg("存在为空的规则", {time: 3000, tips: 3});
				$("body").find(".mask-req-str").remove();
				return false;
			}
		}
		return true;
	};
	
	// 根据code获可选规则对象
	RuleCodeUtil.prototype.getPointUtilByCode = function(code) {
		var index = 0;
		$.each(utilList, function(i, item) {
			if (code == item.code) {
				index = i;
			}
		});
		return utilList[index];
	};

	var ruleCode = new RuleCodeUtil();
	// 初始化
	ruleCode.init = function(options, data) {
		if (!options) return;
		$.each(ruleCode_setting, function(index, item) {
			if (!isNull(item)) {
				if (item.id == options.id) {
					ruleCode_setting.splice(index, 1);
					return;
				}
			}
		});
		options["data"] = data || null;
		ruleCode_setting.push(this._createObject(options.id, options));
		// 初始化
		this.render(options);
	};
	
	ruleCode._createObject = function(id, options){
	    var obj = {
	        id : id,
	        options : options
	    };
	    return obj;
	}
	
	// 获取数据前需要校验，自行调用
	ruleCode.checkData = function(id) {
		// todo
		return this.checkDataById(id);
	};

	// 获取数据
	ruleCode.getData = function(id) {
		// todo
		return this.getDataById(id);
	};

	winui.ruleCode = ruleCode;
	layui.link(basePath + '../../lib/layui/lay/modules/ruleCode/ruleCode.css');
	exports('ruleCode', null);
});
