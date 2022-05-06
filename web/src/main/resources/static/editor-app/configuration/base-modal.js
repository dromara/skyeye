/**
 * 公用信息提示modal
 * 
 * Created by HANZO on 2016/6/21.
 */
/*
 * <div class="modal fade" id="myModal" tabindex="-1" role="dialog">
 * 
 * <div class="modal-dialog" role="document">
 * 
 * <div class="modal-content">
 * 
 * <div class="modal-header"> <button type="button" class="close"
 * data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
 * <h4 class="modal-title">Modal title</h4> </div>
 * 
 * <div class="modal-body"> ... </div>
 * 
 * <div class="modal-footer"> <button type="button" class="btn btn-default"
 * data-dismiss="modal">Close</button> <button type="button" class="btn
 * btn-primary">Save changes</button> </div>
 * 
 * </div>
 * 
 * </div>
 * 
 * </div>
 */

(function($, window, document, undefined) {
	window.modals = window.modals || {};

	function _modal_structure(config, ok, cancel) {
		// <div id="myModal" class="modal fade" tabindex="-1" role="dialog">
		var modal = document.createElement('DIV');
		modal.id = 'modal-tips-div';
		modal.className = 'modal fade';
		modal.tabindex = -1;
		modal.role = 'dialog';
		modal.setAttribute('aria-labelledby', 'myModalLabel');

		// <div class="modal-dialog" role="document">
		var dialog = document.createElement('DIV');
		dialog.className = 'modal-dialog modal-' + (config.large ? 'lg' : 'sm');

		//update for HANZO, 2016/12/28
		if(config.width) {
			dialog.style.width = config.width + 'px';
		}

		dialog.role = 'document';
		modal.appendChild(dialog);

		// <div class="modal-content">
		var content = document.createElement('DIV');
		content.className = 'modal-content';
		dialog.appendChild(content);

		// <div class="modal-header">
		var header = document.createElement('DIV');
		header.className = 'modal-header';

		// <button type="button" class="close" data-dismiss="modal"
		// aria-label="Close"><span aria-hidden="true">&times;</span></button>
		var close_icon = document.createElement('BUTTON');
		close_icon.className = 'close';
		close_icon.type = 'button';
		close_icon.setAttribute('data-dismiss', 'modal');
		close_icon.setAttribute('aria-label', 'Close');
		close_icon.innerHTML = '<span aria-hidden="true">&times;</span>';
		header.appendChild(close_icon);

		// //<h4 class="modal-title">Modal title</h4>
		if(config.title) {
			var title = document.createElement('H5');
			title.id = 'myModalLabel';
			title.className = 'modal-title';
			title.innerHTML = config.title;
			header.appendChild(title);
		}
		
		content.appendChild(header);

		// <div class="modal-body">
		var body = document.createElement('DIV');
		body.className = 'modal-body';

		//update for HANZO, 2016/12/28
		if(config.src) {
			body.style.height = config.height + 'px';

			var frame = document.createElement('iframe');
			frame.style.margin = 0;
			frame.style.padding = 0;
			frame.style.width = '100%';
			frame.style.height = '100%';
			frame.style.border = 'none';
			frame.src = config.src;
			body.appendChild(frame);
		} else {
			body.innerHTML = config.text || '';
		}

		content.appendChild(body);

		if(ok || cancel || config.has_footer) {
			// <div class="modal-footer">
			var footer = document.createElement('DIV');
			footer.className = 'modal-footer';

			if(cancel) {
				// <button type="button" class="btn btn-default"
				// data-dismiss="modal">Close</button>
				var btn_close = document.createElement('BUTTON');
				btn_close.className = 'btn btn-default btn-sm';
				btn_close.type = 'button';
				btn_close.setAttribute('data-dismiss', 'modal');
				btn_close.innerHTML = config.cancel_label;
				btn_close.onclick = function() {
					if(config.cancel_call) {
						config.cancel_call.call(this);
					}
				};

				footer.appendChild(btn_close);
			}

			if(ok) {
				// <button type="button" class="btn btn-primary">Save</button>
				var btn_ok = document.createElement('BUTTON');
				btn_ok.className = 'btn btn-primary btn-sm';
				btn_ok.type = 'button';
				//btn_ok.setAttribute('data-dismiss', 'modal');
				btn_ok.innerHTML = config.ok_label;
				btn_ok.onclick = function() {
					$("#modal-tips-div").modal('hide');
					if(config.callback) {
						config.callback.call(this);
					}
				};
				footer.appendChild(btn_ok);
			}
			content.appendChild(footer);
		}
		return modal;
	}

	function _create_modal(config, ok, cancel) {
		_remove_modal();
		var modal = _modal_structure(config, ok, cancel);
		document.body.appendChild(modal);
		$('#modal-tips-div').modal('show');
		$('#modal-tips-div').on("hidden.bs.modal", function() {
			modals.fixwyhtml5();
			_remove_modal();
			$(this).removeData("bs.modal");
		});
		return modal;
	}

	function _remove_modal() {
		$("#modal-tips-div").remove();
		$("div.modal-backdrop").remove();
	}

	/**
	 * 目前阶段涉及参数说明 { title: 'modal标题', text: '提示内容', ok_label: '确定按钮的内容',
	 * cancel_label: '取消按钮的内容', callback: '确定按钮的回调函数', cancel_call: '取消按钮的回调函数',
	 * large: 'modal大小模式' }
	 */
	var _CONFIG = {
		ok_label: '确定',
		cancel_label: '关闭',
		large: false
	};

	function modal_params(text, callback) {
		return $.extend({}, _CONFIG, (typeof text != 'object') ? {
			text: text,
			callback: callback
		} : text);
	}

	var _TITLE = {
		info: '提示',
		correct: '成功',
		warn: '警告',
		error: '错误'
	};

	function _alert(args, type) {
		var cfg = modal_params.apply(this, args);
		cfg.title = cfg.title || _TITLE[type];
		return _create_modal(cfg, false, true);
	}

	/**
	 * 用法: 1.modals.info('...'); 2.modals.info({ ... });
	 * 
	 * correct、warn、error可不要。为统一规范尽量选择相应函数
	 * 
	 * @returns {*}
	 */
	modals.info = function() {
		return _alert.call(this, arguments, 'info');
	};

	modals.correct = function() {
		return _alert.call(this, arguments, 'correct');
	};

	modals.warn = function() {
		return _alert.call(this, arguments, 'warn');
	};

	modals.error = function() {
		return _alert.call(this, arguments, 'error');
	};

	/**
	 * 用法: 1.modals.confirm('...'); 2.modals.confirm({ ... });
	 * 
	 * @returns {*}
	 */
	modals.confirm = function() {
		var cfg = modal_params.apply(this, arguments);
		cfg.title = cfg.title || '确认提示';
		cfg.cancel_label = cfg.cancel_label == _CONFIG.cancel_label ? '取消' : cfg.cancel_label;

		return _create_modal(cfg, true, true);
	};

	modals.popup = function(cfg) {
		return _create_modal(cfg || {}, false, false);
	};

	//{"backdrop":"static"}点击背景不会消失  
	var _win_config = {
		winId: 'user-win',
		backdrop: true,
		keyboard: true,
		width: 900
	};
	modals.openWin = function(config) {
		var winId = config.winId || _win_config.winId;
		this.closeWin(winId);
		config = $.extend({}, _win_config, config);
		this._create_modal(config);
		if(!config.url && !config.loadContent) {
			this.error('未配置url');
		}
		$("#" + winId).modal({
			remote: config.url,
			backdrop: config.backdrop || _win_config.backdrop,
			keyboard: config.keyboard || _win_config.keyboard
		});
		if(config.loadContent) {
			config.loadContent();
		}
		this.showWin(config.winId || _win_config.winId);
		$("#" + winId).on('shown.bs.modal', function() {
			$(this).find(".modal-title").html(config.title);
			if(config.showFunc)
				config.showFunc.call(this);
		});
		$("#" + winId).on("hidden.bs.modal", function() {
			modals.closeWin(winId);
			if(config.hideFunc)
				config.hideFunc.call(this);
			modals.fixwyhtml5();
			$(this).removeData("bs.modal");

		});
	}
	modals.fixwyhtml5 = function() {
		if($(".wysihtml5-editor")) {
			$(document.body).removeClass('modal-open');
			$(document.body).css("padding-right", "0px");
		}
	}
	modals._create_modal = function(config) {
		var modal = document.createElement('DIV');
		modal.id = config.winId;
		modal.className = 'modal fade';
		modal.tabindex = -1;
		modal.role = 'dialog';
		modal.setAttribute('aria-labelledby', 'myModalLabel');
		modal.setAttribute('aria-hidden', true);

		var dialog = document.createElement('DIV');
		dialog.className = 'modal-dialog';
		dialog.role = 'document';
		if(isNaN(config.width) && config.width.indexOf("px") > 0)
			$(dialog).css('width', config.width);
		else
			$(dialog).css('width', config.width + "px");
		modal.appendChild(dialog);

		var content = document.createElement('DIV');
		content.className = 'modal-content';
		dialog.appendChild(content);

		document.body.appendChild(modal);
	}

	modals.closeWin = function(winId) {
		winId = winId || _win_config.winId;
		var win = document.getElementById(winId);
		if(win) {
			$(win).remove();
			$("div.modal-backdrop").remove();
		}
	}

	modals.hideWin = function(winId) {
		winId = winId || _win_config.winId;
		$("#" + winId).modal('hide');
	}

	modals.showWin = function(winId) {
		winId = winId || _win_config.winId;
		$("#" + winId).modal('show');
	}

	modals.removeData = function(winId) {
		winId = winId || _win_config.winId;
		$("#" + winId).removeData("bs.modal");
	}
})(jQuery, window, document);