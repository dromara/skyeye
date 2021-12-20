/*!
 * smallPop 0.1.2 | https://github.com/silvio-r/spop
 * Copyright (c) 2015 Sílvio Rosa @silvior_
 * MIT license
 */

layui.define(["jquery"], function(exports) {
	var jQuery = layui.jquery;
(function($){
;(function() {
	'use strict';

	var animationTime = 390;
	var options, defaults, container, icon, layout, popStyle, positions, close;

	var SmallPop = function(template, style) {
		this.defaults = {
			// 消息提示的模板。可以是一个字符串，或这是HTML代码。
			template  : null,
			// toast消息提示的主题样式，可以是info，success， warning 或 error。
			style     : 'info',
			// 是否自动关闭。
			autoclose : false,
			// toast消息提示的位置。可以是：top-right，top-left，top-center，bottom-left，bottom-center或bottom-right。
			position  : 'top-right',
			// 是否显示图标。
			icon      : true,
			// 是否对消息进行分组。
			group     : false,
			// toast消息提示打开时的回调函数。
			onOpen    : false,
			// toast消息提示关闭时的回调函数spop.js 
			onClose   : false
		};
		defaults = extend(this.defaults, spop.defaults);

		if ( typeof template === 'string' || typeof style === 'string' ) {
			options = { template: template, style: style || defaults.style};
		} else if (typeof template === 'object') {
			options = template;
		} else {
			console.error('Invalid arguments.');
			return false;
		}

		this.opt = extend( defaults, options);
		if ($('spop--' + this.opt.group)) {
			this.remove($('spop--' + this.opt.group));
		}
		this.open();
	};

	SmallPop.prototype.create = function(template) {
		container = $(this.getPosition('spop--', this.opt.position));
		icon = (!this.opt.icon) ? '' : '<i class="spop-icon '+
		            this.getStyle('spop-icon--', this.opt.style) +'"></i>';
		layout ='<div class="spop-close" data-spop="close" aria-label="Close">&times;</div>' +
						icon +
					'<div class="spop-body">' +
						template +
					'</div>';

		if (!container) {
			this.popContainer = document.createElement('div');
			this.popContainer.setAttribute('class', 'spop-container ' +
				this.getPosition('spop--', this.opt.position));
			this.popContainer.setAttribute('id', this.getPosition('spop--', this.opt.position));
			document.body.appendChild(this.popContainer);
			container = $(this.getPosition('spop--', this.opt.position));
		}

		this.pop = document.createElement('div');
		this.pop.setAttribute('class', 'spop spop--out spop--in ' + this.getStyle('spop--', this.opt.style) );

		if (this.opt.group && typeof this.opt.group === 'string') {
			this.pop.setAttribute('id', 'spop--' + this.opt.group);
		}
		this.pop.setAttribute('role', 'alert');
		this.pop.innerHTML = layout;
		container.appendChild(this.pop);
	};

	SmallPop.prototype.getStyle = function(sufix, arg) {
		popStyle = {
			'success': 'success',
			'error'  : 'error',
			'warning': 'warning'
		};
		return sufix + (popStyle[arg] || 'info');
	};

	SmallPop.prototype.getPosition = function(sufix, position) {
		positions = {
			'top-left'     : 'top-left',
			'top-center'   : 'top-center',
			'top-right'    : 'top-right',
			'bottom-left'  : 'bottom-left',
			'bottom-center': 'bottom-center',
			'bottom-right' : 'bottom-right'
		};
		return sufix + (positions[position] || 'top-right');
	};

	SmallPop.prototype.open = function() {
		this.create(this.opt.template);
		if (this.opt.onOpen) { this.opt.onOpen();}
		this.close();
	};

	SmallPop.prototype.close = function () {
		if (this.opt.autoclose && typeof this.opt.autoclose === 'number') {
			this.autocloseTimer = setTimeout( this.remove.bind(this, this.pop), this.opt.autoclose);
		}
		this.pop.addEventListener('click', this.addListeners.bind(this) , false);
	};

	SmallPop.prototype.addListeners = function(event) {
		close = event.target.getAttribute('data-spop');
		if (close === 'close') {
			if (this.autocloseTimer) { clearTimeout(this.autocloseTimer);}
			this.remove(this.pop);
		}
	};

	SmallPop.prototype.remove = function(elm) {
		if (this.opt.onClose) { this.opt.onClose();}
		removeClass(elm, 'spop--in');
		setTimeout( function () {
			if(document.body.contains(elm)) {
				elm.parentNode.removeChild(elm);
			}
		}, animationTime);
	};
	// Helpers
	function $(el, con) {
		return typeof el === 'string'? (con || document).getElementById(el) : el || null;
	}

	function removeClass(el, className) {
		if (el.classList) {
			el.classList.remove(className);
		} else {
			el.className = el.className.replace(new RegExp('(^|\\b)' +
							className.split(' ').join('|') +
							'(\\b|$)', 'gi'), ' ');
		}
	}

	function extend(obj, src) {
		for (var key in src) {
			if (src.hasOwnProperty(key)) obj[key] = src[key];
		}
		return obj;
	}

	window.spop = function (template, style) {
		if ( !template || !window.addEventListener ) { return false;}
		return new SmallPop(template, style);
	};

	spop.defaults = {};
}());
}(jQuery));
	layui.link(basePath + '../../lib/layui/lay/modules/spop/spop.css');
	exports('spop', null);
});
