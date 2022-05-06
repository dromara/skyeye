// 获取今天是本月的几号
function getTodayDay(){
    var today = new Date();
    var todayDay = today.getDate();
    return todayDay;
}

// 获取本月一号的日期
function getOneYMDFormatDate(){
    var date = new Date;
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    month = (month < 10 ? "0" + month : month);
    return year.toString() + "-" + month.toString() + "-" + "01";
}

// 获取上个月一号的日期
function getTOneYMDFormatDate(){
    var date = new Date;
    var year = date.getFullYear();
    var month = date.getMonth();
    month = (month < 10 ? "0" + month : month);
    return year.toString() + "-" + month.toString() + "-" + "01";
}

// 获取前一天的时间
function getYesterdayYMDFormatDate(){
    var myDate = new Date();
    var lw = new Date(myDate - 1000 * 60 * 60 * 24 * 1);
    var lastY = lw.getFullYear();
    var lastM = lw.getMonth() + 1;
    var lastD = lw.getDate();
    return lastY + "-" + (lastM < 10 ? "0" + lastM : lastM) + "-" + (lastD < 10 ? "0" + lastD : lastD);
}

// 获取本月日期
function getOneYMFormatDate(){
    var date = new Date;
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    month = (month < 10 ? "0" + month : month);
    return year.toString() + "-" + month.toString();
}

// 获取指定Date的日期
function getPointYMFormatDate(date){
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    month = (month < 10 ? "0" + month : month);
    return year.toString() + "-" + month.toString();
}

/**
 * 获取指定日期是第几周
 *
 * @param sdate 日期格式yyyy-mm-dd
 * @returns {number}
 */
function weekofyear(sdate) {
    var d = new Date(sdate);
    var myYear = d.getFullYear();
    var firstDate = new Date(myYear + "-01-01");
    var dayofyear = 0;
    for (var i = 0; i < d.getMonth(); i++) {
        switch (i) {
            case 0:
            case 2:
            case 4:
            case 6:
            case 7:
            case 9:
                dayofyear += 31;
                break;
            case 1:
                if (isLeapYear(d)) {
                    dayofyear += 29;
                }
                else {
                    dayofyear += 28;
                }
                break;
            case 3:
            case 5:
            case 8:
            case 10:
                dayofyear += 30;
                break;
        }
    }
    dayofyear += d.getDate() + 1;
    var week = firstDate.getDay();
    var dayNum = dayofyear - (7 - week);
    var weekNum = 1;
    weekNum = weekNum + (dayNum / 7);
    if (dayNum % 7 != 0)
        weekNum = weekNum + 1;
    return parseInt(weekNum);
}

function isLeapYear(date) {
    return (0 == date.getFullYear() % 4 && ((date.getFullYear() % 100 != 0) || (date.getFullYear() % 400 == 0)));
}

/**
 * 获取指定日期是周几
 *
 * @param date 日期格式yyyy-mm-dd
 * @returns {number}
 */
function weekDay(date){
    var _date = new Date(date);
    var num = _date.getDay();
    if(num == 0){
        return 7;
    }
    return num;
}

/**
 * 计算时间差（相差分钟）
 *
 * @param startTime 开始时间，格式为HH:mm:ss
 * @param endTime 结束时间，格式为HH:mm:ss
 * @returns {number}
 */
function timeDifference(startTime, endTime){
    var start1 = startTime.split(":");
    var startAll = parseInt(start1[0] * 60) + parseInt(start1[1]);
    var end1 = endTime.split(":");
    var endAll = parseInt(end1[0] * 60) + parseInt(end1[1]);
    return endAll - startAll;
}

/**
 * 获取两个时间段重叠的时间段,参数格式为HH:mm:ss
 * @param startTime 开始时间1
 * @param endTime 结束时间1
 * @param restStartTime 开始时间2
 * @param restEndTime 结束时间2
 */
function getOverlapTime(startTime, endTime, restStartTime, restEndTime){
    var result = [];
    // 开始时间以大的为准
    if(compare_HHmmss(startTime, restStartTime)){
        result.push(startTime);
    }else{
        result.push(restStartTime);
    }
    // 结束时间以小的为准
    if(compare_HHmmss(restEndTime, endTime)){
        result.push(endTime);
    }else{
        result.push(restEndTime);
    }
    return result;
}

/**
 * 时间戳格式化函数
 * @param  {string} format    格式
 * @param  {int}    timestamp 要格式化的时间 默认为当前时间
 * @return {string}           格式化的时间字符串
 */
function date(format, timestamp){
    var a, jsdate=((timestamp) ? new Date(timestamp*1000) : new Date());
    var pad = function(n, c){
        if((n = n + "").length < c){
            return new Array(++c - n.length).join("0") + n;
        } else {
            return n;
        }
    };
    var txt_weekdays = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
    var txt_ordin = {1:"st", 2:"nd", 3:"rd", 21:"st", 22:"nd", 23:"rd", 31:"st"};
    var txt_months = ["", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
    var f = {
        d: function(){return pad(f.j(), 2)},
        D: function(){return f.l().substr(0,3)},
        j: function(){return jsdate.getDate()},
        l: function(){return txt_weekdays[f.w()]},
        N: function(){return f.w() + 1},
        S: function(){return txt_ordin[f.j()] ? txt_ordin[f.j()] : 'th'},
        w: function(){return jsdate.getDay()},
        z: function(){return (jsdate - new Date(jsdate.getFullYear() + "/1/1")) / 864e5 >> 0},
        W: function(){
            var a = f.z(), b = 364 + f.L() - a;
            var nd2, nd = (new Date(jsdate.getFullYear() + "/1/1").getDay() || 7) - 1;
            if(b <= 2 && ((jsdate.getDay() || 7) - 1) <= 2 - b){
                return 1;
            } else{
                if(a <= 2 && nd >= 4 && a >= (6 - nd)){
                    nd2 = new Date(jsdate.getFullYear() - 1 + "/12/31");
                    return date("W", Math.round(nd2.getTime()/1000));
                } else{
                    return (1 + (nd <= 3 ? ((a + nd) / 7) : (a - (7 - nd)) / 7) >> 0);
                }
            }
        },
        F: function(){return txt_months[f.n()]},
        m: function(){return pad(f.n(), 2)},
        M: function(){return f.F().substr(0,3)},
        n: function(){return jsdate.getMonth() + 1},
        t: function(){
            var n;
            if( (n = jsdate.getMonth() + 1) == 2 ){
                return 28 + f.L();
            } else{
                if( n & 1 && n < 8 || !(n & 1) && n > 7 ){
                    return 31;
                } else{
                    return 30;
                }
            }
        },
        L: function(){var y = f.Y();return (!(y & 3) && (y % 1e2 || !(y % 4e2))) ? 1 : 0},
        Y: function(){return jsdate.getFullYear()},
        y: function(){return (jsdate.getFullYear() + "").slice(2)},
        a: function(){return jsdate.getHours() > 11 ? "pm" : "am"},
        A: function(){return f.a().toUpperCase()},
        B: function(){
            var off = (jsdate.getTimezoneOffset() + 60)*60;
            var theSeconds = (jsdate.getHours() * 3600) + (jsdate.getMinutes() * 60) + jsdate.getSeconds() + off;
            var beat = Math.floor(theSeconds/86.4);
            if (beat > 1000) beat -= 1000;
            if (beat < 0) beat += 1000;
            if ((String(beat)).length == 1) beat = "00"+beat;
            if ((String(beat)).length == 2) beat = "0"+beat;
            return beat;
        },
        g: function(){return jsdate.getHours() % 12 || 12},
        G: function(){return jsdate.getHours()},
        h: function(){return pad(f.g(), 2)},
        H: function(){return pad(jsdate.getHours(), 2)},
        i: function(){return pad(jsdate.getMinutes(), 2)},
        s: function(){return pad(jsdate.getSeconds(), 2)},
        O: function(){
            var t = pad(Math.abs(jsdate.getTimezoneOffset()/60*100), 4);
            if (jsdate.getTimezoneOffset() > 0) t = "-" + t; else t = "+" + t;
            return t;
        },
        P: function(){var O = f.O();return (O.substr(0, 3) + ":" + O.substr(3, 2))},
        c: function(){return f.Y() + "-" + f.m() + "-" + f.d() + "T" + f.h() + ":" + f.i() + ":" + f.s() + f.P()},
        U: function(){return Math.round(jsdate.getTime()/1000)}
    };

    return format.replace(/[\\]?([a-zA-Z])/g, function(t, s){
        if( t!=s ){
            ret = s;
        } else if( f[s] ){
            ret = f[s]();
        } else{
            ret = s;
        }
        return ret;
    });
}

/**
 * 获取当前时间
 * @returns {String}
 */
function getFormatDate(){
    var nowDate = new Date();
    var year = nowDate.getFullYear();
    var month = nowDate.getMonth() + 1 < 10 ? "0" + (nowDate.getMonth() + 1) : nowDate.getMonth() + 1;
    var date = nowDate.getDate() < 10 ? "0" + nowDate.getDate() : nowDate.getDate();
    var hour = nowDate.getHours()< 10 ? "0" + nowDate.getHours() : nowDate.getHours();
    var minute = nowDate.getMinutes()< 10 ? "0" + nowDate.getMinutes() : nowDate.getMinutes();
    var second = nowDate.getSeconds()< 10 ? "0" + nowDate.getSeconds() : nowDate.getSeconds();
    return year + "-" + month + "-" + date + " " + hour + ":" + minute + ":" + second;
}

/**
 * 获取当前的年月日
 * @returns {String}
 */
function getYMDFormatDate(){
    var nowDate = new Date();
    var year = nowDate.getFullYear();
    var month = nowDate.getMonth() + 1 < 10 ? "0" + (nowDate.getMonth() + 1) : nowDate.getMonth() + 1;
    var date = nowDate.getDate() < 10 ? "0" + nowDate.getDate() : nowDate.getDate();
    return year + "-" + month + "-" + date;
}

/**
 * 获取当前的时分秒
 * @returns {String}
 */
function getHMSFormatDate(){
    var nowDate = new Date();
    var hour = nowDate.getHours()< 10 ? "0" + nowDate.getHours() : nowDate.getHours();
    var minute = nowDate.getMinutes()< 10 ? "0" + nowDate.getMinutes() : nowDate.getMinutes();
    var second = nowDate.getSeconds()< 10 ? "0" + nowDate.getSeconds() : nowDate.getSeconds();
    return hour + ":" + minute + ":" + second;
}

/**
 * 获取当前时间30天之前的日期
 * @returns
 */
function getThirdDayToDate(){
    var myDate = new Date();
    //获取三十天前日期
    var lw = new Date(myDate - 1000 * 60 * 60 * 24 * 30);//最后一个数字30可改，30天的意思
    var lastY = lw.getFullYear();
    var lastM = lw.getMonth() + 1;
    var lastD = lw.getDate();
    return lastY + "-" + (lastM < 10 ? "0" + lastM : lastM) + "-" + (lastD < 10 ? "0" + lastD : lastD);//三十天之前日期
}

/**
 * 比较时间大小-时分秒
 * @param a
 * @param b
 */
function compare_hms(a, b){
    var c = new Date(a);
    var d = new Date(b);
    var i = c.getHours() * 60 * 60 + c.getMinutes() * 60 + c.getSeconds();
    var n = d.getHours() * 60 * 60 + d.getMinutes() * 60 + d.getSeconds();
    if(i > n){
        return true;
    }else if(i < n){
        return false;
    }else{
        return true;
    }
}

/**
 * 比较时间大小-时分秒，格式为HH:mm:ss,参数a大于参数b返回true
 * @param a
 * @param b
 */
function compare_HHmmss(a, b){
    var array1 = a.split(":");
    var total1 = array1[0] * 3600 + array1[1] * 60 + array1[2];
    var array2 = b.split(":");
    var total2 = array2[0] * 3600 + array2[1] * 60 + array2[2];
    return total1 - total2 > 0 ? true : false;
}

/**
 * 年月日时分秒转时分
 * @param a
 * @returns {String}
 */
function hms2hm(a){
    var d = new Date(Date.parse(a.replace(/-/g, "/")));
    var i = d.getHours() + ":" + d.getMinutes();
    return i;
}

/**
 * 根据年月日获取周几
 * @param a
 * @returns {String}
 */
function getMyDay(date){
    var week;
    if(date.getDay()==0) week="周日"
    if(date.getDay()==1) week="周一"
    if(date.getDay()==2) week="周二"
    if(date.getDay()==3) week="周三"
    if(date.getDay()==4) week="周四"
    if(date.getDay()==5) week="周五"
    if(date.getDay()==6) week="周六"
    return week;
}

/**
 * 获取今天是周几
 * @returns {String}
 */
function getThisWeekDay(){
    var date = new Date();
    if(date.getDay() == 0){
        return 7
    }
    return date.getDay();
}

/*
 *   功能:实现VBScript的DateAdd功能.
 *   参数:interval,字符串表达式，表示要添加的时间间隔.
 *   参数:number,数值表达式，表示要添加的时间间隔的个数.
 *   参数:date,时间对象.
 *   返回:新的时间对象.
 *   var now = new Date();
 *   var newDate = DateAdd( "d", 5, now);
 *---------------   DateAdd(interval,number,date)   -----------------
 */
function DateAdd(interval, number, date) {
    switch (interval) {
        case "y": {
            date.setFullYear(date.getFullYear() + number);
            return date;
            break;
        }
        case "q": {
            date.setMonth(date.getMonth() + number * 3);
            return date;
            break;
        }
        case "m": {
            date.setMonth(date.getMonth() + number);
            return date;
            break;
        }
        case "-m": {
            date.setMonth(date.getMonth() - number);
            return date;
            break;
        }
        case "w": {
            date.setDate(date.getDate() + number * 7);
            return date;
            break;
        }
        case "d": {
            date.setDate(date.getDate() + number);
            return date;
            break;
        }
        case "h": {
            date.setHours(date.getHours() + number);
            return date;
            break;
        }
        case "m": {
            date.setMinutes(date.getMinutes() + number);
            return date;
            break;
        }
        case "s": {
            date.setSeconds(date.getSeconds() + number);
            return date;
            break;
        }
        default: {
            date.setDate(date.getDate() + number);
            return date;
            break;
        }
    }
}

Date.prototype.format = function(format) {
    /*
     * 使用例子:format="yyyy-MM-dd hh:mm:ss";
     */
    var o = {
        "M+": this.getMonth() + 1, // month
        "d+": this.getDate(), // day
        "h+": this.getHours(), // hour
        "m+": this.getMinutes(), // minute
        "s+": this.getSeconds(), // second
        "q+": Math.floor((this.getMonth() + 3) / 3), // quarter
        "S": this.getMilliseconds()
        // millisecond
    }

    if(/(y+)/.test(format)) {
        format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 -
            RegExp.$1.length));
    }

    for(var k in o) {
        if(new RegExp("(" + k + ")").test(format)) {
            format = format.replace(RegExp.$1, RegExp.$1.length == 1 ?
                o[k] :
                ("00" + o[k]).substr(("" + o[k]).length));
        }
    }
    return format;
}

/**
 * 获取两个日期之间的日期，不包括头和尾
 * @param begin
 * @param end
 */
function getAll(begin, end) {
    var ab = begin.split("-");
    var ae = end.split("-");
    var db = new Date();
    db.setUTCFullYear(ab[0], ab[1] - 1, ab[2]);
    var de = new Date();
    de.setUTCFullYear(ae[0], ae[1] - 1, ae[2]);
    var unixDb = db.getTime();
    var unixDe = de.getTime();
    var thisArray = new Array();
    for(var k = unixDb + 24 * 60 * 60 * 1000; k < unixDe;) {
        thisArray.push((new Date(parseInt(k))).format('yyyy-MM-dd'));
        k = k + 24 * 60 * 60 * 1000;
    }
    return thisArray;
}

/**
 * 计算开始时间和结束时间之间间隔minute分钟的多个时间段
 *
 * @param startTime 格式：HH:mm
 * @param endTime 格式：HH:mm
 * @param minute 例如：30
 */
function getTimePointMinute(startTime, endTime, minute){
    var result = new Array();
    var startHour = parseInt(startTime.split(":")[0]);
    var startMinute = parseInt(startTime.split(":")[1]);
    var currentMinute = startHour * 60 + startMinute;

    var endHour = parseInt(endTime.split(":")[0]);
    var endMinute = parseInt(endTime.split(":")[1]);
    var maxMinute = endHour * 60 + endMinute;
    if((currentMinute + minute) <= maxMinute){
        var newStartTime = turnTime(startTime, minute);
        result.push(startTime + " ~ " + newStartTime);
        result = $.merge(result, getTimePointMinute(newStartTime, endTime, minute));
    }
    return result;
}

function turnTime(time, mm){
    var hour = parseInt(time.split(":")[0]);
    var minute = parseInt(time.split(":")[1]);
    if((minute + mm) >= 60){
        minute = minute + mm - 60;
        hour = hour + 1;
    }else{
        minute = minute + mm;
    }
    return (hour < 10 ? ("0" + hour) : hour) + ":" + (minute < 10 ? (minute + "0") : minute);
}