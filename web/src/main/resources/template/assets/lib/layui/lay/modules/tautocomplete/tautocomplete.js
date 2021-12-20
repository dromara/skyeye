
layui.define(["jquery"], function(exports) {
	var jQuery = layui.jquery;
		(function ($) {
		    "use strict";
		
		    $.fn.tautocomplete = function (options, callback) {
		
		        // default parameters
		        var settings = $.extend({
		            width: "300px",
		            columns: [],
		            onchange: null,
		            norecord: "暂无对应数据",
		            dataproperty: null,
		            regex: "^[a-zA-Z0-9\b]+$",
		            data: null,
		            placeholder: null,
		            theme: "default"
		        }, options);
		
		        var cssClass = [["default", "adropdown"], ["classic", "aclassic"], ["white", "awhite"]];
		
		        // set theme
		        cssClass.filter(function (v, i) {
		            if (v[0] == settings.theme) {
		                settings.theme = v[1];
		                return;
		            }
		        });
		        
		        // initialize DOM elements
		        var el = {
		            ddDiv: $("<div>", { class: settings.theme }),
		            ddTable: $("<table></table>", { style: "width:" + settings.width }),
		            ddTableCaption: $("<caption>" + settings.norecord + "</caption>"),
		            ddTextbox: $("<input type='text'>")
		        };
		
		        var keys = {
		            UP: 38,
		            DOWN: 40,
		            ENTER: 13,
		            TAB: 9
		        };
		
		        var errors = {
		            columnNA: "Error: Columns Not Defined",
		            dataNA: "Error: Data Not Available"
		        };
		        
		        // plugin properties
		        var tautocomplete = {
		            id: function () {
		                return el.ddTextbox.data("id");
		            },
		            text: function () {
		                return el.ddTextbox.data("text");
		            },
		            searchdata: function () {
		                return el.ddTextbox.val();
		            },
		            isNull: function () {
		                if (el.ddTextbox.data("id") == "")
		                    return true;
		                else
		                    return false;
		            }
		        };
		
		        // delay function which listens to the textbox entry
		        var delay = (function () {
		            var timer = 0;
		            return function (callsback, ms) {
		                clearTimeout(timer);
		                timer = setTimeout(callsback, ms);
		            };
		        })();
		        var focused = false;
		        // check if the textbox is focused.
		        if (this.is(':focus')) {
		            focused = true;
		        }
		
		        // get number of columns
		        var cols = settings.columns.length;
		        var orginalTextBox = this;
		        // wrap the div for style
		        this.wrap("<div class='acontainer'></div>");
		
		        // create a textbox for input
		        this.after(el.ddTextbox);
		        el.ddTextbox.attr("autocomplete", "off");
		        el.ddTextbox.css("width", this.width + "px"); 
		        el.ddTextbox.css("font-size", this.css("font-size"));
		        el.ddTextbox.attr("placeholder", settings.placeholder);
		        el.ddTextbox.attr("class", "layui-input layui-input-search");
		
		        // check for mandatory parameters
		        if (settings.columns == "" || settings.columns == null) {
		            el.ddTextbox.attr("placeholder", errors.columnNA);
		        } else if (settings.data == "" || settings.data == null) {
		            el.ddTextbox.attr("placeholder", errors.dataNA);
		        }
		
		        // append data property
		        if (settings.dataproperty != null) {
		            for (var key in settings.dataproperty) {
		                el.ddTextbox.attr("data-" + key, settings.dataproperty[key]);
		            }
		        }
		
		        // append div after the textbox
		        this.after(el.ddDiv);
		
		        // hide the current text box (used for stroing the values)
		        this.hide();
		
		        // append table after the new textbox
		        el.ddDiv.append(el.ddTable);
		        el.ddTable.attr("cellspacing", "0");
		
		        // append table caption
		        el.ddTable.append(el.ddTableCaption);
		
		        // 创建表格表头的行
		        var header = "<thead><tr>";
		        for (var i = 0; i <= cols - 1; i++) {
		            header = header + "<th>" + settings.columns[i].title + "</th>"
		        }
		        header = header + "</thead></tr>"
		        el.ddTable.append(header);
		
		        // assign data fields to the textbox, helpful in case of .net postbacks
		        {
		            var id = "", text = "";
		            if (this.val() != "") {
		                var val = this.val().split("#$#");
		                id = val[0];
		                text = val[1];
		            }
		            el.ddTextbox.attr("data-id", id);
		            el.ddTextbox.attr("data-text", text);
		            el.ddTextbox.val(text);
		        }
		
		        if (focused) {
		            el.ddTextbox.focus();
		        }
		
		        // event handlers
		        // autocomplete key press
		        el.ddTextbox.keyup(function (e) {
		            //return if up/down/return key
	                if (el.ddTextbox.val() == "") {
	                    hideDropDown();
	                    return;
	                }
	                // hide no record found message
	                el.ddTableCaption.hide();
	                el.ddTextbox.addClass("loading");
	                if ($.isFunction(settings.data)) {
	                    var data = settings.data.call(this);
	                    jsonParser(data, settings.columns);
	                } else {
	                    // default function
	                }
		        });
		        el.ddTextbox.change(function (e) {
		            //return if up/down/return key
	                if (el.ddTextbox.val() == "") {
	                    hideDropDown();
	                    return;
	                }
	                // hide no record found message
	                el.ddTableCaption.hide();
	                el.ddTextbox.addClass("loading");
	                if ($.isFunction(settings.data)) {
	                    var data = settings.data.call(this);
	                    jsonParser(data, settings.columns);
	                } else {
	                    // default function
	                }
		        });
		
		        // do not allow special characters
		        el.ddTextbox.keypress(function (event) {
		            var regex = new RegExp(settings.regex);
		            var key = String.fromCharCode(!event.charCode ? event.which : event.charCode);
		            if (!regex.test(key)) {
		                event.preventDefault();
		                return false;
		            }
		        });
				
		        // row click event
		        el.ddTable.delegate("tr", "mousedown", function () {
		            el.ddTable.find(".selected").removeClass("selected");
		            $(this).addClass("selected");
		            select();
		        });
		
		        // textbox blur event
		        el.ddTextbox.focusout(function () {
		            hideDropDown();
		            // clear if the text value is invalid 
		            if ($(this).val() != $(this).data("text")) {
		                var change = true;
		                if ($(this).data("text") == "") {
		                    change = false;
		                }
		                $(this).data("text", "");
		                $(this).data("id", "");
		                $(this).val("");
		                orginalTextBox.val("");
		                if (change) {
		                    onChange();
		                }
		            }
		        });
		
		        function select() {
		            var selected = el.ddTable.find("tbody").find(".selected");
		            el.ddTextbox.data("id", selected.find('td').eq(0).text());
		            el.ddTextbox.data("text", selected.find('td').eq(1).text());
		            el.ddTextbox.val(selected.find('td').eq(1).text());
		            orginalTextBox.val(selected.find('td').eq(0).text() + '#$#' + selected.find('td').eq(1).text());
		            hideDropDown();
		            onChange();
		            el.ddTextbox.focus();
		        }
		
		        function onChange() {
		            // onchange callback function
		            if ($.isFunction(settings.onchange)) {
		                settings.onchange.call(this);
		            } else {
		                // default function for onchange
		            }
		        }
		
		        function hideDropDown() {
		            el.ddTable.hide();
		            el.ddTextbox.removeClass("inputfocus");
		            el.ddDiv.removeClass("highlight");
		            el.ddTableCaption.hide();
		        }
		
		        function showDropDown() {
		            var cssTop = "45px 1px 0px 1px";
		            var cssBottom = "1px 1px " + (el.ddTextbox.height() + 20) + "px 1px";
		            // reset div top, left and margin
		            el.ddDiv.css("top", "0px");
		            el.ddDiv.css("left", "0px");
		            el.ddTable.css("margin", cssTop);
		            el.ddTextbox.addClass("inputfocus");
		            el.ddDiv.addClass("highlight");
		            el.ddTable.show();
		            // adjust div top according to the visibility
		            if (!isDivHeightVisible(el.ddDiv)) {
		                el.ddDiv.css("top", -1 * (el.ddTable.height()) + "px");
		                el.ddTable.css("margin", cssBottom);
		                if (!isDivHeightVisible(el.ddDiv)) {
		                    el.ddDiv.css("top", "0px");
		                    el.ddTable.css("margin", cssTop);
		                    $('html, body').animate({
		                        scrollTop: (el.ddDiv.offset().top - 60)
		                    }, 250);
		                }
		            }
		            // adjust div left according to the visibility
		            if (!isDivWidthVisible(el.ddDiv)) {
		                el.ddDiv.css("left", "-" + (el.ddTable.width() - el.ddTextbox.width() - 20) + "px");
		            }
		        }
		        function jsonParser(jsonData, columns) {
		            try{
		                el.ddTextbox.removeClass("loading");
		                // remove all rows from the table
		                el.ddTable.find("tbody").find("tr").remove();
		                var i = 0;
		                var row = null, cell = null;
		                if (jsonData != null) {
		                    for (i = 0; i < jsonData.length; i++) {
		                        // display only 15 rows of data
		                        if (i >= 15)
		                            continue;
		                        var obj = jsonData[i];
		                        row = "<td>" + obj["id"] + "</td>";
		                        for (var j = 0; j < columns.length; j++) {
		                            // return on column count
	                                cell = obj[columns[j].field];
	                                row = row + "<td>" + cell + "</td>";
		                        }
		                        // append row to the table
		                        el.ddTable.append("<tr>" + row + "</tr>");
		                    }
		                }
		                //debugger;
		                // show no records exists
		                if (i == 0)
		                    el.ddTableCaption.show();
		                // hide first column (ID row)
		                el.ddTable.find('td:nth-child(1)').hide();
		                showDropDown();
		            } catch (e) {
		                alert("Error: " + e);
		            }
		        }
		        return tautocomplete;
		    };
		}(jQuery));
		
		function isDivHeightVisible(elem) {
		    var docViewTop = $(window).scrollTop();
		    var docViewBottom = docViewTop + $(window).height();
		    var elemTop = $(elem).offset().top;
		    var elemBottom = elemTop + $(elem).height();
		    return ((elemBottom >= docViewTop) && (elemTop <= docViewBottom)
		      && (elemBottom <= docViewBottom) && (elemTop >= docViewTop));
		}
		function isDivWidthVisible(elem) {
		    var docViewLeft = $(window).scrollLeft();
		    var docViewRight = docViewLeft + $(window).width();
		    var elemLeft = $(elem).offset().left;
		    var elemRight = elemLeft + $(elem).width();
		    return ((elemRight >= docViewLeft) && (elemLeft <= docViewRight)
		      && (elemRight <= docViewRight) && (elemLeft >= docViewLeft));
		}
		
	layui.link(basePath + '../../lib/layui/lay/modules/tautocomplete/tautocomplete.css');
	exports('tautocomplete', null);
});