// 附件插件
var skyeyeEnclosure = {
    enclosureListKey: 'skyeyeJsonKey',
    enclosureBtnTemplate: '<button type="button" class="layui-btn layui-btn-primary layui-btn-xs" id="{{btnId}}">附件上传</button>',
    /**
     * 初始化附件插件，多个使用逗号隔开，只支持id
     *
     * @param ids 需要初始化的附件盒子的id
     * @param callback 回调函数
     */
    init: function (ids, callback) {
        var idsArray = ids.split(',');
        $.each(idsArray, function (i, id) {
            // 按钮id
            var btnId = id + "Btn";
            // 初始化数据为[]
            $("#" + id).attr(skyeyeEnclosure.enclosureListKey, JSON.stringify([]));
            // 加载dom对象
            skyeyeEnclosure.loadEnclosureHTML(id, btnId, 1);
            // 添加[附件上传]按钮的监听事件
            skyeyeEnclosure.initClick(id, btnId, callback);
        });
    },

    /**
     * 初始化附件插件，多个使用逗号隔开，只支持id
     *
     * @param param {需要初始化的附件盒子的id: 默认数据}
     * @param callback 回调函数
     */
    initTypeISData: function (param, callback) {
        $.each(param, function (boxId, data) {
            // 按钮id
            var btnId = boxId + "Btn";
            // 初始化数据
            $("#" + boxId).attr(skyeyeEnclosure.enclosureListKey, JSON.stringify(isNull(data) ? [] : data));
            // 加载dom对象
            skyeyeEnclosure.loadEnclosureHTML(boxId, btnId, 1);
            // 添加[附件上传]按钮的监听事件
            skyeyeEnclosure.initClick(boxId, btnId, callback);
        });
    },

    /**
     * 初始化点击事件
     *
     * @param id 盒子id
     * @param btnId 按钮id
     * @param callback 回调函数
     */
    initClick: function (id, btnId, callback){
        $("body").on("click", "#" + btnId, function() {
            _openNewWindows({
                url: "../../tpl/common/enclosureupload.html?boxId=" + id,
                title: "上传附件",
                pageId: "enclosureuploadpage",
                area: ['420px', '420px'],
                callBack: function (refreshCode) {
                    // 重新加载dom对象
                    skyeyeEnclosure.loadEnclosureHTML(id, btnId, 1);
                    if(typeof(callback) == "function") {
                        callback(skyeyeEnclosure.getJSONEnclosureListByBoxId(id));
                    }
                }});
        });
    },

    /**
     * 附件详情展示
     *
     * @param param
     */
    showDetails: function (param) {
        $.each(param, function (boxId, data) {
            // 按钮id
            var btnId = boxId + "Btn";
            // 初始化数据
            $("#" + boxId).attr(skyeyeEnclosure.enclosureListKey, JSON.stringify(isNull(data) ? [] : data));
            // 加载dom对象
            skyeyeEnclosure.loadEnclosureHTML(boxId, btnId, 2);
        });
    },

    /**
     * 加载附件列表
     *
     * @param boxId 盒子id
     * @param btnId 按钮id
     * @param type 1.允许出现[附件上传]按钮 2.不允许出现[附件上传]按钮
     */
    loadEnclosureHTML: function (boxId, btnId, type){
        var enclosureList = skyeyeEnclosure.getJSONEnclosureListByBoxId(boxId);
        var str = "";
        $.each(enclosureList, function(i, item) {
            if(type == 1){
                str += '<br><a rowid="' + item.id + '" class="enclosureItem" rowpath="' + item.fileAddress + '" href="javascript:;" style="color:blue;">' + item.name + '</a>';
            } else {
                str += '<a rowid="' + item.id + '" class="enclosureItem" rowpath="' + item.fileAddress + '" href="javascript:;" style="color:blue;">' + item.name + '</a><br>';
            }
        });
        var btnHtml = "";
        if(type == 1){
            btnHtml += getDataUseHandlebars(skyeyeEnclosure.enclosureBtnTemplate, {btnId: btnId});
        }
        $("#" + boxId).html(btnHtml + str);

        // 加载点击下载事件
        skyeyeEnclosure.initDownloadEvent();
    },

    /**
     * 加载点击下载事件
     */
    initDownloadEvent: function () {

        $("body").on("click", ".enclosureItem", function() {
            download(fileBasePath + $(this).attr("rowpath"), $(this).html());
        });

    },

    /**
     * 获取指定id的附件
     *
     * @param id 盒子id
     */
    getJSONEnclosureListByBoxId: function (id) {
        return [].concat(JSON.parse($("#" + id).attr(skyeyeEnclosure.enclosureListKey)));
    },

    /**
     * 获取指定id的附件id，逗号隔开
     *
     * @param id 盒子id
     */
    getEnclosureIdsByBoxId: function (id){
        var enclosureList = skyeyeEnclosure.getJSONEnclosureListByBoxId(id);
        var enclosureInfo = "";
        $.each(enclosureList, function (i, item) {
            enclosureInfo += item.id + ',';
        })
        return enclosureInfo;
    }
};