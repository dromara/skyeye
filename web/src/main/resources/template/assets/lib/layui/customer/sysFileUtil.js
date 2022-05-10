
// 系统文件相关处理工具
var sysFileUtil = {

    /**
     * 获取文件路径后缀
     *
     * @param url 文件路径
     */
    getFileExt: function (url) {
        return (/[.]/.exec(url)) ? /[^.]+$/.exec(url.toLowerCase()) : '';
    },

    /**
     * 下载
     * @param  {String} url 目标文件地址
     * @param  {String} filename 想要保存的文件名称
     */
    download: function(url, filename) {
        sysFileUtil.getBlob(url, function(blob) {
            sysFileUtil.saveAs(blob, filename);
        });
    },

    /**
     * 下载图片
     *
     * @param path
     * @param imgName
     */
    downloadImage: function(path, imgName) {
        var _OBJECT_URL;
        var request = new XMLHttpRequest();
        request.addEventListener('readystatechange', function (e) {
            if (request.readyState == 4) {
                _OBJECT_URL = URL.createObjectURL(request.response);
                var $a = $("<a></a>").attr("href", _OBJECT_URL).attr("download", imgName);
                $a[0].click();
            }
        });
        request.responseType = 'blob';
        request.open('get', path);
        request.send();
    },

    /**
     * 获取 blob
     * @param  {String} url 目标文件地址
     * @return {cb}
     */
    getBlob: function (url,cb) {
        var xhr = new XMLHttpRequest();
        xhr.open('GET', url, true);
        xhr.responseType = 'blob';
        xhr.onload = function() {
            if (xhr.status === 200) {
                cb(xhr.response);
            }
        };
        xhr.send();
    },

    /**
     * 根据文件路径转换成File对象
     *
     * @param url 文件路径
     * @param callback 回调函数
     */
    getFileByUrl: function (url, callback) {
        sysFileUtil.getBlob(url, function(blob) {
            const files = new File(
                [blob],
                sysFileUtil.getFileNameByUrl(url)
            );
            callback(files);
        });
    },

    /**
     * 根据文件路径获取文件名
     *
     * @param url 文件路径
     * @returns {*}
     */
    getFileNameByUrl: function (url) {
        // 通过\分隔字符串，成字符串数组
        var arr = url.split('\\');
        // 取最后一个，就是文件全名,含后缀
        var fileName = arr[arr.length-1];
        return fileName;
    },

    /**
     * 保存
     * @param  {Blob} blob
     * @param  {String} filename 想要保存的文件名称
     */
    saveAs: function(blob, filename) {
        if (window.navigator.msSaveOrOpenBlob) {
            navigator.msSaveBlob(blob, filename);
        } else {
            var link = document.createElement('a');
            var body = document.querySelector('body');
            link.href = window.URL.createObjectURL(blob);
            link.download = filename;
            // fix Firefox
            link.style.display = 'none';
            body.appendChild(link);
            link.click();
            body.removeChild(link);
            window.URL.revokeObjectURL(link.href);
        };
    }

}