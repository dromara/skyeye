
// 组织机构工具类
var organizationUtil = {

    // 企业树
    companyTree: null,
    // 部门树
    departmentTree: null,
    // 岗位树
    jobTree: null,
    // 岗位定级树
    jobScoreTree: null,

    // 初始化新增页面的组织机构
    initAddOrganization: function (dtree) {
        // 初始化公司
        organizationUtil.companyTree = dtree.render({
            elem: "#demoTree1",
            url: reqBasePath + 'queryCompanyMationListTree',
            dataStyle: 'layuiStyle',
            done: function(json) {
                if($("#demoTree1 li").length > 0){
                    $("#demoTree1 li").eq(0).children('div').click();
                }
            }
        });

        dtree.on("node('demoTree1')" ,function(param) {
            var choose = dtree.getNowParam(organizationUtil.companyTree);
            // 初始化部门
            organizationUtil.departmentTree = dtree.render({
                elem: "#demoTree2",
                url: reqBasePath + 'companydepartment006?companyId=' + choose.nodeId,
                dataStyle: 'layuiStyle',
                done: function(json) {
                    if($("#demoTree2 li").length > 0){
                        $("#demoTree2 li").eq(0).children('div').click();
                    }
                }
            });
        });

        dtree.on("node('demoTree2')" ,function(param){
            var choose = dtree.getNowParam(organizationUtil.departmentTree);
            // 初始化职位
            organizationUtil.jobTree = dtree.render({
                elem: "#demoTree3",
                url: reqBasePath + 'companyjob006?departmentId=' + choose.nodeId,
                dataStyle: 'layuiStyle',
                done: function(json) {
                    if($("#demoTree3 li").length > 0){
                        $("#demoTree3 li").eq(0).children('div').click();
                    }
                }
            });
        });

        dtree.on("node('demoTree3')" ,function(param){
            var choose = dtree.getNowParam(organizationUtil.jobTree);
            // 初始化职位定级
            organizationUtil.jobScoreTree = dtree.render({
                elem: "#demoTree4",
                url: reqBasePath + 'companyjobscore008?jobId=' + choose.nodeId,
                dataStyle: 'layuiStyle',
                method: 'GET',
                done: function(json) {
                    if($("#demoTree4 li").length > 0){
                        $("#demoTree4 li").eq(0).children('div').click();
                    }
                }
            });
        });
    },

    // 初始化编辑页面的组织机构
    initEditOrganization: function (dtree, mation) {
        // 初始化公司
        organizationUtil.companyTree = dtree.render({
            elem: "#demoTree1",
            url: reqBasePath + 'queryCompanyMationListTree',
            dataStyle: 'layuiStyle',
            done: function(json) {
                if($("#demoTree1 li").length > 0){
                    for(var i = 0; i < $("#demoTree1 li").length; i++){
                        if($("#demoTree1 li").eq(i).attr("data-id") == mation.companyId){
                            $("#demoTree1 li").eq(i).children('div').click();
                            return;
                        }
                    }
                }
            }
        });

        dtree.on("node('demoTree1')" ,function(param) {
            var choose = dtree.getNowParam(organizationUtil.companyTree);
            // 初始化部门
            organizationUtil.departmentTree = dtree.render({
                elem: "#demoTree2",
                url: reqBasePath + 'companydepartment006?companyId=' + choose.nodeId,
                dataStyle: 'layuiStyle',
                done: function(json) {
                    if($("#demoTree2 li").length > 0){
                        for(var i = 0; i < $("#demoTree2 li").length; i++){
                            if($("#demoTree2 li").eq(i).attr("data-id") == mation.departmentId){
                                $("#demoTree2 li").eq(i).children('div').click();
                                return;
                            }
                        }
                    }
                }
            });
        });

        dtree.on("node('demoTree2')" ,function(param){
            var choose = dtree.getNowParam(organizationUtil.departmentTree);
            // 初始化职位
            organizationUtil.jobTree = dtree.render({
                elem: "#demoTree3",
                url: reqBasePath + 'companyjob006?departmentId=' + choose.nodeId,
                dataStyle: 'layuiStyle',
                done: function(json) {
                    if($("#demoTree3 li").length > 0){
                        for(var i = 0; i < $("#demoTree3 li").length; i++){
                            if($("#demoTree3 li").eq(i).attr("data-id") == mation.jobId){
                                $("#demoTree3 li").eq(i).children('div').click();
                                return;
                            }
                        }
                    }
                }
            });
        });

        dtree.on("node('demoTree3')" ,function(param){
            var choose = dtree.getNowParam(organizationUtil.jobTree);
            // 初始化职位定级
            organizationUtil.jobScoreTree = dtree.render({
                elem: "#demoTree4",
                url: reqBasePath + 'companyjobscore008?jobId=' + choose.nodeId,
                dataStyle: 'layuiStyle',
                method: 'GET',
                done: function(json) {
                    if($("#demoTree4 li").length > 0){
                        for(var i = 0; i < $("#demoTree4 li").length; i++){
                            if($("#demoTree4 li").eq(i).attr("data-id") == mation.jobScoreId){
                                $("#demoTree4 li").eq(i).children('div').click();
                                return;
                            }
                        }
                    }
                }
            });
        });
    },

    /**
     * 获取企业信息
     */
    getCompanyMation: function (dtree) {
        return organizationUtil.judgeNullRetuenObject(dtree.getNowParam(organizationUtil.companyTree));
    },

    /**
     * 获取部门信息
     */
    getDepartmentMation: function (dtree) {
        return organizationUtil.judgeNullRetuenObject(dtree.getNowParam(organizationUtil.departmentTree));
    },

    /**
     * 获取岗位信息
     */
    getJobMation: function (dtree) {
        return organizationUtil.judgeNullRetuenObject(dtree.getNowParam(organizationUtil.jobTree));
    },

    /**
     * 获取岗位定级信息
     */
    getJobScoreMation: function (dtree) {
        return organizationUtil.judgeNullRetuenObject(dtree.getNowParam(organizationUtil.jobScoreTree));
    },

    judgeNullRetuenObject: function (object) {
        return isNull(object) ? {} : object;
    }

}
