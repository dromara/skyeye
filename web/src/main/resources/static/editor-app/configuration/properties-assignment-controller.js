/*
 * Activiti Modeler component part of the Activiti project
 * Copyright 2005-2014 Alfresco Software, Ltd. All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

/*
 * Assignment
 */
var propertiesMultipes = '0';
var propertiesIds = '0';

var KisBpmAssignmentCtrl = ['$scope', '$modal', function ($scope, $modal) {

    // Config for the modal window
    var opts = {
        template: 'editor-app/configuration/properties/assignment-popup.html?version=' + Date.now(),
        scope: $scope
    };

    // Open the dialog
    $modal(opts);
}];

var KisBpmAssignmentPopupCtrl = ['$scope', function ($scope) {

    // Put json representing assignment on scope
    if ($scope.property.value !== undefined && $scope.property.value !== null
        && $scope.property.value.assignment !== undefined
        && $scope.property.value.assignment !== null) {
        $scope.assignment = $scope.property.value.assignment;
    } else {
        $scope.assignment = {};
    }

    if ($scope.assignment.candidateUsers == undefined || $scope.assignment.candidateUsers.length == 0) {
        $scope.assignment.candidateUsers = [{value: '', name: ''}];
    }

    // Click handler for + button after enum value
    var userValueIndex = 1;
    $scope.addCandidateUserValue = function (index) {
        $scope.assignment.candidateUsers.splice(index + 1, 0, {value: 'value ' + userValueIndex++});
    };

    // Click handler for - button after enum value
    $scope.removeCandidateUserValue = function (index) {
        $scope.assignment.candidateUsers.splice(index, 1);
    };

    if ($scope.assignment.candidateGroups == undefined || $scope.assignment.candidateGroups.length == 0) {
        $scope.assignment.candidateGroups = [{value: '', name: ''}];
    }

    var groupValueIndex = 1;
    $scope.addCandidateGroupValue = function (index) {
        $scope.assignment.candidateGroups.splice(index + 1, 0, {value: 'value ' + groupValueIndex++});
    };

    // Click handler for - button after enum value
    $scope.removeCandidateGroupValue = function (index) {
        $scope.assignment.candidateGroups.splice(index, 1);
    };

    $scope.save = function () {

        $scope.property.value = {};
        handleAssignmentInput($scope);
        $scope.property.value.assignment = $scope.assignment;

        $scope.updatePropertyInModel($scope.property);
        $scope.close();
    };

    // Close button handler
    $scope.close = function () {
        handleAssignmentInput($scope);
        $scope.property.mode = 'read';
        $scope.$hide();
    };

    //-----------add select User/Group button handler By billJiang--------------
    //代理人(审批人)/候选人
    $scope.selectUser = function (multiple) {
        var title = "选择代理人(单选)"
        var ids = ($scope.assignment.assignee ? $scope.assignment.assignee : 0);
        if (multiple == 1) {
            title = "选择候选人(多选)";
            //候选人id
            ids = 0;
            if ($scope.assignment.candidateUsers) {
                var idsArr = [];
                //alert( $scope.assignment.candidateUsers.length);
                for (var i = 0; i < $scope.assignment.candidateUsers.length; i++) {
                    if ($scope.assignment.candidateUsers[i].value)
                        idsArr.push($scope.assignment.candidateUsers[i].value);
                }
                if (idsArr.length > 0) {
                    ids = idsArr.join(",");
                }
            }
        }
        propertiesMultipes = multiple;
        propertiesIds = ids;
        modals.openWin({
            winId: 'userSelectWin',
            url: basePath + '/tpl/actFlow/chooseAgentPeople.html',
            width: '800px',
            title: title,
        })
    };

    //候选组
    $scope.selectGroup = function () {
        var ids = 0;
        if ($scope.assignment.candidateGroups) {
            var idsArr = [];
            for (var i = 0; i < $scope.assignment.candidateGroups.length; i++) {
                if ($scope.assignment.candidateGroups[i].value)
                    idsArr.push($scope.assignment.candidateGroups[i].value);
            }
            if (idsArr.length > 0) {
                ids = idsArr.join(",");
            }
        }
        propertiesIds = ids;
        modals.openWin({
            winId: 'groupSelectWin',
            url: basePath + '/tpl/actFlow/chooseAgentGroup.html?ids=' + ids,
            width: '1200px',
            title: '选择候选组(多选)'
        })
    }

    //回填受理人
    $scope.setAssignee = function (assignee, userName) {
        $scope.assignment.assignee = assignee;
        $scope.assignment.assigneeName = userName;
        //jQuery("#assigneeNameField").val(userName);
        $scope.$apply();
    };

    //回填候选人
    $scope.setCandidateUsers = function (userIds, userNames) {
        var users = null;
        if (!userIds) {
            $scope.assignment.candidateUsers = users;
        } else {
            var userIdArr = userIds.split(",");
            var userNameArr = userNames.split(",");
            users = [];
            for (var i = 0; i < userIdArr.length; i++) {
                var userObj = {};
                userObj["value"] = userIdArr[i];
                userObj["name"] = userNameArr[i];
                users.push(userObj);
            }
            $scope.assignment.candidateUsers = users;

        }
        $scope.$apply();
    };

    //回填候选组
    $scope.setCandidateGroups = function (groupIds, groupNames) {
        var groups = null;
        if (!groupIds) {
            $scope.assignment.candidateGroups = groups;
        } else {
            var groupIdArr = groupIds.split(",");
            var groupNameArr = groupNames.split(",");
            groups = [];
            for (var i = 0; i < groupIdArr.length; i++) {
                var groupObj = {};
                groupObj["value"] = groupIdArr[i];
                groupObj["name"] = groupNameArr[i];
                groups.push(groupObj);
            }
            $scope.assignment.candidateGroups = groups;
        }
        //加上这句话回填后界面立即生效
        $scope.$apply();
    };

    var handleAssignmentInput = function ($scope) {
        if ($scope.assignment.candidateUsers) {
            var emptyUsers = true;
            var toRemoveIndexes = [];
            for (var i = 0; i < $scope.assignment.candidateUsers.length; i++) {
                if ($scope.assignment.candidateUsers[i].value != '') {
                    emptyUsers = false;
                } else {
                    toRemoveIndexes[toRemoveIndexes.length] = i;
                }
            }

            for (var i = 0; i < toRemoveIndexes.length; i++) {
                $scope.assignment.candidateUsers.splice(toRemoveIndexes[i], 1);
            }

            if (emptyUsers) {
                $scope.assignment.candidateUsers = undefined;
            }
        }

        if ($scope.assignment.candidateGroups) {
            var emptyGroups = true;
            var toRemoveIndexes = [];
            for (var i = 0; i < $scope.assignment.candidateGroups.length; i++) {
                if ($scope.assignment.candidateGroups[i].value != '') {
                    emptyGroups = false;
                } else {
                    toRemoveIndexes[toRemoveIndexes.length] = i;
                }
            }

            for (var i = 0; i < toRemoveIndexes.length; i++) {
                $scope.assignment.candidateGroups.splice(toRemoveIndexes[i], 1);
            }

            if (emptyGroups) {
                $scope.assignment.candidateGroups = undefined;
            }
        }
    };
}];