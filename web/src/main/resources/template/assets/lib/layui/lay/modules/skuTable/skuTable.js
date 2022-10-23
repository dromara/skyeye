
var normsStock = new Array();

layui.define(['jquery', 'form', 'upload', 'layer', 'sortable'], function (exports) {
    "use strict";
    var $ = layui.jquery,
        form = layui.form,
        upload = layui.upload,
        layer = layui.layer,
        sortable = layui.sortable;

    // 工具类
    class Util {
        static config = {
            shade: [0.02, '#000'],
            time: 2000
        };

        static msg = {
            // 成功消息
            success: function (msg, callback = null) {
                return layer.msg(msg, {
                    icon: 1,
                    shade: Util.config.shade,
                    scrollbar: false,
                    time: Util.config.time,
                    shadeClose: true
                }, callback);
            },
            // 失败消息
            error: function (msg, callback = null) {
                return layer.msg(msg, {
                    icon: 2,
                    shade: Util.config.shade,
                    scrollbar: false,
                    time: Util.config.time,
                    shadeClose: true
                }, callback);
            },
            // 警告消息框
            alert: function (msg, callback = null) {
                return layer.alert(msg, {end: callback, scrollbar: false});
            },
            // 对话框
            confirm: function (msg, ok, no) {
                var index = layer.confirm(msg, {title: '操作确认', btn: ['确认', '取消']}, function () {
                    typeof ok === 'function' && ok.call(this);
                }, function () {
                    typeof no === 'function' && no.call(this);
                    Util.msg.close(index);
                });
                return index;
            },
            // 消息提示
            tips: function (msg, callback = null) {
                return layer.msg(msg, {
                    time: Util.config.time,
                    shade: Util.config.shade,
                    end: callback,
                    shadeClose: true
                });
            },
            // 输入框
            prompt: function (option, callback = null) {
                return layer.prompt(option, callback);
            },
            // 关闭消息框
            close: function (index) {
                return layer.close(index);
            }
        };
    }

    class SkuTable {
        data = {
            specData: [],
            skuData: {},
            specDataDelete: false,
            otherMationData: {}
        };

        selTemplate = getFileContent('tpl/template/select-option.tpl');
        unitGroupList = [];

        options = {
			boxId: 'skyeye-weizhiqiang',
            otherElemId: 'wzq-other-elem',
            otherMationElemId: 'wzq-other-mation',
            specTableElemId: 'fairy-spec-table',
            skuTableElemId: 'fairy-sku-table',
            skuNameType: 0,
            skuNameDelimiter: '-',
            multipleSkuTableConfig: {
                thead: [
                    {title: '图片', icon: ''},
                    {title: '销售价(元)', icon: 'layui-icon-cols'},
                    {title: '市场价(元)', icon: 'layui-icon-cols'},
                    {title: '成本价(元)', icon: 'layui-icon-cols'},
                    {title: '库存', icon: 'layui-icon-cols'},
                    {title: '状态', icon: ''},
                ],
                tbody: [
                    {type: 'image', field: 'picture', value: '', verify: '', reqtext: ''},
                    {type: 'input', field: 'price', value: '0', verify: 'required|number', reqtext: '销售价不能为空'},
                    {type: 'input', field: 'market_price', value: '0', verify: 'required|number', reqtext: '市场价不能为空'},
                    {type: 'input', field: 'cost_price', value: '0', verify: 'required|number', reqtext: '成本价不能为空'},
                    {type: 'input', field: 'stock', value: '0', verify: 'required|number', reqtext: '库存不能为空'},
                    {
                        type: 'select',
                        field: 'status',
                        option: [{key: '启用', value: '1'}, {key: '禁用', value: '0'}],
                        verify: '',
                        reqtext: ''
                    },
                ]
            },
            rowspan: false,
            skuIcon: 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAOCAYAAAAfSC3RAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyZpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNi1jMDY3IDc5LjE1Nzc0NywgMjAxNS8wMy8zMC0yMzo0MDo0MiAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIDIwMTUgKFdpbmRvd3MpIiB4bXBNTTpJbnN0YW5jZUlEPSJ4bXAuaWlkOjczN0RFNzU1MTk1RTExRTlBMEQ5OEEwMEM5NDNFOEE4IiB4bXBNTTpEb2N1bWVudElEPSJ4bXAuZGlkOjczN0RFNzU2MTk1RTExRTlBMEQ5OEEwMEM5NDNFOEE4Ij4gPHhtcE1NOkRlcml2ZWRGcm9tIHN0UmVmOmluc3RhbmNlSUQ9InhtcC5paWQ6NzM3REU3NTMxOTVFMTFFOUEwRDk4QTAwQzk0M0U4QTgiIHN0UmVmOmRvY3VtZW50SUQ9InhtcC5kaWQ6NzM3REU3NTQxOTVFMTFFOUEwRDk4QTAwQzk0M0U4QTgiLz4gPC9yZGY6RGVzY3JpcHRpb24+IDwvcmRmOlJERj4gPC94OnhtcG1ldGE+IDw/eHBhY2tldCBlbmQ9InIiPz5NHmJUAAAA+0lEQVR42pySPwsBYRzH7zk3KIP34CVIKSOrELLJdpuymyzew90kIwMZvACDsCldWZTFn5WQpPN5rlPXlXJ39en7/J57fn+fR9i2rYT5NNM0B2gC3n/6qHBQDMOwZNYg4LOQ3vcQld40/w6lC13Xbd/eHElC3G1JqL4DFWSNprz7BMpAFJ6YkW+jThaosuxAD/rY6R9lCmeq8IAmtKBA1A1OW9YjtIS9QvPYRZkcXo43EzqjF/mDQ5an7ALShTFk4eQOsgFTWeoNKl4nt68J0oYc1LHLbmtDp1IyLgPe4QCuMkIsyAWSuYbs5HD29DML8OTkHR9F2Ef+EWAAdwmkvBAtw94AAAAASUVORK5CYII=',
			specData: [],
			skuData: {},
            otherMationData: {}
        };

		// 构造函数
        constructor(options) {
            this.options = $.extend(this.options, options);
			this.data.specData = isNull(this.options.specData) ? [] : this.options.specData;
			this.data.skuData = isNull(this.options.skuData) ? {} : this.options.skuData;
            this.data.otherMationData = isNull(this.options.otherMationData) ? {} : this.options.otherMationData;
			// 编辑到时候在这里做分支
			this.css();
			this.render();
			this.listen();
        }

        css() {
            $('head').append(`<style>
                #${this.options.specTableElemId} tbody tr {
                  cursor: move;
                  transition:unset;
                  -webkit-transition:unset;
                }
                #${this.options.specTableElemId} tbody tr td:last-child > i.layui-icon-delete {
                  margin-right:15px;
                  margin-left:-12px;
                  vertical-align: top;
                }
                #${this.options.specTableElemId} tbody tr td div.fairy-spec-value-create,
                #${this.options.specTableElemId} tfoot tr td div.fairy-spec-create {
                  display: inline-block;
                  color: #1E9FFF;
                  vertical-align: middle;
                  padding: 0px 6px;
                }
                #${this.options.specTableElemId} tfoot tr td div.layui-form-checkbox {
                  margin-top: 0;
                }
                #${this.options.specTableElemId} tfoot tr td div.layui-form-checkbox > span{
                  color: #1E9FFF;
                }
                #${this.options.specTableElemId} img.fairy-sku-img,
                #${this.options.skuTableElemId} tbody tr td > img.fairy-sku-img{
                  width: 16px;
                  height: 16px;
                  padding: 6px;
                  border: 1px solid #eceef1;
                  vertical-align: middle;
                }
                #${this.options.specTableElemId} img.fairy-sku-img,
                #${this.options.specTableElemId} tbody tr td > i.layui-icon-delete,
                #${this.options.specTableElemId} tbody tr td div.fairy-spec-value-create,
                #${this.options.specTableElemId} tfoot tr td div.fairy-spec-create,
                #${this.options.skuTableElemId} thead tr th > i.layui-icon,
                #${this.options.skuTableElemId} tbody tr td > img.fairy-sku-img {
                  cursor: pointer;
                }
                </style>`
            );
        }

        render() {
			this.initHtml();
            this.resetRender();

            if ($.isEmptyObject(this.data.otherMationData) || this.data.otherMationData.unit == 1) {
                // 单规格
                $(`#${this.options.specTableElemId}`).html(this.loadSimpleSkuConfig());
                this.loadUploadPicLogo();
            } else {
                // 多规格
                this.renderOtherMation();
                if ($.isEmptyObject(this.data.otherMationData)) {
                    this.renderSpecTable();
                    this.renderMultipleSkuTable();
                }
            }
        }
		
		// 初始化html界面
		initHtml() {
			var _html = `<div id="${this.options.otherElemId}">` +
                    `<div class="layui-form-item layui-col-xs12">
                        <label class="layui-form-label">规格类型<i class="red">*</i></label>
                        <div class="layui-input-block winui-radio">
                            <input type="radio" name="unit" value="1" title="单规格" lay-filter="wzq-unit" checked="checked"/>
                            <input type="radio" name="unit" value="2" title="多规格" lay-filter="wzq-unit"/>
                        </div>
                    </div>` +
                `</div>` +
                `<div id="${this.options.otherMationElemId}"></div>` +
                `<div id="${this.options.specTableElemId}"></div>` +
				`<div id="${this.options.skuTableElemId}"></div>`;
			$(`#${this.options.boxId}`).html(_html);
            if (!$.isEmptyObject(this.data.otherMationData)) {
                $("input:radio[name=unit][value=" + this.data.otherMationData.unit + "]").attr("checked", true);
            }
            form.render();
		}

        resetRender(targets = []) {
            if (targets.length) {
                targets.forEach((item) => {
                    $(`#${item}`).html(``);
                })
            } else {
                $(`#${this.options.otherMationElemId}`).html(``);
                $(`#${this.options.specTableElemId}`).html(``);
                $(`#${this.options.skuTableElemId}`).html(``);
            }
        }

        listen() {
            var that = this;

            /**
             * 监听所选规格值的变化
             */
            form.on('checkbox(fairy-spec-filter)', function (data) {
                var specData = [];
                $.each($(`#${that.options.specTableElemId} tbody tr`), function () {
                    var options = [], value = [];
                    $.each($(this).find('input[type=checkbox]'), function () {
                        $(this).is(':checked') && value.push($(this).val());
                        options.push({rowNum: $(this).val(), title: $(this).attr('title')});
                    });
                    specData.push({
                        rowNum: $(this).find('td').eq(0).data('num'),
                        title: $(this).find('td').eq(0).text(),
                        options: options,
                        value: value
                    });
                });
                that.data.specData = specData;
                that.data.skuData = $.extend(that.data.skuData, that.getFormSkuData());
                that.resetRender([that.options.skuTableElemId]);
                that.renderMultipleSkuTable();
            });

            /**
             * 监听批量赋值
             */
            $(document).on('click', `#${this.options.skuTableElemId} thead tr th i`, function () {
                var thisI = this;
                Util.msg.prompt({title: $(thisI).parent().text().trim() + '批量赋值'}, function (value, index, elem) {
                    $.each($(`#${that.options.skuTableElemId} tbody tr`), function () {
                        var index = that.options.rowspan ?
                            $(thisI).parent().index() - ($(`#${that.options.skuTableElemId} thead th.fairy-spec-name`).length - $(this).children('td.fairy-spec-value').length) :
                            $(thisI).parent().index();
                        $(this).find('td').eq(index).children('input').val(value);
                    });
                    Util.msg.close(index);
                });
            });

            /**
             * 监听添加规格
             */
			var normsNameIndex = that.data.specData.length > 0 ? Math.max.apply(Math, that.data.specData.map(function(i) {return i.rowNum})) + 1 : 1;
            $(document).on('click', `#${this.options.specTableElemId} .fairy-spec-create`, function () {
                layer.prompt({title: '规格'}, function (value, index, elem) {
                    var check = getInPoingArr(that.data.specData, "title", value);
                    if (isNull(check)) {
                        that.data.specData.push({rowNum: normsNameIndex, title: value, options: [], value: []});
                        that.resetRender([that.options.specTableElemId]);
                        that.renderSpecTable();
                        Util.msg.close(index);
                        normsNameIndex++;
                    } else {
                        winui.window.msg('该规格名已存在，请更换。', {icon: 2, time: 2000});
                    }
                });
            });

            /**
             * 监听添加规格值
             */
			var _options = [];
			that.data.specData.map(function(i) {_options = _options.concat(i.options)});
			var normsValueIndex = _options.length > 0 ? Math.max.apply(Math, _options.map(function(i) {return i.rowNum})) + 1 : 1;
            $(document).on('click', `#${this.options.specTableElemId} .fairy-spec-value-create`, function () {
                var rowNum = $(this).parent('td').prev().data('num');
                layer.prompt({title: '规格值'}, function (value, index, elem) {
					that.data.specData.forEach(function (v, i) {
						if (v.rowNum == rowNum) {
                            var check = getInPoingArr(v.options, "title", value);
                            if (isNull(check)) {
                                v.options.push({rowNum: normsValueIndex, title: value});
                            } else {
                                winui.window.msg('该规格值已存在，请更换。', {icon: 2, time: 2000});
                                return false;
                            }
						}
					});
					that.resetRender([that.options.specTableElemId]);
					that.renderSpecTable();
                    Util.msg.close(index);
					normsValueIndex++;
                });
            });

            /**
             * 监听删除规格/规格值
             */
            $(document).on('click', `#${this.options.specTableElemId} i.layui-icon-delete`, function () {
                if (typeof $(this).attr('data-spec-num') !== "undefined") {
                    var specNum = $(this).attr('data-spec-num');
					that.deleteSpec(specNum);
                } else if (typeof $(this).attr('data-spec-value-num') !== "undefined") {
                    var specValueNum = $(this).attr('data-spec-value-num');
					that.deleteSpecValue(specValueNum)
                }
            });

            /**
             * 初始库存变化事件
             */
            $(document).on('click', '.stockMore', function () {
                var _this = $(this);
                // 获取行号
                var _data = _this.attr("stock");

                normsStock = isNull(_data) ? [] : [].concat(JSON.parse(_data));
                _openNewWindows({
                    url: "../../tpl/materialnormstock/materialnormstock.html",
                    title: "库存信息",
                    pageId: "materialnormstock",
                    area: ['70vw', '70vh'],
                    callBack: function (refreshCode) {
                        var str = "";
                        $.each(normsStock, function(i, item) {
                            str += '<br><span class="layui-badge layui-bg-blue" style="height: 25px !important; line-height: 25px !important; margin: 5px 0px;">' + item.depotName + '【' + item.stock + '】</span>';
                        });
                        _this.attr("stock", JSON.stringify(normsStock));
                        _this.parent().html(_this.prop("outerHTML") + str);
                    }});
            });

            /**
             * 监听规格表是否开启删除
             */
            form.on('checkbox(fairy-spec-delete-filter)', function (data) {
                that.data.specDataDelete = data.elem.checked;
                if (data.elem.checked) {
                    $(`#${that.options.specTableElemId} tbody tr i.layui-icon-delete`).removeClass('layui-hide');
                } else {
                    $(`#${that.options.specTableElemId} tbody tr i.layui-icon-delete`).addClass('layui-hide')
                }
            });

            /**
             * 规格变化类型
             */
            form.on('radio(wzq-unit)', function (data) {
                that.resetRender();
                var val = data.value;
                if (val == 1) {
                    $(`#${that.options.specTableElemId}`).html(that.loadSimpleSkuConfig());
                    that.loadUploadPicLogo();
                } else {
                    that.renderOtherMation();
                    that.renderSpecTable();
                    that.renderMultipleSkuTable();
                }
            });

            /**
             * 计量单位变化
             */
            form.on('select(unitGroupId)', function(data) {
                if (isNull(data.value)) {
                    $("#firstInUnit").html("");
                    $("#firstOutUnit").html("");
                    form.render('select');
                } else {
                    $.each(that.unitGroupList, function (i, item) {
                        if (item.id == data.value) {
                            var str = getDataUseHandlebars(that.selTemplate, {rows: item.unitList});
                            $("#firstInUnit").html(str);
                            $("#firstOutUnit").html(str);
                            form.render('select');
                            that.resetRender([that.options.skuTableElemId]);
                            that.renderMultipleSkuTable();
                            return false;
                        }
                    });
                }
            });

            /**
             * 图片移入放大/移出恢复
             */
            var imgLayerIndex = null;
            $(document).on('mouseenter', '.fairy-sku-img', function () {
                imgLayerIndex = layer.tips('<img src="' + $(this).attr('src') + '" style="max-width:200px;"  alt=""/>', this, {
                    tips: [2, 'rgba(41,41,41,.5)'],
                    time: 0
                });
            }).on('mouseleave', '.fairy-sku-img', function () {
                layer.close(imgLayerIndex);
            })
        }

        deleteSpec(specId) {
            this.data.specData.forEach((item, index) => {
                if (item.rowNum == specId) {
                    this.data.specData.splice(index, 1);
                }
            });
            this.resetRender([this.options.specTableElemId, this.options.skuTableElemId]);
            this.renderSpecTable();
            this.renderMultipleSkuTable();
        }

        deleteSpecValue(specValueNum) {
            this.data.specData.forEach((item, index) => {
                item.options.forEach((value, key) => {
                    if (value.rowNum == specValueNum) {
                        if (item.value.includes(specValueNum)) {
                            item.value.splice(item.value.indexOf(specValueNum), 1);
                        }
                        item.options.splice(key, 1);
                    }
                })
            });
            this.resetRender([this.options.specTableElemId, this.options.skuTableElemId]);
            this.renderSpecTable();
            this.renderMultipleSkuTable();
        }

        renderFormItem(label, content, target) {
            var html = '';
			html += '<div class="layui-form-item  layui-col-xs12">';
			html += `<label class="layui-form-label">${label ? label + ':' : ''}</label>`;
			html += '<div class="layui-input-block">';
			html += content;
			html += '</div>';
			html += '</div>';
			$(`#${target}`).html(html);
			form.render();
        }

        /**
         * 加载多规格的其他信息
         */
        renderOtherMation() {
            var str = `<div class="layui-form-item layui-col-xs12">
                        <label class="layui-form-label">计量单位<i class="red">*</i></label>
                            <div class="layui-input-block">
                                <select lay-filter="unitGroupId" lay-search="" id="unitGroupId" win-verify="required"></select>
                            </div>
                        </div>
                       <div class="layui-form-item layui-col-xs6">
                        <label class="layui-form-label">首选入库单位<i class="red">*</i></label>
                        <div class="layui-input-block">
                            <select lay-filter="firstInUnit" lay-search="" id="firstInUnit" win-verify="required"></select>
                        </div>
                       </div>
                       <div class="layui-form-item layui-col-xs6">
                        <label class="layui-form-label">首选出库单位<i class="red">*</i></label>
                        <div class="layui-input-block">
                            <select lay-filter="firstOutUnit" lay-search="" id="firstOutUnit" win-verify="required"></select>
                        </div>
                       </div>`;
            $(`#${this.options.otherMationElemId}`).html(str);
            var that = this;
            AjaxPostUtil.request({url: flowableBasePath + "materialunit006", params: {}, type: 'json', method: "POST", callback: function(json) {
                $(`#unitGroupId`).html(getDataUseHandlebars(that.selTemplate, json));
                that.unitGroupList = json.rows;
            }, async: false});

            if (!$.isEmptyObject(that.data.otherMationData)) {
                $("#unitGroupId").val(that.data.otherMationData.unitGroupId);
                $.each(that.unitGroupList, function (i, item) {
                    if (item.id == that.data.otherMationData.unitGroupId) {
                        var str = getDataUseHandlebars(that.selTemplate, {rows: item.unitList});
                        $("#firstInUnit").html(str);
                        $("#firstInUnit").val(that.data.otherMationData.firstInUnit);
                        $("#firstOutUnit").html(str);
                        $("#firstOutUnit").val(that.data.otherMationData.firstOutUnit);
                        form.render('select');
                        that.renderSpecTable();
                        that.renderMultipleSkuTable();
                        return false;
                    }
                });
            }
        }

        /**
         * 渲染规格表
         */
        renderSpecTable() {
			var tableId = this.options.specTableElemId + '-id';
            var that = this,
                table = `<table class="layui-table" id="${tableId}"><thead><tr><th>规格名</th><th>规格值</th></tr></thead><colgroup><col width="140"></colgroup><tbody>`;
            $.each(this.data.specData, function (index, item) {
                table += `<tr data-num="${item.rowNum}" data-id="${item.rowNum}">`;
                table += `<td data-num="${item.rowNum}">${item.title} <i class="layui-icon layui-icon-delete layui-anim layui-anim-scale ${that.data.specDataDelete ? '' : 'layui-hide'}" data-spec-num="${item.rowNum}"></i></td>`;
                table += '<td>';
                $.each(item.options, function (key, value) {
                    table += `<input type="checkbox" title="${value.title}" lay-filter="fairy-spec-filter" value="${value.rowNum}" ${item.value.includes(value.rowNum) ? 'checked' : ''} /> <i class="layui-icon layui-icon-delete layui-anim layui-anim-scale ${that.data.specDataDelete ? '' : 'layui-hide'}" data-spec-value-num="${value.rowNum}"></i> `;
                });
                table += '<div class="fairy-spec-value-create"><i class="fa fa-plus"></i>规格值</div>';
                table += '</td>';
                table += '</tr>';
            });
            table += '</tbody>';

            table += '<tfoot><tr><td colspan="2" style="line-height: 14px;">';
            table += `<input type="checkbox" title="开启删除" lay-skin="primary" lay-filter="fairy-spec-delete-filter" ${that.data.specDataDelete ? 'checked' : ''}/>`;
			table += `<div class="fairy-spec-create"><i class="fa fa-plus"></i>规格</div>`;
            table += '</td></tr></tfoot>';

            table += '</table>';

            this.renderFormItem('商品规格', table, this.options.specTableElemId);

            /**
             * 拖拽
             */
            var sortableObj = sortable.create($(`#${tableId} tbody`)[0], {
                animation: 1000,
                onEnd: (evt) => {
                    //获取拖动后的排序
                    var sortArr = sortableObj.toArray(),
                        sortSpecData = [];
                    this.data.specData.forEach((item) => {
                        sortSpecData[sortArr.indexOf(String(item.rowNum))] = item;
                    });
                    this.data.specData = sortSpecData;
                    this.resetRender([this.options.skuTableElemId]);
                    this.renderMultipleSkuTable();
                },
            });
        }

        /**
         * 渲染sku表
         */
        renderMultipleSkuTable() {
			var tableId = this.options.skuTableElemId + '-id';
            var that = this, table = `<table class="layui-table" id="${tableId}">`;
			var prependThead = [], prependTbody = [];

            // 加载计量单位作为其中的一个规格
            var unitGroupId = $("#unitGroupId").val();
            if (!isNull(unitGroupId)) {
                var prependTbodyItem = [];
                $.each(that.unitGroupList, function (i, item) {
                    if (item.id == unitGroupId) {
                        $.each(item.unitList, function (j, bean) {
                            prependTbodyItem.push({
                                rowNum: bean.id,
                                title: bean.name
                            });
                        });
                        return false;
                    }
                });
                prependThead.push('计量单位');
                prependTbody.push(prependTbodyItem);
            }

			$.each(that.data.specData, function (index, item) {
				var isShow = item.options.some(function (option, index, array) {
					return item.value.includes(option.rowNum);
				});
				if (isShow) {
					prependThead.push(item.title);
					var prependTbodyItem = [];
					$.each(item.options, function (key, option) {
						if (item.value.includes(option.rowNum)) {
							prependTbodyItem.push({rowNum: option.rowNum, title: option.title});
						}
					});
					prependTbody.push(prependTbodyItem);
				}
			});

			table += '<colgroup>' + '<col width="70">'.repeat(prependThead.length + 1) + '</colgroup>';
			table += '<thead>';
			if (prependThead.length > 0) {
				var theadTr = '<tr>';

				theadTr += prependThead.map(function (t, i, a) {
					return '<th class="fairy-spec-name">' + t + '</th>';
				}).join('');

				this.options.multipleSkuTableConfig.thead.forEach(function (item) {
                    if (isNull(item.width)) {
                        theadTr += '<th>' + item.title + (item.icon ? ' <i class="layui-icon ' + item.icon + '"></i>' : '') + '</th>';
                    } else {
                        theadTr += '<th style="width: ' + item.width + '">' + item.title + (item.icon ? ' <i class="layui-icon ' + item.icon + '"></i>' : '') + '</th>';
                    }
				});
				theadTr += '</tr>';
				table += theadTr;
			} else {
				var theadTr = '<tr>';
				this.options.multipleSkuTableConfig.thead.forEach(function (item) {
                    if (isNull(item.width)) {
                        theadTr += '<th>' + item.title + (item.icon ? ' <i class="layui-icon ' + item.icon + '"></i>' : '') + '</th>';
                    } else {
                        theadTr += '<th style="width: ' + item.width + '">' + item.title + (item.icon ? ' <i class="layui-icon ' + item.icon + '"></i>' : '') + '</th>';
                    }
				});
				theadTr += '</tr>';
				table += theadTr;
			}
			table += '</thead>';

			if (this.options.rowspan) {
				var skuRowspanArr = [];
				prependTbody.forEach(function (v, i, a) {
					var num = 1, index = i;
					while (index < a.length - 1) {
						num *= a[index + 1].length;
						index++;
					}
					skuRowspanArr.push(num);
				});
			}

			var prependTbodyTrs = [];
			if (prependTbody.length > 0) {
				prependTbody.reduce(function (prev, cur, index, array) {
					var tmp = [];
					prev.forEach(function (a) {
						cur.forEach(function (b) {
							tmp.push({rowNum: a.rowNum + that.options.skuNameDelimiter + b.rowNum, title: a.title + that.options.skuNameDelimiter + b.title});
						});
					});
					return tmp;
				}).forEach(function (item, index, array) {
					var tr = `<tr id="${item.rowNum}" row="${index}">`;

					tr += item.title.split(that.options.skuNameDelimiter).map(function (t, i, a) {
						if (that.options.rowspan) {
							if (index % skuRowspanArr[i] === 0 && skuRowspanArr[i] > 1) {
								return '<td class="fairy-spec-value" rowspan="' + skuRowspanArr[i] + '">' + t + '</td>';
							} else if (skuRowspanArr[i] === 1) {
								return '<td class="fairy-spec-value">' + t + '</td>';
							} else {
								return '';
							}
						} else {
							return '<td>' + t + '</td>';
						}
					}).join('');
					tr += that.loadMultipleSkuTableConfig(item, index);
					tr += '</tr>';

					prependTbodyTrs.push(tr);
				});
			} else {
				var tr = '<tr id="0" row="0">';
				tr += that.loadMultipleSkuTableConfig(null, 0);
				tr += '</tr>';
				
				prependTbodyTrs.push(tr);
			}

			table += '<tbody>';
			if (prependTbodyTrs.length > 0) {
				table += prependTbodyTrs.join('');
			}
			table += '</tbody>';


            table += '</table>';

            this.renderFormItem('商品库存', table, this.options.skuTableElemId);
            this.loadUploadPicLogo();
            form.render();
        }

        loadUploadPicLogo() {
            upload.render({
                elem: '.fairy-sku-img',
                url: reqBasePath + 'common003',
                data: {type: 29},
                exts: 'png|jpg|ico|jpeg|gif',
                accept: 'images',
                acceptMime: 'image/*',
                multiple: false,
                done: function (res) {
                    if (res.returnCode === 0) {
                        var url = res.bean.picUrl;
                        $(this.item).attr('src', url).prev().val(url);
                    } else {
                        var msg = res.returnMessage == undefined ? '返回数据格式有误' : res.returnMessage;
                        Util.msg.error(msg);
                    }
                    return false;
                }
            });
        }
		
		loadMultipleSkuTableConfig(item, index) {
			var that = this;
			var tr = '';
			that.options.multipleSkuTableConfig.tbody.forEach(function (c) {
				var key = '';
				if (item != null) {
					key = item.rowNum;
				}
				var type = c.field;
				var value = (!isNull(key) && !isNull(that.data.skuData[key])) ? that.data.skuData[key][type] : c.value;
				var id = c.field + index;
				switch (c.type) {
					case "image":
                        value = isNull(value) ? '' : value;
						tr += '<td><input type="hidden" id="' + id + '" name="' + type + '" value="' + value + '" win-verify="' + c.verify + '" lay-reqtext="' + c.reqtext + '">' +
							'<img class="fairy-sku-img" src="' + value + '"></td>';
						break;
					case "select":
						tr += '<td><select id="' + id + '" name="' + type + '" win-verify="' + c.verify + '" lay-reqtext="' + c.reqtext + '">';
						c.option.forEach(function (o) {
                            if (!isNull(value) && o.id == value) {
                                tr += '<option value="' + o.id + '" selected="selected">' + o.name + '</option>';
                            } else {
                                tr += '<option value="' + o.id + '">' + o.name + '</option>';
                            }
						});
						tr += '</select></td>';
						break;
                    case "btn":
                        value = isNull(value) ? [] : JSON.parse(value);
                        tr += "<td><button type='button' class='layui-btn layui-btn-primary layui-btn-xs stockMore' stock='" + JSON.stringify(value) + "' id='" + id + "'>库存信息</button>";
                        $.each(value, function(i, item) {
                            tr += '<br><span class="layui-badge layui-bg-blue" style="height: 25px !important; line-height: 25px !important; margin: 5px 0px;">' + item.depotName + '【' + item.stock + '】</span>';
                        });
                        tr += '</td>';
                        break;
					case "input":
					default:
						tr += '<td><input type="text" id="' + id + '" name="' + type + '" value="' + value + '" class="layui-input" win-verify="' + c.verify + '" lay-reqtext="' + c.reqtext + '"></td>';
						break;
				}
			});
			return tr;
		}

        loadSimpleSkuConfig() {
            var that = this;
            var unitNameValue = '';
            if (!$.isEmptyObject(that.data.otherMationData)) {
                unitNameValue = that.data.otherMationData.unitName;
            }
            var div = `<div class="layui-form-item layui-col-xs6">
                        <label class="layui-form-label">计量单位<i class="red">*</i></label>
                        <div class="layui-input-block">
                            <input type="text" id="unitName" name="unitName" win-verify="required" placeholder="请输入计量单位" class="layui-input" maxlength="50" value="${unitNameValue}"/>
                        </div>
                    </div>`;
            that.options.multipleSkuTableConfig.tbody.forEach(function (item, index) {
                var type = item.field;
                var value = (!isNull(that.data.skuData["simpleNorms"])) ? that.data.skuData["simpleNorms"][type] : item.value;
                var id = item.field;
                var title = that.options.multipleSkuTableConfig.thead[index].title;
                div += `<div class="layui-form-item layui-col-xs6">` +
                    `<label class="layui-form-label">${title}<i class="red">*</i></label>` +
                    `<div class="layui-input-block">`;

                switch (item.type) {
                    case "image":
                        value = isNull(value) ? '' : value;
                        div += '<input type="hidden" id="' + id + '" name="' + type + '" value="' + value + '" win-verify="' + item.verify + '" lay-reqtext="' + item.reqtext + '">' +
                            '<img class="fairy-sku-img skyeye-img" src="' + value + '">';
                        break;
                    case "select":
                        div += '<select id="' + id + '" name="' + type + '" win-verify="' + item.verify + '" lay-reqtext="' + item.reqtext + '">';
                        item.option.forEach(function (o) {
                            if (!isNull(value) && o.id == value) {
                                div += '<option value="' + o.id + '" selected="selected">' + o.name + '</option>';
                            } else {
                                div += '<option value="' + o.id + '">' + o.name + '</option>';
                            }
                        });
                        div += '</select>';
                        break;
                    case "btn":
                        value = isNull(value) ? [] : JSON.parse(value);
                        div += "<button type='button' class='layui-btn layui-btn-primary layui-btn-xs stockMore' stock='" + JSON.stringify(value) + "' id='" + id + "'>库存信息</button>";
                        $.each(value, function(i, item) {
                            div += '<br><span class="layui-badge layui-bg-blue" style="height: 25px !important; line-height: 25px !important; margin: 5px 0px;">' + item.depotName + '【' + item.stock + '】</span>';
                        });
                        break;
                    case "input":
                    default:
                        div += '<input type="text" id="' + id + '" name="' + type + '" value="' + value + '" class="layui-input" win-verify="' + item.verify + '" lay-reqtext="' + item.reqtext + '">';
                        break;
                }
                div += '</div></div>';
            });
            return div;
        }

        getFormSkuData() {
            var skuData = {};
			var that = this;
			var tableId = this.options.skuTableElemId + '-id';
            if ($("input:radio[name='unit']:checked").val() == 1) {
                // 单规格
                var params = {orderBy: 1};
                that.options.multipleSkuTableConfig.tbody.forEach(function (c) {
                    var type = c.field;
                    switch (c.type) {
                        case "image":
                            params[type] = $(`#${type}`).val();
                            break;
                        case "select":
                            params[type] = $(`#${type}`).val();
                            break;
                        case "btn":
                            params[type] = $(`#${type}`).attr("stock");
                            break;
                        case "input":
                        default:
                            params[type] = $(`#${type}`).val();
                            break;
                    }
                });
                skuData["simpleNorms"] = params;
            } else {
                $.each($(`#${tableId} tbody tr`), function (index, item) {
                    var tr = $(item);
                    var id = tr.attr("id");
                    var rowNum = tr.attr("row");
                    var params = {orderBy: index + 1};
                    that.options.multipleSkuTableConfig.tbody.forEach(function (c) {
                        var type = c.field;
                        var tdId = type + rowNum;
                        switch (c.type) {
                            case "image":
                                params[type] = $(`#${tdId}`).val();
                                break;
                            case "select":
                                params[type] = $(`#${tdId}`).val();
                                break;
                            case "btn":
                                params[type] = $(`#${tdId}`).attr("stock");
                                break;
                            case "input":
                            default:
                                params[type] = $(`#${tdId}`).val();
                                break;
                        }
                    });
                    skuData[id] = params;
                });
            }
            return skuData;
        }

        getFormSkuDataList() {
            var data = this.getFormSkuData();
            var list = [];
            $.each(data, function (key, value) {
                value["tableNum"] = key;
                list.push(value);
            });
            return list;
        }
		
		getFormSpecData() {
			return this.data.specData;
		}
    }

    exports("skuTable", {
        render: function (options) {
            return new SkuTable(options);
        }
    })
});
