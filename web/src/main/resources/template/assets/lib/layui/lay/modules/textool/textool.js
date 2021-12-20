/**
 * Layui 文本输入工具组件
 * 
 * @author  iTanken
 * @since   20200310
 */
layui.define(['jquery'], function(exports) {
	var $ = layui.$,
		baseClassName = 'layext-text-tool',
		extClassName = 'layext-textool-pane',
		style, alignRight = true,
		alignClass, nodes = [],
		tools = {
			"hide": null,
			"count": null,
			"copy": null,
			"reset": null,
			"clear": null,
			hideIndex: -1,
			countIndex: -1,
			copyIndex: -1,
			resetIndex: -1,
			clearIndex: -1,
			hideName: "hide",
			countName: "count",
			copyName: "copy",
			resetName: "reset",
			clearName: "clear",
			hideClass: "layext-textool-minmax",
			countClass: "layext-textool-count",
			maxClass: "layext-textool-max",
			copyClass: "layext-textool-copy",
			resetClass: "layext-textool-reset",
			clearClass: "layext-textool-clear",
			copyTextId: baseClassName + '-copy-text',
			lengthClass: 'layext-textool-length',
			lengthOverClass: baseClassName + '-length-over',
			laytips: 'layext-textool-laytips'
		},
		defaultOptions = {
			// 根据元素 id 值单独渲染，为空默认根据 class='layext-text-tool' 批量渲染
			eleId: null,
			// 批量设置输入框最大长度，可结合 eleId 单独设置最大长度
			maxlength: -1,
			// 初始化回调，无参
			initEnd: $.noop,
			// 显示回调，参数为当前输入框和工具条面板的 jQuery 对象
			showEnd: $.noop,
			// 隐藏回调，参数为当前输入框和工具条面板的 jQuery 对象
			hideEnd: $.noop,
			// 初始化展开，默认展开，否则收起
			initShow: true,
			// 启用指定工具模块，默认依次为字数统计、复制内容、重置内容、清空内容，按数组顺序显示
			tools: ['count', 'copy', 'reset', 'clear'],
			// 工具按钮提示类型，默认为 'title' 属性，可选 'laytips'，使用 layer 组件的吸附提示， 其他值不显示提示
			tipType: 'title',
			// 吸附提示背景颜色
			tipColor: '#01AAED',
			// 对齐方向，默认右对齐，可选左对齐 'left'
			align: 'right',
			// 工具条字体颜色
			color: '#666666',
			// 工具条背景颜色
			bgColor: '#FFFFFF',
			// 工具条边框颜色
			borderColor: '#E6E6E6',
			// 工具条附加样式类名
			className: '',
			// z-index
			zIndex: 19891014
		},
		Class = function(custom) {
			var _this = this;
			_this.tipsAttr = null;
			_this.selector = null;
			_this.init(_this, custom || {});
		};

	/** 初始化 */
	Class.prototype.init = function(_this, custom) {
		_this.options = $.extend({}, defaultOptions, custom);
		_this.selector = $.trim(_this.options.eleId) === '' ? '.' + baseClassName : '#' + _this.options.eleId;

		_this.initStyle(_this);
		_this.initPrototype();

		$(_this.selector).each(function(i, n) {
			var $this = $(this),
				maxlength = _this.options.maxlength;
			!isNaN(maxlength) && maxlength > -1 && $this.attr('maxlength', maxlength);
			_this.addTextool(_this, $this);
		});
		_this.initTips(_this);
		typeof _this.options.initEnd === 'function' && _this.options.initEnd();
	};

	/** 初始化扩展样式 */
	Class.prototype.initStyle = function(_this) {
		_this.options.zIndex = isNaN(_this.options.zIndex) ? 0 : _this.options.zIndex || 0;

		style = ['<style type="text/css">',
			'#', tools.copyTextId, ' { width: 0; height: 0; position: absolute; top: -190000px; }',
			_this.selector, ' { position: relative; }',
			_this.selector, ' + .', extClassName, ' { position: relative; margin-top: -2px; display: block; outline: none; }',
			_this.selector, ' + .', extClassName, ' * { color: ', (_this.options.color || '#666666'), '; }',
			_this.selector, ' + .', extClassName, ' a > i { font-size: 12px!important; }',
			_this.selector, ' + .', extClassName, ' a { padding: 0 3px; cursor: pointer; }',
			_this.selector, ' + .', extClassName, ' a:active { background-color: #E2E2E2; opacity: 0.8; }',
			_this.selector, ' + .', extClassName, ' .', tools.lengthClass, ' * { font-family: Consolas, sans-serif; }',
			_this.selector, ' + .', extClassName, ' .', tools.lengthClass, ' { display: inline-block; border-width: 0 1px; }', /* border: 1px solid #F6F6F6; */
			_this.selector, ' + .', extClassName, ' .', tools.lengthClass, ' * { font-family: Consolas, sans-serif; }',
			_this.selector, ' + .', extClassName, ' .', tools.lengthOverClass, ' { color: #FF5722; }',
			_this.selector, ' + .', extClassName, ' .', tools.countClass, ', ', _this.selector, ' + .', extClassName, ' .', tools.maxClass, ' { display: inline-block; min-width: 26px; height: 16px; line-height: 18px; }',
			_this.selector, ' + .', extClassName, ' > .layui-badge { overflow: hidden; border-color: ', (_this.options.borderColor || '#E6E6E6'), '; background-color: ', (_this.options.bgColor || '#FFFFFF'), '; }',
			_this.selector, ' + .', extClassName, ' .', tools.maxClass, ' { opacity: 0.9; }',
			_this.selector, ' + .', extClassName, '-r { text-align: right; }',
			_this.selector, ' + .', extClassName, '-l { text-align: left; }',
			'</style>'
		].join('');

		$('head link:last')[0] && $('head link:last').after(style) || $('head').append(style);
	};

	/** 初始化默认方法，处理 JS 兼容问题 */
	Class.prototype.initPrototype = function() {
		// 获取数组元素下标
		!Array.prototype.indexOf && (Array.prototype.indexOf = function(array, value) {
			array = array || [];
			for(var i = array.length; i--;) {
				if(array[i] == value) {
					return i;
				}
			}

			return -1;
		});
	};

	/** 添加文本工具 */
	Class.prototype.addTextool = function(_this, $target) {
		var $extPane = $target.next('.' + extClassName);
		if(!$extPane[0]) {
			// 不存在，添加元素
			$target.after(_this.getToolsNode(_this, $target));
			$extPane = $target.next('.' + extClassName);

			_this.setEvent(_this, $target, $extPane);
		}

		$extPane.fadeIn(200, function() {
			!_this.options.initShow && $extPane.find('.' + tools.hideClass).trigger('click');
		});
	};

	/** 复制文本 */
	Class.prototype.copyText = function(_this, $target) {
		if(!$target) {
			return false;
		}!$('#' + tools.copyTextId).length && $('body').append('<textarea id=' + tools.copyTextId + ' readonly="readonly"></textarea>');
		var $copy = $('#' + tools.copyTextId),
			value = $target.val();
		$copy.val(value === '' ? ' ' : value).select();
		document.execCommand('copy');
		_this.showTip(_this, $target, '已复制！');
	};

	/** 设置内容长度 */
	Class.prototype.setValLength = function(_this, $target) {
		var $length = $target.next('.' + extClassName).find('.' + tools.countClass);
		$length.text($target.val().length);
		if($target.val().length > $target.attr('maxlength')) {
			$length.addClass(tools.lengthOverClass);
		} else if($length.hasClass(tools.lengthOverClass)) {
			$length.removeClass(tools.lengthOverClass);
		}
	}

	/** 设置工具条事件 */
	Class.prototype.setEvent = function(_this, $target, $extPane) {
		_this.setValLength(_this, $target);
		var initValue = $target.val();
		// 文本工具条按钮点击事件
		$extPane.on('click', 'a', function(e) {
			var $this = $(this),
				$icon = $this.children('i.layui-icon');
			if($this.hasClass(tools.hideClass)) {
				// 收起展开按钮事件
				$this.nextAll().toggle('fast');
				$this.prevAll().toggle('fast');
				if($icon.hasClass('layui-icon-more')) {
					$icon.removeClass('layui-icon-more').addClass('layui-icon-more-vertical');
					$this.attr(_this.tipsAttr, '展开');
					typeof _this.options.hideEnd === 'function' && _this.options.hideEnd($target, $extPane);
				} else {
					$icon.removeClass('layui-icon-more-vertical').addClass('layui-icon-more');
					$this.attr(_this.tipsAttr, '收起');
					typeof _this.options.showEnd === 'function' && _this.options.showEnd($target, $extPane);
				}
				_this.tipsAttr === tools.laytips && _this.showTip(_this, $this, $this.attr(_this.tipsAttr));
			}
			if($this.hasClass(tools.copyClass)) {
				// 复制按钮事件
				_this.copyText(_this, $target);
			}
			if($this.hasClass(tools.resetClass)) {
				// 重置按钮事件
				$target.val(initValue);
				_this.setValLength(_this, $target);
			}
			if($this.hasClass(tools.clearClass)) {
				// 清空按钮事件
				$target.val('');
				_this.setValLength(_this, $target);
			}

			layui.stope(e);
			return false;
		});
		// 字数统计事件
		$target.on('keyup input', function(e) {
			_this.setValLength(_this, $target);

			layui.stope(e);
			return false;
		});
	};

	/** 获取工具条节点 */
	Class.prototype.getToolsNode = function(_this, $target) {
		if(!$target) return false;
		// 总是显示收起展开按钮
		tools.hide = ['<a href="javascript:;"', _this.getTips(_this, '收起'), 'class="', tools.hideClass, '"><i class="layui-icon layui-icon-more"></i></a>'].join('');
		// 至少显示一个工具模块
		_this.options.tools = _this.options.tools || [tools.countName];
		// 字数统计
		tools.countIndex = _this.options.tools.indexOf(tools.countName);
		if(tools.countIndex > -1) {
			var maxlength = $target.attr('maxlength') || -1;
			tools.count = ['<span class="', tools.lengthClass, '"><b class="', tools.countClass, '"', _this.getTips(_this, '当前字数'), '>0</b>',
				(maxlength < 0 ? '' : ['/<span class="', tools.maxClass, '"', _this.getTips(_this, '最大字数'), '>', maxlength, '</span>'].join('')), '</span>'
			].join('');
		}
		// 复制内容
		tools.copyIndex = _this.options.tools.indexOf(tools.copyName);
		if(tools.copyIndex > -1) {
			tools.copy = ['<a href="javascript:;" class="', tools.copyClass, '"', _this.getTips(_this, '复制'),
				'><i class="fa fa-copy"></i></a>'
			].join('');
		}
		// 重置内容
		tools.resetIndex = _this.options.tools.indexOf(tools.resetName);
		if(tools.resetIndex > -1) {
			tools.reset = ['<a href="javascript:;" class="', tools.resetClass, '"', _this.getTips(_this, '重置'),
				'><i class="layui-icon layui-icon-refresh-1"></i></a>'
			].join('');
		}
		// 清空内容
		tools.clearIndex = _this.options.tools.indexOf(tools.clearName);
		if(tools.clearIndex > -1) {
			tools.clear = ['<a href="javascript:;" class="', tools.clearClass, '"', _this.getTips(_this, '清空'),
				'><i class="fa fa-close"></i></a>'
			].join('');
		}

		if(_this.options.align === 'left') {
			// 居左对齐
			alignRight = false;
			alignClass = extClassName + '-l';
		} else {
			// 居右对其
			alignRight = true;
			alignClass = extClassName + '-r';
		}
		// 处理工具条节点
		nodes = ['<span class="layui-unselect ', extClassName, ' ', alignClass, ' ', $.trim(_this.options.className),
			' layui-anim layui-anim-fadein"><span class="layui-badge layui-badge-rim">'
		];
		!alignRight && nodes.push(tools.hide);
		for(var i = 0; i < _this.options.tools.length; i++) {
			nodes.push(tools[_this.options.tools[i]] || '');
		}
		alignRight && nodes.push(tools.hide);
		nodes.push('</span></span>');

		return nodes.join('');
	};

	/** 获取提示信息属性 */
	Class.prototype.getTips = function(_this, msg) {
		switch(_this.options.tipType) {
			case 'title':
				_this.tipsAttr = 'title';
				break;
			case 'laytips':
				_this.tipsAttr = tools.laytips;
				break;
			default:
				return '';
		}

		return [' ', _this.tipsAttr, '=', msg, ' '].join('');
	};

	/** 初始化吸附提示 */
	Class.prototype.initTips = function(_this) {
		$('[' + tools.laytips + ']').each(function(i, n) {
			var $target = $(n);
			if($.trim($target.attr(_this.tipsAttr)) !== '') {
				$target.hover(function() {
					_this.showTip(_this, $target, $target.attr(_this.tipsAttr));
				}, _this.hideTip);
			}
		});
	};

	/** 显示吸附提示 */
	Class.prototype.showTip = function(_this, $target, msg) {
		_this.hideTip();
		layui.layer.tips(msg, $target, {
			tips: [1, _this.options.tipColor || '#01AAED'],
			time: 2e3,
			anim: 5,
			zIndex: (_this.options.zIndex || 0) + 2
		});
	};

	/** 隐藏吸附提示 */
	Class.prototype.hideTip = function() {
		layui.layer.closeAll('tips');
	};

	exports('textool', {
		/** 初始化入口方法 */
		init: function(custom) {
			return new Class(custom);
		}
	});

});

//layui.config({
//	base: 'js/modules/'
//}).extend({
//	numinput: 'textool/textool.min'
//}).use(['form', 'textool'], function() {
//	var $ = layui.$,
//		form = layui.form,
//		textool = layui.textool;
//	textool.init({
//		// 根据元素 id 值单独渲染，为空默认根据 class='layext-text-tool' 批量渲染
//		eleId: null,
//		// 批量设置输入框最大长度，可结合 eleId 单独设置最大长度
//		maxlength: -1,
//		// 初始化回调，无参
//		initEnd: $.noop,
//		// 显示回调，参数为当前输入框和工具条面板的 jQuery 对象
//		showEnd: $.noop,
//		// 隐藏回调，参数为当前输入框和工具条面板的 jQuery 对象
//		hideEnd: $.noop,
//		// 初始化展开，默认展开，否则收起
//		initShow: true,
//		// 启用指定工具模块，默认依次为字数统计、复制内容、重置内容、清空内容，按数组顺序显示
//		tools: ['count', 'copy', 'reset', 'clear'],
//		// 工具按钮提示类型，默认为 'title' 属性，可选 'laytips'，使用 layer 组件的吸附提示， 其他值不显示提示
//		tipType: 'title',
//		// 吸附提示背景颜色
//		tipColor: '#01AAED',
//		// 对齐方向，默认右对齐，可选左对齐 'left'
//		align: 'right',
//		// 工具条字体颜色
//		color: '#666666',
//		// 工具条背景颜色
//		bgColor: '#FFFFFF',
//		// 工具条边框颜色
//		borderColor: '#E6E6E6',
//		// 工具条附加样式类名
//		className: '',
//		// z-index
//		zIndex: 19891014
//	});
//});