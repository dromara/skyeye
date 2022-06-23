

// 集合工具类函数
var arrayUtil = {

    /**
     * 移除集合中指定name的元素
     *
     * @param list 集合
     * @param name 指定name
     */
    removeArrayPointName: function (list, name) {
        var inArray = -1;
        $.each(list, function(i, item) {
            if(name === item.name) {
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