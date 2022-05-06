/*!WebUploader 0.1.5*/
layui.define(["jquery"], function(exports) {
	var jQuery = layui.jquery;
	(function($) {
		(function(root, factory) {
			var modules = {},
				_require = function(deps, callback) {
					var args, len, i;
					if(typeof deps === 'string') {
						return getModule(deps);
					} else {
						args = [];
						for(len = deps.length, i = 0; i < len; i++) {
							args.push(getModule(deps[i]));
						}
						return callback.apply(null, args);
					}
				},
				_define = function(id, deps, factory) {
					if(arguments.length === 2) {
						factory = deps;
						deps = null;
					}
					_require(deps || [], function() {
						setModule(id, factory, arguments);
					});
				},
				setModule = function(id, factory, args) {
					var module = {
							exports: factory
						},
						returned;
					if(typeof factory === 'function') {
						args.length || (args = [_require, module.exports, module]);
						returned = factory.apply(null, args);
						returned !== undefined && (module.exports = returned);
					}
					modules[id] = module.exports;
				},
				getModule = function(id) {
					var module = modules[id] || root[id];
					if(!module) {
						throw new Error('`' + id + '` is undefined');
					}
					return module;
				},
				exportsTo = function(obj) {
					var key, host, parts, part, last, ucFirst;
					ucFirst = function(str) {
						return str && (str.charAt(0).toUpperCase() + str.substr(1));
					};
					for(key in modules) {
						host = obj;
						if(!modules.hasOwnProperty(key)) {
							continue;
						}
						parts = key.split('/');
						last = ucFirst(parts.pop());
						while((part = ucFirst(parts.shift()))) {
							host[part] = host[part] || {};
							host = host[part];
						}
						host[last] = modules[key];
					}
					return obj;
				},
				makeExport = function(dollar) {
					root.__dollar = dollar;
					return exportsTo(factory(root, _define, _require));
				},
				origin;
			if(typeof module === 'object' && typeof module.exports === 'object') {
				module.exports = makeExport();
			} else if(typeof define === 'function' && define.amd) {
				define(['jquery'], makeExport);
			} else {
				origin = root.WebUploader;
				root.WebUploader = makeExport();
				root.WebUploader.noConflict = function() {
					root.WebUploader = origin;
				};
			}
		})(window, function(window, define, require) {
			define('dollar-third', [], function() {
				var $ = window.__dollar || window.jQuery || window.Zepto;
				if(!$) {
					throw new Error('jQuery or Zepto not found!');
				}
				return $;
			});
			define('dollar', ['dollar-third'], function(_) {
				return _;
			});
			define('promise-third', ['dollar'], function($) {
				return {
					Deferred: $.Deferred,
					when: $.when,
					isPromise: function(anything) {
						return anything && typeof anything.then === 'function';
					}
				};
			});
			define('promise', ['promise-third'], function(_) {
				return _;
			});
			define('base', ['dollar', 'promise'], function($, promise) {
				var noop = function() {},
					call = Function.call;

				function uncurryThis(fn) {
					return function() {
						return call.apply(fn, arguments);
					};
				}

				function bindFn(fn, context) {
					return function() {
						return fn.apply(context, arguments);
					};
				}

				function createObject(proto) {
					var f;
					if(Object.create) {
						return Object.create(proto);
					} else {
						f = function() {};
						f.prototype = proto;
						return new f();
					}
				}
				return {
					version: '0.1.5',
					$: $,
					Deferred: promise.Deferred,
					isPromise: promise.isPromise,
					when: promise.when,
					browser: (function(ua) {
						var ret = {},
							webkit = ua.match(/WebKit\/([\d.]+)/),
							chrome = ua.match(/Chrome\/([\d.]+)/) || ua.match(/CriOS\/([\d.]+)/),
							ie = ua.match(/MSIE\s([\d\.]+)/) || ua.match(/(?:trident)(?:.*rv:([\w.]+))?/i),
							firefox = ua.match(/Firefox\/([\d.]+)/),
							safari = ua.match(/Safari\/([\d.]+)/),
							opera = ua.match(/OPR\/([\d.]+)/);
						webkit && (ret.webkit = parseFloat(webkit[1]));
						chrome && (ret.chrome = parseFloat(chrome[1]));
						ie && (ret.ie = parseFloat(ie[1]));
						firefox && (ret.firefox = parseFloat(firefox[1]));
						safari && (ret.safari = parseFloat(safari[1]));
						opera && (ret.opera = parseFloat(opera[1]));
						return ret;
					})(navigator.userAgent),
					os: (function(ua) {
						var ret = {},
							android = ua.match(/(?:Android);?[\s\/]+([\d.]+)?/),
							ios = ua.match(/(?:iPad|iPod|iPhone).*OS\s([\d_]+)/);
						android && (ret.android = parseFloat(android[1]));
						ios && (ret.ios = parseFloat(ios[1].replace(/_/g, '.')));
						return ret;
					})(navigator.userAgent),
					inherits: function(Super, protos, staticProtos) {
						var child;
						if(typeof protos === 'function') {
							child = protos;
							protos = null;
						} else if(protos && protos.hasOwnProperty('constructor')) {
							child = protos.constructor;
						} else {
							child = function() {
								return Super.apply(this, arguments);
							};
						}
						$.extend(true, child, Super, staticProtos || {});
						child.__super__ = Super.prototype;
						child.prototype = createObject(Super.prototype);
						protos && $.extend(true, child.prototype, protos);
						return child;
					},
					noop: noop,
					bindFn: bindFn,
					log: (function() {
						if(window.console) {
							return bindFn(console.log, console);
						}
						return noop;
					})(),
					nextTick: (function() {
						return function(cb) {
							setTimeout(cb, 1);
						};
					})(),
					slice: uncurryThis([].slice),
					guid: (function() {
						var counter = 0;
						return function(prefix) {
							var guid = (+new Date()).toString(32),
								i = 0;
							for(; i < 5; i++) {
								guid += Math.floor(Math.random() * 65535).toString(32);
							}
							return(prefix || 'wu_') + guid + (counter++).toString(32);
						};
					})(),
					formatSize: function(size, pointLength, units) {
						var unit;
						units = units || ['B', 'K', 'M', 'G', 'TB'];
						while((unit = units.shift()) && size > 1024) {
							size = size / 1024;
						}
						return(unit === 'B' ? size : size.toFixed(pointLength || 2)) +
							unit;
					}
				};
			});
			define('mediator', ['base'], function(Base) {
				var $ = Base.$,
					slice = [].slice,
					separator = /\s+/,
					protos;

				function findHandlers(arr, name, callback, context) {
					return $.grep(arr, function(handler) {
						return handler && (!name || handler.e === name) && (!callback || handler.cb === callback || handler.cb._cb === callback) && (!context || handler.ctx === context);
					});
				}

				function eachEvent(events, callback, iterator) {
					$.each((events || '').split(separator), function(_, key) {
						iterator(key, callback);
					});
				}

				function triggerHanders(events, args) {
					var stoped = false,
						i = -1,
						len = events.length,
						handler;
					while(++i < len) {
						handler = events[i];
						if(handler.cb.apply(handler.ctx2, args) === false) {
							stoped = true;
							break;
						}
					}
					return !stoped;
				}
				protos = {
					on: function(name, callback, context) {
						var me = this,
							set;
						if(!callback) {
							return this;
						}
						set = this._events || (this._events = []);
						eachEvent(name, callback, function(name, callback) {
							var handler = {
								e: name
							};
							handler.cb = callback;
							handler.ctx = context;
							handler.ctx2 = context || me;
							handler.id = set.length;
							set.push(handler);
						});
						return this;
					},
					once: function(name, callback, context) {
						var me = this;
						if(!callback) {
							return me;
						}
						eachEvent(name, callback, function(name, callback) {
							var once = function() {
								me.off(name, once);
								return callback.apply(context || me, arguments);
							};
							once._cb = callback;
							me.on(name, once, context);
						});
						return me;
					},
					off: function(name, cb, ctx) {
						var events = this._events;
						if(!events) {
							return this;
						}
						if(!name && !cb && !ctx) {
							this._events = [];
							return this;
						}
						eachEvent(name, cb, function(name, cb) {
							$.each(findHandlers(events, name, cb, ctx), function() {
								delete events[this.id];
							});
						});
						return this;
					},
					trigger: function(type) {
						var args, events, allEvents;
						if(!this._events || !type) {
							return this;
						}
						args = slice.call(arguments, 1);
						events = findHandlers(this._events, type);
						allEvents = findHandlers(this._events, 'all');
						return triggerHanders(events, args) && triggerHanders(allEvents, arguments);
					}
				};
				return $.extend({
					installTo: function(obj) {
						return $.extend(obj, protos);
					}
				}, protos);
			});
			define('uploader', ['base', 'mediator'], function(Base, Mediator) {
				var $ = Base.$;

				function Uploader(opts) {
					this.options = $.extend(true, {}, Uploader.options, opts);
					this._init(this.options);
				}
				Uploader.options = {};
				Mediator.installTo(Uploader.prototype);
				$.each({
					upload: 'start-upload',
					stop: 'stop-upload',
					getFile: 'get-file',
					getFiles: 'get-files',
					addFile: 'add-file',
					addFiles: 'add-file',
					sort: 'sort-files',
					removeFile: 'remove-file',
					cancelFile: 'cancel-file',
					skipFile: 'skip-file',
					retry: 'retry',
					isInProgress: 'is-in-progress',
					makeThumb: 'make-thumb',
					md5File: 'md5-file',
					getDimension: 'get-dimension',
					addButton: 'add-btn',
					predictRuntimeType: 'predict-runtime-type',
					refresh: 'refresh',
					disable: 'disable',
					enable: 'enable',
					reset: 'reset'
				}, function(fn, command) {
					Uploader.prototype[fn] = function() {
						return this.request(command, arguments);
					};
				});
				$.extend(Uploader.prototype, {
					state: 'pending',
					_init: function(opts) {
						var me = this;
						me.request('init', opts, function() {
							me.state = 'ready';
							me.trigger('ready');
						});
					},
					option: function(key, val) {
						var opts = this.options;
						if(arguments.length > 1) {
							if($.isPlainObject(val) && $.isPlainObject(opts[key])) {
								$.extend(opts[key], val);
							} else {
								opts[key] = val;
							}
						} else {
							return key ? opts[key] : opts;
						}
					},
					getStats: function() {
						var stats = this.request('get-stats');
						return stats ? {
							successNum: stats.numOfSuccess,
							progressNum: stats.numOfProgress,
							cancelNum: stats.numOfCancel,
							invalidNum: stats.numOfInvalid,
							uploadFailNum: stats.numOfUploadFailed,
							queueNum: stats.numOfQueue,
							interruptNum: stats.numofInterrupt
						} : {};
					},
					trigger: function(type) {
						var args = [].slice.call(arguments, 1),
							opts = this.options,
							name = 'on' + type.substring(0, 1).toUpperCase() +
							type.substring(1);
						if(Mediator.trigger.apply(this, arguments) === false || $.isFunction(opts[name]) && opts[name].apply(this, args) === false || $.isFunction(this[name]) && this[name].apply(this, args) === false || Mediator.trigger.apply(Mediator, [this, type].concat(args)) === false) {
							return false;
						}
						return true;
					},
					destroy: function() {
						this.request('destroy', arguments);
						this.off();
					},
					request: Base.noop
				});
				Base.create = Uploader.create = function(opts) {
					return new Uploader(opts);
				};
				Base.Uploader = Uploader;
				return Uploader;
			});
			define('runtime/runtime', ['base', 'mediator'], function(Base, Mediator) {
				var $ = Base.$,
					factories = {},
					getFirstKey = function(obj) {
						for(var key in obj) {
							if(obj.hasOwnProperty(key)) {
								return key;
							}
						}
						return null;
					};

				function Runtime(options) {
					this.options = $.extend({
						container: document.body
					}, options);
					this.uid = Base.guid('rt_');
				}
				$.extend(Runtime.prototype, {
					getContainer: function() {
						var opts = this.options,
							parent, container;
						if(this._container) {
							return this._container;
						}
						parent = $(opts.container || document.body);
						container = $(document.createElement('div'));
						container.attr('id', 'rt_' + this.uid);
						container.css({
							position: 'absolute',
							top: '0px',
							left: '0px',
							width: '1px',
							height: '1px',
							overflow: 'hidden'
						});
						parent.append(container);
						parent.addClass('webuploader-container');
						this._container = container;
						this._parent = parent;
						return container;
					},
					init: Base.noop,
					exec: Base.noop,
					destroy: function() {
						this._container && this._container.remove();
						this._parent && this._parent.removeClass('webuploader-container');
						this.off();
					}
				});
				Runtime.orders = 'html5,flash';
				Runtime.addRuntime = function(type, factory) {
					factories[type] = factory;
				};
				Runtime.hasRuntime = function(type) {
					return !!(type ? factories[type] : getFirstKey(factories));
				};
				Runtime.create = function(opts, orders) {
					var type, runtime;
					orders = orders || Runtime.orders;
					$.each(orders.split(/\s*,\s*/g), function() {
						if(factories[this]) {
							type = this;
							return false;
						}
					});
					type = type || getFirstKey(factories);
					if(!type) {
						throw new Error('Runtime Error');
					}
					runtime = new factories[type](opts);
					return runtime;
				};
				Mediator.installTo(Runtime.prototype);
				return Runtime;
			});
			define('runtime/client', ['base', 'mediator', 'runtime/runtime'], function(Base, Mediator, Runtime) {
				var cache;
				cache = (function() {
					var obj = {};
					return {
						add: function(runtime) {
							obj[runtime.uid] = runtime;
						},
						get: function(ruid, standalone) {
							var i;
							if(ruid) {
								return obj[ruid];
							}
							for(i in obj) {
								if(standalone && obj[i].__standalone) {
									continue;
								}
								return obj[i];
							}
							return null;
						},
						remove: function(runtime) {
							delete obj[runtime.uid];
						}
					};
				})();

				function RuntimeClient(component, standalone) {
					var deferred = Base.Deferred(),
						runtime;
					this.uid = Base.guid('client_');
					this.runtimeReady = function(cb) {
						return deferred.done(cb);
					};
					this.connectRuntime = function(opts, cb) {
						if(runtime) {
							throw new Error('already connected!');
						}
						deferred.done(cb);
						if(typeof opts === 'string' && cache.get(opts)) {
							runtime = cache.get(opts);
						}
						runtime = runtime || cache.get(null, standalone);
						if(!runtime) {
							runtime = Runtime.create(opts, opts.runtimeOrder);
							runtime.__promise = deferred.promise();
							runtime.once('ready', deferred.resolve);
							runtime.init();
							cache.add(runtime);
							runtime.__client = 1;
						} else {
							Base.$.extend(runtime.options, opts);
							runtime.__promise.then(deferred.resolve);
							runtime.__client++;
						}
						standalone && (runtime.__standalone = standalone);
						return runtime;
					};
					this.getRuntime = function() {
						return runtime;
					};
					this.disconnectRuntime = function() {
						if(!runtime) {
							return;
						}
						runtime.__client--;
						if(runtime.__client <= 0) {
							cache.remove(runtime);
							delete runtime.__promise;
							runtime.destroy();
						}
						runtime = null;
					};
					this.exec = function() {
						if(!runtime) {
							return;
						}
						var args = Base.slice(arguments);
						component && args.unshift(component);
						return runtime.exec.apply(this, args);
					};
					this.getRuid = function() {
						return runtime && runtime.uid;
					};
					this.destroy = (function(destroy) {
						return function() {
							destroy && destroy.apply(this, arguments);
							this.trigger('destroy');
							this.off();
							this.exec('destroy');
							this.disconnectRuntime();
						};
					})(this.destroy);
				}
				Mediator.installTo(RuntimeClient.prototype);
				return RuntimeClient;
			});
			define('lib/dnd', ['base', 'mediator', 'runtime/client'], function(Base, Mediator, RuntimeClent) {
				var $ = Base.$;

				function DragAndDrop(opts) {
					opts = this.options = $.extend({}, DragAndDrop.options, opts);
					opts.container = $(opts.container);
					if(!opts.container.length) {
						return;
					}
					RuntimeClent.call(this, 'DragAndDrop');
				}
				DragAndDrop.options = {
					accept: null,
					disableGlobalDnd: false
				};
				Base.inherits(RuntimeClent, {
					constructor: DragAndDrop,
					init: function() {
						var me = this;
						me.connectRuntime(me.options, function() {
							me.exec('init');
							me.trigger('ready');
						});
					}
				});
				Mediator.installTo(DragAndDrop.prototype);
				return DragAndDrop;
			});
			define('widgets/widget', ['base', 'uploader'], function(Base, Uploader) {
				var $ = Base.$,
					_init = Uploader.prototype._init,
					_destroy = Uploader.prototype.destroy,
					IGNORE = {},
					widgetClass = [];

				function isArrayLike(obj) {
					if(!obj) {
						return false;
					}
					var length = obj.length,
						type = $.type(obj);
					if(obj.nodeType === 1 && length) {
						return true;
					}
					return type === 'array' || type !== 'function' && type !== 'string' && (length === 0 || typeof length === 'number' && length > 0 && (length - 1) in obj);
				}

				function Widget(uploader) {
					this.owner = uploader;
					this.options = uploader.options;
				}
				$.extend(Widget.prototype, {
					init: Base.noop,
					invoke: function(apiName, args) {
						var map = this.responseMap;
						if(!map || !(apiName in map) || !(map[apiName] in this) || !$.isFunction(this[map[apiName]])) {
							return IGNORE;
						}
						return this[map[apiName]].apply(this, args);
					},
					request: function() {
						return this.owner.request.apply(this.owner, arguments);
					}
				});
				$.extend(Uploader.prototype, {
					_init: function() {
						var me = this,
							widgets = me._widgets = [],
							deactives = me.options.disableWidgets || '';
						$.each(widgetClass, function(_, klass) {
							(!deactives || !~deactives.indexOf(klass._name)) && widgets.push(new klass(me));
						});
						return _init.apply(me, arguments);
					},
					request: function(apiName, args, callback) {
						var i = 0,
							widgets = this._widgets,
							len = widgets && widgets.length,
							rlts = [],
							dfds = [],
							widget, rlt, promise, key;
						args = isArrayLike(args) ? args : [args];
						for(; i < len; i++) {
							widget = widgets[i];
							rlt = widget.invoke(apiName, args);
							if(rlt !== IGNORE) {
								if(Base.isPromise(rlt)) {
									dfds.push(rlt);
								} else {
									rlts.push(rlt);
								}
							}
						}
						if(callback || dfds.length) {
							promise = Base.when.apply(Base, dfds);
							key = promise.pipe ? 'pipe' : 'then';
							return promise[key](function() {
								var deferred = Base.Deferred(),
									args = arguments;
								if(args.length === 1) {
									args = args[0];
								}
								setTimeout(function() {
									deferred.resolve(args);
								}, 1);
								return deferred.promise();
							})[callback ? key : 'done'](callback || Base.noop);
						} else {
							return rlts[0];
						}
					},
					destroy: function() {
						_destroy.apply(this, arguments);
						this._widgets = null;
					}
				});
				Uploader.register = Widget.register = function(responseMap, widgetProto) {
					var map = {
							init: 'init',
							destroy: 'destroy',
							name: 'anonymous'
						},
						klass;
					if(arguments.length === 1) {
						widgetProto = responseMap;
						$.each(widgetProto, function(key) {
							if(key[0] === '_' || key === 'name') {
								key === 'name' && (map.name = widgetProto.name);
								return;
							}
							map[key.replace(/[A-Z]/g, '-$&').toLowerCase()] = key;
						});
					} else {
						map = $.extend(map, responseMap);
					}
					widgetProto.responseMap = map;
					klass = Base.inherits(Widget, widgetProto);
					klass._name = map.name;
					widgetClass.push(klass);
					return klass;
				};
				Uploader.unRegister = Widget.unRegister = function(name) {
					if(!name || name === 'anonymous') {
						return;
					}
					for(var i = widgetClass.length; i--;) {
						if(widgetClass[i]._name === name) {
							widgetClass.splice(i, 1)
						}
					}
				};
				return Widget;
			});
			define('widgets/filednd', ['base', 'uploader', 'lib/dnd', 'widgets/widget'], function(Base, Uploader, Dnd) {
				var $ = Base.$;
				Uploader.options.dnd = '';
				return Uploader.register({
					name: 'dnd',
					init: function(opts) {
						if(!opts.dnd || this.request('predict-runtime-type') !== 'html5') {
							return;
						}
						var me = this,
							deferred = Base.Deferred(),
							options = $.extend({}, {
								disableGlobalDnd: opts.disableGlobalDnd,
								container: opts.dnd,
								accept: opts.accept
							}),
							dnd;
						this.dnd = dnd = new Dnd(options);
						dnd.once('ready', deferred.resolve);
						dnd.on('drop', function(files) {
							me.request('add-file', [files]);
						});
						dnd.on('accept', function(items) {
							return me.owner.trigger('dndAccept', items);
						});
						dnd.init();
						return deferred.promise();
					},
					destroy: function() {
						this.dnd && this.dnd.destroy();
					}
				});
			});
			define('lib/filepaste', ['base', 'mediator', 'runtime/client'], function(Base, Mediator, RuntimeClent) {
				var $ = Base.$;

				function FilePaste(opts) {
					opts = this.options = $.extend({}, opts);
					opts.container = $(opts.container || document.body);
					RuntimeClent.call(this, 'FilePaste');
				}
				Base.inherits(RuntimeClent, {
					constructor: FilePaste,
					init: function() {
						var me = this;
						me.connectRuntime(me.options, function() {
							me.exec('init');
							me.trigger('ready');
						});
					}
				});
				Mediator.installTo(FilePaste.prototype);
				return FilePaste;
			});
			define('widgets/filepaste', ['base', 'uploader', 'lib/filepaste', 'widgets/widget'], function(Base, Uploader, FilePaste) {
				var $ = Base.$;
				return Uploader.register({
					name: 'paste',
					init: function(opts) {
						if(!opts.paste || this.request('predict-runtime-type') !== 'html5') {
							return;
						}
						var me = this,
							deferred = Base.Deferred(),
							options = $.extend({}, {
								container: opts.paste,
								accept: opts.accept
							}),
							paste;
						this.paste = paste = new FilePaste(options);
						paste.once('ready', deferred.resolve);
						paste.on('paste', function(files) {
							me.owner.request('add-file', [files]);
						});
						paste.init();
						return deferred.promise();
					},
					destroy: function() {
						this.paste && this.paste.destroy();
					}
				});
			});
			define('lib/blob', ['base', 'runtime/client'], function(Base, RuntimeClient) {
				function Blob(ruid, source) {
					var me = this;
					me.source = source;
					me.ruid = ruid;
					this.size = source.size || 0;
					if(!source.type && this.ext && ~'jpg,jpeg,png,gif,bmp'.indexOf(this.ext)) {
						this.type = 'image/' + (this.ext === 'jpg' ? 'jpeg' : this.ext);
					} else {
						this.type = source.type || 'application/octet-stream';
					}
					RuntimeClient.call(me, 'Blob');
					this.uid = source.uid || this.uid;
					if(ruid) {
						me.connectRuntime(ruid);
					}
				}
				Base.inherits(RuntimeClient, {
					constructor: Blob,
					slice: function(start, end) {
						return this.exec('slice', start, end);
					},
					getSource: function() {
						return this.source;
					}
				});
				return Blob;
			});
			define('lib/file', ['base', 'lib/blob'], function(Base, Blob) {
				var uid = 1,
					rExt = /\.([^.]+)$/;

				function File(ruid, file) {
					var ext;
					this.name = file.name || ('untitled' + uid++);
					ext = rExt.exec(file.name) ? RegExp.$1.toLowerCase() : '';
					if(!ext && file.type) {
						ext = /\/(jpg|jpeg|png|gif|bmp)$/i.exec(file.type) ? RegExp.$1.toLowerCase() : '';
						this.name += '.' + ext;
					}
					this.ext = ext;
					this.lastModifiedDate = file.lastModifiedDate || (new Date()).toLocaleString();
					Blob.apply(this, arguments);
				}
				return Base.inherits(Blob, File);
			});
			define('lib/filepicker', ['base', 'runtime/client', 'lib/file'], function(Base, RuntimeClent, File) {
				var $ = Base.$;

				function FilePicker(opts) {
					opts = this.options = $.extend({}, FilePicker.options, opts);
					opts.container = $(opts.id);
					if(!opts.container.length) {
						throw new Error('鎸夐挳鎸囧畾閿欒');
					}
					opts.innerHTML = opts.innerHTML || opts.label || opts.container.html() || '';
					opts.button = $(opts.button || document.createElement('div'));
					opts.button.html(opts.innerHTML);
					opts.container.html(opts.button);
					RuntimeClent.call(this, 'FilePicker', true);
				}
				FilePicker.options = {
					button: null,
					container: null,
					label: null,
					innerHTML: null,
					multiple: true,
					accept: null,
					name: 'file'
				};
				Base.inherits(RuntimeClent, {
					constructor: FilePicker,
					init: function() {
						var me = this,
							opts = me.options,
							button = opts.button;
						button.addClass('webuploader-pick');
						me.on('all', function(type) {
							var files;
							switch(type) {
								case 'mouseenter':
									button.addClass('webuploader-pick-hover');
									break;
								case 'mouseleave':
									button.removeClass('webuploader-pick-hover');
									break;
								case 'change':
									files = me.exec('getFiles');
									me.trigger('select', $.map(files, function(file) {
										file = new File(me.getRuid(), file);
										file._refer = opts.container;
										return file;
									}), opts.container);
									break;
							}
						});
						me.connectRuntime(opts, function() {
							me.refresh();
							me.exec('init', opts);
							me.trigger('ready');
						});
						this._resizeHandler = Base.bindFn(this.refresh, this);
						$(window).on('resize', this._resizeHandler);
					},
					refresh: function() {
						var shimContainer = this.getRuntime().getContainer(),
							button = this.options.button,
							width = button.outerWidth ? button.outerWidth() : button.width(),
							height = button.outerHeight ? button.outerHeight() : button.height(),
							pos = button.offset();
						width && height && shimContainer.css({
							bottom: 'auto',
							right: 'auto',
							width: width + 'px',
							height: height + 'px'
						}).offset(pos);
					},
					enable: function() {
						var btn = this.options.button;
						btn.removeClass('webuploader-pick-disable');
						this.refresh();
					},
					disable: function() {
						var btn = this.options.button;
						this.getRuntime().getContainer().css({
							top: '-99999px'
						});
						btn.addClass('webuploader-pick-disable');
					},
					destroy: function() {
						var btn = this.options.button;
						$(window).off('resize', this._resizeHandler);
						btn.removeClass('webuploader-pick-disable webuploader-pick-hover ' +
							'webuploader-pick');
					}
				});
				return FilePicker;
			});
			define('widgets/filepicker', ['base', 'uploader', 'lib/filepicker', 'widgets/widget'], function(Base, Uploader, FilePicker) {
				var $ = Base.$;
				$.extend(Uploader.options, {
					pick: null,
					accept: null
				});
				return Uploader.register({
					name: 'picker',
					init: function(opts) {
						this.pickers = [];
						return opts.pick && this.addBtn(opts.pick);
					},
					refresh: function() {
						$.each(this.pickers, function() {
							this.refresh();
						});
					},
					addBtn: function(pick) {
						var me = this,
							opts = me.options,
							accept = opts.accept,
							promises = [];
						if(!pick) {
							return;
						}
						$.isPlainObject(pick) || (pick = {
							id: pick
						});
						$(pick.id).each(function() {
							var options, picker, deferred;
							deferred = Base.Deferred();
							options = $.extend({}, pick, {
								accept: $.isPlainObject(accept) ? [accept] : accept,
								swf: opts.swf,
								runtimeOrder: opts.runtimeOrder,
								id: this
							});
							picker = new FilePicker(options);
							picker.once('ready', deferred.resolve);
							picker.on('select', function(files) {
								me.owner.request('add-file', [files]);
							});
							picker.init();
							me.pickers.push(picker);
							promises.push(deferred.promise());
						});
						return Base.when.apply(Base, promises);
					},
					disable: function() {
						$.each(this.pickers, function() {
							this.disable();
						});
					},
					enable: function() {
						$.each(this.pickers, function() {
							this.enable();
						});
					},
					destroy: function() {
						$.each(this.pickers, function() {
							this.destroy();
						});
						this.pickers = null;
					}
				});
			});
			define('lib/image', ['base', 'runtime/client', 'lib/blob'], function(Base, RuntimeClient, Blob) {
				var $ = Base.$;

				function Image(opts) {
					this.options = $.extend({}, Image.options, opts);
					RuntimeClient.call(this, 'Image');
					this.on('load', function() {
						this._info = this.exec('info');
						this._meta = this.exec('meta');
					});
				}
				Image.options = {
					quality: 90,
					crop: false,
					preserveHeaders: false,
					allowMagnify: false
				};
				Base.inherits(RuntimeClient, {
					constructor: Image,
					info: function(val) {
						if(val) {
							this._info = val;
							return this;
						}
						return this._info;
					},
					meta: function(val) {
						if(val) {
							this._meta = val;
							return this;
						}
						return this._meta;
					},
					loadFromBlob: function(blob) {
						var me = this,
							ruid = blob.getRuid();
						this.connectRuntime(ruid, function() {
							me.exec('init', me.options);
							me.exec('loadFromBlob', blob);
						});
					},
					resize: function() {
						var args = Base.slice(arguments);
						return this.exec.apply(this, ['resize'].concat(args));
					},
					crop: function() {
						var args = Base.slice(arguments);
						return this.exec.apply(this, ['crop'].concat(args));
					},
					getAsDataUrl: function(type) {
						return this.exec('getAsDataUrl', type);
					},
					getAsBlob: function(type) {
						var blob = this.exec('getAsBlob', type);
						return new Blob(this.getRuid(), blob);
					}
				});
				return Image;
			});
			define('widgets/image', ['base', 'uploader', 'lib/image', 'widgets/widget'], function(Base, Uploader, Image) {
				var $ = Base.$,
					throttle;
				throttle = (function(max) {
					var occupied = 0,
						waiting = [],
						tick = function() {
							var item;
							while(waiting.length && occupied < max) {
								item = waiting.shift();
								occupied += item[0];
								item[1]();
							}
						};
					return function(emiter, size, cb) {
						waiting.push([size, cb]);
						emiter.once('destroy', function() {
							occupied -= size;
							setTimeout(tick, 1);
						});
						setTimeout(tick, 1);
					};
				})(5 * 1024 * 1024);
				$.extend(Uploader.options, {
					thumb: {
						width: 110,
						height: 110,
						quality: 70,
						allowMagnify: true,
						crop: true,
						preserveHeaders: false,
						type: 'image/jpeg'
					},
					compress: {
						width: 1600,
						height: 1600,
						quality: 90,
						allowMagnify: false,
						crop: false,
						preserveHeaders: true
					}
				});
				return Uploader.register({
					name: 'image',
					makeThumb: function(file, cb, width, height) {
						var opts, image;
						file = this.request('get-file', file);
						if(!file.type.match(/^image/)) {
							cb(true);
							return;
						}
						opts = $.extend({}, this.options.thumb);
						if($.isPlainObject(width)) {
							opts = $.extend(opts, width);
							width = null;
						}
						width = width || opts.width;
						height = height || opts.height;
						image = new Image(opts);
						image.once('load', function() {
							file._info = file._info || image.info();
							file._meta = file._meta || image.meta();
							if(width <= 1 && width > 0) {
								width = file._info.width * width;
							}
							if(height <= 1 && height > 0) {
								height = file._info.height * height;
							}
							image.resize(width, height);
						});
						image.once('complete', function() {
							cb(false, image.getAsDataUrl(opts.type));
							image.destroy();
						});
						image.once('error', function(reason) {
							cb(reason || true);
							image.destroy();
						});
						throttle(image, file.source.size, function() {
							file._info && image.info(file._info);
							file._meta && image.meta(file._meta);
							image.loadFromBlob(file.source);
						});
					},
					beforeSendFile: function(file) {
						var opts = this.options.compress || this.options.resize,
							compressSize = opts && opts.compressSize || 0,
							noCompressIfLarger = opts && opts.noCompressIfLarger || false,
							image, deferred;
						file = this.request('get-file', file);
						if(!opts || !~'image/jpeg,image/jpg'.indexOf(file.type) || file.size < compressSize || file._compressed) {
							return;
						}
						opts = $.extend({}, opts);
						deferred = Base.Deferred();
						image = new Image(opts);
						deferred.always(function() {
							image.destroy();
							image = null;
						});
						image.once('error', deferred.reject);
						image.once('load', function() {
							var width = opts.width,
								height = opts.height;
							file._info = file._info || image.info();
							file._meta = file._meta || image.meta();
							if(width <= 1 && width > 0) {
								width = file._info.width * width;
							}
							if(height <= 1 && height > 0) {
								height = file._info.height * height;
							}
							image.resize(width, height);
						});
						image.once('complete', function() {
							var blob, size;
							try {
								blob = image.getAsBlob(opts.type);
								size = file.size;
								if(!noCompressIfLarger || blob.size < size) {
									file.source = blob;
									file.size = blob.size;
									file.trigger('resize', blob.size, size);
								}
								file._compressed = true;
								deferred.resolve();
							} catch(e) {
								deferred.resolve();
							}
						});
						file._info && image.info(file._info);
						file._meta && image.meta(file._meta);
						image.loadFromBlob(file.source);
						return deferred.promise();
					}
				});
			});
			define('file', ['base', 'mediator'], function(Base, Mediator) {
				var $ = Base.$,
					idPrefix = 'WU_FILE_',
					idSuffix = 0,
					rExt = /\.([^.]+)$/,
					statusMap = {};

				function gid() {
					return idPrefix + idSuffix++;
				}

				function WUFile(source) {
					this.name = source.name || 'Untitled';
					this.size = source.size || 0;
					this.type = source.type || 'application/octet-stream';
					this.lastModifiedDate = source.lastModifiedDate || (new Date() * 1);
					this.id = gid();
					this.ext = rExt.exec(this.name) ? RegExp.$1 : '';
					this.statusText = '';
					statusMap[this.id] = WUFile.Status.INITED;
					this.source = source;
					this.loaded = 0;
					this.on('error', function(msg) {
						this.setStatus(WUFile.Status.ERROR, msg);
					});
				}
				$.extend(WUFile.prototype, {
					setStatus: function(status, text) {
						var prevStatus = statusMap[this.id];
						typeof text !== 'undefined' && (this.statusText = text);
						if(status !== prevStatus) {
							statusMap[this.id] = status;
							this.trigger('statuschange', status, prevStatus);
						}
					},
					getStatus: function() {
						return statusMap[this.id];
					},
					getSource: function() {
						return this.source;
					},
					destroy: function() {
						this.off();
						delete statusMap[this.id];
					}
				});
				Mediator.installTo(WUFile.prototype);
				WUFile.Status = {
					INITED: 'inited',
					QUEUED: 'queued',
					PROGRESS: 'progress',
					ERROR: 'error',
					COMPLETE: 'complete',
					CANCELLED: 'cancelled',
					INTERRUPT: 'interrupt',
					INVALID: 'invalid'
				};
				return WUFile;
			});
			define('queue', ['base', 'mediator', 'file'], function(Base, Mediator, WUFile) {
				var $ = Base.$,
					STATUS = WUFile.Status;

				function Queue() {
					this.stats = {
						numOfQueue: 0,
						numOfSuccess: 0,
						numOfCancel: 0,
						numOfProgress: 0,
						numOfUploadFailed: 0,
						numOfInvalid: 0,
						numofDeleted: 0,
						numofInterrupt: 0
					};
					this._queue = [];
					this._map = {};
				}
				$.extend(Queue.prototype, {
					append: function(file) {
						this._queue.push(file);
						this._fileAdded(file);
						return this;
					},
					prepend: function(file) {
						this._queue.unshift(file);
						this._fileAdded(file);
						return this;
					},
					getFile: function(fileId) {
						if(typeof fileId !== 'string') {
							return fileId;
						}
						return this._map[fileId];
					},
					fetch: function(status) {
						var len = this._queue.length,
							i, file;
						status = status || STATUS.QUEUED;
						for(i = 0; i < len; i++) {
							file = this._queue[i];
							if(status === file.getStatus()) {
								return file;
							}
						}
						return null;
					},
					sort: function(fn) {
						if(typeof fn === 'function') {
							this._queue.sort(fn);
						}
					},
					getFiles: function() {
						var sts = [].slice.call(arguments, 0),
							ret = [],
							i = 0,
							len = this._queue.length,
							file;
						for(; i < len; i++) {
							file = this._queue[i];
							if(sts.length && !~$.inArray(file.getStatus(), sts)) {
								continue;
							}
							ret.push(file);
						}
						return ret;
					},
					removeFile: function(file) {
						var me = this,
							existing = this._map[file.id];
						if(existing) {
							delete this._map[file.id];
							file.destroy();
							this.stats.numofDeleted++;
						}
					},
					_fileAdded: function(file) {
						var me = this,
							existing = this._map[file.id];
						if(!existing) {
							this._map[file.id] = file;
							file.on('statuschange', function(cur, pre) {
								me._onFileStatusChange(cur, pre);
							});
						}
					},
					_onFileStatusChange: function(curStatus, preStatus) {
						var stats = this.stats;
						switch(preStatus) {
							case STATUS.PROGRESS:
								stats.numOfProgress--;
								break;
							case STATUS.QUEUED:
								stats.numOfQueue--;
								break;
							case STATUS.ERROR:
								stats.numOfUploadFailed--;
								break;
							case STATUS.INVALID:
								stats.numOfInvalid--;
								break;
							case STATUS.INTERRUPT:
								stats.numofInterrupt--;
								break;
						}
						switch(curStatus) {
							case STATUS.QUEUED:
								stats.numOfQueue++;
								break;
							case STATUS.PROGRESS:
								stats.numOfProgress++;
								break;
							case STATUS.ERROR:
								stats.numOfUploadFailed++;
								break;
							case STATUS.COMPLETE:
								stats.numOfSuccess++;
								break;
							case STATUS.CANCELLED:
								stats.numOfCancel++;
								break;
							case STATUS.INVALID:
								stats.numOfInvalid++;
								break;
							case STATUS.INTERRUPT:
								stats.numofInterrupt++;
								break;
						}
					}
				});
				Mediator.installTo(Queue.prototype);
				return Queue;
			});
			define('widgets/queue', ['base', 'uploader', 'queue', 'file', 'lib/file', 'runtime/client', 'widgets/widget'], function(Base, Uploader, Queue, WUFile, File, RuntimeClient) {
				var $ = Base.$,
					rExt = /\.\w+$/,
					Status = WUFile.Status;
				return Uploader.register({
					name: 'queue',
					init: function(opts) {
						var me = this,
							deferred, len, i, item, arr, accept, runtime;
						if($.isPlainObject(opts.accept)) {
							opts.accept = [opts.accept];
						}
						if(opts.accept) {
							arr = [];
							for(i = 0, len = opts.accept.length; i < len; i++) {
								item = opts.accept[i].extensions;
								item && arr.push(item);
							}
							if(arr.length) {
								accept = '\\.' + arr.join(',').replace(/,/g, '$|\\.').replace(/\*/g, '.*') + '$';
							}
							me.accept = new RegExp(accept, 'i');
						}
						me.queue = new Queue();
						me.stats = me.queue.stats;
						if(this.request('predict-runtime-type') !== 'html5') {
							return;
						}
						deferred = Base.Deferred();
						this.placeholder = runtime = new RuntimeClient('Placeholder');
						runtime.connectRuntime({
							runtimeOrder: 'html5'
						}, function() {
							me._ruid = runtime.getRuid();
							deferred.resolve();
						});
						return deferred.promise();
					},
					_wrapFile: function(file) {
						if(!(file instanceof WUFile)) {
							if(!(file instanceof File)) {
								if(!this._ruid) {
									throw new Error('Can\'t add external files.');
								}
								file = new File(this._ruid, file);
							}
							file = new WUFile(file);
						}
						return file;
					},
					acceptFile: function(file) {
						var invalid = !file || !file.size || this.accept && rExt.exec(file.name) && !this.accept.test(file.name);
						return !invalid;
					},
					_addFile: function(file) {
						var me = this;
						file = me._wrapFile(file);
						if(!me.owner.trigger('beforeFileQueued', file)) {
							return;
						}
						if(!me.acceptFile(file)) {
							me.owner.trigger('error', 'Q_TYPE_DENIED', file);
							return;
						}
						me.queue.append(file);
						me.owner.trigger('fileQueued', file);
						return file;
					},
					getFile: function(fileId) {
						return this.queue.getFile(fileId);
					},
					addFile: function(files) {
						var me = this;
						if(!files.length) {
							files = [files];
						}
						files = $.map(files, function(file) {
							return me._addFile(file);
						});
						me.owner.trigger('filesQueued', files);
						if(me.options.auto) {
							setTimeout(function() {
								me.request('start-upload');
							}, 20);
						}
					},
					getStats: function() {
						return this.stats;
					},
					removeFile: function(file, remove) {
						var me = this;
						file = file.id ? file : me.queue.getFile(file);
						this.request('cancel-file', file);
						if(remove) {
							this.queue.removeFile(file);
						}
					},
					getFiles: function() {
						return this.queue.getFiles.apply(this.queue, arguments);
					},
					fetchFile: function() {
						return this.queue.fetch.apply(this.queue, arguments);
					},
					retry: function(file, noForceStart) {
						var me = this,
							files, i, len;
						if(file) {
							file = file.id ? file : me.queue.getFile(file);
							file.setStatus(Status.QUEUED);
							noForceStart || me.request('start-upload');
							return;
						}
						files = me.queue.getFiles(Status.ERROR);
						i = 0;
						len = files.length;
						for(; i < len; i++) {
							file = files[i];
							file.setStatus(Status.QUEUED);
						}
						me.request('start-upload');
					},
					sortFiles: function() {
						return this.queue.sort.apply(this.queue, arguments);
					},
					reset: function() {
						this.owner.trigger('reset');
						this.queue = new Queue();
						this.stats = this.queue.stats;
					},
					destroy: function() {
						this.reset();
						this.placeholder && this.placeholder.destroy();
					}
				});
			});
			define('widgets/runtime', ['uploader', 'runtime/runtime', 'widgets/widget'], function(Uploader, Runtime) {
				Uploader.support = function() {
					return Runtime.hasRuntime.apply(Runtime, arguments);
				};
				return Uploader.register({
					name: 'runtime',
					init: function() {
						if(!this.predictRuntimeType()) {
							throw Error('Runtime Error');
						}
					},
					predictRuntimeType: function() {
						var orders = this.options.runtimeOrder || Runtime.orders,
							type = this.type,
							i, len;
						if(!type) {
							orders = orders.split(/\s*,\s*/g);
							for(i = 0, len = orders.length; i < len; i++) {
								if(Runtime.hasRuntime(orders[i])) {
									this.type = type = orders[i];
									break;
								}
							}
						}
						return type;
					}
				});
			});
			define('lib/transport', ['base', 'runtime/client', 'mediator'], function(Base, RuntimeClient, Mediator) {
				var $ = Base.$;

				function Transport(opts) {
					var me = this;
					opts = me.options = $.extend(true, {}, Transport.options, opts || {});
					RuntimeClient.call(this, 'Transport');
					this._blob = null;
					this._formData = opts.formData || {};
					this._headers = opts.headers || {};
					this.on('progress', this._timeout);
					this.on('load error', function() {
						me.trigger('progress', 1);
						clearTimeout(me._timer);
					});
				}
				Transport.options = {
					server: '',
					method: 'POST',
					withCredentials: false,
					fileVal: 'file',
					timeout: 2 * 60 * 1000,
					formData: {},
					headers: {},
					sendAsBinary: false
				};
				$.extend(Transport.prototype, {
					appendBlob: function(key, blob, filename) {
						var me = this,
							opts = me.options;
						if(me.getRuid()) {
							me.disconnectRuntime();
						}
						me.connectRuntime(blob.ruid, function() {
							me.exec('init');
						});
						me._blob = blob;
						opts.fileVal = key || opts.fileVal;
						opts.filename = filename || opts.filename;
					},
					append: function(key, value) {
						if(typeof key === 'object') {
							$.extend(this._formData, key);
						} else {
							this._formData[key] = value;
						}
					},
					setRequestHeader: function(key, value) {
						if(typeof key === 'object') {
							$.extend(this._headers, key);
						} else {
							this._headers[key] = value;
						}
					},
					send: function(method) {
						this.exec('send', method);
						this._timeout();
					},
					abort: function() {
						clearTimeout(this._timer);
						return this.exec('abort');
					},
					destroy: function() {
						this.trigger('destroy');
						this.off();
						this.exec('destroy');
						this.disconnectRuntime();
					},
					getResponse: function() {
						return this.exec('getResponse');
					},
					getResponseAsJson: function() {
						return this.exec('getResponseAsJson');
					},
					getStatus: function() {
						return this.exec('getStatus');
					},
					_timeout: function() {
						var me = this,
							duration = me.options.timeout;
						if(!duration) {
							return;
						}
						clearTimeout(me._timer);
						me._timer = setTimeout(function() {
							me.abort();
							me.trigger('error', 'timeout');
						}, duration);
					}
				});
				Mediator.installTo(Transport.prototype);
				return Transport;
			});
			define('widgets/upload', ['base', 'uploader', 'file', 'lib/transport', 'widgets/widget'], function(Base, Uploader, WUFile, Transport) {
				var $ = Base.$,
					isPromise = Base.isPromise,
					Status = WUFile.Status;
				$.extend(Uploader.options, {
					prepareNextFile: false,
					chunked: false,
					chunkSize: 5 * 1024 * 1024,
					chunkRetry: 2,
					threads: 3,
					formData: {}
				});

				function CuteFile(file, chunkSize) {
					var pending = [],
						blob = file.source,
						total = blob.size,
						chunks = chunkSize ? Math.ceil(total / chunkSize) : 1,
						start = 0,
						index = 0,
						len, api;
					api = {
						file: file,
						has: function() {
							return !!pending.length;
						},
						shift: function() {
							return pending.shift();
						},
						unshift: function(block) {
							pending.unshift(block);
						}
					};
					while(index < chunks) {
						len = Math.min(chunkSize, total - start);
						pending.push({
							file: file,
							start: start,
							end: chunkSize ? (start + len) : total,
							total: total,
							chunks: chunks,
							chunk: index++,
							cuted: api
						});
						start += len;
					}
					file.blocks = pending.concat();
					file.remaning = pending.length;
					return api;
				}
				Uploader.register({
					name: 'upload',
					init: function() {
						var owner = this.owner,
							me = this;
						this.runing = false;
						this.progress = false;
						owner.on('startUpload', function() {
							me.progress = true;
						}).on('uploadFinished', function() {
							me.progress = false;
						});
						this.pool = [];
						this.stack = [];
						this.pending = [];
						this.remaning = 0;
						this.__tick = Base.bindFn(this._tick, this);
						owner.on('uploadComplete', function(file) {
							file.blocks && $.each(file.blocks, function(_, v) {
								v.transport && (v.transport.abort(), v.transport.destroy());
								delete v.transport;
							});
							delete file.blocks;
							delete file.remaning;
						});
					},
					reset: function() {
						this.request('stop-upload', true);
						this.runing = false;
						this.pool = [];
						this.stack = [];
						this.pending = [];
						this.remaning = 0;
						this._trigged = false;
						this._promise = null;
					},
					startUpload: function(file) {
						var me = this;
						$.each(me.request('get-files', Status.INVALID), function() {
							me.request('remove-file', this);
						});
						if(file) {
							file = file.id ? file : me.request('get-file', file);
							if(file.getStatus() === Status.INTERRUPT) {
								$.each(me.pool, function(_, v) {
									if(v.file !== file) {
										return;
									}
									v.transport && v.transport.send();
								});
								file.setStatus(Status.QUEUED);
							} else if(file.getStatus() === Status.PROGRESS) {
								return;
							} else {
								file.setStatus(Status.QUEUED);
							}
						} else {
							$.each(me.request('get-files', [Status.INITED]), function() {
								this.setStatus(Status.QUEUED);
							});
						}
						if(me.runing) {
							return;
						}
						me.runing = true;
						var files = [];
						$.each(me.pool, function(_, v) {
							var file = v.file;
							if(file.getStatus() === Status.INTERRUPT) {
								files.push(file);
								me._trigged = false;
								v.transport && v.transport.send();
							}
						});
						var file;
						while((file = files.shift())) {
							file.setStatus(Status.PROGRESS);
						}
						file || $.each(me.request('get-files', Status.INTERRUPT), function() {
							this.setStatus(Status.PROGRESS);
						});
						me._trigged = false;
						Base.nextTick(me.__tick);
						me.owner.trigger('startUpload');
					},
					stopUpload: function(file, interrupt) {
						var me = this;
						if(file === true) {
							interrupt = file;
							file = null;
						}
						if(me.runing === false) {
							return;
						}
						if(file) {
							file = file.id ? file : me.request('get-file', file);
							if(file.getStatus() !== Status.PROGRESS && file.getStatus() !== Status.QUEUED) {
								return;
							}
							file.setStatus(Status.INTERRUPT);
							$.each(me.pool, function(_, v) {
								if(v.file !== file) {
									return;
								}
								v.transport && v.transport.abort();
								me._putback(v);
								me._popBlock(v);
							});
							return Base.nextTick(me.__tick);
						}
						me.runing = false;
						if(this._promise && this._promise.file) {
							this._promise.file.setStatus(Status.INTERRUPT);
						}
						interrupt && $.each(me.pool, function(_, v) {
							v.transport && v.transport.abort();
							v.file.setStatus(Status.INTERRUPT);
						});
						me.owner.trigger('stopUpload');
					},
					cancelFile: function(file) {
						file = file.id ? file : this.request('get-file', file);
						file.blocks && $.each(file.blocks, function(_, v) {
							var _tr = v.transport;
							if(_tr) {
								_tr.abort();
								_tr.destroy();
								delete v.transport;
							}
						});
						file.setStatus(Status.CANCELLED);
						this.owner.trigger('fileDequeued', file);
					},
					isInProgress: function() {
						return !!this.progress;
					},
					_getStats: function() {
						return this.request('get-stats');
					},
					skipFile: function(file, status) {
						file = file.id ? file : this.request('get-file', file);
						file.setStatus(status || Status.COMPLETE);
						file.skipped = true;
						file.blocks && $.each(file.blocks, function(_, v) {
							var _tr = v.transport;
							if(_tr) {
								_tr.abort();
								_tr.destroy();
								delete v.transport;
							}
						});
						this.owner.trigger('uploadSkip', file);
					},
					_tick: function() {
						var me = this,
							opts = me.options,
							fn, val;
						if(me._promise) {
							return me._promise.always(me.__tick);
						}
						if(me.pool.length < opts.threads && (val = me._nextBlock())) {
							me._trigged = false;
							fn = function(val) {
								me._promise = null;
								val && val.file && me._startSend(val);
								Base.nextTick(me.__tick);
							};
							me._promise = isPromise(val) ? val.always(fn) : fn(val);
						} else if(!me.remaning && !me._getStats().numOfQueue && !me._getStats().numofInterrupt) {
							me.runing = false;
							me._trigged || Base.nextTick(function() {
								me.owner.trigger('uploadFinished');
							});
							me._trigged = true;
						}
					},
					_putback: function(block) {
						var idx;
						block.cuted.unshift(block);
						idx = this.stack.indexOf(block.cuted);
						if(!~idx) {
							this.stack.unshift(block.cuted);
						}
					},
					_getStack: function() {
						var i = 0,
							act;
						while((act = this.stack[i++])) {
							if(act.has() && act.file.getStatus() === Status.PROGRESS) {
								return act;
							} else if(!act.has() || act.file.getStatus() !== Status.PROGRESS && act.file.getStatus() !== Status.INTERRUPT) {
								this.stack.splice(--i, 1);
							}
						}
						return null;
					},
					_nextBlock: function() {
						var me = this,
							opts = me.options,
							act, next, done, preparing;
						if((act = this._getStack())) {
							if(opts.prepareNextFile && !me.pending.length) {
								me._prepareNextFile();
							}
							return act.shift();
						} else if(me.runing) {
							if(!me.pending.length && me._getStats().numOfQueue) {
								me._prepareNextFile();
							}
							next = me.pending.shift();
							done = function(file) {
								if(!file) {
									return null;
								}
								act = CuteFile(file, opts.chunked ? opts.chunkSize : 0);
								me.stack.push(act);
								return act.shift();
							};
							if(isPromise(next)) {
								preparing = next.file;
								next = next[next.pipe ? 'pipe' : 'then'](done);
								next.file = preparing;
								return next;
							}
							return done(next);
						}
					},
					_prepareNextFile: function() {
						var me = this,
							file = me.request('fetch-file'),
							pending = me.pending,
							promise;
						if(file) {
							promise = me.request('before-send-file', file, function() {
								if(file.getStatus() === Status.PROGRESS || file.getStatus() === Status.INTERRUPT) {
									return file;
								}
								return me._finishFile(file);
							});
							me.owner.trigger('uploadStart', file);
							file.setStatus(Status.PROGRESS);
							promise.file = file;
							promise.done(function() {
								var idx = $.inArray(promise, pending);
								~idx && pending.splice(idx, 1, file);
							});
							promise.fail(function(reason) {
								file.setStatus(Status.ERROR, reason);
								me.owner.trigger('uploadError', file, reason);
								me.owner.trigger('uploadComplete', file);
							});
							pending.push(promise);
						}
					},
					_popBlock: function(block) {
						var idx = $.inArray(block, this.pool);
						this.pool.splice(idx, 1);
						block.file.remaning--;
						this.remaning--;
					},
					_startSend: function(block) {
						var me = this,
							file = block.file,
							promise;
						if(file.getStatus() !== Status.PROGRESS) {
							if(file.getStatus() === Status.INTERRUPT) {
								me._putback(block);
							}
							return;
						}
						me.pool.push(block);
						me.remaning++;
						block.blob = block.chunks === 1 ? file.source : file.source.slice(block.start, block.end);
						promise = me.request('before-send', block, function() {
							if(file.getStatus() === Status.PROGRESS) {
								me._doSend(block);
							} else {
								me._popBlock(block);
								Base.nextTick(me.__tick);
							}
						});
						promise.fail(function() {
							if(file.remaning === 1) {
								me._finishFile(file).always(function() {
									block.percentage = 1;
									me._popBlock(block);
									me.owner.trigger('uploadComplete', file);
									Base.nextTick(me.__tick);
								});
							} else {
								block.percentage = 1;
								me.updateFileProgress(file);
								me._popBlock(block);
								Base.nextTick(me.__tick);
							}
						});
					},
					_doSend: function(block) {
						var me = this,
							owner = me.owner,
							opts = me.options,
							file = block.file,
							tr = new Transport(opts),
							data = $.extend({}, opts.formData),
							headers = $.extend({}, opts.headers),
							requestAccept, ret;
						block.transport = tr;
						tr.on('destroy', function() {
							delete block.transport;
							me._popBlock(block);
							Base.nextTick(me.__tick);
						});
						tr.on('progress', function(percentage) {
							block.percentage = percentage;
							me.updateFileProgress(file);
						});
						requestAccept = function(reject) {
							var fn;
							ret = tr.getResponseAsJson() || {};
							ret._raw = tr.getResponse();
							fn = function(value) {
								reject = value;
							};
							if(!owner.trigger('uploadAccept', block, ret, fn)) {
								reject = reject || 'server';
							}
							return reject;
						};
						tr.on('error', function(type, flag) {
							block.retried = block.retried || 0;
							if(block.chunks > 1 && ~'http,abort'.indexOf(type) && block.retried < opts.chunkRetry) {
								block.retried++;
								tr.send();
							} else {
								if(!flag && type === 'server') {
									type = requestAccept(type);
								}
								file.setStatus(Status.ERROR, type);
								owner.trigger('uploadError', file, type);
								owner.trigger('uploadComplete', file);
							}
						});
						tr.on('load', function() {
							var reason;
							if((reason = requestAccept())) {
								tr.trigger('error', reason, true);
								return;
							}
							if(file.remaning === 1) {
								me._finishFile(file, ret);
							} else {
								tr.destroy();
							}
						});
						data = $.extend(data, {
							id: file.id,
							name: file.name,
							type: file.type,
							lastModifiedDate: file.lastModifiedDate,
							size: file.size
						});
						block.chunks > 1 && $.extend(data, {
							chunks: block.chunks,
							chunk: block.chunk
						});
						owner.trigger('uploadBeforeSend', block, data, headers);
						tr.appendBlob(opts.fileVal, block.blob, file.name);
						tr.append(data);
						tr.setRequestHeader(headers);
						tr.send();
					},
					_finishFile: function(file, ret, hds) {
						var owner = this.owner;
						return owner.request('after-send-file', arguments, function() {
							file.setStatus(Status.COMPLETE);
							owner.trigger('uploadSuccess', file, ret, hds);
						}).fail(function(reason) {
							if(file.getStatus() === Status.PROGRESS) {
								file.setStatus(Status.ERROR, reason);
							}
							owner.trigger('uploadError', file, reason);
						}).always(function() {
							owner.trigger('uploadComplete', file);
						});
					},
					updateFileProgress: function(file) {
						var totalPercent = 0,
							uploaded = 0;
						if(!file.blocks) {
							return;
						}
						$.each(file.blocks, function(_, v) {
							uploaded += (v.percentage || 0) * (v.end - v.start);
						});
						totalPercent = uploaded / file.size;
						this.owner.trigger('uploadProgress', file, totalPercent || 0);
					}
				});
			});
			define('widgets/validator', ['base', 'uploader', 'file', 'widgets/widget'], function(Base, Uploader, WUFile) {
				var $ = Base.$,
					validators = {},
					api;
				api = {
					addValidator: function(type, cb) {
						validators[type] = cb;
					},
					removeValidator: function(type) {
						delete validators[type];
					}
				};
				Uploader.register({
					name: 'validator',
					init: function() {
						var me = this;
						Base.nextTick(function() {
							$.each(validators, function() {
								this.call(me.owner);
							});
						});
					}
				});
				api.addValidator('fileNumLimit', function() {
					var uploader = this,
						opts = uploader.options,
						count = 0,
						max = parseInt(opts.fileNumLimit, 10),
						flag = true;
					if(!max) {
						return;
					}
					uploader.on('beforeFileQueued', function(file) {
						if(count >= max && flag) {
							flag = false;
							this.trigger('error', 'Q_EXCEED_NUM_LIMIT', max, file);
							setTimeout(function() {
								flag = true;
							}, 1);
						}
						return count >= max ? false : true;
					});
					uploader.on('fileQueued', function() {
						count++;
					});
					uploader.on('fileDequeued', function() {
						count--;
					});
					uploader.on('reset', function() {
						count = 0;
					});
				});
				api.addValidator('fileSizeLimit', function() {
					var uploader = this,
						opts = uploader.options,
						count = 0,
						max = parseInt(opts.fileSizeLimit, 10),
						flag = true;
					if(!max) {
						return;
					}
					uploader.on('beforeFileQueued', function(file) {
						var invalid = count + file.size > max;
						if(invalid && flag) {
							flag = false;
							this.trigger('error', 'Q_EXCEED_SIZE_LIMIT', max, file);
							setTimeout(function() {
								flag = true;
							}, 1);
						}
						return invalid ? false : true;
					});
					uploader.on('fileQueued', function(file) {
						count += file.size;
					});
					uploader.on('fileDequeued', function(file) {
						count -= file.size;
					});
					uploader.on('reset', function() {
						count = 0;
					});
				});
				api.addValidator('fileSingleSizeLimit', function() {
					var uploader = this,
						opts = uploader.options,
						max = opts.fileSingleSizeLimit;
					if(!max) {
						return;
					}
					uploader.on('beforeFileQueued', function(file) {
						if(file.size > max) {
							file.setStatus(WUFile.Status.INVALID, 'exceed_size');
							this.trigger('error', 'F_EXCEED_SIZE', max, file);
							return false;
						}
					});
				});
				api.addValidator('duplicate', function() {
					var uploader = this,
						opts = uploader.options,
						mapping = {};
					if(opts.duplicate) {
						return;
					}

					function hashString(str) {
						var hash = 0,
							i = 0,
							len = str.length,
							_char;
						for(; i < len; i++) {
							_char = str.charCodeAt(i);
							hash = _char + (hash << 6) + (hash << 16) - hash;
						}
						return hash;
					}
					uploader.on('beforeFileQueued', function(file) {
						var hash = file.__hash || (file.__hash = hashString(file.name +
							file.size + file.lastModifiedDate));
						if(mapping[hash]) {
							this.trigger('error', 'F_DUPLICATE', file);
							return false;
						}
					});
					uploader.on('fileQueued', function(file) {
						var hash = file.__hash;
						hash && (mapping[hash] = true);
					});
					uploader.on('fileDequeued', function(file) {
						var hash = file.__hash;
						hash && (delete mapping[hash]);
					});
					uploader.on('reset', function() {
						mapping = {};
					});
				});
				return api;
			});
			define('lib/md5', ['runtime/client', 'mediator'], function(RuntimeClient, Mediator) {
				function Md5() {
					RuntimeClient.call(this, 'Md5');
				}
				Mediator.installTo(Md5.prototype);
				Md5.prototype.loadFromBlob = function(blob) {
					var me = this;
					if(me.getRuid()) {
						me.disconnectRuntime();
					}
					me.connectRuntime(blob.ruid, function() {
						me.exec('init');
						me.exec('loadFromBlob', blob);
					});
				};
				Md5.prototype.getResult = function() {
					return this.exec('getResult');
				};
				return Md5;
			});
			define('widgets/md5', ['base', 'uploader', 'lib/md5', 'lib/blob', 'widgets/widget'], function(Base, Uploader, Md5, Blob) {
				return Uploader.register({
					name: 'md5',
					md5File: function(file, start, end) {
						var md5 = new Md5(),
							deferred = Base.Deferred(),
							blob = (file instanceof Blob) ? file : this.request('get-file', file).source;
						md5.on('progress load', function(e) {
							e = e || {};
							deferred.notify(e.total ? e.loaded / e.total : 1);
						});
						md5.on('complete', function() {
							deferred.resolve(md5.getResult());
						});
						md5.on('error', function(reason) {
							deferred.reject(reason);
						});
						if(arguments.length > 1) {
							start = start || 0;
							end = end || 0;
							start < 0 && (start = blob.size + start);
							end < 0 && (end = blob.size + end);
							end = Math.min(end, blob.size);
							blob = blob.slice(start, end);
						}
						md5.loadFromBlob(blob);
						return deferred.promise();
					}
				});
			});
			define('runtime/compbase', [], function() {
				function CompBase(owner, runtime) {
					this.owner = owner;
					this.options = owner.options;
					this.getRuntime = function() {
						return runtime;
					};
					this.getRuid = function() {
						return runtime.uid;
					};
					this.trigger = function() {
						return owner.trigger.apply(owner, arguments);
					};
				}
				return CompBase;
			});
			define('runtime/html5/runtime', ['base', 'runtime/runtime', 'runtime/compbase'], function(Base, Runtime, CompBase) {
				var type = 'html5',
					components = {};

				function Html5Runtime() {
					var pool = {},
						me = this,
						destroy = this.destroy;
					Runtime.apply(me, arguments);
					me.type = type;
					me.exec = function(comp, fn) {
						var client = this,
							uid = client.uid,
							args = Base.slice(arguments, 2),
							instance;
						if(components[comp]) {
							instance = pool[uid] = pool[uid] || new components[comp](client, me);
							if(instance[fn]) {
								return instance[fn].apply(instance, args);
							}
						}
					};
					me.destroy = function() {
						return destroy && destroy.apply(this, arguments);
					};
				}
				Base.inherits(Runtime, {
					constructor: Html5Runtime,
					init: function() {
						var me = this;
						setTimeout(function() {
							me.trigger('ready');
						}, 1);
					}
				});
				Html5Runtime.register = function(name, component) {
					var klass = components[name] = Base.inherits(CompBase, component);
					return klass;
				};
				if(window.Blob && window.FileReader && window.DataView) {
					Runtime.addRuntime(type, Html5Runtime);
				}
				return Html5Runtime;
			});
			define('runtime/html5/blob', ['runtime/html5/runtime', 'lib/blob'], function(Html5Runtime, Blob) {
				return Html5Runtime.register('Blob', {
					slice: function(start, end) {
						var blob = this.owner.source,
							slice = blob.slice || blob.webkitSlice || blob.mozSlice;
						blob = slice.call(blob, start, end);
						return new Blob(this.getRuid(), blob);
					}
				});
			});
			define('runtime/html5/dnd', ['base', 'runtime/html5/runtime', 'lib/file'], function(Base, Html5Runtime, File) {
				var $ = Base.$,
					prefix = 'webuploader-dnd-';
				return Html5Runtime.register('DragAndDrop', {
					init: function() {
						var elem = this.elem = this.options.container;
						this.dragEnterHandler = Base.bindFn(this._dragEnterHandler, this);
						this.dragOverHandler = Base.bindFn(this._dragOverHandler, this);
						this.dragLeaveHandler = Base.bindFn(this._dragLeaveHandler, this);
						this.dropHandler = Base.bindFn(this._dropHandler, this);
						this.dndOver = false;
						elem.on('dragenter', this.dragEnterHandler);
						elem.on('dragover', this.dragOverHandler);
						elem.on('dragleave', this.dragLeaveHandler);
						elem.on('drop', this.dropHandler);
						if(this.options.disableGlobalDnd) {
							$(document).on('dragover', this.dragOverHandler);
							$(document).on('drop', this.dropHandler);
						}
					},
					_dragEnterHandler: function(e) {
						var me = this,
							denied = me._denied || false,
							items;
						e = e.originalEvent || e;
						if(!me.dndOver) {
							me.dndOver = true;
							items = e.dataTransfer.items;
							if(items && items.length) {
								me._denied = denied = !me.trigger('accept', items);
							}
							me.elem.addClass(prefix + 'over');
							me.elem[denied ? 'addClass' : 'removeClass'](prefix + 'denied');
						}
						e.dataTransfer.dropEffect = denied ? 'none' : 'copy';
						return false;
					},
					_dragOverHandler: function(e) {
						var parentElem = this.elem.parent().get(0);
						if(parentElem && !$.contains(parentElem, e.currentTarget)) {
							return false;
						}
						clearTimeout(this._leaveTimer);
						this._dragEnterHandler.call(this, e);
						return false;
					},
					_dragLeaveHandler: function() {
						var me = this,
							handler;
						handler = function() {
							me.dndOver = false;
							me.elem.removeClass(prefix + 'over ' + prefix + 'denied');
						};
						clearTimeout(me._leaveTimer);
						me._leaveTimer = setTimeout(handler, 100);
						return false;
					},
					_dropHandler: function(e) {
						var me = this,
							ruid = me.getRuid(),
							parentElem = me.elem.parent().get(0),
							dataTransfer, data;
						if(parentElem && !$.contains(parentElem, e.currentTarget)) {
							return false;
						}
						e = e.originalEvent || e;
						dataTransfer = e.dataTransfer;
						if(isNull(dataTransfer)){
							return;
						}
						try {
							data = dataTransfer.getData('text/html');
						} catch(err) {}
						if(data) {
							return;
						}
						me._getTansferFiles(dataTransfer, function(results) {
							me.trigger('drop', $.map(results, function(file) {
								return new File(ruid, file);
							}));
						});
						me.dndOver = false;
						me.elem.removeClass(prefix + 'over');
						return false;
					},
					_getTansferFiles: function(dataTransfer, callback) {
						var results = [],
							promises = [],
							items, files, file, item, i, len, canAccessFolder;
						items = dataTransfer.items;
						files = dataTransfer.files;
						canAccessFolder = !!(items && items[0].webkitGetAsEntry);
						for(i = 0, len = files.length; i < len; i++) {
							file = files[i];
							item = items && items[i];
							if(canAccessFolder && item.webkitGetAsEntry().isDirectory) {
								promises.push(this._traverseDirectoryTree(item.webkitGetAsEntry(), results));
							} else {
								results.push(file);
							}
						}
						Base.when.apply(Base, promises).done(function() {
							if(!results.length) {
								return;
							}
							callback(results);
						});
					},
					_traverseDirectoryTree: function(entry, results) {
						var deferred = Base.Deferred(),
							me = this;
						if(entry.isFile) {
							entry.file(function(file) {
								results.push(file);
								deferred.resolve();
							});
						} else if(entry.isDirectory) {
							entry.createReader().readEntries(function(entries) {
								var len = entries.length,
									promises = [],
									arr = [],
									i;
								for(i = 0; i < len; i++) {
									promises.push(me._traverseDirectoryTree(entries[i], arr));
								}
								Base.when.apply(Base, promises).then(function() {
									results.push.apply(results, arr);
									deferred.resolve();
								}, deferred.reject);
							});
						}
						return deferred.promise();
					},
					destroy: function() {
						var elem = this.elem;
						if(!elem) {
							return;
						}
						elem.off('dragenter', this.dragEnterHandler);
						elem.off('dragover', this.dragOverHandler);
						elem.off('dragleave', this.dragLeaveHandler);
						elem.off('drop', this.dropHandler);
						if(this.options.disableGlobalDnd) {
							$(document).off('dragover', this.dragOverHandler);
							$(document).off('drop', this.dropHandler);
						}
					}
				});
			});
			define('runtime/html5/filepaste', ['base', 'runtime/html5/runtime', 'lib/file'], function(Base, Html5Runtime, File) {
				return Html5Runtime.register('FilePaste', {
					init: function() {
						var opts = this.options,
							elem = this.elem = opts.container,
							accept = '.*',
							arr, i, len, item;
						if(opts.accept) {
							arr = [];
							for(i = 0, len = opts.accept.length; i < len; i++) {
								item = opts.accept[i].mimeTypes;
								item && arr.push(item);
							}
							if(arr.length) {
								accept = arr.join(',');
								accept = accept.replace(/,/g, '|').replace(/\*/g, '.*');
							}
						}
						this.accept = accept = new RegExp(accept, 'i');
						this.hander = Base.bindFn(this._pasteHander, this);
						elem.on('paste', this.hander);
					},
					_pasteHander: function(e) {
						var allowed = [],
							ruid = this.getRuid(),
							items, item, blob, i, len;
						e = e.originalEvent || e;
						items = e.clipboardData.items;
						for(i = 0, len = items.length; i < len; i++) {
							item = items[i];
							if(item.kind !== 'file' || !(blob = item.getAsFile())) {
								continue;
							}
							allowed.push(new File(ruid, blob));
						}
						if(allowed.length) {
							e.preventDefault();
							e.stopPropagation();
							this.trigger('paste', allowed);
						}
					},
					destroy: function() {
						this.elem.off('paste', this.hander);
					}
				});
			});
			define('runtime/html5/filepicker', ['base', 'runtime/html5/runtime'], function(Base, Html5Runtime) {
				var $ = Base.$;
				return Html5Runtime.register('FilePicker', {
					init: function() {
						var container = this.getRuntime().getContainer(),
							me = this,
							owner = me.owner,
							opts = me.options,
							label = this.label = $(document.createElement('label')),
							input = this.input = $(document.createElement('input')),
							arr, i, len, mouseHandler;
						input.attr('type', 'file');
						input.attr('name', opts.name);
						input.addClass('webuploader-element-invisible');
						label.on('click', function() {
							input.trigger('click');
						});
						if (opts.webkitdirectory) {
						    input.attr('webkitdirectory', true);
						}
						label.css({
							opacity: 0,
							width: '100%',
							height: '100%',
							display: 'block',
							cursor: 'pointer',
							background: '#ffffff'
						});
						if(opts.multiple) {
							input.attr('multiple', 'multiple');
						}
						if(opts.accept && opts.accept.length > 0) {
							arr = [];
							for(i = 0, len = opts.accept.length; i < len; i++) {
								arr.push(opts.accept[i].mimeTypes);
							}
							input.attr('accept', arr.join(','));
						}
						container.append(input);
						container.append(label);
						mouseHandler = function(e) {
							owner.trigger(e.type);
						};
						input.on('change', function(e) {
							var fn = arguments.callee,
								clone;
							me.files = e.target.files;
							clone = this.cloneNode(true);
							clone.value = null;
							this.parentNode.replaceChild(clone, this);
							input.off();
							input = $(clone).on('change', fn).on('mouseenter mouseleave', mouseHandler);
							owner.trigger('change');
						});
						label.on('mouseenter mouseleave', mouseHandler);
					},
					getFiles: function() {
						return this.files;
					},
					destroy: function() {
						this.input.off();
						this.label.off();
					}
				});
			});
			define('runtime/html5/util', ['base'], function(Base) {
				var urlAPI = window.createObjectURL && window || window.URL && URL.revokeObjectURL && URL || window.webkitURL,
					createObjectURL = Base.noop,
					revokeObjectURL = createObjectURL;
				if(urlAPI) {
					createObjectURL = function() {
						return urlAPI.createObjectURL.apply(urlAPI, arguments);
					};
					revokeObjectURL = function() {
						return urlAPI.revokeObjectURL.apply(urlAPI, arguments);
					};
				}
				return {
					createObjectURL: createObjectURL,
					revokeObjectURL: revokeObjectURL,
					dataURL2Blob: function(dataURI) {
						var byteStr, intArray, ab, i, mimetype, parts;
						parts = dataURI.split(',');
						if(~parts[0].indexOf('base64')) {
							byteStr = atob(parts[1]);
						} else {
							byteStr = decodeURIComponent(parts[1]);
						}
						ab = new ArrayBuffer(byteStr.length);
						intArray = new Uint8Array(ab);
						for(i = 0; i < byteStr.length; i++) {
							intArray[i] = byteStr.charCodeAt(i);
						}
						mimetype = parts[0].split(':')[1].split(';')[0];
						return this.arrayBufferToBlob(ab, mimetype);
					},
					dataURL2ArrayBuffer: function(dataURI) {
						var byteStr, intArray, i, parts;
						parts = dataURI.split(',');
						if(~parts[0].indexOf('base64')) {
							byteStr = atob(parts[1]);
						} else {
							byteStr = decodeURIComponent(parts[1]);
						}
						intArray = new Uint8Array(byteStr.length);
						for(i = 0; i < byteStr.length; i++) {
							intArray[i] = byteStr.charCodeAt(i);
						}
						return intArray.buffer;
					},
					arrayBufferToBlob: function(buffer, type) {
						var builder = window.BlobBuilder || window.WebKitBlobBuilder,
							bb;
						if(builder) {
							bb = new builder();
							bb.append(buffer);
							return bb.getBlob(type);
						}
						return new Blob([buffer], type ? {
							type: type
						} : {});
					},
					canvasToDataUrl: function(canvas, type, quality) {
						return canvas.toDataURL(type, quality / 100);
					},
					parseMeta: function(blob, callback) {
						callback(false, {});
					},
					updateImageHead: function(data) {
						return data;
					}
				};
			});
			define('runtime/html5/imagemeta', ['runtime/html5/util'], function(Util) {
				var api;
				api = {
					parsers: {
						0xffe1: []
					},
					maxMetaDataSize: 262144,
					parse: function(blob, cb) {
						var me = this,
							fr = new FileReader();
						fr.onload = function() {
							cb(false, me._parse(this.result));
							fr = fr.onload = fr.onerror = null;
						};
						fr.onerror = function(e) {
							cb(e.message);
							fr = fr.onload = fr.onerror = null;
						};
						blob = blob.slice(0, me.maxMetaDataSize);
						fr.readAsArrayBuffer(blob.getSource());
					},
					_parse: function(buffer, noParse) {
						if(buffer.byteLength < 6) {
							return;
						}
						var dataview = new DataView(buffer),
							offset = 2,
							maxOffset = dataview.byteLength - 4,
							headLength = offset,
							ret = {},
							markerBytes, markerLength, parsers, i;
						if(dataview.getUint16(0) === 0xffd8) {
							while(offset < maxOffset) {
								markerBytes = dataview.getUint16(offset);
								if(markerBytes >= 0xffe0 && markerBytes <= 0xffef || markerBytes === 0xfffe) {
									markerLength = dataview.getUint16(offset + 2) + 2;
									if(offset + markerLength > dataview.byteLength) {
										break;
									}
									parsers = api.parsers[markerBytes];
									if(!noParse && parsers) {
										for(i = 0; i < parsers.length; i += 1) {
											parsers[i].call(api, dataview, offset, markerLength, ret);
										}
									}
									offset += markerLength;
									headLength = offset;
								} else {
									break;
								}
							}
							if(headLength > 6) {
								if(buffer.slice) {
									ret.imageHead = buffer.slice(2, headLength);
								} else {
									ret.imageHead = new Uint8Array(buffer).subarray(2, headLength);
								}
							}
						}
						return ret;
					},
					updateImageHead: function(buffer, head) {
						var data = this._parse(buffer, true),
							buf1, buf2, bodyoffset;
						bodyoffset = 2;
						if(data.imageHead) {
							bodyoffset = 2 + data.imageHead.byteLength;
						}
						if(buffer.slice) {
							buf2 = buffer.slice(bodyoffset);
						} else {
							buf2 = new Uint8Array(buffer).subarray(bodyoffset);
						}
						buf1 = new Uint8Array(head.byteLength + 2 + buf2.byteLength);
						buf1[0] = 0xFF;
						buf1[1] = 0xD8;
						buf1.set(new Uint8Array(head), 2);
						buf1.set(new Uint8Array(buf2), head.byteLength + 2);
						return buf1.buffer;
					}
				};
				Util.parseMeta = function() {
					return api.parse.apply(api, arguments);
				};
				Util.updateImageHead = function() {
					return api.updateImageHead.apply(api, arguments);
				};
				return api;
			});
			define('runtime/html5/imagemeta/exif', ['base', 'runtime/html5/imagemeta'], function(Base, ImageMeta) {
				var EXIF = {};
				EXIF.ExifMap = function() {
					return this;
				};
				EXIF.ExifMap.prototype.map = {
					'Orientation': 0x0112
				};
				EXIF.ExifMap.prototype.get = function(id) {
					return this[id] || this[this.map[id]];
				};
				EXIF.exifTagTypes = {
					1: {
						getValue: function(dataView, dataOffset) {
							return dataView.getUint8(dataOffset);
						},
						size: 1
					},
					2: {
						getValue: function(dataView, dataOffset) {
							return String.fromCharCode(dataView.getUint8(dataOffset));
						},
						size: 1,
						ascii: true
					},
					3: {
						getValue: function(dataView, dataOffset, littleEndian) {
							return dataView.getUint16(dataOffset, littleEndian);
						},
						size: 2
					},
					4: {
						getValue: function(dataView, dataOffset, littleEndian) {
							return dataView.getUint32(dataOffset, littleEndian);
						},
						size: 4
					},
					5: {
						getValue: function(dataView, dataOffset, littleEndian) {
							return dataView.getUint32(dataOffset, littleEndian) / dataView.getUint32(dataOffset + 4, littleEndian);
						},
						size: 8
					},
					9: {
						getValue: function(dataView, dataOffset, littleEndian) {
							return dataView.getInt32(dataOffset, littleEndian);
						},
						size: 4
					},
					10: {
						getValue: function(dataView, dataOffset, littleEndian) {
							return dataView.getInt32(dataOffset, littleEndian) / dataView.getInt32(dataOffset + 4, littleEndian);
						},
						size: 8
					}
				};
				EXIF.exifTagTypes[7] = EXIF.exifTagTypes[1];
				EXIF.getExifValue = function(dataView, tiffOffset, offset, type, length, littleEndian) {
					var tagType = EXIF.exifTagTypes[type],
						tagSize, dataOffset, values, i, str, c;
					if(!tagType) {
						Base.log('Invalid Exif data: Invalid tag type.');
						return;
					}
					tagSize = tagType.size * length;
					dataOffset = tagSize > 4 ? tiffOffset + dataView.getUint32(offset + 8, littleEndian) : (offset + 8);
					if(dataOffset + tagSize > dataView.byteLength) {
						Base.log('Invalid Exif data: Invalid data offset.');
						return;
					}
					if(length === 1) {
						return tagType.getValue(dataView, dataOffset, littleEndian);
					}
					values = [];
					for(i = 0; i < length; i += 1) {
						values[i] = tagType.getValue(dataView, dataOffset + i * tagType.size, littleEndian);
					}
					if(tagType.ascii) {
						str = '';
						for(i = 0; i < values.length; i += 1) {
							c = values[i];
							if(c === '\u0000') {
								break;
							}
							str += c;
						}
						return str;
					}
					return values;
				};
				EXIF.parseExifTag = function(dataView, tiffOffset, offset, littleEndian, data) {
					var tag = dataView.getUint16(offset, littleEndian);
					data.exif[tag] = EXIF.getExifValue(dataView, tiffOffset, offset, dataView.getUint16(offset + 2, littleEndian), dataView.getUint32(offset + 4, littleEndian), littleEndian);
				};
				EXIF.parseExifTags = function(dataView, tiffOffset, dirOffset, littleEndian, data) {
					var tagsNumber, dirEndOffset, i;
					if(dirOffset + 6 > dataView.byteLength) {
						Base.log('Invalid Exif data: Invalid directory offset.');
						return;
					}
					tagsNumber = dataView.getUint16(dirOffset, littleEndian);
					dirEndOffset = dirOffset + 2 + 12 * tagsNumber;
					if(dirEndOffset + 4 > dataView.byteLength) {
						Base.log('Invalid Exif data: Invalid directory size.');
						return;
					}
					for(i = 0; i < tagsNumber; i += 1) {
						this.parseExifTag(dataView, tiffOffset, dirOffset + 2 + 12 * i, littleEndian, data);
					}
					return dataView.getUint32(dirEndOffset, littleEndian);
				};
				EXIF.parseExifData = function(dataView, offset, length, data) {
					var tiffOffset = offset + 10,
						littleEndian, dirOffset;
					if(dataView.getUint32(offset + 4) !== 0x45786966) {
						return;
					}
					if(tiffOffset + 8 > dataView.byteLength) {
						Base.log('Invalid Exif data: Invalid segment size.');
						return;
					}
					if(dataView.getUint16(offset + 8) !== 0x0000) {
						Base.log('Invalid Exif data: Missing byte alignment offset.');
						return;
					}
					switch(dataView.getUint16(tiffOffset)) {
						case 0x4949:
							littleEndian = true;
							break;
						case 0x4D4D:
							littleEndian = false;
							break;
						default:
							Base.log('Invalid Exif data: Invalid byte alignment marker.');
							return;
					}
					if(dataView.getUint16(tiffOffset + 2, littleEndian) !== 0x002A) {
						Base.log('Invalid Exif data: Missing TIFF marker.');
						return;
					}
					dirOffset = dataView.getUint32(tiffOffset + 4, littleEndian);
					data.exif = new EXIF.ExifMap();
					dirOffset = EXIF.parseExifTags(dataView, tiffOffset, tiffOffset + dirOffset, littleEndian, data);
				};
				ImageMeta.parsers[0xffe1].push(EXIF.parseExifData);
				return EXIF;
			});
			define('runtime/html5/jpegencoder', [], function(require, exports, module) {
				function JPEGEncoder(quality) {
					var self = this;
					var fround = Math.round;
					var ffloor = Math.floor;
					var YTable = new Array(64);
					var UVTable = new Array(64);
					var fdtbl_Y = new Array(64);
					var fdtbl_UV = new Array(64);
					var YDC_HT;
					var UVDC_HT;
					var YAC_HT;
					var UVAC_HT;
					var bitcode = new Array(65535);
					var category = new Array(65535);
					var outputfDCTQuant = new Array(64);
					var DU = new Array(64);
					var byteout = [];
					var bytenew = 0;
					var bytepos = 7;
					var YDU = new Array(64);
					var UDU = new Array(64);
					var VDU = new Array(64);
					var clt = new Array(256);
					var RGB_YUV_TABLE = new Array(2048);
					var currentQuality;
					var ZigZag = [0, 1, 5, 6, 14, 15, 27, 28, 2, 4, 7, 13, 16, 26, 29, 42, 3, 8, 12, 17, 25, 30, 41, 43, 9, 11, 18, 24, 31, 40, 44, 53, 10, 19, 23, 32, 39, 45, 52, 54, 20, 22, 33, 38, 46, 51, 55, 60, 21, 34, 37, 47, 50, 56, 59, 61, 35, 36, 48, 49, 57, 58, 62, 63];
					var std_dc_luminance_nrcodes = [0, 0, 1, 5, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0];
					var std_dc_luminance_values = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11];
					var std_ac_luminance_nrcodes = [0, 0, 2, 1, 3, 3, 2, 4, 3, 5, 5, 4, 4, 0, 0, 1, 0x7d];
					var std_ac_luminance_values = [0x01, 0x02, 0x03, 0x00, 0x04, 0x11, 0x05, 0x12, 0x21, 0x31, 0x41, 0x06, 0x13, 0x51, 0x61, 0x07, 0x22, 0x71, 0x14, 0x32, 0x81, 0x91, 0xa1, 0x08, 0x23, 0x42, 0xb1, 0xc1, 0x15, 0x52, 0xd1, 0xf0, 0x24, 0x33, 0x62, 0x72, 0x82, 0x09, 0x0a, 0x16, 0x17, 0x18, 0x19, 0x1a, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2a, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3a, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0x4a, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x5a, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0x6a, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7a, 0x83, 0x84, 0x85, 0x86, 0x87, 0x88, 0x89, 0x8a, 0x92, 0x93, 0x94, 0x95, 0x96, 0x97, 0x98, 0x99, 0x9a, 0xa2, 0xa3, 0xa4, 0xa5, 0xa6, 0xa7, 0xa8, 0xa9, 0xaa, 0xb2, 0xb3, 0xb4, 0xb5, 0xb6, 0xb7, 0xb8, 0xb9, 0xba, 0xc2, 0xc3, 0xc4, 0xc5, 0xc6, 0xc7, 0xc8, 0xc9, 0xca, 0xd2, 0xd3, 0xd4, 0xd5, 0xd6, 0xd7, 0xd8, 0xd9, 0xda, 0xe1, 0xe2, 0xe3, 0xe4, 0xe5, 0xe6, 0xe7, 0xe8, 0xe9, 0xea, 0xf1, 0xf2, 0xf3, 0xf4, 0xf5, 0xf6, 0xf7, 0xf8, 0xf9, 0xfa];
					var std_dc_chrominance_nrcodes = [0, 0, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0];
					var std_dc_chrominance_values = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11];
					var std_ac_chrominance_nrcodes = [0, 0, 2, 1, 2, 4, 4, 3, 4, 7, 5, 4, 4, 0, 1, 2, 0x77];
					var std_ac_chrominance_values = [0x00, 0x01, 0x02, 0x03, 0x11, 0x04, 0x05, 0x21, 0x31, 0x06, 0x12, 0x41, 0x51, 0x07, 0x61, 0x71, 0x13, 0x22, 0x32, 0x81, 0x08, 0x14, 0x42, 0x91, 0xa1, 0xb1, 0xc1, 0x09, 0x23, 0x33, 0x52, 0xf0, 0x15, 0x62, 0x72, 0xd1, 0x0a, 0x16, 0x24, 0x34, 0xe1, 0x25, 0xf1, 0x17, 0x18, 0x19, 0x1a, 0x26, 0x27, 0x28, 0x29, 0x2a, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3a, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0x4a, 0x53, 0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x5a, 0x63, 0x64, 0x65, 0x66, 0x67, 0x68, 0x69, 0x6a, 0x73, 0x74, 0x75, 0x76, 0x77, 0x78, 0x79, 0x7a, 0x82, 0x83, 0x84, 0x85, 0x86, 0x87, 0x88, 0x89, 0x8a, 0x92, 0x93, 0x94, 0x95, 0x96, 0x97, 0x98, 0x99, 0x9a, 0xa2, 0xa3, 0xa4, 0xa5, 0xa6, 0xa7, 0xa8, 0xa9, 0xaa, 0xb2, 0xb3, 0xb4, 0xb5, 0xb6, 0xb7, 0xb8, 0xb9, 0xba, 0xc2, 0xc3, 0xc4, 0xc5, 0xc6, 0xc7, 0xc8, 0xc9, 0xca, 0xd2, 0xd3, 0xd4, 0xd5, 0xd6, 0xd7, 0xd8, 0xd9, 0xda, 0xe2, 0xe3, 0xe4, 0xe5, 0xe6, 0xe7, 0xe8, 0xe9, 0xea, 0xf2, 0xf3, 0xf4, 0xf5, 0xf6, 0xf7, 0xf8, 0xf9, 0xfa];

					function initQuantTables(sf) {
						var YQT = [16, 11, 10, 16, 24, 40, 51, 61, 12, 12, 14, 19, 26, 58, 60, 55, 14, 13, 16, 24, 40, 57, 69, 56, 14, 17, 22, 29, 51, 87, 80, 62, 18, 22, 37, 56, 68, 109, 103, 77, 24, 35, 55, 64, 81, 104, 113, 92, 49, 64, 78, 87, 103, 121, 120, 101, 72, 92, 95, 98, 112, 100, 103, 99];
						for(var i = 0; i < 64; i++) {
							var t = ffloor((YQT[i] * sf + 50) / 100);
							if(t < 1) {
								t = 1;
							} else if(t > 255) {
								t = 255;
							}
							YTable[ZigZag[i]] = t;
						}
						var UVQT = [17, 18, 24, 47, 99, 99, 99, 99, 18, 21, 26, 66, 99, 99, 99, 99, 24, 26, 56, 99, 99, 99, 99, 99, 47, 66, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99, 99];
						for(var j = 0; j < 64; j++) {
							var u = ffloor((UVQT[j] * sf + 50) / 100);
							if(u < 1) {
								u = 1;
							} else if(u > 255) {
								u = 255;
							}
							UVTable[ZigZag[j]] = u;
						}
						var aasf = [1.0, 1.387039845, 1.306562965, 1.175875602, 1.0, 0.785694958, 0.541196100, 0.275899379];
						var k = 0;
						for(var row = 0; row < 8; row++) {
							for(var col = 0; col < 8; col++) {
								fdtbl_Y[k] = (1.0 / (YTable[ZigZag[k]] * aasf[row] * aasf[col] * 8.0));
								fdtbl_UV[k] = (1.0 / (UVTable[ZigZag[k]] * aasf[row] * aasf[col] * 8.0));
								k++;
							}
						}
					}

					function computeHuffmanTbl(nrcodes, std_table) {
						var codevalue = 0;
						var pos_in_table = 0;
						var HT = new Array();
						for(var k = 1; k <= 16; k++) {
							for(var j = 1; j <= nrcodes[k]; j++) {
								HT[std_table[pos_in_table]] = [];
								HT[std_table[pos_in_table]][0] = codevalue;
								HT[std_table[pos_in_table]][1] = k;
								pos_in_table++;
								codevalue++;
							}
							codevalue *= 2;
						}
						return HT;
					}

					function initHuffmanTbl() {
						YDC_HT = computeHuffmanTbl(std_dc_luminance_nrcodes, std_dc_luminance_values);
						UVDC_HT = computeHuffmanTbl(std_dc_chrominance_nrcodes, std_dc_chrominance_values);
						YAC_HT = computeHuffmanTbl(std_ac_luminance_nrcodes, std_ac_luminance_values);
						UVAC_HT = computeHuffmanTbl(std_ac_chrominance_nrcodes, std_ac_chrominance_values);
					}

					function initCategoryNumber() {
						var nrlower = 1;
						var nrupper = 2;
						for(var cat = 1; cat <= 15; cat++) {
							for(var nr = nrlower; nr < nrupper; nr++) {
								category[32767 + nr] = cat;
								bitcode[32767 + nr] = [];
								bitcode[32767 + nr][1] = cat;
								bitcode[32767 + nr][0] = nr;
							}
							for(var nrneg = -(nrupper - 1); nrneg <= -nrlower; nrneg++) {
								category[32767 + nrneg] = cat;
								bitcode[32767 + nrneg] = [];
								bitcode[32767 + nrneg][1] = cat;
								bitcode[32767 + nrneg][0] = nrupper - 1 + nrneg;
							}
							nrlower <<= 1;
							nrupper <<= 1;
						}
					}

					function initRGBYUVTable() {
						for(var i = 0; i < 256; i++) {
							RGB_YUV_TABLE[i] = 19595 * i;
							RGB_YUV_TABLE[(i + 256) >> 0] = 38470 * i;
							RGB_YUV_TABLE[(i + 512) >> 0] = 7471 * i + 0x8000;
							RGB_YUV_TABLE[(i + 768) >> 0] = -11059 * i;
							RGB_YUV_TABLE[(i + 1024) >> 0] = -21709 * i;
							RGB_YUV_TABLE[(i + 1280) >> 0] = 32768 * i + 0x807FFF;
							RGB_YUV_TABLE[(i + 1536) >> 0] = -27439 * i;
							RGB_YUV_TABLE[(i + 1792) >> 0] = -5329 * i;
						}
					}

					function writeBits(bs) {
						var value = bs[0];
						var posval = bs[1] - 1;
						while(posval >= 0) {
							if(value & (1 << posval)) {
								bytenew |= (1 << bytepos);
							}
							posval--;
							bytepos--;
							if(bytepos < 0) {
								if(bytenew == 0xFF) {
									writeByte(0xFF);
									writeByte(0);
								} else {
									writeByte(bytenew);
								}
								bytepos = 7;
								bytenew = 0;
							}
						}
					}

					function writeByte(value) {
						byteout.push(clt[value]);
					}

					function writeWord(value) {
						writeByte((value >> 8) & 0xFF);
						writeByte((value) & 0xFF);
					}

					function fDCTQuant(data, fdtbl) {
						var d0, d1, d2, d3, d4, d5, d6, d7;
						var dataOff = 0;
						var i;
						var I8 = 8;
						var I64 = 64;
						for(i = 0; i < I8; ++i) {
							d0 = data[dataOff];
							d1 = data[dataOff + 1];
							d2 = data[dataOff + 2];
							d3 = data[dataOff + 3];
							d4 = data[dataOff + 4];
							d5 = data[dataOff + 5];
							d6 = data[dataOff + 6];
							d7 = data[dataOff + 7];
							var tmp0 = d0 + d7;
							var tmp7 = d0 - d7;
							var tmp1 = d1 + d6;
							var tmp6 = d1 - d6;
							var tmp2 = d2 + d5;
							var tmp5 = d2 - d5;
							var tmp3 = d3 + d4;
							var tmp4 = d3 - d4;
							var tmp10 = tmp0 + tmp3;
							var tmp13 = tmp0 - tmp3;
							var tmp11 = tmp1 + tmp2;
							var tmp12 = tmp1 - tmp2;
							data[dataOff] = tmp10 + tmp11;
							data[dataOff + 4] = tmp10 - tmp11;
							var z1 = (tmp12 + tmp13) * 0.707106781;
							data[dataOff + 2] = tmp13 + z1;
							data[dataOff + 6] = tmp13 - z1;
							tmp10 = tmp4 + tmp5;
							tmp11 = tmp5 + tmp6;
							tmp12 = tmp6 + tmp7;
							var z5 = (tmp10 - tmp12) * 0.382683433;
							var z2 = 0.541196100 * tmp10 + z5;
							var z4 = 1.306562965 * tmp12 + z5;
							var z3 = tmp11 * 0.707106781;
							var z11 = tmp7 + z3;
							var z13 = tmp7 - z3;
							data[dataOff + 5] = z13 + z2;
							data[dataOff + 3] = z13 - z2;
							data[dataOff + 1] = z11 + z4;
							data[dataOff + 7] = z11 - z4;
							dataOff += 8;
						}
						dataOff = 0;
						for(i = 0; i < I8; ++i) {
							d0 = data[dataOff];
							d1 = data[dataOff + 8];
							d2 = data[dataOff + 16];
							d3 = data[dataOff + 24];
							d4 = data[dataOff + 32];
							d5 = data[dataOff + 40];
							d6 = data[dataOff + 48];
							d7 = data[dataOff + 56];
							var tmp0p2 = d0 + d7;
							var tmp7p2 = d0 - d7;
							var tmp1p2 = d1 + d6;
							var tmp6p2 = d1 - d6;
							var tmp2p2 = d2 + d5;
							var tmp5p2 = d2 - d5;
							var tmp3p2 = d3 + d4;
							var tmp4p2 = d3 - d4;
							var tmp10p2 = tmp0p2 + tmp3p2;
							var tmp13p2 = tmp0p2 - tmp3p2;
							var tmp11p2 = tmp1p2 + tmp2p2;
							var tmp12p2 = tmp1p2 - tmp2p2;
							data[dataOff] = tmp10p2 + tmp11p2;
							data[dataOff + 32] = tmp10p2 - tmp11p2;
							var z1p2 = (tmp12p2 + tmp13p2) * 0.707106781;
							data[dataOff + 16] = tmp13p2 + z1p2;
							data[dataOff + 48] = tmp13p2 - z1p2;
							tmp10p2 = tmp4p2 + tmp5p2;
							tmp11p2 = tmp5p2 + tmp6p2;
							tmp12p2 = tmp6p2 + tmp7p2;
							var z5p2 = (tmp10p2 - tmp12p2) * 0.382683433;
							var z2p2 = 0.541196100 * tmp10p2 + z5p2;
							var z4p2 = 1.306562965 * tmp12p2 + z5p2;
							var z3p2 = tmp11p2 * 0.707106781;
							var z11p2 = tmp7p2 + z3p2;
							var z13p2 = tmp7p2 - z3p2;
							data[dataOff + 40] = z13p2 + z2p2;
							data[dataOff + 24] = z13p2 - z2p2;
							data[dataOff + 8] = z11p2 + z4p2;
							data[dataOff + 56] = z11p2 - z4p2;
							dataOff++;
						}
						var fDCTQuant;
						for(i = 0; i < I64; ++i) {
							fDCTQuant = data[i] * fdtbl[i];
							outputfDCTQuant[i] = (fDCTQuant > 0.0) ? ((fDCTQuant + 0.5) | 0) : ((fDCTQuant - 0.5) | 0);
						}
						return outputfDCTQuant;
					}

					function writeAPP0() {
						writeWord(0xFFE0);
						writeWord(16);
						writeByte(0x4A);
						writeByte(0x46);
						writeByte(0x49);
						writeByte(0x46);
						writeByte(0);
						writeByte(1);
						writeByte(1);
						writeByte(0);
						writeWord(1);
						writeWord(1);
						writeByte(0);
						writeByte(0);
					}

					function writeSOF0(width, height) {
						writeWord(0xFFC0);
						writeWord(17);
						writeByte(8);
						writeWord(height);
						writeWord(width);
						writeByte(3);
						writeByte(1);
						writeByte(0x11);
						writeByte(0);
						writeByte(2);
						writeByte(0x11);
						writeByte(1);
						writeByte(3);
						writeByte(0x11);
						writeByte(1);
					}

					function writeDQT() {
						writeWord(0xFFDB);
						writeWord(132);
						writeByte(0);
						for(var i = 0; i < 64; i++) {
							writeByte(YTable[i]);
						}
						writeByte(1);
						for(var j = 0; j < 64; j++) {
							writeByte(UVTable[j]);
						}
					}

					function writeDHT() {
						writeWord(0xFFC4);
						writeWord(0x01A2);
						writeByte(0);
						for(var i = 0; i < 16; i++) {
							writeByte(std_dc_luminance_nrcodes[i + 1]);
						}
						for(var j = 0; j <= 11; j++) {
							writeByte(std_dc_luminance_values[j]);
						}
						writeByte(0x10);
						for(var k = 0; k < 16; k++) {
							writeByte(std_ac_luminance_nrcodes[k + 1]);
						}
						for(var l = 0; l <= 161; l++) {
							writeByte(std_ac_luminance_values[l]);
						}
						writeByte(1);
						for(var m = 0; m < 16; m++) {
							writeByte(std_dc_chrominance_nrcodes[m + 1]);
						}
						for(var n = 0; n <= 11; n++) {
							writeByte(std_dc_chrominance_values[n]);
						}
						writeByte(0x11);
						for(var o = 0; o < 16; o++) {
							writeByte(std_ac_chrominance_nrcodes[o + 1]);
						}
						for(var p = 0; p <= 161; p++) {
							writeByte(std_ac_chrominance_values[p]);
						}
					}

					function writeSOS() {
						writeWord(0xFFDA);
						writeWord(12);
						writeByte(3);
						writeByte(1);
						writeByte(0);
						writeByte(2);
						writeByte(0x11);
						writeByte(3);
						writeByte(0x11);
						writeByte(0);
						writeByte(0x3f);
						writeByte(0);
					}

					function processDU(CDU, fdtbl, DC, HTDC, HTAC) {
						var EOB = HTAC[0x00];
						var M16zeroes = HTAC[0xF0];
						var pos;
						var I16 = 16;
						var I63 = 63;
						var I64 = 64;
						var DU_DCT = fDCTQuant(CDU, fdtbl);
						for(var j = 0; j < I64; ++j) {
							DU[ZigZag[j]] = DU_DCT[j];
						}
						var Diff = DU[0] - DC;
						DC = DU[0];
						if(Diff == 0) {
							writeBits(HTDC[0]);
						} else {
							pos = 32767 + Diff;
							writeBits(HTDC[category[pos]]);
							writeBits(bitcode[pos]);
						}
						var end0pos = 63;
						for(;
							(end0pos > 0) && (DU[end0pos] == 0); end0pos--) {};
						if(end0pos == 0) {
							writeBits(EOB);
							return DC;
						}
						var i = 1;
						var lng;
						while(i <= end0pos) {
							var startpos = i;
							for(;
								(DU[i] == 0) && (i <= end0pos); ++i) {}
							var nrzeroes = i - startpos;
							if(nrzeroes >= I16) {
								lng = nrzeroes >> 4;
								for(var nrmarker = 1; nrmarker <= lng; ++nrmarker)
									writeBits(M16zeroes);
								nrzeroes = nrzeroes & 0xF;
							}
							pos = 32767 + DU[i];
							writeBits(HTAC[(nrzeroes << 4) + category[pos]]);
							writeBits(bitcode[pos]);
							i++;
						}
						if(end0pos != I63) {
							writeBits(EOB);
						}
						return DC;
					}

					function initCharLookupTable() {
						var sfcc = String.fromCharCode;
						for(var i = 0; i < 256; i++) {
							clt[i] = sfcc(i);
						}
					}
					this.encode = function(image, quality) {
						if(quality) setQuality(quality);
						byteout = new Array();
						bytenew = 0;
						bytepos = 7;
						writeWord(0xFFD8);
						writeAPP0();
						writeDQT();
						writeSOF0(image.width, image.height);
						writeDHT();
						writeSOS();
						var DCY = 0;
						var DCU = 0;
						var DCV = 0;
						bytenew = 0;
						bytepos = 7;
						this.encode.displayName = "_encode_";
						var imageData = image.data;
						var width = image.width;
						var height = image.height;
						var quadWidth = width * 4;
						var tripleWidth = width * 3;
						var x, y = 0;
						var r, g, b;
						var start, p, col, row, pos;
						while(y < height) {
							x = 0;
							while(x < quadWidth) {
								start = quadWidth * y + x;
								p = start;
								col = -1;
								row = 0;
								for(pos = 0; pos < 64; pos++) {
									row = pos >> 3;
									col = (pos & 7) * 4;
									p = start + (row * quadWidth) + col;
									if(y + row >= height) {
										p -= (quadWidth * (y + 1 + row - height));
									}
									if(x + col >= quadWidth) {
										p -= ((x + col) - quadWidth + 4)
									}
									r = imageData[p++];
									g = imageData[p++];
									b = imageData[p++];
									YDU[pos] = ((RGB_YUV_TABLE[r] + RGB_YUV_TABLE[(g + 256) >> 0] + RGB_YUV_TABLE[(b + 512) >> 0]) >> 16) - 128;
									UDU[pos] = ((RGB_YUV_TABLE[(r + 768) >> 0] + RGB_YUV_TABLE[(g + 1024) >> 0] + RGB_YUV_TABLE[(b + 1280) >> 0]) >> 16) - 128;
									VDU[pos] = ((RGB_YUV_TABLE[(r + 1280) >> 0] + RGB_YUV_TABLE[(g + 1536) >> 0] + RGB_YUV_TABLE[(b + 1792) >> 0]) >> 16) - 128;
								}
								DCY = processDU(YDU, fdtbl_Y, DCY, YDC_HT, YAC_HT);
								DCU = processDU(UDU, fdtbl_UV, DCU, UVDC_HT, UVAC_HT);
								DCV = processDU(VDU, fdtbl_UV, DCV, UVDC_HT, UVAC_HT);
								x += 32;
							}
							y += 8;
						}
						if(bytepos >= 0) {
							var fillbits = [];
							fillbits[1] = bytepos + 1;
							fillbits[0] = (1 << (bytepos + 1)) - 1;
							writeBits(fillbits);
						}
						writeWord(0xFFD9);
						var jpegDataUri = 'data:image/jpeg;base64,' + btoa(byteout.join(''));
						byteout = [];
						return jpegDataUri
					}

					function setQuality(quality) {
						if(quality <= 0) {
							quality = 1;
						}
						if(quality > 100) {
							quality = 100;
						}
						if(currentQuality == quality) return
						var sf = 0;
						if(quality < 50) {
							sf = Math.floor(5000 / quality);
						} else {
							sf = Math.floor(200 - quality * 2);
						}
						initQuantTables(sf);
						currentQuality = quality;
					}

					function init() {
						if(!quality) quality = 50;
						initCharLookupTable()
						initHuffmanTbl();
						initCategoryNumber();
						initRGBYUVTable();
						setQuality(quality);
					}
					init();
				};
				JPEGEncoder.encode = function(data, quality) {
					var encoder = new JPEGEncoder(quality);
					return encoder.encode(data);
				}
				return JPEGEncoder;
			});
			define('runtime/html5/androidpatch', ['runtime/html5/util', 'runtime/html5/jpegencoder', 'base'], function(Util, encoder, Base) {
				var origin = Util.canvasToDataUrl,
					supportJpeg;
				Util.canvasToDataUrl = function(canvas, type, quality) {
					var ctx, w, h, fragement, parts;
					if(!Base.os.android) {
						return origin.apply(null, arguments);
					}
					if(type === 'image/jpeg' && typeof supportJpeg === 'undefined') {
						fragement = origin.apply(null, arguments);
						parts = fragement.split(',');
						if(~parts[0].indexOf('base64')) {
							fragement = atob(parts[1]);
						} else {
							fragement = decodeURIComponent(parts[1]);
						}
						fragement = fragement.substring(0, 2);
						supportJpeg = fragement.charCodeAt(0) === 255 && fragement.charCodeAt(1) === 216;
					}
					if(type === 'image/jpeg' && !supportJpeg) {
						w = canvas.width;
						h = canvas.height;
						ctx = canvas.getContext('2d');
						return encoder.encode(ctx.getImageData(0, 0, w, h), quality);
					}
					return origin.apply(null, arguments);
				};
			});
			define('runtime/html5/image', ['base', 'runtime/html5/runtime', 'runtime/html5/util'], function(Base, Html5Runtime, Util) {
				var BLANK = 'data:image/gif;base64,R0lGODlhAQABAAD/ACwAAAAAAQABAAACADs%3D';
				return Html5Runtime.register('Image', {
					modified: false,
					init: function() {
						var me = this,
							img = new Image();
						img.onload = function() {
							me._info = {
								type: me.type,
								width: this.width,
								height: this.height
							};
							if(!me._metas && 'image/jpeg' === me.type) {
								Util.parseMeta(me._blob, function(error, ret) {
									me._metas = ret;
									me.owner.trigger('load');
								});
							} else {
								me.owner.trigger('load');
							}
						};
						img.onerror = function() {
							me.owner.trigger('error');
						};
						me._img = img;
					},
					loadFromBlob: function(blob) {
						var me = this,
							img = me._img;
						me._blob = blob;
						me.type = blob.type;
						img.src = Util.createObjectURL(blob.getSource());
						me.owner.once('load', function() {
							Util.revokeObjectURL(img.src);
						});
					},
					resize: function(width, height) {
						var canvas = this._canvas || (this._canvas = document.createElement('canvas'));
						this._resize(this._img, canvas, width, height);
						this._blob = null;
						this.modified = true;
						this.owner.trigger('complete', 'resize');
					},
					crop: function(x, y, w, h, s) {
						var cvs = this._canvas || (this._canvas = document.createElement('canvas')),
							opts = this.options,
							img = this._img,
							iw = img.naturalWidth,
							ih = img.naturalHeight,
							orientation = this.getOrientation();
						s = s || 1;
						cvs.width = w;
						cvs.height = h;
						opts.preserveHeaders || this._rotate2Orientaion(cvs, orientation);
						this._renderImageToCanvas(cvs, img, -x, -y, iw * s, ih * s);
						this._blob = null;
						this.modified = true;
						this.owner.trigger('complete', 'crop');
					},
					getAsBlob: function(type) {
						var blob = this._blob,
							opts = this.options,
							canvas;
						type = type || this.type;
						if(this.modified || this.type !== type) {
							canvas = this._canvas;
							if(type === 'image/jpeg') {
								blob = Util.canvasToDataUrl(canvas, type, opts.quality);
								if(opts.preserveHeaders && this._metas && this._metas.imageHead) {
									blob = Util.dataURL2ArrayBuffer(blob);
									blob = Util.updateImageHead(blob, this._metas.imageHead);
									blob = Util.arrayBufferToBlob(blob, type);
									return blob;
								}
							} else {
								blob = Util.canvasToDataUrl(canvas, type);
							}
							blob = Util.dataURL2Blob(blob);
						}
						return blob;
					},
					getAsDataUrl: function(type) {
						var opts = this.options;
						type = type || this.type;
						if(type === 'image/jpeg') {
							return Util.canvasToDataUrl(this._canvas, type, opts.quality);
						} else {
							return this._canvas.toDataURL(type);
						}
					},
					getOrientation: function() {
						return this._metas && this._metas.exif && this._metas.exif.get('Orientation') || 1;
					},
					info: function(val) {
						if(val) {
							this._info = val;
							return this;
						}
						return this._info;
					},
					meta: function(val) {
						if(val) {
							this._meta = val;
							return this;
						}
						return this._meta;
					},
					destroy: function() {
						var canvas = this._canvas;
						this._img.onload = null;
						if(canvas) {
							canvas.getContext('2d').clearRect(0, 0, canvas.width, canvas.height);
							canvas.width = canvas.height = 0;
							this._canvas = null;
						}
						this._img.src = BLANK;
						this._img = this._blob = null;
					},
					_resize: function(img, cvs, width, height) {
						var opts = this.options,
							naturalWidth = img.width,
							naturalHeight = img.height,
							orientation = this.getOrientation(),
							scale, w, h, x, y;
						if(~[5, 6, 7, 8].indexOf(orientation)) {
							width ^= height;
							height ^= width;
							width ^= height;
						}
						scale = Math[opts.crop ? 'max' : 'min'](width / naturalWidth, height / naturalHeight);
						opts.allowMagnify || (scale = Math.min(1, scale));
						w = naturalWidth * scale;
						h = naturalHeight * scale;
						if(opts.crop) {
							cvs.width = width;
							cvs.height = height;
						} else {
							cvs.width = w;
							cvs.height = h;
						}
						x = (cvs.width - w) / 2;
						y = (cvs.height - h) / 2;
						opts.preserveHeaders || this._rotate2Orientaion(cvs, orientation);
						this._renderImageToCanvas(cvs, img, x, y, w, h);
					},
					_rotate2Orientaion: function(canvas, orientation) {
						var width = canvas.width,
							height = canvas.height,
							ctx = canvas.getContext('2d');
						switch(orientation) {
							case 5:
							case 6:
							case 7:
							case 8:
								canvas.width = height;
								canvas.height = width;
								break;
						}
						switch(orientation) {
							case 2:
								ctx.translate(width, 0);
								ctx.scale(-1, 1);
								break;
							case 3:
								ctx.translate(width, height);
								ctx.rotate(Math.PI);
								break;
							case 4:
								ctx.translate(0, height);
								ctx.scale(1, -1);
								break;
							case 5:
								ctx.rotate(0.5 * Math.PI);
								ctx.scale(1, -1);
								break;
							case 6:
								ctx.rotate(0.5 * Math.PI);
								ctx.translate(0, -height);
								break;
							case 7:
								ctx.rotate(0.5 * Math.PI);
								ctx.translate(width, -height);
								ctx.scale(-1, 1);
								break;
							case 8:
								ctx.rotate(-0.5 * Math.PI);
								ctx.translate(-width, 0);
								break;
						}
					},
					_renderImageToCanvas: (function() {
						if(!Base.os.ios) {
							return function(canvas) {
								var args = Base.slice(arguments, 1),
									ctx = canvas.getContext('2d');
								ctx.drawImage.apply(ctx, args);
							};
						}

						function detectVerticalSquash(img, iw, ih) {
							var canvas = document.createElement('canvas'),
								ctx = canvas.getContext('2d'),
								sy = 0,
								ey = ih,
								py = ih,
								data, alpha, ratio;
							canvas.width = 1;
							canvas.height = ih;
							ctx.drawImage(img, 0, 0);
							data = ctx.getImageData(0, 0, 1, ih).data;
							while(py > sy) {
								alpha = data[(py - 1) * 4 + 3];
								if(alpha === 0) {
									ey = py;
								} else {
									sy = py;
								}
								py = (ey + sy) >> 1;
							}
							ratio = (py / ih);
							return(ratio === 0) ? 1 : ratio;
						}
						if(Base.os.ios >= 7) {
							return function(canvas, img, x, y, w, h) {
								var iw = img.naturalWidth,
									ih = img.naturalHeight,
									vertSquashRatio = detectVerticalSquash(img, iw, ih);
								return canvas.getContext('2d').drawImage(img, 0, 0, iw * vertSquashRatio, ih * vertSquashRatio, x, y, w, h);
							};
						}

						function detectSubsampling(img) {
							var iw = img.naturalWidth,
								ih = img.naturalHeight,
								canvas, ctx;
							if(iw * ih > 1024 * 1024) {
								canvas = document.createElement('canvas');
								canvas.width = canvas.height = 1;
								ctx = canvas.getContext('2d');
								ctx.drawImage(img, -iw + 1, 0);
								return ctx.getImageData(0, 0, 1, 1).data[3] === 0;
							} else {
								return false;
							}
						}
						return function(canvas, img, x, y, width, height) {
							var iw = img.naturalWidth,
								ih = img.naturalHeight,
								ctx = canvas.getContext('2d'),
								subsampled = detectSubsampling(img),
								doSquash = this.type === 'image/jpeg',
								d = 1024,
								sy = 0,
								dy = 0,
								tmpCanvas, tmpCtx, vertSquashRatio, dw, dh, sx, dx;
							if(subsampled) {
								iw /= 2;
								ih /= 2;
							}
							ctx.save();
							tmpCanvas = document.createElement('canvas');
							tmpCanvas.width = tmpCanvas.height = d;
							tmpCtx = tmpCanvas.getContext('2d');
							vertSquashRatio = doSquash ? detectVerticalSquash(img, iw, ih) : 1;
							dw = Math.ceil(d * width / iw);
							dh = Math.ceil(d * height / ih / vertSquashRatio);
							while(sy < ih) {
								sx = 0;
								dx = 0;
								while(sx < iw) {
									tmpCtx.clearRect(0, 0, d, d);
									tmpCtx.drawImage(img, -sx, -sy);
									ctx.drawImage(tmpCanvas, 0, 0, d, d, x + dx, y + dy, dw, dh);
									sx += d;
									dx += dw;
								}
								sy += d;
								dy += dh;
							}
							ctx.restore();
							tmpCanvas = tmpCtx = null;
						};
					})()
				});
			});
			define('runtime/html5/transport', ['base', 'runtime/html5/runtime'], function(Base, Html5Runtime) {
				var noop = Base.noop,
					$ = Base.$;
				return Html5Runtime.register('Transport', {
					init: function() {
						this._status = 0;
						this._response = null;
					},
					send: function() {
						var owner = this.owner,
							opts = this.options,
							xhr = this._initAjax(),
							blob = owner._blob,
							server = opts.server,
							formData, binary, fr;
						if(opts.sendAsBinary) {
							server += (/\?/.test(server) ? '&' : '?') +
								$.param(owner._formData);
							binary = blob.getSource();
						} else {
							formData = new FormData();
							$.each(owner._formData, function(k, v) {
								formData.append(k, v);
							});
							formData.append(opts.fileVal, blob.getSource(), opts.filename || owner._formData.name || '');
						}
						if(opts.withCredentials && 'withCredentials' in xhr) {
							xhr.open(opts.method, server, true);
							xhr.withCredentials = true;
						} else {
							xhr.open(opts.method, server);
						}
						this._setRequestHeader(xhr, opts.headers);
						if(binary) {
							xhr.overrideMimeType && xhr.overrideMimeType('application/octet-stream');
							if(Base.os.android) {
								fr = new FileReader();
								fr.onload = function() {
									xhr.send(this.result);
									fr = fr.onload = null;
								};
								fr.readAsArrayBuffer(binary);
							} else {
								xhr.send(binary);
							}
						} else {
							xhr.send(formData);
						}
					},
					getResponse: function() {
						return this._response;
					},
					getResponseAsJson: function() {
						return this._parseJson(this._response);
					},
					getStatus: function() {
						return this._status;
					},
					abort: function() {
						var xhr = this._xhr;
						if(xhr) {
							xhr.upload.onprogress = noop;
							xhr.onreadystatechange = noop;
							xhr.abort();
							this._xhr = xhr = null;
						}
					},
					destroy: function() {
						this.abort();
					},
					_initAjax: function() {
						var me = this,
							xhr = new XMLHttpRequest(),
							opts = this.options;
						if(opts.withCredentials && !('withCredentials' in xhr) && typeof XDomainRequest !== 'undefined') {
							xhr = new XDomainRequest();
						}
						xhr.upload.onprogress = function(e) {
							var percentage = 0;
							if(e.lengthComputable) {
								percentage = e.loaded / e.total;
							}
							return me.trigger('progress', percentage);
						};
						xhr.onreadystatechange = function() {
							if(xhr.readyState !== 4) {
								return;
							}
							xhr.upload.onprogress = noop;
							xhr.onreadystatechange = noop;
							me._xhr = null;
							me._status = xhr.status;
							if(xhr.status >= 200 && xhr.status < 300) {
								me._response = xhr.responseText;
								return me.trigger('load');
							} else if(xhr.status >= 500 && xhr.status < 600) {
								me._response = xhr.responseText;
								return me.trigger('error', 'server');
							}
							return me.trigger('error', me._status ? 'http' : 'abort');
						};
						me._xhr = xhr;
						return xhr;
					},
					_setRequestHeader: function(xhr, headers) {
						$.each(headers, function(key, val) {
							xhr.setRequestHeader(key, val);
						});
					},
					_parseJson: function(str) {
						var json;
						try {
							json = JSON.parse(str);
						} catch(ex) {
							json = {};
						}
						return json;
					}
				});
			});
			define('runtime/html5/md5', ['runtime/html5/runtime'], function(FlashRuntime) {
				var add32 = function(a, b) {
						return(a + b) & 0xFFFFFFFF;
					},
					cmn = function(q, a, b, x, s, t) {
						a = add32(add32(a, q), add32(x, t));
						return add32((a << s) | (a >>> (32 - s)), b);
					},
					ff = function(a, b, c, d, x, s, t) {
						return cmn((b & c) | ((~b) & d), a, b, x, s, t);
					},
					gg = function(a, b, c, d, x, s, t) {
						return cmn((b & d) | (c & (~d)), a, b, x, s, t);
					},
					hh = function(a, b, c, d, x, s, t) {
						return cmn(b ^ c ^ d, a, b, x, s, t);
					},
					ii = function(a, b, c, d, x, s, t) {
						return cmn(c ^ (b | (~d)), a, b, x, s, t);
					},
					md5cycle = function(x, k) {
						var a = x[0],
							b = x[1],
							c = x[2],
							d = x[3];
						a = ff(a, b, c, d, k[0], 7, -680876936);
						d = ff(d, a, b, c, k[1], 12, -389564586);
						c = ff(c, d, a, b, k[2], 17, 606105819);
						b = ff(b, c, d, a, k[3], 22, -1044525330);
						a = ff(a, b, c, d, k[4], 7, -176418897);
						d = ff(d, a, b, c, k[5], 12, 1200080426);
						c = ff(c, d, a, b, k[6], 17, -1473231341);
						b = ff(b, c, d, a, k[7], 22, -45705983);
						a = ff(a, b, c, d, k[8], 7, 1770035416);
						d = ff(d, a, b, c, k[9], 12, -1958414417);
						c = ff(c, d, a, b, k[10], 17, -42063);
						b = ff(b, c, d, a, k[11], 22, -1990404162);
						a = ff(a, b, c, d, k[12], 7, 1804603682);
						d = ff(d, a, b, c, k[13], 12, -40341101);
						c = ff(c, d, a, b, k[14], 17, -1502002290);
						b = ff(b, c, d, a, k[15], 22, 1236535329);
						a = gg(a, b, c, d, k[1], 5, -165796510);
						d = gg(d, a, b, c, k[6], 9, -1069501632);
						c = gg(c, d, a, b, k[11], 14, 643717713);
						b = gg(b, c, d, a, k[0], 20, -373897302);
						a = gg(a, b, c, d, k[5], 5, -701558691);
						d = gg(d, a, b, c, k[10], 9, 38016083);
						c = gg(c, d, a, b, k[15], 14, -660478335);
						b = gg(b, c, d, a, k[4], 20, -405537848);
						a = gg(a, b, c, d, k[9], 5, 568446438);
						d = gg(d, a, b, c, k[14], 9, -1019803690);
						c = gg(c, d, a, b, k[3], 14, -187363961);
						b = gg(b, c, d, a, k[8], 20, 1163531501);
						a = gg(a, b, c, d, k[13], 5, -1444681467);
						d = gg(d, a, b, c, k[2], 9, -51403784);
						c = gg(c, d, a, b, k[7], 14, 1735328473);
						b = gg(b, c, d, a, k[12], 20, -1926607734);
						a = hh(a, b, c, d, k[5], 4, -378558);
						d = hh(d, a, b, c, k[8], 11, -2022574463);
						c = hh(c, d, a, b, k[11], 16, 1839030562);
						b = hh(b, c, d, a, k[14], 23, -35309556);
						a = hh(a, b, c, d, k[1], 4, -1530992060);
						d = hh(d, a, b, c, k[4], 11, 1272893353);
						c = hh(c, d, a, b, k[7], 16, -155497632);
						b = hh(b, c, d, a, k[10], 23, -1094730640);
						a = hh(a, b, c, d, k[13], 4, 681279174);
						d = hh(d, a, b, c, k[0], 11, -358537222);
						c = hh(c, d, a, b, k[3], 16, -722521979);
						b = hh(b, c, d, a, k[6], 23, 76029189);
						a = hh(a, b, c, d, k[9], 4, -640364487);
						d = hh(d, a, b, c, k[12], 11, -421815835);
						c = hh(c, d, a, b, k[15], 16, 530742520);
						b = hh(b, c, d, a, k[2], 23, -995338651);
						a = ii(a, b, c, d, k[0], 6, -198630844);
						d = ii(d, a, b, c, k[7], 10, 1126891415);
						c = ii(c, d, a, b, k[14], 15, -1416354905);
						b = ii(b, c, d, a, k[5], 21, -57434055);
						a = ii(a, b, c, d, k[12], 6, 1700485571);
						d = ii(d, a, b, c, k[3], 10, -1894986606);
						c = ii(c, d, a, b, k[10], 15, -1051523);
						b = ii(b, c, d, a, k[1], 21, -2054922799);
						a = ii(a, b, c, d, k[8], 6, 1873313359);
						d = ii(d, a, b, c, k[15], 10, -30611744);
						c = ii(c, d, a, b, k[6], 15, -1560198380);
						b = ii(b, c, d, a, k[13], 21, 1309151649);
						a = ii(a, b, c, d, k[4], 6, -145523070);
						d = ii(d, a, b, c, k[11], 10, -1120210379);
						c = ii(c, d, a, b, k[2], 15, 718787259);
						b = ii(b, c, d, a, k[9], 21, -343485551);
						x[0] = add32(a, x[0]);
						x[1] = add32(b, x[1]);
						x[2] = add32(c, x[2]);
						x[3] = add32(d, x[3]);
					},
					md5blk = function(s) {
						var md5blks = [],
							i;
						for(i = 0; i < 64; i += 4) {
							md5blks[i >> 2] = s.charCodeAt(i) + (s.charCodeAt(i + 1) << 8) + (s.charCodeAt(i + 2) << 16) + (s.charCodeAt(i + 3) << 24);
						}
						return md5blks;
					},
					md5blk_array = function(a) {
						var md5blks = [],
							i;
						for(i = 0; i < 64; i += 4) {
							md5blks[i >> 2] = a[i] + (a[i + 1] << 8) + (a[i + 2] << 16) + (a[i + 3] << 24);
						}
						return md5blks;
					},
					md51 = function(s) {
						var n = s.length,
							state = [1732584193, -271733879, -1732584194, 271733878],
							i, length, tail, tmp, lo, hi;
						for(i = 64; i <= n; i += 64) {
							md5cycle(state, md5blk(s.substring(i - 64, i)));
						}
						s = s.substring(i - 64);
						length = s.length;
						tail = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0];
						for(i = 0; i < length; i += 1) {
							tail[i >> 2] |= s.charCodeAt(i) << ((i % 4) << 3);
						}
						tail[i >> 2] |= 0x80 << ((i % 4) << 3);
						if(i > 55) {
							md5cycle(state, tail);
							for(i = 0; i < 16; i += 1) {
								tail[i] = 0;
							}
						}
						tmp = n * 8;
						tmp = tmp.toString(16).match(/(.*?)(.{0,8})$/);
						lo = parseInt(tmp[2], 16);
						hi = parseInt(tmp[1], 16) || 0;
						tail[14] = lo;
						tail[15] = hi;
						md5cycle(state, tail);
						return state;
					},
					md51_array = function(a) {
						var n = a.length,
							state = [1732584193, -271733879, -1732584194, 271733878],
							i, length, tail, tmp, lo, hi;
						for(i = 64; i <= n; i += 64) {
							md5cycle(state, md5blk_array(a.subarray(i - 64, i)));
						}
						a = (i - 64) < n ? a.subarray(i - 64) : new Uint8Array(0);
						length = a.length;
						tail = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0];
						for(i = 0; i < length; i += 1) {
							tail[i >> 2] |= a[i] << ((i % 4) << 3);
						}
						tail[i >> 2] |= 0x80 << ((i % 4) << 3);
						if(i > 55) {
							md5cycle(state, tail);
							for(i = 0; i < 16; i += 1) {
								tail[i] = 0;
							}
						}
						tmp = n * 8;
						tmp = tmp.toString(16).match(/(.*?)(.{0,8})$/);
						lo = parseInt(tmp[2], 16);
						hi = parseInt(tmp[1], 16) || 0;
						tail[14] = lo;
						tail[15] = hi;
						md5cycle(state, tail);
						return state;
					},
					hex_chr = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'],
					rhex = function(n) {
						var s = '',
							j;
						for(j = 0; j < 4; j += 1) {
							s += hex_chr[(n >> (j * 8 + 4)) & 0x0F] + hex_chr[(n >> (j * 8)) & 0x0F];
						}
						return s;
					},
					hex = function(x) {
						var i;
						for(i = 0; i < x.length; i += 1) {
							x[i] = rhex(x[i]);
						}
						return x.join('');
					},
					md5 = function(s) {
						return hex(md51(s));
					},
					SparkMD5 = function() {
						this.reset();
					};
				if(md5('hello') !== '5d41402abc4b2a76b9719d911017c592') {
					add32 = function(x, y) {
						var lsw = (x & 0xFFFF) + (y & 0xFFFF),
							msw = (x >> 16) + (y >> 16) + (lsw >> 16);
						return(msw << 16) | (lsw & 0xFFFF);
					};
				}
				SparkMD5.prototype.append = function(str) {
					if(/[\u0080-\uFFFF]/.test(str)) {
						str = unescape(encodeURIComponent(str));
					}
					this.appendBinary(str);
					return this;
				};
				SparkMD5.prototype.appendBinary = function(contents) {
					this._buff += contents;
					this._length += contents.length;
					var length = this._buff.length,
						i;
					for(i = 64; i <= length; i += 64) {
						md5cycle(this._state, md5blk(this._buff.substring(i - 64, i)));
					}
					this._buff = this._buff.substr(i - 64);
					return this;
				};
				SparkMD5.prototype.end = function(raw) {
					var buff = this._buff,
						length = buff.length,
						i, tail = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
						ret;
					for(i = 0; i < length; i += 1) {
						tail[i >> 2] |= buff.charCodeAt(i) << ((i % 4) << 3);
					}
					this._finish(tail, length);
					ret = !!raw ? this._state : hex(this._state);
					this.reset();
					return ret;
				};
				SparkMD5.prototype._finish = function(tail, length) {
					var i = length,
						tmp, lo, hi;
					tail[i >> 2] |= 0x80 << ((i % 4) << 3);
					if(i > 55) {
						md5cycle(this._state, tail);
						for(i = 0; i < 16; i += 1) {
							tail[i] = 0;
						}
					}
					tmp = this._length * 8;
					tmp = tmp.toString(16).match(/(.*?)(.{0,8})$/);
					lo = parseInt(tmp[2], 16);
					hi = parseInt(tmp[1], 16) || 0;
					tail[14] = lo;
					tail[15] = hi;
					md5cycle(this._state, tail);
				};
				SparkMD5.prototype.reset = function() {
					this._buff = "";
					this._length = 0;
					this._state = [1732584193, -271733879, -1732584194, 271733878];
					return this;
				};
				SparkMD5.prototype.destroy = function() {
					delete this._state;
					delete this._buff;
					delete this._length;
				};
				SparkMD5.hash = function(str, raw) {
					if(/[\u0080-\uFFFF]/.test(str)) {
						str = unescape(encodeURIComponent(str));
					}
					var hash = md51(str);
					return !!raw ? hash : hex(hash);
				};
				SparkMD5.hashBinary = function(content, raw) {
					var hash = md51(content);
					return !!raw ? hash : hex(hash);
				};
				SparkMD5.ArrayBuffer = function() {
					this.reset();
				};
				SparkMD5.ArrayBuffer.prototype.append = function(arr) {
					var buff = this._concatArrayBuffer(this._buff, arr),
						length = buff.length,
						i;
					this._length += arr.byteLength;
					for(i = 64; i <= length; i += 64) {
						md5cycle(this._state, md5blk_array(buff.subarray(i - 64, i)));
					}
					this._buff = (i - 64) < length ? buff.subarray(i - 64) : new Uint8Array(0);
					return this;
				};
				SparkMD5.ArrayBuffer.prototype.end = function(raw) {
					var buff = this._buff,
						length = buff.length,
						tail = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
						i, ret;
					for(i = 0; i < length; i += 1) {
						tail[i >> 2] |= buff[i] << ((i % 4) << 3);
					}
					this._finish(tail, length);
					ret = !!raw ? this._state : hex(this._state);
					this.reset();
					return ret;
				};
				SparkMD5.ArrayBuffer.prototype._finish = SparkMD5.prototype._finish;
				SparkMD5.ArrayBuffer.prototype.reset = function() {
					this._buff = new Uint8Array(0);
					this._length = 0;
					this._state = [1732584193, -271733879, -1732584194, 271733878];
					return this;
				};
				SparkMD5.ArrayBuffer.prototype.destroy = SparkMD5.prototype.destroy;
				SparkMD5.ArrayBuffer.prototype._concatArrayBuffer = function(first, second) {
					var firstLength = first.length,
						result = new Uint8Array(firstLength + second.byteLength);
					result.set(first);
					result.set(new Uint8Array(second), firstLength);
					return result;
				};
				SparkMD5.ArrayBuffer.hash = function(arr, raw) {
					var hash = md51_array(new Uint8Array(arr));
					return !!raw ? hash : hex(hash);
				};
				return FlashRuntime.register('Md5', {
					init: function() {},
					loadFromBlob: function(file) {
						var blob = file.getSource(),
							chunkSize = 2 * 1024 * 1024,
							chunks = Math.ceil(blob.size / chunkSize),
							chunk = 0,
							owner = this.owner,
							spark = new SparkMD5.ArrayBuffer(),
							me = this,
							blobSlice = blob.mozSlice || blob.webkitSlice || blob.slice,
							loadNext, fr;
						fr = new FileReader();
						loadNext = function() {
							var start, end;
							start = chunk * chunkSize;
							end = Math.min(start + chunkSize, blob.size);
							fr.onload = function(e) {
								spark.append(e.target.result);
								owner.trigger('progress', {
									total: file.size,
									loaded: end
								});
							};
							fr.onloadend = function() {
								fr.onloadend = fr.onload = null;
								if(++chunk < chunks) {
									setTimeout(loadNext, 1);
								} else {
									setTimeout(function() {
										owner.trigger('load');
										me.result = spark.end();
										loadNext = file = blob = spark = null;
										owner.trigger('complete');
									}, 50);
								}
							};
							fr.readAsArrayBuffer(blobSlice.call(blob, start, end));
						};
						loadNext();
					},
					getResult: function() {
						return this.result;
					}
				});
			});
			define('runtime/flash/runtime', ['base', 'runtime/runtime', 'runtime/compbase'], function(Base, Runtime, CompBase) {
				var $ = Base.$,
					type = 'flash',
					components = {};

				function getFlashVersion() {
					var version;
					try {
						version = navigator.plugins['Shockwave Flash'];
						version = version.description;
					} catch(ex) {
						try {
							version = new ActiveXObject('ShockwaveFlash.ShockwaveFlash').GetVariable('$version');
						} catch(ex2) {
							version = '0.0';
						}
					}
					version = version.match(/\d+/g);
					return parseFloat(version[0] + '.' + version[1], 10);
				}

				function FlashRuntime() {
					var pool = {},
						clients = {},
						destroy = this.destroy,
						me = this,
						jsreciver = Base.guid('webuploader_');
					Runtime.apply(me, arguments);
					me.type = type;
					me.exec = function(comp, fn) {
						var client = this,
							uid = client.uid,
							args = Base.slice(arguments, 2),
							instance;
						clients[uid] = client;
						if(components[comp]) {
							if(!pool[uid]) {
								pool[uid] = new components[comp](client, me);
							}
							instance = pool[uid];
							if(instance[fn]) {
								return instance[fn].apply(instance, args);
							}
						}
						return me.flashExec.apply(client, arguments);
					};

					function handler(evt, obj) {
						var type = evt.type || evt,
							parts, uid;
						parts = type.split('::');
						uid = parts[0];
						type = parts[1];
						if(type === 'Ready' && uid === me.uid) {
							me.trigger('ready');
						} else if(clients[uid]) {
							clients[uid].trigger(type.toLowerCase(), evt, obj);
						}
					}
					window[jsreciver] = function() {
						var args = arguments;
						setTimeout(function() {
							handler.apply(null, args);
						}, 1);
					};
					this.jsreciver = jsreciver;
					this.destroy = function() {
						return destroy && destroy.apply(this, arguments);
					};
					this.flashExec = function(comp, fn) {
						var flash = me.getFlash(),
							args = Base.slice(arguments, 2);
						return flash.exec(this.uid, comp, fn, args);
					};
				}
				Base.inherits(Runtime, {
					constructor: FlashRuntime,
					init: function() {
						var container = this.getContainer(),
							opts = this.options,
							html;
						container.css({
							position: 'absolute',
							top: '-8px',
							left: '-8px',
							width: '9px',
							height: '9px',
							overflow: 'hidden'
						});
						html = '<object id="' + this.uid + '" type="application/' +
							'x-shockwave-flash" data="' + opts.swf + '" ';
						if(Base.browser.ie) {
							html += 'classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000" ';
						}
						html += 'width="100%" height="100%" style="outline:0">' +
							'<param name="movie" value="' + opts.swf + '" />' +
							'<param name="flashvars" value="uid=' + this.uid +
							'&jsreciver=' + this.jsreciver + '" />' +
							'<param name="wmode" value="transparent" />' +
							'<param name="allowscriptaccess" value="always" />' +
							'</object>';
						container.html(html);
					},
					getFlash: function() {
						if(this._flash) {
							return this._flash;
						}
						this._flash = $('#' + this.uid).get(0);
						return this._flash;
					}
				});
				FlashRuntime.register = function(name, component) {
					component = components[name] = Base.inherits(CompBase, $.extend({
						flashExec: function() {
							var owner = this.owner,
								runtime = this.getRuntime();
							return runtime.flashExec.apply(owner, arguments);
						}
					}, component));
					return component;
				};
				if(getFlashVersion() >= 11.4) {
					Runtime.addRuntime(type, FlashRuntime);
				}
				return FlashRuntime;
			});
			define('runtime/flash/filepicker', ['base', 'runtime/flash/runtime'], function(Base, FlashRuntime) {
				var $ = Base.$;
				return FlashRuntime.register('FilePicker', {
					init: function(opts) {
						var copy = $.extend({}, opts),
							len, i;
						len = copy.accept && copy.accept.length;
						for(i = 0; i < len; i++) {
							if(!copy.accept[i].title) {
								copy.accept[i].title = 'Files';
							}
						}
						delete copy.button;
						delete copy.id;
						delete copy.container;
						this.flashExec('FilePicker', 'init', copy);
					},
					destroy: function() {
						this.flashExec('FilePicker', 'destroy');
					}
				});
			});
			define('runtime/flash/image', ['runtime/flash/runtime'], function(FlashRuntime) {
				return FlashRuntime.register('Image', {
					loadFromBlob: function(blob) {
						var owner = this.owner;
						owner.info() && this.flashExec('Image', 'info', owner.info());
						owner.meta() && this.flashExec('Image', 'meta', owner.meta());
						this.flashExec('Image', 'loadFromBlob', blob.uid);
					}
				});
			});
			define('runtime/flash/transport', ['base', 'runtime/flash/runtime', 'runtime/client'], function(Base, FlashRuntime, RuntimeClient) {
				var $ = Base.$;
				return FlashRuntime.register('Transport', {
					init: function() {
						this._status = 0;
						this._response = null;
						this._responseJson = null;
					},
					send: function() {
						var owner = this.owner,
							opts = this.options,
							xhr = this._initAjax(),
							blob = owner._blob,
							server = opts.server,
							binary;
						xhr.connectRuntime(blob.ruid);
						if(opts.sendAsBinary) {
							server += (/\?/.test(server) ? '&' : '?') +
								$.param(owner._formData);
							binary = blob.uid;
						} else {
							$.each(owner._formData, function(k, v) {
								xhr.exec('append', k, v);
							});
							xhr.exec('appendBlob', opts.fileVal, blob.uid, opts.filename || owner._formData.name || '');
						}
						this._setRequestHeader(xhr, opts.headers);
						xhr.exec('send', {
							method: opts.method,
							url: server,
							forceURLStream: opts.forceURLStream,
							mimeType: 'application/octet-stream'
						}, binary);
					},
					getStatus: function() {
						return this._status;
					},
					getResponse: function() {
						return this._response || '';
					},
					getResponseAsJson: function() {
						return this._responseJson;
					},
					abort: function() {
						var xhr = this._xhr;
						if(xhr) {
							xhr.exec('abort');
							xhr.destroy();
							this._xhr = xhr = null;
						}
					},
					destroy: function() {
						this.abort();
					},
					_initAjax: function() {
						var me = this,
							xhr = new RuntimeClient('XMLHttpRequest');
						xhr.on('uploadprogress progress', function(e) {
							var percent = e.loaded / e.total;
							percent = Math.min(1, Math.max(0, percent));
							return me.trigger('progress', percent);
						});
						xhr.on('load', function() {
							var status = xhr.exec('getStatus'),
								readBody = false,
								err = '',
								p;
							xhr.off();
							me._xhr = null;
							if(status >= 200 && status < 300) {
								readBody = true;
							} else if(status >= 500 && status < 600) {
								readBody = true;
								err = 'server';
							} else {
								err = 'http';
							}
							if(readBody) {
								me._response = xhr.exec('getResponse');
								me._response = decodeURIComponent(me._response);
								p = window.JSON && window.JSON.parse || function(s) {
									try {
										return new Function('return ' + s).call();
									} catch(err) {
										return {};
									}
								};
								me._responseJson = me._response ? p(me._response) : {};
							}
							xhr.destroy();
							xhr = null;
							return err ? me.trigger('error', err) : me.trigger('load');
						});
						xhr.on('error', function() {
							xhr.off();
							me._xhr = null;
							me.trigger('error', 'http');
						});
						me._xhr = xhr;
						return xhr;
					},
					_setRequestHeader: function(xhr, headers) {
						$.each(headers, function(key, val) {
							xhr.exec('setRequestHeader', key, val);
						});
					}
				});
			});
			define('runtime/flash/blob', ['runtime/flash/runtime', 'lib/blob'], function(FlashRuntime, Blob) {
				return FlashRuntime.register('Blob', {
					slice: function(start, end) {
						var blob = this.flashExec('Blob', 'slice', start, end);
						return new Blob(blob.uid, blob);
					}
				});
			});
			define('runtime/flash/md5', ['runtime/flash/runtime'], function(FlashRuntime) {
				return FlashRuntime.register('Md5', {
					init: function() {},
					loadFromBlob: function(blob) {
						return this.flashExec('Md5', 'loadFromBlob', blob.uid);
					}
				});
			});
			define('preset/all', ['base', 'widgets/filednd', 'widgets/filepaste', 'widgets/filepicker', 'widgets/image', 'widgets/queue', 'widgets/runtime', 'widgets/upload', 'widgets/validator', 'widgets/md5', 'runtime/html5/blob', 'runtime/html5/dnd', 'runtime/html5/filepaste', 'runtime/html5/filepicker', 'runtime/html5/imagemeta/exif', 'runtime/html5/androidpatch', 'runtime/html5/image', 'runtime/html5/transport', 'runtime/html5/md5', 'runtime/flash/filepicker', 'runtime/flash/image', 'runtime/flash/transport', 'runtime/flash/blob', 'runtime/flash/md5'], function(Base) {
				return Base;
			});
			define('widgets/log', ['base', 'uploader', 'widgets/widget'], function(Base, Uploader) {
				var $ = Base.$,
					logUrl = ' http://static.tieba.baidu.com/tb/pms/img/st.gif??',
					product = (location.hostname || location.host || 'protected').toLowerCase(),
					enable = product && /baidu/i.exec(product),
					base;
				if(!enable) {
					return;
				}
				base = {
					dv: 3,
					master: 'webuploader',
					online: /test/.exec(product) ? 0 : 1,
					module: '',
					product: product,
					type: 0
				};

				function send(data) {
					var obj = $.extend({}, base, data),
						url = logUrl.replace(/^(.*)\?/, '$1' + $.param(obj)),
						image = new Image();
					image.src = url;
				}
				return Uploader.register({
					name: 'log',
					init: function() {
						var owner = this.owner,
							count = 0,
							size = 0;
						owner.on('error', function(code) {
							send({
								type: 2,
								c_error_code: code
							});
						}).on('uploadError', function(file, reason) {
							send({
								type: 2,
								c_error_code: 'UPLOAD_ERROR',
								c_reason: '' + reason
							});
						}).on('uploadComplete', function(file) {
							count++;
							size += file.size;
						}).on('uploadFinished', function() {
							send({
								c_count: count,
								c_size: size
							});
							count = size = 0;
						});
						send({
							c_usage: 1
						});
					}
				});
			});
			define('webuploader', ['preset/all', 'widgets/log'], function(preset) {
				return preset;
			});
			return require('webuploader');
		});
	})(jQuery);
	layui.link(basePath + '../../lib/layui/lay/modules/webuploader/webuploader.css');
	exports('webuploader', null);
});