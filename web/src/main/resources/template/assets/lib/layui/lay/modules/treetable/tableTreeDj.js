layui.define(['table', 'jquery'], function (exports) {
    const MOD_NAME = 'tableTreeDj';
    const $ = layui.jquery;
    const table = layui.table;

    class Tree {
        constructor() {
            this.config = {
                keyId: "id" // 当前ID
                , keyPid: "pid" // 上级ID
                , title: "name" // 标题名称字段,此字段td用于绑定单击折叠展开功能
                , indent: ' &nbsp; &nbsp;' // 缩进.可以是其他字符
                // 图标
                , icon: {
                    open: 'fa fas fa-caret-down', // 展开时候图标
                    close: 'fa fas fa-caret-right', // 折叠时候图标
                }

                // 是否启用展开状态缓存
                // 传true表示启用缓存,占用 localStorage的key = unfoldStatus
                // 传具体字符串表示启用, 字符串会设置成key
                , showCache: false
                , sort: 'asc' // 排序方式.['asc', 'desc'].必须小写
                // 点击展开时候的回调函数
                , showByPidCallback: {}

                // 点击折叠时候的回调函数
                , hideByPidCallback: {}
                , defaultShow: false // 全部展开,如果开启缓存,只有缓存里面没有展开状态时候此配置才生效.否则按缓存里面的展开状态来初始化数据
            };
            // 运行数据模板
            this.runTemplate = {
                hasChild: [] // 是否有子级[id=>true]
                , parentChild: {} // 父子级关系 [pid=> [id, id]]
                , level: [] // 层级 [{id:level}]
                , childParent: {} // 子级与父级对应关系 [id=>pid]
                , dataIndex: {} // 表格 data-index 与 数据id 的对应关系
                , unfoldStatus: {} // id=>true ,true展开, false折叠.
                , idArr: [] // id数组
                , pidArr: [] // pid数组
            }
            // 实际运行时候的数据
            this.run = {}

            // table参数,作为中转变量,以期二次渲染的时候不用再次传入
            this.objTable = {};

            // 原始data
            this.dataOri = [];
        }

        // 渲染
        render(obj, config) {
            // 此操作是为了在多次调用render方法的时候,可以忽略obj参数
            if (!!obj) {
                this.objTable = obj;
            } else {
                obj = this.objTable;
            }
            // if(obj.url == null) {
            //     console.error("url不可为空");
            //     return;
            // }

            if (!!config) {
                this.config = $.extend(this.config, config);
            }

            // 整理数据初始状态
            var loadData = false;
            const parseData = obj.parseData || {};
            obj.parseData = (res) => {
                if (JSON.stringify(parseData) !== "{}") {
                    res = parseData(res)
                }
                this._initDo()
                res.rows = this._parse(res.rows);
                loadData = true;
                return res;
            }

            // 数据渲染之后,执行的操作
            const done = obj.done || {};
            obj.done = (res, curr, count) => {
                this._done(obj, res, curr, count);
                if (JSON.stringify(done) !== "{}") {
                    done(res, curr, count);
                }
            }
            table.render(obj);
            if (!loadData) {
                this._initDo()
            }
        }

        // 重载
        reload(tableId, obj) {
            this._initDo();
            tableId = tableId || this.objTable.id;
            table.reload(tableId, obj);
        }

        // 获取table对象
        getTable() {
            return table;
        }


        // ================ 以下方法外部也可以调用 ========================

        // 隐藏全部子级
        hideAll(obj) {
            const dataIndex = this.getDataIndex();
            const layId = obj.id;

            for (let id in dataIndex) {
                if (this.run.hasChild.indexOf(id) !== -1) {
                    this.hideByPid(id, layId);
                }
            }
        }

        // 显示全部子级
        showAll(obj) {
            const dataIndex = this.getDataIndex();
            const layId = obj.id;

            for (let id in dataIndex) {
                if (this.run.hasChild.indexOf(id) !== -1) {
                    this.showByPid(id, layId);
                }
            }
        }

        getElemTrByIndex(layId, index) {
            return $("[lay-id='" + layId + "'] table tr[data-index='" + index + "']");
        }

        getElemTdByIndex(layId) {
            const title = this.getTitle();
            return $('[lay-id="' + layId + '"] [data-field="' + title + '"]')
        }

        getElemIconByIndex(layId, index) {
            const title = this.getTitle();
            return $("[lay-id='" + layId + "'] table tr[data-index='" + index + "'] td[data-field=" + title + "] div span");
        }

        // 根据父元素隐藏子元素
        hideByPid(id, layId) {
            const idArr = this.getParentChild(id);
            if (!idArr) {
                return false;
            }

            const dataIndex = this.getDataIndex();

            // 执行隐藏操作
            Object.keys(idArr).forEach((idChildKey) => {
                const idChild = idArr[idChildKey];
                const index = dataIndex[idChild];
                this.hideByDataIndex(layId, index);

                // 递归的执行下级
                this.hideByPid(idChild, layId);
            });

            // 图标改为 close
            const index = dataIndex[id];
            const iconClose = this.getIconClose();
            const elemIcon = this.getElemIconByIndex(layId, index);
            elemIcon.removeClass().addClass(iconClose);

            // 当前ID 折叠状态设为 false
            this.setUnfoldStatus(id, false);

            this._hideByPidCallback(idArr);
        }

        showByPid(id, layId) {
            const idArr = this.getParentChild(id);
            if (!idArr) {
                return false;
            }

            const dataIndex = this.getDataIndex();

            // 当前折叠状态, 执行展开操作
            Object.keys(idArr).forEach((idChildKey) => {
                const idChild = idArr[idChildKey];
                const index = dataIndex[idChild];
                this.showByDataIndex(layId, index);
            });

            // 更换图标
            // 图标改为 open
            const index = dataIndex[id];
            const iconOpen = this.getIconOpen();
            const elemIcon = this.getElemIconByIndex(layId, index);
            elemIcon.removeClass().addClass(iconOpen);

            // 当前ID 折叠状态设为 true
            this.setUnfoldStatus(id, true);

            // 回调函数
            this._showByPidCallback(idArr);
        }

        // 根据 data-index 隐藏一行
        hideByDataIndex(layId, index) {
            const elem = this.getElemTrByIndex(layId, index);
            elem.addClass('layui-hide');
        }

        // 根据 data-index 显示一行
        showByDataIndex(layId, index) {
            const elem = this.getElemTrByIndex(layId, index);
            elem.removeClass('layui-hide');
        }

        // ================== 获取器 ====================
        // 获取主键 key

        getDataOri() {
            return this.dataOri;
        }

        getRun() {
            return this.run;
        }

        getKeyId() {
            return this.config.keyId;
        }

        // 获取上级 key
        getKeyPid() {
            return this.config.keyPid;
        }

        // 获取要缩进的字段
        getTitle() {
            return this.config.title;
        }

        // 获取缩进字符
        getIndent() {
            return this.config.indent;
        }

        // 获取图标开启
        getIconOpen() {
            return this.config.icon.open;
        }

        // 获取图标关闭
        getIconClose() {
            return this.config.icon.close;
        }

        // 获取层级
        getLevel(id) {
            if (!!id) {
                const level = this.run.level;
                for (let i = 0; i < level.length; i++) {
                    if (level[i][id] === id) {
                        return level[i]['level']
                    }
                }
            }
            return this.run.level;
        }

        // 获取父子级关系
        getParentChild(id) {
            if (!!id) {
                return this.run.parentChild[id];
            }
            return this.run.parentChild;
        }

        // 获取 dataIndex
        getDataIndex() {
            return this.run.dataIndex;
        }

        // 获取是否有子级数组
        getHasChild() {
            return this.run.hasChild;
        }

        // 获取展开的id
        getUnfoldStatus(id) {
            if (!!id) {
                return this.run.unfoldStatus[id] || false;
            }
            return this.run.unfoldStatus;
        }

        // 设置展开id数据的值
        setUnfoldStatus(id, flag) {
            flag = flag || false;
            this.run.unfoldStatus[id] = flag;

            const cache = this.getShowCache();
            if (cache) {
                this.cache(cache, this.run.unfoldStatus);
            }
        }

        // 获取是否启用展示缓存,返回值是 缓存的 key
        getShowCache() {
            let cache = this.config.showCache;
            if (cache === true) {
                return "unfoldStatus";
            }
            return cache;
        }

        // 获取排序方式
        getSort() {
            return this.config.sort || 'asc';
        }

        // 缓存操作
        cache(key, val) {
            if (val) {
                val = JSON.stringify(val);
                localStorage.setItem(key, val);
            }
            return JSON.parse(localStorage.getItem(key));
        }

        // ================= 私有方法 ===================

        _initDo() {
            // 初始化运行时配置参数
            this.run = JSON.parse(JSON.stringify(this.runTemplate));

            // 获取缓存
            const cache = this.getShowCache();
            if (cache) {
                this.run.unfoldStatus = this.cache(cache) || {};
            }
        }

        // 整理渲染时候的数据.this.run
        _parseInit(data) {
            const keyId = this.getKeyId();
            const keyPid = this.getKeyPid();

            // 将ID改为字符串,因为既当key又当value.会把整型改为字符串类型,所以这里统一改成字符串型
            for (let i = 0; i < data.length; i++) {
                data[i][keyId] = data[i][keyId] + "";
                data[i][keyPid] = data[i][keyPid] + "";
            }

            // 一. 获取id数组
            data.forEach((obj) => {
                const id = obj[keyId];
                this.run.idArr.push(id);
            });

            // 二. pid数组
            data.forEach((obj) => {
                const pid = obj[keyPid];
                if (this.run.idArr.indexOf(pid) !== -1) {
                    // 判断本条pid对应的数据是否存在
                    if (this.run.pidArr.indexOf(pid) === -1) {
                        // 如果没有才添加
                        this.run.pidArr.push(pid);
                    }
                }
            });

            // 三. 子级与父级 / 父级与子级 对应关系
            data.forEach((obj) => {
                const id = obj[keyId];
                const pid = obj[keyPid];

                if (this.run.idArr.indexOf(id) !== -1 && this.run.pidArr.indexOf(pid) !== -1) {
                    // 整理子级与父级对应关系
                    this.run.childParent[id] = pid;

                    // 整理父级与子级对应关系
                    let parentChild = this.run.parentChild[pid] || [];
                    parentChild.push(id);
                    this.run.parentChild[pid] = parentChild;
                }
            });

            // 四. hasChild, level
            data.forEach((obj) => {
                const id = obj[keyId];
                const pid = obj[keyPid];

                // 1. 整理 hasChild
                if (this.run.idArr.indexOf(pid) !== -1) {
                    // 数据真实存在才添加到 hasChild
                    // 1. 整理 hasChild 是否有子级[id1, id2, id3]
                    if (this.run.hasChild.indexOf(pid) === -1) {
                        // 没有才追加
                        this.run.hasChild.push(pid);
                    }
                }

                // 2. 整理 level, 根据当前向上找pid.一直找到不存在.
                let hasParent = true;
                let level = 0;
                let idCheck = id;
                while (hasParent) {
                    // 判断当前父级是否存在.不存在则退出
                    if (!this.run.childParent.hasOwnProperty(idCheck)) {
                        hasParent = false;
                        break;
                    }

                    // 获取父级ID,用于下次检测
                    idCheck = this.run.childParent[idCheck];
                    level++;
                }
                this.run.level.push({"id": id, "level": level});

            });
        }

        // 开始渲染
        _parseDo(data) {
            // 显示图标 -- 给标题增加图标span
            data = this._showIcon(data);

            // 显示缩进
            data = this._showIndent(data);

            // 排序, 使子级紧挨在父级下面
            data = this._disposalSortParent(data);
            return data;
        }

        // 数据整理(总) - 获取数据之后,渲染数据之前.
        _parse(data) {
            if (!data) data = [];

            // 保留原始data
            this._setDataOri(data);

            // 整理渲染时候的数据.this.run
            this._parseInit(data);

            // 开始渲染
            data = this._parseDo(data)
            return data;
        }

        // 整理done之前的准备工作
        _doneInit(data) {
            // 一点扫尾工作,必须等待整理完数据才可以执行
            // 整理 data-index 与 id 的对应关系,点击得到 data-index => $(data-index) => id; id => dataIndex[id] => data-index
            this._disposalDataIndex(data);
        }

        _doneDo(obj, data) {
            const id = obj.id
            // 初始化展开状态, 优先根据缓存确定是否展开,缓存没有则判断是否默认展开,以上不符合则隐藏子级
            this._initShow(data, obj);

            // 给标题绑定点击事件
            this._bindTitleClick(id);
        }

        // 数据渲染之后,执行的操作
        _done(obj, res, curr, count) {
            this._doneInit(res.rows);
            this._doneDo(obj, res.rows);
        }

        // 初始化展开状态, 根据缓存确定是否展开
        _initShow(data, obj) {
            const layId = obj.id
            const that = this;

            // 判断是否有缓存存在
            const hasUnfold = that.getUnfoldStatus()
            if (JSON.stringify(hasUnfold) !== '{}') {
                const keyId = this.getKeyId();
                data.forEach((item) => {
                    const id = item[keyId];

                    // 判断当前折叠还是展开
                    const unfoldId = that.getUnfoldStatus(id);
                    if (unfoldId) {
                        // 下级展开
                        that.showByPid(id, layId);
                    } else {
                        // 下级折叠
                        that.hideByPid(id, layId);
                    }
                });
                return;
            }

            const defaultShow = that._getDefaultShow()
            if (defaultShow) {
                // 如果缓存不存在,则判断是否配置全部展开
                that.showAll(obj)
            } else {
                // 否则全部折叠
                that.hideAll(obj)
            }
        }

        _titleClick(e) {
            const that = e.data.that
            const layId = e.data.layId

            var index = $(e.target).parents('tr').attr("data-index");
            let id;
            $.each(that.getDataIndex(), function (key, value) {
                if (parseInt(index) === value) {
                    id = key;
                }
            });

            // 判断当前折叠还是展开
            const unfoldId = that.getUnfoldStatus(id);
            if (unfoldId) {
                // 下级折叠
                that.hideByPid(id, layId);
            } else {
                // 下级展开
                that.showByPid(id, layId);
            }
        }

        // 给标题绑定点击事件
        _bindTitleClick(layId) {
            const param = {
                layId: layId,
                that: this
            }

            const tdTitle = this.getElemTdByIndex(layId)
            tdTitle.off('click').bind('click', param, this._titleClick)
        }

        // 整理数据 - 整理 layui.table 行中的 data-index 与 数据id 的对应关系[id=>index]
        _disposalDataIndex(data) {
            let dataIndex = {};
            const keyId = this.getKeyId();
            data.forEach((item, index) => {
                const id = item[keyId];
                dataIndex[id] = index;
            })
            this.run.dataIndex = dataIndex;
        }

        // 排序 - 使子级紧挨在父级下面
        _disposalSortParent(data) {
            const resData = [];

            const level = this.getLevel();
            const pc = this.getParentChild();
            const sort = this.getSort();

            // 1. 先排序顶级
            const dataTop = [];
            for (let i = 0; i < level.length; i++) {
                if (level[i]["level"] === 0) {
                    const key = level[i]['id'];
                    if (sort === 'asc') {
                        dataTop.push(key);
                    } else {
                        dataTop.splice(0, 0, key);
                    }
                }
            }

            let dataTopHas = dataTop.length > 0;
            while (dataTopHas) {
                const id = dataTop[0];

                const i = this._getdataOriIndexById(id, data);

                // 先将值取出来存入 resData 中
                resData.push(data[i]);

                // 再从 data 中删除这个元素. 注意要用完再删
                data.splice(i, 1);
                dataTop.splice(0, 1);

                let child = pc[id];
                if (child) {
                    if (sort !== 'asc') {
                        child = child.reverse();
                    }

                    dataTop.splice(0, 0, ...child);
                }

                dataTopHas = dataTop.length > 0;
            }

            return resData;
        }

        // 获取某id=x所在某个数组的位置
        _getdataOriIndexById(id, data) {
            const keyId = this.getKeyId();
            for (let i = 0; i < data.length; i++) {
                const line = data[i];
                if (line[keyId] === id) {
                    return i;
                }
            }
            return false;
        }

        // 显示缩进 - 标题前面增加缩进字符串
        _showIndent(data) {
            let tmp = [];

            const indent = this.getIndent();
            const level = this.getLevel();
            const keyId = this.getKeyId();

            data.forEach((item) => {
                const id = item[keyId];
                const title = this.getTitle();
                let indentItem = '';
                let levelValue = 0;
                for (let i = 0; i < level.length; i++) {
                    if (level[i]['id'] === id) {
                        levelValue = level[i]['level'];
                    }
                }

                for (let i = 0; i < levelValue; i++) {
                    indentItem = indentItem + indent
                }
                item[title] = indentItem + item[title];
                tmp.push(item);
            })
            return tmp;
        }

        // 显示图标 - 标题增加span标签
        _showIcon(data) {
            let tmp = [];

            const keyId = this.getKeyId();
            const hasChild = this.getHasChild();

            data.forEach((item) => {
                const id = item[keyId];
                const title = this.getTitle();
                const iconClose = this.getIconClose();
                if (hasChild.indexOf(id) !== -1) {
                    item[title] = '<span class="' + iconClose + '" style="font-size: 18px; width: 18px; height: 18px; text-align: center;"></span>' + item[title];
                }
                tmp.push(item);
            });
            return tmp;
        }

        // 展开回调函数
        _showByPidCallback(idArr) {
            let callback = this.config.showByPidCallback
            if (JSON.stringify(callback) !== "{}") {
                callback(idArr);
            }
        }

        // 折叠回调函数
        _hideByPidCallback(idArr) {
            let callback = this.config.hideByPidCallback
            if (JSON.stringify(callback) !== "{}") {
                callback(idArr);
            }
        }

        // 获取是否默认展开
        _getDefaultShow() {
            return this.config.defaultShow
        }

        _setDefaultShow(defaultShow) {
            this.config.defaultShow = defaultShow
        }

        _setDataOri(dataOri) {
            this.dataOri = JSON.parse(JSON.stringify(dataOri));
        }

        clone() {
            return new Tree();
        }
    }

    const obj = new Tree();
    exports(MOD_NAME, obj)
});