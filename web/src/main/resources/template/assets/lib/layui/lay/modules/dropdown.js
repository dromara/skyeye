/**

 @Name：layui.dropdown 下拉按钮
 @Author：First
 @License：MIT

 */
layui.define('jquery', function (exports) {
  var $ = layui.$,
      device = layui.device(),
      MOD_NAME = 'dropdown',
      CLASS_NAME = '.layui-dropdown-menu',
      //事件类型，默认为'click'（移动端则为'click'）
      event = (device.android || device.ios) ? 'click' : 'click',
      //当前Dropdown对象
      that;

  Dropdown = function () {
    //当前实例
    this.inst = null;
    this.currReElem = null;
  };

  //隐藏
  var __hideDropdown = function (e) {
    if (isClosable(e.target)
        && isClosable(e.target.parentElement)
        && isClosable(e.target.parentElement.parentElement)) {
      that.hide();
    }
  };

  //判断dropdown是否可以隐藏
  var isClosable = function (elem) {
    return elem
        && elem.className.indexOf('layui-btn-dropdown') == -1
        && elem.className.indexOf('layui-dropdown-menu') == -1;
  };

  //修正显示位置
  Dropdown.prototype.hide = function () {
    if (that && that.inst && that.inst.is(':visible')) {
      that.inst.css('display', 'none');
      $('body').off(event, __hideDropdown);
    }
  };

  //渲染
  Dropdown.prototype.render = function () {
    that = this;
    $('.layui-btn-dropdown').each(function (index, elem) {
      var reElem = $(elem);
      reElem.data('id', 'dropdown-' + index);
      event = (device.android || device.ios) ? 'click' : 'click';

      reElem[event](function () {
        if (!that.inst//第一次显示
            || that.currReElem.data('id') != reElem.data('id')//切换到其他dropdown
            || (that.currReElem.data('id') == reElem.data('id') && !that.inst.is(':visible'))) {//重新移动到当前dropdown
          //隐藏
          that.hide();
          //这里暂时采用fixed定位
          var dropElem = reElem.find(CLASS_NAME),
              left = reElem.offset().left - $(window).scrollLeft(),
              top = reElem.offset().top + reElem.height() - $(window).scrollTop() + 10,
              containerWidth = reElem.width(),
              dropWidth = dropElem.width(),
              offsetRight = left + containerWidth,
              overflow = (left + dropWidth) > $(window).width(),
              css = {'display': 'block', 'position': 'fixed', 'top': top + 'px', 'left': left + 'px'};

          overflow && $.extend(true, css, {'left': (offsetRight - dropWidth) + 'px'});
          //显示
          dropElem.css(css).on('click', 'li', function () {
            dropElem.css('display', 'none');
          });

          that.inst = dropElem;
          that.currReElem = reElem;
          $('body').on(event, __hideDropdown);
        }
      });
    });
  };

  //自动完成渲染
  var dropdown = new Dropdown();
  dropdown.render();

  $(window).scroll(function () {
    dropdown.hide();
  });

  exports(MOD_NAME, dropdown);
})