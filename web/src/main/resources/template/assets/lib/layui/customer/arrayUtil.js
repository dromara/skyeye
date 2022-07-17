

// 集合工具类函数
var arrayUtil = {

    /**
     * 移除集合中指定name的元素
     *
     * @param list 集合
     * @param name 指定name
     */
    removeArrayPointName: function (list, name) {
        return arrayUtil.removeArrayPointKey(list, 'name', name);
    },

    /**
     * 移除集合中指定key的元素
     *
     * @param list 集合
     * @param key 指定key
     * @param value 指定的值
     */
    removeArrayPointKey: function (list, key, value) {
        var inArray = -1;
        $.each(list, function(i, item) {
            if(value === item[key]) {
                inArray = i;
                return false;
            }
        });
        if(inArray != -1) {
            list.splice(inArray, 1);
        }
        return list;
    }

}