
   jQuery.fn.extend({
    uploadPreview: function (opts) {
        var _self = this,
            _this = $(this);
        opts = jQuery.extend({
            Img: "ImgPr",
            Width: 160,
            Height: 160,
            ImgType: ["gif", "jpeg", "jpg", "bmp", "png"],
            Callback: function () {}
        }, opts || {});
        _self.getObjectURL = function (file) {
            var url = null;
            if (window.createObjectURL != undefined) {
                url = window.createObjectURL(file)
            } else if (window.URL != undefined) {
                url = window.URL.createObjectURL(file)
            } else if (window.webkitURL != undefined) {
                url = window.webkitURL.createObjectURL(file)
            }
            return url
        };
        _this.change(function () {
            if (this.value) {
                 /*是IE确定版本*/

                 if(navigator.appName =="Microsoft Internet Explorer"){
                    try {
                        $("#" + opts.Img).attr('src', _self.getObjectURL(this.files[0]))
                    } catch (e) {
                        var src = "";
                        var obj = $("#" + opts.Img);
                        var div = obj.parent("div")[0];
                       /* _self.select();
                        if (top != self) {
                            window.parent.document.body.focus()
                        } else {
                            _self.blur()
                        }*/
                        var fileId="#"+ this.id;
                        $(fileId)[0].select();
                        $(fileId)[0].blur();
                        src = document.selection.createRange().text;
                        document.selection.empty();
                        obj.hide();
                      
                        if(obj.parent=='a'){

                        }
                        obj.parent("div").css({
                            'filter': 'progid:DXImageTransform.Microsoft.AlphaImageLoader(sizingMethod=scale)',
                            'width': opts.Width + 'px',
                            'height': opts.Height + 'px'
                        });
                     div.filters.item("DXImageTransform.Microsoft.AlphaImageLoader").src = src ;
                     $("#" + opts.Img).attr('src',src);
                     $("#" + opts.Img).attr('flag','IE8');
                    }
                } else {
                    $("#" + opts.Img).attr('src', _self.getObjectURL(this.files[0]))
                }
                
                opts.Callback()
            }
        })
    }
});
   jQuery.extend({
    //展示图片
     picShow:function(path,imageName){
      var path,trim_Version; 
      var imageNameStr="#"+imageName;
      var version=navigator.appVersion.split(";"); 
      if(navigator.appName =="Microsoft Internet Explorer"){
                 try {
                     (imageNameStr).attr("src",path);
                 } 
                 catch (e)
                  {
                    document.getElementById(imageName).src="";
                    document.getElementById(imageName).style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(enabled='true',sizingMethod='scale',src=\"" + path + "\")";//使用滤镜效果
                    $(imageNameStr).attr("src",path);
                   } 
                }
                 else {
                   $(imageNameStr).attr("src",path);
               }
           },
      //f返回默认图片
     staticPic:function(imageName,contantDiv){
             var imageNameStr="#"+imageName;
               if(judegeBroser()=='IE8')
                {  
                if($(imageNameStr).attr('srcT')){
            	    document.getElementById(imageName).src="";
            	     $(imageNameStr).parent("div").removeAttr('style');	
                   $(imageNameStr).removeAttr('display');
                   $(imageNameStr).parent("div").css({
                      'width': 160+ 'px',
                      'height': 160 + 'px',
                   });
                   $(imageNameStr).attr("src",$(imageNameStr).attr('srcT'));
                   var srcT= $(imageNameStr).attr('srcT');   
                   document.getElementById(contantDiv).style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(enabled='true',sizingMethod='scale',src=\"" +srcT+ "\")";
              }else{
                  $(imageNameStr).parent("div").removeAttr('style');
                  $(imageNameStr).removeAttr('display');
                  $(imageNameStr).parent("div").css({
                         'width': 160+ 'px',
                         'height': 160 + 'px',
                     });
              }
              $(imageNameStr).show();
                 } 
               else{
                if($(imageNameStr).attr('srcT')){
                     $(imageNameStr).attr("src",$(imageNameStr).attr('srcT'));
                }else{
                    $(imageNameStr).attr("src","../../assets/img/pictop.png");
                }
               }
             //IE8
               function judegeBroser(){
               try {
                  var trim_Version=navigator.appVersion.split(";")[1].replace(/[ ]/g,"");
                  if(navigator.appName=="Microsoft Internet Explorer" && (trim_Version=="MSIE8.0"||trim_Version=="MSIE9.0")){
                      return "IE8";
                  }}
                  catch (e) {
                      return "!IE8"
                  }
                }
           },
     //f返回默认图片
   staticPic1:function(imageName,contantDiv){
           var imageNameStr="#"+imageName;
             if(judegeBroser()=='IE8')
              {  
              if($(imageNameStr).attr('srcT')){
          	    document.getElementById(imageName).src="";
          	     $(imageNameStr).parent("div").removeAttr('style');	
                 $(imageNameStr).removeAttr('display');
                 $(imageNameStr).parent("div").css({
                    'width': 160+ 'px',
                    'height': 160 + 'px',
                 });
                 $(imageNameStr).attr("src",$(imageNameStr).attr('srcT'));
                 var srcT= $(imageNameStr).attr('srcT');   
                 document.getElementById(contantDiv).style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(enabled='true',sizingMethod='scale',src=\"" +srcT+ "\")";
            }else{
                $(imageNameStr).parent("div").removeAttr('style');
                $(imageNameStr).removeAttr('display');
                $(imageNameStr).parent("div").css({
                       'width': 160+ 'px',
                       'height': 160 + 'px',
                   });
            }
            $(imageNameStr).show();
               } 
             else{
              if($(imageNameStr).attr('srcT')){
                   $(imageNameStr).attr("src",$(imageNameStr).attr('srcT'));
              }else{
                  $(imageNameStr).attr("src","../../assets/img/uploadPic.png");
              }
             }
           //IE8
             function judegeBroser(){
             try {
                var trim_Version=navigator.appVersion.split(";")[1].replace(/[ ]/g,"");
                if(navigator.appName=="Microsoft Internet Explorer" && (trim_Version=="MSIE8.0"||trim_Version=="MSIE9.0")){
                    return "IE8";
                }}
                catch (e) {
                    return "!IE8"
                }
              }
         },
         //f返回默认图片
     staticPic2:function(imageName,contantDiv){
         var imageNameStr="#"+imageName;
           if(judegeBroser()=='IE8')
            {  
            if($(imageNameStr).attr('srcT')){
        	    document.getElementById(imageName).src="";
        	     $(imageNameStr).parent("div").removeAttr('style');	
               $(imageNameStr).removeAttr('display');
               $(imageNameStr).parent("div").css({
                  'width': 160+ 'px',
                  'height': 160 + 'px',
               });
               $(imageNameStr).attr("src",$(imageNameStr).attr('srcT'));
               var srcT= $(imageNameStr).attr('srcT');   
               document.getElementById(contantDiv).style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(enabled='true',sizingMethod='scale',src=\"" +srcT+ "\")";
          }else{
              $(imageNameStr).parent("div").removeAttr('style');
              $(imageNameStr).removeAttr('display');
              $(imageNameStr).parent("div").css({
                     'width': 160+ 'px',
                     'height': 160 + 'px',
                 });
          }
          $(imageNameStr).show();
             } 
           else{
            if($(imageNameStr).attr('srcT')){
                 $(imageNameStr).attr("src",$(imageNameStr).attr('srcT'));
            }else{
                $(imageNameStr).attr("src","../../assets/img/uploadPicSquare.png");
            }
           }
         //IE8
           function judegeBroser(){
           try {
              var trim_Version=navigator.appVersion.split(";")[1].replace(/[ ]/g,"");
              if(navigator.appName=="Microsoft Internet Explorer" && (trim_Version=="MSIE8.0"||trim_Version=="MSIE9.0")){
                  return "IE8";
              }}
              catch (e) {
                  return "!IE8"
              }
            }
       }
   });
