//app.js
App({
  onLaunch: function () {
    // 展示本地存储能力
    var logs = wx.getStorageSync('logs') || []
    logs.unshift(Date.now())
    wx.setStorageSync('logs', logs)

    // 登录
    wx.login({
      success: function (res) {
        // 发送 res.code 到后台换取 openId, sessionKey, unionId
      }
    })
    // 获取用户信息
    wx.getSetting({
      success: function (res) {
        if (res.authSetting['scope.userInfo']) {
          // 已经授权，可以直接调用 getUserInfo 获取头像昵称，不会弹框
          wx.getUserInfo({
            success: function (res) {
              // 可以将 res 发送给后台解码出 unionId
              this.globalData.userInfo = res.userInfo

              // 由于 getUserInfo 是网络请求，可能会在 Page.onLoad 之后才返回
              // 所以此处加入 callback 以防止这种情况
              if (this.userInfoReadyCallback) {
                this.userInfoReadyCallback(res)
              }
            }
          })
        }
      }
    })
  },
  globalData: {
    userInfo: null
  },
  
  stringToJson: function (data) {
    return JSON.parse(data);
  },

  isNull: function(data){
    if (data == null || data == "" || data == "undefined"){
      return true;
    }else{
      return false;
    }
  },
  
  data: {
    servsers: "http://218.28.192.34:10001/",
    imgServerIp: "http://47.94.148.186:30110/",
    APPMAPKEY:"HUPBZ-SNCWQ-A6M5W-GEIJM-SF4PQ-M4BOX",
    // APP_ID: "wx4dcee834cc6446dd",
    // APP_SECRET: "7165088c1f7312c9fda45670d63bd878",
    APP_ID: "wx5bef16e15f8499e9",
    APP_SECRET: "c9fbc0d1c91c103594fdba46601307e2",
  }
})