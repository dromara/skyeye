layui.define(["jquery"], function(exports) {
	var jQuery = layui.jquery;
	(function($) {
		var opt;

		$.fn.jqprint = function(options) {
			opt = $.extend({}, $.fn.jqprint.defaults, options);

			var $element = (this instanceof jQuery) ? this : $(this);

			if(opt.operaSupport && $.support.opera) {
				var tab = window.open("", "jqPrint-preview");
				tab.document.open();

				var doc = tab.document;
			} else {
				var $iframe = $("<iframe  />");

				if(!opt.debug) {
					$iframe.css({
						position: "absolute",
						width: "0px",
						height: "0px",
						left: "-600px",
						top: "-600px"
					});
				}

				$iframe.appendTo("body");
				var doc = $iframe[0].contentWindow.document;
			}

			if(opt.importCSS) {
				if($("link[media=print]").length > 0) {
					$("link[media=print]").each(function() {
						doc.write("<link type='text/css' rel='stylesheet' href='" + $(this).attr("href") + "' media='print' />");
					});
				} else {
					$("link").each(function() {
						doc.write("<link type='text/css' rel='stylesheet' href='" + $(this).attr("href") + "' />");
					});
				}
			}
			
			//是否展示页眉
			var headHtml = opt.header ? "<head><style type='text/css' media='print'> @page{size: auto; margin: 0mm;}html{margin: 0px;}</style></head><body>" : "";
			
			if(opt.printContainer) {
				doc.write(headHtml + $element.outer());
			} else {
				$element.each(function() {
					doc.write($(this).html());
				});
			}

			doc.close();

			(opt.operaSupport && $.support.opera ? tab : $iframe[0].contentWindow).focus();
			setTimeout(function() {
				(opt.operaSupport && $.support.opera ? tab : $iframe[0].contentWindow).print();
				if(tab) {
					tab.close();
				}
			}, 1000);
		}

		$.fn.jqprint.defaults = {
			title: "", //页眉展示内容
			header: true, //页眉是否展示
			debug: false, //如果是true则可以显示iframe查看效果（iframe默认高和宽都很小，可以再源码中调大），默认是false
			importCSS: true, //true表示引进原来的页面的css，默认是true。（如果是true，先会找$("link[media=print]")，若没有会去找$("link")中的css文件）
			printContainer: true, //表示如果原来选择的对象必须被纳入打印（注意：设置为false可能会打破你的CSS规则）。
			operaSupport: true//表示如果插件也必须支持歌opera浏览器，在这种情况下，它提供了建立一个临时的打印选项卡。默认是true
		};

		// Thanks to 9__, found at http://users.livejournal.com/9__/380664.html
		jQuery.fn.outer = function() {
			return $($('<div></div>').html(this.clone())).html();
		}
	})(jQuery);
	exports('jqprint', null);
});